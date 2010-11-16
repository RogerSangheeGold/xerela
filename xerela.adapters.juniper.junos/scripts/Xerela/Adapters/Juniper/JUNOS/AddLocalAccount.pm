package Xerela::Adapters::Juniper::JUNOS::AddLocalAccount;

use strict;
use warnings;

use Xerela::Adapters::Juniper::JUNOS::AutoLogin;
use Xerela::Adapters::Juniper::JUNOS::Disconnect qw(disconnect);
use Xerela::Adapters::Utils qw(mask_to_bits);
use Xerela::CLIProtocolFactory;
use Xerela::Logger;
use Xerela::Typer;

my $LOGGER = Xerela::Logger::get_logger();

sub invoke
{
	my $pkg            = shift;
	my $syslogDocument = shift;

	# Initial connection
	my ( $connectionPath, $username, $password, $privilege ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );

	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Juniper::JUNOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	$cliProtocol->send_and_wait_for( 'set cli screen-length 0', $promptRegex );
	my $configPrompt = '#\s*$';
	my $response = $cliProtocol->send_and_wait_for( 'configure', $configPrompt );

	if ( $username =~ /^\S+$/i && $password =~ /^\S+$/i )
	{
		$_ = 'read-only';
		$_ = 'super-user' if ( $privilege eq 'SU' );
		$response .= $cliProtocol->send_and_wait_for( "set system login user $username class $_ authentication plain-text-password", 'New password:' );
		$response .= $cliProtocol->send_and_wait_for( "$password", 'error:|Retype new password:' );
		if ( $response =~ /Retype new/mi )
		{
			$response .= $cliProtocol->send_and_wait_for( "$password", 'error:|'.$configPrompt );
		}
	}
	if ( $response !~ /error:/mi )
	{
		$response .= $cliProtocol->send_and_wait_for( 'commit', $configPrompt );
	}
	$response .= $cliProtocol->send_and_wait_for( 'exit', $promptRegex );

	disconnect($cliProtocol);

	return $response;
}

1;
