package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIReply;

public class APIGenerateDownloadL3UrlReply extends APIReply {
    String downloadUrl;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
