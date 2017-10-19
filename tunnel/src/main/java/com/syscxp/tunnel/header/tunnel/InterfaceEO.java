package com.syscxp.tunnel.header.tunnel;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by DCY on 2017-09-08
 */
@Entity
@Table
public class InterfaceEO extends InterfaceAO {

    @Column
    private String deleted;

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
