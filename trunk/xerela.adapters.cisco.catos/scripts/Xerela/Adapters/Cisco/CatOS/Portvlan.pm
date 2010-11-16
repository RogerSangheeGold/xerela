package Xerela::Adapters::Cisco::CatOS::Portvlan;

use strict;
use warnings;

use Xerela::Adapters::Cisco::CatOS::AutoLogin;
use Xerela::Adapters::Cisco::CatOS::Disconnect qw(disconnect);
use Xerela::CLIProtocolFactory;
use Xerela::Logger;
use Xerela::Typer;

my $LOGGER = Xerela::Logger::get_logger();

sub invoke
{
	my $pkg = shift;
	my $doc = shift;

	# Initial connection
	my ( $connectionPath, $interfaces ) = Xerela::Typer::translate_document( $doc, 'connectionPath' );
	my $interfacesHash = $interfaces->{interface};
	my $cliProtocol    = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex    = Xerela::Adapters::Cisco::CatOS::AutoLogin::execute( $cliProtocol, $connectionPath );

	my $response;
	for my $intName ( sort keys %$interfacesHash )
	{
		my $vlan = $interfacesHash->{$intName}->{vlanNumber};
		$response .= $cliProtocol->send_and_wait_for( 'set vlan '. $vlan.' ' . $intName, $promptRegex );
	}
	disconnect($cliProtocol);
	return $response;
}

1;
