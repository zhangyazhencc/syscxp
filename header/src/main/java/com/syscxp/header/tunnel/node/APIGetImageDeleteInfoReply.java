package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Created by wangwg on 2017/10/09
 */
public class APIGetImageDeleteInfoReply extends APIReply {

    String delete_url ;
    String timestamp;
    String md5;
    String nodeId;
    String images_url;

    public String getDelete_url() {
        return delete_url;
    }

    public void setDelete_url(String delete_url) {
        this.delete_url = delete_url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getImages_url() {
        return images_url;
    }

    public void setImages_url(String images_url) {
        this.images_url = images_url;
    }
}


