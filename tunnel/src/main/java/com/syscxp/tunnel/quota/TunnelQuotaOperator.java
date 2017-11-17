package com.syscxp.tunnel.quota;

import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.identity.QuotaUtil;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.NeedQuotaCheckMessage;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.tunnel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TunnelQuotaOperator implements Quota.QuotaOperator {

    @Autowired
    public DatabaseFacade dbf;
    @Autowired
    protected RESTFacade restf;

    public class InterfaceQuota {
        public long interfaceNum;
    }

    public class TunnelQuota {
        public long tunnelNum;
    }

    @Transactional(readOnly = true)
    public TunnelQuota getUsedTunnel(String accountUUid) {
        TunnelQuota quota = new TunnelQuota();
        quota.tunnelNum = getUsedTunnelNum(accountUUid);
        return quota;
    }

    @Transactional(readOnly = true)
    public InterfaceQuota getUsedInterface(String accountUUid) {
        InterfaceQuota quota = new InterfaceQuota();
        quota.interfaceNum = getUsedInterfaceNum(accountUUid);
        return quota;
    }

    @Transactional(readOnly = true)
    public long getUsedTunnelNum(String accountUuid) {
        Long num = Q.New(TunnelVO.class).eq(TunnelVO_.accountUuid, accountUuid).count();
        return num == null ? 0 : num;
    }

    @Transactional(readOnly = true)
    public long getUsedInterfaceNum(String accountUuid) {
        Long num = Q.New(InterfaceVO.class).eq(InterfaceVO_.accountUuid, accountUuid).count();
        return num == null ? 0 : num;
    }

    @Override
    public void checkQuota(APIMessage msg, Map<String, Quota.QuotaPair> pairs) {
        if (!msg.getSession().isAdminSession()) {
            if (msg instanceof APICreateInterfaceMsg) {
                check((APICreateInterfaceMsg) msg, pairs);
            } else if (msg instanceof APICreateTunnelMsg) {
                check((APICreateTunnelMsg) msg, pairs);
            }
        } else {
            if (msg instanceof APICreateInterfaceManualMsg) {
                check((APICreateInterfaceManualMsg) msg, pairs);
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

        InterfaceQuota interfaceQuota = getUsedInterface(accountUuid);
        TunnelQuota tuunelQuota = getUsedTunnel(accountUuid);

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

        checkInterfaceNum(currentAccountUuid, ownerAccountUuid, interfaceNumQuota);
    }

    @Transactional(readOnly = true)
    private void check(APICreateInterfaceManualMsg msg, Map<String, Quota.QuotaPair> pairs) {

        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();

        long interfaceNumQuota = pairs.get(TunnelConstant.QUOTA_INTERFACE_NUM).getValue();

        checkInterfaceNum(currentAccountUuid, ownerAccountUuid, interfaceNumQuota);
    }

    @Transactional(readOnly = true)
    private void check(APICreateTunnelMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();

        long tunnelNumQuota = pairs.get(TunnelConstant.QUOTA_TUNNEL_NUM).getValue();

        checkTunnelNum(currentAccountUuid, ownerAccountUuid, tunnelNumQuota);
    }

    @Transactional(readOnly = true)
    private void check(APICreateTunnelManualMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();

        long tunnelNumQuota = pairs.get(TunnelConstant.QUOTA_TUNNEL_NUM).getValue();

        checkTunnelNum(currentAccountUuid, ownerAccountUuid, tunnelNumQuota);
    }

    public void checkTunnelNum(String currentAccountUuid, String ownerAccountUuid, long tunnelNumQuota) {
        long tunnelNumUsed = getUsedTunnelNum(ownerAccountUuid);
        CheckQuota(currentAccountUuid, ownerAccountUuid, TunnelConstant.QUOTA_TUNNEL_NUM, tunnelNumQuota, tunnelNumUsed);

    }

    public void checkInterfaceNum(String currentAccountUuid, String ownerAccountUuid, long interfaceNumQuota) {
        long interfaceNumUsed = getUsedInterfaceNum(ownerAccountUuid);
        CheckQuota(currentAccountUuid, ownerAccountUuid, TunnelConstant.QUOTA_INTERFACE_NUM, interfaceNumQuota, interfaceNumUsed);
    }

    private void CheckQuota(String currentAccountUuid, String ownerAccountUuid, String quotaName, long quotaNum, long usedNum) {
        long askedNum = 1;

        QuotaUtil.QuotaCompareInfo quotaCompareInfo;
        {
            quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
            quotaCompareInfo.currentAccountUuid = currentAccountUuid;
            quotaCompareInfo.ownerAccountUuid = ownerAccountUuid;
            quotaCompareInfo.quotaName = quotaName;
            quotaCompareInfo.quotaValue = quotaNum;
            quotaCompareInfo.currentUsed = usedNum;
            quotaCompareInfo.request = askedNum;
            new QuotaUtil().CheckQuota(quotaCompareInfo);
        }
    }
}
