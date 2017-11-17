package com.syscxp.tunnel.quota;

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

    @Override
    public void checkQuota(APIMessage msg, Map<String, Quota.QuotaPair> pairs) {
        if (!msg.getSession().isAdminSession()) {
            if (msg instanceof APICreateInterfaceMsg) {
                check((APICreateInterfaceMsg) msg, pairs);
            } else if (msg instanceof APICreateInterfaceManualMsg) {
                check((APICreateInterfaceManualMsg) msg, pairs);
            } else if (msg instanceof APICreateTunnelMsg) {
                check((APICreateTunnelMsg) msg, pairs);
            } else if (msg instanceof APICreateTunnelManualMsg) {
                check((APICreateTunnelManualMsg) msg, pairs);
            }
        }
    }

    @Override
    public void checkQuota(NeedQuotaCheckMessage msg, Map<String, Quota.QuotaPair> pairs) {

    }

    @Override
    public List<Quota.QuotaUsage> getQuotaUsageByAccount(String accountUuid) {
        List<Quota.QuotaUsage> usages = new ArrayList<>();

        TunnelQuotaUtil.InterfaceQuota interfaceQuota = new TunnelQuotaUtil().getUsedInterface(accountUuid);
        TunnelQuotaUtil.TunnelQuota tuunelQuota = new TunnelQuotaUtil().getUsedTunnel(accountUuid);

        Quota.QuotaUsage usage = new Quota.QuotaUsage();
        usage.setName(TunnelConstant.QUOTA_INTERFACE_NUM);
        usage.setUsed(interfaceQuota.interfaceNum);
        usages.add(usage);

        usage = new Quota.QuotaUsage();
        usage.setName(TunnelConstant.QUOTA_TUNNEL_NUM);
        usage.setUsed(tuunelQuota.tunnelNum);
        usages.add(usage);

        return usages;
    }

    @Transactional(readOnly = true)
    private void check(APICreateInterfaceMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();

        long interfaceNumQuota = pairs.get(TunnelConstant.QUOTA_INTERFACE_NUM).getValue();

        new TunnelQuotaUtil().checkInterfaceNum(currentAccountUuid, ownerAccountUuid, interfaceNumQuota);
    }

    @Transactional(readOnly = true)
    private void check(APICreateInterfaceManualMsg msg, Map<String, Quota.QuotaPair> pairs) {

        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();

        long interfaceNumQuota = pairs.get(TunnelConstant.QUOTA_INTERFACE_NUM).getValue();

        new TunnelQuotaUtil().checkInterfaceNum(currentAccountUuid, ownerAccountUuid, interfaceNumQuota);
    }

    @Transactional(readOnly = true)
    private void check(APICreateTunnelMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();

        long tunnelNumQuota = pairs.get(TunnelConstant.QUOTA_TUNNEL_NUM).getValue();

        new TunnelQuotaUtil().checkTunnelNum(currentAccountUuid, ownerAccountUuid, tunnelNumQuota);
    }

    @Transactional(readOnly = true)
    private void check(APICreateTunnelManualMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();

        long tunnelNumQuota = pairs.get(TunnelConstant.QUOTA_TUNNEL_NUM).getValue();

        new TunnelQuotaUtil().checkTunnelNum(currentAccountUuid, ownerAccountUuid, tunnelNumQuota);
    }



}
