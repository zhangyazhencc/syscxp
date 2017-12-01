package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;

public class ClientInfoReply extends MessageReply {
    public CertInfo certInfo;

    public CertInfo getCertInfo() {
        return certInfo;
    }

    public void setCertInfo(CertInfo certInfo) {
        this.certInfo = certInfo;
    }
}
