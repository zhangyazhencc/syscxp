package org.zstack.billing.header.identity.bill;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.sql.Timestamp;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"read", "bill"})
public class APIGetMonetaryGroupByProductTypeMsg extends APISyncCallMessage{

    @APIParam(nonempty = false)
    private Timestamp dateStart;

    @APIParam(nonempty = false)
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
