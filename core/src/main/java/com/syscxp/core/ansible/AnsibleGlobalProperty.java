package com.syscxp.core.ansible;

import com.syscxp.core.GlobalPropertyDefinition;
import com.syscxp.core.GlobalProperty;

/**
 */
@GlobalPropertyDefinition
public class AnsibleGlobalProperty {
    @GlobalProperty(name = "Ansible.executable", defaultValue = "python")
    public static String EXECUTABLE;
    @GlobalProperty(name = "Ansible.syscxplibPackageName", defaultValue = "syscxplib-2.2.0.tar.gz")
    public static String SYSCXPLIB_PACKAGE_NAME;
    @GlobalProperty(name = "Ansible.syscxpRoot", defaultValue = "/var/lib/syscxp")
    public static String SYSCXP_ROOT;
    @GlobalProperty(name = "Ansible.var.syscxp_repo", defaultValue = "false")
    public static String SYSCXP_REPO;
    @GlobalProperty(name = "Ansible.fullDeploy", defaultValue = "false")
    public static boolean FULL_DEPLOY;
    @GlobalProperty(name = "Ansible.keepHostsFileInMemory", defaultValue = "true")
    public static boolean KEEP_HOSTS_FILE_IN_MEMORY;
    @GlobalProperty(name = "Ansible.debugMode", defaultValue = "false")
    public static boolean DEBUG_MODE;
    @GlobalProperty(name = "Ansible.debugMode2", defaultValue = "false")
    public static boolean DEBUG_MODE2;
}
