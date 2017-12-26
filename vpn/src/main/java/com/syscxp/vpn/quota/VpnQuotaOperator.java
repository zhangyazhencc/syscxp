package com.syscxp.vpn.quota;

import com.syscxp.core.db.Q;
import com.syscxp.core.identity.QuotaUtil;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.NeedQuotaCheckMessage;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.vpn.vpn.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wangjie
 */
public class VpnQuotaOperator implements Quota.QuotaOperator {

    public class VpnQuota {
        public long vpnNum;
        public long vpnCertNum;
    }

    @Override
    public void checkQuota(APIMessage msg, Map<String, Quota.QuotaPair> pairs) {
        if (msg instanceof APICreateVpnCertMsg) {
            check((APICreateVpnCertMsg) msg, pairs);
        } else if (msg instanceof APICreateVpnMsg) {
            check((APICreateVpnMsg) msg, pairs);
        }
    }

    @Transactional(readOnly = true)
    private void check(APICreateVpnCertMsg msg, Map<String, Quota.QuotaPair> pairs) {
        long quotaNum = pairs.get(VpnConstant.QUOTA_VPN_CERT_NUM).getValue();
        long currentUsed = getUsedVpnCertNum(msg.getAccountUuid());

        new QuotaUtil().CheckQuota(VpnConstant.QUOTA_VPN_CERT_NUM, currentUsed, quotaNum);
    }

    @Transactional(readOnly = true)
    private void check(APICreateVpnMsg msg, Map<String, Quota.QuotaPair> pairs) {

        long quotaNum = pairs.get(VpnConstant.QUOTA_VPN_NUM).getValue();
        long currentUsed = getUsedVpnCertNum(msg.getAccountUuid());

        new QuotaUtil().CheckQuota(VpnConstant.QUOTA_VPN_NUM, currentUsed, quotaNum);
    }

    @Transactional(readOnly = true)
    public long getUsedVpnNum(String accountUuid) {
        Long num = Q.New(VpnVO.class).eq(VpnVO_.accountUuid, accountUuid).count();
        return num == null ? 0 : num;
    }

    @Transactional(readOnly = true)
    public long getUsedVpnCertNum(String accountUuid) {
        Long num = Q.New(VpnCertVO.class).eq(VpnCertVO_.accountUuid, accountUuid).count();
        return num == null ? 0 : num;
    }

    @Transactional(readOnly = true)
    public VpnQuota getUsedVpn(String accountUUid) {
        VpnQuota quota = new VpnQuota();
        quota.vpnCertNum = getUsedVpnCertNum(accountUUid);
        quota.vpnNum = getUsedVpnNum(accountUUid);
        return quota;
    }

    @Override
    public void checkQuota(NeedQuotaCheckMessage msg, Map<String, Quota.QuotaPair> pairs) {

    }

    @Override
    public List<Quota.QuotaUsage> getQuotaUsageByAccount(String accountUuid) {
        List<Quota.QuotaUsage> usages = new ArrayList<>();

        VpnQuota vpnQuota = getUsedVpn(accountUuid);

        Quota.QuotaUsage usage = new Quota.QuotaUsage();
        usage.setName(VpnConstant.QUOTA_VPN_CERT_NUM);
        usage.setUsed(vpnQuota.vpnCertNum);
        usages.add(usage);

        usage = new Quota.QuotaUsage();
        usage.setName(VpnConstant.QUOTA_VPN_NUM);
        usage.setUsed(vpnQuota.vpnNum);
        usages.add(usage);

        return usages;
    }
}
