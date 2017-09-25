package org.zstack.billing.header.balance;

import org.zstack.header.billing.BillingConstant;
import org.zstack.header.billing.Category;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT)
public class APICreateAccountDischargeMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam
    private Category category;

    @APIParam(numberRange = {1,100})
    private int  disCharge;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getDisCharge() {
        return disCharge;
    }

    public void setDisCharge(int disCharge) {
        this.disCharge = disCharge;
    }
}
