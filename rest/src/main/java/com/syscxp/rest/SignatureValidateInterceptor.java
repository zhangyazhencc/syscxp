package com.syscxp.rest;

import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.Digest;
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

    public static final String SIGNATURE_METHOD = "SignatureMethod";
    public static final long EXPIRE_TIME = 600 * 1000;

    @Override
    public void intercept(HttpServletRequest req) throws RestServletRequestInterceptorException {

        String secretKey = getSecretKey(SECRET_ID);
        String signatureString = getSignatureString(req, secretKey);

        Long timestamp = Long.valueOf(req.getParameter(TIMESTAMP));

        if (!req.getParameter(SIGNATURE).equals(signatureString) || System.currentTimeMillis() - EXPIRE_TIME > timestamp) {
            throw new RestServletRequestInterceptorException(4100, "Signature校验失败");
        }

    }

    private String getSignatureString(HttpServletRequest req, String secretKey) {
        Map<String, String[]> vars = new TreeMap<>(Comparator.naturalOrder());
        vars.putAll(req.getParameterMap());
        vars.remove(SIGNATURE);
        List<String> params = CollectionUtils.transformToList(vars.entrySet(),
                (Function<String, Map.Entry<String, String[]>>) arg -> arg.getKey() + "=" + s(arg.getKey()));

        String requestString = req.getMethod() + req.getRequestURL() + "?" + StringTemplate.join(params, "&") + secretKey;

        if (req.getParameterMap().containsKey(SIGNATURE_METHOD)) {
            return Digest.getSignature(requestString, req.getParameter(SIGNATURE_METHOD));
        } else {
            return Digest.getMD5(requestString);
        }
    }

    private String getSecretKey(String secretId) {

        return secretId;
    }
}
