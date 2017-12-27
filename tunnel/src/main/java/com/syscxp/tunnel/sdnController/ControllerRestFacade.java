package com.syscxp.tunnel.sdnController;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.rest.RESTFacade;
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
 * @Description: 控制器命令下发.
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

        sendCommandForResponce(url, commandParam, new ReturnValueCompletion<ControllerCommands.ControllerRestResponse>(completion) {
            @Override
            public void success(ControllerCommands.ControllerRestResponse returnValue) {

                if (returnValue.isSuccess()) {
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
                                       final ReturnValueCompletion<ControllerCommands.ControllerRestResponse> completion) {
        try {
            logger.info(String.format("call controller url %s, command params %s.", url, commandParam));
            ControllerCommands.ControllerRestResponse response = syncPostForResponseNoretry(url, commandParam);
            logger.info("response: " + JSONObjectUtil.toJsonString(response));
            logger.debug(String.format("successfully post %s", url));
            completion.success(response);
        } catch (Exception e) {
            logger.debug(String.format("unable to post %s. %s", url, e.getMessage()));
            completion.fail(Platform.operr("unable to post %s. %s", url, e.getMessage()));
        }
    }

    // syncHttpRequest
    private ControllerCommands.ControllerRestResponse syncPostForResponseNoretry(String url, String commandParam) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentLength(commandParam.length());
        HttpEntity<String> req = new HttpEntity<String>(commandParam, requestHeaders);
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("json post[%s], %s", url, req.toString()));
        }
        ResponseEntity<String> rsp = restf.getRESTTemplate().postForEntity(url, req, String.class);

        if (logger.isTraceEnabled()) {
            logger.trace(String.format("[http response(url: %s)] %s", url, rsp.getBody()));
        }

        if (rsp.getStatusCode() != org.springframework.http.HttpStatus.OK) {
            throw new RuntimeException(String.format("failed to post to %s, status code: %s, response body: %s", url, rsp.getStatusCode(), rsp.getBody()));
        }

        return JSONObjectUtil.toObject(rsp.getBody(), ControllerCommands.ControllerRestResponse.class);
    }
}
