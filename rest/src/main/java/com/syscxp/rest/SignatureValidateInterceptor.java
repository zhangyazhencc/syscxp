package com.syscxp.rest;

import com.syscxp.core.identity.AbstractIdentityInterceptor;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.HMAC;
import com.syscxp.utils.Utils;
import com.syscxp.utils.data.StringTemplate;
import com.syscxp.utils.function.Function;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.syscxp.utils.StringDSL.s;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/28 17:14
 * Author: wj
 */
public class SignatureValidateInterceptor implements RestServletRequestInterceptor {
    private static final CLogger LOGGER = Utils.getLogger(SignatureValidateInterceptor.class);

    @Autowired
    private AbstractIdentityInterceptor identityInterceptor;

    private static final long EXPIRE_TIME = 600 * 1000;

    @Override
    public void intercept(HttpServletRequest req) throws RestServletRequestInterceptorException {
        if (RestConstants.ASYNC_JOB_ACTION.equals(req.getParameter(RestConstants.ACTION))) {
            return;
        }
        String secretId = req.getParameter(RestConstants.SECRET_ID);
        String secretKey = getSecretKey(secretId, getIpAdrress(req));
        String signatureString = getSignatureString(req, secretKey);

        Long timestamp = Long.valueOf(req.getParameter(RestConstants.TIMESTAMP));

        if (!req.getParameter(RestConstants.SIGNATURE).equals(signatureString) || System.currentTimeMillis() - EXPIRE_TIME > timestamp) {
            throw new RestServletRequestInterceptorException(RestConstants.INVALID_PARAMETER, "Signature校验失败");
        }
        LOGGER.debug("Signature校验成功，获取sessionUuid");

        req.setAttribute(RestConstants.SESSION_UUID, getSessionUuid(secretId, secretKey));
    }

    private String getSignatureString(HttpServletRequest req, String secretKey) {
        Map<String, String[]> vars = new TreeMap<>(Comparator.comparing(String::toLowerCase));
        vars.putAll(req.getParameterMap());
        vars.remove(RestConstants.SIGNATURE);
        List<String> params = CollectionUtils.transformToList(vars.entrySet(),
                (Function<String, Map.Entry<String, String[]>>) arg -> arg.getKey() + "=" + s(arg.getValue()));

        String requestString = req.getMethod() + req.getRequestURL().toString() + "?" + StringTemplate.join(params, "&");

        String hmac;
        if (req.getParameterMap().containsKey(RestConstants.SIGNATURE_METHOD)) {
            hmac = HMAC.encryptHMACString(requestString, secretKey, req.getParameter(RestConstants.SIGNATURE_METHOD));
        } else {
            hmac = HMAC.encryptHMACString(requestString, secretKey);
        }
        return HMAC.encryptBase64(hmac);
    }

    private String getSecretKey(String secretId, String ip) throws RestServletRequestInterceptorException {
        String secretKey;
        try {
            secretKey = identityInterceptor.getSecretKey(secretId, ip);
        } catch (Exception e) {
            throw new RestServletRequestInterceptorException(RestConstants.IDENTITY_ERROR, e.getMessage());
        }
        return secretKey;
    }

    private String getSessionUuid(String secretId, String secretKey) throws RestServletRequestInterceptorException {
        SessionInventory session;
        try {
            session = identityInterceptor.getSessionUuid(secretId, secretKey);
        } catch (Exception e) {
            throw new RestServletRequestInterceptorException(RestConstants.IDENTITY_ERROR, e.getMessage());
        }

        return session.getUuid();
    }

    private static String getIpAdrress(HttpServletRequest request) {
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            } else {
                return XFor;
            }
        }
        XFor = Xip;
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            return XFor;
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }
}
