package Xerela::Adapters::APC::SmartUPS;

use strict;

use Xerela::Adapters::APC::SmartUPS::AutoLogin;
use Xerela::Adapters::APC::SmartUPS::Parsers
  qw(parse_routing create_config parse_local_accounts parse_chassis parse_snmp parse_system parse_interfaces parse_static_routes);
use Xerela::Adapters::APC::SmartUPS::GetConfig qw(get_config);
use Xerela::Adapters::APC::SmartUPS::Restore;
use Xerela::Adapters::Utils qw(get_model_filehandle close_model_filehandle);
use Xerela::Adapters::GenericAdapter;
use Xerela::CLIProtocol;
use Xerela::CLIProtocolFactory;
use Xerela::ConnectionPath;
use Xerela::Credentials;
use Xerela::Logger;
use Xerela::Model::XmlPrint;
use Xerela::SNMP;
use Xerela::SnmpSessionFactory;
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
	my $responses   = {};       # will contain device responses to be handed to the Parsers module

	# Translate the backup operation XML document into Xerela::ConnectionPath
	my ($connectionPath) = Xerela::Typer::translate_document( $backupDoc, 'connectionPath' );

	# Set up the XmlPrint object for printing the XerelaElementDocument (ZED)
	my $filehandle = get_model_filehandle( 'APC Smart-UPS', $connectionPath->get_ip_address() );
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, 'common' );
	$printer->open_model();

	# Build the Telnet/SSH connection
	my ( $cliProtocol, $promptRegex ) = _connect($connectionPath);
	
	# Build the SNMP connection object
	my $snmpSession = Xerela::SnmpSessionFactory->create($connectionPath);
	$responses->{snmp}       = Xerela::Adapters::GenericAdapter::get_snmp($snmpSession);         
	$responses->{interfaces} = Xerela::Adapters::GenericAdapter::get_interfaces($snmpSession);
	$responses->{uptime}     = _get_uptime($snmpSession);

	$cliProtocol->set_more_prompt( 'Press <ENTER> to continue...', '20' );
	$responses->{mainmenu} = _main_menu( $cliProtocol, $promptRegex );
	_enter_menu( 'System', $cliProtocol, $promptRegex );
	$responses->{system} = _enter_menu( 'About System', $cliProtocol, $promptRegex );
	_main_menu( $cliProtocol, $promptRegex );
	$responses->{config} = get_config( $cliProtocol, $connectionPath );
	_disconnect($cliProtocol);

	parse_system( $responses, $printer );
	parse_chassis( $responses, $printer );
	create_config( $responses, $printer );
	parse_interfaces( $responses, $printer );
	parse_local_accounts( $responses, $printer );
	parse_snmp( $responses, $printer );
	parse_static_routes( $responses, $printer );
	$printer->close_model();                # close out the XerelaElementDocument
	close_model_filehandle($filehandle);    # Make sure to close the model file handle
}

sub _main_menu
{

	# escape to the main menu
	my $cliProtocol = shift;
	my $prompt      = shift;
	my $menu = ''; 
	while ( $menu !~ /\d+-\s*Logout/ )
	{
		$menu = $cliProtocol->send_as_bytes_and_wait( '1B', $prompt );    # escape until we get to the main menu
	}
	return $menu;
}

sub _enter_menu
{

	# press the number corresponding to the incoming menu name and return the results
	my $menuName    = shift;
	my $cliProtocol = shift;
	my $prompt      = shift;

	my $menu = $cliProtocol->send_and_wait_for( '', $prompt );    # enter nothing so we can see the current menu
	if ( $menu =~ /(\d+)-\s*$menuName/ )
	{
		return $cliProtocol->send_and_wait_for( $1, $prompt );    # choose the number corresponding to the menu name
	}
	return $menu;
}

sub commands
{
	my $packageName = shift;
	my $commandDoc  = shift;

	my ( $connectionPath, $commands ) = Xerela::Typer::translate_document( $commandDoc, 'connectionPath' );
	my ( $cliProtocol, $devicePromptRegex ) = _connect($connectionPath);

	my $result = Xerela::Adapters::GenericAdapter::execute_cli_commands( 'APC Smart-UPS', $cliProtocol, $commands, $devicePromptRegex . '|(#|\$|>)\s*$' );
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
	my $devicePromptRegex = Xerela::Adapters::APC::SmartUPS::AutoLogin::execute( $cliProtocol, $connectionPath );

	# Store the regular expression that matches the primary prompt of the device under the key "prompt"
	# on the Xerela::CLIProtocol object
	$cliProtocol->set_prompt_by_name( 'prompt', $devicePromptRegex );

	# Return the created Xerela::CLIProtocol object and the device prompt encountered after successfully connecting to a device.
	return ( $cliProtocol, $devicePromptRegex );
}

sub _disconnect
{
	my $cliProtocol = shift;
	my $prompt      = $cliProtocol->get_prompt_by_name('prompt');
	my $menu        = _main_menu( $cliProtocol, $prompt );
	if ( $menu =~ /(\d+)-\s*Logout/ )
	{
		return $cliProtocol->send($1);
	}
	$cliProtocol->disconnect();
}

sub _get_uptime
{

	# retrieve the sysUpTime via SNMP
	my $snmpSession = shift;

	$snmpSession->translate( [ '-timeticks' => 0, ] );    # turn off Net::SNMP translation of timeticks
	my $sysUpTimeOid = '.1.3.6.1.2.1.1.3.0';                              # the OID for sysUpTime
	my $getResult = Xerela::SNMP::get( $snmpSession, [$sysUpTimeOid] );
	return $getResult->{$sysUpTimeOid};
}

1;
