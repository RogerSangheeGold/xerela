package Xerela::Adapters::Extreme::XOS::Loginbanner;

use strict;
use warnings;
use MIME::Base64 'decode_base64';

use Xerela::Adapters::Extreme::XOS::AutoLogin;
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
	$banner =~ s/^\s*$//m;
	my $cliProtocol  = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex  = Xerela::Adapters::Extreme::XOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $configPrompt = '#\s*$';

	my $response;
	$response .= $cliProtocol->send('configure banner');
	$response .= $cliProtocol->send($banner);
	$response .= $cliProtocol->send('');                                                 # empty line ends the banner
	$response .= $cliProtocol->send_and_wait_for( 'save config primary', '\(y\/n\)' );
	$response .= $cliProtocol->send_and_wait_for( 'y', $configPrompt, 300 );
	$cliProtocol->send('exit');
	return $response;
}

1;
