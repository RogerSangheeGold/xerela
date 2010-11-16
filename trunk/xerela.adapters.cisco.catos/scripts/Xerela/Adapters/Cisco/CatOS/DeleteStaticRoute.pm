package Xerela::Adapters::Cisco::CatOS::DeleteStaticRoute;

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
	my ( $connectionPath, $staticRoutes ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );
	my $staticRoutesHash	= $staticRoutes->{staticRoute};
	my $cliProtocol			= Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex			= Xerela::Adapters::Cisco::CatOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $response			= $cliProtocol->send_and_wait_for( 'set length 0', $promptRegex );

	foreach my $key ( @{$staticRoutesHash} )
	{
		my ( $destAddress, $destMask, $gwAddress ) = ( $key->{destAddress}, $key->{destMask}, $key->{gwAddress} );
		$destMask = mask_to_bits( $destMask );
		$response .= $cliProtocol->send_and_wait_for( "clear ip route $destAddress $gwAddress", $promptRegex );
	}

	disconnect($cliProtocol);

	return $response;
}

1;
