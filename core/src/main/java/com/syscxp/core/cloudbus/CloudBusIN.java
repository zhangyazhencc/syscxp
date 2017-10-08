package com.syscxp.core.cloudbus;

import com.rabbitmq.client.Connection;
import com.syscxp.header.Service;

public interface CloudBusIN extends CloudBus {
    Connection getConnection();
    
    void activeService(Service serv);

    void activeService(String id);
    
    void deActiveService(Service serv);
    
    void deActiveService(String id);
}
