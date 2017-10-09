package com.syscxp.core.ansible;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.utils.ShellResult;
import com.syscxp.utils.ShellUtils;
import com.syscxp.utils.StringDSL.StringWrapper;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.ssh.SshResult;
import com.syscxp.utils.ssh.SshShell;

import static com.syscxp.core.Platform.operr;

import java.util.HashMap;
import java.util.Map;

import static com.syscxp.utils.StringDSL.ln;

/**
 * Created by frank on 12/6/2015.
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class SshFolderMd5Checker implements AnsibleChecker {
    private static final CLogger logger = Utils.getLogger(SshFolderMd5Checker.class);

    @Autowired
    private ErrorFacade errf;

    private String srcFolder;
    private String dstFolder;
    private String username;
    private String password;
    private String hostname;
    private int port = 22;

    private static StringWrapper script = ln(
            "if [ ! -d {0} ]; then",
            "echo \"cannot find the folder {0}\"",
            "exit 101",
            "fi",
            "files=`find {0} -type f`",
            "for f in $files",
            "do",
            "md5sum $f",
            "done",
            "exit 0"
    );

    public String getSrcFolder() {
        return srcFolder;
    }

    public void setSrcFolder(String srcFolder) {
        this.srcFolder = srcFolder;
    }

    public String getDstFolder() {
        return dstFolder;
    }

    public void setDstFolder(String dstFolder) {
        this.dstFolder = dstFolder;
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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean needDeploy() {
        String srcScript = script.format(srcFolder);
        ShellResult srcRes = ShellUtils.runAndReturn(srcScript, false);
        if (!srcRes.isReturnCode(0)) {
            throw new OperationFailureException(operr("cannot check md5sum of files in the folder[%s].\nstdout:%s\nstderr:%s", srcFolder,
                            srcRes.getStdout(), srcRes.getStderr()));
        }

        String dstScript = script.format(dstFolder);
        SshShell ssh = new SshShell();
        ssh.setHostname(hostname);
        ssh.setUsername(username);
        ssh.setPassword(password);
        ssh.setPort(port);
        SshResult dstRes = ssh.runScript(dstScript);
        if (dstRes.getReturnCode() == 101) {
            // dst folder doesn't existing
            return true;
        } else if (dstRes.getReturnCode() != 0) {
            throw new OperationFailureException(operr("cannot check md5sum of files in the folder[%s] on the host[ip:%s].\nstdout:%s\nstderr:%s",
                            dstFolder, hostname, dstRes.getStdout(), dstRes.getStderr()));
        }

        Map<String, String> srcMd5sum = new HashMap<String, String>();
        Map<String, String> dstMd5sum = new HashMap<String, String>();

        for (String s : srcRes.getStdout().split("\n")) {
            if (StringUtils.isBlank(s)) {
                continue;
            }

            String[] pair = s.split(" +");
            String fileName = pair[1].replaceAll(srcFolder, "");
            srcMd5sum.put(fileName, pair[0]);
        }

        for (String s : dstRes.getStdout().split("\n")) {
            if (StringUtils.isBlank(s)) {
                continue;
            }

            String[] pair = s.split(" +");
            String fileName = pair[1].replaceAll(dstFolder, "");
            dstMd5sum.put(fileName, pair[0]);
        }

        if (dstMd5sum.size() != srcMd5sum.size()) {
            logger.debug(String.format("file number mismatch. the source folder[%s] contains %s files while the destination" +
                    " folder[%s] on the host[ip:%s] contains %s files. Need to deploy the agent", srcFolder, dstFolder,
                    srcMd5sum.size(), hostname, dstMd5sum.size()));
            return true;
        }

        for (Map.Entry<String, String> e : srcMd5sum.entrySet()) {
            String fileName = e.getKey();
            String srcMd5 = e.getValue();
            String dstMd5 = dstMd5sum.get(fileName);
            if (dstMd5 == null) {
                logger.debug(String.format("cannot find the file[%s] in the folder[%s] of the host[ip:%s]. Need to deploy agent",
                        fileName, dstFolder, hostname));
                return true;
            }

            if (!srcMd5.equals(dstMd5)) {
                logger.debug(String.format("md5[src md5: %s, dst md5: %s, dst host ip:%s] of the file[%s] changed. Need to deploy agent",
                        srcMd5, dstMd5, hostname, fileName));
                return true;
            }
        }

        logger.debug(String.format("no files changed on the dest host[ip:%s]", hostname));
        return false;
    }

    @Override
    public void deleteDestFile() {
        // do nothing
    }
}
