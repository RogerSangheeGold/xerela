package Xerela::Adapters::Cisco::ArrowPoint::RestoreStartupConfig;

use strict;
use warnings;

use Xerela::Response;
use Xerela::TransferProtocolFactory;
use Xerela::Logger;
use Xerela::Adapters::Utils qw(escape_filename);

my $LOGGER = Xerela::Logger::get_logger();

sub restore_via_tftp
{
	my ( $connectionPath, $cliProtocol, $startupFile, $promptRegex ) = @_;
	my $tftpFileServer    = $connectionPath->get_file_server_by_name("TFTP");
	my $startupConfigName = escape_filename ( $cliProtocol->get_ip_address() ) . ".startup-config";
	my $startupConfigFile = $tftpFileServer->get_root_dir() . "/$startupConfigName";

	# Write out the file to the TFTP directory
	open( STARTUP, ">$startupConfigFile" );
	print STARTUP $startupFile->get_blob();
	close(STARTUP);

	my $response = $cliProtocol->send_and_wait_for( "copy tftp " . $tftpFileServer->get_ip_address() . " " . $startupConfigName . " startup-config", $promptRegex );
	unlink($startupConfigFile);

	if ( $response !~ /completed/i )
	{
		$LOGGER->fatal_error_code($TFTP_ERROR, $cliProtocol->get_ip_address(), $response);
	}
	else
	{
		return;    
	}
}

1;
