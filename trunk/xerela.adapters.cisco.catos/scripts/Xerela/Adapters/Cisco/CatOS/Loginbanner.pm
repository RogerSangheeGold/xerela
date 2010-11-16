package Xerela::Adapters::Cisco::CatOS::Loginbanner;

use strict;
use warnings;
use MIME::Base64 'decode_base64';

use Xerela::Adapters::Cisco::CatOS::AutoLogin;
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
	my $promptRegex = Xerela::Adapters::Cisco::CatOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	
	my $delimeter = ($banner !~ /!/) ? '!' : '$';
	my $response = $cliProtocol->send_and_wait_for( 'set banner motd '.$delimeter.''.$banner.''.$delimeter, $promptRegex );
	$cliProtocol->send('exit');
	return $response;
}

1;
