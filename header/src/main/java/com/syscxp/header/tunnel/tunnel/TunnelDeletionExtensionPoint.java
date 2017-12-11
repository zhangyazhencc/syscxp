package com.syscxp.header.tunnel.tunnel;

public interface TunnelDeletionExtensionPoint {

    void preDelete(TunnelVO vo);

    void beforeDelete();

    void afterDelete();

}
