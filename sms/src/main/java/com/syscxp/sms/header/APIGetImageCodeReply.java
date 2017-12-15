package com.syscxp.sms.header;

import com.syscxp.header.message.APIReply;

/**
 * Created by wangwg on 2017/12/01.
 */
public class APIGetImageCodeReply extends APIReply {

    String imageUuid;

    String imageCode;

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

}
