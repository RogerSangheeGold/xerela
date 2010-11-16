#!/usr/bin/perl
use strict;

use Xerela::ConnectionPath;
use Xerela::SnmpSessionFactory;
use Xerela::Typer;
use Xerela::Logger;
use Xerela::SNMP;

# Redirect warnings to the Logger so they don't pollute Tool output
my $LOGGER = Xerela::Logger::get_logger();
local $SIG{__WARN__} = sub {
	my $warning = shift;
	chomp $warning;
	$LOGGER->debug($warning);
};

my $connectionPathXml = shift(@ARGV);

# Parse the backup operation XML document and extract a Xerela::ConnectionPath object from it
my ($connectionPath) = Xerela::Typer::translate_document( $connectionPathXml, 'connectionPath' );
my $snmpSession = Xerela::SnmpSessionFactory->create($connectionPath);

#------------------------------------------------------------
# See http://dev.xerela.org/docs/perldoc/Xerela/SNMP.htm for more examples
#  
#   my $resultsHash = Xerela::SNMP::walk( $session, $oid );  
#------------------------------------------------------------

print "OK\n";