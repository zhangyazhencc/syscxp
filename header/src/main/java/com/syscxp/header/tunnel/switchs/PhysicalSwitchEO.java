package com.syscxp.header.tunnel.switchs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by DCY on 2017-09-06
 */
@Entity
@Table
public class PhysicalSwitchEO extends PhysicalSwitchAO {
    @Column
    private String deleted;

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
