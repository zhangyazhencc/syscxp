package com.syscxp.sdk;

/**
 * Project: syscxp
 * Package: com.syscxp.sdk
 * Date: 2017/12/26 14:03
 * Author: wj
 */
public interface Completion<T> {
    void complete(T ret);
}
