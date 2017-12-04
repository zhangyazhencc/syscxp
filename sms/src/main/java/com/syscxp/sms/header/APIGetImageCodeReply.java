package com.syscxp.sms.header;

import com.syscxp.header.message.APIReply;

/**
 * Created by wangwg on 2017/12/01.
 */
public class APIGetImageCodeReply extends APIReply {

    String ImageUuid;

    String ImageCode;

    public String getImageUuid() {
        return ImageUuid;
    }

    public void setImageUuid(String imageUuid) {
        ImageUuid = imageUuid;
    }

    public String getImageCode() {
        return ImageCode;
    }

    public void setImageCode(String imageCode) {
        ImageCode = imageCode;
    }
}
