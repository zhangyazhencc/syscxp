package com.syscxp.account.quota;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.user.APICreateUserMsg;
import com.syscxp.account.header.user.UserVO;
import com.syscxp.account.header.user.UserVO_;
import com.syscxp.core.db.Q;
import com.syscxp.core.identity.QuotaUtil;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.NeedQuotaCheckMessage;
import com.syscxp.header.quota.Quota;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserQuotaOperator implements Quota.QuotaOperator {

    public class UserQuota {
        public long uesrNum;
    }

    @Override
    public void checkQuota(APIMessage msg, Map<String, Quota.QuotaPair> pairs) {
        if (msg instanceof APICreateUserMsg) {
            check((APICreateUserMsg) msg, pairs);
        }
    }

    @Transactional(readOnly = true)
    private void check(APICreateUserMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String ownerAccountUuid = msg.getAccountUuid();
        String quotaName = AccountConstant.QUOTA_USER_NUM;
        long quotaNum = pairs.get(quotaName).getValue();
        long currentUsed = getUsedUserNum(ownerAccountUuid);

        CheckTunnelQuota(currentAccountUuid, ownerAccountUuid, quotaName, quotaNum, currentUsed);
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
    public UserQuota getUsedUser(String accountUUid) {
        UserQuota quota = new UserQuota();
        quota.uesrNum = getUsedUserNum(accountUUid);
        return quota;
    }

    @Transactional(readOnly = true)
    public long getUsedUserNum(String accountUuid) {
        Long num = Q.New(UserVO.class).eq(UserVO_.accountUuid, accountUuid).count();
        return num == null ? 0 : num;
    }

    @Override
    public void checkQuota(NeedQuotaCheckMessage msg, Map<String, Quota.QuotaPair> pairs) {

    }

    @Override
    public List<Quota.QuotaUsage> getQuotaUsageByAccount(String accountUuid) {
        List<Quota.QuotaUsage> usages = new ArrayList<>();

        UserQuota quota = getUsedUser(accountUuid);

        Quota.QuotaUsage usage = new Quota.QuotaUsage();
        usage.setName(AccountConstant.QUOTA_USER_NUM);
        usage.setUsed(quota.uesrNum);
        usages.add(usage);

        return usages;
    }
}
