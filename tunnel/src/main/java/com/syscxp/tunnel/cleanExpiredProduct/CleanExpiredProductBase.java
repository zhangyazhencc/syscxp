package com.syscxp.tunnel.cleanExpiredProduct;

import com.syscxp.core.Platform;
import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigUpdateExtensionPoint;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.identity.IdentityGlobalProperty;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.account.APIGetAccountExpiredCleanMsg;
import com.syscxp.header.account.APIGetAccountExpiredCleanReply;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.tunnel.edgeLine.EdgeLineVO;
import com.syscxp.header.tunnel.edgeLine.EdgeLineVO_;
import com.syscxp.header.tunnel.network.L3EndpointVO;
import com.syscxp.header.tunnel.network.L3EndpointVO_;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.identity.TunnelGlobalConfig;
import com.syscxp.tunnel.tunnel.TunnelBase;
import com.syscxp.tunnel.tunnel.TunnelJobAndTaskBase;
import com.syscxp.tunnel.tunnel.TunnelManagerImpl;
import com.syscxp.tunnel.tunnel.job.DeleteRenewVOAfterDeleteResourceJob;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Create by DCY on 2018/3/22
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class CleanExpiredProductBase {
    private static final CLogger logger = Utils.getLogger(CleanExpiredProductBase.class);

    @Autowired
    private ThreadFacade thdf;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private JobQueueFacade jobf;

    @Autowired
    private RESTFacade restf;

    @Autowired
    private TunnelManagerImpl tunnelManager;

    private Future<Void> cleanExpiredProductThread = null;
    private int cleanExpiredProductInterval;
    private int expiredProductCloseTime;
    private int expiredProductDeleteTime;

    private void startCleanExpiredProduct() {
        cleanExpiredProductInterval = TunnelGlobalConfig.CLEAN_EXPIRED_PRODUCT_INTERVAL.value(Integer.class);
        expiredProductCloseTime = TunnelGlobalConfig.EXPIRED_PRODUCT_CLOSE_TIME.value(Integer.class);
        expiredProductDeleteTime = TunnelGlobalConfig.EXPIRED_PRODUCT_DELETE_TIME.value(Integer.class);

        if (cleanExpiredProductThread != null) {
            cleanExpiredProductThread.cancel(true);
        }

        cleanExpiredProductThread = thdf.submitPeriodicTask(new CleanExpiredProductThread(), TimeUnit.SECONDS.toSeconds(3600));
        logger.debug(String
                .format("security group cleanExpiredProductThread starts[cleanExpiredProductInterval: %s hours]", cleanExpiredProductInterval));
    }

    public void restartCleanExpiredProduct() {

        startCleanExpiredProduct();

        TunnelGlobalConfig.CLEAN_EXPIRED_PRODUCT_INTERVAL.installUpdateExtension(new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                logger.debug(String.format("%s change from %s to %s, restart tracker thread",
                        oldConfig.getCanonicalName(), oldConfig.value(), newConfig.value()));
                startCleanExpiredProduct();
            }
        });
        TunnelGlobalConfig.EXPIRED_PRODUCT_CLOSE_TIME.installUpdateExtension(new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                logger.debug(String.format("%s change from %s to %s, restart tracker thread",
                        oldConfig.getCanonicalName(), oldConfig.value(), newConfig.value()));
                startCleanExpiredProduct();
            }
        });
        TunnelGlobalConfig.EXPIRED_PRODUCT_DELETE_TIME.installUpdateExtension(new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                logger.debug(String.format("%s change from %s to %s, restart tracker thread",
                        oldConfig.getCanonicalName(), oldConfig.value(), newConfig.value()));
                startCleanExpiredProduct();
            }
        });
    }

    private class CleanExpiredProductThread implements PeriodicTask {

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getInterval() {
            return TimeUnit.HOURS.toSeconds(cleanExpiredProductInterval);
        }

        @Override
        public String getName() {
            return "clean-expired-product-" + Platform.getManagementServerId();
        }

        private Timestamp getCloseTime(){
            Timestamp time = Timestamp.valueOf(dbf.getCurrentSqlTime().toLocalDateTime().minusDays(expiredProductCloseTime < expiredProductDeleteTime ? expiredProductCloseTime : expiredProductDeleteTime));
            return time;
        }

        /**
         * 过期专线
         * */
        private List<TunnelVO> getTunnels() {
            return Q.New(TunnelVO.class)
                    .lte(TunnelVO_.expireDate, getCloseTime())
                    .list();
        }

        /**
         * 过期物理接口
         * */
        private List<InterfaceVO> getInterfaces() {

            return Q.New(InterfaceVO.class)
                    .lte(InterfaceVO_.expireDate, getCloseTime())
                    .list();
        }

        /**
         * 过期产品是否需要清理
         * */
        private boolean isNeedExpired(String accountUuid){
            APIGetAccountExpiredCleanMsg msg = new APIGetAccountExpiredCleanMsg();
            msg.setUuid(accountUuid);
            String url = URLBuilder.buildUrlFromBase(IdentityGlobalProperty.ACCOUNT_SERVER_URL);
            InnerMessageHelper.setMD5(msg);

            RestAPIResponse restAPIResponse = restf.syncJsonPost(url, RESTApiDecoder.dump(msg), RestAPIResponse.class);
            APIReply reply = (APIReply) RESTApiDecoder.loads(restAPIResponse.getResult());

            if(!reply.isSuccess()){
                //询问account是否需要清理失败
                return false;
            }else{
                APIGetAccountExpiredCleanReply apiGetAccountExpiredCleanReply = reply.castReply();

                return apiGetAccountExpiredCleanReply.isExpiredClean();
            }
        }

        private void deleteInterface(List<InterfaceVO> ifaces, Timestamp close, Timestamp delete) {

            for (InterfaceVO vo : ifaces) {

                if(isNeedExpired(vo.getOwnerAccountUuid()) && vo.getExpireDate() != null){
                    if (vo.getExpireDate().before(delete)) {
                        if (!Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.interfaceUuid, vo.getUuid()).isExists()
                                && !Q.New(EdgeLineVO.class).eq(EdgeLineVO_.interfaceUuid, vo.getUuid()).isExists()
                                && !Q.New(L3EndpointVO.class).eq(L3EndpointVO_.interfaceUuid, vo.getUuid()).isExists()) {
                            dbf.remove(vo);
                            //删除续费表
                            logger.info("删除接口成功，并创建任务：DeleteRenewVOAfterDeleteResourceJob");
                            DeleteRenewVOAfterDeleteResourceJob job = new DeleteRenewVOAfterDeleteResourceJob();
                            job.setAccountUuid(vo.getOwnerAccountUuid());
                            job.setResourceType(vo.getClass().getSimpleName());
                            job.setResourceUuid(vo.getUuid());
                            jobf.execute("删除物理接口-删除续费表", Platform.getManagementServerId(), job);
                        }

                    }else if (vo.getExpireDate().before(close) && vo.getState() == InterfaceState.Unpaid) {
                        dbf.remove(vo);
                    }
                }

            }
        }

        private void deleteTunnel(List<TunnelVO> tunnelVOs, Timestamp close, Timestamp delete){
            TunnelBase tunnelBase = new TunnelBase();

            for (TunnelVO vo : tunnelVOs) {

                if(isNeedExpired(vo.getOwnerAccountUuid())){

                    if (vo.getExpireDate().before(delete)) {
                        vo.setAccountUuid(null);
                        final TunnelVO vo2 = dbf.updateAndRefresh(vo);
                        tunnelManager.doDeleteTunnel(vo2, false, vo.getOwnerAccountUuid(), new ReturnValueCompletion<TunnelInventory>(null) {
                            @Override
                            public void success(TunnelInventory inv) {
                            }

                            @Override
                            public void fail(ErrorCode errorCode) {
                            }
                        });
                    } else if (vo.getExpireDate().before(close)) {
                        if (vo.getState() == TunnelState.Unpaid) {
                            tunnelBase.deleteTunnelDB(vo);
                        } else if (vo.getState() == TunnelState.Enabled) {
                            new TunnelJobAndTaskBase().taskDisableTunnel(vo,new ReturnValueCompletion<TunnelInventory>(null) {
                                @Override
                                public void success(TunnelInventory inv) {
                                }

                                @Override
                                public void fail(ErrorCode errorCode) {
                                }
                            });
                        }
                    }
                }

            }
        }

        @Override
        public void run() {
            if (!TunnelGlobalConfig.EXPIRED_PRODUCT_CLEAN_RUN.value(Boolean.class)){
                return;
            }

            Timestamp closeTime = Timestamp.valueOf(LocalDateTime.now().minusDays(expiredProductCloseTime));
            Timestamp deleteTime = Timestamp.valueOf(LocalDateTime.now().minusDays(expiredProductDeleteTime));

            try {
                List<TunnelVO> tunnelVOs = getTunnels();
                logger.debug("delete expired tunnel.");
                if (!tunnelVOs.isEmpty()){
                    deleteTunnel(tunnelVOs, closeTime, deleteTime);
                }

                List<InterfaceVO> ifaces = getInterfaces();
                logger.debug("delete expired interface.");
                if (!ifaces.isEmpty()){
                    deleteInterface(ifaces, closeTime, deleteTime);
                }


            } catch (Throwable t) {
                logger.warn("unhandled exception", t);
            }
        }

    }
}
