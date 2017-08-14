package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.*;
import org.zstack.account.header.identity.UserInventory;
import org.zstack.account.header.AccountVO;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.identity.AccountStatus;
import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.UserGrade;

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

    public  void handle(APIUpdateAccountMsg msg) {
        AccountVO account = dbf.findByUuid(msg.getUuid(), AccountVO.class);

        if (msg.getPassword() != null) {
            account.setPassword(msg.getPassword());
        }
        if (msg.getCompany() != null) {
            account.setCompany(msg.getCompany());
        }
        if (msg.getDepartment() != null) {
            account.setDepartment(msg.getDepartment());
        }
        if (msg.getDescription() != null) {
            account.setDescription(msg.getDescription());
        }
        if (msg.getEmail() != null) {
            account.setEmail(msg.getEmail());
        }
        if (msg.getGrade() != null) {
            if(msg.getGrade().equals(UserGrade.Normal)){
                account.setGrade(UserGrade.Normal);
            }else if(msg.getGrade().equals(UserGrade.Middling)){
                account.setGrade(UserGrade.Middling);
            }else if(msg.getGrade().equals(UserGrade.Important)){
                account.setGrade(UserGrade.Important);
            }
        }
        if (msg.getIndustry() != null) {
            account.setIndustry(msg.getIndustry());
        }
        if (msg.getPhone() != null) {
            account.setPhone(msg.getPhone());
        }
        if (msg.getStatus() != null) {
            account.setStatus(msg.getStatus().equals(AccountStatus.Available)
                    ?AccountStatus.Available:AccountStatus.Disabled);
        }
        if (msg.getTrueName() != null) {
            account.setTrueName(msg.getTrueName());
        }
        if (msg.getType() != null) {
            if(msg.getType().equals(AccountType.Normal)){
                account.setType(AccountType.Normal);
            }else if(msg.getType().equals(AccountType.Proxy)){
                account.setType(AccountType.Proxy);
            }else if(msg.getType().equals(AccountType.SystemAdmin)){
                account.setType(AccountType.SystemAdmin);
            }
        }

        account = dbf.updateAndRefresh(account);

        APIChangeResultEvent evt = new APIChangeResultEvent(msg.getId());
        evt.setObject(account);
        bus.publish(evt);
    }


    public void handle(APIUpdateUserMsg msg) {
        UserVO user = dbf.findByUuid(msg.getUuid(), UserVO.class);

        boolean update = false;
        if (msg.getName() != null) {
            user.setName(msg.getName());
            update = true;
        }
        if (msg.getDescription() != null) {
            user.setDescription(msg.getDescription());
            update = true;
        }
        if (msg.getPassword() != null) {
            user.setPassword(msg.getPassword());
            update = true;
        }
        if (msg.getDepartment() != null) {
            user.setPassword(msg.getPassword());
            update = true;
        }
        if (msg.getEmail() != null) {
            user.setEmail(msg.getEmail());
            update = true;
        }
        if (msg.getPhone() != null) {
            user.setPhone(msg.getPhone());
            update = true;
        }
        if (msg.getStatus() != null) {
            user.setStatus(msg.getStatus().equals(AccountStatus.Available)
                    ?AccountStatus.Available:AccountStatus.Disabled);
            update = true;
        }
        if (msg.getTrueName() != null) {
            user.setTrueName(msg.getTrueName());
            update = true;
        }
        if (update) {
            user = dbf.updateAndRefresh(user);
        }
        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());
        evt.setInventory(UserInventory.valueOf(user));
        bus.publish(evt);
    }

}
