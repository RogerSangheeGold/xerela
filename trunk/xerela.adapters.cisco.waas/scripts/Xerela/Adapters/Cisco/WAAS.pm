package Xerela::Adapters::Cisco::WAAS;

use strict;
use warnings;

use Xerela::CLIProtocolFactory;
use Xerela::CLIProtocol;
use Xerela::Adapters::Cisco::WAAS::AutoLogin;
use Xerela::Adapters::Cisco::WAAS::GetRunningConfig qw(get_running_config);
use Xerela::Adapters::Cisco::WAAS::GetStartupConfig qw(get_startup_config);
use Xerela::Adapters::Cisco::WAAS::Parsers
  qw(parse_vtp parse_static_routes parse_vlans parse_routing parse_access_ports parse_interfaces create_config parse_local_accounts parse_chassis parse_filters parse_snmp parse_system parse_stp);
use Xerela::Adapters::Cisco::WAAS::Disconnect qw(disconnect);
use Xerela::Adapters::Cisco::WAAS::RestoreStartupConfig;
use Xerela::Adapters::Utils qw(get_model_filehandle close_model_filehandle);
use Xerela::Adapters::GenericAdapter;
use Xerela::ConnectionPath;
use Xerela::Typer;
use Xerela::Model::XmlPrint;
use Xerela::Logger;
use Data::Dumper;

# Get the instance of the Xerela::Logger module
my $LOGGER = Xerela::Logger::get_logger();

# Specifies that this adapter is a subclass of Xerela::Adapters::BaseAdapter
use Xerela::Adapters::BaseAdapter;
our @ISA = qw(Xerela::Adapters::BaseAdapter);

sub backup
{
	my $package_name = shift;

	# Retrieve the operation XML document that contains all of the IP, protocol, credential, and file server information
	# that is needed to successfully backup a device.
	my $backup_doc = shift;
	my $responses  = {};

	# Parse the backup operation XML document and extract a Xerela::ConnectionPath object from it
	my ($connection_path) = Xerela::Typer::translate_document( $backup_doc, 'connectionPath' );

	# Connect to the device and capture the Xerela::CLIProtocol that is created as a result of the connection.
	# Also be sure to capture the device prompt that is returned as a result of a successful connection.
	my ( $cli_protocol, $enable_prompt_regex ) = _connect($connection_path);

	# Get rid of the more prompt
	my $termLen = $cli_protocol->send_and_wait_for( "terminal length 0", $enable_prompt_regex );
	if ( $termLen =~ /Invalid input/i )
	{

		# set the --more-- prompt if the term length 0 didn't go through
		$cli_protocol->set_more_prompt( '--More--\s*$', '20' );
	}

	# Grab an output filehandle for the model
	my $filehandle = get_model_filehandle( "WAAS", $connection_path->get_ip_address() );

	# initialize the model printer
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, 'cisco', "http://www.xerela.org/model/cisco/1.0 cisco.xsd" );
	$printer->open_model();

	# Gather inputs for the model

	$responses->{running_config} = get_running_config( $cli_protocol, $connection_path );
	$responses->{version} = $cli_protocol->send_and_wait_for( "show hardware", $enable_prompt_regex );
	$responses->{snmp} = $cli_protocol->send_and_wait_for( "show snmp stats", $enable_prompt_regex );
	parse_system( $responses, $printer );

	$responses->{show_inventory} = $cli_protocol->send_and_wait_for( "show inventory", $enable_prompt_regex );

        parse_chassis( $responses, $printer );
	delete $responses->{version};

	$responses->{startup_config} = get_startup_config( $cli_protocol, $connection_path );
	create_config( $responses, $printer );
	delete $responses->{startup_config};

        parse_interfaces( $responses, $printer );
	parse_local_accounts( $responses, $printer );
	parse_snmp( $responses, $printer );
	parse_static_routes( $responses, $printer );
	delete $responses->{running_config};
	$printer->close_model;

	# Close the model output file handle
	close_model_filehandle($filehandle);

	# Disconnect from the specified device
	disconnect($cli_protocol);
}

sub commands
{
	my $package_name = shift;
	my $command_doc  = shift;
	my ( $connection_path, $commands ) = Xerela::Typer::translate_document( $command_doc, 'connectionPath' );
	my ( $cli_protocol, $enable_prompt_regex ) = _connect($connection_path);
	my $termLen = $cli_protocol->send_and_wait_for( "terminal length 0", $enable_prompt_regex );
	my $result = Xerela::Adapters::GenericAdapter::execute_cli_commands( 'WAAS', $cli_protocol, $commands, $enable_prompt_regex.'|(#|\$|>)\s*$' );
	disconnect($cli_protocol);
	return $result;
}

sub restore
{
	my $package_name = shift;
	my $command_doc  = shift;
	my ( $connection_path, $restoreFile ) = Xerela::Typer::translate_document( $command_doc, 'connectionPath' );

	# Check to see if TFTP is supported
	my $tftp_protocol = $connection_path->get_protocol_by_name("TFTP") if ( defined($connection_path) );

	if ( $restoreFile->get_path() =~ /startup-config/i )
	{
		if ( defined($tftp_protocol) )
		{
			my ( $cli_protocol, $enable_prompt_regex ) = _connect($connection_path);
			Xerela::Adapters::Cisco::WAAS::RestoreStartupConfig::restore_via_tftp( $connection_path, $cli_protocol, $restoreFile );
			disconnect($cli_protocol);
		}

		else
		{
			$LOGGER->fatal("Unable to restore startup config.  TFTP protocol is not available.");
		}
	}
	else
	{
		$LOGGER->fatal( "Unable to promote this type of configuration '" . $restoreFile->get_path() . "'." );
	}
}

sub _connect
{

	my $connection_path = shift;
	my $cli_protocol = Xerela::CLIProtocolFactory::create($connection_path);
	my $enable_prompt_regex = Xerela::Adapters::Cisco::WAAS::AutoLogin::execute( $cli_protocol, $connection_path );

	$cli_protocol->set_prompt_by_name( "enablePrompt", $enable_prompt_regex );

	return ( $cli_protocol, $enable_prompt_regex );
}


1;

__END__

=head1 NAME

Xerela::Adapters::Cisco::WAAS - Adapter for performing various operations against Cisco WAAS/WAE devices.

=head1 SYNOPSIS

    use Xerela::Adapters::Cisco::WAAS;
	Xerela::Adapters::Cisco::WAAS::backup( $backup_document );

=head1 DESCRIPTION

This module represents an adapter that can be used to perform various operations against against Cisco WAAS devices.

Generally you would run this module through the Xerela C<invoke.pl> script.

=head1 PUBLIC SUB-ROUTINES

=over 12

=item C<backup($backup_document)>

Performs the backup of the device that is described in the specified XML document.  This XML document contains
a "connectionPath" element that contains the IP/hostname, protocol, credential, and file server information that
may be needed to connect to, authenticate with, and back up the device in question.  This XML will be parsed into
a C<Xerela::ConnectionPath> object to help easily access this vital information.

The act of backing up a device means that information about a device will be retreived, as well as the configuration
data/files for that device.  All of this collected information will be used to populate the Xerela Element Document,
which is an open and extensible format for describing a network device.

The return value for this method will be a string containing the successfully populated Xerela Element Document.

=back

=head1 PRIVATE SUB-ROUTINES

=over 12

=item C<_connect($connection_path)>

Creates the initial CLI connection to the device.  A list is returned containing two elements: the first is a
C<Xerela::CLIProtocol> object that is a CLI client that can communitcate with the device, and the second is a
regular expression that can be used to match the primary prompt of the device.  This is useful when sending
commands to and receiving responses from the device through the C<Xerela::CLIProtocol> object and being able to
know when a command has generated all the output and returned back to the primary prompt.

=item C<_get_file_systems($show_file_systems_response)>

Parses the response of the "show file systems" and returns an array representing the file system of the Cisco Security
Appliance device.

=back

=head1 LICENSE

The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is Ziptie Client Framework.

The Initial Developer of the Original Code is AlterPoint.
Portions created by AlterPoint are Copyright (C) 2006,
AlterPoint, Inc. All Rights Reserved.

=head1 AUTHOR

Contributor(s): -Z
Date: February 5, 2008

=cut
