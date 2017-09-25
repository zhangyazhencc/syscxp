package org.zstack.account.header.account;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by frank on 7/14/2015.
 */
@AutoQuery(replyClass = APIQueryAccountReply.class, inventoryClass = AccountInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIQueryAccountMsg extends APIQueryMessage {

    private boolean queryCustomer;

    public boolean isQueryCustomer() {
        return queryCustomer;
    }

    public void setQueryCustomer(boolean queryCustomer) {
        this.queryCustomer = queryCustomer;
    }
}
