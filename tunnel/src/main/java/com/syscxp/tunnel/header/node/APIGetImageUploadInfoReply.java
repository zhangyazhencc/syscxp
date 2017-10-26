package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Created by wangwg on 2017/10/09
 */
public class APIGetImageUploadInfoReply extends APIReply {

    String upload_url ;
    String delete_url ;
    String image_url_prefix ;

    String timestamp;
    String md5;
    String fileNumLimit;
    String nodeId;

    Object images_url;


    public String getUpload_url() {
        return upload_url;
    }

    public void setUpload_url(String upload_url) {
        this.upload_url = upload_url;
    }

    public String getDelete_url() {
        return delete_url;
    }

    public void setDelete_url(String delete_url) {
        this.delete_url = delete_url;
    }

    public String getImage_url_prefix() {
        return image_url_prefix;
    }

    public void setImage_url_prefix(String image_url_prefix) {
        this.image_url_prefix = image_url_prefix;
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

    public String getFileNumLimit() {
        return fileNumLimit;
    }

    public void setFileNumLimit(String fileNumLimit) {
        this.fileNumLimit = fileNumLimit;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Object getImages_url() {
        return images_url;
    }

    public void setImages_url(Object images_url) {
        this.images_url = images_url;
    }
}


