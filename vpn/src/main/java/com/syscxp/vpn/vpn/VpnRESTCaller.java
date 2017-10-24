package com.syscxp.vpn.vpn;

import com.syscxp.core.Platform;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.http.*;
import com.syscxp.vpn.vpn.VpnCommands.*;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class VpnRESTCaller {
    private static final CLogger logger = Utils.getLogger(VpnRESTCaller.class);
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private ErrorFacade errf;

    private String baseUrl;

    public VpnRESTCaller(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public VpnRESTCaller() {
        this(VpnGlobalProperty.VPN_BASE_URL);
    }

    /**
     * 获取任务处理结果
     */
    public TaskResult syncPostForResult(String path, VpnAgentCommand cmd) {
        return syncPostForResponseNoretry(path, cmd).getResult();
    }

    /**
     * 获取返回结果
     */
    public VpnAgentResponse syncPostForResponse(String path, VpnAgentCommand cmd) {
        return restf.syncJsonPost(buildUrl(path), cmd, VpnAgentResponse.class);
    }

    public VpnAgentResponse syncPostForResponseNoretry(String url, VpnAgentCommand cmd) {
        String body = JSONObjectUtil.toJsonString(cmd);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentLength(body.length());
        HttpEntity<String> req = new HttpEntity<String>(body, requestHeaders);
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("json post[%s], %s", url, req.toString()));
        }

        ResponseEntity<String> rsp = restf.getRESTTemplate().postForEntity(url, req, String.class);

        if (rsp.getStatusCode() != org.springframework.http.HttpStatus.OK) {
            throw new OperationFailureException(Platform.operr("failed to post to %s, status code: %s, response body: %s", url, rsp.getStatusCode(), rsp.getBody()));
        }

        if (logger.isTraceEnabled()) {
            logger.trace(String.format("[http response(url: %s)] %s", url, rsp.getBody()));
        }
        logger.info(String.format("[http response(url: %s)] %s", url, rsp.getBody()));

        return JSONObjectUtil.toObject(rsp.getBody(), VpnAgentResponse.class);

    }

    /**
     * 检查状态
     */
    public RunStatus checkStatus(String path, VpnAgentCommand cmd) {
        String url = buildUrl(path);
        try {
            VpnAgentResponse response = syncPostForResponseNoretry(url, cmd);
            logger.debug(String.format("successfully post %s", url));
            return response.getStatus();
        } catch (Exception e) {
            logger.debug(String.format("unable to post %s. %s", url, e.getMessage()));
            return RunStatus.UNKOWN;
        }

    }

    private String buildUrl(String path) {
        return URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.VPN_ROOT_PATH, path);
    }

    /**
     * http调用内部服务
     */
    public APIReply syncJsonPost(APIMessage innerMsg) {
        String url = URLBuilder.buildUrlFromBase(baseUrl, RESTConstant.REST_API_CALL);
        InnerMessageHelper.setMD5(innerMsg);

        RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(innerMsg), RestAPIResponse.class);
        return (APIReply) RESTApiDecoder.loads(rsp.getResult());
    }

    public void sendCommand(String path, VpnAgentCommand cmd, final Completion completion) {
        String url = buildUrl(path);
        sendCommandForResponce(url, cmd, new ReturnValueCompletion<VpnAgentResponse>(completion) {
            @Override
            public void success(VpnAgentResponse returnValue) {
                TaskResult result = returnValue.getResult();
                if (result.isSuccess()) {
                    completion.success();
                    logger.debug(String.format("successfully execute the command[%s].", url));
                } else {
                    completion.fail(Platform.operr("failed to execute the command[%s]. %s", url, result.getMessage()));
                }
            }

            @Override
            public void fail(ErrorCode errorCode) {
                completion.fail(errorCode);
            }
        });
    }

    public void sendCommandForResponce(String url, VpnAgentCommand cmd,
                                       final ReturnValueCompletion<VpnAgentResponse> completion) {
        try {
            VpnAgentResponse response = syncPostForResponseNoretry(url, cmd);
            logger.debug(String.format("successfully post %s", url));
            completion.success(response);
        } catch (Exception e) {
            logger.debug(String.format("unable to post %s. %s", url, e.getMessage()));
            completion.fail(Platform.operr("unable to post %s. %s", url, e.getMessage()));
        }
    }


}

