package com.syscxp.tunnel.node;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

@GlobalPropertyDefinition
public class NodeImageGlobalProperty {

    @GlobalProperty(name = "node_image_upload_url",defaultValue = "http://192.168.211.99/node_images/node_images_upload.php")
    public static String UPLOAD_URL;

    @GlobalProperty(name = "node_image_delete_url",defaultValue = "http://192.168.211.99/node_images/node_images_delete.php")
    public static String DELETE_URL;

    @GlobalProperty(name = "node_image_image_url_prefix",defaultValue = "http://192.168.211.99/node_images/")
    public static String IMAGE_URL_PREFIX;

    @GlobalProperty(name = "node_image_upload_key",defaultValue = "sysclouduploadkey")
    public static String UPLOAD_KEY;

    @GlobalProperty(name = "node_image_fileNumLimit",defaultValue = "5")
    public static String FILENUMLIMIT;



}
