package com.syscxp.core.cloudbus;

/**
 */
public class BusQueue {
    private final String name;
    private final String bindingKey;
    private final String busExchange;

    public BusQueue(String name, String bindingKey, String busExchange) {
        this.name = name;
        this.bindingKey = bindingKey;
        this.busExchange = busExchange;
    }

    public BusQueue(String name, String busExchange) {
        this.name = name;
        this.bindingKey = name;
        this.busExchange = busExchange;
    }

    public String getName() {
        return name;
    }

    public String getBindingKey() {
        return bindingKey;
    }

    public String getBusExchange() {
        return busExchange;
    }
}
