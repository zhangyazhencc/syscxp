package org.zstack.billing.header.identity;

import org.zstack.billing.identity.BillingConstant;
import org.zstack.billing.identity.BillingGlobalConfig;
import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.utils.gson.JSONObjectUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class PolicyInventory {
    public static class PolicyStatement {
        private BillingConstant.StatementEffect effect;
        private List<String> actions;

        public BillingConstant.StatementEffect getEffect() {
            return effect;
        }

        public void setEffect(BillingConstant.StatementEffect effect) {
            this.effect = effect;
        }

        public List<String> getActions() {
            return actions;
        }

        public void setActions(List<String> actions) {
            this.actions = actions;
        }

        public void addAction(String a) {
            if (actions == null) {
                actions = new ArrayList<String>();
            }
            actions.add(a);
        }
    }


    private List<PolicyStatement> statements;
    private String name;
    private String uuid;
    private String accountUuid;
    private String description;
    private Timestamp createDate;
    private Timestamp lastOpDate;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
