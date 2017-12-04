#!/usr/bin/env python
#coding:utf-8

import os
import sys
import pexpect
import subprocess
import shutil


class VpnCert(object):
    def __init__(self, uuid):
        self.UUID = uuid

        self.BASE_DIR = '/var/lib/syscxp/vpndata/'
        self.EASY_RSA_DIR = os.path.join(self.BASE_DIR, 'easy-rsa')
        self.KEY_DIR = os.path.join(self.EASY_RSA_DIR, 'keys')
        self.VPN_DIR = os.path.join(self.BASE_DIR, 'vpn-{}'.format(self.UUID))
        self.vars_text = '''
export EASY_RSA="`pwd`"
export OPENSSL="openssl"
export PKCS11TOOL="pkcs11-tool"
export GREP="grep"
export KEY_CONFIG=`$EASY_RSA/whichopensslcnf $EASY_RSA`
export KEY_DIR="$EASY_RSA/keys"
echo NOTE: If you run ./clean-all, I will be doing a rm -rf on $KEY_DIR
export PKCS11_MODULE_PATH="dummy"
export PKCS11_PIN="dummy"
export KEY_SIZE=1024
export CA_EXPIRE=3650
export KEY_EXPIRE=3650
export KEY_COUNTRY="CN"
export KEY_PROVINCE="CA"
export KEY_CITY="SZ"
export KEY_ORG="Syscloud"
export KEY_EMAIL="admin@localhost.com"
export KEY_OU="Syscloud"
export KEY_NAME="server"
'''

    def check_env(self):
        if not os.path.isdir(self.EASY_RSA_DIR):
            shutil.copytree("/usr/share/easy-rsa/2.0/", self.EASY_RSA_DIR)
            os.chdir(self.EASY_RSA_DIR)
            shutil.copy("openssl-1.0.0.cnf", "openssl.cnf")
            os.mkdir(self.KEY_DIR)
            with open('vars', 'wb') as vars_f:
                vars_f.write(self.vars_text)

        os.chdir(self.EASY_RSA_DIR)
        build_env = '[^"]source ./vars'

        build_files = ['build-ca', 'build-dh', 'build-key', 'build-key-server']

        for file_name in build_files:
            with open(file_name, 'rb') as bf:
                content = bf.read()
                if build_env in content:
                    continue
                else:
                    os.system("sed -i '2a source ./vars' {}".format(file_name))


    def build_ca(self):
        os.chdir(self.EASY_RSA_DIR)
        cmd_init = "source ./vars;./clean-all "
        subprocess.call(cmd_init, shell=True, stdout=sys.stdout)
        cmd = './build-ca'

        create_cmd = pexpect.spawn(cmd)
        ## debug for create ca
        # create_cmd.logfile = sys.stdout
        create_cmd.expect('Country Name')
        create_cmd.sendline('\n')
        create_cmd.expect('State or Province Name')
        create_cmd.sendline('')
        create_cmd.expect('Locality Name')
        create_cmd.sendline('')
        create_cmd.expect('Organization Name')
        create_cmd.sendline('')
        create_cmd.expect('Organizational Unit Name')
        create_cmd.sendline('')
        create_cmd.expect('Common Name')
        create_cmd.sendline('')
        create_cmd.expect('Name')
        create_cmd.sendline('')
        create_cmd.expect('Email Address')
        create_cmd.sendline('')
        create_cmd.expect(pexpect.EOF)


    def build_key(self, common_name):
        if common_name == 'server':
            cmd = './build-key-server {}'.format(common_name)
        elif common_name == 'client':
            cmd = './build-key {}'.format(common_name)
        else:
            raise ValueError('build_key suppport param "server or client"')

        create_cmd = pexpect.spawn(cmd, timeout=3)
        ## debug for create
        #create_cmd.logfile = sys.stdout
        create_cmd.expect("Country Name")
        create_cmd.sendline("")
        create_cmd.expect("State or Province Name")
        create_cmd.sendline("")
        create_cmd.expect("Locality Name")
        create_cmd.sendline("")
        create_cmd.expect("Organization Name")
        create_cmd.sendline("")
        create_cmd.expect("Organizational Unit Name")
        create_cmd.sendline("")
        create_cmd.expect("Common Name")
        create_cmd.sendline(common_name)
        create_cmd.expect("Name")
        create_cmd.sendline("")
        create_cmd.expect("Email Address")
        create_cmd.sendline("")
        create_cmd.expect("A challenge password")
        create_cmd.sendline("")
        create_cmd.expect("An optional company name")
        create_cmd.sendline("")
        create_cmd.expect("y/n")
        create_cmd.sendline("y")
        create_cmd.expect("y/n")
        create_cmd.sendline("y")
        create_cmd.expect(pexpect.EOF)

    def build_dh(self):
        cmd = './build-dh'
        subprocess.call(cmd, shell=True, stdout=open('/dev/null', 'w'), stderr=open('/dev/null', 'w'))

    def copy_cert(self):
        if not os.path.isdir(self.VPN_DIR):
            os.mkdir(self.VPN_DIR)
        os.system('\cp -rp {} {}'.format(self.KEY_DIR, "{}/keys".format(self.VPN_DIR)))
        os.system('rm -f ' + self.VPN_DIR + '/keys/{0*.pem,index*,serial*}')

    def check_cert(self):
        cmd = "du -b {}/*".format(self.VPN_DIR)
        files_list = os.popen(cmd).read().strip().split("\n")
        res = True
        for file in files_list:
            file_list = file.split()
            if int(file_list[0]) > 0:
                continue
            else:
                res = False
        return res

    def create_vpn_cert(self):
        self.check_env()
        self.build_ca()
        self.build_dh()
        self.build_key('server')
        self.build_key('client')
        self.copy_cert()
        res = self.check_cert()
        return res

if __name__ == '__main__':
    uuid = sys.argv[1]
    vpnCert = VpnCert(uuid)
    vpnCert.create_vpn_cert()