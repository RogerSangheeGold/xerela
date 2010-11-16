package Xerela::Adapters::Cisco::IOS::Snmpcommunitychange;

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
	my ( $connectionPath, $addCommunity, $removeCommunity ) =
	  Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );
	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Cisco::IOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	$cliProtocol->send_and_wait_for( 'terminal length 0', $promptRegex );
	my $configPrompt = '#\s*$';
	my $response     = $cliProtocol->send_and_wait_for( 'config term', $configPrompt );

	if ( $addCommunity->{community} )
	{
		$response .= $cliProtocol->send_and_wait_for( 'snmp-server community ' . $addCommunity->{community} . ' ' . $addCommunity->{accessType}, $configPrompt );
	}

	if ( $removeCommunity->{community} )
	{
		$response .= $cliProtocol->send_and_wait_for( 'no snmp-server community ' . $removeCommunity->{community}, $configPrompt ); }

	$response .= $cliProtocol->send_and_wait_for( 'end',       $promptRegex );
	$response .= $cliProtocol->send_and_wait_for( 'write mem', $promptRegex );
	disconnect($cliProtocol);
	return $response;
}

1;
