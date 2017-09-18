package org.zstack.header.account;

import org.zstack.header.message.APIReply;

public class APIExistsAccountByUuidReply  extends APIReply {

    private boolean exist;

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}
