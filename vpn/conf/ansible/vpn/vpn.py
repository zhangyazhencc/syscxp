#!/usr/bin/env python
# encoding=utf-8
import argparse
from datetime import datetime

from syscxplib import *

# create log
logger_dir = "/var/log/syscxp/"
create_log(logger_dir)
banner("Starting to deploy vpn agent")
start_time = datetime.now()
# set default value
file_root = "files/vpn"
pip_url = "https=//pypi.python.org/simple/"
chroot_env = 'false'
init_install = 'false'
syscxp_repo = 'false'
post_url = ""
pkg_vpnagent = ""
virtualenv_version = "12.1.1"
remote_user = "root"
remote_pass = None
remote_port = None

# get parameter from shell
parser = argparse.ArgumentParser(description='Deploy vpn to host')
parser.add_argument('-i', type=str, help="""specify inventory host file
                        default=/etc/ansible/hosts""")
parser.add_argument('--private-key', type=str, help='use this file to authenticate the connection')
parser.add_argument('-e', type=str, help='set additional variables as key=value or YAML/JSON')
args = parser.parse_args()
argument_dict = eval(args.e)

# update the variable from shell arguments
locals().update(argument_dict)
virtenv_path = "%s/virtualenv/vpn/" % syscxp_root
workplace = "%s/vpn" % syscxp_root
vpn_root = "%s/package" % workplace

host_post_info = HostPostInfo()
host_post_info.host_inventory = args.i
host_post_info.host = host
host_post_info.post_url = post_url
host_post_info.private_key = args.private_key
host_post_info.remote_user = remote_user
host_post_info.remote_pass = remote_pass
host_post_info.remote_port = remote_port
if remote_pass is not None and remote_user != 'root':
    host_post_info.become = True

# include syscxplib.py
(distro, distro_version, distro_release) = get_remote_host_info(host_post_info)
syscxplib_args = SyscxpLibArgs()
syscxplib_args.distro = distro
syscxplib_args.distro_release = distro_release
syscxplib_args.distro_version = distro_version
syscxplib_args.syscxp_repo = syscxp_repo
syscxplib_args.yum_server = yum_server
syscxplib_args.syscxp_root = syscxp_root
syscxplib_args.host_post_info = host_post_info
syscxplib_args.pip_url = pip_url
syscxplib_args.trusted_host = trusted_host
syscxplib = SyscxpLib(syscxplib_args)

# name: judge this process is init install or upgrade
if file_dir_exist("path=" + vpn_root, host_post_info):
    init_install = False
else:
    init_install = True
    # name: create root directories
    command = 'mkdir -p %s %s' % (vpn_root, virtenv_path)
    host_post_info.post_label = "ansible.shell.mkdir"
    host_post_info.post_label_param = "%s, %s" % (vpn_root, virtenv_path)
    run_remote_command(command, host_post_info)

run_remote_command("rm -rf %s/*" % vpn_root, host_post_info)


if distro == "RedHat" or distro == "CentOS":
    # handle syscxp_repo
    if syscxp_repo != 'false':
        # name: install related packages on RedHat based OS from user defined repo
        command = ("yum --enablerepo=%s clean metadata && pkg_list=`rpm -q openssh-clients bridge-utils wget sed vconfig net-tools sshpass iputils libffi-devel "
                   "rsync nmap ipset wondershaper easy-rsa  openvpn ntpdate pexpect | grep \"not installed\" | awk '{ print $2 }'` && for pkg in $pkg_list; do yum "
                   "--disablerepo=* --enablerepo=%s install -y $pkg; done;") % (syscxp_repo, syscxp_repo)
        host_post_info.post_label = "ansible.shell.install.pkg"
        host_post_info.post_label_param = "openssh-clients,bridge-utils,wget,sed," \
                                          "vconfig,net-tools,sshpass,iputils,rsync,nmap," \
                                          "ipset,wondershaper,easy-rsa,openvpn,ntpdate,pexpect,libffi-devel"
        run_remote_command(command, host_post_info)
    else:
        # name: install vpn related packages on RedHat based OS from online
        for pkg in ['openssh-clients', 'bridge-utils', 'wget', 'sed', 'vconfig',
                    'net-tools', 'sshpass', 'rsync', 'nmap', 'ipset', 'libffi-devel']:
            yum_install_package(pkg, host_post_info)

    # handle distro version specific task
    if distro_version < 7:
        # name: disable NetworkManager in RHEL6 and Centos6
        network_manager_installed = yum_check_package("NetworkManager", host_post_info)
        if network_manager_installed is True:
            service_status("NetworkManager", "state=stopped enabled=no", host_post_info)

    else:
        # name: disable firewalld in RHEL7 and Centos7
        command = "(which firewalld && service firewalld stop && chkconfig firewalld off) || true"
        host_post_info.post_label = "ansible.shell.disable.service"
        host_post_info.post_label_param = "firewalld"
        run_remote_command(command, host_post_info)
        # name: disable NetworkManager in RHEL7 and Centos7
        service_status("NetworkManager", "state=stopped enabled=no", host_post_info, ignore_error=True)

    # name: disable selinux on RedHat based OS
    set_selinux("state=disabled", host_post_info)
    run_remote_command("setenforce 0 || true", host_post_info)

else:
    error("unsupported OS!")


# name: copy syscxplib
copy_arg = CopyArg()
copy_arg.src = "files/syscxplib/%s" % pkg_syscxplib
copy_arg.dest = "%s/%s" % (vpn_root, pkg_syscxplib)
copy_syscxplib = copy(copy_arg, host_post_info)

# name: copy vpnagent
copy_arg = CopyArg()
copy_arg.src = "%s/%s" % (file_root, pkg_vpnagent)
copy_arg.dest = "%s/%s" % (vpn_root, pkg_vpnagent)
copy_vpnagent = copy(copy_arg, host_post_info)

# only for os using init.d not systemd
# name: copy vpn service file
copy_arg = CopyArg()
copy_arg.src = "files/vpn/syscxp-vpnagent"
copy_arg.dest = "/etc/init.d/"
copy_arg.args = "mode=755"
copy(copy_arg, host_post_info)

# name: install virtualenv
virtual_env_status = check_and_install_virtual_env(virtualenv_version, trusted_host, pip_url, host_post_info)
if virtual_env_status is False:
    command = "rm -rf %s && rm -rf %s" % (virtenv_path, vpn_root)
    host_post_info.post_label = "ansible.shell.remove.file"
    host_post_info.post_label_param = "%s, %s" % (virtenv_path, vpn_root)
    run_remote_command(command, host_post_info)
    sys.exit(1)
# name: make sure virtualenv has been setup
command = "[ -f %s/bin/python ] || virtualenv --system-site-packages %s " % (virtenv_path, virtenv_path)
host_post_info.post_label = "ansible.shell.check.virtualenv"
host_post_info.post_label_param = None
run_remote_command(command, host_post_info)
command = "%s/bin/pip install -U pip " % virtenv_path
run_remote_command(command, host_post_info)

# name: install syscxplib
if copy_syscxplib != "changed:False":
    agent_install_arg = AgentInstallArg(trusted_host, pip_url, virtenv_path, init_install)
    agent_install_arg.agent_name = "syscxplib"
    agent_install_arg.agent_root = vpn_root
    agent_install_arg.pkg_name = pkg_syscxplib
    agent_install_arg.virtualenv_site_packages = "yes"
    agent_install(agent_install_arg, host_post_info)

# name: install vpn agent
if copy_vpnagent != "changed:False":
    agent_install_arg = AgentInstallArg(trusted_host, pip_url, virtenv_path, init_install)
    agent_install_arg.agent_name = "vpn agent"
    agent_install_arg.agent_root = vpn_root
    agent_install_arg.pkg_name = pkg_vpnagent
    agent_install_arg.virtualenv_site_packages = "yes"
    agent_install(agent_install_arg, host_post_info)

# name: add audit rules for signals
command = "systemctl enable auditd; systemctl start auditd; " \
          "auditctl -D -k syscxp_log_kill; " \
          "auditctl -a always,exit -F arch=b64 -F a1=9 -S kill -k syscxp_log_kill; " \
          "auditctl -a always,exit -F arch=b64 -F a1=15 -S kill -k syscxp_log_kill"
host_post_info.post_label = "ansible.shell.audit.signal"
host_post_info.post_label_param = None
run_remote_command(command, host_post_info)

# handlers
if chroot_env == 'false':
    # name: restart vpnagent, do not use ansible systemctl due to vpnagent can start by itself, so systemctl will not know
    # the vpn agent status when we want to restart it to use the latest vpn agent code
    if distro == "RedHat" or distro == "CentOS":
        command = "service syscxp-vpnagent stop && service syscxp-vpnagent start && chkconfig syscxp-vpnagent on"
    else:
        error("unsupported OS!")

    host_post_info.post_label = "ansible.shell.restart.service"
    host_post_info.post_label_param = "syscxp-vpnagent"
    run_remote_command(command, host_post_info)


host_post_info.start_time = start_time
handle_ansible_info("SUCC: Deploy vpn agent successful", host_post_info, "INFO")

sys.exit(0)
