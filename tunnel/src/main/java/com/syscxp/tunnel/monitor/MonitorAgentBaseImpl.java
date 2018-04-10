package com.syscxp.tunnel.monitor;

import com.syscxp.header.Component;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.monitor.MonitorAgentConstant;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public class MonitorAgentBaseImpl implements MonitorAgentBase, Component {

    private static final CLogger logger = Utils.getLogger(MonitorAgentBaseImpl.class);

    @Autowired
    private RESTFacade restf;

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-11-02.
     * @Description: Agent API返回对象.
     */
    public static class AgentResponse {
        private String msg;
        private boolean success;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

    /***
     * 获取监控agent 服务url
     * @param hostIp
     * @param method
     * @return
     */
    private String getUrl(String hostIp, String method) {
        String url = "http://" + hostIp + ":" + MonitorAgentConstant.SERVER_PORT + method;

        return url;
    }

    /***
     * 发送监控agent命令
     * @param hostIp:监控主机ip
     * @param method：调用方法
     * @param command：下发参数
     * @param returnClass
     * @param <T>
     * @return
     */
    public <T> T httpCall(String hostIp, String method, String command, Class<T> returnClass) {
        String url = getUrl(hostIp, method);
        Class baseResp = AgentResponse.class;

        if ((returnClass != baseResp) && !returnClass.getClass().isAssignableFrom(baseResp.getClass()))
            throw new IllegalClassException("return class should extend MonitorAgentBaseImpl.AgentResponse");

        logger.info(String.format("======= [Agent Request] url: %s command: %s", url, command));

        String resp = restf.syncJsonPost(url, command);

        logger.info(String.format("======= [Agent Response] %s", resp));

        if (StringUtils.isNotEmpty(resp) && returnClass != Void.class) {
            return JSONObjectUtil.toObject(resp, returnClass);
        } else {
            return null;
        }
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }
}
