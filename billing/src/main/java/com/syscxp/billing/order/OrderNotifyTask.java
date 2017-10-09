package com.syscxp.billing.order;

import com.syscxp.core.thread.CancelablePeriodicTask;

import java.util.concurrent.TimeUnit;

public class OrderNotifyTask implements CancelablePeriodicTask {

    private String name;

    private String url;


    public OrderNotifyTask(String name){
        this.name = name;
    }
    @Override
    public boolean run() {

        return false;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.HOURS;
    }

    @Override
    public long getInterval() {
        return 24;
    }

    @Override
    public String getName() {
        return name;
    }
}
