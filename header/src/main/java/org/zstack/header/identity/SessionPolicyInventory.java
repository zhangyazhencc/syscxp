package org.zstack.header.identity;

import java.util.List;

/**
 * Created by zxhread on 17/8/4.
 */
public class SessionPolicyInventory extends SessionInventory {

    public static class SessionPolicy {
        private List<PolicyStatement> statements;
        private String name;
        private String uuid;

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

    public static SessionPolicyInventory valueOf(SessionInventory session){
        SessionPolicyInventory sp = new SessionPolicyInventory();
        sp.setUuid(session.getUuid());
        sp.setAccountUuid(session.getAccountUuid());
        sp.setUserUuid(session.getUserUuid());
        sp.setType(session.getType());
        sp.setCreateDate(session.getCreateDate());
        sp.setExpiredDate(session.getExpiredDate());
        return sp;
    }
    private List<SessionPolicy> sessionPolicys;

    public List<SessionPolicy> getSessionPolicys() {
        return sessionPolicys;
    }

    public void setStatements(List<SessionPolicy> sessionPolicys) {
        this.sessionPolicys = sessionPolicys;
    }


}
