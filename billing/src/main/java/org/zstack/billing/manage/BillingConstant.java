package org.zstack.billing.manage;

import org.zstack.header.rest.SDK;

public interface BillingConstant {

    public static final String SERVICE_ID = "billing";

    public static final String ACTION_CATEGORY = "billing";
    public static final String ACTION_CATEGORY_ACCOUNT = "account";
    public static final String ACTION_CATEGORY_SLA = "sla";
    public static final String ACTION_CATEGORY_ORDER = "order";
    public static final String ACTION_CATEGORY_RECHARGE = "recharge";
    public static final String ACTION_CATEGORY_RENEW = "renew";
    public static final String ACTION_CATEGORY_RECEIPT = "receipt";

    public static final String CATEGORY = "billing";



    @SDK(sdkClassName = "PolicyStatementEffect")
    enum StatementEffect {
        Allow, Deny,
    }

}
