package org.zstack.core.rest;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zstack.header.alipay.APIVerifyNotifyMsg;
import org.zstack.header.alipay.APIVerifyNotifyReply;
import org.zstack.header.alipay.APIVerifyReturnMsg;
import org.zstack.header.alipay.APIVerifyReturnReply;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APISyncCallMessage;
import org.zstack.header.rest.*;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class RESTApiController {
    private static final CLogger logger = Utils.getLogger(RESTApiController.class);
    @Autowired
    private RESTApiFacade restApi;
    @Autowired
    private RESTFacade restf;

    @RequestMapping(value = RESTConstant.REST_API_RESULT + "{uuid}", method = {RequestMethod.GET, RequestMethod.PUT})
    public void queryResult(@PathVariable String uuid, HttpServletResponse rsp) throws IOException {
        try {
            RestAPIResponse apiRsp = restApi.getResult(uuid);
            if (apiRsp == null) {
                rsp.sendError(HttpStatus.SC_NOT_FOUND, String.format("No api result[uuid:%s] found", uuid));
                return;
            }
            rsp.setCharacterEncoding("UTF-8");
            PrintWriter writer = rsp.getWriter();
            String res = JSONObjectUtil.toJsonString(apiRsp);
            rsp.setStatus(HttpStatus.SC_OK);
            writer.write(res);
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
            rsp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, t.getMessage());
        }
    }

    @RequestMapping(value = "/alipay/return", method = {RequestMethod.GET})
    @ResponseBody
    public String alipayReturnUrl(HttpServletRequest request, HttpServletResponse rsp) throws IOException {
        try {
            //获取支付宝GET过来反馈信息
            Map<String, String> params = new HashMap<String, String>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }
            APIVerifyReturnMsg msg = new APIVerifyReturnMsg();
            msg.setParam(params);
            msg.setServiceId("billing");
            RestAPIResponse res = restApi.call(msg);
            if (res.getState().equals(RestAPIState.Done.toString())) {
                APIVerifyReturnReply replay = (APIVerifyReturnReply) RESTApiDecoder.loads(res.getResult());
                if (replay.getInventory()) {
                    return "success";
                }
            }

        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
            return "failure";
        }
        return "failure";
    }

    @RequestMapping(value = "/alipay/notify", method = {RequestMethod.POST})
    public void alipayNotifyUrl(HttpServletRequest request, HttpServletResponse rsp) throws IOException {
        try {
            Map<String,String> params = new HashMap<String,String>();
            Map<String,String[]> requestParams = request.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            APIVerifyNotifyMsg msg = new APIVerifyNotifyMsg();
            msg.setParam(params);
            msg.setServiceId("billing");
            RestAPIResponse res = restApi.call(msg);
            if (res.getState().equals(RestAPIState.Done.toString())) {
                APIVerifyNotifyReply replay = (APIVerifyNotifyReply) RESTApiDecoder.loads(res.getResult());
                if (replay.getInventory()) {
                    rsp.getWriter().println("success");
                }else {
                    rsp.getWriter().println("failure");
                }

            }else {
                rsp.getWriter().println("failure");
            }

        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
            rsp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, t.getMessage());
        }
    }

    private String handleByMessageType(String body, String ip) {
        APIMessage amsg = null;
        try {
            amsg = (APIMessage) RESTApiDecoder.loads(body);
            amsg.setIp(ip);
        } catch (Throwable t) {
            return t.getMessage();
        }

        RestAPIResponse rsp = null;
        if (amsg instanceof APISyncCallMessage) {
            rsp = restApi.call(amsg);
        } else {
            rsp = restApi.send(amsg);
        }

        return JSONObjectUtil.toJsonString(rsp);
    }

    @RequestMapping(value = RESTConstant.REST_API_CALL, method = {RequestMethod.POST, RequestMethod.PUT})
    public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpEntity<String> entity = restf.httpServletRequestToHttpEntity(request);
        try {
            String ret = handleByMessageType(entity.getBody(), getRemortIP(request));
            response.setStatus(HttpStatus.SC_OK);
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(ret);
        } catch (Throwable t) {
            StringBuilder sb = new StringBuilder(String.format("Error when calling %s", request.getRequestURI()));
            sb.append(String.format("\nheaders: %s", entity.getHeaders().toString()));
            sb.append(String.format("\nbody: %s", entity.getBody()));
            sb.append(String.format("\nexception message: %s", t.getMessage()));
            logger.debug(sb.toString(), t);
            response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, sb.toString());
        }
    }

    private String getRemortIP(HttpServletRequest request) {
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        }
        return request.getHeader("x-forwarded-for");
    }
}
