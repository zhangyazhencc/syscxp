package com.syscxp.vpn.vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigUpdateExtensionPoint;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.message.Message;
import com.syscxp.header.query.QueryOp;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.SyncHttpCallHandler;
import com.syscxp.header.vpn.VpnAgentCommand;
import com.syscxp.header.vpn.VpnAgentResponse.*;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.vpn.header.host.APIDeleteVpnHostEvent;
import com.syscxp.vpn.header.host.HostStatus;
import com.syscxp.vpn.header.host.VpnHostVO;
import com.syscxp.vpn.header.vpn.*;
import com.syscxp.vpn.vpn.VpnCommands.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.core.Platform.operr;

@Component
public class VpnManagerImpl extends AbstractService implements VpnManager, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(VpnManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ThreadFacade thdf;

    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateVpnMsg) {
            handle((APICreateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnMsg) {
            handle((APIUpdateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnBandwidthMsg) {
            handle((APIUpdateVpnBandwidthMsg) msg);
        } else if (msg instanceof APIDeleteVpnMsg) {
            handle((APIDeleteVpnMsg) msg);
        } else if (msg instanceof APICreateVpnInterfaceMsg) {
            handle((APICreateVpnInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteVpnInterfaceMsg) {
            handle((APIDeleteVpnInterfaceMsg) msg);
        } else if (msg instanceof APICreateVpnRouteMsg) {
            handle((APICreateVpnRouteMsg) msg);
        } else if (msg instanceof APIDeleteVpnRouteMsg) {
            handle((APIDeleteVpnRouteMsg) msg);
        } else if (msg instanceof APIGetVpnMsg) {
            handle((APIGetVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnStateMsg) {
            handle((APIUpdateVpnStateMsg) msg);
        } else if (msg instanceof APIUpdateVpnCidrMsg) {
            handle((APIUpdateVpnCidrMsg) msg);
        } else if (msg instanceof APIUpdateVpnExpireDateMsg) {
            handle((APIUpdateVpnExpireDateMsg) msg);
        } else if (msg instanceof APIDownloadCertificateMsg) {
            handle((APIDownloadCertificateMsg) msg);
        } else if (msg instanceof APIResetCertificateMsg) {
            handle((APIResetCertificateMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIResetCertificateMsg msg) {
        final APIResetCertificateEvent evt = new APIResetCertificateEvent(msg.getId());

        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        vpn.setSid(Platform.getUuid());
        vpn.setCertKey(Platform.getUuid());

        ResetCertificateCmd cmd = ResetCertificateCmd.valueOf(vpn);

        new VpnRESTCaller().sendCommand(VpnConstant.RESET_CERTIFICATE_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                evt.setInventory(VpnInventory.valueOf(dbf.updateAndRefresh(vpn)));
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
            }
        });

        bus.publish(evt);

    }

    private void handle(APIDownloadCertificateMsg msg) {
        final APIDownloadCertificateEvent evt = new APIDownloadCertificateEvent(msg.getId());
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        DownloadCertificateCmd cmd = DownloadCertificateCmd.valueOf(vpn);
        try {
            DownloadCertificateResponse rsp = restf.syncJsonPost(VpnConstant.DOWNLOAD_CERTIFICATE_PATH, cmd,
                    DownloadCertificateResponse.class);
            CertificateInventory inv = new CertificateInventory();
            inv.setCaCert(rsp.getCaCert());
            inv.setClientCert(rsp.getClientCert());
            inv.setClientConf(rsp.getClientConf());
            inv.setClientKey(rsp.getClientKey());

            evt.setInventory(inv);
        } catch (Exception e) {
            evt.setError(operr("failed to post to %s", VpnConstant.DOWNLOAD_CERTIFICATE_PATH));
        }
        bus.publish(evt);

    }

    private void handle(APIGetVpnMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        VpnInventory inventory = VpnInventory.valueOf(vpn);

        APIGetVpnReply reply = new APIGetVpnReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);
    }

    private void handle(APICreateVpnMsg msg) {
        APICreateVpnEvent evt = new APICreateVpnEvent(msg.getId());
        VpnVO vpn = new VpnVO();
        vpn.setUuid(Platform.getUuid());
        vpn.setSid(Platform.getUuid());
        vpn.setCertKey(Platform.getUuid());
        vpn.setMaxModifies(CoreGlobalProperty.VPN_MAX_MOTIFIES);
        vpn.setAccountUuid(msg.getAccountUuid());
        vpn.setDescription(msg.getDescription());
        vpn.setName(msg.getName());
        vpn.setVpnCidr(msg.getVpnCidr());
        vpn.setBandwidth(msg.getBandwidth());
        vpn.setEndpointUuid(msg.getEndpointUuid());
        vpn.setPayment(Payment.UNPAID);
        vpn.setState(VpnState.Enabled);
        vpn.setStatus(VpnStatus.Connecting);
        vpn.setDuration(msg.getDuration());
        vpn.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(msg.getDuration())));
        vpn.setPort(generatePort(msg.getHostUuid()));
        vpn.setHostUuid(msg.getHostUuid());
        vpn.setVpnHost(dbf.findByUuid(msg.getHostUuid(), VpnHostVO.class));

        VpnInterfaceVO vpnIface = new VpnInterfaceVO();
        vpnIface.setUuid(Platform.getUuid());
        vpnIface.setName(msg.getInterfaceName());
        vpnIface.setVpnUuid(vpn.getUuid());
        vpnIface.setLocalIp(msg.getLocalIp());
        vpnIface.setNetmask(msg.getNetmask());
        vpnIface.setVlan(msg.getVlan());
        vpnIface.setNetworkUuid(msg.getNetworkUuid());
        vpn.setVpnInterfaces(CollectionDSL.list(vpnIface));

        vpn = dbf.persistAndRefresh(vpn);
        dbf.persistAndRefresh(vpnIface);

        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        orderMsg.setProductName(vpn.getName());
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        orderMsg.setDuration(vpn.getDuration());
        orderMsg.setProductDescription(vpn.getDescription());
        orderMsg.setUnits(msg.getProductPriceUnits());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());

        if (!createOrder(orderMsg)) {
            evt.setError(errf.stringToOperationError("付款失败"));
            vpn = dbf.reload(vpn);
            evt.setInventory(VpnInventory.valueOf(dbf.reload(vpn)));
            bus.publish(evt);
            return;
        }

        vpn.setPayment(Payment.PAID);
        final VpnVO vo = dbf.updateAndRefresh(vpn);

        CreateVpnCmd cmd = CreateVpnCmd.valueOf(vo);

        new VpnRESTCaller().sendCommand(VpnConstant.INIT_VPN_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                vo.setStatus(VpnStatus.Connected);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                vo.setStatus(VpnStatus.Disconnected);
                evt.setError(errorCode);
            }
        });

        dbf.updateAndRefresh(vo);
        evt.setInventory(VpnInventory.valueOf(vo));
        bus.publish(evt);

    }

    private boolean createOrder(APICreateOrderMsg orderMsg) {
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        APIReply reply;
        try {
            reply = new VpnRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);
        } catch (Exception e) {
            return false;
        }
        return reply.isSuccess();
    }

    // 生成端口号，规则：从30000开始，当前主机存在的vpn服务端口号+1
    private Integer generatePort(String hostUuid) {
        Q q = Q.New(VpnVO.class).eq(VpnVO_.hostUuid, hostUuid)
                .orderBy(VpnVO_.port, SimpleQuery.Od.DESC).limit(1).select(VpnVO_.port);
        boolean flag = q.isExists();
        if (!flag)
            return 30000;
        return (Integer) q.findValue() + 1;
    }

    @Transactional
    public void handle(APIUpdateVpnExpireDateMsg msg) {
        APIUpdateVpnExpireDateEvent evt = new APIUpdateVpnExpireDateEvent(msg.getId());

        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        LocalDateTime newTime = vpn.getExpireDate().toLocalDateTime();
        boolean success = false;
        switch (msg.getType()) {
            case RENEW:
                APICreateRenewOrderMsg renewOrderMsg = new APICreateRenewOrderMsg();
                renewOrderMsg.setProductUuid(vpn.getUuid());
                renewOrderMsg.setDuration(msg.getDuration());
                renewOrderMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
                renewOrderMsg.setAccountUuid(msg.getAccountUuid());
                renewOrderMsg.setOpAccountUuid(msg.getOpAccountUuid());
                renewOrderMsg.setStartTime(vpn.getCreateDate());
                renewOrderMsg.setExpiredTime(vpn.getExpireDate());
                success = createOrder(renewOrderMsg);
                newTime = newTime.plusMonths(msg.getDuration());
                break;
            case SLA_COMPENSATION:
                APICreateSLACompensationOrderMsg slaCompensationOrderMsg =
                        new APICreateSLACompensationOrderMsg();
                slaCompensationOrderMsg.setProductUuid(vpn.getUuid());
                slaCompensationOrderMsg.setProductName(vpn.getName());
                slaCompensationOrderMsg.setProductDescription(vpn.getDescription());
                slaCompensationOrderMsg.setProductType(ProductType.VPN);
                slaCompensationOrderMsg.setDuration(msg.getDuration());
                slaCompensationOrderMsg.setAccountUuid(msg.getAccountUuid());
                slaCompensationOrderMsg.setOpAccountUuid(msg.getOpAccountUuid());
                slaCompensationOrderMsg.setStartTime(vpn.getCreateDate());
                slaCompensationOrderMsg.setExpiredTime(vpn.getExpireDate());
                success = createOrder(slaCompensationOrderMsg);
                newTime = newTime.plusDays(msg.getDuration());
                break;
        }
        if (success) {
            vpn.setExpireDate(Timestamp.valueOf(newTime));
            vpn = dbf.getEntityManager().merge(vpn);
        } else {
            evt.setError(errf.stringToOperationError("订单操作失败"));
        }

        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnBandwidthMsg msg) {
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());

        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        VpnMotifyRecordVO record = new VpnMotifyRecordVO();
        record.setUuid(Platform.getUuid());
        record.setVpnUuid(vpn.getUuid());
        record.setOpAccountUuid(msg.getOpAccountUuid());
        record.setMotifyType(
                msg.getBandwidth() > vpn.getBandwidth() ? MotifyType.UPGRADE : MotifyType.DEMOTION);

        vpn.setBandwidth(msg.getBandwidth());

        APICreateModifyOrderMsg orderMsg = new APICreateModifyOrderMsg();
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductName(vpn.getName());
        orderMsg.setProductDescription(vpn.getDescription());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        orderMsg.setUnits(msg.getProductPriceUnits());
        orderMsg.setStartTime(vpn.getCreateDate());
        orderMsg.setExpiredTime(vpn.getExpireDate());

        if (!createOrder(orderMsg)) {
            evt.setError(errf.stringToOperationError("订单操作失败"));
            bus.publish(evt);
            return;
        }


        final VpnVO vo = dbf.getEntityManager().merge(vpn);
        dbf.getEntityManager().persist(record);

//        UpdateVpnBandWidthCmd cmd = UpdateVpnBandWidthCmd.valueOf(vo);
//
//        new VpnRESTCaller().sendCommand(VpnConstant.UPDATE_VPN_BANDWIDTH_PATH, cmd, new Completion(evt) {
//            @Override
//            public void success() {
//
//            }
//
//            @Override
//            public void fail(ErrorCode errorCode) {
//                evt.setError(errorCode);
//            }
//        });

        evt.setInventory(VpnInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnMsg msg) {
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        boolean update = false;
        if (!StringUtils.isEmpty(msg.getName())) {
            vpn.setName(msg.getName());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getDescription())) {
            vpn.setDescription(msg.getDescription());
            update = true;
        }
        if (msg.getMaxModifies() != null) {
            vpn.setMaxModifies(msg.getMaxModifies());
            update = true;
        }
        if (update)
            vpn = dbf.getEntityManager().merge(vpn);
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnStateMsg msg) {
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        VpnState next = msg.getState();
        if (vpn.getState() == next) {
            evt.setInventory(VpnInventory.valueOf(vpn));
            bus.publish(evt);
            return;
        }
        vpn.setState(next);
        VpnAgentCommand cmd = null;
        String path = null;
        switch (next) {
            case Enabled:
                cmd = CreateVpnCmd.valueOf(vpn);
                path = VpnConstant.START_VPN_PATH;
                break;
            case Disabled:
                cmd = DeleteVpnCmd.valueOf(vpn);
                path = VpnConstant.STOP_VPN_PATH;
                break;
        }

        new VpnRESTCaller().sendCommand(path, cmd, new Completion(evt) {
            @Override
            public void success() {
                dbf.getEntityManager().merge(vpn);
                evt.setInventory(VpnInventory.valueOf(vpn));
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
            }
        });

        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnCidrMsg msg) {
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        vpn.setVpnCidr(msg.getVpnCidr());

        UpdateVpnCidrCmd cmd = UpdateVpnCidrCmd.valueOf(vpn);
        dbf.getEntityManager().merge(vpn);

//        new VpnRESTCaller().sendCommand(VpnConstant.UPDATE_VPN_CIDR_PATH, cmd, new Completion(evt) {
//            @Override
//            public void success() {
//                dbf.getEntityManager().merge(vpn);
//                evt.setInventory(VpnInventory.valueOf(vpn));
//            }
//
//            @Override
//            public void fail(ErrorCode errorCode) {
//                evt.setError(errorCode);
//            }
//        });

        bus.publish(evt);
    }


    @Transactional
    public void handle(APIDeleteVpnMsg msg) {
        APIDeleteVpnHostEvent evt = new APIDeleteVpnHostEvent(msg.getId());
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setProductName(vpn.getName());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        orderMsg.setStartTime(vpn.getCreateDate());
        orderMsg.setExpiredTime(vpn.getExpireDate());

        if (!createOrder(orderMsg)) {
            evt.setError(errf.stringToOperationError("退订失败"));
            bus.publish(evt);
            return;
        }

        DeleteVpnCmd cmd = DeleteVpnCmd.valueOf(vpn);
        new VpnRESTCaller().sendCommand(VpnConstant.DELETE_VPN_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                dbf.remove(vpn);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
            }
        });

        bus.publish(evt);
    }

    @Transactional
    public void handle(APICreateVpnInterfaceMsg msg) {
        APICreateVpnInterfaceEvent evt = new APICreateVpnInterfaceEvent(msg.getId());
        VpnVO vpn = dbf.findByUuid(msg.getVpnUuid(), VpnVO.class);

        VpnInterfaceVO iface = new VpnInterfaceVO();
        iface.setUuid(Platform.getUuid());
        iface.setVpnUuid(msg.getVpnUuid());
        iface.setName(msg.getName());
        iface.setVlan(msg.getVlan());
        iface.setNetworkUuid(msg.getTunnelUuid());
        iface.setLocalIp(msg.getLocalIP());
        iface.setNetmask(msg.getNetmask());

        VpnInterfaceCmd cmd = VpnInterfaceCmd.valueOf(vpn.getVpnHost().getManageIp(), iface);

        new VpnRESTCaller().sendCommand(VpnConstant.ADD_VPN_INTERFACE_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                evt.setInventory(VpnInterfaceInventory.valueOf(dbf.persistAndRefresh(iface)));
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
            }
        });

        bus.publish(evt);
    }

    @Transactional
    public void handle(APIDeleteVpnInterfaceMsg msg) {
        APIDeleteVpnInterfaceEvent evt = new APIDeleteVpnInterfaceEvent(msg.getId());
        VpnInterfaceVO iface = dbf.findByUuid(msg.getUuid(), VpnInterfaceVO.class);

        VpnVO vpn = dbf.findByUuid(iface.getVpnUuid(), VpnVO.class);
        VpnInterfaceCmd cmd = VpnInterfaceCmd.valueOf(vpn.getVpnHost().getManageIp(), iface);

        new VpnRESTCaller().sendCommand(VpnConstant.DELETE_VPN_INTERFACE_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                dbf.remove(iface);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
            }
        });


        bus.publish(evt);
    }

    @Transactional
    public void handle(APICreateVpnRouteMsg msg) {
        APICreateVpnRouteEvent evt = new APICreateVpnRouteEvent(msg.getId());

        VpnRouteVO route = new VpnRouteVO();
        route.setVpnUuid(msg.getVpnUuid());
        route.setRouteType(msg.getRouteType());
        route.setNextInterface(msg.getNextIface());
        route.setTargetCidr(msg.getTargetCidr());

        VpnVO vpn = dbf.findByUuid(msg.getVpnUuid(), VpnVO.class);
        VpnRouteCmd cmd = VpnRouteCmd.valueOf(vpn.getVpnHost().getManageIp(), route);

        new VpnRESTCaller().sendCommand(VpnConstant.ADD_VPN_ROUTE_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                evt.setInventory(VpnRouteInventory.valueOf(dbf.persistAndRefresh(route)));
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setInventory(VpnRouteInventory.valueOf(dbf.persistAndRefresh(route)));
                evt.setError(errorCode);
            }
        });

        bus.publish(evt);
    }

    @Transactional
    public void handle(APIDeleteVpnRouteMsg msg) {
        APIDeleteVpnRouteEvent evt = new APIDeleteVpnRouteEvent(msg.getId());
        VpnRouteVO route = dbf.findByUuid(msg.getUuid(), VpnRouteVO.class);

        VpnVO vpn = dbf.findByUuid(route.getVpnUuid(), VpnVO.class);
        VpnRouteCmd cmd = VpnRouteCmd.valueOf(vpn.getVpnHost().getManageIp(), route);

        new VpnRESTCaller().sendCommand(VpnConstant.ADD_VPN_ROUTE_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                dbf.remove(route);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
            }
        });
        bus.publish(evt);
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }


    public String getId() {
        return bus.makeLocalServiceId(VpnConstant.SERVICE_ID);
    }

    private Future<Void> hostCheckThread;
    private int vpnStatusCheckWorkerInterval;
    private List<String> disconnectedVpn = new ArrayList<>();

    private void startFailureHostCopingThread() {
        hostCheckThread = thdf.submitPeriodicTask(new VpnStatusCheckWorker());
        logger.debug(String
                .format("security group failureHostCopingThread starts[failureHostWorkerInterval: %ss]",
                        vpnStatusCheckWorkerInterval));
    }

    private void restartFailureHostCopingThread() {
        if (hostCheckThread != null) {
            hostCheckThread.cancel(true);
        }
        startFailureHostCopingThread();
    }

    private void prepareGlobalConfig() {
        vpnStatusCheckWorkerInterval =
                VpnGlobalConfig.VPN_STATUS_CHECK_WORKER_INTERVAL.value(Integer.class);

        GlobalConfigUpdateExtensionPoint onUpdate = new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                if (VpnGlobalConfig.VPN_STATUS_CHECK_WORKER_INTERVAL.isMe(newConfig)) {
                    vpnStatusCheckWorkerInterval = newConfig.value(Integer.class);
                    restartFailureHostCopingThread();
                }
            }
        };

        VpnGlobalConfig.VPN_STATUS_CHECK_WORKER_INTERVAL.installUpdateExtension(onUpdate);
    }

    private class VpnStatusCheckWorker implements PeriodicTask {
        @Transactional
        private List<VpnVO> getAllVpns() {
            return Q.New(VpnVO.class)
                    .eq(VpnVO_.state, VpnState.Enabled)
                    .notEq(VpnVO_.status, VpnStatus.Connecting)
                    .list();
        }

        private void updateVpnStatus(List<String> vpnUuids) {
            logger.debug(String.format("update vpn status Disconnected, uuid in %s", JSONObjectUtil.toJsonString(vpnUuids)));
            UpdateQuery.New(VpnVO.class).in(VpnVO_.uuid, vpnUuids)
                    .set(VpnVO_.status, VpnStatus.Disconnected).update();
        }

        private boolean reconnectVpn(VpnVO vo) {
            if (vo.getVpnHost().getStatus() == HostStatus.Disconnected) {
                return false;
            }
            //Todo VPN重连
            ReconnectVpnCmd cmd = ReconnectVpnCmd.valueOf(vo);
            TaskResult result;
            try {
                result = new VpnRESTCaller()
                        .syncPostForResult(VpnConstant.RECONNECT_VPN_PATH, cmd);
                if (result.isSuccess()) {
                    UpdateQuery.New(VpnVO.class).eq(VpnVO_.uuid, vo.getUuid())
                            .set(VpnVO_.status, VpnStatus.Connected).update();
                }
            } catch (Exception e) {
                return false;
            }
            return result.isSuccess();
        }

        @Override
        public void run() {
            logger.debug("start check vpn status");
            disconnectedVpn.clear();
            List<VpnVO> vos = getAllVpns();
            if (vos.isEmpty()) {
                return;
            }
            for (VpnVO vo : vos) {
                if (vo.getStatus() == VpnStatus.Disconnected) {
                    disconnectedVpn.add(vo.getUuid());
                    continue;
                }
                CheckVpnStatusCmd cmd = CheckVpnStatusCmd.valueOf(vo);
                RunStatus status =  new VpnRESTCaller().checkStatus(VpnConstant.CHECK_VPN_STATUS_PATH, cmd);

                if (status == RunStatus.UP && vo.getStatus() == VpnStatus.Connected)
                        continue;
                if (!reconnectVpn(vo)) {
                    disconnectedVpn.add(vo.getUuid());
                }
            }
            updateVpnStatus(disconnectedVpn);
        }

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getInterval() {
            return vpnStatusCheckWorkerInterval;
        }

        @Override
        public String getName() {
            return VpnStatusCheckWorker.class.getName();
        }
    }


    public boolean start() {
        prepareGlobalConfig();
        restf.registerSyncHttpCallHandler(OrderType.BUY.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back.", CoreGlobalProperty.BILLING_SERVER_URL));
                        VpnVO vpn = updateVpnFromOrder(cmd);
                        if (vpn != null && vpn.getStatus() == VpnStatus.Connecting)
                            new VpnRESTCaller().syncPostForResult(VpnConstant.INIT_VPN_PATH,
                                    CreateVpnCmd.valueOf(vpn));

                        return null;
                    }
                });
        restf.registerSyncHttpCallHandler(OrderType.UN_SUBCRIBE.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back.", CoreGlobalProperty.BILLING_SERVER_URL));

                        VpnVO vpn = dbf.findByUuid(cmd.getPorductUuid(), VpnVO.class);
                        if (vpn != null) {
                            dbf.remove(vpn);
                            new VpnRESTCaller().syncPostForResult(VpnConstant.DELETE_VPN_PATH,
                                    CreateVpnCmd.valueOf(vpn));
                        }
                        return null;
                    }
                });

        restf.registerSyncHttpCallHandler(OrderType.RENEW.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back.", CoreGlobalProperty.BILLING_SERVER_URL));

                        updateVpnFromOrder(cmd);
                        return null;
                    }
                });
        restf.registerSyncHttpCallHandler(OrderType.SLA_COMPENSATION.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back.", CoreGlobalProperty.BILLING_SERVER_URL));
                        updateVpnFromOrder(cmd);
                        return null;
                    }
                });
        restf.registerSyncHttpCallHandler(OrderType.MODIFY.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back.", CoreGlobalProperty.BILLING_SERVER_URL));
                        updateVpnFromOrder(cmd);
                        return null;
                    }
                });

        return true;
    }

    private VpnVO updateVpnFromOrder(OrderCallbackCmd cmd) {
        VpnVO vpn = dbf.findByUuid(cmd.getPorductUuid(), VpnVO.class);

        boolean update = false;
        if (vpn.getPayment() == Payment.UNPAID) {
            vpn.setPayment(Payment.PAID);
            update = true;
        }
        if (cmd.getExpireDate() != null) {
            vpn.setExpireDate(cmd.getExpireDate());
            update = true;
        }
        return update ? dbf.updateAndRefresh(vpn) : null;
    }

    public boolean stop() {
        return true;
    }

    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateVpnMsg) {
            validate((APICreateVpnMsg) msg);
        } else if (msg instanceof APIQueryVpnMsg) {
            validate((APIQueryVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnMsg) {
            validate((APIUpdateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnBandwidthMsg) {
            validate((APIUpdateVpnBandwidthMsg) msg);
        } else if (msg instanceof APIDownloadCertificateMsg) {
            validate((APIDownloadCertificateMsg) msg);
        }
        return msg;
    }

    private void validate(APIDownloadCertificateMsg msg) {
        Q q = Q.New(VpnVO.class)
                .eq(VpnVO_.uuid, msg.getUuid())
                .eq(VpnVO_.sid, msg.getSid())
                .eq(VpnVO_.certKey, msg.getKey());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(
                    argerr("Vpn[uuid:%s, sid:%s, key:%s]验证不通过.", msg.getUuid(), msg.getSid(),
                            msg.getKey()));
        }
    }

    private void validate(APIUpdateVpnBandwidthMsg msg) {
        LocalDateTime dateTime =
                LocalDate.now().withDayOfMonth(LocalDate.MIN.getDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(VpnMotifyRecordVO.class).eq(VpnMotifyRecordVO_.vpnUuid, msg.getUuid())
                .gte(VpnMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime))
                .lt(VpnMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime.plusMonths(1))).count();
        Integer maxModifies =
                Q.New(VpnVO.class).eq(VpnVO_.uuid, msg.getUuid()).select(VpnVO_.maxModifies)
                        .findValue();

        if (times >= maxModifies) {
            throw new OperationFailureException(
                    operr("The Vpn[uuid:%s] has motified %s times.", msg.getUuid(), times));
        }
    }


    private void validate(APIUpdateVpnMsg msg) {
        Q q = Q.New(VpnVO.class).eq(VpnVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(
                    argerr("The Vpn[name:%s] is already exist.", msg.getName()));
    }

    private void validate(APIQueryVpnMsg msg) {
        if (msg.getSession().getType() != AccountType.SystemAdmin) {
            msg.addQueryCondition(VpnVO_.accountUuid.toString(), QueryOp.EQ,
                    msg.getSession().getAccountUuid());
        }
    }

    private void validate(APICreateVpnMsg msg) {
        if (msg.getSession().getType() == AccountType.Normal && StringUtils
                .isEmpty(msg.getAccountUuid())) {
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] is not a admin or proxy.",
                            msg.getSession().getAccountUuid()));
        }
        Q q = Q.New(VpnVO.class).eq(VpnVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(
                    argerr("The Vpn[name:%s] is already exist.", msg.getName()));

        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(msg.getProductPriceUnits());
        APIReply rsp = new VpnRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!rsp.isSuccess())
            throw new ApiMessageInterceptionException(
                    argerr("查询价格失败.", msg.getAccountUuid()));
        APIGetProductPriceReply reply = (APIGetProductPriceReply) rsp;
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
    }
}
