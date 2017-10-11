package com.syscxp.billing.order;

public enum NotifyOrderInterval {
    FIRST(1,0),
    SECOND(2,1),
    THIRD(3,3),
    FOURTH(4,5),
    FIFTH(5,10),
    SIXTH(6,60),
    SEVENTH(7,180),
    EIGHTH(8,300),
    NINTH(9,420),
    TENTH(10,600);

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
