package Xerela::Adapters::Cisco::ACNS;

use strict;
use warnings;

use Xerela::Adapters::Cisco::ACNS::AutoLogin;
use Xerela::Adapters::Cisco::ACNS::GetRunningConfig qw(get_running_config);
use Xerela::Adapters::Cisco::ACNS::GetStartupConfig qw(get_startup_config);
use Xerela::Adapters::Cisco::ACNS::Parsers qw(parse_system parse_chassis create_config parse_access_ports parse_filters parse_interfaces parse_local_accounts parse_routing parse_snmp parse_stp parse_static_routes parse_vlans parse_vlan_trunking);
use Xerela::Adapters::Cisco::ACNS::Disconnect qw(disconnect);
use Xerela::Adapters::Cisco::ACNS::RestoreStartupConfig;

use Xerela::Adapters::Utils qw(get_model_filehandle close_model_filehandle);
#use Xerela::Adapters::GenericAdapter;
use Xerela::CLIProtocol;
use Xerela::CLIProtocolFactory;
use Xerela::ConnectionPath;
use Xerela::Credentials;
use Xerela::Logger;
use Xerela::Model::XmlPrint;
#use Xerela::SNMP;
#use Xerela::SnmpSessionFactory;
use Xerela::Typer;

# Grab a reference to the Xerela::Logger
my $LOGGER = Xerela::Logger::get_logger();

# Specifies that this adapter is a subclass of Xerela::Adapters::BaseAdapter
use Xerela::Adapters::BaseAdapter;
our @ISA = qw(Xerela::Adapters::BaseAdapter);

sub backup
{
	my $packageName = shift;
	my $backupDoc   = shift;    # how to backup this device
	my $responses    = {};       # will contain device responses to be handed to the Parsers module
	
	# Translate the backup operation XML document into Xerela::ConnectionPath
	my ($connectionPath) = Xerela::Typer::translate_document( $backupDoc, 'connectionPath' );
	
	# Set up the XmlPrint object for printing the XerelaElementDocument (ZED)
	my $filehandle = get_model_filehandle( 'Cisco ACNS Platforms', $connectionPath->get_ip_address() );
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, 'cisco', "http://www.xerela.org/model/cisco/1.0 cisco.xsd" );
	$printer->open_model();
	
	# Make a Telnet or SSH connection
	my ( $cliProtocol, $enable_prompt_regex ) = _connect($connectionPath);
	
	# Get rid of the more prompt
	my $termLen = $cliProtocol->send_and_wait_for( "terminal length 0", $enable_prompt_regex );
	if ( $termLen =~ /Invalid input/i )
	{

		# set the --more-- prompt if the term length 0 didn't go through
		$cliProtocol->set_more_prompt( '--More--\s*$', '20' );
	}
	
	$responses->{running_config} = get_running_config( $cliProtocol, $connectionPath );
	$responses->{startup_config} = get_startup_config( $cliProtocol, $connectionPath );

	while ($responses->{running_config} =~ /^interface\s+(\S+)\s+(\d+\/\d+)/mig)
	{
		my $type = $1;
		my $number = $2;

		$responses->{interfaces}->{"$type$number"} = $cliProtocol->send_and_wait_for( "show interface $type $number", $enable_prompt_regex );
	}
	
	$responses->{disk}           = $cliProtocol->send_and_wait_for( "show disk details", $enable_prompt_regex );
	$responses->{flash}          = $cliProtocol->send_and_wait_for( "show flash", $enable_prompt_regex );
	$responses->{hardware}       = $cliProtocol->send_and_wait_for( "show hardware", $enable_prompt_regex );
	#$responses->{routes}         = $cliProtocol->send_and_wait_for( "show ip routes", $enable_prompt_regex );
	#$responses->{snmp}           = $cliProtocol->send_and_wait_for( "show snmp stats", $enable_prompt_regex );
	#$responses->{version}        = $cliProtocol->send_and_wait_for( "show version", $enable_prompt_regex );
	
	parse_system( $responses, $printer );
	parse_chassis( $responses, $printer );
	create_config( $responses, $printer );
	#parse_access_ports( $responses, $printer );
	#parse_filters( $responses, $printer );
	my $subnets = parse_interfaces( $responses, $printer );
	#print $subnets->{'FastEthernet'}->{ipAddress};
	parse_local_accounts( $responses, $printer );
	#parse_routing( $responses, $printer );
	parse_snmp( $responses, $printer );
	#parse_stp( $responses, $printer );
	parse_static_routes( $responses, $printer, $subnets );
	#parse_vlans( $responses, $printer );
	#parse_vlan_trunking( $responses, $printer );
	
	delete $responses->{running_config};
	delete $responses->{startup_config};
	delete $responses->{interfaces};
	
	delete $responses->{disk};
	delete $responses->{flash};
	delete $responses->{hardware};
	#delete $responses->{routes};
	#delete $responses->{snmp};
	#delete $responses->{version};
	
	_disconnect($cliProtocol);
	$printer->close_model();                # close out the XerelaElementDocument
	close_model_filehandle($filehandle);    # Make sure to close the model file handle
}

sub commands
{
	my $packageName = shift;
	my $commandDoc  = shift;

	my ( $connectionPath, $commands ) = Xerela::Typer::translate_document( $commandDoc, 'connectionPath' );
	my ( $cliProtocol, $devicePromptRegex ) = _connect($connectionPath);

	my $result = Xerela::Adapters::GenericAdapter::execute_cli_commands( 'Cisco ACNS Platforms', $cliProtocol, $commands, $devicePromptRegex . '|(#|\$|>)\s*$' );
	_disconnect($cliProtocol);
	return $result;
}

sub _connect
{

	# Grab our arguments
	my $connectionPath = shift;

	# Create a new CLI protocol object by using the Xerela::CLIProtocolFactory::create sub-routine
	# to examine the Xerela::ConnectionPath argument for any command line interface (CLI) protocols
	# that may be specified.
	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);

	# Make a connection to and successfully authenticate with the device
	my $devicePromptRegex = Xerela::Adapters::Cisco::ACNS::AutoLogin::execute( $cliProtocol, $connectionPath );

	# Store the regular expression that matches the primary prompt of the device under the key "prompt"
	# on the Xerela::CLIProtocol object
	$cliProtocol->set_prompt_by_name( 'enablePrompt', $devicePromptRegex );

	# Return the created Xerela::CLIProtocol object and the device prompt encountered after successfully connecting to a device.
	return ( $cliProtocol, $devicePromptRegex );
}

sub _disconnect
{

	# Grab the Xerela::CLIProtocol object passed in
	my $cliProtocol = shift;

	# Close this session and exit
	$cliProtocol->send("exit");
}

1;
