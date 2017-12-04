package com.syscxp.vpn.vpn;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.thread.ChainTask;
import com.syscxp.core.thread.SyncTaskChain;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.core.workflow.ShareFlow;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.vpn.VpnConstant;
import com.syscxp.header.vpn.agent.*;
import com.syscxp.header.vpn.vpn.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.ssh.SshShell;
import com.syscxp.vpn.exception.VpnErrors;
import com.syscxp.vpn.vpn.VpnCommands.*;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Map;

import static com.syscxp.core.Platform.operr;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class VpnBase extends AbstractVpn {
    private static final CLogger logger = Utils.getLogger(VpnBase.class);

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ThreadFacade thdf;


    // ///////////////////// REST URL //////////////////////////
    private String createCertPath;
    private String vpnConfPath;
    private String rateLimitingPath;
    private String vpnPortPath;
    private String vpnServicePath;
    private String startAllPath;
    private String destroyVpnPath;
    private String clientInfoPath;
    private String loginInfoPath;
    private String initVpnPath;


    protected VpnBase(VpnVO self) {
        super(self);
        createCertPath = VpnConstant.CREATE_CERT_PATH;
        vpnConfPath = VpnConstant.VPN_CONF_PATH;
        rateLimitingPath = VpnConstant.RATE_LIMITING_PATH;
        vpnPortPath = VpnConstant.VPN_PORT_PATH;
        vpnServicePath = VpnConstant.VPN_SERVICE_PATH;
        startAllPath = VpnConstant.START_ALL_PATH;
        destroyVpnPath = VpnConstant.DESTROY_VPN_PATH;
        clientInfoPath = VpnConstant.CLIENT_INFO_PATH;
        loginInfoPath = VpnConstant.LOGIN_INFO_PATH;
        initVpnPath = VpnConstant.INIT_VPN_PATH;
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
        } else if (msg instanceof ChangeVpnStateMsg) {
            handle((ChangeVpnStateMsg) msg);
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
        PushCertReply reply = new PushCertReply();

        InitVpnCmd cmd = new InitVpnCmd();
        cmd.vpnuuid = msg.getVpnUuid();

        httpCall(initVpnPath, cmd, InitVpnRsp.class, new ReturnValueCompletion<InitVpnRsp>(msg) {
            @Override
            public void success(InitVpnRsp ret) {
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode err) {
                bus.reply(msg, reply);
            }
        });

    }


    private void handle(final InitVpnMsg msg) {
        thdf.chainSubmit(new ChainTask(msg) {
            @Override
            public String getSyncSignature() {
                return id;
            }

            @Override
            public void run(final SyncTaskChain chain) {
                initVpn(msg, new NoErrorCompletion(chain) {
                    @Override
                    public void done() {
                        chain.next();
                    }
                });
            }

            @Override
            public String getName() {
                return String.format("start-vpn-%s-on-host-%s", msg.getVpnUuid(), self.getHostUuid());
            }

            @Override
            protected int getSyncLevel() {
                return getHostSyncLevel();
            }
        });
    }

    protected int getHostSyncLevel() {
        return 2;
    }

    private void initVpn(final InitVpnMsg msg, final NoErrorCompletion completion) {
        InitVpnReply reply = new InitVpnReply();
        InitVpnCmd cmd = new InitVpnCmd();
        cmd.vpnuuid = msg.getVpnUuid();
        cmd.hostip = msg.getHostIp();
        cmd.ddnport = msg.getInterfaceName();
        cmd.vpnport = msg.getVpnPort();
        cmd.vpnvlanid = msg.getVpnVlan();
        cmd.username = msg.getUsername();
        cmd.passwd = msg.getPasswd();
        cmd.speed = msg.getSpeed();

        httpCall(initVpnPath, cmd, InitVpnRsp.class, new ReturnValueCompletion<InitVpnRsp>(msg, completion) {
            @Override
            public void success(InitVpnRsp ret) {
                VpnStatus next = "UP".equals(ret.vpnStatus) ? VpnStatus.Connected : VpnStatus.Disconnected;
                changVpnSatus(next);
                reply.setStatus(next.toString());
                bus.reply(msg, reply);
                completion.done();
            }

            @Override
            public void fail(ErrorCode err) {
                changVpnSatus(VpnStatus.Disconnected);
                reply.setError(err);
                reply.setSuccess(true);
                reply.setStatus(VpnStatus.Disconnected.toString());
                bus.reply(msg, reply);
                completion.done();
            }
        });
    }

    private boolean changVpnSatus(VpnStatus next) {
        if (!Q.New(VpnVO.class).eq(VpnVO_.uuid, self.getUuid()).isExists()) {
            throw new CloudRuntimeException(String.format("change vpn status fail, can not find the vpn[%s]", self.getUuid()));
        }

        VpnStatus before = self.getStatus();
        if (before == next) {
            return false;
        }
        self.setStatus(next);
        self = dbf.updateAndRefresh(self);
        logger.debug(String.format("Vpn %s [uuid:%s] changed status from %s to %s",
                self.getName(), self.getUuid(), before, next));
        return true;
    }

    private void handle(final StartAllMsg msg) {
        StartAllReply reply = new StartAllReply();

        StartAllCmd cmd = new StartAllCmd();
        cmd.vpnuuid = msg.getVpnUuid();
        cmd.ddnport = msg.getInterfaceName();
        cmd.vpnport = msg.getVpnPort();
        cmd.vpnvlanid = msg.getVpnVlan();
        cmd.speed = msg.getSpeed();

        httpCall(startAllPath, cmd, StartAllRsp.class, new ReturnValueCompletion<StartAllRsp>(msg) {
            @Override
            public void success(StartAllRsp ret) {
                VpnStatus next = "UP".equals(ret.vpnStatus) ? VpnStatus.Connected : VpnStatus.Disconnected;
                changVpnSatus(next);
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                changVpnSatus(VpnStatus.Disconnected);
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(final DestroyVpnMsg msg) {

        thdf.chainSubmit(new ChainTask(msg) {
            @Override
            public String getSyncSignature() {
                return String.format("connect-host-%s", self.getUuid());
            }

            @Override
            public void run(SyncTaskChain chain) {
                final DestroyVpnReply reply = new DestroyVpnReply();

                final FlowChain flowChain = FlowChainBuilder.newShareFlowChain();
                flowChain.setName(String.format("delete-vpn-%s", self.getUuid()));
                flowChain.then(new ShareFlow() {
                    @Override
                    public void setup() {
                        flow(new NoRollbackFlow() {
                            String __name__ = "stop-vpn";

                            @Override
                            public void run(final FlowTrigger trigger, Map data) {
                                VpnServiceCmd cmd = new VpnServiceCmd();
                                cmd.vpnuuid = msg.getVpnUuid();
                                cmd.vpnport = msg.getVpnPort();
                                cmd.vpnvlanid = msg.getVpnVlan();
                                cmd.command = "stop";

                                httpCall(vpnServicePath, cmd, VpnServiceRsp.class, new ReturnValueCompletion<VpnServiceRsp>(trigger) {
                                    @Override
                                    public void success(VpnServiceRsp ret) {
                                        VpnStatus next = "UP".equals(ret.vpnStatus) ? VpnStatus.Connected : VpnStatus.Disconnected;
                                        changVpnSatus(next);
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
                            String __name__ = "destroy-vpn";

                            @Override
                            public void run(final FlowTrigger trigger, Map data) {
                                DestroyVpnCmd cmd = new DestroyVpnCmd();
                                cmd.vpnuuid = msg.getVpnUuid();
                                cmd.ddnport = msg.getInterfaceName();
                                cmd.vpnport = msg.getVpnPort();
                                cmd.vpnvlanid = msg.getVpnVlan();
                                httpCall(destroyVpnPath, cmd, DestroyVpnRsp.class, new ReturnValueCompletion<DestroyVpnRsp>(trigger) {
                                    @Override
                                    public void success(DestroyVpnRsp ret) {
                                        dbf.removeByPrimaryKey(self.getUuid(), VpnVO.class);
                                        trigger.next();
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
                return "delete-vpn";
            }
        });

    }

    private void handle(ChangeVpnStateMsg msg) {
        ChangeVpnStateReply reply = new ChangeVpnStateReply();

        VpnServiceCmd cmd = new VpnServiceCmd();
        cmd.vpnuuid = self.getUuid();
        cmd.vpnport = self.getPort().toString();
        cmd.vpnvlanid = self.getVlan().toString();
        if (msg.getState() == VpnState.Enabled)
            cmd.command = "start";
        else
            cmd.command = "stop";

        httpCall(vpnServicePath, cmd, VpnServiceRsp.class, new ReturnValueCompletion<VpnServiceRsp>(msg) {
            @Override
            public void success(VpnServiceRsp ret) {
                if ("UP".equals(ret.vpnStatus) && msg.getState() == VpnState.Enabled) {
                    changVpnSatus(VpnStatus.Connected);
                } else if ("DOWN".equals(ret.vpnStatus) && msg.getState() == VpnState.Disabled) {
                    changVpnSatus(VpnStatus.Disconnected);
                } else {
                    reply.setError(errf.stringToOperationError("change vpn state"));
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

    private void handle(VpnStatusMsg msg) {
        VpnStatusReply reply = new VpnStatusReply();
        if (self.getStatus() == VpnStatus.Connecting) {
            reply.setError(operr("vpn is connecting"));
            bus.reply(msg, reply);
            return;
        }
        statusCheck(new Completion(msg) {
            @Override
            public void success() {
                reply.setConnected(true);
                reply.setCurrentStatus(self.getStatus());

                changVpnSatus(VpnStatus.Connected);
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setConnected(false);
                reply.setCurrentStatus(self.getStatus());
                reply.setError(errorCode);
                reply.setSuccess(true);
                changVpnSatus(VpnStatus.Disconnected);

                bus.reply(msg, reply);
            }
        });

    }

    protected void statusCheck(final Completion completion) {
        VpnServiceCmd cmd = new VpnServiceCmd();
        cmd.vpnuuid = self.getUuid();

        httpCall(vpnServicePath, cmd, VpnServiceRsp.class, new ReturnValueCompletion<VpnServiceRsp>(completion) {
            @Override
            public void success(VpnServiceRsp ret) {
                if ("UP".equals(ret.vpnStatus)) {
                    completion.success();
                } else {
                    completion.fail(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR, "vpn service disconnected"));
                }
            }

            @Override
            public void fail(ErrorCode errorCode) {
                completion.fail(errorCode);
            }
        });
    }


    private void handle(final RateLimitingMsg msg) {
        RateLimitingReply reply = new RateLimitingReply();

        RateLimitingCmd cmd = new RateLimitingCmd();
        cmd.vpnport = msg.getVpnPort();
        cmd.speed = msg.getSpeed();
        cmd.command = msg.getCommand();
        httpCall(rateLimitingPath, cmd, RateLimitingRsp.class, new ReturnValueCompletion<RateLimitingRsp>(msg) {
            @Override
            public void success(RateLimitingRsp ret) {
                reply.setVpnLimit(ret.vpnLimit);
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
        cmd.vpnuuid = msg.getVpnUuid();
        cmd.vpnport = msg.getVpnPort();
        cmd.hostip = msg.getHostIp();

        httpCall(vpnConfPath, cmd, VpnConfRsp.class, new ReturnValueCompletion<VpnConfRsp>(msg) {
            @Override
            public void success(VpnConfRsp ret) {
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
        cmd.vpnvlanid = msg.getVpnVlan();
        cmd.ddnport = msg.getInterfaceName();
        cmd.vpnport = msg.getVpnPort();
        cmd.command = msg.getCommand();

        httpCall(vpnPortPath, cmd, VpnPortRsp.class, new ReturnValueCompletion<VpnPortRsp>(msg) {
            @Override
            public void success(VpnPortRsp ret) {
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(final ClientInfoMsg msg) {
        ClientInfoReply reply = new ClientInfoReply();

        ClientInfoCmd cmd = new ClientInfoCmd();
        cmd.vpnuuid = msg.getVpnUuid();

        httpCall(clientInfoPath, cmd, ClientInfoRsp.class, new ReturnValueCompletion<ClientInfoRsp>(msg) {
            @Override
            public void success(ClientInfoRsp ret) {
                VpnCertVO vpnCert = saveVpnCert(ret, self.getAccountUuid());
                reply.setInventory(VpnCertInventory.valueOf(vpnCert));
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private VpnCertVO saveVpnCert(ClientInfoRsp info, String accountUuid) {

        VpnCertVO vo = Q.New(VpnCertVO.class).eq(VpnCertVO_.accountUuid, accountUuid).find();
        if (vo == null) {
            vo = new VpnCertVO();
            vo.setUuid(Platform.getUuid());
            vo.setAccountUuid(accountUuid);
        }
        vo.setCaCert(info.ca_crt);
        vo.setClientCert(info.client_crt);
        vo.setClientKey(info.client_key);
        vo.setClientConf(info.client_conf);
        return dbf.updateAndRefresh(vo);
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


}
