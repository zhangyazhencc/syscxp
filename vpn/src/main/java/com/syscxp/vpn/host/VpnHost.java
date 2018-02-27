package com.syscxp.vpn.host;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.ansible.AnsibleConstant;
import com.syscxp.core.ansible.AnsibleGlobalProperty;
import com.syscxp.core.ansible.AnsibleRunner;
import com.syscxp.core.ansible.SshFileMd5Checker;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.host.HostBase;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.core.workflow.ShareFlow;
import com.syscxp.header.core.AsyncLatch;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.host.*;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.JsonAsyncRESTCallback;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.vpn.host.APIUpdateVpnHostMsg;
import com.syscxp.header.vpn.host.VpnHostConstant;
import com.syscxp.header.vpn.host.VpnHostInventory;
import com.syscxp.header.vpn.host.VpnHostVO;
import com.syscxp.utils.ShellUtils;
import com.syscxp.utils.StringBind;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;
import com.syscxp.utils.path.PathUtil;
import com.syscxp.utils.ssh.Ssh;
import com.syscxp.utils.ssh.SshResult;
import com.syscxp.utils.ssh.SshShell;
import com.syscxp.vpn.host.VpnHostCommands.ConnectCmd;
import com.syscxp.vpn.host.VpnHostCommands.ConnectResponse;
import com.syscxp.vpn.host.VpnHostCommands.PingCmd;
import com.syscxp.vpn.host.VpnHostCommands.PingResponse;
import com.syscxp.vpn.vpn.VpnGlobalConfig;
import com.syscxp.vpn.vpn.VpnGlobalProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.operr;

/**
 * @author wangjie
 */
public class VpnHost extends HostBase implements Host {
    private static final CLogger LOGGER = Utils.getLogger(VpnHost.class);

    @Autowired
    @Qualifier("VpnHostFactory")
    private VpnHostFactory factory;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;

    private String baseUrl;
    private String connectPath;
    private String pingPath;
    private String echoPath;

    private String agentPackageName = VpnGlobalProperty.AGENT_PACKAGE_NAME;


    protected VpnHost(HostVO self, VpnHostContext context) {
        super(self);

        baseUrl = context.getBaseUrl();

        UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(baseUrl);
        ub.path(VpnHostConstant.AGENT_CONNECT_PATH);
        connectPath = ub.build().toUriString();

        ub = UriComponentsBuilder.fromHttpUrl(baseUrl);
        ub.path(VpnHostConstant.AGENT_PING_PATH);
        pingPath = ub.build().toUriString();

        ub = UriComponentsBuilder.fromHttpUrl(baseUrl);
        ub.path(VpnHostConstant.AGENT_ECHO_PATH);
        echoPath = ub.build().toString();

    }

    private VpnHostVO getSelf() {
        return (VpnHostVO) self;
    }

    @Override
    protected void pingHook(final Completion completion) {
        FlowChain chain = FlowChainBuilder.newShareFlowChain();
        chain.setName(String.format("ping-Vpn-host-%s", self.getUuid()));
        chain.then(new ShareFlow() {
            @Override
            public void setup() {
                flow(new NoRollbackFlow() {
                    String __name__ = "ping-host";

                    @AfterDone
                    List<Runnable> afterDone = new ArrayList<>();

                    @Override
                    public void run(FlowTrigger trigger, Map data) {
                        PingCmd cmd = new PingCmd();
                        cmd.hostUuid = self.getUuid();
                        restf.asyncJsonPost(pingPath, cmd, new JsonAsyncRESTCallback<PingResponse>(trigger) {
                            @Override
                            public void fail(ErrorCode err) {
                                trigger.fail(err);
                            }

                            @Override
                            public void success(PingResponse ret) {
                                if (ret.isSuccess()) {
                                    if (!self.getUuid().equals(ret.getHostUuid())) {
                                        afterDone.add(() -> {
                                            String info = String.format("detected abnormal status[host uuid change, " +
                                                    "expected: %s but: %s] of Vpnagent, it's mainly caused by Vpnagent " +
                                                    "restarts behind syscxp management server. Report this to ping task, " +
                                                    "it will issue a reconnect soon", self.getUuid(), ret.getHostUuid());
                                            LOGGER.warn(info);
                                            ReconnectHostMsg rmsg = new ReconnectHostMsg();
                                            rmsg.setHostUuid(self.getUuid());
                                            bus.makeLocalServiceId(rmsg, HostConstant.SERVICE_ID);
                                            bus.send(rmsg);
                                        });
                                    }

                                    trigger.next();
                                } else {
                                    trigger.fail(errf.stringToOperationError(ret.getError()));
                                }
                            }

                            @Override
                            public Class<PingResponse> getReturnClass() {
                                return PingResponse.class;
                            }
                        });
                    }
                });

                flow(new NoRollbackFlow() {
                    String __name__ = "call-ping-no-failure-plugins";

                    @Override
                    public void run(FlowTrigger trigger, Map data) {
                        List<VpnHostPingAgentNoFailureExtensionPoint> exts = pluginRgty.getExtensionList(VpnHostPingAgentNoFailureExtensionPoint.class);
                        if (exts.isEmpty()) {
                            trigger.next();
                            return;
                        }

                        AsyncLatch latch = new AsyncLatch(exts.size(), new NoErrorCompletion(trigger) {
                            @Override
                            public void done() {
                                trigger.next();
                            }
                        });

                        VpnHostInventory inv = (VpnHostInventory) getSelfInventory();
                        for (VpnHostPingAgentNoFailureExtensionPoint ext : exts) {
                            LOGGER.debug(String.format("calling VpnHostPingAgentNoFailureExtensionPoint[%s]", ext.getClass()));
                            ext.vpnPingAgentNoFailure(inv, new NoErrorCompletion(latch) {
                                @Override
                                public void done() {
                                    latch.ack();
                                }
                            });
                        }
                    }
                });

                flow(new NoRollbackFlow() {
                    String __name__ = "call-ping-plugins";

                    @Override
                    public void run(FlowTrigger trigger, Map data) {
                        List<VpnHostPingAgentExtensionPoint> exts = pluginRgty.getExtensionList(VpnHostPingAgentExtensionPoint.class);
                        Iterator<VpnHostPingAgentExtensionPoint> it = exts.iterator();
                        callPlugin(it, trigger);
                    }

                    private void callPlugin(Iterator<VpnHostPingAgentExtensionPoint> it, FlowTrigger trigger) {
                        if (!it.hasNext()) {
                            trigger.next();
                            return;
                        }

                        VpnHostPingAgentExtensionPoint ext = it.next();
                        LOGGER.debug(String.format("calling VpnHostPingAgentExtensionPoint[%s]", ext.getClass()));
                        ext.vpnPingAgent((VpnHostInventory) getSelfInventory(), new Completion(trigger) {
                            @Override
                            public void success() {
                                callPlugin(it, trigger);
                            }

                            @Override
                            public void fail(ErrorCode errorCode) {
                                trigger.fail(errorCode);
                            }
                        });
                    }
                });

                done(new FlowDoneHandler(completion) {
                    @Override
                    public void handle(Map data) {
                        completion.success();
                    }
                });

                error(new FlowErrorHandler(completion) {
                    @Override
                    public void handle(ErrorCode errCode, Map data) {
                        completion.fail(errCode);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void changeStateHook(HostState current, HostStateEvent stateEvent, HostState next) {
        LOGGER.debug(String.format("Host: %s changed state from %s to %s by %s", self.getName(), current, next, stateEvent));
    }

    @Override
    public void connectHook(final ConnectHostInfo info, final Completion complete) {
        FlowChain chain = FlowChainBuilder.newShareFlowChain();
        chain.setName(String.format("run-ansible-for-host-%s", self.getUuid()));
        chain.then(new ShareFlow() {
            @Override
            public void setup() {

                flow(new NoRollbackFlow() {
                    String __name__ = "check-if-host-can-reach-management-node";

                    @Override
                    public void run(FlowTrigger trigger, Map data) {
                        SshShell sshShell = new SshShell();
                        sshShell.setHostname(getSelf().getHostIp());
                        sshShell.setUsername(getSelf().getUsername());
                        sshShell.setPassword(getSelf().getPassword());
                        sshShell.setPort(getSelf().getSshPort());
                        ShellUtils.run(String.format("arp -d %s || true", getSelf().getSshPort()));
                        SshResult ret = sshShell.runCommand(String.format("curl --connect-timeout 10 %s", restf
                                .getCallbackUrl()));
                        if (ret.isSshFailure()) {
                            trigger.fail(operr("unable to connect to Host[ip:%s, username:%s, sshPort:%d] to check " +
                                            "the management node connectivity,please check if username/password is wrong; %s",
                                    self.getHostIp(), getSelf().getUsername(), getSelf().getSshPort(), ret.getExitErrorMessage()));
                        } else if (ret.getReturnCode() != 0) {
                            trigger.fail(operr("the host[ip:%s] cannot access the management node's callback url. It seems" +
                                            " that the host cannot reach the management IP[%s]. %s %s", self.getHostIp(), Platform.getManagementServerIp(),
                                    ret.getStderr(), ret.getExitErrorMessage()));
                        } else {
                            trigger.next();
                        }
                    }
                });

                flow(new NoRollbackFlow() {
                    String __name__ = "apply-ansible-playbook";

                    @Override
                    public void run(final FlowTrigger trigger, Map data) {
//                        String srcPath = String.format("%s/files/vpn/%s", AnsibleConstant.ROOT_DIR, agentPackageName);
                        String srcPath = PathUtil.findFileOnClassPath(String.format("ansible/vpn/%s", agentPackageName), true).getAbsolutePath();
                        String destPath = String.format("/var/lib/syscxp/vpn/package/%s", agentPackageName);
                        SshFileMd5Checker checker = new SshFileMd5Checker();
                        checker.setUsername(getSelf().getUsername());
                        checker.setPassword(getSelf().getPassword());
                        checker.setSshPort(getSelf().getSshPort());
                        checker.setTargetIp(getSelf().getHostIp());
                        /*checker.addSrcDestPair(String.format("%s/%s", AnsibleConstant.SYSCXPLIB_ROOT, AnsibleGlobalProperty.SYSCXPLIB_PACKAGE_NAME),
                                String.format("/var/lib/syscxp/vpn/package/%s", AnsibleGlobalProperty.SYSCXPLIB_PACKAGE_NAME));*/
                        checker.addSrcDestPair(SshFileMd5Checker.SYSCXPLIB_SRC_PATH,
                                String.format("/var/lib/syscxp/vpn/package/%s", AnsibleGlobalProperty.SYSCXPLIB_PACKAGE_NAME));
                        checker.addSrcDestPair(srcPath, destPath);

                        AnsibleRunner runner = new AnsibleRunner();
                        runner.installChecker(checker);
                        runner.setAgentPort(VpnGlobalProperty.AGENT_PORT);
                        runner.setTargetIp(getSelf().getHostIp());
                        runner.setPlayBookName(VpnHostConstant.ANSIBLE_PLAYBOOK_NAME);
                        runner.setUsername(getSelf().getUsername());
                        runner.setPassword(getSelf().getPassword());
                        runner.setSshPort(getSelf().getSshPort());
                        if (info.isNewAdded()) {
                            runner.putArgument("init", "true");
                            runner.setFullDeploy(true);
                        }
                        runner.putArgument("pkg_vpnagent", agentPackageName);
                        runner.putArgument("falcon_ip", VpnGlobalConfig.FALCON_API_IP.value());
                        runner.putArgument("transfer_rpc_ip", VpnGlobalConfig.TRANSFER_RPC_IP.value());
                        runner.putArgument("hostname", String.format("%s.syscxp.com", self.getHostIp().replaceAll("\\.", "-")));

                        UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(restf.getBaseUrl());
                        ub.path(new StringBind(AnsibleConstant.AGENT_ANSIBLE_LOG_PATH_FROMAT).bind("uuid", self.getUuid()).toString());
                        String postUrl = ub.build().toString();

                        runner.putArgument("post_url", postUrl);
                        runner.run(new Completion(trigger) {
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
                    String __name__ = "echo-host";

                    @Override
                    public void run(final FlowTrigger trigger, Map data) {
                        restf.echo(echoPath, new Completion(trigger) {
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
                    String __name__ = "prepare-host-env";

                    @Override
                    public void run(FlowTrigger trigger, Map data) {
                        String script = "which iptables > /dev/null && iptables -C FORWARD -j REJECT --reject-with icmp-host-prohibited > /dev/null 2>&1 && iptables -D FORWARD -j REJECT --reject-with icmp-host-prohibited > /dev/null 2>&1 || true";
                        try {
                            runShell(script);
                            trigger.next();
                        } catch (Exception e) {
                            trigger.fail(errf.instantiateErrorCode(HostErrors.CONNECTION_ERROR, e.getMessage()));
                        }
                    }
                });

                error(new FlowErrorHandler(complete) {
                    @Override
                    public void handle(ErrorCode errCode, Map data) {
                        complete.fail(errCode);
                    }
                });

                done(new FlowDoneHandler(complete) {
                    @Override
                    public void handle(Map data) {
                        continueConnect(info.isNewAdded(), complete);
                    }
                });
            }
        }).start();
    }

    private ErrorCode connectToAgent() {
        ErrorCode errCode = null;
        try {
            ConnectCmd cmd = new ConnectCmd();
            cmd.setHostUuid(self.getUuid());
            cmd.setSendCommandUrl(restf.getSendCommandUrl());
            cmd.setIptablesRules(VpnGlobalProperty.IPTABLES_RULES);
            ConnectResponse rsp = restf.syncJsonPost(connectPath, cmd, ConnectResponse.class);
            if (!rsp.isSuccess()) {
                errCode = operr("unable to connect to host[uuid:%s, ip:%s, url:%s], because %s", self.getUuid(), self.getHostIp(), connectPath,
                        rsp.getError());
            }
        } catch (RestClientException e) {
            errCode = operr("unable to connect to host[uuid:%s, ip:%s, url:%s], because %s", self.getUuid(), self.getHostIp(),
                    connectPath, e.getMessage());
        } catch (Throwable t) {
            LOGGER.warn(t.getMessage(), t);
            errCode = errf.throwableToInternalError(t);
        }

        return errCode;
    }

    private void continueConnect(final boolean newAdded, final Completion completion) {
        ErrorCode errCode = connectToAgent();
        if (errCode != null) {
            throw new OperationFailureException(errCode);
        }

        FlowChain chain = FlowChainBuilder.newSimpleFlowChain();
        chain.setName(String.format("continue-connecting-host-%s-%s", self.getHostIp(), self.getUuid()));
        for (VpnHostConnectExtensionPoint extp : factory.getConnectExtensions()) {
            VpnHostConnectedContext ctx = new VpnHostConnectedContext();
            ctx.setInventory((VpnHostInventory) getSelfInventory());
            ctx.setNewAddedHost(newAdded);

            chain.then(extp.createHostConnectingFlow(ctx));
        }
        chain.allowEmptyFlow();
        chain.done(new FlowDoneHandler(completion) {
            @Override
            public void handle(Map data) {
                completion.success();
            }
        }).error(new FlowErrorHandler(completion) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                String err = String.format("connection error for Vpn host[uuid:%s, ip:%s]", self.getUuid(),
                        self.getHostIp());
                completion.fail(errf.instantiateErrorCode(HostErrors.CONNECTION_ERROR, err, errCode));
            }
        }).start();
    }

    @Override
    protected HostInventory getSelfInventory() {
        return VpnHostInventory.valueOf(getSelf());
    }

    @Override
    protected void deleteHook() {
        LOGGER.debug(String.format("Host: %s is being deleted", self.getName()));
        dbf.removeByPrimaryKey(self.getUuid(), VpnHostVO.class);
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        try {
            if (msg instanceof APIMessage) {
                handleApiMessage((APIMessage) msg);
            } else {
                handleLocalMessage(msg);
            }
        } catch (Exception e) {
            bus.logExceptionWithMessageDump(msg, e);
            bus.replyErrorByMessageType(msg, e);
        }
    }

    @Override
    protected void handleApiMessage(APIMessage msg) {
        super.handleApiMessage(msg);
    }

    @Override
    protected void handleLocalMessage(Message msg) {
        super.handleLocalMessage(msg);
    }

    private SshResult runShell(String script) {
        Ssh ssh = new Ssh();
        ssh.setHostname(self.getHostIp());
        ssh.setPort(getSelf().getSshPort());
        ssh.setUsername(getSelf().getUsername());
        ssh.setPassword(getSelf().getPassword());
        ssh.shell(script);
        return ssh.runAndClose();
    }

    @Override
    protected HostVO updateHost(APIUpdateHostMsg msg) {
        if (!(msg instanceof APIUpdateVpnHostMsg)) {
            return super.updateHost(msg);
        }

        VpnHostVO vo = (VpnHostVO) super.updateHost(msg);
        vo = vo == null ? getSelf() : vo;

        APIUpdateVpnHostMsg umsg = (APIUpdateVpnHostMsg) msg;
        if (umsg.getPublicIp() != null) {
            vo.setPublicIp(umsg.getPublicIp());
        }
        if (umsg.getUsername() != null) {
            vo.setUsername(umsg.getUsername());
        }
        if (umsg.getUsername() != null) {
            vo.setUsername(umsg.getUsername());
        }
        if (umsg.getNodeUuid() != null) {
            vo.setNodeUuid(umsg.getNodeUuid());
        }
        if (umsg.getPassword() != null && !umsg.getPassword().equals("")) {
            vo.setPassword(umsg.getPassword());
        }
        if (umsg.getSshPort() != null && NetworkUtils.isLegalPort(umsg.getSshPort())) {
            vo.setSshPort(umsg.getSshPort());
        }
        return vo;
    }
}
