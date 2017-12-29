package com.syscxp.account.header.account;

import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;
import com.syscxp.header.vo.Index;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/17.
 */

@Entity
@Table
@Inheritance(strategy=InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class AccountApiSecurityVO {
    @Id
    @Column
    private String uuid;
    
    @Column
    @Index
    private String accountUuid;

    @Column
    private String secretId;

    @Column
    private String secretKey;

    @Column
    private String allowIp;

    @Column
    private Timestamp createDate;
    
    @Column
    private Timestamp lastOpDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public String getSecretId() {
        return secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getAllowIp() {
        return allowIp;
    }

    public void setSecretId(String secretId) {
        this.secretId = "account" + secretId;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setAllowIp(String allowIp) {
        this.allowIp = allowIp;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
