package com.syscxp.tunnel.quota;

import com.syscxp.header.tunnel.tunnel.InterfaceVO;
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

import javax.persistence.TypedQuery;

/**
 * Created by miao on 16-10-9.
 */
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
        public long interfaceBandwidth;
    }

    public class TunnelQuota {
        public long tunnelNum;
        public long tunnelBandwidth;
    }

    @Transactional(readOnly = true)
    public InterfaceQuota getUsedInterface(String accountUUid) {
        InterfaceQuota quota = new InterfaceQuota();

        quota.interfaceNum = getUsedInterfaceBandwidth(accountUUid);
        quota.interfaceBandwidth = getUsedInterfaceNum(accountUUid);

        return quota;
    }

    @Transactional(readOnly = true)
    public long getUsedInterfaceNum(String accountUuid) {
        String sql = "select count(image) " +
                " from ImageVO image, AccountResourceRefVO ref " +
                " where image.uuid = ref.resourceUuid " +
                " and ref.accountUuid = :auuid " +
                " and ref.resourceType = :rtype ";
        TypedQuery<Long> q = dbf.getEntityManager().createQuery(sql, Long.class);
        q.setParameter("auuid", accountUuid);
        q.setParameter("rtype", InterfaceVO.class.getSimpleName());
        Long imageNum = q.getSingleResult();
        imageNum = imageNum == null ? 0 : imageNum;
        return imageNum;
    }

    @Transactional(readOnly = true)
    public long getUsedInterfaceBandwidth(String accountUuid) {
        String sql = "select sum(image.actualSize) " +
                " from ImageVO image ,AccountResourceRefVO ref " +
                " where image.uuid = ref.resourceUuid " +
                " and ref.accountUuid = :auuid " +
                " and ref.resourceType = :rtype ";
        TypedQuery<Long> q = dbf.getEntityManager().createQuery(sql, Long.class);
        q.setParameter("auuid", accountUuid);
        q.setParameter("rtype", InterfaceVO.class.getSimpleName());
        Long imageSize = q.getSingleResult();
        imageSize = imageSize == null ? 0 : imageSize;
        return imageSize;
    }

   /* @BypassWhenUnitTest
    public void checkImageSizeQuotaUseHttpHead(APIAddImageMsg msg, Map<String, Quota.QuotaPair> pairs) {
        long imageSizeQuota = pairs.get(ImageConstant.QUOTA_IMAGE_SIZE).getValue();
        long imageSizeUsed = new ImageQuotaUtil().getUsedImageSize(msg.getSession().getAccountUuid());
        long imageSizeAsked = getLocalImageSizeOnBackupStorage(msg);
        if ((imageSizeQuota == 0) || (imageSizeUsed + imageSizeAsked > imageSizeQuota)) {
            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.QUOTA_EXCEEDING,
                    String.format("quota exceeding. The account[uuid: %s] exceeds a quota[name: %s, value: %s]",
                            msg.getSession().getAccountUuid(), ImageConstant.QUOTA_IMAGE_SIZE, imageSizeQuota)
            ));
        }
    }*/


   /* public long getLocalImageSizeOnBackupStorage(APIAddImageMsg msg) {
        long imageSizeAsked = 0;
        final String url = msg.getUrl().trim();
        if (url.startsWith("file:///")) {
            GetLocalFileSizeOnBackupStorageMsg gmsg = new GetLocalFileSizeOnBackupStorageMsg();
            String bsUuid = msg.getBackupStorageUuids().get(0);
            gmsg.setBackupStorageUuid(bsUuid);
            gmsg.setUrl(url.split("://")[1]);
            bus.makeTargetServiceIdByResourceUuid(gmsg, BackupStorageConstant.SERVICE_ID, bsUuid);
            GetLocalFileSizeOnBackupStorageReply reply = (GetLocalFileSizeOnBackupStorageReply) bus.call(gmsg);
            if (!reply.isSuccess()) {
                logger.warn(String.format("cannot get image. The image url : %s. description: %s.name: %s",
                        url, msg.getDescription(), msg.getName()));
                throw new OperationFailureException(reply.getError());
            } else {
                imageSizeAsked = reply.getSize();
            }
        } else if (url.startsWith("http") || url.startsWith("https")) {
            String len = null;
            HttpHeaders header = restf.getRESTTemplate().headForHeaders(url);
            try {
                len = header.getFirst("Content-Length");
            } catch (Exception e) {
                logger.warn(String.format("cannot get image.  The image url : %s. description: %s.name: %s",
                        url, msg.getDescription(), msg.getName()));
            }
            imageSizeAsked = len == null ? 0 : Long.valueOf(len);
        }
        return imageSizeAsked;
    }*/
}