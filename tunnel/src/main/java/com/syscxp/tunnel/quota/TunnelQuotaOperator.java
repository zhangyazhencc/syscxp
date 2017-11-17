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
            } else if (msg instanceof APIUpdateTunnelBandwidthMsg) {
                check((APIUpdateTunnelBandwidthMsg) msg, pairs);
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

        Quota.QuotaUsage usage = new Quota.QuotaUsage();
        usage.setName(TunnelConstant.QUOTA_INTERFACE_NUM);
        usage.setUsed(interfaceQuota.interfaceNum);
        usages.add(usage);

        usage = new Quota.QuotaUsage();
        usage.setName(TunnelConstant.QUOTA_INTERFACE_BANDWIDTH);
        usage.setUsed(interfaceQuota.interfaceBandwidth);
        usages.add(usage);

        return usages;
    }

    @Transactional(readOnly = true)
    private void check(APICreateInterfaceMsg msg, Map<String, Quota.QuotaPair> pairs) {
       /* String currentAccountUuid = msg.getSession().getAccountUuid();
        String resourceTargetOwnerAccountUuid = msg.getAccountUuid();
        if (new QuotaUtil().isAdminAccount(resourceTargetOwnerAccountUuid)) {
            return;
        }

        SimpleQuery<AccountResourceRefVO> q = dbf.createQuery(AccountResourceRefVO.class);
        q.add(AccountResourceRefVO_.resourceUuid, Op.EQ, msg.getResourceUuid());
        AccountResourceRefVO accResRefVO = q.find();


        if (accResRefVO.getResourceType().equals(ImageVO.class.getSimpleName())) {
            long imageNumQuota = pairs.get(ImageConstant.QUOTA_IMAGE_NUM).getValue();
            long imageSizeQuota = pairs.get(ImageConstant.QUOTA_IMAGE_SIZE).getValue();

            long imageNumUsed = new ImageQuotaUtil().getUsedImageNum(resourceTargetOwnerAccountUuid);
            long imageSizeUsed = new ImageQuotaUtil().getUsedImageSize(resourceTargetOwnerAccountUuid);

            ImageVO image = dbf.getEntityManager().find(ImageVO.class, msg.getResourceUuid());
            long imageNumAsked = 1;
            long imageSizeAsked = image.getSize();


            QuotaUtil.QuotaCompareInfo quotaCompareInfo;
            {
                quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
                quotaCompareInfo.currentAccountUuid = currentAccountUuid;
                quotaCompareInfo.resourceTargetOwnerAccountUuid = resourceTargetOwnerAccountUuid;
                quotaCompareInfo.quotaName = ImageConstant.QUOTA_IMAGE_NUM;
                quotaCompareInfo.quotaValue = imageNumQuota;
                quotaCompareInfo.currentUsed = imageNumUsed;
                quotaCompareInfo.request = imageNumAsked;
                new QuotaUtil().CheckQuota(quotaCompareInfo);
            }

            {
                quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
                quotaCompareInfo.currentAccountUuid = currentAccountUuid;
                quotaCompareInfo.resourceTargetOwnerAccountUuid = resourceTargetOwnerAccountUuid;
                quotaCompareInfo.quotaName = ImageConstant.QUOTA_IMAGE_SIZE;
                quotaCompareInfo.quotaValue = imageSizeQuota;
                quotaCompareInfo.currentUsed = imageSizeUsed;
                quotaCompareInfo.request = imageSizeAsked;
                new QuotaUtil().CheckQuota(quotaCompareInfo);
            }
        }*/

    }

    @Transactional(readOnly = true)
    private void check(APICreateInterfaceManualMsg msg, Map<String, Quota.QuotaPair> pairs) {
        /* String currentAccountUuid = msg.getSession().getAccountUuid();
        String resourceTargetOwnerAccountUuid = new QuotaUtil().getResourceOwnerAccountUuid(msg.getImageUuid());

        long imageNumQuota = pairs.get(ImageConstant.QUOTA_IMAGE_NUM).getValue();
        long imageSizeQuota = pairs.get(ImageConstant.QUOTA_IMAGE_SIZE).getValue();
        long imageNumUsed = new ImageQuotaUtil().getUsedImageNum(resourceTargetOwnerAccountUuid);
        long imageSizeUsed = new ImageQuotaUtil().getUsedImageSize(resourceTargetOwnerAccountUuid);

        ImageVO image = dbf.getEntityManager().find(ImageVO.class, msg.getImageUuid());
        long imageNumAsked = 1;
        long imageSizeAsked = image.getSize();

        QuotaUtil.QuotaCompareInfo quotaCompareInfo;
        {
            quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
            quotaCompareInfo.currentAccountUuid = currentAccountUuid;
            quotaCompareInfo.resourceTargetOwnerAccountUuid = resourceTargetOwnerAccountUuid;
            quotaCompareInfo.quotaName = ImageConstant.QUOTA_IMAGE_NUM;
            quotaCompareInfo.quotaValue = imageNumQuota;
            quotaCompareInfo.currentUsed = imageNumUsed;
            quotaCompareInfo.request = imageNumAsked;
            new QuotaUtil().CheckQuota(quotaCompareInfo);
        }

        {
            quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
            quotaCompareInfo.currentAccountUuid = currentAccountUuid;
            quotaCompareInfo.resourceTargetOwnerAccountUuid = resourceTargetOwnerAccountUuid;
            quotaCompareInfo.quotaName = ImageConstant.QUOTA_IMAGE_SIZE;
            quotaCompareInfo.quotaValue = imageSizeQuota;
            quotaCompareInfo.currentUsed = imageSizeUsed;
            quotaCompareInfo.request = imageSizeAsked;
            new QuotaUtil().CheckQuota(quotaCompareInfo);
        }*/
    }

    @Transactional(readOnly = true)
    private void check(APICreateTunnelMsg msg, Map<String, Quota.QuotaPair> pairs) {
       /* String currentAccountUuid = msg.getSession().getAccountUuid();
        String resourceTargetOwnerAccountUuid = msg.getSession().getAccountUuid();
        long imageNumQuota = pairs.get(ImageConstant.QUOTA_IMAGE_NUM).getValue();
        long imageNumUsed = new ImageQuotaUtil().getUsedImageNum(resourceTargetOwnerAccountUuid);
        long imageNumAsked = 1;

        QuotaUtil.QuotaCompareInfo quotaCompareInfo;
        {
            quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
            quotaCompareInfo.currentAccountUuid = currentAccountUuid;
            quotaCompareInfo.resourceTargetOwnerAccountUuid = resourceTargetOwnerAccountUuid;
            quotaCompareInfo.quotaName = ImageConstant.QUOTA_IMAGE_NUM;
            quotaCompareInfo.quotaValue = imageNumQuota;
            quotaCompareInfo.currentUsed = imageNumUsed;
            quotaCompareInfo.request = imageNumAsked;
            new QuotaUtil().CheckQuota(quotaCompareInfo);
        }
        new ImageQuotaUtil().checkImageSizeQuotaUseHttpHead(msg, pairs);*/
    }

    @Transactional(readOnly = true)
    private void check(APICreateTunnelManualMsg msg, Map<String, Quota.QuotaPair> pairs) {
       /* String currentAccountUuid = msg.getSession().getAccountUuid();
        String resourceTargetOwnerAccountUuid = msg.getSession().getAccountUuid();
        long imageNumQuota = pairs.get(ImageConstant.QUOTA_IMAGE_NUM).getValue();
        long imageNumUsed = new ImageQuotaUtil().getUsedImageNum(resourceTargetOwnerAccountUuid);
        long imageNumAsked = 1;

        QuotaUtil.QuotaCompareInfo quotaCompareInfo;
        {
            quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
            quotaCompareInfo.currentAccountUuid = currentAccountUuid;
            quotaCompareInfo.resourceTargetOwnerAccountUuid = resourceTargetOwnerAccountUuid;
            quotaCompareInfo.quotaName = ImageConstant.QUOTA_IMAGE_NUM;
            quotaCompareInfo.quotaValue = imageNumQuota;
            quotaCompareInfo.currentUsed = imageNumUsed;
            quotaCompareInfo.request = imageNumAsked;
            new QuotaUtil().CheckQuota(quotaCompareInfo);
        }
        new ImageQuotaUtil().checkImageSizeQuotaUseHttpHead(msg, pairs);*/
    }

    @Transactional(readOnly = true)
    private void check(APIUpdateTunnelBandwidthMsg msg, Map<String, Quota.QuotaPair> pairs) {
       /* String currentAccountUuid = msg.getSession().getAccountUuid();
        String resourceTargetOwnerAccountUuid = msg.getSession().getAccountUuid();
        long imageNumQuota = pairs.get(ImageConstant.QUOTA_IMAGE_NUM).getValue();
        long imageNumUsed = new ImageQuotaUtil().getUsedImageNum(resourceTargetOwnerAccountUuid);
        long imageNumAsked = 1;

        QuotaUtil.QuotaCompareInfo quotaCompareInfo;
        {
            quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
            quotaCompareInfo.currentAccountUuid = currentAccountUuid;
            quotaCompareInfo.resourceTargetOwnerAccountUuid = resourceTargetOwnerAccountUuid;
            quotaCompareInfo.quotaName = ImageConstant.QUOTA_IMAGE_NUM;
            quotaCompareInfo.quotaValue = imageNumQuota;
            quotaCompareInfo.currentUsed = imageNumUsed;
            quotaCompareInfo.request = imageNumAsked;
            new QuotaUtil().CheckQuota(quotaCompareInfo);
        }
        new ImageQuotaUtil().checkImageSizeQuotaUseHttpHead(msg, pairs);*/
    }
}
