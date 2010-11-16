use Test::More tests => 4;
use Time::HiRes;
use Xerela::Addressing::Subnet;

my $subnet = new Xerela::Addressing::Subnet('192.168.1.50', 24);
ok ($subnet->contains('192.168.1.100'));
ok ($subnet->to_string eq '192.168.1/24');

my $ipv6Subnet = new Xerela::Addressing::Subnet('ffff::5', 64);
ok ($ipv6Subnet->contains('ffff::6'));
ok ($ipv6Subnet->to_string eq 'ffff::/64');
