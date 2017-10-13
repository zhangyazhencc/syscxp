package com.syscxp.header.vpn;

import com.syscxp.header.rest.RestAPIState;

/**
 */
public class VpnAgentResponse {
    // 任务结果
    private TaskResult result;
    // 运行状态
    private RunStatus status;

    public RunStatus getStatus() {
        return status;
    }

    public void setStatus(RunStatus status) {
        this.status = status;
    }

    public TaskResult getResult() {
        return result;
    }

    public void setResult(TaskResult result) {
        this.result = result;
    }

    public static class TaskResult {
        private String message;
        private boolean success = false;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

    /**
     * 物理机或VPN运行状态
     */
    public enum RunStatus {
        DOWN,
        UP,
        UNKOWN
    }
}
