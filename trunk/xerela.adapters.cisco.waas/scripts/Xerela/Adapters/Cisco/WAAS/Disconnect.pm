package Xerela::Adapters::Cisco::WAAS::Disconnect;

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
	my $cliProtocol = shift;
	$cliProtocol->send_as_bytes("03");
	_send_exit($cliProtocol);
	$cliProtocol->disconnect();
}

sub _send_exit
{
	my $cliProtocol = shift;
	my @responses = ();

	push(@responses, Xerela::Response->new("#|>", \&_send_exit));
	push(@responses, Xerela::Response->new(".*"));
	
	$cliProtocol->send("exit");

	my $response = $cliProtocol->wait_for_responses(\@responses);
	my $nextInteraction = undef;

	if ($response)
	{
		$nextInteraction = $response->get_next_interaction();
	}
	else
	{
		$LOGGER->fatal("Invalid response from device encountered!");
	}
	
	if ($nextInteraction)
	{
		return &$nextInteraction($cliProtocol);
	}
}

1;
