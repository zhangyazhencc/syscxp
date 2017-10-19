package com.syscxp.tunnel.header.endpoint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by DCY on 2017-08-23
 */
@Entity
@Table
public class EndpointEO extends EndpointAO{
    @Column
    private String deleted;

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
