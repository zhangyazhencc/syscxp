package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.*;
import org.zstack.account.header.identity.AccountInventory;
import org.zstack.account.header.identity.AfterCreateAccountExtensionPoint;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SQLBatchWithReturn;
import org.zstack.header.identity.AccountStatus;
import org.zstack.header.identity.AccountType;
import org.zstack.utils.CollectionUtils;

/**
 * Created by wangwg on 2017/8/14.
 */
public class CreateHand {

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private PluginRegistry pluginRgty;

    public void handle(APICreateAccountMsg msg) {
        final AccountInventory inv = new SQLBatchWithReturn<AccountInventory>() {
            @Override
            protected AccountInventory scripts() {
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
                persist(vo);
                reload(vo);

                return AccountInventory.valueOf(vo);
            }
        }.execute();


        CollectionUtils.safeForEach(pluginRgty.getExtensionList(AfterCreateAccountExtensionPoint.class),
                arg -> arg.afterCreateAccount(inv));

        APICreateAccountEvent evt = new APICreateAccountEvent(msg.getId());
        evt.setInventory(inv);
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
        evt.setObject(uservo);
        bus.publish(evt);
    }





}
