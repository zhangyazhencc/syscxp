package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

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

    @APIParam(emptyString = false,validValues = {"0","1"})
    private Integer completed;

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

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }
}
