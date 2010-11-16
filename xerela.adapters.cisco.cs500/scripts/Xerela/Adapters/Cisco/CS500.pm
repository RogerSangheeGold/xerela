package Xerela::Adapters::Cisco::CS500;

use strict;
use Xerela::CLIProtocolFactory;
use Xerela::CLIProtocol;
use Xerela::ConnectionPath;
use Xerela::Credentials;
use Xerela::Adapters::Cisco::CS500::AutoLogin;
use Xerela::Adapters::Cisco::CS500::Parsers
  qw( create_config parse_local_accounts parse_chassis parse_snmp parse_system parse_interfaces parse_access_ports );
use Xerela::Adapters::Cisco::CS500::Restore;
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
	my $filehandle = get_model_filehandle( 'cs500', $connection_path->get_ip_address() );

	# initialize the model printer
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, 'cisco', "http://www.xerela.org/model/cisco/1.0 cisco.xsd");
	$printer->open_model();

	# Begin executing commands on the device.  The results of each command will
	# be stored in a single hashtable ($parsers) and fed into each parsing method
	my $responses = {};
	

	# Get rid of the more prompt
	my $termLen = $cli_protocol->send_and_wait_for( "terminal length 0", $prompt_regex );
	if ($termLen =~ /Type help or|Invalid|Command not valid/i)
	{
		# set the --more-- prompt if the term length 0 didn't go through
	        $cli_protocol->set_more_prompt( '\s*-- More --\s*', '20');
    	}
    								
    	$responses->{version} = $cli_protocol->send_and_wait_for( 'show version', $prompt_regex );
    	$responses->{config} = $cli_protocol->send_and_wait_for( 'show configuration' , $prompt_regex );
	$responses->{config} =~ s/$prompt_regex$//;
	$responses->{config} =~ s/^.*?(?=^!)//ms;
    	
	
	#System and chassis information
	parse_system($responses, $printer);
	parse_chassis($responses, $printer);
	create_config($responses,$printer);
	delete $responses->{version};
	
	#Line information
	$responses->{accessPorts} = $cli_protocol->send_and_wait_for('show line', $prompt_regex);
	parse_access_ports( $responses, $printer);
	delete $responses->{accessPorts};
	
	#Interface information
	$responses->{interfaces} = $cli_protocol->send_and_wait_for('show interfaces', $prompt_regex);
	parse_interfaces($responses, $printer);
	#Free memory
	delete $responses->{interfaces};
	
	#Local user account information
	parse_local_accounts($responses, $printer);
	
	#SNMP Community Information
	parse_snmp($responses, $printer);
	delete $responses->{config};

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
	
	my $result = Xerela::Adapters::GenericAdapter::execute_cli_commands('cs500', $cli_protocol, $commands, '(#|\$|>)\s*$');
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
	my $device_prompt_regex = Xerela::Adapters::Cisco::CS500::AutoLogin::execute( $cli_protocol, $connection_path );
	
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
