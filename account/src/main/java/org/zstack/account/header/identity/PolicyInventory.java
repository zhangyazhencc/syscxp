package org.zstack.account.header.identity;

import org.zstack.header.identity.StatementEffect;
import org.zstack.header.identity.PolicyStatement;
import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.query.ExpandedQueryAlias;
import org.zstack.header.query.ExpandedQueryAliases;
import org.zstack.header.search.Inventory;
import org.zstack.utils.gson.JSONObjectUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = PolicyVO.class)

@ExpandedQueries({
        @ExpandedQuery(expandedField = "account", inventoryClass = AccountInventory.class,
                foreignKey = "accountUuid", expandedInventoryKey = "uuid"),
        @ExpandedQuery(expandedField = "userRef", inventoryClass = UserPolicyRefInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "policyUuid", hidden = true)
})

public class PolicyInventory {

    public static PolicyInventory valueOf(PolicyVO vo) {
        PolicyInventory inv = new PolicyInventory();
        inv.setName(vo.getName());
        inv.setUuid(vo.getUuid());
        inv.setStatements(JSONObjectUtil.toCollection(vo.getPolicyStatement(), ArrayList.class, PolicyStatement.class));
        inv.setAccountUuid(vo.getAccountUuid());
        return inv;
    }

    public static List<PolicyInventory> valueOf(Collection<PolicyVO> vos) {
        List<PolicyInventory> invs = new ArrayList<PolicyInventory>();
        for (PolicyVO vo : vos) {
            invs.add(valueOf(vo));
        }
        return invs;
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
