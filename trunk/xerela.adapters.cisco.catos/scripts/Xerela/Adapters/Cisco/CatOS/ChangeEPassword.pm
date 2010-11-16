package Xerela::Adapters::Cisco::CatOS::ChangeEPassword;

use strict;
use warnings;

use Xerela::Adapters::Cisco::CatOS::AutoLogin;
use Xerela::Adapters::Cisco::CatOS::Disconnect qw(disconnect);
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
	my ( $connectionPath, $newPassword ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );

	my $credentials = $connectionPath->get_credentials();
	my $oldPassword = $credentials->{enablePassword};
	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Cisco::CatOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $response	= $cliProtocol->send_and_wait_for( 'set length 0', $promptRegex );

	if ( $newPassword =~ /^\S+$/i )
	{
		$response .= $cliProtocol->send_and_wait_for( "set enablepass", 'old\s+password:' );
		$response .= $cliProtocol->send_and_wait_for( "$oldPassword", 'new\s+password:|incorrect' );
		if ( $response =~ /new\s+password/mi )
		{
			$response .= $cliProtocol->send_and_wait_for( "$newPassword", 'new\s+password:' );
			$response .= $cliProtocol->send_and_wait_for( "$newPassword", $promptRegex );
		}
	}

	disconnect($cliProtocol);

	return $response;
}

1;
