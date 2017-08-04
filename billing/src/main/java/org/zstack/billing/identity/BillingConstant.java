package org.zstack.billing.identity;

import org.zstack.header.rest.SDK;

public interface BillingConstant {

    public static final String SERVICE_ID = "billing";

    public static final String ACTION_CATEGORY = "billing";
    public static final String CATEGORY = "billing";

    @SDK(sdkClassName = "PolicyStatementEffect")
    enum StatementEffect {
        Allow, Deny,
    }

}
