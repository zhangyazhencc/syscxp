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
        public int statusCode;
        public String error;

        public RestServletRequestInterceptorException(int statusCode, String error) {
            this.statusCode = statusCode;
            this.error = error;
        }
    }



    void intercept(HttpServletRequest req) throws RestServletRequestInterceptorException;
}
