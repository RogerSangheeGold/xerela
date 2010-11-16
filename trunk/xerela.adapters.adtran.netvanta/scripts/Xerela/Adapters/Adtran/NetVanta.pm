package Xerela::Adapters::Adtran::NetVanta;

use strict;

use Xerela::CLIProtocolFactory;
use Xerela::CLIProtocol;
use Xerela::ConnectionPath;
use Xerela::Credentials;
use Xerela::Adapters::Adtran::NetVanta::AutoLogin;
use Xerela::Adapters::Adtran::NetVanta::GetRunningConfig qw(get_running_config);
use Xerela::Adapters::Adtran::NetVanta::GetStartupConfig qw(get_startup_config);
use Xerela::Adapters::Adtran::NetVanta::Parsers
  qw(create_config parse_local_accounts parse_chassis parse_snmp parse_system parse_interfaces parse_stp parse_static_routes);
use Xerela::Adapters::Adtran::NetVanta::Restore;
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
	my ( $cli_protocol, $enable_prompt_regex ) = _connect( $connection_path );

	# Grab an output filehandle for the model.  This usually points to STDOUT
	my $filehandle = get_model_filehandle( 'Netvanta', $connection_path->get_ip_address() );

	# initialize the model printer
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, 'common' );
	$printer->open_model();

	# Begin executing commands on the device.  The results of each command will
	# be stored in a single hashtable ($parsers) and fed into each parsing method
	my $responses = {};
	
	my $more_removal = undef;
	
	# Get rid of the more prompt
	my $termLen = $cli_protocol->send_and_wait_for( "terminal length 0", $enable_prompt_regex );
	if ( $termLen =~ /Unrecognized command/i )
	{
		# set the --more-- prompt if the term length 0 didn't go through
		$cli_protocol->set_more_prompt( '--MORE--', '20' );
		
		#This, until fixed where it should, brings the need
		#to add code to delete all the lines with a --MORE--
		#in them (and any leading/trailing whitespaces), like this:
		#$version =~ s/^--MORE--\W*/  /mgis;
		#it will be conditional here for now
		$more_removal = 1;
		
	}
	 
	# Gather inputs for the model
	#No =~s/^--MORE--\s+//gis for this one, it's done before it gets here
	$responses->{running_config} = get_running_config( $cli_protocol, $connection_path );
	
	my $version = $cli_protocol->send_and_wait_for( "show version", $enable_prompt_regex );
	if ($more_removal)
	{
		$version =~ s/--MORE--[\x8\s]*//mgi;
	}
	$version =~ /show version(.*)$/msig;
	$responses->{version} = $1;
	
	my $config = $cli_protocol->send_and_wait_for( "show configuration", $enable_prompt_regex );
	if ($more_removal)
	{
		#print "antes:\n$config\n";
		$config =~ s/--MORE--[\x8\s]*//mgi;
		#$config =~ s/\p{C}//migs;
		#print "*****************************\ndespues:\n$config\n";
	}
	$config =~ /show configuration(.*)$/msig;
	$responses->{config} = $1;
	
	my $files	= $cli_protocol->send_and_wait_for( 'show flash', $enable_prompt_regex );
	if ($more_removal)
	{
		$files =~ s/--MORE--[\x8\s]*//mgi;
	}
	$files =~ /show flash(.*)$/msi;
	$responses->{files}	= $files;
	
	parse_system( $responses, $printer );
	
	#No =~s/^--MORE--\s+//gis for this one, it's done before it gets here
	$responses->{running_config} = get_running_config( $cli_protocol, $connection_path );
	$responses->{startup_config} = get_startup_config( $cli_protocol, $connection_path );
	
	parse_chassis( $responses, $printer );
	create_config( $responses, $printer );
	delete $responses->{startup_config};
	
	my $interfaces = $cli_protocol->send_and_wait_for( "show interfaces", $enable_prompt_regex );
	if ($more_removal)
	{
		#leave two spaces for the parser to keep working
		$interfaces =~ s/--MORE--[\x8\s]*//mgi;
	}
	$interfaces =~ /Displaying interfaces\.\.\.\s*(.*)$/msig;
	$responses->{interfaces} = $1;
	parse_interfaces($responses, $printer);
	#Free memory
	delete $responses->{interfaces};
	
	#Local user account information
	parse_local_accounts($responses, $printer);
			
	#SNMP Community Information
	parse_snmp($responses, $printer);
	delete $responses->{config};
		
	my $static_routes = $cli_protocol->send_and_wait_for( "show ip route static", $enable_prompt_regex );
	if ($more_removal)
	{
		$static_routes =~ s/--MORE--[\x8\s]*//mgi;
	}
	$static_routes =~ /show ip route static(.*)$/mis;
	$responses->{static_routes} = $1;
	parse_static_routes( $responses, $printer );
	delete $responses->{static_routes};

	
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
	
	my $result = Xerela::Adapters::GenericAdapter::execute_cli_commands('Netvanta', $cli_protocol, $commands, '(#|\$|>)\s*$');
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
	my $device_prompt_regex = Xerela::Adapters::Adtran::NetVanta::AutoLogin::execute( $cli_protocol, $connection_path );
	
	# Store the regular expression that matches the primary prompt of the device under the key "prompt"
	# on the Xerela::CLIProtocol object
	$cli_protocol->set_prompt_by_name( 'enablePrompt', $device_prompt_regex );
	
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
