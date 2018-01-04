package com.syscxp.rest;

import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.HMAC;
import com.syscxp.utils.data.StringTemplate;
import com.syscxp.utils.function.Function;

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

    private static final long EXPIRE_TIME = 600 * 1000;

    @Override
    public void intercept(HttpServletRequest req) throws RestServletRequestInterceptorException {

        String secretKey = getSecretKey(req.getParameter(RestConstants.SECRET_ID));
        String signatureString = getSignatureString(req, secretKey);

        Long timestamp = Long.valueOf(req.getParameter(RestConstants.TIMESTAMP));

        if (!req.getParameter(RestConstants.SIGNATURE).equals(signatureString) || System.currentTimeMillis() - EXPIRE_TIME > timestamp) {
            throw new RestServletRequestInterceptorException(4100, "Signature校验失败");
        }

    }

    private String getSignatureString(HttpServletRequest req, String secretKey) {
        Map<String, String[]> vars = new TreeMap<>(Comparator.comparing(String::toLowerCase));
        vars.putAll(req.getParameterMap());
        vars.remove(RestConstants.SIGNATURE);
        List<String> params = CollectionUtils.transformToList(vars.entrySet(),
                (Function<String, Map.Entry<String, String[]>>) arg -> arg.getKey() + "=" + s(arg.getValue()));

        System.out.println(req.getRequestURL().toString());

        String requestString = req.getMethod() + "?" + StringTemplate.join(params, "&");

        String hmac;
        if (req.getParameterMap().containsKey(RestConstants.SIGNATURE_METHOD)) {
            hmac = HMAC.encryptHMACString(requestString,secretKey, req.getParameter(RestConstants.SIGNATURE_METHOD));
        } else {
            hmac = HMAC.encryptHMACString(requestString,secretKey);
        }
        return HMAC.encryptBase64(hmac);
    }

    private String getSecretKey(String secretId) {

// todo

        return secretId;
    }
}
