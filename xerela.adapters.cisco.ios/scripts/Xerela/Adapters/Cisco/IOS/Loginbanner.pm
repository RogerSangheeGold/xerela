package Xerela::Adapters::Cisco::IOS::Loginbanner;

use strict;
use warnings;
use MIME::Base64 'decode_base64';

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
	my ( $connectionPath, $banner ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );
	$banner = decode_base64($banner);
	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Cisco::IOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	$cliProtocol->send_and_wait_for( 'terminal length 0', $promptRegex );
	my $configPrompt = '#\s*$';
	my $response = $cliProtocol->send_and_wait_for( 'config term', $configPrompt );
	
	my $delimeter = ($banner !~ /!/) ? '!' : '$';
	$response .= $cliProtocol->send_and_wait_for( 'banner motd '.$delimeter.''.$banner.''.$delimeter, $configPrompt );
	$response .= $cliProtocol->send_and_wait_for( 'end', $promptRegex );
	$response .= $cliProtocol->send_and_wait_for( 'write mem',         $promptRegex );
	disconnect($cliProtocol);
	return $response;
}

1;
