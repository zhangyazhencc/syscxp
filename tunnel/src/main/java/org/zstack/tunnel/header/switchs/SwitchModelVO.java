package org.zstack.tunnel.header.switchs;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-06
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class SwitchModelVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String model;

    @Column
    private String subModel;

    @Column
    private Integer mpls;

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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSubModel() {
        return subModel;
    }

    public void setSubModel(String subModel) {
        this.subModel = subModel;
    }

    public Integer getMpls() {
        return mpls;
    }

    public void setMpls(Integer mpls) {
        this.mpls = mpls;
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
