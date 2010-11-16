package Xerela::Adapters::CheckPoint::SecurePlatform::Disconnect;

use strict;
use Xerela::CLIProtocol;
use Xerela::Response;
use Xerela::Logger;

use Exporter 'import';
our @EXPORT_OK = qw(disconnect);

# Get the instance of the Xerela::Logger module
my $LOGGER = Xerela::Logger::get_logger();

sub disconnect
{
	# Grab the CLI protocol
	my $cliProtocol = shift;
	
	# Close this session and exit
	$cliProtocol->send("exit");
	$cliProtocol->send("exit");
	
	# Finally, disconnect from our CLIProtocol
	$cliProtocol->disconnect();
}

1;