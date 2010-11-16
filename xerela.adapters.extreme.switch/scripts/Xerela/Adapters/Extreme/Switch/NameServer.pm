package Xerela::Adapters::Extreme::Switch::NameServer;

use strict;
use warnings;

use Xerela::Adapters::Extreme::Switch::AutoLogin;
use Xerela::Adapters::Extreme::Switch::Disconnect qw(disconnect);
use Xerela::CLIProtocolFactory;
use Xerela::Logger;
use Xerela::Typer;

my $LOGGER = Xerela::Logger::get_logger();

sub invoke
{
	my $pkg            = shift;
	my $syslogDocument = shift;

	# Initial connection
	my ( $connectionPath, $nsAddress, $nsAction, $domainSuffixName ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );

	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Extreme::Switch::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $response;

	my $configRegex = '(?:\*\s+)?'.$promptRegex;
	if ( $nsAddress =~ /^[\da-f\:\.]+$/i )
	{
		if ( $nsAction eq 'add' )
		{
			$response .= $cliProtocol->send_and_wait_for( "configure dns-client add name-server $nsAddress", $configRegex );
		}
		elsif ( $nsAction eq 'delete' )
		{
			$response .= $cliProtocol->send_and_wait_for( "configure dns-client delete name-server $nsAddress", $configRegex );
		}
	}
	if ( $domainSuffixName =~ /^[\da-z\.\-]+$/i )
	{
		$response .= $cliProtocol->send_and_wait_for( "configure dns-client add domain-suffix $domainSuffixName", $configRegex );
	}
	$response .= $cliProtocol->send_and_wait_for( 'save', 'Do you want to save' );
	$response .= $cliProtocol->send_and_wait_for( '!.!', $promptRegex, 150 );

	disconnect($cliProtocol);

	return $response;
}


1;
