package Xerela::Adapters::Cisco::CatOS::Snmpcommunitychange;

use strict;
use warnings;

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
	my ( $connectionPath, $addCommunity, $removeCommunity ) =
	  Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );
	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Cisco::CatOS::AutoLogin::execute( $cliProtocol, $connectionPath );

	my $response;
	
	if ( $removeCommunity->{community} )
	{
		$response .= "\n**** The community string '".$removeCommunity->{community}."' can be removed by being overwritten. ****\n\n";
	}
	
	if ( $addCommunity->{community} )
	{
		my $accessType = ( $addCommunity->{accessType} =~ /ro/i )? 'read-only' : 'read-write'; 
		$response .= $cliProtocol->send_and_wait_for( 'set snmp community '.$accessType.' '. $addCommunity->{community}, $promptRegex );
	}


	$cliProtocol->send('exit');
	return $response;
}

1;
