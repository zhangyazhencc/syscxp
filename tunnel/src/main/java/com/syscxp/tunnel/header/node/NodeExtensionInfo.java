package com.syscxp.tunnel.header.node;

import org.bson.types.ObjectId;

import java.util.List;

public class NodeExtensionInfo<T> {
    ObjectId _id;
    String province;
    String status;
    T networkInfo;
    T en;
    T BCI;
    T code;
    T collingAndFireControl;
    String created_at;
    T cabinetInfo;
    T electricSystem;
    String node_id;
    T machineRoomInfo;
    T property;
    T roomNOC;
    String updated_at;
    List<String> images_url;

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

    public T getNetworkInfo() {
        return networkInfo;
    }

    public void setNetworkInfo(T networkInfo) {
        this.networkInfo = networkInfo;
    }

    public T getEn() {
        return en;
    }

    public void setEn(T en) {
        this.en = en;
    }

    public T getBCI() {
        return BCI;
    }

    public void setBCI(T BCI) {
        this.BCI = BCI;
    }

    public T getCode() {
        return code;
    }

    public void setCode(T code) {
        this.code = code;
    }

    public T getCollingAndFireControl() {
        return collingAndFireControl;
    }

    public void setCollingAndFireControl(T collingAndFireControl) {
        this.collingAndFireControl = collingAndFireControl;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public T getCabinetInfo() {
        return cabinetInfo;
    }

    public void setCabinetInfo(T cabinetInfo) {
        this.cabinetInfo = cabinetInfo;
    }

    public T getElectricSystem() {
        return electricSystem;
    }

    public void setElectricSystem(T electricSystem) {
        this.electricSystem = electricSystem;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public T getMachineRoomInfo() {
        return machineRoomInfo;
    }

    public void setMachineRoomInfo(T machineRoomInfo) {
        this.machineRoomInfo = machineRoomInfo;
    }

    public T getProperty() {
        return property;
    }

    public void setProperty(T property) {
        this.property = property;
    }

    public T getRoomNOC() {
        return roomNOC;
    }

    public void setRoomNOC(T roomNOC) {
        this.roomNOC = roomNOC;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public List<String> getImages_url() {
        return images_url;
    }

    public void setImages_url(List<String> images_url) {
        this.images_url = images_url;
    }
}
