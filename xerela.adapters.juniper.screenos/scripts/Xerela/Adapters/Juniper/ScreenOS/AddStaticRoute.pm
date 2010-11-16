package Xerela::Adapters::Juniper::ScreenOS::AddStaticRoute;

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
	my ( $connectionPath, $destAddress, $destMask, $gwAddress ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );

	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Juniper::ScreenOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $response	= $cliProtocol->send_and_wait_for( 'set console page 0', $promptRegex );

	my $ifs		= $cliProtocol->send_and_wait_for( "get interface", $promptRegex );
	$response	.= $ifs;
	if ( $destAddress =~ /^\S+$/i && $destMask =~ /^\S+$/i && $gwAddress =~ /^\S+$/i )
	{
		$destMask = mask_to_bits( $destMask );
		while ( $ifs =~ /^(\S+)\s+\d+\..+$/mig )
		{
			my $ifName = $1;
			$response .= $cliProtocol->send_and_wait_for( "set route $destAddress/$destMask interface $ifName gateway $gwAddress", $promptRegex );
		}
	}
	$response .= $cliProtocol->send_and_wait_for( 'save', $promptRegex );

	disconnect($cliProtocol);

	return $response;
}

1;
