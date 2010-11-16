package Xerela::Adapters::Cisco::SecurityAppliance::Telemetry;

use strict;
use warnings;

use Xerela::Logger;
use Xerela::Typer;
use Xerela::CLIProtocolFactory;
use Xerela::Adapters::Cisco::SecurityAppliance::AutoLogin;
use Xerela::Adapters::Cisco::SecurityAppliance::Disconnect qw(disconnect);
use Xerela::Adapters::Cisco::SecurityAppliance::Parsers qw(parse_arp parse_cdp parse_telemetry_interfaces parse_mac_table parse_routing_neighbors);
use Xerela::Adapters::Utils qw(choose_admin_ip get_model_filehandle close_model_filehandle);
use Xerela::Model::XmlPrint;

my $LOGGER = Xerela::Logger::get_logger();

sub invoke
{
	my $pkg               = shift;
	my $connectionPathDoc = shift;
	
	# setup work
	my ($connectionPath, $discoveryParams) = Xerela::Typer::translate_document( $connectionPathDoc, 'connectionPath' );
	my $filehandle = get_model_filehandle( "SecurityAppliance", $connectionPath->get_ip_address() );
	my $cliProtocol = Xerela::CLIProtocolFactory::create($connectionPath);
	my $responses = {};
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, 'telemetry');
	$printer->attributes(1);
	$printer->open_discovery_event();

	# Make a connection to device and get the data necessary to fill out the discovery event document 
	my $promptRegex = Xerela::Adapters::Cisco::SecurityAppliance::AutoLogin::execute( $cliProtocol, $connectionPath );
	$cliProtocol->send_and_wait_for('terminal pager 0', $promptRegex);
	$printer->open_element('neighbors');
	
	# process the ARP table
	$responses->{arp} = $cliProtocol->send_and_wait_for('show arp', $promptRegex);
	parse_arp($responses, $printer);
	delete $responses->{arp};
	
	# process the XDP neighbors
	$responses->{ndp} = $cliProtocol->send_and_wait_for('show ipv6 neighbor', $promptRegex);
	parse_cdp($responses, $printer);
	delete $responses->{ndp};

	# process the MAC address table 
	#$responses->{mac} = $cliProtocol->send_and_wait_for('', $promptRegex);
	#parse_mac_table($responses, $printer);
	#delete $responses->{mac};
	
	# process the routing neighbors
	$responses->{ospf}		= $cliProtocol->send_and_wait_for('show ospf neighbor', $promptRegex);
	$responses->{routes}	= $cliProtocol->send_and_wait_for('show route', $promptRegex);
	#$responses->{eigrp} = $cliProtocol->send_and_wait_for('', $promptRegex);
	#$responses->{bgp} = $cliProtocol->send_and_wait_for('', $promptRegex);
	parse_routing_neighbors($responses, $printer);
	delete $responses->{ospf};
	delete $responses->{routes};
	#delete $responses->{eigrp};
	#delete $responses->{bgp};
	
	$printer->close_element('neighbors');
	
	# process the interfaces and their status
	$responses->{interfaces} = $cliProtocol->send_and_wait_for('show interface', $promptRegex);
	my $interfacesHash = parse_telemetry_interfaces($responses, $printer);
	delete $responses->{interfaces};
	
	my $adminIp    = $connectionPath->get_ip_address();
	if ( $discoveryParams->{calculateAdminIp} eq 'true' )
	{
		$adminIp = choose_admin_ip($connectionPath->get_ip_address(), $interfacesHash);
	}
	$printer->print_element( 'adminIp', $adminIp );
	
	# tear down work
	$printer->close_element('DiscoveryEvent');
	close_model_filehandle($filehandle);
	disconnect($cliProtocol);
}

1;
