package com.syscxp.tunnel.header.host;

import com.syscxp.header.message.MessageReply;

/**
 * Created by xing5 on 2016/3/14.
 */
public class MonitorRunShellReply extends MessageReply {
    private int returnCode;
    private String stdout;
    private String stderr;

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }
}
