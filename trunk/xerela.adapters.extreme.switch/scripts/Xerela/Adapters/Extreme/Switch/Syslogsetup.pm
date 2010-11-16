package Xerela::Adapters::Extreme::Switch::Syslogsetup;

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
	my ( $connectionPath, $newHosts, $removeHosts ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );
	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Extreme::Switch::AutoLogin::execute( $cliProtocol, $connectionPath );
	$cliProtocol->send_and_wait_for( 'disable clipaging', $promptRegex );
	my $configPrompt = '#\s*$';
	my $response;

	foreach my $server (keys %{$newHosts->{server}})
	{
		$response .= $cliProtocol->send_and_wait_for( 'configure syslog add ' .$server. ' local7 debug', $configPrompt );
	}
	
	foreach my $server (keys %{$removeHosts->{server}})
	{
		$response .= $cliProtocol->send_and_wait_for( 'configure syslog delete ' .$server. ' local7 debug', $configPrompt );
	}

	$response .= $cliProtocol->send_and_wait_for('save config primary', 'database|overwrite it\? \(y\/n\)');
	$response .= $cliProtocol->send_and_wait_for('y', $configPrompt, 300);
	$cliProtocol->send('exit');
	return $response;
}

1;
