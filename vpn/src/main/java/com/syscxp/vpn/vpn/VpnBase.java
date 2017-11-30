package com.syscxp.vpn.vpn;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.ChainTask;
import com.syscxp.core.thread.SyncTaskChain;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.Constants;
import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.JsonAsyncRESTCallback;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.vpn.VpnConstant;
import com.syscxp.header.vpn.agent.*;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.utils.URLBuilder;
import com.syscxp.vpn.vpn.VpnCommands.*;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.operr;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class VpnBase implements Vpn {

    @Autowired
    private CloudBus bus;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private ErrorFacade errf;

    protected VpnVO self;
    protected final String id;
    // ///////////////////// REST URL //////////////////////////
    private String baseUrl;
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

    private String scheme = VpnGlobalProperty.AGENT_URL_SCHEME;
    private int port = VpnGlobalProperty.AGENT_PORT;
    private String rootPath = VpnGlobalProperty.AGENT_URL_ROOT_PATH;

    protected VpnBase(VpnVO self) {
        this.self = self;
        id = "Vpn-" + self.getUuid();

        if (!"".equals(rootPath)) {
            baseUrl = URLBuilder.buildUrl(scheme, self.getVpnHost().getHostIp(), port, rootPath);
        } else {
            baseUrl = URLBuilder.buildUrl(scheme, self.getVpnHost().getHostIp(), port);
        }
        createCertPath = URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.CREATE_CERT_PATH);
        vpnConfPath = URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.VPN_CONF_PATH);
        rateLimitingPath = URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.RATE_LIMITING_PATH);
        vpnPortPath = URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.VPN_PORT_PATH);
        vpnServicePath = URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.VPN_SERVICE_PATH);
        startAllPath = URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.START_ALL_PATH);
        destroyVpnPath = URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.DESTROY_VPN_PATH);
        clientInfoPath = URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.CLIENT_INFO_PATH);
        loginInfoPath = URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.LOGIN_INFO_PATH);
        initVpnPath = URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.INIT_VPN_PATH);
    }

    class Http<T> {
        String path;
        AgentCommand cmd;
        Class<T> responseClass;
        String commandStr;
        TimeUnit unit;
        Long timeout;

        public Http(String path, String cmd, Class<T> rspClz, TimeUnit unit, Long timeout) {
            this.path = path;
            this.commandStr = cmd;
            this.responseClass = rspClz;
            this.unit = unit;
            this.timeout = timeout;
        }

        public Http(String path, AgentCommand cmd, Class<T> rspClz) {
            this.path = path;
            this.cmd = cmd;
            this.responseClass = rspClz;
        }

        void call(ReturnValueCompletion<T> completion) {
            Map<String, String> header = new HashMap<>();
            header.put(Constants.AGENT_HTTP_HEADER_RESOURCE_UUID, self.getUuid());
            if (commandStr != null) {
                restf.asyncJsonPost(path, commandStr, header, new JsonAsyncRESTCallback<T>(completion) {
                    @Override
                    public void fail(ErrorCode err) {
                        completion.fail(err);
                    }

                    @Override
                    public void success(T ret) {
                        completion.success(ret);
                    }

                    @Override
                    public Class<T> getReturnClass() {
                        return responseClass;
                    }
                }, unit, timeout);
            } else {
                restf.asyncJsonPost(path, cmd, header, new JsonAsyncRESTCallback<T>(completion) {
                    @Override
                    public void fail(ErrorCode err) {
                        completion.fail(err);
                    }

                    @Override
                    public void success(T ret) {
                        completion.success(ret);
                    }

                    @Override
                    public Class<T> getReturnClass() {
                        return responseClass;
                    }
                });
            }
        }
    }

    @Override
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

        new Http<>(initVpnPath, cmd, InitVpnRsp.class).call(new ReturnValueCompletion<InitVpnRsp>(msg, completion) {
            @Override
            public void success(InitVpnRsp ret) {
                if (ret.isSuccess()) {
                } else {
                    reply.setError(operr(ret.getError()));
                }
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
        new Http<>(createCertPath, cmd, CreateCertRsp.class).call(new ReturnValueCompletion<CreateCertRsp>(msg, completion) {
            @Override
            public void success(CreateCertRsp ret) {
                if (ret.isSuccess()) {
                    reply.setVpnCert(ret.vpnCert);
                } else {
                    reply.setError(operr(ret.getError()));
                }
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

        new Http<>(startAllPath, cmd, StartAllRsp.class).call(new ReturnValueCompletion<StartAllRsp>(msg) {
            @Override
            public void success(StartAllRsp ret) {
                if (ret.isSuccess()) {
                    reply.setStatus(ret.vpnSatus);
                } else {
                    reply.setError(operr(ret.getError()));
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

    private void handle(final DestroyVpnMsg msg) {
        DestroyVpnReply reply = new DestroyVpnReply();

        DestroyVpnCmd cmd = new DestroyVpnCmd();
        cmd.vpnuuid = msg.getVpnUuid();
        cmd.ddnport = msg.getInterfaceName();
        cmd.vpnport = msg.getVpnPort();
        cmd.vpnvlanid = msg.getVpnVlan();
        new Http<>(destroyVpnPath, cmd, DestroyVpnRsp.class).call(new ReturnValueCompletion<DestroyVpnRsp>(msg) {
            @Override
            public void success(DestroyVpnRsp ret) {
                if (ret.isSuccess()) {
                    reply.setStatus(ret.vpnSatus);
                } else {
                    reply.setError(operr(ret.getError()));
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

    private void handle(final VpnServiceMsg msg) {
        VpnServiceReply reply = new VpnServiceReply();

        VpnServiceCmd cmd = new VpnServiceCmd();
        cmd.vpnuuid = msg.getVpnUuid();
        cmd.vpnport = msg.getVpnPort();
        cmd.vpnvlanid = msg.getVpnVlan();
        cmd.command = msg.getCommand();

        new Http<>(vpnServicePath, cmd, VpnServiceRsp.class).call(new ReturnValueCompletion<VpnServiceRsp>(msg) {
            @Override
            public void success(VpnServiceRsp ret) {
                if (ret.isSuccess()) {
                    reply.setStatus(ret.vpnSatus);
                } else {
                    reply.setError(operr(ret.getError()));
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


    private void handle(final RateLimitingMsg msg) {
        RateLimitingReply reply = new RateLimitingReply();

        RateLimitingCmd cmd = new RateLimitingCmd();
        cmd.vpnport = msg.getVpnPort();
        cmd.speed = msg.getSpeed();
        cmd.command = msg.getCommand();
        new Http<>(rateLimitingPath, cmd, RateLimitingRsp.class).call(new ReturnValueCompletion<RateLimitingRsp>(msg) {
            @Override
            public void success(RateLimitingRsp ret) {
                if (ret.isSuccess()) {
                    reply.setVpnLimit(ret.vpnLimit);
                } else {
                    reply.setError(operr(ret.getError()));
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
        cmd.vpnuuid = msg.getVpnUuid();
        cmd.vpnport = msg.getVpnPort();
        cmd.hostip = msg.getHostIp();

        new Http<>(vpnConfPath, cmd, VpnConfRsp.class).call(new ReturnValueCompletion<VpnConfRsp>(msg) {
            @Override
            public void success(VpnConfRsp ret) {
                if (ret.isSuccess()) {
                    reply.setVpnSucc(ret.vpnSucc);
                } else {
                    reply.setError(operr(ret.getError()));
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
        cmd.vpnport = msg.getVpnPort();
        cmd.ddnport = msg.getInterfaceName();
        cmd.vpnport = msg.getVpnPort();
        cmd.command = msg.getCommand();

        new Http<>(vpnPortPath, cmd, VpnPortRsp.class).call(new ReturnValueCompletion<VpnPortRsp>(msg) {
            @Override
            public void success(VpnPortRsp ret) {
                if (ret.isSuccess()) {
                    reply.setStatus(ret.vpnSatus);
                } else {
                    reply.setError(operr(ret.getError()));
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

    private void handle(final ClientInfoMsg msg) {
        ClientInfoReply reply = new ClientInfoReply();

        ClientInfoCmd cmd = new ClientInfoCmd();
        cmd.vpnuuid = msg.getVpnUuid();

        new Http<>(clientInfoPath, cmd, ClientInfoRsp.class).call(new ReturnValueCompletion<ClientInfoRsp>(msg) {
            @Override
            public void success(ClientInfoRsp ret) {
                if (ret.isSuccess()) {
                    CertInventory inventory = new CertInventory();
                    inventory.setCaCert(ret.ca_crt);
                    inventory.setClientCert(ret.client_crt);
                    inventory.setClientConf(ret.client_conf);
                    inventory.setClientKey(ret.client_key);
                    reply.setInventory(inventory);
                } else {
                    reply.setError(operr(ret.getError()));
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

    private void handle(final LoginInfoMsg msg) {
        LoginInfoReply reply = new LoginInfoReply();

        LoginInfoCmd cmd = new LoginInfoCmd();
        new Http<>(loginInfoPath, cmd, LoginInfoRsp.class).call(new ReturnValueCompletion<LoginInfoRsp>(msg) {
            @Override
            public void success(LoginInfoRsp ret) {
                if (ret.isSuccess()) {
                    reply.setPasswdfile(ret.passwdfile);
                } else {
                    reply.setError(operr(ret.getError()));
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
}
