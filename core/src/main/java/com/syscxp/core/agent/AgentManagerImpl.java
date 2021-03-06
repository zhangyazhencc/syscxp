package com.syscxp.core.agent;

import com.syscxp.core.ansible.SshFolderMd5Checker;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.header.core.workflow.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.ansible.AnsibleNeedRun;
import com.syscxp.core.ansible.AnsibleRunner;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.defer.Defer;
import com.syscxp.core.defer.Deferred;
import com.syscxp.core.thread.ChainTask;
import com.syscxp.core.thread.SyncTaskChain;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.core.workflow.ShareFlow;
import com.syscxp.header.AbstractService;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.ShellUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;
import com.syscxp.utils.path.PathUtil;
import com.syscxp.utils.ssh.Ssh;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.syscxp.utils.CollectionDSL.e;
import static com.syscxp.utils.CollectionDSL.map;
import static com.syscxp.utils.StringDSL.ln;

/**
 * Created by frank on 12/5/2015.
 */
public class AgentManagerImpl extends AbstractService implements AgentManager {
    private static final CLogger logger = Utils.getLogger(AgentManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private RESTFacade restf;

    private Map<String, Map<String, AgentStruct>> agents = new HashMap<String, Map<String, AgentStruct>>();
    private String srcRootFolder;

    public static final String ECHO_PATH = "/host/echo";
    public static final String INIT_PATH = "/host/init";

    public static final class InitAgentHostCmd {
        public Map<String, Object> Config = new HashMap<String, Object>();
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof DeployAgentMsg) {
            handle((DeployAgentMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    void init() {
        if (CoreGlobalProperty.UNIT_TEST_ON) {
            return;
        }

        ShellUtils.run(String.format("mkdir -p %s", AgentConstant.SRC_ANSIBLE_ROOT), false);
        File srcFolder = PathUtil.findFolderOnClassPath(AgentConstant.ANSIBLE_MODULE_PATH, true);
        srcRootFolder = srcFolder.getAbsolutePath();
        ShellUtils.run(String.format("yes | cp -r %s %s", srcRootFolder, AgentConstant.SRC_ANSIBLE_ROOT), false);
    }

    private void handle(final DeployAgentMsg msg) {
        thdf.chainSubmit(new ChainTask(msg) {
            @Override
            public String getSyncSignature() {
                return String.format("deploy-agent-to-host-%s", msg.getIp());
            }

            @Override
            public void run(final SyncTaskChain chain) {
                deployAgent(msg, new NoErrorCompletion(msg, chain) {
                    @Override
                    public void done() {
                        chain.next();
                    }
                });
            }

            @Override
            public String getName() {
                return getSyncSignature();
            }
        });
    }

    private void connect(final DeployAgentMsg msg, final Completion completion) {
        FlowChain chain = FlowChainBuilder.newShareFlowChain();
        chain.setName(String.format("continue-connect-agent-host-%s:%s", msg.getIp(), msg.getAgentPort()));
        chain.then(new ShareFlow() {
            private String url(String path) {
                return String.format("http://%s:%s%s", msg.getIp(), msg.getAgentPort(), path);
            }

            @Override
            public void setup() {
                flow(new NoRollbackFlow() {
                    String __name__ = "echo-host";

                    @Override
                    public void run(final FlowTrigger trigger, Map data) {
                        restf.echo(url(ECHO_PATH), new Completion(trigger) {
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
                    String __name__= "init-host";

                    @Override
                    public void run(FlowTrigger trigger, Map data) {
                        Map<String, Object> config = new HashMap<String, Object>();
                        config.put(AgentConstant.CONFIG_COMMAND_URL, restf.getSendCommandUrl());
                        if (msg.getConfig() != null) {
                            config.putAll(msg.getConfig());
                        }

                        InitAgentHostCmd cmd = new InitAgentHostCmd();
                        cmd.Config = config;

                        restf.syncJsonPost(url(INIT_PATH), cmd, Void.class);
                        trigger.next();
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

    private void deployAgent(final DeployAgentMsg msg, final NoErrorCompletion noErrorCompletion) {
        if (msg.getAgentPort() == null) {
            throw new CloudRuntimeException("agentPort cannot be null");
        }

        final DeployAgentReply reply = new DeployAgentReply();
        Map<String, AgentStruct> m = agents.get(msg.getOwner());
        if (m == null) {
            logger.warn(String.format("no plugins found for the agent[owner:%s], skip deploying agent", msg.getOwner()));
            bus.reply(msg, reply);
            noErrorCompletion.done();
            return;
        }

        try {
            String agentYamlPath = PathUtil.join(AgentConstant.ANSIBLE_MODULE_PATH, "server", AgentConstant.ANSIBLE_PLAYBOOK_NAME);
            File agentYaml = PathUtil.findFileOnClassPath(agentYamlPath, true);

            final File tmpInclude = File.createTempFile("syscxp", "ansibleInclude");
            StringBuilder sb = new StringBuilder("---\n\n");
            for (AgentStruct s : m.values()) {
                sb.append(String.format("- include: %s\n", s.getAnsibleYaml()));
            }
            FileUtils.writeStringToFile(tmpInclude, sb.toString());

            String agentYamlContent = FileUtils.readFileToString(agentYaml);
            agentYamlContent = ln(agentYamlContent).formatByMap(map(
                    e("remoteRoot", AgentConstant.DST_ANSIBLE_ROOT),
                    e("srcRoot", srcRootFolder),
                    e("agentYamls", String.format("%s", tmpInclude.getAbsolutePath())),
                    e("outterServerIp", msg.getIp()),
                    e("outterServerPort", msg.getAgentPort().toString())
            ));

            final File tmpAgentYaml = File.createTempFile("syscxp", "ansilbeTempAgent");
            FileUtils.writeStringToFile(tmpAgentYaml, agentYamlContent);

            SshFolderMd5Checker checker = new SshFolderMd5Checker();
            checker.setPassword(msg.getPassword());
            checker.setUsername(msg.getUsername());
            if (msg.getSshPort() != null) {
                checker.setPort(msg.getSshPort());
            }
            checker.setHostname(msg.getIp());
            checker.setSrcFolder(srcRootFolder);
            checker.setDstFolder(AgentConstant.DST_ANSIBLE_ROOT);
            final boolean fileChanged = checker.needDeploy();

            if (fileChanged) {
                Ssh ssh = new Ssh();
                ssh.setPassword(msg.getPassword());
                ssh.setUsername(msg.getUsername());
                ssh.setHostname(msg.getIp());
                if (msg.getSshPort() != null) {
                    ssh.setPort(msg.getSshPort());
                }
            }

            AnsibleRunner runner = new AnsibleRunner();
            runner.setAnsibleNeedRun(new AnsibleNeedRun() {
                @Override
                public boolean isRunNeed() {
                    return fileChanged || !NetworkUtils.isRemotePortOpen(msg.getIp(), msg.getAgentPort(), (int) TimeUnit.SECONDS.toMillis(5));
                }
            });
            runner.setPassword(msg.getPassword());
            runner.setUsername(msg.getUsername());
            runner.setAgentPort(msg.getAgentPort());
            runner.setRunOnLocal(true);
            runner.setFullDeploy(msg.isDeployAnyway());
            runner.setAnsibleExecutable("ansible-playbook");
            if (msg.getSshPort() != null) {
                runner.setSshPort(msg.getSshPort());
            }
            runner.setTargetIp(msg.getIp());
            runner.setPlayBookPath(tmpAgentYaml.getAbsolutePath());
            runner.run(new Completion(msg, noErrorCompletion) {
                @Override
                @Deferred
                public void success() {
                    Defer.defer(new Runnable() {
                        @Override
                        public void run() {
                            tmpInclude.delete();
                            tmpAgentYaml.delete();
                        }
                    });

                    connect(msg, new Completion(msg, noErrorCompletion) {
                        @Override
                        public void success() {
                            bus.reply(msg, reply);
                            noErrorCompletion.done();
                        }

                        @Override
                        public void fail(ErrorCode errorCode) {
                            reply.setError(errorCode);
                            bus.reply(msg, reply);
                            noErrorCompletion.done();
                        }
                    });
                }

                @Override
                @Deferred
                public void fail(ErrorCode errorCode) {
                    Defer.defer(new Runnable() {
                        @Override
                        public void run() {
                            tmpInclude.delete();
                            tmpAgentYaml.delete();
                        }
                    });

                    reply.setError(errorCode);
                    bus.reply(msg, reply);
                    noErrorCompletion.done();
                }
            });
        } catch (IOException e) {
            throw new CloudRuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(AgentConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public void registerAgent(AgentStruct struct) {
        Map<String, AgentStruct> m = agents.get(struct.getAgentOwner());
        if (m == null) {
            m = new HashMap<String, AgentStruct>();
            agents.put(struct.getAgentOwner(), m);
        }

        AgentStruct old = m.get(struct.getAgentId());
        if (old != null) {
            throw new CloudRuntimeException(String.format("there has been an agent[id:%s] registered to the owner[%s]", struct.getAgentId(), struct.getAgentOwner()));
        }

        m.put(struct.getAgentId(), struct);
        ShellUtils.run(String.format("yes | cp -r %s %s", struct.getFileFolder(), AgentConstant.SRC_ANSIBLE_ROOT), false);
    }
}
