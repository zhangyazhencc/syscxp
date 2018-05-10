package com.syscxp.core.rest;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.rest.RESTApiFacade;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
                rsp.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("No api result[uuid:%s] found", uuid));
                return;
            }
            rsp.setCharacterEncoding("UTF-8");
            PrintWriter writer = rsp.getWriter();
            String res = JSONObjectUtil.toJsonString(apiRsp);
            rsp.setStatus(HttpServletResponse.SC_OK);
            writer.write(res);
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
            rsp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t.getMessage());
        }
    }

    private String handleByMessageType(String body, String ip, String referer) {
        logger.info(String.format("Received request body[referer-url:%s]: %s", referer, body));
        APIMessage amsg = null;
        try {
            amsg = (APIMessage) RESTApiDecoder.loads(body);
            amsg.setIp(ip);
        } catch (Throwable t) {
            logger.error(t.getMessage());
            return t.getMessage();
        }


        RestAPIResponse rsp;
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
            String ret = handleByMessageType(entity.getBody(), getRemortIP(request), request.getHeader("Referer") + request.getHeader("referer-url"));
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(ret);
        } catch (Throwable t) {
            StringBuilder sb = new StringBuilder(String.format("Error when calling %s", request.getRequestURI()));
            sb.append(String.format("\nheaders: %s", entity.getHeaders().toString()));
            sb.append(String.format("\nbody: %s", entity.getBody()));
            sb.append(String.format("\nexception message: %s", t.getMessage()));
            logger.debug(sb.toString(), t);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, sb.toString());
        }
    }

    private String getRemortIP(HttpServletRequest request) {
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        }
        return request.getHeader("x-forwarded-for");
    }
}
