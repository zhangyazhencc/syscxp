package com.syscxp.rest;

import javax.servlet.http.HttpServletRequest;

import static com.syscxp.rest.RestConstants.ASYNC_JOB_ACTION;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/28 17:14
 * Author: wj
 */
public class PublicParamsValidateInterceptor implements RestServletRequestInterceptor {

    @Override
    public void intercept(HttpServletRequest req) throws RestServletRequestInterceptorException {
        if (req.getParameterMap().containsKey(ASYNC_JOB_ACTION)) {
            return;
        }
        if (!req.getParameterMap().containsKey(ACTION)) {
            throw new RestServletRequestInterceptorException(401, "缺少签名参数：Action");
        }
        if (!req.getParameterMap().containsKey(SECRET_ID)) {
            throw new RestServletRequestInterceptorException(401, "缺少密钥参数：SecretId");
        }
        if (!req.getParameterMap().containsKey(SIGNATURE)) {
            throw new RestServletRequestInterceptorException(401, "缺少签名参数：Signature");
        }
        if (!req.getParameterMap().containsKey(TIMESTAMP)) {
            throw new RestServletRequestInterceptorException(401, "缺少密钥参数：Timestamp");
        }

        if (!req.getParameterMap().containsKey(NONCE)) {
            throw new RestServletRequestInterceptorException(401, "缺少签名参数：Nonce");
        }
    }
}
