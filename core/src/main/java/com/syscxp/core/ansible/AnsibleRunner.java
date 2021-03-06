package com.syscxp.core.ansible;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.defer.Defer;
import com.syscxp.core.defer.Deferred;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.core.Completion;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.ShellUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;
import com.syscxp.utils.path.PathUtil;
import com.syscxp.utils.ssh.SshResult;
import com.syscxp.utils.ssh.SshShell;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.syscxp.utils.CollectionDSL.e;
import static com.syscxp.utils.CollectionDSL.map;
import static com.syscxp.utils.StringDSL.ln;

/**
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class AnsibleRunner {
    private static final CLogger logger = Utils.getLogger(AnsibleRunner.class);

    @Autowired
    private AnsibleFacade asf;
    @Autowired
    private CloudBus bus;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;

    private static String privKeyFile;
    private List<AnsibleChecker> checkers = new ArrayList<AnsibleChecker>();

    static {
        privKeyFile = PathUtil.findFileOnClassPath(AnsibleConstant.RSA_PRIVATE_KEY).getAbsolutePath();
    }

    {
        fullDeploy = AnsibleGlobalProperty.FULL_DEPLOY;
    }

    private String targetIp;
    private String username;
    private String password;
    private String privateKey;
    private int sshPort = 22;
    private String playBookName;
    private String playBookPath;
    private Map<String, Object> arguments = new HashMap<String, Object>();
    private int agentPort;
    private boolean localPublicKey;
    private boolean fullDeploy;
    private boolean runOnLocal;
    private AnsibleNeedRun ansibleNeedRun;
    private String ansibleExecutable;

    public String getAnsibleExecutable() {
        return ansibleExecutable;
    }

    public void setAnsibleExecutable(String ansibleExecutable) {
        this.ansibleExecutable = ansibleExecutable;
    }

    public String getPlayBookPath() {
        return playBookPath;
    }

    public void setPlayBookPath(String playBookPath) {
        this.playBookPath = playBookPath;
    }

    public AnsibleNeedRun getAnsibleNeedRun() {
        return ansibleNeedRun;
    }

    public void setAnsibleNeedRun(AnsibleNeedRun ansibleNeedRun) {
        this.ansibleNeedRun = ansibleNeedRun;
    }

    public boolean isRunOnLocal() {
        return runOnLocal;
    }

    public void setRunOnLocal(boolean runOnLocal) {
        this.runOnLocal = runOnLocal;
    }

    public boolean isLocalPublicKey() {
        return localPublicKey;
    }

    public void setLocalPublicKey(boolean localPublicKey) {
        this.localPublicKey = localPublicKey;
    }

    public void putArgument(String key, Object value) {
        arguments.put(key, value);
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public int getSshPort() {
        return sshPort;
    }

    public void setSshPort(int sshPort) {
        this.sshPort = sshPort;
    }

    public String getPlayBookName() {
        return playBookName;
    }

    public void setPlayBookName(String playBookName) {
        this.playBookName = playBookName;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    public int getAgentPort() {
        return agentPort;
    }

    public void setAgentPort(int agentPort) {
        this.agentPort = agentPort;
    }

    public boolean isFullDeploy() {
        return fullDeploy;
    }

    public void setFullDeploy(boolean fullDeploy) {
        this.fullDeploy = fullDeploy;
    }

    private void setupPublicKey() throws IOException {
        File pubKeyFile = PathUtil.findFileOnClassPath(AnsibleConstant.RSA_PUBLIC_KEY);
        String script = PathUtil.findFileOnClassPath(AnsibleConstant.IMPORT_PUBLIC_KEY_SCRIPT_PATH, true).getAbsolutePath();
        if (localPublicKey) {
            ShellUtils.run(String.format("sh %s %s", script, pubKeyFile.getAbsolutePath()));
        } else {
            setupPublicKeyOnRemote();
        }
    }

    @Deferred
    private void setupPublicKeyOnRemote() {
        String script = ln(
                "#!/bin/sh",
                "if [ ! -d ~/.ssh ]; then",
                "mkdir -p ~/.ssh",
                "chmod 700 ~/.ssh",
                "fi",
                "if [ ! -f ~/.ssh/authorized_keys ]; then",
                "touch ~/.ssh/authorized_keys",
                "chmod 600 ~/.ssh/authorized_keys",
                "fi",
                "pub_key='{pubkey}'",
                "grep \"$pub_key\" ~/.ssh/authorized_keys > /dev/null",
                "if [ $? -eq 1 ]; then",
                "echo \"$pub_key\" >> ~/.ssh/authorized_keys",
                "fi",
                "if [ -x /sbin/restorecon ]; then",
                "/sbin/restorecon ~/.ssh ~/.ssh/authorized_keys",
                "fi",
                "exit 0"
        ).formatByMap(map(
                e("pubkey", asf.getPublicKey())
        ));

        SshShell ssh = new SshShell();
        ssh.setHostname(targetIp);
        ssh.setPassword(password);
        ssh.setPort(sshPort);
        ssh.setUsername(username);

        if (privateKey != null) {
            try {
                final File tempKeyFile = File.createTempFile("syscxp", "tmp");
                FileUtils.writeStringToFile(tempKeyFile, privateKey);

                ShellUtils.run(String.format("chmod 600 %s", tempKeyFile.getAbsolutePath()));

                Defer.defer(new Runnable() {
                    @Override
                    public void run() {
                        tempKeyFile.delete();
                    }
                });

                ssh.setPrivateKeyFile(tempKeyFile.getAbsolutePath());
            } catch (IOException e) {
                throw new CloudRuntimeException(e);
            }
        }

        SshResult res = ssh.runScript(script);
        res.raiseExceptionIfFailed();
    }

    private void callAnsible(final Completion completion) {
        RunAnsibleMsg msg = new RunAnsibleMsg();
        msg.setTargetIp(targetIp);
        msg.setPrivateKeyFile(privKeyFile);
        msg.setArguments(arguments);
        msg.setAnsibleExecutable(ansibleExecutable);

        if (playBookPath != null) {
            msg.setPlayBookPath(playBookPath);
        } else {
            msg.setPlayBookPath(PathUtil.join(AnsibleConstant.ROOT_DIR, playBookName));
        }

        if (runOnLocal) {
            bus.makeLocalServiceId(msg, AnsibleConstant.SERVICE_ID);
        } else {
            bus.makeTargetServiceIdByResourceUuid(msg, AnsibleConstant.SERVICE_ID, targetIp);
        }

        bus.send(msg, new CloudBusCallBack(completion) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    completion.success();
                } else {
                    cleanup();
                    completion.fail(reply.getError());
                }
            }
        });
    }

    private boolean runChecker() {
        for (AnsibleChecker checker : checkers) {
            if (checker.needDeploy()) {
                logger.debug(String.format("checker[%s] reports deploy is needed", checker.getClass()));
                return true;
            }
        }

        return false;
    }

    private boolean isNeedRun() {
        List<String> ignoreAgentPortModule = new ArrayList<String>();
        ignoreAgentPortModule.add("imagestorebackupstorage.py");
        if (isFullDeploy()) {
            logger.debug("Ansible.fullDeploy is set, run ansible anyway");
            return true;
        }

        if (ansibleNeedRun != null) {
            return ansibleNeedRun.isRunNeed();
        }

        boolean changed = asf.isModuleChanged(playBookName);
        if (changed) {
            logger.debug(String.format("ansible module[%s] changed, run ansible", playBookName));
            return true;
        }

        if (agentPort != 0) {
            boolean opened = NetworkUtils.isRemotePortOpen(targetIp, agentPort, (int) TimeUnit.SECONDS.toMillis(5));
            if (!opened) {
                logger.debug(String.format("agent port[%s] on target ip[%s] is not opened, run ansible[%s]", agentPort, targetIp, playBookName));
                return true;
            }

            if (runChecker()) {
                return true;
            }

            logger.debug(String.format("agent port[%s] on target ip[%s] is opened, ansible module[%s] is not changed, skip to run ansible", agentPort, targetIp, playBookName));
            return false;
        } else if (ignoreAgentPortModule.contains(playBookName)) {
            logger.debug(String.format("module %s will not check agent port, only check md5sum", playBookName));
            if (runChecker()) {
                logger.debug(String.format("module %s md5sum changed, run ansible", playBookName));
                return true;
            } else {
                logger.debug(String.format("module %s md5sum not change, skip to run ansible", playBookName));
                return false;
            }
        }

        logger.debug("agent port is not set, run ansible anyway");
        return true;
    }

    private void cleanup() {
        // deleting source files. Then next time ansible is called, AnsibleChecker returns false that lets ansible run
        for (AnsibleChecker checker : checkers) {
            checker.deleteDestFile();
        }
    }

    public void run(Completion completion) {
        try {
            if (!isNeedRun()) {
                completion.success();
                return;
            }

            putArgument("pip_url", AnsibleConstant.PIP_URL);
            putArgument("trusted_host", AnsibleConstant.TRUSTED_HOST);
            putArgument("yum_server", AnsibleConstant.YUM_SERVER);
            putArgument("remote_user", username);
            if (password != null && !password.isEmpty()) {
                putArgument("remote_pass", password);
            }
            putArgument("remote_port", Integer.toString(sshPort));

            logger.debug(String.format("starts to run ansible[%s]", playBookPath == null ? playBookName : playBookPath));
            new PrepareAnsible().setTargetIp(targetIp).prepare();
            setupPublicKey();
            callAnsible(completion);
        } catch (Exception e) {
            completion.fail(errf.stringToOperationError(e.getMessage()));
        }
    }

    public List<AnsibleChecker> getCheckers() {
        return checkers;
    }

    public void installChecker(AnsibleChecker checker) {
        checkers.add(checker);
    }

    public String getPipUrl(){

        return  restf.getBaseUrl() + AnsibleConstant.PIP_URL;

    }
}
