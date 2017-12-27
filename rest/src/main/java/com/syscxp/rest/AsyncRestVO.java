package com.syscxp.rest;

import com.syscxp.header.vo.BaseResource;
import com.syscxp.header.vo.Index;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 15:06
 * Author: wj
 */
@Entity
@Table
@BaseResource
public class AsyncRestVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String requestData;

    @Column
    @Enumerated(EnumType.STRING)
    @Index
    private AsyncRestState state;

    @Column
    private String result;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public AsyncRestState getState() {
        return state;
    }

    public void setState(AsyncRestState state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}

