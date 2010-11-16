#  Run our tests, either quietly or with verbose output
all:
	perl -MTest::Harness -e '$$Test::Harness::verbose=0; runtests @ARGV;' *.t ../../../org.xerela.adapters.perl/scripts/tests/*.t
verbose:
	perl -MTest::Harness -e '$$Test::Harness::verbose=1; runtests @ARGV;' *.t ../../../org.xerela.adapters.perl/scripts/tests/*.t
clean:
	find . -name '*~' -exec rm -f \{\} \;
