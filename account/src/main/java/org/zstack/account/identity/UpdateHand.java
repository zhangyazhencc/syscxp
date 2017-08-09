package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.identity.*;
import org.zstack.account.header.identity.updatemsg.*;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.identity.SessionInventory;

import javax.persistence.Query;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/8.
 */
public class UpdateHand {

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private IdentiyInterceptor identiyInterceptor;


    public void handle(APIChangeUserPWDMsg msg) {
        APIChangeResultReply reply = new APIChangeResultReply();

        if(msg.isIsupdate()){
            String sql = "update UserVO set password= :newpassword where uuid = :uuid and password = :oldpassword";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newpassword", msg.getNewpassword());
            q.setParameter("oldpassword", msg.getOldpassword());

            int result = q.executeUpdate();
            if(result > 0 ){
                reply.setSuccess(true);
                reply.setMessage("success");
                reply.setObject(dbf.findByUuid(msg.getUuid(),UserVO.class));
            }else{
                reply.setSuccess(false);
                reply.setMessage("bad old passwords or username");
            }
        }else{
            reply.setSuccess(true);
            reply.setMessage("Validation code is correct");
        }

        bus.reply(msg, reply);
    }

    public void handle(APIChangeAccountPWDMsg msg) {
        APIChangeResultReply reply = new APIChangeResultReply();


        if(msg.isIsupdate()){
            String sql = "update AccountVO set password= :newpassword where uuid = :uuid and password = :oldpassword";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newpassword", msg.getNewpassword());
            q.setParameter("oldpassword", msg.getOldpassword());

            if(q.executeUpdate() > 0 ){
                reply.setSuccess(true);
                reply.setMessage("success");
                reply.setObject(dbf.findByUuid(msg.getUuid(),AccountVO.class));
            }else{
                reply.setMessage("bad old passwords or accountname");
                reply.setSuccess(false);
            }
        }else{
            reply.setSuccess(true);
            reply.setMessage("Validation code is correct");
        }



        bus.reply(msg, reply);
    }

    public void handle(APIChangeAccountPhoneMsg msg) {
        APIChangeResultReply reply = new APIChangeResultReply();
        if(msg.isIsupdate()){
            String sql = "update AccountVO set phone= :newphone where uuid = :uuid ";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newphone", msg.getPhone());
            int result = q.executeUpdate();

            if(result > 0 ){
                reply.setMessage("success");
                reply.setSuccess(true);
                reply.setObject(dbf.findByUuid(msg.getUuid(),AccountVO.class));
            }else{
                reply.setSuccess(false);
                reply.setMessage("bad uuid or accountname");
            }
        }else{
            reply.setSuccess(true);
            reply.setMessage("Validation code is correct");
        }

        bus.reply(msg, reply);
    }

    public void handle(APIChangeUserPhoneMsg msg) {
        APIChangeResultReply reply = new APIChangeResultReply();

        if(msg.isIsupdate()){
            String sql = "update UserVO set phone = :newphone where uuid = :uuid";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newphone", msg.getPhone());

            int result = q.executeUpdate();
            if(result > 0){
                reply.setSuccess(true);
                reply.setMessage("the phone has been modified");
                reply.setObject(dbf.findByUuid(msg.getUuid(),UserVO.class));
            }else{
                reply.setSuccess(false);
                reply.setMessage("bad uuid or username");
            }
        }else{
            reply.setSuccess(true);
            reply.setMessage("Validation code is correct");
        }

        bus.reply(msg, reply);
    }

    public void handle(APIChangeAccountEmailMsg msg) {
        APIChangeResultReply reply = new APIChangeResultReply();

        if(msg.isIsupdate()){
            String sql = "update AccountVO set email = :newmail where uuid = :uuid";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newmail", msg.getEmail());

            int result = q.executeUpdate();
            if(result > 0){
                reply.setSuccess(true);
                reply.setMessage("success");
                reply.setObject(dbf.findByUuid(msg.getUuid(),AccountVO.class));
            }else{
                reply.setSuccess(false);
                reply.setMessage("bad uuid or AccountName");
            }
        }else{
            reply.setSuccess(true);
            reply.setMessage("Validation code is correct");
        }

        bus.reply(msg, reply);
    }


    public void handle(APIChangeUserEmailMsg msg) {
        APIChangeResultReply reply = new APIChangeResultReply();
        if(msg.isIsupdate()){
            String sql = "update UserVO set email = :newmail where uuid = :uuid";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newmail", msg.getEmail());

            int result = q.executeUpdate();
            if(result > 0 ){
                reply.setSuccess(true);
                reply.setMessage("success");
                reply.setObject(dbf.findByUuid(msg.getUuid(),UserVO.class));
            }else{
                reply.setSuccess(false);
                reply.setMessage("bad uuid or AccountName");
            }
        }else{
            reply.setSuccess(true);
            reply.setMessage("Validation code is correct");
        }

        bus.reply(msg, reply);
    }


    public void handle(APIChangeIndustryMsg msg) {

        APIChangeResultReply reply = new APIChangeResultReply();

        String sql = "update AccountVO set industry = :newindustry where uuid = :uuid ";
        Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
        q.setParameter("uuid", msg.getUuid());
        q.setParameter("newindustry", msg.getNewIndustry());

        if(q.executeUpdate() > 0 ){
            reply.setSuccess(true);
            reply.setMessage("success");
        }else{
            reply.setSuccess(false);
            reply.setMessage("bad uuid or AccountName");
        }
        bus.reply(msg, reply);
    }
}
