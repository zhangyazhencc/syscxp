package org.zstack.account.header.identity;

import org.zstack.header.configuration.PythonClass;
import org.zstack.header.rest.SDK;

@PythonClass
public interface AccountConstant {
    String SERVICE_ID = "identity";

    String INITIAL_SYSTEM_ADMIN_UUID = "36c27e8ff05c4780bf6d2fa65700f22e";

    String INITIAL_SYSTEM_ADMIN_NAME = "admin";

    String SYSTEM_ADMIN_ROLE = ".*";

    int RESOURCE_PERMISSION_READ = 1;
    int RESOURCE_PERMISSION_WRITE = 2;

    String ACTION_CATEGORY = "identity";

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
