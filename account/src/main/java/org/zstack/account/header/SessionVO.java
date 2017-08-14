package org.zstack.account.header;

import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.vo.ForeignKey;
import org.zstack.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class SessionVO {
    @Id
    @Column
    private String uuid;
    
    @Column
    @ForeignKey(parentEntityClass = AccountVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String accountUuid;
    
    @Column
    private String userUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountType type;
    
    @Column
    private Timestamp expiredDate;
    
    @Column
    private Timestamp createDate;

    public SessionInventory toSessionInventory() {
        SessionInventory inv = new SessionInventory();
        inv.setAccountUuid(this.getAccountUuid());
        inv.setCreateDate(this.getCreateDate());
        inv.setExpiredDate(this.getExpiredDate());
        inv.setUserUuid(this.getUserUuid());
        inv.setUuid(this.getUuid());
        inv.setType(this.getType());
        return inv;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Timestamp getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }
}
