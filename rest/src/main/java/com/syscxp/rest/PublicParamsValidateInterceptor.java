package com.syscxp.rest;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/28 17:14
 * Author: wj
 */
public class PublicParamsValidateInterceptor implements RestServletRequestInterceptor {

    @Override
    public void intercept(HttpServletRequest req) throws RestServletRequestInterceptorException {
        if (StringUtils.isEmpty(req.getParameter(RestConstants.ACTION))) {
            throw new RestServletRequestInterceptorException(RestConstants.INVALID_PARAMETER, "缺少公共参数：Action");
        }
        if (StringUtils.isEmpty(req.getParameter(RestConstants.TIMESTAMP))) {
            throw new RestServletRequestInterceptorException(RestConstants.INVALID_PARAMETER, "缺少公共参数：Timestamp");
        }
        if (StringUtils.isEmpty(req.getParameter(RestConstants.NONCE))) {
            throw new RestServletRequestInterceptorException(RestConstants.INVALID_PARAMETER, "缺少公共参数：Nonce");
        }
        if (RestConstants.ASYNC_JOB_ACTION.equals(req.getParameter(RestConstants.ACTION))) {
            return;
        }
        if (StringUtils.isEmpty(req.getParameter(RestConstants.SECRET_ID))) {
            throw new RestServletRequestInterceptorException(RestConstants.INVALID_PARAMETER, "缺少公共参数：SecretId");
        }
        if (StringUtils.isEmpty(req.getParameter(RestConstants.SIGNATURE))) {
            throw new RestServletRequestInterceptorException(RestConstants.INVALID_PARAMETER, "缺少公共参数：Signature");
        }

    }
}
