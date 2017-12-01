package com.syscxp.vpn.vpn;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.thread.ChainTask;
import com.syscxp.core.thread.SyncTaskChain;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.vpn.VpnConstant;
import com.syscxp.header.vpn.agent.*;
import com.syscxp.header.vpn.vpn.VpnStatus;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.header.vpn.vpn.VpnVO_;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.vpn.vpn.VpnCommands.*;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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
        } else if (msg instanceof CreateCertMsg) {
            handle((CreateCertMsg) msg);
        } else if (msg instanceof StartAllMsg) {
            handle((StartAllMsg) msg);
        } else if (msg instanceof DestroyVpnMsg) {
            handle((DestroyVpnMsg) msg);
        } else if (msg instanceof VpnServiceMsg) {
            handle((VpnServiceMsg) msg);
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
        } else {
            bus.dealWithUnknownMessage(msg);
        }
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
                VpnStatus next ="UP".equals(ret.vpnSatus)? VpnStatus.Connected:VpnStatus.Disconnected;
                changVpnSatus(next);
                bus.reply(msg, reply);
                completion.done();
            }

            @Override
            public void fail(ErrorCode err) {
                changVpnSatus(VpnStatus.Disconnected);
                reply.setError(err);
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

    private void handle(final CreateCertMsg msg) {
        thdf.chainSubmit(new ChainTask(msg) {
            @Override
            public String getSyncSignature() {
                return id;
            }

            @Override
            public void run(final SyncTaskChain chain) {
                createCert(msg, new NoErrorCompletion(chain) {
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

    private void createCert(final CreateCertMsg msg, final NoErrorCompletion completion) {
        CreateCertReply reply = new CreateCertReply();
        CreateCertCmd cmd = new CreateCertCmd();
        cmd.vpnuuid = msg.getVpnUuid();
        httpCall(createCertPath, cmd, CreateCertRsp.class, new ReturnValueCompletion<CreateCertRsp>(msg, completion) {
            @Override
            public void success(CreateCertRsp ret) {
                bus.reply(msg, reply);
                completion.done();
            }

            @Override
            public void fail(ErrorCode err) {
                reply.setError(err);
                bus.reply(msg, reply);
                completion.done();
            }
        });
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
                VpnStatus next ="UP".equals(ret.vpnSatus)? VpnStatus.Connected:VpnStatus.Disconnected;
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
        DestroyVpnReply reply = new DestroyVpnReply();

        DestroyVpnCmd cmd = new DestroyVpnCmd();
        cmd.vpnuuid = msg.getVpnUuid();
        cmd.ddnport = msg.getInterfaceName();
        cmd.vpnport = msg.getVpnPort();
        cmd.vpnvlanid = msg.getVpnVlan();
        httpCall(destroyVpnPath, cmd, DestroyVpnRsp.class, new ReturnValueCompletion<DestroyVpnRsp>(msg) {
            @Override
            public void success(DestroyVpnRsp ret) {
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

    private void handle(final VpnServiceMsg msg) {
        VpnServiceReply reply = new VpnServiceReply();

        VpnServiceCmd cmd = new VpnServiceCmd();
        cmd.vpnuuid = msg.getVpnUuid();
        cmd.vpnport = msg.getVpnPort();
        cmd.vpnvlanid = msg.getVpnVlan();
        cmd.command = msg.getCommand();

        httpCall(vpnServicePath, cmd, VpnServiceRsp.class, new ReturnValueCompletion<VpnServiceRsp>(msg) {
            @Override
            public void success(VpnServiceRsp ret) {
                VpnStatus next ="UP".equals(ret.vpnSatus)? VpnStatus.Connected:VpnStatus.Disconnected;
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
                CertInfo certInfo = new CertInfo();
                certInfo.setCaCert(ret.ca_crt);
                certInfo.setClientCert(ret.client_crt);
                certInfo.setClientConf(ret.client_conf);
                certInfo.setClientKey(ret.client_key);
                reply.setCertInfo(certInfo);
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


}
