package Xerela::Adapters::Juniper::ScreenOS::DeleteLocalAccount;

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
	my ( $connectionPath, $username ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );

	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Juniper::ScreenOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $response	= $cliProtocol->send_and_wait_for( 'set console page 0', $promptRegex );

	if ( $username =~ /^\S+$/i )
	{
		$response .= $cliProtocol->send_and_wait_for( "unset user $username", $promptRegex );
	}
	$response .= $cliProtocol->send_and_wait_for( 'save', $promptRegex );

	disconnect($cliProtocol);

	return $response;
}

1;
