package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;

public class APIGenerateDownloadUrlReply extends APIReply {
    String downloadUrl;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
