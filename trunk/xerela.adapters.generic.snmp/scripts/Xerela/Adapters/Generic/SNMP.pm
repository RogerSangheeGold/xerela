package Xerela::Adapters::Generic::SNMP;

use strict;
use Xerela::CLIProtocolFactory;
use Xerela::CLIProtocol;
use Xerela::ConnectionPath;
use Xerela::Credentials;
use Xerela::Adapters::BaseAdapter;
use Xerela::Adapters::Generic::SNMP::Parsers
  qw(parse_chassis parse_system create_config);
use Xerela::Adapters::Utils qw(get_model_filehandle close_model_filehandle);
use Xerela::Adapters::GenericAdapter;
use Xerela::Model::XmlPrint;
use Xerela::Logger;
use Xerela::SnmpSessionFactory;
use Data::Dumper;

# Grab a reference to the Xerela::Logger
my $LOGGER = Xerela::Logger::get_logger();

# Specifies that this adapter is a subclass of Xerela::Adapters::BaseAdapter
our @ISA = qw(Xerela::Adapters::BaseAdapter);

sub backup
{
	my $package_name = shift;
	my $backup_doc   = shift;    # how to backup this device

	# Translate the backup operation XML document into Xerela::ConnectionPath
	my ($connection_path) = Xerela::Typer::translate_document( $backup_doc, 'connectionPath' );

	# Grab the Xerela::Credentials object from the connection path
	my $credentials = $connection_path->get_credentials();

	# Grab an output filehandle for the model.  This usually points to STDOUT
	my $filehandle = get_model_filehandle( 'Generic', $connection_path->get_ip_address() );

	# initialize the model printer
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, 'common' );
	$printer->open_model();
	my $responses = {};

	# Create a Net::SNMP session
	my $snmp_session = Xerela::SnmpSessionFactory->create( $connection_path, $credentials );

	$responses->{snmp} = Xerela::Adapters::GenericAdapter::get_snmp($snmp_session);
	
	
	# Gather the system uptime
	$snmp_session->translate(['-timeticks' => 0,]);
	my $sysUpTimeOid = '.1.3.6.1.2.1.1.3.0';
	my $getResult = Xerela::SNMP::get($snmp_session, [$sysUpTimeOid]);
	$responses->{uptime} = $getResult->{$sysUpTimeOid};
	
	parse_system($responses, $printer);
	parse_chassis($responses, $printer);
	create_config($responses, $printer);

	$printer->print_element( 'interfaces', Xerela::Adapters::GenericAdapter::get_interfaces($snmp_session) );
	$printer->print_element( 'snmp',       $responses->{snmp} );
	$printer->close_model();

	# Make sure to close the model file handle
	close_model_filehandle($filehandle);

}

1;
