package com.syscxp.core.agent;

import com.syscxp.core.ansible.AnsibleConstant;
import com.syscxp.utils.path.PathUtil;

/**
 * Created by frank on 12/5/2015.
 */
public class AgentConstant {
    public static final String SERVICE_ID = "agent";

    public static final String ANSIBLE_PLAYBOOK_NAME = "monitor.yaml";
    public static final String ANSIBLE_MODULE_PATH = "ansible/monitor";

    public static final String SRC_ANSIBLE_ROOT = PathUtil.join(AnsibleConstant.ROOT_DIR, "files");
    public static final String DST_ANSIBLE_ROOT = "/var/lib/syscxp/agent/package";

    public static final int AGENT_PORT = 10001;

    public static final String CONFIG_COMMAND_URL = "commandUrl";

}
