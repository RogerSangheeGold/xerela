package Xerela::Adapters::Extreme::Switch::Snmpcommunitychange;

use strict;
use warnings;

use Xerela::Adapters::Extreme::Switch::AutoLogin;
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
	my $promptRegex  = Xerela::Adapters::Extreme::Switch::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $configPrompt = '#\s*$';
	my $response;
	if ( $addCommunity->{community} )
	{
		my $authorization = ( $addCommunity->{accessType} =~ /RW/i ) ? 'readwrite' : 'readonly';
		$response .= $cliProtocol->send_and_wait_for( 'configure snmp add community '.$authorization.' '.$addCommunity->{community}, $configPrompt );
	}

	if ( $removeCommunity->{community} )
	{
		my $authorization = ( $removeCommunity->{accessType} =~ /RW/i ) ? 'readwrite' : 'readonly';
		$response .= $cliProtocol->send_and_wait_for( "configure snmp delete community $authorization " . $removeCommunity->{community}, $configPrompt );
	}
	
	$response .=    $cliProtocol->send_and_wait_for('save config primary', 'database|overwrite it\? \(y\/n\)');
	$response .=    $cliProtocol->send_and_wait_for('y', $configPrompt, 300);
	$cliProtocol->send('exit');
	return $response;
}

1;
