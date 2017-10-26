package com.syscxp.header.host;

public interface HostFactory {
    HostVO createHost(HostVO vo, AddHostMessage msg);

    Host getHost(HostVO vo);

    HostType getHostType();

    HostInventory getHostInventory(HostVO vo);

    HostInventory getHostInventory(String uuid);
}
