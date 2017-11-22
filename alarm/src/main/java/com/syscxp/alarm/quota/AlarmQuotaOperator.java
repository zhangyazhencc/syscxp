package com.syscxp.alarm.quota;

import com.syscxp.alarm.header.resourcePolicy.*;
import com.syscxp.core.db.Q;
import com.syscxp.core.identity.QuotaUtil;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.NeedQuotaCheckMessage;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.quota.QuotaConstant;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmQuotaOperator implements Quota.QuotaOperator {

    public class AlarmPolicyQuota {
        public long alarmPolicyNum;
        public long policyRoleNum;
    }

    @Override
    public void checkQuota(APIMessage msg, Map<String, Quota.QuotaPair> pairs) {
        if (msg instanceof APICreatePolicyMsg) {
            check((APICreatePolicyMsg) msg, pairs);
        } else if (msg instanceof APICreateRegulationMsg) {
            check((APICreateRegulationMsg) msg, pairs);
        }
    }

    @Transactional(readOnly = true)
    private void check(APICreatePolicyMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();
        String quotaName = AlarmConstant.QUOTA_ALARM_POLICY_NUM;
        long quotaNum = pairs.get(quotaName).getValue();
        long currentUsed = getUsedPolicyNum(ownerAccountUuid, msg.getProductType());

        CheckTunnelQuota(currentAccountUuid, ownerAccountUuid, quotaName, quotaNum, currentUsed);
    }

    @Transactional(readOnly = true)
    private void check(APICreateRegulationMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String quotaName = AlarmConstant.QUOTA_POLICY_RULE_NUM;
        long quotaNum = pairs.get(quotaName).getValue();
        long currentUsed = getUsedRuleNum(currentAccountUuid);

        CheckTunnelQuota(currentAccountUuid, currentAccountUuid, quotaName, quotaNum, currentUsed);
    }

    private void CheckTunnelQuota(String currentAccountUuid, String ownerAccountUuid, String quotaName, long quotaNum, long currentUsed) {
        long askedNum = 1;
        QuotaUtil.QuotaCompareInfo quotaCompareInfo;
        {
            quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
            quotaCompareInfo.currentAccountUuid = currentAccountUuid;
            quotaCompareInfo.ownerAccountUuid = ownerAccountUuid;
            quotaCompareInfo.quotaName = quotaName;
            quotaCompareInfo.quotaValue = quotaNum;
            quotaCompareInfo.currentUsed = currentUsed;
            quotaCompareInfo.request = askedNum;
            new QuotaUtil().CheckQuota(quotaCompareInfo);
        }
    }

    @Transactional(readOnly = true)
    public long getUsedPolicyNum(String accountUuid, ProductType productType) {
        Long num = Q.New(PolicyVO.class)
                .eq(PolicyVO_.productType, productType)
                .eq(PolicyVO_.accountUuid, accountUuid).count();
        return num == null ? 0 : num;
    }

    @Transactional(readOnly = true)
    public long getUsedRuleNum(String policyUuid) {
        Long num = Q.New(RegulationVO.class).eq(RegulationVO_.policyUuid, policyUuid).count();
        return num == null ? 0 : num;
    }

    @Override
    public void checkQuota(NeedQuotaCheckMessage msg, Map<String, Quota.QuotaPair> pairs) {

    }

    @Override
    public List<Quota.QuotaUsage> getQuotaUsageByAccount(String accountUuid) {
        List<Quota.QuotaUsage> usages = new ArrayList<>();

        Quota.QuotaUsage usage = new Quota.QuotaUsage();
        usage.setName(AlarmConstant.QUOTA_ALARM_POLICY_NUM);
        usage.setTotal(QuotaConstant.QUOTA_ALARM_POLICY_NUM);
        usages.add(usage);

        usage = new Quota.QuotaUsage();
        usage.setName(AlarmConstant.QUOTA_POLICY_RULE_NUM);
        usage.setTotal(QuotaConstant.QUOTA_POLICY_RULE_NUM);
        usages.add(usage);

        return usages;
    }
}
