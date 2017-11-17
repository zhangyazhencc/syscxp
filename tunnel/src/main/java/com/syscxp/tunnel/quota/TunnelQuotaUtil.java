package com.syscxp.tunnel.quota;

import com.syscxp.core.db.Q;
import com.syscxp.core.identity.QuotaUtil;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.tunnel.*;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.Map;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class TunnelQuotaUtil {
    private static final CLogger logger = Utils.getLogger(TunnelQuotaUtil.class);

    @Autowired
    public DatabaseFacade dbf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private CloudBus bus;
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