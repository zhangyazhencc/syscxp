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
    private AccountType type;
    @APINoSession
    private Timestamp expiredDate;
    @APINoSession
    private Timestamp createDate;

    @APINoSession
    private String supportAccountUuid;      //proxy or admin account uuid
    @APINoSession
    private String supportUserUuid;         //proxy or admin user uuid
    @APINoSession
    private ProxySupportStrategy supportStrategy; //proxy support strategy

    @APINoSession
    private List<PolicyStatement> policyStatements;

    public List<PolicyStatement> getPolicyStatements() {
        return policyStatements;
    }

    public void setPolicyStatements(List<PolicyStatement> policyStatements) {
        this.policyStatements = policyStatements;
    }

    public boolean isAccountSession() {
        return accountUuid.equals(userUuid)
                && (supportAccountUuid == null || supportAccountUuid.equals(supportUserUuid));
    }

    public boolean isUserSession() {
        return !accountUuid.equals(userUuid)
                || ( supportAccountUuid != null && ! supportAccountUuid.equals(supportUserUuid) );
    }

    public boolean isAdminAccountSession() {
        return type == AccountType.SystemAdmin
                && (accountUuid.equals(userUuid)
                && (supportAccountUuid == null || supportAccountUuid.equals(supportUserUuid)));
    }

    public boolean isAdminUserSession() {
        return type == AccountType.SystemAdmin
                && ( !accountUuid.equals(userUuid)
                || ( supportAccountUuid != null && ! supportAccountUuid.equals(supportUserUuid) )
        );
    }

    public boolean isProxyAccountSession(){
        return this.type==AccountType.Proxy
                && (accountUuid.equals(userUuid)
                && (supportAccountUuid == null || supportAccountUuid.equals(supportUserUuid)));
    }

    public boolean isProxyUserSession(){
        return this.type==AccountType.Proxy
                && ( !accountUuid.equals(userUuid)
                || ( supportAccountUuid != null && ! supportAccountUuid.equals(supportUserUuid)));
    }

    public void resetAccountUuidAndUserUuid(String toAccountUuid){
        if (toAccountUuid == null) {
            if (this.getSupportAccountUuid() != null){
                this.setAccountUuid(this.getSupportAccountUuid());
                this.setUserUuid(this.getSupportUserUuid());
            }
            this.setSupportAccountUuid(null);
            this.setSupportUserUuid(null);
            this.setSupportStrategy(null);
        }else{
            if (this.supportAccountUuid == null){
                this.setSupportAccountUuid(this.accountUuid);
                this.setSupportUserUuid(this.userUuid);
            }
            this.setAccountUuid(toAccountUuid);
            this.setUserUuid(toAccountUuid);
        }
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

    public String getSupportAccountUuid() {
        return supportAccountUuid;
    }

    public void setSupportAccountUuid(String supportAccountUuid) {
        this.supportAccountUuid = supportAccountUuid;
    }

    public String getSupportUserUuid() {
        return supportUserUuid;
    }

    public void setSupportUserUuid(String supportUserUuid) {
        this.supportUserUuid = supportUserUuid;
    }

    public ProxySupportStrategy getSupportStrategy() {
        return supportStrategy;
    }

    public void setSupportStrategy(ProxySupportStrategy supportStrategy) {
        this.supportStrategy = supportStrategy;
    }
}
