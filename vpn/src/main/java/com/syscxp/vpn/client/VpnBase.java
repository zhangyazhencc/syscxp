package com.syscxp.vpn.client;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.thread.ChainTask;
import com.syscxp.core.thread.SyncTaskChain;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.core.workflow.ShareFlow;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.vpn.VpnAO;
import com.syscxp.header.vpn.l3vpn.L3VpnVO;
import com.syscxp.header.vpn.vpn.VpnConstant;
import com.syscxp.header.vpn.agent.*;
import com.syscxp.header.vpn.host.HostInterfaceVO;
import com.syscxp.header.vpn.host.HostInterfaceVO_;
import com.syscxp.header.vpn.vpn.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.data.SizeUnit;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.vpn.exception.VpnErrors;
import com.syscxp.vpn.client.VpnCommands.*;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Map;

import static com.syscxp.core.Platform.operr;

/**
 * @author wangjie
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class VpnBase extends AbstractVpn {
    private static final CLogger LOGGER = Utils.getLogger(VpnBase.class);

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ThreadFacade thdf;

    private String vpnConfPath;
    private String rateLimitingPath;
    private String vpnPortPath;
    private String vpnServicePath;
    private String startAllPath;
    private String destroyVpnPath;
    private String clientInfoPath;
    private String loginInfoPath;
    private String initVpnPath;
    private String pushCertPath;

    private static final String UP = "UP";
    private static final String DOWN = "DOWN";

    private static final String VPN_START = "start";
    private static final String VPN_RESTART = "restart";
    private static final String VPN_STOP = "stop";
    private static final String VPN_STATUS = "status";

    private static final String RATE_LIMITING = "set";

    public VpnBase(VpnAO self) {
        super(self);

        vpnConfPath = VpnConstant.VPN_CONF_PATH;
        rateLimitingPath = VpnConstant.RATE_LIMITING_PATH;
        vpnPortPath = VpnConstant.VPN_PORT_PATH;
        vpnServicePath = VpnConstant.VPN_SERVICE_PATH;
        startAllPath = VpnConstant.START_ALL_PATH;
        destroyVpnPath = VpnConstant.DESTROY_VPN_PATH;
        clientInfoPath = VpnConstant.CLIENT_INFO_PATH;
        loginInfoPath = VpnConstant.LOGIN_INFO_PATH;
        initVpnPath = VpnConstant.INIT_VPN_PATH;
        pushCertPath = VpnConstant.PUSH_CERT_PATH;

    }

    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleLocalMessage(Message msg) {
        if (msg instanceof InitVpnMsg) {
            handle((InitVpnMsg) msg);
        } else if (msg instanceof StartAllMsg) {
            handle((StartAllMsg) msg);
        } else if (msg instanceof DestroyVpnMsg) {
            handle((DestroyVpnMsg) msg);
        } else if (msg instanceof StopVpnMsg) {
            handle((StopVpnMsg) msg);
        } else if (msg instanceof StartVpnMsg) {
            handle((StartVpnMsg) msg);
        } else if (msg instanceof RateLimitingMsg) {
            handle((RateLimitingMsg) msg);
        } else if (msg instanceof VpnConfMsg) {
            handle((VpnConfMsg) msg);
        } else if (msg instanceof VpnPortMsg) {
            handle((VpnPortMsg) msg);
        } else if (msg instanceof ClientInfoMsg) {
            handle((ClientInfoMsg) msg);
        } else if (msg instanceof LoginInfoMsg) {
            handle((LoginInfoMsg) msg);
        } else if (msg instanceof VpnStatusMsg) {
            handle((VpnStatusMsg) msg);
        } else if (msg instanceof PushCertMsg) {
            handle((PushCertMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(PushCertMsg msg) {

        thdf.chainSubmit(new ChainTask(msg) {
            @Override
            public String getSyncSignature() {
                return String.format("push-cert-%s", self.getUuid());
            }

            @Override
            public void run(SyncTaskChain chain) {
                final PushCertReply reply = new PushCertReply();

                final FlowChain flowChain = FlowChainBuilder.newShareFlowChain();
                flowChain.setName(String.format("push-cert-%s", self.getUuid()));
                flowChain.then(new ShareFlow() {
                    @Override
                    public void setup() {
                        flow(new NoRollbackFlow() {
                            String __name__ = "push-cert";

                            @Override
                            public void run(final FlowTrigger trigger, Map data) {
                                PushCertCmd cmd = new PushCertCmd();
                                cmd.vpnuuid = self.getUuid();
                                cmd.certinfo = getCertInfo();

                                httpCall(pushCertPath, cmd, PushCertRsp.class, new ReturnValueCompletion<PushCertRsp>(trigger) {
                                    @Override
                                    public void success(PushCertRsp ret) {
                                        if (ret.isSuccess()) {
                                            trigger.next();
                                        } else {
                                            trigger.fail(errf.instantiateErrorCode(VpnErrors.PUSH_CERT_ERROR, ret.getError()));
                                        }
                                    }

                                    @Override
                                    public void fail(ErrorCode errorCode) {
                                        trigger.fail(errorCode);
                                    }
                                });
                            }
                        });
                        flow(new NoRollbackFlow() {
                            String __name__ = "restart-service";

                            @Override
                            public void run(final FlowTrigger trigger, Map data) {

                                vpnService(VPN_RESTART, new ReturnValueCompletion<String>(trigger) {
                                    @Override
                                    public void success(String ret) {
                                        if (UP.equals(ret)) {
                                            changVpnSatus(VpnStatus.Connected);
                                            trigger.next();
                                        } else {
                                            changVpnSatus(VpnStatus.Disconnected);
                                            trigger.fail(errf.instantiateErrorCode(VpnErrors.VPN_RESTART_ERROR, "重启VPN服务失败"));
                                        }
                                    }

                                    @Override
                                    public void fail(ErrorCode errorCode) {
                                        trigger.fail(errorCode);
                                    }
                                });
                            }
                        });

                        done(new FlowDoneHandler(msg) {
                            @Override
                            public void handle(Map data) {
                                bus.reply(msg, reply);
                            }
                        });

                        error(new FlowErrorHandler(msg) {
                            @Override
                            public void handle(ErrorCode errCode, Map data) {
                                reply.setError(errCode);
                                bus.reply(msg, reply);
                            }
                        });

                        Finally(new FlowFinallyHandler(msg) {
                            @Override
                            public void Finally() {
                                chain.next();
                            }
                        });
                    }
                }).start();
            }

            @Override
            public String getName() {
                return "push-cert";
            }
        });
    }


    private void handle(final InitVpnMsg msg) {
        thdf.chainSubmit(new ChainTask(msg) {
            @Override
            public String getSyncSignature() {
                return String.format("init-vpn-%s", self.getUuid());
            }

            @Override
            public void run(final SyncTaskChain chain) {
                final InitVpnReply reply = new InitVpnReply();

                final FlowChain flowChain = FlowChainBuilder.newShareFlowChain();
                flowChain.setName(String.format("init-vpn-%s", self.getUuid()));
                flowChain.then(new ShareFlow() {
                    @Override
                    public void setup() {
                        flow(new NoRollbackFlow() {
                            String __name__ = "init-vpn";

                            @Override
                            public void run(final FlowTrigger trigger, Map data) {
                                initVpn(msg, new Completion(trigger) {
                                    @Override
                                    public void success() {
                                        trigger.next();
                                    }

                                    @Override
                                    public void fail(ErrorCode errorCode) {
                                        trigger.fail(errorCode);
                                    }
                                });
                            }
                        });

                        flow(new NoRollbackFlow() {
                            String __name__ = "get-client-info";

                            @Override
                            public void run(final FlowTrigger trigger, Map data) {
                                clientInfo(new Completion(trigger) {
                                    @Override
                                    public void success() {
                                        trigger.next();
                                    }

                                    @Override
                                    public void fail(ErrorCode errorCode) {
                                        trigger.fail(errorCode);
                                    }
                                });
                            }
                        });

                        flow(new NoRollbackFlow() {
                            String __name__ = "service-status";

                            @Override
                            public void run(final FlowTrigger trigger, Map data) {

                                vpnService(VPN_STATUS, new ReturnValueCompletion<String>(trigger) {
                                    @Override
                                    public void success(String ret) {
                                        if (UP.equals(ret)) {
                                            trigger.next();
                                        } else {
                                            trigger.fail(errf.instantiateErrorCode(VpnErrors.INIT_VPN_ERROR,
                                                    "VPN初始化结束, 但是VPN状态为Disconnected"));

                                        }
                                    }

                                    @Override
                                    public void fail(ErrorCode errorCode) {
                                        trigger.fail(errorCode);
                                    }
                                });
                            }
                        });

                        done(new FlowDoneHandler(msg) {
                            @Override
                            public void handle(Map data) {
                                changVpnSatus(VpnStatus.Connected);
                                bus.reply(msg, reply);
                            }
                        });

                        error(new FlowErrorHandler(msg) {
                            @Override
                            public void handle(ErrorCode errCode, Map data) {
                                changVpnSatus(VpnStatus.Disconnected);
                                reply.setError(errCode);
                                bus.reply(msg, reply);
                            }
                        });

                        Finally(new FlowFinallyHandler(msg) {
                            @Override
                            public void Finally() {
                                chain.next();
                            }
                        });
                    }
                }).start();
            }

            @Override
            public String getName() {
                return String.format("start-vpn-%s-on-host-%s", msg.getVpnUuid(), self.getHostUuid());
            }

            @Override
            protected int getSyncLevel() {
                return 2;
            }
        });
    }

    private void initVpn(final InitVpnMsg msg, final Completion completion) {
        InitVpnCmd cmd = new InitVpnCmd();
        cmd.vpnuuid = self.getUuid();
        cmd.hostip = getPublicIp();
        cmd.ddnport = getInterfaceName();
        cmd.vpnport = getPort();
        cmd.vpnvlanid = getVlan();
        cmd.speed = getBandwidth();
        cmd.certinfo = getCertInfo();

        httpCall(initVpnPath, cmd, InitVpnRsp.class, new ReturnValueCompletion<InitVpnRsp>(msg, completion) {
            @Override
            public void success(InitVpnRsp ret) {
                if (ret.isSuccess()) {
                    completion.success();
                } else {
                    completion.fail(errf.instantiateErrorCode(VpnErrors.INIT_VPN_ERROR, ret.getError()));
                }
            }

            @Override
            public void fail(ErrorCode errorCode) {
                completion.fail(errorCode);
            }
        });
    }

    private boolean changVpnSatus(VpnStatus next) {
        if (!Q.New(VpnVO.class).eq(VpnVO_.uuid, self.getUuid()).isExists()) {
            throw new CloudRuntimeException(String.format("修改VPN状态失败, 找不到vpn[%s]", self.getUuid()));
        }

        VpnStatus before = self.getStatus();
        if (before == next) {
            return false;
        }
        self.setStatus(next);
        self = dbf.updateAndRefresh(self);
        LOGGER.debug(String.format("修改VPN[name:%s,uuid:%s]的状态[%s]为 %s",
                self.getName(), self.getUuid(), before, next));
        return true;
    }

    private void handle(final StartAllMsg msg) {
        StartAllReply reply = new StartAllReply();
        changVpnSatus(VpnStatus.Disconnected);
        StartAllCmd cmd = new StartAllCmd();
        cmd.vpnuuid = self.getUuid();
        cmd.ddnport = getInterfaceName();
        cmd.vpnport = getPort();
        cmd.vpnvlanid = getVlan();
        cmd.speed = getBandwidth();

        httpCall(startAllPath, cmd, StartAllRsp.class, new ReturnValueCompletion<StartAllRsp>(msg) {
            @Override
            public void success(StartAllRsp ret) {
                if (UP.equals(ret.vpnStatus)) {
                    changVpnSatus(VpnStatus.Connected);
                } else {
                    reply.setError(errf.instantiateErrorCode(VpnErrors.VPN_RESTART_ERROR, ret.getError()));
                }
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errf.instantiateErrorCode(VpnErrors.VPN_RESTART_ERROR, errorCode.getDetails()));
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(final DestroyVpnMsg msg) {
        changVpnSatus(VpnStatus.Disconnected);
        final DestroyVpnReply reply = new DestroyVpnReply();
        DestroyVpnCmd cmd = new DestroyVpnCmd();
        cmd.vpnuuid = self.getUuid();
        cmd.vpnport = getPort();
        cmd.vpnvlanid = getVlan();
        cmd.ddnport = getInterfaceName();
        httpCall(destroyVpnPath, cmd, DestroyVpnRsp.class, new ReturnValueCompletion<DestroyVpnRsp>(msg) {
            @Override
            public void success(DestroyVpnRsp ret) {
                if (!ret.isSuccess()) {
                    reply.setError(errf.instantiateErrorCode(VpnErrors.VPN_DESTROY_ERROR, ret.getError()));
                }
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(StopVpnMsg msg) {
        changVpnSatus(VpnStatus.Disconnected);
        StopVpnReply reply = new StopVpnReply();
        vpnService(VPN_STOP, new ReturnValueCompletion<String>(msg) {
            @Override
            public void success(String ret) {
                if (UP.equals(ret)) {
                    changVpnSatus(VpnStatus.Connected);
                    reply.setError(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR, "停止VPN服务失败"));
                }
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(StartVpnMsg msg) {
        changVpnSatus(VpnStatus.Disconnected);
        StartVpnReply reply = new StartVpnReply();
        vpnService(VPN_RESTART, new ReturnValueCompletion<String>(msg) {
            @Override
            public void success(String ret) {
                if (UP.equals(ret)) {
                    changVpnSatus(VpnStatus.Connected);
                } else {
                    reply.setError(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR, "重启VPN服务失败"));
                }
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void vpnService(String command, final ReturnValueCompletion<String> completion) {
        VpnServiceCmd cmd = new VpnServiceCmd();
        cmd.vpnuuid = self.getUuid();
        cmd.vpnvlanid = getVlan();
        cmd.vpnport = getPort();
        cmd.command = command;
        httpCall(vpnServicePath, cmd, VpnServiceRsp.class, new ReturnValueCompletion<VpnServiceRsp>(completion) {
            @Override
            public void success(VpnServiceRsp ret) {
                completion.success(ret.vpnStatus);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                completion.fail(errorCode);
            }
        });
    }

    private void handle(VpnStatusMsg msg) {
        VpnStatusReply reply = new VpnStatusReply();
        if (self.getStatus() == VpnStatus.Connecting) {
            reply.setError(operr("VPN正在创建中"));
            bus.reply(msg, reply);
            return;
        }
        vpnService(VPN_STATUS, new ReturnValueCompletion<String>(msg) {
            @Override
            public void success(String ret) {
                reply.setConnected(UP.equals(ret));
                reply.setCurrentStatus(self.getStatus());
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setConnected(false);
                reply.setCurrentStatus(self.getStatus());
                reply.setError(errorCode);
                reply.setSuccess(true);
                bus.reply(msg, reply);
            }
        });

    }


    private void handle(final RateLimitingMsg msg) {
        RateLimitingReply reply = new RateLimitingReply();

        RateLimitingCmd cmd = new RateLimitingCmd();
        cmd.vpnport = getPort();
        cmd.speed = getBandwidth();
        cmd.command = RATE_LIMITING;
        httpCall(rateLimitingPath, cmd, RateLimitingRsp.class, new ReturnValueCompletion<RateLimitingRsp>(msg) {
            @Override
            public void success(RateLimitingRsp ret) {
                if (ret.isSuccess()) {
                    reply.setVpnLimit(ret.vpnLimit);
                } else {
                    reply.setError(errf.instantiateErrorCode(VpnErrors.VPN_RATE_LIMIT_ERROR, ret.getError()));
                }
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(final VpnConfMsg msg) {
        VpnConfReply reply = new VpnConfReply();

        VpnConfCmd cmd = new VpnConfCmd();
        cmd.vpnuuid = self.getUuid();
        cmd.vpnport = getPort();
        cmd.hostip = getPublicIp();
        cmd.level = getLevel();

        httpCall(vpnConfPath, cmd, VpnConfRsp.class, new ReturnValueCompletion<VpnConfRsp>(msg) {
            @Override
            public void success(VpnConfRsp ret) {
                if (ret.isSuccess()) {
                } else {
                    reply.setError(errf.instantiateErrorCode(VpnErrors.VPN_CONF_ERROR, ret.getError()));
                }
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }


    private void handle(final VpnPortMsg msg) {
        VpnPortReply reply = new VpnPortReply();

        VpnPortCmd cmd = new VpnPortCmd();
        cmd.vpnvlanid = getVlan();
        cmd.ddnport = getInterfaceName();
        cmd.vpnport = getPort();
        cmd.command = msg.getCommand();

        httpCall(vpnPortPath, cmd, VpnPortRsp.class, new ReturnValueCompletion<VpnPortRsp>(msg) {
            @Override
            public void success(VpnPortRsp ret) {
                if (ret.isSuccess()) {
                } else {
                    reply.setError(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR, ret.getError()));
                }
                bus.reply(msg, reply);
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void clientInfo(final Completion completion) {
        ClientInfoCmd cmd = new ClientInfoCmd();
        cmd.vpnuuid = self.getUuid();

        httpCall(clientInfoPath, cmd, ClientInfoRsp.class, new ReturnValueCompletion<ClientInfoRsp>(completion) {
            @Override
            public void success(ClientInfoRsp ret) {
                if (ret.isSuccess()) {
                    self.setClientConf(ret.client_conf);
                    dbf.updateAndRefresh(self);
                    completion.success();
                } else {
                    completion.fail(errf.instantiateErrorCode(VpnErrors.VPN_CONF_ERROR, ret.getError()));
                }
            }

            @Override
            public void fail(ErrorCode errorCode) {
                completion.fail(errorCode);
            }
        });
    }

    private void handle(final ClientInfoMsg msg) {
        ClientInfoReply reply = new ClientInfoReply();

        clientInfo(new Completion(msg) {
            @Override
            public void success() {
//                reply.setInventory(VpnInventory.valueOf(dbf.reload(self)));
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(final LoginInfoMsg msg) {
        LoginInfoReply reply = new LoginInfoReply();

        LoginInfoCmd cmd = new LoginInfoCmd();
        cmd.username = msg.getUsername();
        cmd.passwd = msg.getPasswd();
        httpCall(loginInfoPath, cmd, LoginInfoRsp.class, new ReturnValueCompletion<LoginInfoRsp>(msg) {
            @Override
            public void success(LoginInfoRsp ret) {
                reply.setPasswdfile(ret.passwdfile);
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private String getBandwidth() {
        BandwidthOfferingVO bandwidth = dbf.findByUuid(self.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        return String.valueOf(SizeUnit.BYTE.toKiloByte(bandwidth.getBandwidth()));
    }

    private String getHostIp() {
        return self.getVpnHost().getHostIp();
    }

    private String getPublicIp() {
        return self.getVpnHost().getPublicIp();
    }

    private String getInterfaceName() {
        return Q.New(HostInterfaceVO.class)
                .eq(HostInterfaceVO_.endpointUuid, getEndpointUuid())
                .eq(HostInterfaceVO_.hostUuid, self.getHostUuid())
                .select(HostInterfaceVO_.interfaceName).findValue();
    }

    private String getPort() {
        return self.getPort().toString();
    }

    private String getVlan() {
        return self.getVlan().toString();
    }

    private CertInfo getCertInfo() {
        return CertInfo.valueOf(self.getVpnCert());
    }

    private String getLevel() {
        return self instanceof VpnVO ? "2" : "3";
    }

    private String getEndpointUuid() {
        if (self instanceof VpnVO) {
            return ((VpnVO) self).getEndpointUuid();
        } else if (self instanceof L3VpnVO) {
            return ((L3VpnVO) self).getL3EndpointUuid();
        } else {
            return "";

        }
    }
}
