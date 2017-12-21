package com.syscxp.tunnel.node;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

@GlobalPropertyDefinition
public class NodeImageGlobalProperty {

    @GlobalProperty(name = "upload_url",defaultValue = "/node_images/node_images_upload.php")
    public static String UPLOAD_URL;

    @GlobalProperty(name = "delete_url",defaultValue = "/node_images/node_images_delete.php")
    public static String DELETE_URL;

    @GlobalProperty(name = "delete_ip",defaultValue = "http://192.168.211.99")
    public static String DELETE_IP;

    @GlobalProperty(name = "image_url_prefix",defaultValue = "/node_images/")
    public static String IMAGE_URL_PREFIX;

    @GlobalProperty(name = "upload_key",defaultValue = "sysclouduploadkey")
    public static String UPLOAD_KEY;

    @GlobalProperty(name = "fileNumLimit",defaultValue = "5")
    public static String FILENUMLIMIT;



}
