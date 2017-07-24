package org.zstack.account.header.identity;

import org.zstack.header.configuration.PythonClass;
import org.zstack.header.rest.SDK;

@PythonClass
public interface AccountConstant {
    String SERVICE_ID = "identity";

    String INITIAL_SYSTEM_ADMIN_NAME = "admin";

    String SYSTEM_ADMIN_ROLE = ".*";

    int RESOURCE_PERMISSION_READ = 1;
    int RESOURCE_PERMISSION_WRITE = 2;

    String ACTION_CATEGORY = "identity";
    String READ_PERMISSION_POLICY = "default-read-permission";

    String QUOTA_GLOBAL_CONFIG_CATETORY = "quota";

    enum RoleDecision {
        EXPLICIT_DENY,
        DEFAULT_DENY,
        DENY,
        ALLOW,
    }

    @SDK(sdkClassName = "PolicyStatementEffect")
    enum StatementEffect {
        Allow,
        Deny,
    }

    enum Principal {
        Account,
        User,
        Role,
        Group
    }
}
