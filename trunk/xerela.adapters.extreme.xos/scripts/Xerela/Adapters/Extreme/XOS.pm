package Xerela::Adapters::Extreme::XOS;

use strict;
use Xerela::CLIProtocolFactory;
use Xerela::CLIProtocol;
use Xerela::ConnectionPath;
use Xerela::Credentials;
use Xerela::Adapters::Extreme::XOS::AutoLogin;
use Xerela::Adapters::Extreme::XOS::GetConfig qw(get_config);
use Xerela::Adapters::Extreme::XOS::Parsers
  qw(parse_routing create_config parse_local_accounts parse_chassis parse_filters parse_snmp parse_system parse_interfaces parse_static_routes parse_vlans parse_stp);
use Xerela::Adapters::Utils qw(get_model_filehandle close_model_filehandle);
use Xerela::Adapters::GenericAdapter;
use Xerela::Model::XmlPrint;
use Xerela::Logger;

# Grab a reference to the Xerela::Logger
my $LOGGER = Xerela::Logger::get_logger();

# Specifies that this adapter is a subclass of Xerela::Adapters::BaseAdapter
use Xerela::Adapters::BaseAdapter;
our @ISA = qw(Xerela::Adapters::BaseAdapter);

sub backup
{
	my $package_name = shift;
	my $backup_doc   = shift;    # how to backup this device

	# Translate the backup operation XML document into Xerela::ConnectionPath
	my ( $connection_path ) = Xerela::Typer::translate_document( $backup_doc, 'connectionPath' );
	my ( $cli_protocol, $prompt_regex ) = _connect( $connection_path );

	# Grab an output filehandle for the model.  This usually points to STDOUT
	my $filehandle = get_model_filehandle( 'Extreme XOS', $connection_path->get_ip_address() );

	# initialize the model printer
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, 'common' );
	$printer->open_model();

	# Begin executing commands on the device.  The results of each command will
	# be stored in a single hashtable ($parsers) and fed into each parsing method
	my $responses = {};

	$cli_protocol->send_and_wait_for( 'disable clipaging', $prompt_regex );
	$cli_protocol->set_more_prompt( 'Press <SPACE> to continue or <Q> to quit:', '20');
	$responses->{switch}		= $cli_protocol->send_and_wait_for ( 'show switch', $prompt_regex );
	$responses->{version}		= $cli_protocol->send_and_wait_for ( 'show version', $prompt_regex );
	$responses->{configuration} = $cli_protocol->send_and_wait_for ( 'show configuration', $prompt_regex );
	$responses->{memory} 		= $cli_protocol->send_and_wait_for ( 'show memory', $prompt_regex );
	$responses->{power} 		= $cli_protocol->send_and_wait_for ( 'show power', $prompt_regex );
	$responses->{platform} 		= $cli_protocol->send_and_wait_for ( 'show platform version', $prompt_regex ); # this one probably won't work on newer versions
	$responses->{slots} 		= $cli_protocol->send_and_wait_for ( 'show slot detail', $prompt_regex ); # this one probably won't work on older versions

	parse_system( $responses, $printer );
	parse_chassis( $responses, $printer );

	delete $responses->{version};
	delete $responses->{memory};
	delete $responses->{power};
	delete $responses->{platform};

	$responses->{interfaces} = $cli_protocol->send_and_wait_for ( 'show ports configuration no-refresh', $prompt_regex ); # first try with the new command
	if ( $responses->{interfaces} =~ /Invalid input detected/mi ) # if command failed then try old style input
	{
		$responses->{interfaces} = $cli_protocol->send_and_wait_for ( 'show ports configuration', $prompt_regex );
	}

	$responses->{ospf}				= $cli_protocol->send_and_wait_for ( 'show ospf', $prompt_regex );
	$responses->{ospf_area}			= $cli_protocol->send_and_wait_for ( 'show ospf area', $prompt_regex );
	$responses->{ospf_interfaces}	= $cli_protocol->send_and_wait_for ( 'show ospf interfaces', $prompt_regex );
	$responses->{bgp}				= $cli_protocol->send_and_wait_for ( 'show bgp', $prompt_regex );

	$responses->{vlans} = $cli_protocol->send_and_wait_for ( 'show vlan', $prompt_regex );
	while ( $responses->{vlans} =~ /^(\S+)\s+\d+\s+(?:\d+(?:\.\d+)*\s*\/\d+|-+).+$/mig )
	{
		my $vlan = $1;
		$responses->{"vlan_$vlan"} = $cli_protocol->send_and_wait_for ( "show $vlan", $prompt_regex );
	}
	delete $responses->{vlans};

	$responses->{stpd} = $cli_protocol->send_and_wait_for ( 'show stpd', $prompt_regex );
	while ( $responses->{stpd} =~ /^(\S+)\s+\d+\s+[CDERT\-]+\s+.+$/mig )
	{
		my $stp = $1;
		$responses->{"stp_$stp"} = $cli_protocol->send_and_wait_for ( "show $stp", $prompt_regex );
	}
	delete $responses->{stpd};

	$responses->{route} = $cli_protocol->send_and_wait_for ( 'show iproute', $prompt_regex );

	$responses->{config} = get_config( $cli_protocol, $connection_path );
	create_config( $responses, $printer );
	delete $responses->{config};
	delete $responses->{switch};

	parse_interfaces( $responses, $printer );
	delete($responses->{interfaces});

	parse_local_accounts( $responses, $printer );

	parse_routing( $responses, $printer );
	delete $responses->{ospf};
	delete $responses->{ospf_area};
	delete $responses->{ospf_interfaces};
	delete $responses->{bgp};

	parse_snmp( $responses, $printer );

	parse_stp( $responses, $printer );

	parse_static_routes( $responses, $printer );
	delete $responses->{route};

	parse_vlans( $responses, $printer );

	while ( (my $key, my $value) = each(%{$responses}) )
	{
		if ( $key =~ /^(stp|vlan)/)
		{
			delete $responses->{$key};
		}
	}

	delete $responses->{configuration};
	# close out the XerelaElementDocument
	$printer->close_model();
	
	# Make sure to close the model file handle
	close_model_filehandle($filehandle);
	
	# Disconnect from the device
	_disconnect($cli_protocol);
}

sub commands
{
	my $package_name = shift;
	my $command_doc = shift;
	
	my ( $connection_path, $commands ) = Xerela::Typer::translate_document( $command_doc, 'connectionPath' );
	my ( $cli_protocol, $device_prompt_regex ) = _connect( $connection_path );
	
	my $result = Xerela::Adapters::GenericAdapter::execute_cli_commands('Extreme XOS', $cli_protocol, $commands, '(#|\$|>)\s*$');
	_disconnect($cli_protocol);
	return $result;
}

sub _connect
{
	# Grab our arguments
	my $connection_path = shift;

	# Create a new CLI protocol object by using the Xerela::CLIProtocolFactory::create sub-routine
	# to examine the Xerela::ConnectionPath argument for any command line interface (CLI) protocols
	# that may be specified.
	my $cli_protocol = Xerela::CLIProtocolFactory::create($connection_path);

	# Make a connection to and successfully authenticate with the device
	my $device_prompt_regex = Xerela::Adapters::Extreme::XOS::AutoLogin::execute( $cli_protocol, $connection_path );
	
	# Store the regular expression that matches the primary prompt of the device under the key "prompt"
	# on the Xerela::CLIProtocol object
	$cli_protocol->set_prompt_by_name( 'prompt', $device_prompt_regex );
	
	# Return the created Xerela::CLIProtocol object and the device prompt encountered after successfully connecting to a device.
	return ( $cli_protocol, $device_prompt_regex );
}

sub _disconnect
{
	# Grab the Xerela::CLIProtocol object passed in
	my $cli_protocol = shift;

	# Close this session and exit
	$cli_protocol->send("exit");
}

1;
