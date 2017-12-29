package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

/**
 * Created by wangwg on 2017/12/29
 */
public class APIReconcileNodeExtensionInfoMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String node_id;
    @APIParam(required = false,emptyString = false)
    private String status;
    @APIParam(required = false)
    private String province;
    @APIParam(required = false)
    private List<String> property;
    @APIParam(required = false)
    private String roomName;
    @APIParam(required = false)
    private String roomAddress;
    @APIParam(required = false)
    private String consignee;
    @APIParam(required = false)
    private String consigneePhone;

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<String> getProperty() {
        return property;
    }

    public void setProperty(List<String> property) {
        this.property = property;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomAddress() {
        return roomAddress;
    }

    public void setRoomAddress(String roomAddress) {
        this.roomAddress = roomAddress;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }
}
