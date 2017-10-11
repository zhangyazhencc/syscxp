package com.syscxp.core.ansible;

import com.syscxp.utils.ShellResult;
import com.syscxp.utils.ShellUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.path.PathUtil;
import com.syscxp.utils.ssh.Ssh;
import com.syscxp.utils.ssh.SshResult;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class SshFileMd5Checker implements AnsibleChecker {
    private static final CLogger logger = Utils.getLogger(SshFileMd5Checker.class);

    private List<SrcDestPair> srcDestPairs = new ArrayList<SrcDestPair>();
    private String username;
    private String password;
    private String privateKey;
    private String targetIp;
    private int sshPort = 22;

    private class SrcDestPair {
        private SrcDestPair(String srcPath, String destPath) {
            this.srcPath = srcPath;
            this.destPath = destPath;
        }

        String srcPath;
        String destPath;
    }

    public static final String syscxpLIB_SRC_PATH = PathUtil.findFileOnClassPath(String.format("ansible/syscxplib/%s", AnsibleGlobalProperty.syscxpLIB_PACKAGE_NAME), true).getAbsolutePath();

    @Override
    public boolean needDeploy() {
        Ssh ssh = new Ssh();
        ssh.setUsername(username).setPrivateKey(privateKey)
                .setPassword(password).setPort(sshPort)
                .setHostname(targetIp);
        try {
            for (SrcDestPair b : srcDestPairs) {
                String sourceFilePath = b.srcPath;
                String destFilePath = b.destPath;

                ssh.command(String.format("md5sum %s", destFilePath));
                SshResult ret = ssh.run();
                if (ret.getReturnCode() != 0) {
                    return true;
                }
                ssh.reset();

                String destMd5 =  ret.getStdout().split(" ")[0];
                ShellResult sret = ShellUtils.runAndReturn(String.format("md5sum %s", sourceFilePath));
                sret.raiseExceptionIfFail();
                String srcMd5 = sret.getStdout().split(" ")[0];
                if (!destMd5.equals(srcMd5)) {
                    logger.debug(String.format("file MD5 changed, src[%s, md5:%s] dest[%s, md5, %s]", sourceFilePath,
                            srcMd5, destFilePath, destMd5));
                    return true;
                }
            }
        } finally {
            ssh.close();
        }

        return false;
    }

    @Override
    public void deleteDestFile() {
        for (SrcDestPair b : srcDestPairs) {
            String destFilePath = b.destPath;
            Ssh ssh = new Ssh();
            ssh.setUsername(username).setPrivateKey(privateKey)
                    .setPassword(password).setPort(sshPort)
                    .setHostname(targetIp).command(String.format("rm -f %s", destFilePath)).runAndClose();
            logger.debug(String.format("delete dest file[%s]", destFilePath));
        }
    }

    public void addSrcDestPair(String srcFilePath, String destFilePath) {
        srcDestPairs.add(new SrcDestPair(srcFilePath, destFilePath));
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

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }
}