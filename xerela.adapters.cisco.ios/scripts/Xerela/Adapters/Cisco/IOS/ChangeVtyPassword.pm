package Xerela::Adapters::Cisco::IOS::ChangeVtyPassword;

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
	my ( $connectionPath, $newPassword ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );

	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Cisco::IOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	$cliProtocol->send_and_wait_for( 'terminal length 0', $promptRegex );
	my $configPrompt	= '#\s*$';
	my $rconfig			= $cliProtocol->send_and_wait_for( 'show running-config', $promptRegex );
	my $response    	= $cliProtocol->send_and_wait_for( 'config term', $configPrompt );

	if ( $newPassword =~ /^\S+$/i )
	{
		# grab vty config
		my ( $vty_config ) = $rconfig =~ /^(line\s+vty\s+\d+\s+\d+.+?)!/mis;
		if ( $vty_config )
		{
			# reprint all the vty config but password line
			while ( $vty_config =~ /^(.+)$/mg )
			{
				$_ = $1;
				if ( $_ !~ /^password/i )
				{
					$response .= $cliProtocol->send_and_wait_for( "$_", $configPrompt );
				}
				else
				{
					$response .= $cliProtocol->send_and_wait_for( "password $newPassword", $configPrompt );
				}
			}
		}
		else
		{
			$response .= $cliProtocol->send_and_wait_for( "line vty 0 4", $configPrompt );
			$response .= $cliProtocol->send_and_wait_for( "password $newPassword", $configPrompt );
		}
	}

	$response .= $cliProtocol->send_and_wait_for( 'end',       $promptRegex );
	$response .= $cliProtocol->send_and_wait_for( 'write mem', $promptRegex );

	disconnect($cliProtocol);

	return $response;
}

1;
