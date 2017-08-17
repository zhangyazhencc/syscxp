package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.PolicyStatement;
import org.zstack.header.identity.StatementEffect;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

import java.util.List;

import static org.zstack.utils.CollectionDSL.list;

@Action(category = AccountConstant.ACTION_CATEGORY, accountOnly = true)
public class APICreatePolicyMsg extends  APIMessage implements AccountMessage{
    @APIParam(maxLength = 255)
    private String name;

    @APIParam(maxLength = 255)
    public String accountUuid;

    @APIParam(maxLength = 2048, required = false)
    private String description;

    @APIParam(nonempty = true)
    private List<PolicyStatement> statements;

    public List<PolicyStatement> getStatements() {
        return statements;
    }

    public void setStatements(List<PolicyStatement> statements) {
        this.statements = statements;
    }

    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
 
    public static APICreatePolicyMsg __example__() {
        APICreatePolicyMsg msg = new APICreatePolicyMsg();

        msg.setName("USER-RESET-PASSWORD");

        PolicyStatement s = new PolicyStatement();
        s.setEffect(StatementEffect.Allow);
        s.addAction(String.format("%s:%s", AccountConstant.ACTION_CATEGORY, APIUpdateUserMsg.class.getSimpleName()));
        msg.setStatements(list(s));

        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                if (evt.isSuccess()) {
                    ntfy("Creating").resource(((APICreatePolicyEvent)evt).getInventory().getUuid(), PolicyVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
                }
            }
        };
    }
}
