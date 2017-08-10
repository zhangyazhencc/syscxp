package org.zstack.billing.identity;

public enum BillingErrors {

    INSUFFICIENT_BALANCE(2000);

    private String code;

    private BillingErrors(int id) {
        code = String.format("ID.%s", id);
    }

    @Override
    public String toString() {
        return code;
    }
}
