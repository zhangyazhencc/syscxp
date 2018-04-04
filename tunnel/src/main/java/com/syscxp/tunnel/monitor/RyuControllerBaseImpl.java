package com.syscxp.tunnel.monitor;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.header.Component;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.TimeoutRestTemplate;
import com.syscxp.header.tunnel.monitor.MonitorAgentConstant;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public class RyuControllerBaseImpl implements RyuControllerBase, Component {

    private static final CLogger logger = Utils.getLogger(RyuControllerBaseImpl.class);

    @Autowired
    private RESTFacade restf;

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2018-03-22.
     * @Description: 控制器基础返回对象.
     */
    public static class RyuBaseResponse {
        private boolean success;
        private String msg;

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

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2018-03-22.
     * @Description: 2层、3层网络返回对象.
     */
    public static class RyuNetworkResponse extends RyuBaseResponse {
        private Boolean rollback = true;

        public Boolean getRollback() {
            return rollback;
        }

        public void setRollback(Boolean rollback) {
            this.rollback = rollback;
        }
    }


    /***
     * 获取RYU控制器服务url
     * @param method
     * @return
     */
    private String getUrl(String method) {
        String url = CoreGlobalProperty.CONTROLLER_MANAGER_URL + method;

        return url;
    }

    /***
     * 发送监控agent命令
     * @param method：调用方法
     * @param command：下发参数
     * @param returnClass:返回对象，继承
     * @param <T>
     * @return
     */

    @Override
    public <T> T httpCall(String method, String command, Class<T> returnClass) {
        String url = getUrl(method);
        Class baseResp = RyuBaseResponse.class;
        if ((returnClass != baseResp) && !returnClass.getClass().isAssignableFrom(baseResp.getClass()))
            throw new IllegalClassException("return class should extend RyuControllerBaseImpl.RyuBaseResponse");

        logger.info(String.format("======= [Controller Request] url: %s command: %s", url, command));

        String resp = restf.syncJsonPost(url, command);

        logger.info(String.format("======= [Controller Response] %s", resp));

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
