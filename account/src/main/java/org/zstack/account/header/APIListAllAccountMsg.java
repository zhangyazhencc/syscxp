package org.zstack.account.header;

import org.zstack.header.message.APIMessage;

/**
 * Created by Administrator on 2017/8/11.
 */
public class APIListAllAccountMsg extends APIMessage{

    private int offset;
    private int length;

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
