package org.zstack.account.header.account;

import org.zstack.header.configuration.PythonClass;

@PythonClass
public interface AccountConstant {
    String SERVICE_ID = "identity";

    String INITIAL_SYSTEM_ADMIN_UUID = "36c27e8ff05c4780bf6d2fa65700f22e";

    String INITIAL_SYSTEM_ADMIN_NAME = "admin";
    String INITIAL_SYSTEM_ADMIN_PHONE = "13046619165";
    String INITIAL_SYSTEM_ADMIN_EMAIL = "zhaoxh@syscloud.cn";

    // 'password' SHA512 hex coding
    String INITIAL_SYSTEM_ADMIN_PASSWORD = "b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86";

    String SYSTEM_ADMIN_ROLE = ".*";

    int RESOURCE_PERMISSION_READ = 1;
    int RESOURCE_PERMISSION_WRITE = 2;

    public static final String ACTION_CATEGORY_ACCOUNT = "account";
    public static final String ACTION_CATEGORY_USER = "user";

    enum RoleDecision {
        EXPLICIT_DENY,
        DEFAULT_DENY,
        DENY,
        ALLOW,
    }

    enum Principal {
        Account,
        User,
        Role,
        Group
    }
}
