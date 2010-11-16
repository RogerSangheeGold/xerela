#!/usr/bin/perl
use strict;
use Getopt::Long;

use Xerela::Logger;
use Xerela::Typer;
use Xerela::Adapters::Invoker;

# Redirect warnings to the Logger so they don't pollute Tool output
my $LOGGER = Xerela::Logger::get_logger();
local $SIG{__WARN__} = sub {
	my $warning = shift;
	chomp $warning;
	$LOGGER->debug($warning);
};

my ( $connectionPathXml, $adapterId, $newLogHosts, $removeLogHosts );
GetOptions(
	"connectionPath=s" => \$connectionPathXml,
	"adapterId=s"      => \$adapterId,
	"newLogHosts:s"    => \$newLogHosts,
	"removeLogHosts:s" => \$removeLogHosts,
);

my ($connectionPath) = Xerela::Typer::translate_document( $connectionPathXml, 'connectionPath' );
my $device           = $connectionPath->get_ip_address();
my $addServerXml     = '<addServers>';
foreach my $server ( split( /[,\s;]/, $newLogHosts ) )
{
	$addServerXml .= '<server name="' . $server . '"/>' if ($server);
}
$addServerXml .= '</addServers>';

my $removeServerXml = '<removeServers>';
foreach my $server ( split( /[,\s;]/, $removeLogHosts ) )
{
	$removeServerXml .= '<server name="' . $server . '"/>' if ($server);
}
$removeServerXml .= '</removeServers>';
my $operation = 'syslogsetup';
$connectionPathXml =~ s/(<\/\w+>)$/$addServerXml$removeServerXml$1/;
my $response;

eval { $response = Xerela::Adapters::Invoker::invoke( $adapterId, $operation, $connectionPathXml ); };
if ($@)
{
	if ( $@ =~ /Can't locate.+\.pm|Can't locate object method/i )
	{
		print "WARN,$device\n";
		print "\n";
		print "The \"$operation\" operation is not yet implemented for the $adapterId adapter\n";
		print "\n";
		print "Visit http://www.xerela.org/zde for information on how to extend the $adapterId adapter.";
	}
	else
	{
		print "ERROR,$device\n";
		print "\n";
		print "$@";
	}
}
else
{
	print "OK,$device\n\n$response";
}
