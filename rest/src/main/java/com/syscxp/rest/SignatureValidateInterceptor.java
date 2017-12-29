package com.syscxp.rest;

import com.syscxp.utils.CollectionUtils;
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

    public static final String SIGNATURE_KEY = "Signature";
    public static final String SIGNATURE_METHOD = "SignatureMethod";


    @Override
    public void intercept(HttpServletRequest req) throws RestServletRequestInterceptorException {

        if (!req.getParameterMap().containsKey(SIGNATURE_KEY)) {
            throw new RestServletRequestInterceptorException(4100, "缺少签名参数：Signature");
        }
        String signature_value = req.getParameter(SIGNATURE_KEY);

        Map<String, String[]> vars = new TreeMap<>(Comparator.naturalOrder());
        vars.putAll(req.getParameterMap());
        vars.remove(SIGNATURE_KEY);
        List<String> params = CollectionUtils.transformToList(vars.entrySet(),
                (Function<String, Map.Entry<String, String[]>>) arg -> arg.getKey() + "=" + s(arg.getKey()));

        String requestString = req.getMethod() + req.getRequestURL() + "?" + StringTemplate.join(params, "&");

        validate(requestString, req.getParameter(SIGNATURE_METHOD));

    }


    private void validate(String requestString, String SignatureMethod) {

    }
}
