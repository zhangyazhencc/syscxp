package org.zstack.tunnel.header.vo;


import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy= InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class DeployTaskVO {

    @Id
    @Column
    private String uuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = TunnelPointVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String tunnelPointUuid;

    @Column
    private String state;

    @Column
    private String type;

    @Column
    private String description;

    @Column
    private String comment;

    @Column
    private String finshBy;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelPointUuid() {
        return tunnelPointUuid;
    }

    public void setTunnelPointUuid(String tunnelPointUuid) {
        this.tunnelPointUuid = tunnelPointUuid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFinshBy() {
        return finshBy;
    }

    public void setFinshBy(String finshBy) {
        this.finshBy = finshBy;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

}
