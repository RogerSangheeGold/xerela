package Xerela::Adapters::ThreeCom::CoreBuilder::Restore;

use strict;

use Xerela::CLIProtocol;
use Xerela::Response;
use Xerela::ConnectionPath;
use Xerela::CLIProtocolFactory;
use Xerela::Adapters::ThreeCom::CoreBuilder::AutoLogin;
use Xerela::Logger;
use Xerela::Adapters::Utils qw(escape_filename);

# Get the instance of the Xerela::Logger module
my $LOGGER = Xerela::Logger::get_logger();

sub invoke
{
	my $package_name = shift;
	my $command_doc  = shift; # how to restore config

	# Initial connection
	my ( $connectionPath, $restoreFile ) = Xerela::Typer::translate_document( $command_doc, 'connectionPath' );
	my ( $cli_protocol, $prompt_regex ) = _connect( $connectionPath );

	# Restore the config
	execute( $connectionPath, $cli_protocol, $prompt_regex, $restoreFile );

	# Disconnect from the specified device
	_disconnect($cli_protocol);
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
	my $device_prompt_regex = Xerela::Adapters::ThreeCom::CoreBuilder::AutoLogin::execute( $cli_protocol, $connection_path );

	# Store the regular expression that matches the primary prompt of the device under the key "prompt"
	# on the Xerela::CLIProtocol object
	$cli_protocol->set_prompt_by_name( 'enablePrompt', $device_prompt_regex );

	# Return the created Xerela::CLIProtocol object and the device prompt encountered after successfully connecting to a device.
	return ( $cli_protocol, $device_prompt_regex );
}

sub _disconnect
{

	# Grab the Xerela::CLIProtocol object passed in
	my $cliProtocol = shift;

	# Close this session and exit
	$cliProtocol->send("logout");
}

sub execute
{
	my ($connection_path, $cli_protocol, $enable_prompt_regex, $restoreFile) = @_;

	# Check to see if TFTP is supported
	my $tftp_protocol = $connection_path->get_protocol_by_name("TFTP") if ( defined($connection_path) );

	if ( $restoreFile->get_path() =~ /config/i )
	{
		if ( defined($tftp_protocol) )
		{
			restore_via_tftp( $connection_path, $cli_protocol, $restoreFile, $enable_prompt_regex );
		}
		else
		{
			$LOGGER->fatal("Unable to restore 3Com config. Protocol TFTP is not available.");
		}
	}
	else
	{
		$LOGGER->fatal( "Unable to promote this type of configuration '" . $restoreFile->get_path() . "'." );
	}
}

sub restore_via_tftp
{
	my ( $connectionPath, $cliProtocol, $promoteFile, $promptRegex ) = @_;

	my @responses = ();
	push( @responses, Xerela::Response->new( '[Hh]ost IP [Aa]ddress',			\&_send_tftp_ip ) );
	push( @responses, Xerela::Response->new( 'NV [Cc]ontrol [Ff]ile',			\&_send_source_filename ) );
	push( @responses, Xerela::Response->new( 'Enter an optional file label',	\&_send_user_note ) );
	push( @responses, Xerela::Response->new( 'Do you wish to continue',			\&_send_yes ) );
	push( @responses, Xerela::Response->new( 'Error|Failed|Invalid',			undef, 				$TFTP_ERROR ) );

	$cliProtocol->send("system nvdata restore");
	my $response = $cliProtocol->wait_for_responses( \@responses );
	if ( $response )
	{
		my $nextMethod = $response->get_next_interaction();
		return &$nextMethod( $connectionPath, $cliProtocol, $promoteFile, $promptRegex );
	}
	else
	{
		$LOGGER->fatal("Invalid response from device encountered!");
	}
}

sub _send_tftp_ip
{
	my ( $connectionPath, $cliProtocol, $promoteFile, $promptRegex ) = @_;
	my $tftpFileServer = $connectionPath->get_file_server_by_name("TFTP");

	my @responses = ();
	push( @responses, Xerela::Response->new( 'NV [Cc]ontrol [Ff]ile',			\&_send_source_filename ) );
	push( @responses, Xerela::Response->new( 'Enter an optional file label',	\&_send_user_note ) );
	push( @responses, Xerela::Response->new( 'Do you wish to continue',			\&_send_yes ) );
	push( @responses, Xerela::Response->new( 'Error|Failed|Invalid',			undef, 				$TFTP_ERROR ) );

	$cliProtocol->send( $tftpFileServer->get_ip_address() );
	my $response = $cliProtocol->wait_for_responses( \@responses );
	if ($response)
	{
		my $nextMethod = $response->get_next_interaction();
		return &$nextMethod( $connectionPath, $cliProtocol, $promoteFile, $promptRegex );
	}
	else
	{
		$LOGGER->fatal("Invalid response from device encountered!");
	}
}

sub _send_source_filename
{
	my ( $connectionPath, $cliProtocol, $promoteFile, $promptRegex ) = @_;
	my $tftpFileServer	= $connectionPath->get_file_server_by_name("TFTP");
	my $configName		= escape_filename($cliProtocol->get_ip_address() . ".config");
	$configName			=~ s/\./-/g;
	my $configFile		= $tftpFileServer->get_root_dir() . "/$configName";

	# Write out the file to the TFTP directory
	open( CONFIG_FILE, ">$configFile" );
	print CONFIG_FILE $promoteFile->get_blob();
	close( CONFIG_FILE );

	my @responses = ();
	push( @responses, Xerela::Response->new( 'Enter an optional file label',	\&_send_user_note ) );
	push( @responses, Xerela::Response->new( 'Do you wish to continue',			\&_send_yes ) );
	push( @responses, Xerela::Response->new( 'Error|Failed|Invalid',			undef, 				$TFTP_ERROR ) );

	$cliProtocol->send($configName);
	my $response = $cliProtocol->wait_for_responses( \@responses );
	if ($response)
	{
		my $nextMethod = $response->get_next_interaction();
		return &$nextMethod( $connectionPath, $cliProtocol, $promoteFile, $promptRegex );
	}
	else
	{
		$LOGGER->fatal("Invalid response from device encountered!");
	}
}

sub _send_user_note
{
	my ( $connectionPath, $cliProtocol, $promoteFile, $promptRegex ) = @_;

	my @responses = ();
	push( @responses, Xerela::Response->new( 'Do you wish to continue',			\&_send_yes ) );
	push( @responses, Xerela::Response->new( 'data sucessfully restored', 		\&_finish ) );
	push( @responses, Xerela::Response->new( 'Error|Failed|Invalid',			undef, $TFTP_ERROR ) );

	$cliProtocol->send('Xerela Restore');
	my $response = $cliProtocol->wait_for_responses( \@responses, 120 );
	if ($response)
	{
		my $nextMethod = $response->get_next_interaction();
		return &$nextMethod( $connectionPath, $cliProtocol, $promoteFile, $promptRegex );
	}
	else
	{
		$LOGGER->fatal("Invalid response from device encountered!");
	}
}

sub _send_yes
{
	my ( $connectionPath, $cliProtocol, $promoteFile, $promptRegex ) = @_;

	my @responses = ();
	push( @responses, Xerela::Response->new( 'data sucessfully restored', 	\&_finish ) );
	push( @responses, Xerela::Response->new( 'Error|Failed|Invalid',		undef, $TFTP_ERROR ) );

	$cliProtocol->send('y');
	my $response = $cliProtocol->wait_for_responses( \@responses, 120 );
	if ($response)
	{
		my $nextMethod = $response->get_next_interaction();
		return &$nextMethod( $connectionPath, $cliProtocol, $promoteFile, $promptRegex );
	}
	else
	{
		$LOGGER->fatal("Invalid response from device encountered!");
	}
}

sub _finish
{
	my ( $connectionPath, $cliProtocol, $promoteFile, $promptRegex ) = @_;
	my $tftpFileServer	= $connectionPath->get_file_server_by_name("TFTP");
	my $configName		= escape_filename($cliProtocol->get_ip_address() . ".config");
	$configName			=~ s/\./-/g;
	my $configFile		= $tftpFileServer->get_root_dir() . "/$configName";
	unlink($configFile);

	return 0;
}

1;
