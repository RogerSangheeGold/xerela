package Xerela::Adapters::Cisco::CatOS::AddLocalAccount;

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
	my ( $connectionPath, $username, $password, $privilege ) = Xerela::Typer::translate_document( $syslogDocument, 'connectionPath' );

	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $promptRegex = Xerela::Adapters::Cisco::CatOS::AutoLogin::execute( $cliProtocol, $connectionPath );
	my $response	= $cliProtocol->send_and_wait_for( 'set length 0', $promptRegex );

	if ( $username =~ /^\S+$/i && $password =~ /^\S+$/i )
	{
		$_ = '1';
		$_ = '15' if ( $privilege eq 'SU' );
		$response .= $cliProtocol->send_and_wait_for( "set localuser user $username password $password privilege $_", $promptRegex );
	}

	disconnect($cliProtocol);

	return $response;
}

1;
