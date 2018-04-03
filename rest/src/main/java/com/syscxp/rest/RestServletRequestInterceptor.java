package com.syscxp.rest;

import javax.servlet.http.HttpServletRequest;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 15:10
 * Author: wj
 */
public interface RestServletRequestInterceptor {
    class RestServletRequestInterceptorException extends Exception {
        public String code;
        public String message;

        public RestServletRequestInterceptorException(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }


    void intercept(HttpServletRequest req) throws RestServletRequestInterceptorException;
}
