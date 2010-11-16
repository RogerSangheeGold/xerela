package Xerela::Adapters::F5::ThreeDNS::GetBackUpFiles;

use strict;

use Xerela::TransferProtocolFactory;
use Xerela::ConnectionPath;
use Xerela::ConnectionPath::Protocol;
use Xerela::ConnectionPath::FileServer;
use Xerela::Credentials;
use Xerela::Logger;
use Xerela::Recording;
use Xerela::Recording::Interaction;
use Xerela::Adapters::Utils qw(create_unique_filename);

use Exporter 'import';
our @EXPORT_OK = qw(get_backup_files);

# Get the instance of the Xerela::Logger module
my $LOGGER = Xerela::Logger::get_logger();

# Get the instance of the Xerela::Recording module
my $RECORDING = Xerela::Recording::get_recording();

sub get_backup_files
{

	# Grab our Xerela::CLIProtocol, Xerela::ConnectionPath, and configuration file name arguments
	my $cli_protocol    = shift;
	my $connection_path = shift;
	my $config_filename = shift;

	# Create an undef reference that can eventually hold the configuration contents that are found
	my $response = undef;

	# Check to see if SCP is supported.  If so, a SCP client will be used to connect to the BigIP-based device to
	# retrieve it's active configuration.
	my $scp_protocol = $connection_path->get_protocol_by_name("SCP") if ( defined($connection_path) );

	if ( defined($scp_protocol) )
	{
		$response = _get_backup_files_scp( $connection_path, $config_filename );
	}

	# Otherwise, fall back to CLI protocol only
	else
	{
		$LOGGER->fatal("SCP was not in the provided connection path. Unable to perform backup.");
	}

	# Return the configuration found
	return $response;
}

sub _get_backup_files_scp
{

	# Grab our Xerela::ConnectionPath and configuration file name arguments
	my $connection_path = shift;
	my $config_filename = shift;

	# Grab the Xerela::ConnectionPath::Protocol object representing SCP from the Xerela::ConnectionPath object
	my $scp_protocol = $connection_path->get_protocol_by_name("SCP");

	# Retrieve the startup configuration file from the device
	my $xfer_client = Xerela::TransferProtocolFactory::create( $scp_protocol->get_name() );
	$xfer_client->connect(
		$connection_path->get_ip_address(),
		$scp_protocol->get_port(),
		$connection_path->get_credential_by_name("username"),
		$connection_path->get_credential_by_name("password")
	);

	# Used create_unique_filename to get a randomly generated name for a temp file to use for the SCP process
    my $temp_filename = create_unique_filename();
	$xfer_client->get( $config_filename, $temp_filename );

	# Open up the configuration file and read it into memory
    open(CONFIG, $temp_filename) || $LOGGER->fatal("[$SCP_ERROR]\nCould not open the retrieved configuration file stored in '$temp_filename'");
    my @entire_file = <CONFIG>;
    close(CONFIG);
    my $config_contents = join( "", @entire_file );

    # Record the file transfer of the config
    # Arguments: protocol name, file name, response/contents, whether or not Xerela acted as the file transfer server
    $RECORDING->create_xfer_interaction($scp_protocol->get_name(), $temp_filename, $config_contents, 0);

	# Return the filename.  The caller must delete the config file
	return $temp_filename;
}

1;
