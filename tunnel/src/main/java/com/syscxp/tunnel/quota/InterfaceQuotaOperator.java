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

public class InterfaceQuotaOperator implements Quota.QuotaOperator {

    public class InterfaceQuota {
        public long interfaceNum;
    }

    @Override
    public void checkQuota(APIMessage msg, Map<String, Quota.QuotaPair> pairs) {
        if (msg instanceof APICreateInterfaceMsg) {
            check((APICreateInterfaceMsg) msg, pairs);
        } else if (msg instanceof APICreateInterfaceManualMsg) {
            check((APICreateInterfaceManualMsg) msg, pairs);
        }
    }

    @Transactional(readOnly = true)
    private void check(APICreateInterfaceMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();

        CheckQuota(currentAccountUuid, ownerAccountUuid, pairs);

    }

    @Transactional(readOnly = true)
    private void check(APICreateInterfaceManualMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();

        CheckQuota(currentAccountUuid, ownerAccountUuid, pairs);
    }

    private void CheckQuota(String currentAccountUuid, String ownerAccountUuid, Map<String, Quota.QuotaPair> pairs) {

        long quotaNum = pairs.get(TunnelConstant.QUOTA_INTERFACE_NUM).getValue();
        long askedNum = 1;

        QuotaUtil.QuotaCompareInfo quotaCompareInfo;
        {
            quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
            quotaCompareInfo.currentAccountUuid = currentAccountUuid;
            quotaCompareInfo.ownerAccountUuid = ownerAccountUuid;
            quotaCompareInfo.quotaName = TunnelConstant.QUOTA_INTERFACE_NUM;
            quotaCompareInfo.quotaValue = quotaNum;
            quotaCompareInfo.currentUsed = getUsedInterfaceNum(ownerAccountUuid);
            quotaCompareInfo.request = askedNum;
            new QuotaUtil().CheckQuota(quotaCompareInfo);
        }
    }

    @Transactional(readOnly = true)
    public InterfaceQuota getUsedInterface(String accountUUid) {
        InterfaceQuota quota = new InterfaceQuota();
        quota.interfaceNum = getUsedInterfaceNum(accountUUid);
        return quota;
    }

    @Transactional(readOnly = true)
    public long getUsedInterfaceNum(String accountUuid) {
        Long num = Q.New(InterfaceVO.class).eq(InterfaceVO_.accountUuid, accountUuid).count();
        return num == null ? 0 : num;
    }

    @Override
    public void checkQuota(NeedQuotaCheckMessage msg, Map<String, Quota.QuotaPair> pairs) {

    }

    @Override
    public List<Quota.QuotaUsage> getQuotaUsageByAccount(String accountUuid) {
        List<Quota.QuotaUsage> usages = new ArrayList<>();

        InterfaceQuota interfaceQuota = getUsedInterface(accountUuid);

        Quota.QuotaUsage usage = new Quota.QuotaUsage();
        usage.setName(TunnelConstant.QUOTA_INTERFACE_NUM);
        usage.setUsed(interfaceQuota.interfaceNum);
        usages.add(usage);

        return usages;
    }
}
