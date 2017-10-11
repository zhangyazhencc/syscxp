package com.syscxp.tunnel.header.aliEdgeRouter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class AliEdgeRouterEO extends AliEdgeRouterAO{
    @Column
    private Integer deleted;

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
