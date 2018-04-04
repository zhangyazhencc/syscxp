package com.syscxp.idc.quota;

import com.syscxp.core.db.Q;
import com.syscxp.core.identity.QuotaUtil;
import com.syscxp.header.idc.SolutionConstant;
import com.syscxp.header.idc.solution.*;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.NeedQuotaCheckMessage;
import com.syscxp.header.quota.Quota;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create by DCY on 2018/4/4
 */
public class SolutionQuotaOperator implements Quota.QuotaOperator {


    public class SolutionQuota {
        public long solutionNum;
        public long solutionInterfaceNum;
        public long solutionTunnelNum;
        public long solutionVpnNum;
    }

    @Override
    public void checkQuota(APIMessage msg, Map<String, Quota.QuotaPair> pairs) {
        if (msg instanceof APICreateSolutionMsg) {
            check((APICreateSolutionMsg) msg, pairs);
        } else if (msg instanceof APICreateSolutionInterfaceMsg) {
            check((APICreateSolutionInterfaceMsg) msg, pairs);
        } else if (msg instanceof APICreateSolutionTunnelMsg) {
            check((APICreateSolutionTunnelMsg) msg, pairs);
        } else if (msg instanceof APICreateSolutionVpnMsg) {
            check((APICreateSolutionVpnMsg) msg, pairs);
        }
    }

    @Transactional(readOnly = true)
    private void check(APICreateSolutionMsg msg, Map<String, Quota.QuotaPair> pairs) {
        long quotaNum = pairs.get(SolutionConstant.QUOTA_SOLUTION_NUM).getValue();
        long currentUsed = getUsedSolutionNum(msg.getAccountUuid());

        new QuotaUtil().CheckQuota(SolutionConstant.QUOTA_SOLUTION_NUM, currentUsed, quotaNum);
    }

    @Transactional(readOnly = true)
    private void check(APICreateSolutionInterfaceMsg msg, Map<String, Quota.QuotaPair> pairs) {
        long quotaNum = pairs.get(SolutionConstant.QUOTA_SOLUTION_INTERFACE_NUM).getValue();
        long currentUsed = getUsedSolutionNum(msg.getSolutionUuid());

        new QuotaUtil().CheckQuota(SolutionConstant.QUOTA_SOLUTION_INTERFACE_NUM, currentUsed, quotaNum);
    }

    @Transactional(readOnly = true)
    private void check(APICreateSolutionTunnelMsg msg, Map<String, Quota.QuotaPair> pairs) {
        long quotaNum = pairs.get(SolutionConstant.QUOTA_SOLUTION_TUNNEL_NUM).getValue();
        long currentUsed = getUsedSolutionNum(msg.getSolutionUuid());

        new QuotaUtil().CheckQuota(SolutionConstant.QUOTA_SOLUTION_TUNNEL_NUM, currentUsed, quotaNum);
    }

    @Transactional(readOnly = true)
    private void check(APICreateSolutionVpnMsg msg, Map<String, Quota.QuotaPair> pairs) {

        long quotaNum = pairs.get(SolutionConstant.QUOTA_SOLUTION_VPN_NUM).getValue();
        long currentUsed = getUsedSolutionNum(msg.getSolutionUuid());

        new QuotaUtil().CheckQuota(SolutionConstant.QUOTA_SOLUTION_VPN_NUM, currentUsed, quotaNum);
    }

    @Transactional(readOnly = true)
    public SolutionQuota getUsedSolution(String accountUUid) {
        SolutionQuota quota = new SolutionQuota();
        quota.solutionNum = getUsedSolutionNum(accountUUid);
        return quota;
    }

    @Transactional(readOnly = true)
    public long getUsedSolutionNum(String accountUuid) {
        Long num = Q.New(SolutionVO.class).eq(SolutionVO_.accountUuid, accountUuid).count();
        return num == null ? 0 : num;
    }

    @Transactional(readOnly = true)
    public long getUsedSolutionVpnNum(String solutionUuid) {
        Long num = Q.New(SolutionVpnVO.class).eq(SolutionVpnVO_.solutionUuid, solutionUuid).count();
        return num == null ? 0 : num;
    }


    @Transactional(readOnly = true)
    public long getUsedSolutionTunnelNum(String solutionUuid) {
        Long num = Q.New(SolutionTunnelVO.class).eq(SolutionTunnelVO_.solutionUuid, solutionUuid).count();
        return num == null ? 0 : num;
    }

    @Transactional(readOnly = true)
    public long getUsedSolutionInterfaceNum(String solutionUuid) {
        Long num = Q.New(SolutionInterfaceVO.class).eq(SolutionInterfaceVO_.solutionUuid, solutionUuid).count();
        return num == null ? 0 : num;
    }

    @Override
    public void checkQuota(NeedQuotaCheckMessage msg, Map<String, Quota.QuotaPair> pairs) {

    }

    @Override
    public List<Quota.QuotaUsage> getQuotaUsageByAccount(String accountUuid) {
        List<Quota.QuotaUsage> usages = new ArrayList<>();

        SolutionQuota quota = getUsedSolution(accountUuid);

        Quota.QuotaUsage usage = new Quota.QuotaUsage();
        usage.setName(SolutionConstant.QUOTA_SOLUTION_NUM);
        usage.setUsed(quota.solutionNum);
        usages.add(usage);

        usage = new Quota.QuotaUsage();
        usage.setName(SolutionConstant.QUOTA_SOLUTION_INTERFACE_NUM);
        usage.setUsed(quota.solutionInterfaceNum);
        usages.add(usage);

        usage = new Quota.QuotaUsage();
        usage.setName(SolutionConstant.QUOTA_SOLUTION_TUNNEL_NUM);
        usage.setUsed(quota.solutionTunnelNum);
        usages.add(usage);

        usage = new Quota.QuotaUsage();
        usage.setName(SolutionConstant.QUOTA_SOLUTION_VPN_NUM);
        usage.setUsed(quota.solutionVpnNum);
        usages.add(usage);
        return usages;
    }
}
