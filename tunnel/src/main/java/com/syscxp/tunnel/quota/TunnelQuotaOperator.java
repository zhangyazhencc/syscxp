package com.syscxp.tunnel.quota;

import com.syscxp.core.db.Q;
import com.syscxp.core.identity.QuotaUtil;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.NeedQuotaCheckMessage;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.tunnel.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TunnelQuotaOperator implements Quota.QuotaOperator {

    public class TunnelQuota {
        public long tunnelNum;
    }

    @Override
    public void checkQuota(APIMessage msg, Map<String, Quota.QuotaPair> pairs) {
        if (msg instanceof APICreateTunnelMsg) {
            check((APICreateTunnelMsg) msg, pairs);
        } else if (msg instanceof APICreateTunnelManualMsg) {
            check((APICreateTunnelManualMsg) msg, pairs);
        }
    }

    @Transactional(readOnly = true)
    private void check(APICreateTunnelMsg msg, Map<String, Quota.QuotaPair> pairs) {
        long quotaNum = pairs.get(TunnelConstant.QUOTA_TUNNEL_NUM).getValue();
        long currentUsed = getUsedTunnelNum(msg.getAccountUuid());

        new QuotaUtil().CheckQuota(TunnelConstant.QUOTA_TUNNEL_NUM, currentUsed, quotaNum);
    }

    @Transactional(readOnly = true)
    private void check(APICreateTunnelManualMsg msg, Map<String, Quota.QuotaPair> pairs) {

        long quotaNum = pairs.get(TunnelConstant.QUOTA_TUNNEL_NUM).getValue();
        long currentUsed = getUsedTunnelNum(msg.getAccountUuid());

        long ifaceQuotaNum = pairs.get(TunnelConstant.QUOTA_INTERFACE_NUM).getValue();
        long ifaceCurrentUsed = getUsedInterfaceNum(msg.getAccountUuid());
        if (msg.getInterfaceAUuid() == null) {
            new QuotaUtil().CheckQuota(TunnelConstant.QUOTA_INTERFACE_NUM, ifaceCurrentUsed, ifaceQuotaNum);
        }
        if (msg.getInterfaceZUuid() == null) {
            new QuotaUtil().CheckQuota(TunnelConstant.QUOTA_INTERFACE_NUM, ifaceCurrentUsed, ifaceQuotaNum);
        }

        new QuotaUtil().CheckQuota(TunnelConstant.QUOTA_TUNNEL_NUM, currentUsed, quotaNum);
    }

    @Transactional(readOnly = true)
    public long getUsedInterfaceNum(String accountUuid) {
        Long num = Q.New(InterfaceVO.class).eq(InterfaceVO_.accountUuid, accountUuid).count();
        return num == null ? 0 : num;
    }

    @Transactional(readOnly = true)
    public TunnelQuota getUsedTunnel(String accountUUid) {
        TunnelQuota quota = new TunnelQuota();
        quota.tunnelNum = getUsedTunnelNum(accountUUid);
        return quota;
    }

    @Transactional(readOnly = true)
    public long getUsedTunnelNum(String accountUuid) {
        Long num = Q.New(TunnelVO.class).eq(TunnelVO_.accountUuid, accountUuid).count();
        return num == null ? 0 : num;
    }

    @Override
    public void checkQuota(NeedQuotaCheckMessage msg, Map<String, Quota.QuotaPair> pairs) {

    }

    @Override
    public List<Quota.QuotaUsage> getQuotaUsageByAccount(String accountUuid) {
        List<Quota.QuotaUsage> usages = new ArrayList<>();

        TunnelQuota tuunelQuota = getUsedTunnel(accountUuid);

        Quota.QuotaUsage usage = new Quota.QuotaUsage();
        usage.setName(TunnelConstant.QUOTA_TUNNEL_NUM);
        usage.setUsed(tuunelQuota.tunnelNum);
        usages.add(usage);

        return usages;
    }
}
