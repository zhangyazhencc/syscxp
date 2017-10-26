package com.syscxp.header.host;

import java.util.List;

public interface HostBaseExtensionFactory {
    Host getHost(HostVO vo);

    List<Class> getMessageClasses();
}
