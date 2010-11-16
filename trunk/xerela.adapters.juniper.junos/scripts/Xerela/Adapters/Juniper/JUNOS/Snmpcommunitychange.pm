package Xerela::Adapters::Juniper::JUNOS::Snmpcommunitychange;

use strict;
use warnings;

use Xerela::Adapters::Juniper::JUNOS::AutoLogin;
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
	my $cliProtocol  = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex  = Xerela::Adapters::Juniper::JUNOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $configPrompt = '#\s*$';
	my $response     = $cliProtocol->send_and_wait_for( 'configure', $configPrompt );

	if ( $addCommunity->{community} )
	{
		my $authorization = ( $addCommunity->{accessType} =~ /RW/i ) ? 'read-write' : 'read-only';
		$response .=
		  $cliProtocol->send_and_wait_for(
			'set snmp community ' . $addCommunity->{community} . ' authorization ' . $authorization,
			$configPrompt );
	}

	if ( $removeCommunity->{community} )
	{
		$response .=
		  $cliProtocol->send_and_wait_for( 'delete snmp community ' . $removeCommunity->{community}, $configPrompt );
	}

	$response .= $cliProtocol->send_and_wait_for( 'commit', $configPrompt );
	$response .= $cliProtocol->send_and_wait_for( 'quit',   $promptRegex );
	$cliProtocol->send('exit');
	return $response;
}

1;
