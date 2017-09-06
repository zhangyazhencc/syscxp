package org.zstack.billing.manage;

import org.zstack.header.message.APIParam;
import org.zstack.header.query.APIQueryMessage;

public class APIQueryExpendMessage extends APIQueryMessage {

    @APIParam
    private boolean selfSelect;

    public boolean isSelfSelect() {
        return selfSelect;
    }

    public void setSelfSelect(boolean selfSelect) {
        this.selfSelect = selfSelect;
    }

}
