package com.syscxp.header.billing;

public interface BillingConstant {

    public static final String SERVICE_ID_BALANCE = "balance";
    public static final String SERVICE_ID_BILL = "bill";
    public static final String SERVICE_ID_ORDER = "order";
    public static final String SERVICE_ID_RECEIPT = "receipt";
    public static final String SERVICE_ID_RENEW = "renew";
    public static final String SERVICE_ID_SLA = "sla";
    public static final String SERVICE_ID_REPORT = "report";

    public static final String ACTION_SERVICE = "billing";
    
    public static final String ACTION_CATEGORY_BILLING = "billing";
    public static final String ACTION_CATEGORY_PRICE = "price";
    public static final String ACTION_CATEGORY_SLA = "sla";
    public static final String ACTION_CATEGORY_ORDER = "order";
    public static final String ACTION_CATEGORY_ORDER_PRICE = "orderPrice";  //订单，续费价格
    public static final String ACTION_CATEGORY_RENEW = "renew";
    public static final String ACTION_CATEGORY_RECEIPT = "receipt";
    public static final String ACTION_CATEGORY_REPORT = "report";

    public static final String ACTION_CATEGORY_DISCOUNT = "discount";
    public static final String ACTION_CATEGORY_CREDIT = "credit";
    public static final String ACTION_CATEGORY_PRESENT = "present";
    public static final String ACTION_CATEGORY_RECHARGE = "recharge";


}
