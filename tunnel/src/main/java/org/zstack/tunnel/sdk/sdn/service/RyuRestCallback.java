package org.zstack.tunnel.sdk.sdn.service;

import org.springframework.http.HttpEntity;
import org.zstack.header.core.AsyncBackup;
import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.rest.AsyncRESTCallback;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-30.
 * @Description: .
 */
public class RyuRestCallback extends AsyncRESTCallback {
    public RyuRestCallback(AsyncBackup one, AsyncBackup... others) {
        super(one, others);
    }

    @Override
    public void fail(ErrorCode err) {
        System.out.println("Ryu callback fail!");
    }

    @Override
    public void success(HttpEntity<String> responseEntity) {
        System.out.println("Ryu callback success!");
    }
}
