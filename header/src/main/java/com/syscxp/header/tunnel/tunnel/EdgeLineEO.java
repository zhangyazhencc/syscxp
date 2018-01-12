package com.syscxp.header.tunnel.tunnel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Create by DCY on 2018/1/9
 */
@Entity
@Table
public class EdgeLineEO extends EdgeLineAO{

    @Column
    private String deleted;

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
