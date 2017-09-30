package org.zstack.account.header.ticket;


import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/9/26.
 */

@Entity
@Table
public class TicketTypeVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    private String category;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String typeValue) {
        this.code = typeValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String typeName) {
        this.name = typeName;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
