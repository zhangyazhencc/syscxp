package org.zstack.header.identity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxhread on 17/8/3.
 */
public class UserPolicyInventory {

    private String uuid;
    private String accountUuid;
    private List<PolicyStatement> statements;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public void addStatement(PolicyStatement s) {
        if (statements == null) {
            statements = new ArrayList<PolicyStatement>();
        }

        statements.add(s);
    }

    public void addStatement(List<PolicyStatement> s) {
        if (statements == null) {
            statements = new ArrayList<PolicyStatement>();
        }

        statements.addAll(s);
    }

    public List<PolicyStatement> getStatements() {
        return statements;
    }

    public void setStatements(List<PolicyStatement> statements) {
        this.statements = statements;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
