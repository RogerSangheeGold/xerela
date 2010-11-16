#  Run our tests, either quietly or with verbose output
all:
	perl -I ../../org.xerela.adapters.perl/scripts invoke.pl Xerela::Adapters::Cisco::IOS backup -i samples/c2610.xml > out.xml
	xmltidy out.xml
