package com.syscxp.billing.rest;

import com.syscxp.core.rest.RESTApiController;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.alipay.APIVerifyNotifyMsg;
import com.syscxp.header.alipay.APIVerifyNotifyReply;
import com.syscxp.header.alipay.APIVerifyReturnMsg;
import com.syscxp.header.alipay.APIVerifyReturnReply;
import com.syscxp.header.rest.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class AlipayController {

    private static final CLogger logger = Utils.getLogger(RESTApiController.class);
    @Autowired
    private RESTApiFacade restApi;

    @RequestMapping(value = "/alipay/return", method = {RequestMethod.GET})
    @ResponseBody
    public void alipayReturnUrl(HttpServletRequest request, HttpServletResponse rsp) throws IOException {
        try {
            //获取支付宝GET过来反馈信息
            Map<String, String> params = getParamterMap(request);
            APIVerifyReturnMsg msg = new APIVerifyReturnMsg();
            msg.setParam(params);
            RestAPIResponse res = restApi.call(msg);
            if (res.getState().equals(RestAPIState.Done.toString())) {
                APIVerifyReturnReply replay = (APIVerifyReturnReply) RESTApiDecoder.loads(res.getResult());
                if (replay.getInventory()) {
                    rsp.sendRedirect("/naas/account/#/recharge?result=success&money="+replay.getAddMoney().doubleValue());
                    return;
                }
            }
            rsp.sendRedirect( "/naas/account/#/recharge?result=failure&addMoney=0");
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            rsp.sendRedirect( "/naas/account/#/recharge?result=failure&money=0");
        }
    }

    @RequestMapping(value = "/alipay/notify", method = {RequestMethod.POST})
    public void alipayNotifyUrl(HttpServletRequest request, HttpServletResponse rsp) throws IOException {
        try {
            Map<String, String> params = getParamterMap(request);
            APIVerifyNotifyMsg msg = new APIVerifyNotifyMsg();

            msg.setParam(params);
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

    private Map<String, String> getParamterMap(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
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
        return params;
    }


}
