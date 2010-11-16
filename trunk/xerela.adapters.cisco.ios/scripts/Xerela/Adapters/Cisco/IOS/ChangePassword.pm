package Xerela::Adapters::Cisco::IOS::ChangePassword;

use strict;
use warnings;

use Xerela::Adapters::Cisco::IOS::AutoLogin;
use Xerela::Adapters::Cisco::IOS::Disconnect qw(disconnect);
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
	my $promptRegex = Xerela::Adapters::Cisco::IOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	$cliProtocol->send_and_wait_for( 'terminal length 0', $promptRegex );
	my $configPrompt	= '#\s*$';
	$_					= $cliProtocol->send_and_wait_for( 'show running-config', $promptRegex );
	my $response    	= $cliProtocol->send_and_wait_for( 'config term', $configPrompt );

	if ( $username =~ /^\S+$/i && $newPassword =~ /^\S+$/i )
	{
		$response .= $cliProtocol->send_and_wait_for( "username $username password $newPassword", $configPrompt ) if ( /^username\s+$username\s+/mi );
	}
	$response .= $cliProtocol->send_and_wait_for( 'end',       $promptRegex );
	$response .= $cliProtocol->send_and_wait_for( 'write mem', $promptRegex );

	disconnect($cliProtocol);

	return $response;
}

1;
