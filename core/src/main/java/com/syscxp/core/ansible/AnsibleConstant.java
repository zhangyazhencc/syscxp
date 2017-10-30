package com.syscxp.core.ansible;

import com.syscxp.utils.path.PathUtil;

/**
 */
public interface AnsibleConstant {
    public static String SERVICE_ID = "ansible";
    public static String ROOT_DIR = PathUtil.getFolderUndersyscxpHomeFolder("ansible");
    public static String SYSCXPLIB_ROOT = PathUtil.getFolderUndersyscxpHomeFolder("ansible/files/syscxplib/");
    public static String INVENTORY_FILE = PathUtil.getFilePathUndersyscxpHomeFolder("ansible/hosts");
    public static String PYPI_REPO = PathUtil.getFilePathUndersyscxpHomeFolder("/static/pypi/simple");
    public static String CONFIGURATION_FILE = PathUtil.getFilePathUndersyscxpHomeFolder("ansible/ansible.cfg");
    public static String LOG_PATH = PathUtil.getFilePathUndersyscxpHomeFolder("ansible/log");
    public static String IMPORT_PUBLIC_KEY_SCRIPT_PATH = "ansible/import_public_key.sh";
    public static String RSA_PUBLIC_KEY = "ansible/rsaKeys/id_rsa.pub";
    public static String RSA_PRIVATE_KEY = "ansible/rsaKeys/id_rsa";
    public static String PIP_URL = "http://10.88.248.5/simple";
}
