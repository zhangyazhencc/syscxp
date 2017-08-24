package org.zstack.tunnel.header.endpoint;

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
    private Integer deleted;

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
