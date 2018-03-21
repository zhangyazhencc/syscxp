package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIReply;

/**
 * Create by DCY on 2018/3/20
 */
public class APIGetL3VidAutoReply extends APIReply {

    private Integer vid;

    public Integer getVid() {
        return vid;
    }

    public void setVid(Integer vid) {
        this.vid = vid;
    }
}
