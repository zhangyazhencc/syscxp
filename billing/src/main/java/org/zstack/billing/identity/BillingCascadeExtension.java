package org.zstack.billing.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.billing.header.identity.AccountBalanceInventory;
import org.zstack.billing.header.identity.AccountBalanceVO;
import org.zstack.core.cascade.AbstractAsyncCascadeExtension;
import org.zstack.core.cascade.CascadeAction;
import org.zstack.core.cascade.CascadeConstant;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.core.Completion;

import java.util.Arrays;
import java.util.List;

/**
 * Created by frank on 7/15/2015.
 */
public class BillingCascadeExtension extends AbstractAsyncCascadeExtension {
    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

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
        completion.success();
    }

    private void handleDeletion(final CascadeAction action, final Completion completion) {
        final List<AccountBalanceInventory> ainvs = action.getParentIssuerContext();


    }

    private void handleDeletionCheck(CascadeAction action, Completion completion) {
        completion.success();
    }

    @Override
    public List<String> getEdgeNames() {
        return Arrays.asList();
    }

    @Override
    public String getCascadeResourceName() {
        return AccountBalanceVO.class.getSimpleName();
    }

    @Override
    public CascadeAction createActionForChildResource(CascadeAction action) {
        if (CascadeConstant.DELETION_CODES.contains(action.getActionCode())) {
            return action;
        }

        return null;
    }
}
