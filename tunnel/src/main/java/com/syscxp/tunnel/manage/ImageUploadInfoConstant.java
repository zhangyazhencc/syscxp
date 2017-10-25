package com.syscxp.tunnel.manage;

import com.syscxp.header.configuration.PythonClass;

/**
 * Created by wangwg on 2017-10-25
 */
@PythonClass
public interface ImageUploadInfoConstant {

    String upload_url = "http://172.16.123.129/node_images/node_images_upload.php";
    String delete_url = "http://172.16.123.129/node_images/node_images_delete.php";
    String image_url_prefix = "http://172.16.123.129/node_images/";
    String upload_key = "sysclouduploadkey";
    String fileNumLimit = "5";

}
