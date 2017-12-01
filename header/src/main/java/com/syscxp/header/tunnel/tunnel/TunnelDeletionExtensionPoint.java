package com.syscxp.header.tunnel.tunnel;

public interface TunnelDeletionExtensionPoint {

    void preDelete();

    void beforeDelete();

    void afterDelete();

}
