package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.identity.*;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.identity.SessionInventory;

import javax.persistence.Query;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/8.
 */
public class AccountHand {
    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private IdentiyInterceptor identiyInterceptor;

    public void handle(APIProvingSessionMsg msg) {
        APIProvingSessionReply reply = new APIProvingSessionReply();

        SessionInventory s = identiyInterceptor.getSessions().get(msg.getSessionUuid());
        Timestamp current = dbf.getCurrentSqlTime();
        boolean valid = true;
        if (s != null && current.after(s.getExpiredDate())) {
            valid = false;
            identiyInterceptor.logOutSession(s.getUuid());
        } else {
            SessionVO session = dbf.findByUuid(msg.getSessionUuid(), SessionVO.class);
            if (session == null ) {
                valid = false;
            } else if (session != null && current.after(session.getExpiredDate())) {
                valid = false;
                identiyInterceptor.logOutSession(session.getUuid());
            }
        }

        reply.setValidSession(valid);
        reply.setSessionInventory(s);
        bus.reply(msg, reply);
    }

    public void handle(APIChangeUserPWDMsg msg) {
        APIChangeuserPWDReply reply = new APIChangeuserPWDReply();

        String sql = "update UserVO set password= :newpassword where name = :name and password = :oldpassword";
        Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
        q.setParameter("newpassword", msg.getNewpassword());
        q.setParameter("name", msg.getUsername());
        q.setParameter("oldpassword", msg.getOldpassword());

        int result = q.executeUpdate();
        if(result > 0 ){
            reply.setSuccess(true);
            reply.setMessage("success");
        }else{
            reply.setSuccess(false);
            reply.setMessage("bad old passwords");
        }

        bus.reply(msg, reply);
    }
}
