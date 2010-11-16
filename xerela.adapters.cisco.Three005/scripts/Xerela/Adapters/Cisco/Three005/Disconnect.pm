package Xerela::Adapters::Cisco::Three005::Disconnect;
use strict;
use Xerela::CLIProtocol;
use Xerela::Response;
use Xerela::Logger;

use Exporter 'import';
our @EXPORT_OK =
  qw(disconnect);
  
# Get the instance of the Xerela::Logger module
my $LOGGER = Xerela::Logger::get_logger();

# Attempts to disconnect from an IOS-based device that a CLIProtocol object is currently connected to.
# If all is well, the device will be successfully disconnected from and no error will occur.
#
# Input:	$cliProtocol -	A valid Xerela::CLIProtocol object that is already connected to a device.
#			$debug -		Whether or not to display debug information.
#
# Returns:	Nothing.
sub disconnect
{
	my $cliProtocol = shift;
	my $responseRegex = '.*' ;
	$cliProtocol->send_and_wait_for('h', $responseRegex);
	$cliProtocol->send_and_wait_for('6',$responseRegex);
	$cliProtocol->send_as_bytes('20');
	$cliProtocol->disconnect();	
}

1;

__END__

=head1 AUTHOR

Contributor(s): Ashuin Sharma(asharma@isthmusit.com), dwhite (dylamite@xerela.org)
Date: Sep 29, 2007

=cut