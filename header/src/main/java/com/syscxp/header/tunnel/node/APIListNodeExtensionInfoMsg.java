package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

/**
 * Created by wangwg on 2017/10/26
 */
@SuppressCredentialCheck
public class APIListNodeExtensionInfoMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,required = false)
    private String operatorCategory;

    @APIParam(emptyString = false,required = false)
    private String province;

    @APIParam(emptyString = false,required = false)
    private String roomLevel;

    @APIParam(emptyString = false,required = false)
    private String property = "idc_node";


    @APIParam(emptyString = false,required = false)
    private String orderBy;

    @APIParam(emptyString = false,required = false)
    private String orderPolicy;

    @APIParam(emptyString = false,required = false)
    private String pageNo;

    @APIParam(emptyString = false,required = false)
    private String page_size;

    public String getOperatorCategory() {
        return operatorCategory;
    }

    public void setOperatorCategory(String operatorCategory) {
        this.operatorCategory = operatorCategory;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRoomLevel() {
        return roomLevel;
    }

    public void setRoomLevel(String roomLevel) {
        this.roomLevel = roomLevel;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getOrderPolicy() {
        return orderPolicy;
    }

    public void setOrderPolicy(String orderPolicy) {
        this.orderPolicy = orderPolicy;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPage_size() {
        return page_size;
    }

    public void setPage_size(String page_size) {
        this.page_size = page_size;
    }
}
