package com.syscxp.header.identity;

import com.syscxp.header.rest.APINoSession;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class SessionInventory implements Serializable {

    private String uuid;

    @APINoSession
    private String accountUuid;
    @APINoSession
    private String userUuid;
    @APINoSession
    private Timestamp expiredDate;
    @APINoSession
    private Timestamp createDate;
    @APINoSession
    private AccountType type;
    @APINoSession
    private List<PolicyStatement> policyStatements;

    public List<PolicyStatement> getPolicyStatements() {
        return policyStatements;
    }

    public void setPolicyStatements(List<PolicyStatement> policyStatements) {
        this.policyStatements = policyStatements;
    }

    public boolean isAccountSession() {
        return accountUuid.equals(userUuid);
    }

    public boolean isUserSession() {
        return !accountUuid.equals(userUuid);
    }

    public boolean isAdminSession() {
        return this.type == AccountType.SystemAdmin;
    }

    public boolean isAdminAccountSession() {
        return isAdminSession() && isAccountSession();
    }

    public boolean isAdminUserSession() {
        return isAdminSession() && isUserSession();
    }

    public boolean isProxySession(){return this.type==AccountType.Proxy;}

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
