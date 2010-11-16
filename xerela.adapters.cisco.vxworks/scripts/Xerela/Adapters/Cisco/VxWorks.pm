package Xerela::Adapters::Cisco::VxWorks;

use strict;
use Xerela::CLIProtocolFactory;
use Xerela::CLIProtocol;
use Xerela::ConnectionPath;
use Xerela::Credentials;
use Xerela::HTTP;
use Xerela::Adapters::Cisco::VxWorks::AutoLogin;
use Xerela::Adapters::Cisco::VxWorks::Parsers
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

	# Grab the Xerela::Credentials object from the connection path
	my $credentials = $connection_path->get_credentials();

	# Grab the Xerela::ConnectionPath::Protocol object that represents an HTTP/HTTPS protocol.
	my $http_protocol = $connection_path->get_protocol_by_name("HTTPS");
	$http_protocol = $connection_path->get_protocol_by_name("HTTP") if ( !defined($http_protocol) );

	# If neither a HTTP or HTTPS protocol could be found, then that is fatal
	if ( !defined($http_protocol) )
	{
		$LOGGER->fatal("No 'HTTP' or 'HTTPS' protocol defined within the specified connection path!  Please make sure that either is 'HTTP' or 'HTTPS' protocol defined!");
	}

	# Create a new Xerela::HTTP agent and connect to it using the information from the Xerela::ConnectionPath
	# and Xerela::Credentials objects.
	my $http_agent = Xerela::HTTP->new();
	$http_agent->connect(
		$http_protocol->get_name(),
		$connection_path->get_ip_address(),
		$http_protocol->get_port(),
		$credentials->{username},
		$credentials->{password},
	);

	# Grab an output filehandle for the model.  This usually points to STDOUT
	my $filehandle = get_model_filehandle( 'Cisco VxWorks', $connection_path->get_ip_address() );

	# initialize the model printer
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, 'common' );
	$printer->open_model();

	# Begin executing commands on the device.  The results of each command will
	# be stored in a single hashtable ($parsers) and fed into each parsing method
	my $responses = {};

	$responses->{home} = $http_agent->get("");
	$responses->{snmp} = $http_agent->get("SetSNMP.shm");

	$_ = $http_agent->get("SetConfiguration.shm");
	if ( /<a href="\/?([^"]+)">Download <b>All<\/b> System Configuration/ )
	{
		$responses->{config} = $http_agent->get($1);
	}

	$responses->{users}		= $http_agent->get("ShowUsers.shm");
	$responses->{routes}	= $http_agent->get("SetRouting.shm");

	my ( $if_blob ) = $responses->{home} =~ /Network Ports(.+)\[Home\]/mis;
	while ( $if_blob =~ /<[^>]+><a\s+href="\/?([^"]+)">([^<]+)/mig )
	{
		$_ = $1;
		if ( /ifIndex=(\d+)/i )
		{
			my $if_id = $1;
			$responses->{'ifs'.$if_id} = $http_agent->get($_);
		}
	}

	$responses->{setup} = $http_agent->get("Setup.shm");
	while ( $responses->{setup} =~ /<a href="\/?([^"\s]+)">Identification<\/a>/mig )
	{
		$_ = $1;
		if ( /ifIndex=(\d+)/i )
		{
			my $if_id = $1;
			$responses->{'ifi'.$if_id} = $http_agent->get($_);
		}
	}

	parse_system( $responses, $printer );
	parse_chassis( $responses, $printer );
	create_config( $responses, $printer );
	my $subnets = parse_interfaces( $responses, $printer );
	parse_local_accounts( $responses, $printer );
	parse_snmp( $responses, $printer );

	while ( ( my $key, my $value ) = each (%{$responses}) )
	{
		delete $responses->{$key};
	}

	# close out the XerelaElementDocument
	$printer->close_model();
	
	# Make sure to close the model file handle
	close_model_filehandle($filehandle);
}

sub commands
{
	my $package_name = shift;
	my $command_doc = shift;
	
	my ( $connection_path, $commands ) = Xerela::Typer::translate_document( $command_doc, 'connectionPath' );
	my ( $cli_protocol, $device_prompt_regex ) = _connect( $connection_path );
	
	my $result = Xerela::Adapters::GenericAdapter::execute_cli_commands('Cisco VxWorks', $cli_protocol, $commands, '(#|\$|>)\s*$');
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
	my $device_prompt_regex = Xerela::Adapters::Cisco::VxWorks::AutoLogin::execute( $cli_protocol, $connection_path );
	
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
