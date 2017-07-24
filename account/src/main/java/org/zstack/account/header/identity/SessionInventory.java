package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountType;

import java.io.Serializable;
import java.sql.Timestamp;

public class SessionInventory extends org.zstack.header.identity.SessionInventory {
    private String uuid;
    private String accountUuid;
    private String userUuid;
    private Timestamp expiredDate;
    private Timestamp createDate;
    private String supportAccountUuid;      //support or admin account uuid
    private AccountType type;

    public static org.zstack.header.identity.SessionInventory valueOf(SessionVO vo, AccountVO avo) {
        SessionInventory inv = new SessionInventory();
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setCreateDate(vo.getCreateDate());
        inv.setExpiredDate(vo.getExpiredDate());
        inv.setUserUuid(vo.getUserUuid());
        inv.setUuid(vo.getUuid());
        if (avo != null){
            if (avo.getType() != AccountType.Normal ){
                inv.setType(avo.getType());
            }
        }
        return inv;
    }

    public boolean isAdminAccountSession() {
        return this.type == AccountType.SystemAdmin && (this.accountUuid.equals(this.getSupportAccountUuid()) || this.getSupportAccountUuid()==null);
    }

    public boolean isSupportAccount() {
        return (this.type == AccountType.Support || this.type == AccountType.SystemAdmin);
    }

    public boolean isSupportAccountSession() {
        return isSupportAccount() && ( this.getSupportAccountUuid()==null );
    }

    public boolean isAccountSession() {
        return accountUuid.equals(userUuid);
    }

    public void resetAccountUuidAndUserUuid(AccountVO vo){
        if (this.supportAccountUuid == null){
            this.setSupportAccountUuid(this.accountUuid);
        }
        this.setAccountUuid(vo.getUuid());
        this.setUserUuid(vo.getUuid());
    }

    public boolean isUserSession() {
        return !accountUuid.equals(userUuid);
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

    public String getSupportAccountUuid() {
        return supportAccountUuid;
    }

    public void setSupportAccountUuid(String supportAccountUuid) {
        this.supportAccountUuid = supportAccountUuid;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }
}
