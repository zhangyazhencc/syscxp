package com.syscxp.tunnel.header.node;

import org.bson.types.ObjectId;

public class NodeExtensionInfo {
    ObjectId _id;
    String province;
    String status;
    Object networkInfo;
    Object en;
    Object BCI;
    Object code;
    Object collingAndFireControl;
    String created_at;
    Object cabinetInfo;
    Object electricSystem;
    String node_id;
    Object machineRoomInfo;
    Object property;
    Object roomNOC;
    String updated_at;
    Object images_url;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getNetworkInfo() {
        return networkInfo;
    }

    public void setNetworkInfo(Object networkInfo) {
        this.networkInfo = networkInfo;
    }

    public Object getEn() {
        return en;
    }

    public void setEn(Object en) {
        this.en = en;
    }

    public Object getBCI() {
        return BCI;
    }

    public void setBCI(Object BCI) {
        this.BCI = BCI;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public Object getCollingAndFireControl() {
        return collingAndFireControl;
    }

    public void setCollingAndFireControl(Object collingAndFireControl) {
        this.collingAndFireControl = collingAndFireControl;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Object getCabinetInfo() {
        return cabinetInfo;
    }

    public void setCabinetInfo(Object cabinetInfo) {
        this.cabinetInfo = cabinetInfo;
    }

    public Object getElectricSystem() {
        return electricSystem;
    }

    public void setElectricSystem(Object electricSystem) {
        this.electricSystem = electricSystem;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public Object getMachineRoomInfo() {
        return machineRoomInfo;
    }

    public void setMachineRoomInfo(Object machineRoomInfo) {
        this.machineRoomInfo = machineRoomInfo;
    }

    public Object getProperty() {
        return property;
    }

    public void setProperty(Object property) {
        this.property = property;
    }

    public Object getRoomNOC() {
        return roomNOC;
    }

    public void setRoomNOC(Object roomNOC) {
        this.roomNOC = roomNOC;
    }

    public void setRoomNOC(String roomNOC) {
        this.roomNOC = roomNOC;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Object getImages_url() {
        return images_url;
    }

    public void setImages_url(Object images_url) {
        this.images_url = images_url;
    }
}
