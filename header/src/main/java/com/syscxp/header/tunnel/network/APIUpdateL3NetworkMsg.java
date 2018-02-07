package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.sql.Timestamp;

public class APIUpdateL3NetworkMsg extends APIMessage {

    @APIParam
    private String uuid;
    @APIParam(required = false)
    private String accountUuid;
    @APIParam(required = false)
    private String ownerAccountUuid;
    @APIParam(required = false)
    private String name;
    @APIParam(required = false)
    private String code;
    @APIParam(required = false)
    private Long vid;
    @APIParam(required = false)
    private String type;
    @APIParam(required = false)
    private String status;
    @APIParam(required = false)
    private Long endPointNum;
    @APIParam(required = false)
    private String description;
    @APIParam(required = false)
    private Long duration;
    @APIParam(required = false)
    private String productChargeModel;
    @APIParam(required = false)
    private Long maxModifies;
    @APIParam(required = false)
    private Timestamp expireDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getOwnerAccountUuid() {
        return ownerAccountUuid;
    }

    public void setOwnerAccountUuid(String ownerAccountUuid) {
        this.ownerAccountUuid = ownerAccountUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getVid() {
        return vid;
    }

    public void setVid(Long vid) {
        this.vid = vid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEndPointNum() {
        return endPointNum;
    }

    public void setEndPointNum(Long endPointNum) {
        this.endPointNum = endPointNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(String productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public Long getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Long maxModifies) {
        this.maxModifies = maxModifies;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }
}
