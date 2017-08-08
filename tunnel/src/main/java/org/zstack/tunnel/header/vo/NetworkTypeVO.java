package org.zstack.tunnel.header.vo;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy= InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class NetworkTypeVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String name;

    @Column
    private String code;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
