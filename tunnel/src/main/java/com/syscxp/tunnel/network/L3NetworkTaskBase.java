package com.syscxp.tunnel.network;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2018/3/14
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class L3NetworkTaskBase {
    private static final CLogger logger = Utils.getLogger(L3NetworkTaskBase.class);

    @Autowired
    private JobQueueFacade jobf;

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;
}
