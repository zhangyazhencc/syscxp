package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by frank on 7/14/2015.
 */
@AutoQuery(replyClass = APIQueryAccountReply.class, inventoryClass = AccountInventory.class)
@Action(services = {"account"}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIQueryAccountMsg extends APIQueryMessage {

    private boolean queryCustomer;

    public boolean isQueryCustomer() {
        return queryCustomer;
    }

    public void setQueryCustomer(boolean queryCustomer) {
        this.queryCustomer = queryCustomer;
    }
}
