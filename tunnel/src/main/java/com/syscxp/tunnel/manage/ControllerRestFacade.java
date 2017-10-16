package com.syscxp.tunnel.manage;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.tunnel.header.controller.ControllerRestResponse;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-10-13.
 * @Description: .
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class ControllerRestFacade {
    private static final CLogger logger = Utils.getLogger(ControllerRestFacade.class);

    @Autowired
    private RESTFacade restf;

    private String SERVER_URL;

    public ControllerRestFacade(String url) {
        this.SERVER_URL = url;
    }

    public ControllerRestFacade() {
        this(CoreGlobalProperty.CONTROLLER_MANAGER_URL);
    }

    public void sendCommand(String commandName, String commandParam, Completion completion) {
        String url = SERVER_URL + commandName;

        sendCommandForResponce(url, commandParam, new ReturnValueCompletion<ControllerRestResponse>(completion) {
            @Override
            public void success(ControllerRestResponse returnValue) {
                //  TaskResult result = returnValue.getResult();

                boolean isSuccess = "0".equals(returnValue.getCode());
                if (isSuccess) {
                    completion.success();
                    logger.debug(String.format("successfully execute the command[%s].", url));
                } else {
                    completion.fail(Platform.operr("failed to execute the command[%s]. %s", url, returnValue.getMsg()));
                }
            }

            @Override
            public void fail(ErrorCode errorCode) {
                completion.fail(errorCode);
            }
        });
    }

    public void sendCommandForResponce(String url, String commandParam,
                                       final ReturnValueCompletion<ControllerRestResponse> completion) {
        try {
            ControllerRestResponse response = syncPostForResponseNoretry(url, commandParam);
            logger.info("response: " + response.toString());
            logger.info("result: " + "0".equals(response.getCode()));
            logger.debug(String.format("successfully post %s", url));
            completion.success(response);
        } catch (Exception e) {
            logger.debug(String.format("unable to post %s. %s", url, e.getMessage()));
            completion.fail(Platform.operr("unable to post %s. %s", url, e.getMessage()));
        }
    }

    // syncHttpRequest
    private ControllerRestResponse syncPostForResponseNoretry(String url, String commandParam) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentLength(commandParam.length());
        HttpEntity<String> req = new HttpEntity<String>(commandParam, requestHeaders);
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

        logger.debug(String.format("[http response(url: %s)] %s", url, rsp.getBody()));

        // ControllerRestResponse restResponse = restf.syncJsonPost(url, commandParam,ControllerRestResponse.class);

        if (rsp.getStatusCode() != org.springframework.http.HttpStatus.OK) {
            throw new OperationFailureException(Platform.operr("failed to post to %s, status code: %s, response body: %s", url, rsp.getStatusCode(), rsp.getBody()));
        }

        return JSONObjectUtil.toObject(rsp.getBody(), ControllerRestResponse.class);
    }
}