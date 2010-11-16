package Xerela::Adapters::Cisco::CatOS::Syslogsetup;

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
	my ( $connectionPath, $newHosts, $removeHosts ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );
	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Cisco::CatOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	$cliProtocol->send_and_wait_for( 'terminal length 0', $promptRegex );
	my $response;
	my $loggingEnabled;
	foreach my $server ( keys %{ $newHosts->{server} } )
	{
		if ( !$loggingEnabled )
		{
			$response .= $cliProtocol->send_and_wait_for( 'set logging server enable', $promptRegex );
			$loggingEnabled = 1;
		}
		$response .= $cliProtocol->send_and_wait_for( 'set logging server ' . $server, $promptRegex );
	}

	foreach my $server ( keys %{ $removeHosts->{server} } )
	{
		$response .= $cliProtocol->send_and_wait_for( 'clear logging server ' . $server, $promptRegex );
	}

	$cliProtocol->send('exit');
	return $response;
}

1;
