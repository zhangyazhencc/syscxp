package com.syscxp.tunnel.node;

import com.syscxp.header.configuration.PythonClass;

/**
 * Created by wangwg on 2017-10-25
 */
@PythonClass
public interface ImageUploadInfoConstant {

    String upload_url = "/node_images/node_images_upload.php";
    String delete_url = "http://192.168.211.99/node_images/node_images_delete.php";
    String image_url_prefix = "/node_images/";
    String upload_key = "sysclouduploadkey";
    String fileNumLimit = "5";

}
