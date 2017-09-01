package org.zstack.billing.header.bill;

import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.sql.Timestamp;

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
