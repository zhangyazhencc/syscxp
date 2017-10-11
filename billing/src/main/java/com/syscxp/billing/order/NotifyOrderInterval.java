package com.syscxp.billing.order;

public enum NotifyOrderInterval {
    FIRST(0,0),
    SECOND(1,1),
    THIRD(2,3),
    FOURTH(3,5),
    FIFTH(4,10),
    SIXTH(5,60),
    SEVENTH(6,180),
    EIGHTH(7,300),
    NINTH(8,420),
    TENTH(9,600);

    NotifyOrderInterval(int times,int minutes){
        this.times = times;
        this.minutes = minutes;
    }

    public static  int getMinutes(int times){
        for(NotifyOrderInterval n :NotifyOrderInterval.values()){
            if(n.getTimes() == times){
                return n.getMinutes();
            }
        }
        return -1;
    }

    private int minutes;
    private int times;
    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
