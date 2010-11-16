package Xerela::Adapters::Vyatta::OFR;

use strict;

use Xerela::Adapters::Vyatta::OFR::AutoLogin;
use Xerela::Adapters::Vyatta::OFR::Disconnect qw(disconnect);
use Xerela::Adapters::Vyatta::OFR::Parsers
  qw(parse_interfaces create_config parse_local_accounts parse_chassis parse_filters parse_routing parse_snmp parse_system);
use Xerela::Adapters::Utils qw(get_model_filehandle close_model_filehandle);
use Xerela::Adapters::GenericAdapter;
use Xerela::CLIProtocolFactory;
use Xerela::CLIProtocol;
use Xerela::ConnectionPath;
use Xerela::ConnectionPath::Protocol;
use Xerela::Model::XmlPrint;
use Xerela::Typer;
use Xerela::Logger;

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
	my ( $cli_protocol, $device_prompt_regex ) = _connect($connection_path);

	# Store the found device prompt regular expression as "prompt" on the Xerela::CLIProtocol object.
	$cli_protocol->set_prompt_by_name( "prompt", $device_prompt_regex );

	# being creating the model output
	#
	# Grab an output filehandle for the model
	my $filehandle = get_model_filehandle( "OFR", $connection_path->get_ip_address() );
	my $printer = Xerela::Model::XmlPrint->new( $filehandle, "common" );
	$printer->open_model();

	# Gather inputs for the core model
	$responses->{showVer}  = _send( $cli_protocol, "show version",   $device_prompt_regex );
	$responses->{showName} = _send( $cli_protocol, "show host name", $device_prompt_regex );
	parse_system( $responses, $printer );
	delete( $responses->{showName} );

	# Gather chassis details
	$responses->{showMem} = _send( $cli_protocol, "show system memory", $device_prompt_regex );
	$responses->{showOs}  = _send( $cli_protocol, "show host os",       $device_prompt_regex );
	parse_chassis( $responses, $printer );
	delete( $responses->{showMem} );
	delete( $responses->{showOs} );

	# Calculate the configure prompt for this Vyatta device
	my $configure_prompt_regex = $device_prompt_regex;
	$configure_prompt_regex =~ s/\\>\\/\\#\\/;

	# Add the text configuration to the model output
	$cli_protocol->send_and_wait_for( "configure", $configure_prompt_regex );
	$responses->{config} = _send( $cli_protocol, "show -all | no-more", $configure_prompt_regex );

	# clean up the config (strip the prompt and opening command)
	$responses->{config} =~ s/^.+?show -all.+$//m;
	$responses->{config} =~ s/^\[edit\].+$//sim;

	create_config( $responses, $printer );
	_send( $cli_protocol, "exit", $device_prompt_regex );

	parse_filters( $responses, $printer );
	delete( $responses->{showVer} );

	$responses->{interfaces} = _send( $cli_protocol, "show interfaces | no-more", $device_prompt_regex );
	parse_interfaces( $responses, $printer );
	delete( $responses->{interfaces} );

	# Disconnect from the CLI of the specified device
	disconnect($cli_protocol);

	# populate the rest of the Xerela common model
	parse_local_accounts( $responses, $printer );
	parse_routing( $responses, $printer );
	parse_snmp( $responses, $printer );
	delete( $responses->{config} );

	# write the closing tag of the model
	$printer->close_model();

	# Close the model output file handle
	close_model_filehandle($filehandle);
}

sub commands
{
	my $package_name = shift;
	my $command_doc  = shift;
	my ( $connection_path, $commands ) = Xerela::Typer::translate_document( $command_doc, 'connectionPath' );
	my ( $cli_protocol, $enable_prompt_regex ) = _connect($connection_path);
	my $result = Xerela::Adapters::GenericAdapter::execute_cli_commands( 'OFR', $cli_protocol, $commands, $enable_prompt_regex . '|(#|\$|>)\s*$' );
	disconnect($cli_protocol);
	return $result;
}

sub _connect
{

	# Grab our arguments
	my $connection_path = shift;

	# Create a new CLI protocol object
	my $cli_protocol = Xerela::CLIProtocolFactory::create($connection_path);

	# Make a connection to and successfully authenticate with the OFR device
	my $device_prompt_regex = Xerela::Adapters::Vyatta::OFR::AutoLogin::execute( $cli_protocol, $connection_path );

	# Return the created Xerela::CLIProtocol object and the device prompt encountered after successfully connecting to a device.
	return ( $cli_protocol, $device_prompt_regex );
}

sub _send
{

	# Vyatta always echos the system prompt initially, and it can screw up our waitfors
	# causing us to bail out too early.  This helper method builds a more strict regex so
	# we never match the first prompt.
	my ( $cliProtocol, $command, $prompt ) = @_;
	return $cliProtocol->send_and_wait_for( $command, '(?s)' . quotemeta($command) . '.*' . $prompt );
}

1;

__END__

=head1 NAME

Xerela::Adapters::Vyatta::OFR - Adapter for performing various operations against Vyatta Open Flexible Router (OFR) devices.

=head1 SYNOPSIS

    use Xerela::Adapters::Vyatta::OFR;
	Xerela::Adapters::Vyatta::OFR::backup( $backup_document );

=head1 DESCRIPTION

This module represents an adapter that can be used to perform various operations against atta Open Flexible Router (OFR) devices.

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

Contributor(s): rkruse, Dylan White (dylamite@xerela.org)
Date: August 10, 2007

=cut
