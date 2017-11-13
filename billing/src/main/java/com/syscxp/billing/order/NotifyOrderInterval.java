package com.syscxp.billing.order;

public enum NotifyOrderInterval {
    FIRST(0,1),
    SECOND(1,3),
    THIRD(2,10),
    FOURTH(3,60),
    FIFTH(4,600),
    SIXTH(5,1200);

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
