package Xerela::Adapters::Juniper::ScreenOS::ChangePassword;

use strict;
use warnings;

use Xerela::Adapters::Juniper::ScreenOS::AutoLogin;
use Xerela::Adapters::Juniper::ScreenOS::Disconnect qw(disconnect);
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

	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Juniper::ScreenOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $response	= $cliProtocol->send_and_wait_for( 'set console page 0', $promptRegex );
	$_				= $cliProtocol->send_and_wait_for( 'get user all', $promptRegex );

	if ( $username =~ /^\S+$/i && $newPassword =~ /^\S+$/i )
	{
		$response .= $cliProtocol->send_and_wait_for( "set user $username password $newPassword", $promptRegex ) if ( /^\s*\d+\s+$username\s+/mi );
	}
	$response .= $cliProtocol->send_and_wait_for( 'save', $promptRegex );

	disconnect($cliProtocol);

	return $response;
}

1;
