package org.zstack.billing.manage;

public enum BillingErrors {

    INSUFFICIENT_BALANCE(2000),
    NOT_PERMIT_UPDATE(2001);

    private String code;

    private BillingErrors(int id) {
        code = String.format("ID.%s", id);
    }

    @Override
    public String toString() {
        return code;
    }
}
