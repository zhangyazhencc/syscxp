package com.syscxp.tunnel.header.switchs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by DCY on 2017-08-24
 */
@Entity
@Table
public class SwitchEO extends SwitchAO{
    @Column
    private Integer deleted;

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
