package org.zstack.billing.identity;

import org.zstack.header.rest.SDK;

public interface BillingConstant {

    public static final String SERVICE_ID = "billing";

    public static final String ACTION_CATEGORY = "billing";
    public static final String CATEGORY = "billing";

    public static final String ACCOUNT_SERVER = "http://localhost:8080/syscxp/api";

    public static final String GET_SESSION_VALID_MSG = "org.zstack.account.header.identity.APIValidSessionMsg";
    public static final String GET_USER_POLICY_MSG = "org.zstack.account.header.identity.APIGetUserPolicyMsg";

    @SDK(sdkClassName = "PolicyStatementEffect")
    enum StatementEffect {
        Allow, Deny,
    }

}
