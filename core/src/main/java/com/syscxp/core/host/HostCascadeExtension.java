package com.syscxp.core.host;

import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.cascade.*;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusListCallBack;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.core.Completion;
import com.syscxp.header.host.*;
import com.syscxp.header.message.MessageReply;
import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.function.Function;
import com.syscxp.utils.logging.CLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
public class HostCascadeExtension extends AbstractAsyncCascadeExtension {
    private static final CLogger logger = Utils.getLogger(HostCascadeExtension.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private HostExtensionPointEmitter extpEmitter;
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;

    private static final String NAME = HostVO.class.getSimpleName();

    @Override
    public void asyncCascade(CascadeAction action, Completion completion) {
        if (action.isActionCode(CascadeConstant.DELETION_CHECK_CODE)) {
            handleDeletionCheck(action, completion);
        } else if (action.isActionCode(CascadeConstant.DELETION_DELETE_CODE, CascadeConstant.DELETION_FORCE_DELETE_CODE)) {
            handleDeletion(action, completion);
        } else if (action.isActionCode(CascadeConstant.DELETION_CLEANUP_CODE)) {
            handleDeletionCleanup(action, completion);
        } else {
            completion.success();
        }

    }

    private void handleDeletionCleanup(CascadeAction action, Completion completion) {
        dbf.eoCleanup(HostVO.class);
        completion.success();
    }

    private void handleDeletion(final CascadeAction action, final Completion completion) {
        final List<HostInventory> hinvs = hostFromAction(action);
        if (hinvs == null) {
            completion.success();
            return;
        }

        List<HostDeletionMsg> msgs = new ArrayList<HostDeletionMsg>();
        for (HostInventory hinv : hinvs) {
            HostDeletionMsg msg = new HostDeletionMsg();
            msg.setForceDelete(action.isActionCode(CascadeConstant.DELETION_FORCE_DELETE_CODE));
            msg.setHostUuid(hinv.getUuid());
            bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, hinv.getUuid());
            msgs.add(msg);
        }

        bus.send(msgs, 10, new CloudBusListCallBack(completion) {
            @Override
            public void run(List<MessageReply> replies) {
                if (!action.isActionCode(CascadeConstant.DELETION_FORCE_DELETE_CODE)) {
                    for (MessageReply r : replies) {
                        if (!r.isSuccess()) {
                            completion.fail(r.getError());
                            return;
                        }
                    }
                }

                List<String> uuids = new ArrayList<String>();
                for (MessageReply r : replies) {
                    HostInventory hinv = hinvs.get(replies.indexOf(r));
                    uuids.add(hinv.getUuid());
                    logger.debug(String.format("delete host[uuid:%s, name:%s]", hinv.getUuid(), hinv.getName()));
                }

                dbf.removeByPrimaryKeys(uuids, HostVO.class);
                completion.success();
            }
        });
    }

    private void handleDeletionCheck(CascadeAction action, Completion completion) {
        List<HostInventory> hinvs = hostFromAction(action);
        if (hinvs == null) {
            completion.success();
            return;
        }

        try {
            for (HostInventory hinv : hinvs) {
                extpEmitter.preDelete(hinv);
            }

            completion.success();
        } catch (HostException e) {
            completion.fail(errf.throwableToInternalError(e));
        }
    }

    @Override
    public List<String> getEdgeNames() {
//        return Arrays.asList(ClusterVO.class.getSimpleName());
        return Arrays.asList("");
    }

    @Override
    public String getCascadeResourceName() {
        return NAME;
    }

    private List<HostInventory> hostFromAction(CascadeAction action) {
        List<HostInventory> ret = null;
//        if (ClusterVO.class.getSimpleName().equals(action.getParentIssuer())) {
//            List<ClusterInventory> clusters = action.getParentIssuerContext();
//            List<String> cuuids = CollectionUtils.transformToList(clusters, new Function<String, ClusterInventory>() {
//                @Override
//                public String call(ClusterInventory arg) {
//                    return arg.getUuid();
//                }
//            });
//
//            SimpleQuery<HostVO> q = dbf.createQuery(HostVO.class);
//            q.add(HostVO_.clusterUuid, SimpleQuery.Op.IN, cuuids);
//            List<HostVO> hvos = q.list();
//            if (!hvos.isEmpty()) {
//                ret = HostInventory.valueOf(hvos);
//            }
//        } else if (NAME.equals(action.getParentIssuer())) {
//            ret = action.getParentIssuerContext();
//        }

        return ret;
    }

    @Override
    public CascadeAction createActionForChildResource(CascadeAction action) {
        if (CascadeConstant.DELETION_CODES.contains(action.getActionCode())) {
            List<HostInventory> invs = hostFromAction(action);
            if (invs != null) {
                return action.copy().setParentIssuer(NAME).setParentIssuerContext(invs);
            }
        }

        return null;
    }
}
