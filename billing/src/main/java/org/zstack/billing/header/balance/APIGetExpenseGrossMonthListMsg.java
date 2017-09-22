package org.zstack.billing.header.balance;

import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.SuppressUserCredentialCheck;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.sql.Timestamp;

@SuppressUserCredentialCheck
@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIGetExpenseGrossMonthListMsg extends APISyncCallMessage {

    @APIParam
    private Timestamp dateStart;

    @APIParam
    private Timestamp dateEnd;

    public Timestamp getDateStart() {
        return dateStart;
    }

    public void setDateStart(Timestamp dateStart) {
        this.dateStart = dateStart;
    }

    public Timestamp getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Timestamp dateEnd) {
        this.dateEnd = dateEnd;
    }

}
