package com.syscxp.tunnel.header.host;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.ansible.AnsibleGlobalProperty;
import com.syscxp.core.ansible.AnsibleRunner;
import com.syscxp.core.ansible.SshFileMd5Checker;
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
import com.syscxp.header.host.HostVO;
import com.syscxp.header.rest.JsonAsyncRESTCallback;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.ShellUtils;
import com.syscxp.utils.StringBind;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;
import com.syscxp.utils.path.PathUtil;
import com.syscxp.utils.ssh.Ssh;
import com.syscxp.utils.ssh.SshResult;
import com.syscxp.utils.ssh.SshShell;
import com.syscxp.tunnel.header.host.MonitorAgentCommands.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.operr;

public class MonitorHost extends HostBase implements Host {
    private static final CLogger logger = Utils.getLogger(MonitorHost.class);

    @Autowired
    @Qualifier("MonitorHostFactory")
    private MonitorHostFactory factory;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;

    private MonitorHostContext context;

    // ///////////////////// REST URL //////////////////////////
    private String baseUrl;
    private String connectPath;
    private String pingPath;
    private String echoPath;

    private String agentPackageName = MonitorGlobalProperty.AGENT_PACKAGE_NAME;


    protected MonitorHost(HostVO self, MonitorHostContext context) {
        super(self);

        this.context = context;
        baseUrl = context.getBaseUrl();

        UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(baseUrl);
        ub.path(MonitorConstant.AGENT_CONNECT_PATH);
        connectPath = ub.build().toUriString();

        ub = UriComponentsBuilder.fromHttpUrl(baseUrl);
        ub.path(MonitorConstant.AGENT_PING_PATH);
        pingPath = ub.build().toUriString();

        ub = UriComponentsBuilder.fromHttpUrl(baseUrl);
        ub.path(MonitorConstant.AGENT_ECHO_PATH);
        echoPath = ub.build().toString();

    }

    private MonitorHostVO getSelf() {
        return (MonitorHostVO) self;
    }

    @Override
    protected void pingHook(final Completion completion) {
        FlowChain chain = FlowChainBuilder.newShareFlowChain();
        chain.setName(String.format("ping-monitor-host-%s", self.getUuid()));
        chain.then(new ShareFlow() {
            @Override
            public void setup() {
                flow(new NoRollbackFlow() {
                    String __name__ = "ping-host";

                    @AfterDone
                    List<Runnable> afterDone = new ArrayList<>();

                    private boolean isSshPortOpen() {
                        if (CoreGlobalProperty.UNIT_TEST_ON) {
                            return false;
                        }
                        return NetworkUtils.isRemotePortOpen(self.getHostIp(), getSelf().getSshPort(), 2);
                    }

                    @Override
                    public void run(FlowTrigger trigger, Map data) {
                        PingCmd cmd = new PingCmd();
                        cmd.hostUuid = self.getUuid();
                        restf.asyncJsonPost(pingPath, cmd, new JsonAsyncRESTCallback<PingResponse>(trigger) {
                            @Override
                            public void fail(ErrorCode err) {
                                if (isSshPortOpen()) {
                                    logger.debug(String.format("ssh port of host[uuid:%s, ip:%s] is open, ping success",
                                            self.getUuid(), self.getHostIp()));
                                    trigger.next();
                                } else {
                                    trigger.fail(err);
                                }
                            }

                            @Override
                            public void success(PingResponse ret) {
                                if (ret.isSuccess()) {
                                    if (!self.getUuid().equals(ret.getHostUuid())) {
                                        afterDone.add(() -> {
                                            String info = String.format("detected abnormal status[host uuid change, " +
                                                    "expected: %s but: %s] of monitoragent, it's mainly caused by monitoragent " +
                                                    "restarts behind syscxp management server. Report this to ping task, " +
                                                    "it will issue a reconnect soon", self.getUuid(), ret.getHostUuid());
                                            logger.warn(info);
                                            ReconnectHostMsg rmsg = new ReconnectHostMsg();
                                            rmsg.setHostUuid(self.getUuid());
                                            bus.makeTargetServiceIdByResourceUuid(rmsg, MonitorConstant.SERVICE_ID, self.getUuid());
                                            bus.send(rmsg);
                                        });
                                    }

                                    trigger.next();
                                } else {
                                    if (isSshPortOpen()) {
                                        logger.debug(String.format("ssh port of host[uuid:%s, ip:%s] is open, ping success",
                                                self.getUuid(), self.getHostIp()));
                                        trigger.next();
                                    } else {
                                        trigger.fail(operr(ret.getError()));
                                    }
                                }
                            }

                            @Override
                            public Class<PingResponse> getReturnClass() {
                                return PingResponse.class;
                            }
                        }, TimeUnit.SECONDS, 60);
                    }
                });

                flow(new NoRollbackFlow() {
                    String __name__ = "call-ping-no-failure-plugins";

                    @Override
                    public void run(FlowTrigger trigger, Map data) {
                        List<MonitorPingAgentNoFailureExtensionPoint> exts = pluginRgty.getExtensionList
                                (MonitorPingAgentNoFailureExtensionPoint.class);
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

                        MonitorHostInventory inv = (MonitorHostInventory) getSelfInventory();
                        for (MonitorPingAgentNoFailureExtensionPoint ext : exts) {
                            ext.monitorPingAgentNoFailure(inv, new NoErrorCompletion(latch) {
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
                        List<MonitorPingAgentExtensionPoint> exts = pluginRgty.getExtensionList(MonitorPingAgentExtensionPoint.class);
                        Iterator<MonitorPingAgentExtensionPoint> it = exts.iterator();
                        callPlugin(it, trigger);
                    }

                    private void callPlugin(Iterator<MonitorPingAgentExtensionPoint> it, FlowTrigger trigger) {
                        if (!it.hasNext()) {
                            trigger.next();
                            return;
                        }

                        MonitorPingAgentExtensionPoint ext = it.next();
                        logger.debug(String.format("calling KVMPingAgentExtensionPoint[%s]", ext.getClass()));
                        ext.monitorPingAgent((MonitorHostInventory) getSelfInventory(), new Completion(trigger) {
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
    protected int getVmMigrateQuantity() {
        return 1;
    }

    @Override
    protected void changeStateHook(HostState current, HostStateEvent stateEvent, HostState next) {
        logger.debug(String.format("Host: %s changed state from %s to %s by %s", self.getName(), current, next, stateEvent));
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
                        SshResult ret = sshShell.runCommand(String.format("curl --connect-timeout 10 %s", restf.getCallbackUrl()));

                        if (ret.isSshFailure()) {
                            throw new OperationFailureException(operr("unable to connect to Host[ip:%s, username:%s, sshPort:%d] to check the management node connectivity," +
                                    "please check if username/password is wrong; %s", self.getHostIp(), getSelf().getUsername(), getSelf().getSshPort(), ret.getExitErrorMessage()));
                        } else if (ret.getReturnCode() != 0) {
                            throw new OperationFailureException(operr("the host[ip:%s] cannot access the management node's callback url. It seems" +
                                            " that the host cannot reach the management IP[%s]. %s %s", self.getHostIp(), Platform.getManagementServerIp(),
                                    ret.getStderr(), ret.getExitErrorMessage()));
                        }

                        trigger.next();
                    }
                });

                flow(new NoRollbackFlow() {
                    String __name__ = "apply-ansible-playbook";

                    @Override
                    public void run(final FlowTrigger trigger, Map data) {
                        String srcPath = PathUtil.findFileOnClassPath(String.format("ansible/monitor/%s", agentPackageName), true).getAbsolutePath();
                        String destPath = String.format("/var/lib/syscxp/agent/package/%s", agentPackageName);
                        SshFileMd5Checker checker = new SshFileMd5Checker();
                        checker.setUsername(getSelf().getUsername());
                        checker.setPassword(getSelf().getPassword());
                        checker.setSshPort(getSelf().getSshPort());
                        checker.setTargetIp(getSelf().getHostIp());
                        checker.addSrcDestPair(SshFileMd5Checker.SYSCXPLIB_SRC_PATH, String.format("/var/lib/syscxp/agent/package/%s", AnsibleGlobalProperty.SYSCXPLIB_PACKAGE_NAME));
                        checker.addSrcDestPair(srcPath, destPath);

                        AnsibleRunner runner = new AnsibleRunner();
                        runner.installChecker(checker);
                        runner.setAgentPort(MonitorGlobalProperty.AGENT_PORT);
                        runner.setTargetIp(getSelf().getHostIp());
                        runner.setPlayBookName(MonitorConstant.ANSIBLE_PLAYBOOK_NAME);
                        runner.setUsername(getSelf().getUsername());
                        runner.setPassword(getSelf().getPassword());
                        runner.setSshPort(getSelf().getSshPort());
                        if (info.isNewAdded()) {
                            runner.putArgument("init", "true");
                            runner.setFullDeploy(true);
                        }
                        runner.putArgument("pkg_monitoragent", agentPackageName);
                        runner.putArgument("hostname", String.format("%s.syscxp.com", self.getHostIp().replaceAll("\\.", "-")));

                        UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(restf.getBaseUrl());
                        ub.path(new StringBind(MonitorConstant.AGENT_ANSIBLE_LOG_PATH_FROMAT).bind("uuid", self.getUuid()).toString());
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
                        runShell(script);
                        trigger.next();
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
            cmd.setIptablesRules(MonitorGlobalProperty.IPTABLES_RULES);
            ConnectResponse rsp = restf.syncJsonPost(connectPath, cmd, ConnectResponse.class);
            if (!rsp.isSuccess() || !rsp.isIptablesSucc()) {
                errCode = operr("unable to connect to host[uuid:%s, ip:%s, url:%s], because %s", self.getUuid(), self.getHostIp(), connectPath,
                        rsp.getError());
            }
        } catch (RestClientException e) {
            errCode = operr("unable to connect to host[uuid:%s, ip:%s, url:%s], because %s", self.getUuid(), self.getHostIp(),
                    connectPath, e.getMessage());
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
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
        for (MonitorHostConnectExtensionPoint extp : factory.getConnectExtensions()) {
            MonitorHostConnectedContext ctx = new MonitorHostConnectedContext();
            ctx.setInventory((MonitorHostInventory) getSelfInventory());
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
                String err = String.format("connection error for monitor host[uuid:%s, ip:%s]", self.getUuid(),
                        self.getHostIp());
                completion.fail(errf.instantiateErrorCode(HostErrors.CONNECTION_ERROR, err, errCode));
            }
        }).start();
    }

    private String buildUrl(String path) {
        UriComponentsBuilder ub = UriComponentsBuilder.newInstance();
        ub.scheme(MonitorGlobalProperty.AGENT_URL_SCHEME);
        ub.host(self.getHostIp());
        ub.port(MonitorGlobalProperty.AGENT_PORT);
        if (!"".equals(MonitorGlobalProperty.AGENT_URL_ROOT_PATH)) {
            ub.path(MonitorGlobalProperty.AGENT_URL_ROOT_PATH);
        }
        ub.path(path);
        return ub.build().toUriString();
    }

    @Override
    protected HostInventory getSelfInventory() {
        return MonitorHostInventory.valueOf(getSelf());
    }

    @Override
    protected void deleteHook() {
        logger.debug(String.format("Host: %s is being deleted", self.getName()));
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
        if (!(msg instanceof APIUpdateMonitorHostMsg)) {
            return super.updateHost(msg);
        }

        MonitorHostVO vo = (MonitorHostVO) super.updateHost(msg);
        vo = vo == null ? getSelf() : vo;

        APIUpdateMonitorHostMsg umsg = (APIUpdateMonitorHostMsg) msg;
        if (umsg.getNodeUuid() != null) {
            vo.setNodeUuid(umsg.getNodeUuid());
        }
        if (umsg.getUsername() != null) {
            vo.setUsername(umsg.getUsername());
        }
        if (umsg.getPassword() != null) {
            vo.setPassword(umsg.getPassword());
        }
        if (umsg.getSshPort() != null && umsg.getSshPort() > 0 && umsg.getSshPort() <= 65535) {
            vo.setSshPort(umsg.getSshPort());
        }
        return vo;
    }
}
