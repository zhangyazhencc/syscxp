package org.zstack.account.identity;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.core.Platform;
import org.zstack.core.cascade.CascadeConstant;
import org.zstack.core.cascade.CascadeFacade;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.*;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.workflow.FlowChainBuilder;
import org.zstack.core.workflow.ShareFlow;
import org.zstack.header.core.Completion;
import org.zstack.header.core.workflow.*;
import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.account.header.identity.*;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.utils.CollectionUtils;
import org.zstack.utils.DebugUtils;
import org.zstack.utils.ExceptionDSL;
import org.zstack.utils.Utils;
import org.zstack.utils.function.ForEachFunction;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.zstack.core.Platform.argerr;
import static org.zstack.utils.CollectionDSL.list;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class AccountBase extends AbstractAccount {
    private static final CLogger logger = Utils.getLogger(AccountBase.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private AccountManager acntMgr;
    @Autowired
    private CascadeFacade casf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private EventFacade evtf;

    private AccountVO vo;

    public AccountBase(AccountVO vo) {
        this.vo = vo;
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handle(APIUpdateAccountMsg msg) {
        AccountVO account = dbf.findByUuid(msg.getUuid(), AccountVO.class);
        if (msg.getName() != null) {
            account.setName(msg.getName());
        }
        if (msg.getDescription() != null) {
            account.setDescription(msg.getDescription());
        }
        if (msg.getPassword() != null) {
            account.setPassword(msg.getPassword());
        }
        account = dbf.updateAndRefresh(account);

        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());
        evt.setInventory(AccountInventory.valueOf(account));
        bus.publish(evt);
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APIUpdateAccountMsg) {
            handle((APIUpdateAccountMsg) msg);
        } else if (msg instanceof APICreateUserMsg) {
            handle((APICreateUserMsg) msg);
        } else if (msg instanceof APIUpdateUserMsg) {
            handle((APIUpdateUserMsg) msg);
        } else if (msg instanceof APIDeleteUserMsg) {
            handle((APIDeleteUserMsg) msg);
        } else if (msg instanceof APICreatePolicyMsg) {
            handle((APICreatePolicyMsg) msg);
        } else if (msg instanceof APIDeletePolicyMsg) {
            handle((APIDeletePolicyMsg) msg);
        } else if (msg instanceof APIAttachPolicyToUserMsg) {
            handle((APIAttachPolicyToUserMsg) msg);
        } else if (msg instanceof APIDetachPolicyFromUserMsg) {
            handle((APIDetachPolicyFromUserMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIUpdateUserMsg msg) {
        UserVO user = dbf.findByUuid(msg.getUuid(), UserVO.class);

        if (!AccountConstant.INITIAL_SYSTEM_ADMIN_UUID.equals(msg.getAccountUuid()) && !user.getAccountUuid().equals(msg.getAccountUuid())) {
            throw new OperationFailureException(argerr("the user[uuid:%s] does not belong to the" +
                    " account[uuid:%s]", user.getUuid(), msg.getAccountUuid()));
        }

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
        if (update) {
            user = dbf.updateAndRefresh(user);
        }

        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());
        evt.setInventory(UserInventory.valueOf(user));
        bus.publish(evt);
    }

    private void handle(APIDetachPolicyFromUserMsg msg) {
        SimpleQuery<UserPolicyRefVO> q = dbf.createQuery(UserPolicyRefVO.class);
        q.add(UserPolicyRefVO_.policyUuid, Op.EQ, msg.getPolicyUuid());
        q.add(UserPolicyRefVO_.userUuid, Op.EQ, msg.getUserUuid());
        UserPolicyRefVO ref = q.find();
        if (ref != null) {
            dbf.remove(ref);
        }

        bus.publish(new APIDetachPolicyFromUserEvent(msg.getId()));
    }

    private void handle(APIDeletePolicyMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), PolicyVO.class);
        APIDeletePolicyEvent evt = new APIDeletePolicyEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIDeleteUserMsg msg) {
        UserVO user = dbf.findByUuid(msg.getUuid(), UserVO.class);
        if (user != null) {
            UserInventory inv = UserInventory.valueOf(user);
            UserDeletedData d = new UserDeletedData();
            d.setInventory(inv);
            d.setUserUuid(inv.getUuid());
            evtf.fire(IdentityCanonicalEvents.USER_DELETED_PATH, d);

            dbf.remove(user);
        }

        APIDeleteUserEvent evt = new APIDeleteUserEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIAttachPolicyToUserMsg msg) {
        UserPolicyRefVO upvo = new UserPolicyRefVO();
        upvo.setPolicyUuid(msg.getPolicyUuid());
        upvo.setUserUuid(msg.getUserUuid());
        try {
            dbf.persist(upvo);
        } catch (Throwable t) {
            if (!ExceptionDSL.isCausedBy(t, ConstraintViolationException.class)) {
                throw t;
            }

            // the policy is already attached
        }

        APIAttachPolicyToUserEvent evt = new APIAttachPolicyToUserEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APICreatePolicyMsg msg) {
        PolicyVO pvo = new PolicyVO();
        if (msg.getResourceUuid() != null) {
            pvo.setUuid(msg.getResourceUuid());
        } else {
            pvo.setUuid(Platform.getUuid());
        }
        pvo.setAccountUuid(vo.getUuid());
        pvo.setName(msg.getName());
        pvo.setData(JSONObjectUtil.toJsonString(msg.getStatements()));

        PolicyVO finalPvo = pvo;
        pvo = new SQLBatchWithReturn<PolicyVO>() {
            @Override
            protected PolicyVO scripts() {
                persist(finalPvo);
                reload(finalPvo);
                acntMgr.createAccountResourceRef(msg.getSession().getAccountUuid(), finalPvo.getUuid(), PolicyVO.class);
                return finalPvo;
            }
        }.execute();

        PolicyInventory pinv = PolicyInventory.valueOf(pvo);
        APICreatePolicyEvent evt = new APICreatePolicyEvent(msg.getId());
        evt.setInventory(pinv);
        bus.publish(evt);
    }

    private void handle(APICreateUserMsg msg) {
        APICreateUserEvent evt = new APICreateUserEvent(msg.getId());

        UserVO uvo = new SQLBatchWithReturn<UserVO>() {
            @Override
            protected UserVO scripts() {
                UserVO uvo = new UserVO();
                if (msg.getResourceUuid() != null) {
                    uvo.setUuid(msg.getResourceUuid());
                } else {
                    uvo.setUuid(Platform.getUuid());
                }
                uvo.setAccountUuid(vo.getUuid());
                uvo.setName(msg.getName());
                uvo.setPassword(msg.getPassword());
                uvo.setDescription(msg.getDescription());
                persist(uvo);
                reload(uvo);

                PolicyVO p = Q.New(PolicyVO.class).eq(PolicyVO_.name, "DEFAULT-READ")
                        .eq(PolicyVO_.accountUuid, vo.getUuid()).find();
                if (p != null) {
                    UserPolicyRefVO uref = new UserPolicyRefVO();
                    uref.setPolicyUuid(p.getUuid());
                    uref.setUserUuid(uvo.getUuid());
                    persist(uref);
                }

                p = Q.New(PolicyVO.class).eq(PolicyVO_.name, "USER-RESET-PASSWORD")
                        .eq(PolicyVO_.accountUuid, vo.getUuid()).find();
                if (p != null) {
                    UserPolicyRefVO uref = new UserPolicyRefVO();
                    uref.setPolicyUuid(p.getUuid());
                    uref.setUserUuid(uvo.getUuid());
                    persist(uref);
                }

                acntMgr.createAccountResourceRef(msg.getSession().getAccountUuid(), uvo.getUuid(), UserVO.class);
                return uvo;
            }
        }.execute();

        final UserInventory inv = UserInventory.valueOf(uvo);

        CollectionUtils.safeForEach(pluginRgty.getExtensionList(AfterCreateUserExtensionPoint.class), new ForEachFunction<AfterCreateUserExtensionPoint>() {
            @Override
            public void run(AfterCreateUserExtensionPoint arg) {
                arg.afterCreateUser(inv);
            }
        });

        evt.setInventory(inv);
        bus.publish(evt);
    }
}
