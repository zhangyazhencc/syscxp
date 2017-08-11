package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.identity.VO.AccountVO;
import org.zstack.account.header.identity.APIUpdateMsg.*;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.account.header.identity.VO.*;
import javax.persistence.Query;

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
        APIChangeResultEvent evt = new APIChangeResultEvent(msg.getId());

        if(msg.isIsupdate()){
            String sql = "update UserVO set password= :newpassword where uuid = :uuid and password = :oldpassword";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newpassword", msg.getNewpassword());
            q.setParameter("oldpassword", msg.getOldpassword());

            int result = q.executeUpdate();
            if(result > 0 ){
                evt.setSuccess(true);
                evt.setMessage("success");
                evt.setObject(dbf.findByUuid(msg.getUuid(),UserVO.class));
            }else{
                evt.setSuccess(false);
                evt.setMessage("bad old passwords or username");
            }
        }else{
            evt.setSuccess(true);
            evt.setMessage("Validation code is correct");
        }

        bus.publish(evt);
    }

    public void handle(APIChangeAccountPWDMsg msg) {
        APIChangeResultEvent evt = new APIChangeResultEvent(msg.getId());

        if(msg.isIsupdate()){
            String sql = "update AccountVO set password= :newpassword where uuid = :uuid and password = :oldpassword";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newpassword", msg.getNewpassword());
            q.setParameter("oldpassword", msg.getOldpassword());

            if(q.executeUpdate() > 0 ){
                evt.setSuccess(true);
                evt.setMessage("success");
                evt.setObject(dbf.findByUuid(msg.getUuid(),AccountVO.class));
            }else{
                evt.setMessage("bad old passwords or accountname");
                evt.setSuccess(false);
            }
        }else{
            evt.setSuccess(true);
            evt.setMessage("Validation code is correct");
        }

        bus.publish(evt);
    }

    public void handle(APIChangeAccountPhoneMsg msg) {
        APIChangeResultEvent evt = new APIChangeResultEvent(msg.getId());
        if(msg.isIsupdate()){
            String sql = "update AccountVO set phone= :newphone where uuid = :uuid ";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newphone", msg.getPhone());
            int result = q.executeUpdate();

            if(result > 0 ){
                evt.setMessage("success");
                evt.setSuccess(true);
                evt.setObject(dbf.findByUuid(msg.getUuid(),AccountVO.class));
            }else{
                evt.setSuccess(false);
                evt.setMessage("bad uuid or accountname");
            }
        }else{
            evt.setSuccess(true);
            evt.setMessage("Validation code is correct");
        }

        bus.publish(evt);
    }

    public void handle(APIChangeUserPhoneMsg msg) {
        APIChangeResultEvent evt = new APIChangeResultEvent(msg.getId());

        if(msg.isIsupdate()){
            String sql = "update UserVO set phone = :newphone where uuid = :uuid";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newphone", msg.getPhone());

            int result = q.executeUpdate();
            if(result > 0){
                evt.setSuccess(true);
                evt.setMessage("the phone has been modified");
                evt.setObject(dbf.findByUuid(msg.getUuid(),UserVO.class));
            }else{
                evt.setSuccess(false);
                evt.setMessage("bad uuid or username");
            }
        }else{
            evt.setSuccess(true);
            evt.setMessage("Validation code is correct");
        }

        bus.publish(evt);
    }

    public void handle(APIChangeAccountEmailMsg msg) {
        APIChangeResultEvent evt = new APIChangeResultEvent(msg.getId());

        if(msg.isIsupdate()){
            String sql = "update AccountVO set email = :newmail where uuid = :uuid";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newmail", msg.getEmail());

            int result = q.executeUpdate();
            if(result > 0){
                evt.setSuccess(true);
                evt.setMessage("success");
                evt.setObject(dbf.findByUuid(msg.getUuid(),AccountVO.class));
            }else{
                evt.setSuccess(false);
                evt.setMessage("bad uuid or AccountName");
            }
        }else{
            evt.setSuccess(true);
            evt.setMessage("Validation code is correct");
        }

        bus.publish(evt);
    }


    public void handle(APIChangeUserEmailMsg msg) {
        APIChangeResultEvent evt = new APIChangeResultEvent(msg.getId());
        if(msg.isIsupdate()){
            String sql = "update UserVO set email = :newmail where uuid = :uuid";
            Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", msg.getUuid());
            q.setParameter("newmail", msg.getEmail());

            int result = q.executeUpdate();
            if(result > 0 ){
                evt.setSuccess(true);
                evt.setMessage("success");
                evt.setObject(dbf.findByUuid(msg.getUuid(),UserVO.class));
            }else{
                evt.setSuccess(false);
                evt.setMessage("bad uuid or AccountName");
            }
        }else{
            evt.setSuccess(true);
            evt.setMessage("Validation code is correct");
        }

        bus.publish(evt);
    }


    public void handle(APIChangeIndustryMsg msg) {

        APIChangeResultEvent evt = new APIChangeResultEvent(msg.getId());

        String sql = "update AccountVO set industry = :newindustry where uuid = :uuid ";
        Query q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
        q.setParameter("uuid", msg.getUuid());
        q.setParameter("newindustry", msg.getNewIndustry());

        if(q.executeUpdate() > 0 ){
            evt.setSuccess(true);
            evt.setMessage("success");
        }else{
            evt.setSuccess(false);
            evt.setMessage("bad uuid or AccountName");
        }
        bus.publish(evt);
    }
}
