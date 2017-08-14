package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.*;
import org.zstack.account.header.UserInventory;
import org.zstack.account.header.AccountVO;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.AccountStatus;
import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.AccountGrade;

import javax.persistence.Query;

/**
 * Created by wangwg on 2017/8/8.
 * modify by wangwg on 2017/8/14
 */
public class HandBase {

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    public void handle(APIChangeUserPWDMsg msg) {

        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());
        evt.setSuccess(true);

        if(msg.isIsupdate()){
            UserVO user = dbf.findByUuid(msg.getUuid(), UserVO.class);
            if (user.getPassword().equals(msg.getOldpassword())){
                user.setPassword(msg.getNewpassword());
                evt.setInventory(UserInventory.valueOf(dbf.updateAndRefresh(user)));
            }else{
                throw new CloudRuntimeException("bad old passwords");
            }
        }

        bus.publish(evt);
    }

    public void handle(APIChangeAccountPWDMsg msg) {
        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());
        evt.setSuccess(true);
        if(msg.isIsupdate()){
            AccountVO account = dbf.findByUuid(msg.getUuid(), AccountVO.class);
            if (account.getPassword().equals(msg.getOldpassword())){
                account.setPassword(msg.getNewpassword());
                evt.setInventory(AccountInventory.valueOf(dbf.updateAndRefresh(account)));
            }else{
                throw new CloudRuntimeException("bad old passwords");
            }
        }

        bus.publish(evt);
    }

    public void handle(APIChangeAccountPhoneMsg msg) {
        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());
        evt.setSuccess(true);

        if(msg.isIsupdate()){
            AccountVO account = dbf.findByUuid(msg.getUuid(), AccountVO.class);
            account.setPhone(msg.getPhone());
            evt.setInventory(AccountInventory.valueOf(dbf.updateAndRefresh(account)));
        }

        bus.publish(evt);
    }

    public void handle(APIChangeUserPhoneMsg msg) {
        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());
        evt.setSuccess(true);

        if(msg.isIsupdate()) {
            UserVO user = dbf.findByUuid(msg.getUuid(), UserVO.class);
            user.setPhone(msg.getPhone());
            evt.setInventory(UserInventory.valueOf(dbf.updateAndRefresh(user)));
        }

        bus.publish(evt);
    }

    public void handle(APIChangeAccountEmailMsg msg) {

        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());
        evt.setSuccess(true);

        if(msg.isIsupdate()){
            AccountVO account = dbf.findByUuid(msg.getUuid(), AccountVO.class);
            account.setEmail(msg.getEmail());
            evt.setInventory(AccountInventory.valueOf(dbf.updateAndRefresh(account)));
        }

        bus.publish(evt);
    }


    public void handle(APIChangeUserEmailMsg msg) {
        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());
        evt.setSuccess(true);

        if(msg.isIsupdate()) {
            UserVO user = dbf.findByUuid(msg.getUuid(), UserVO.class);
            user.setEmail(msg.getEmail());
            evt.setInventory(UserInventory.valueOf(dbf.updateAndRefresh(user)));
        }

        bus.publish(evt);
    }


    public void handle(APIChangeIndustryMsg msg) {

        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());
        evt.setSuccess(true);
        AccountVO account = dbf.findByUuid(msg.getUuid(), AccountVO.class);
        account.setIndustry(msg.getNewIndustry());
        evt.setInventory(AccountInventory.valueOf(dbf.updateAndRefresh(account)));


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

        if (msg.getDescription() != null) {
            account.setDescription(msg.getDescription());
        }
        if (msg.getEmail() != null) {
            account.setEmail(msg.getEmail());
        }
        if (msg.getGrade() != null) {
            if(msg.getGrade().equals(AccountGrade.Normal)){
                account.setGrade(AccountGrade.Normal);
            }else if(msg.getGrade().equals(AccountGrade.Middling)){
                account.setGrade(AccountGrade.Middling);
            }else if(msg.getGrade().equals(AccountGrade.Important)){
                account.setGrade(AccountGrade.Important);
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

        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());
        evt.setInventory(AccountInventory.valueOf(account));
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


    public void handle(APICreateAccountMsg msg) {

        AccountVO vo = new AccountVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setPassword(msg.getPassword());
        vo.setCompany(msg.getCompany());
        vo.setDescription(msg.getDescription());
        vo.setEmail(msg.getEmail());
        vo.setIndustry(msg.getIndustry());
        vo.setPhone(msg.getPhone());
        vo.setTrueName(msg.getTrueName());
        vo.setStatus(msg.getStatus());
        vo.setType(msg.getType() != null ? AccountType.valueOf(msg.getType()) : AccountType.Normal);
        vo.setGrade( msg.getGrade());

        dbf.persistAndRefresh(vo);
//        CollectionUtils.safeForEach(pluginRgty.getExtensionList(AfterCreateAccountExtensionPoint.class),
//                arg -> arg.afterCreateAccount(AccountInventory.valueOf(vo)));

        APICreateAccountEvent evt = new APICreateAccountEvent(msg.getId());
        evt.setInventory(AccountInventory.valueOf(vo));
        bus.publish(evt);
    }

    public void handle(APICreateUserMsg msg) {

        UserVO uservo = new UserVO();
        uservo.setUuid(Platform.getUuid());
        uservo.setAccountUuid(msg.getAccountUuid());
        uservo.setDepartment(msg.getDepartment());
        uservo.setDescription(msg.getDescription());
        uservo.setEmail(msg.getEmail());
        uservo.setName(msg.getName());
        uservo.setPassword(msg.getPassword());
        uservo.setPhone(msg.getPhone());
        uservo.setStatus(msg.getStatus() != null ? AccountStatus.valueOf(msg.getStatus()) : AccountStatus.Available);
        uservo.setTrueName(msg.getTrueName());
        dbf.persistAndRefresh(uservo);

        APICreateUserEvent evt = new APICreateUserEvent(msg.getId());
        evt.setInventory(UserInventory.valueOf(uservo));
        bus.publish(evt);
    }
}
