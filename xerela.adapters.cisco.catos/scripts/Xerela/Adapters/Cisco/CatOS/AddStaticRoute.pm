package Xerela::Adapters::Cisco::CatOS::AddStaticRoute;

use strict;
use warnings;

use Xerela::Adapters::Cisco::CatOS::AutoLogin;
use Xerela::Adapters::Cisco::CatOS::Disconnect qw(disconnect);
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
	my ( $connectionPath, $destAddress, $destMask, $gwAddress ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );

	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Cisco::CatOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $response	= $cliProtocol->send_and_wait_for( 'set length 0', $promptRegex );

	if ( $destAddress =~ /^\S+$/i && $destMask =~ /^\S+$/i && $gwAddress =~ /^\S+$/i )
	{
		$destMask = mask_to_bits( $destMask );
		$response .= $cliProtocol->send_and_wait_for( "set ip route $destAddress/$destMask $gwAddress", $promptRegex );
	}

	disconnect($cliProtocol);

	return $response;
}

1;
