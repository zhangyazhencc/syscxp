package org.zstack.tunnel.header.node;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by DCY on 2017-08-22
 */
@Entity
@Table
public class NodeEO extends NodeAO{
    @Column
    private Integer deleted;

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
