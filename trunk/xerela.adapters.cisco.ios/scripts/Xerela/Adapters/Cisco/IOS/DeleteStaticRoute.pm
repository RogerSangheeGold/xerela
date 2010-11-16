package Xerela::Adapters::Cisco::IOS::DeleteStaticRoute;

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
	my ( $connectionPath, $staticRoutes ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );
	my $staticRoutesHash	= $staticRoutes->{staticRoute};
	my $cliProtocol			= Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex			= Xerela::Adapters::Cisco::IOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	$cliProtocol->send_and_wait_for( 'terminal length 0', $promptRegex );
	my $configPrompt = '#\s*$';
	my $response     = $cliProtocol->send_and_wait_for( 'config term', $configPrompt );

	foreach my $key ( @{$staticRoutesHash} )
	{
		my ( $destAddress, $destMask, $gwAddress ) = ( $key->{destAddress}, $key->{destMask}, $key->{gwAddress} );
		$response .= $cliProtocol->send_and_wait_for( "no ip route $destAddress $destMask $gwAddress", $configPrompt );
	}
	$response .= $cliProtocol->send_and_wait_for( 'end',       $promptRegex );
	$response .= $cliProtocol->send_and_wait_for( 'write mem', $promptRegex );

	disconnect($cliProtocol);

	return $response;
}

1;
