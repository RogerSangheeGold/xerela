package Xerela::Adapters::Nortel::Accelar;

use strict;

use Xerela::CLIProtocolFactory;
use Xerela::CLIProtocol;
use Xerela::ConnectionPath;
use Xerela::Credentials;
use Xerela::Adapters::Nortel::Accelar::AutoLogin;
use Xerela::Adapters::Nortel::Accelar::GetConfig qw(get_config);
use Xerela::Adapters::Nortel::Accelar::Parsers
  qw( create_config parse_local_accounts parse_chassis parse_snmp parse_system parse_interfaces parse_static_routes parse_vlans parse_stp);
use Xerela::Adapters::Nortel::Accelar::Restore;
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
	my ( $cli_protocol, $regex ) = _connect( $connection_path );

	# Grab an output filehandle for the model.  This usually points to STDOUT
	my $filehandle = get_model_filehandle( 'accelar', $connection_path->get_ip_address() );

	# initialize the model printer
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, 'common' );
	$printer->open_model();

	# Begin executing commands on the device.  The results of each command will
	# be stored in a single hashtable ($parsers) and fed into each parsing method
	my $responses = {};
	
	#---------------------------------------------------------------------------------------
	# This is an example of sending the command 'uptime' and storing it inside the
	# $responses hashtable.  Uncomment the line below to actually execute the command.
	# 
	# 	$responses->{uptime} = $cli_protocol->send_and_wait_for( 'uptime', $prompt_regex );
	#
	# Note: The default timeout for responses is 30 seconds, however you can pass a custom
	# value to '$cli_protocol->send_and_wait_for()'. To wait up to 60 seconds for the above
	# 'uptime' command to run, you'd use the following syntax:
	#
	#   $responses->{uptime} = $cli_protocol->send_and_wait_for( 'uptime', $prompt_regex, '60' );
	#
	#---------------------------------------------------------------------------------------
	
	#---------------------------------------------------------------------------------------
	# After a command's response has been placed in the $responses hashtable it can be 
	# passed through to the parsing module.  All of the parsing module's methods have
	# been explicitly imported in this module (see the use statement at the top).  Each
	# method can be called directly.  The line below calls the parse_system() method
	# passing in the $responses hashtable and the $printer instance of the XmlPrint module
	#  
	#	parse_system( $responses, $printer );
	#---------------------------------------------------------------------------------------
	
	
	#---------------------------------------------------------------------------------------
	# Adapters should be written with memory consumption in mind.  After each parsing method
	# is complete, the $responses hash should be analyzed and any pieces that are no longer
	# needed to build the Xerela Element Document (ZED) should be deleted from the hash as
	# shown in the following line.
	#
	#	delete ($responses->{uptime});
	#---------------------------------------------------------------------------------------
	
	my $termLen = $cli_protocol->send_and_wait_for( "config cli more false", $regex );
	if ($termLen =~ /not found/i)
	{
		# set the --more-- prompt if the term length 0 didn't go through
		$cli_protocol->set_more_prompt( '--More-- (q = quit)', '20');
	}
	
	#this is just in case it stays at the config/cli level, which shouldn't happen
	$cli_protocol->send_and_wait_for( "box", $regex );
	    								
  $responses->{config} = get_config( $cli_protocol, $connection_path, $regex);
  
  $responses->{system} = $cli_protocol->send_and_wait_for("show sys info", $regex);
  $responses->{files} = $cli_protocol->send_and_wait_for("directory", $regex);
	parse_system( $responses, $printer );
	parse_chassis( $responses, $printer );
	create_config( $responses, $printer );
	delete $responses->{files};
	
	
	$responses->{interfaces} = $cli_protocol->send_and_wait_for("show ip interface", $regex);
	parse_interfaces( $responses, $printer );
	delete $responses->{interfaces};
	
	$responses->{accounts} = $cli_protocol->send_and_wait_for("show cli password", $regex);
	parse_local_accounts($responses, $printer);
	delete $responses->{accounts};
	
	parse_snmp( $responses, $printer );
	delete $responses->{system};
	delete $responses->{config};
	
	$responses->{stp1} = $cli_protocol->send_and_wait_for("show stg info config", $regex);
	$responses->{stp2} = $cli_protocol->send_and_wait_for("show stg info status", $regex);
	parse_stp( $responses, $printer );
	delete $responses->{stp1};
	delete $responses->{stp2};
	
	$responses->{routes} = $cli_protocol->send_and_wait_for("show ip route info", $regex);
	parse_static_routes( $responses, $printer );
	delete $responses->{routes};
	
	$responses->{vlans} = $cli_protocol->send_and_wait_for("show vlan info basic", $regex);
	parse_vlans( $responses, $printer );
	delete $responses->{vlans};
	
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
	
	my $result = Xerela::Adapters::GenericAdapter::execute_cli_commands('accelar', $cli_protocol, $commands, $device_prompt_regex.'|(#|\$|>)\s*$');
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
	my $device_prompt_regex = Xerela::Adapters::Nortel::Accelar::AutoLogin::execute( $cli_protocol, $connection_path );
	
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
