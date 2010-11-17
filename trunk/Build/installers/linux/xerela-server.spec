Summary: Xerela EMS Server
Name: xerela-server
Version: %version
Release: %release
Source0: %archive
License: MPL
Group: System/Servers
BuildArch: i386
BuildRoot: /var/tmp/%{name}-buildroot
Requires: %require

%description
Xerela is a framework for Network Inventory Management.
%prep
%setup -q -n xerela-server
%build
############################################################
# INSTALL
############################################################
%install
rm -rf $RPM_BUILD_ROOT

mkdir -p $RPM_BUILD_ROOT/usr/share/xerela-server

cp -v -R . $RPM_BUILD_ROOT/usr/share/xerela-server/

############################################################
# PRE-INSTALL
############################################################
%pre
useradd -r --shell /bin/false xerela

############################################################
# POST-INSTALL
############################################################
%post
ln -s /usr/share/xerela-server/ztserver /etc/rc.d/init.d/xerela-server
chmod +x /usr/share/xerela-server/ztserver
chmod +x /usr/share/xerela-server/ztwrapper/linux/ztwrapper

cd /etc/rc.d/rc0.d
ln -s ../init.d/xerela-server K05xerela-server

cd /etc/rc.d/rc5.d
ln -s ../init.d/xerela-server S95xerela-server

perl /usr/share/xerela-server/perlcheck.pl

service xerela-server start

############################################################
# PRE-UNINSTALL
############################################################
%preun
service xerela-server stop

############################################################
# UNINSTALL
############################################################
%postun
userdel xerela

rm /etc/rc.d/rc0.d/K05xerela-server
rm /etc/rc.d/rc5.d/S95xerela-server
rm /etc/rc.d/init.d/xerela-server

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,xerela,xerela)

/usr/share/xerela-server
