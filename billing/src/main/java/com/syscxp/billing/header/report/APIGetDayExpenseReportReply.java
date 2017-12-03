package com.syscxp.billing.header.report;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetDayExpenseReportReply extends APIReply {

    List<ReportOrderData> inventories;

    public List<ReportOrderData> getInventories() {
        return inventories;
    }

    public void setInventories(List<ReportOrderData> inventories) {
        this.inventories = inventories;
    }
}
