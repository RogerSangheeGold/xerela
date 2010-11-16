package Xerela::Adapters::Cisco::SecurityAppliance::Ntpserverchange;

use strict;
use warnings;

use Xerela::Adapters::Cisco::SecurityAppliance::AutoLogin;
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
	my $promptRegex = Xerela::Adapters::Cisco::SecurityAppliance::AutoLogin::execute( $cliProtocol, $connectionPath );
	$cliProtocol->send_and_wait_for( 'terminal length 0', $promptRegex );
	my $configPrompt = '#\s*$';
	my $response = $cliProtocol->send_and_wait_for( 'config term', $configPrompt );
	
	foreach my $server (keys %{$newHosts->{server}})
	{
		$response .= $cliProtocol->send_and_wait_for( 'ntp server '.$server, $configPrompt );
	}
	
	foreach my $server (keys %{$removeHosts->{server}})
	{
		$response .= $cliProtocol->send_and_wait_for( 'no ntp server '.$server, $configPrompt );
	}

	$response .= $cliProtocol->send_and_wait_for( 'end', $promptRegex );
	$response .= $cliProtocol->send_and_wait_for( 'write mem',         $promptRegex );
	$cliProtocol->send('exit');
	if ($response =~ /Invalid input/i)
	{
		$LOGGER->fatal("NTP command not supported on this device type.\n".$response);
	}
	return $response;
}

1;
