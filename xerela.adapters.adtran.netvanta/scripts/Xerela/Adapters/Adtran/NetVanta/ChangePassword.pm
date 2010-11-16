package Xerela::Adapters::Adtran::NetVanta::ChangePassword;

use strict;
use warnings;

use Xerela::Adapters::Adtran::NetVanta::AutoLogin;
use Xerela::Adapters::Utils qw(mask_to_bits);
use Xerela::CLIProtocolFactory;
use Xerela::Logger;
use Xerela::Typer;

my $LOGGER = Xerela::Logger::get_logger();

sub invoke
{
	my $pkg            = shift;
	my $syslogDocument = shift;

	# Initial connection
	my ( $connectionPath, $username, $newPassword ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );

	my $cliProtocol		= Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex		= Xerela::Adapters::Adtran::NetVanta::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $configPrompt	= '#\s*$';
	$_					= $cliProtocol->send_and_wait_for( 'show running-config', $promptRegex );
	my $response		= $cliProtocol->send_and_wait_for( 'configure terminal', $configPrompt );

	if ( $username =~ /^\S+$/i && $newPassword =~ /^\S+$/i )
	{
		$response .= $cliProtocol->send_and_wait_for( "username $username password $newPassword", $configPrompt ) if ( /^username\s+"$username"\s+/mi );
	}
	$response .= $cliProtocol->send_and_wait_for( "exit", $promptRegex );
	$response .= $cliProtocol->send_and_wait_for( "write memory", $promptRegex );

	_disconnect($cliProtocol);

	return $response;
}

sub _disconnect
{
	# Grab the Xerela::CLIProtocol object passed in
	my $cli_protocol = shift;

	# Close this session and exit
	$cli_protocol->send("exit");
}

1;
