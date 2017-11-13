package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import javax.print.DocFlavor;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: 速度测试结果更新.
 */
public class APIUpdateSpeedRecordsMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = SpeedRecordsVO.class)
    private String uuid;

    @APIParam(emptyString = false,maxLength = 11)
    private Integer avgSpeed;

    @APIParam(emptyString = false,maxLength = 11)
    private Integer maxSpeed;

    @APIParam(emptyString = false,maxLength = 11)
    private Integer minSpeed;

    @APIParam(emptyString = false,validValues = {"COMPLETED","FAILURE"})
    private SpeedRecordStatus status;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Integer avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Integer getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(Integer minSpeed) {
        this.minSpeed = minSpeed;
    }

    public SpeedRecordStatus getStatus() {
        return status;
    }

    public void setStatus(SpeedRecordStatus status) {
        this.status = status;
    }
}
