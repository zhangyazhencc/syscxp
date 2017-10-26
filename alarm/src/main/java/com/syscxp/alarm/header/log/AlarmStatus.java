package com.syscxp.alarm.header.log;

public enum AlarmStatus {
    ALARM("123"),
    RESUME("123");
    private String name;



    AlarmStatus(String name) {
        this.name = name;
    }

    public static AlarmStatus nameOf(String name){
        for(AlarmStatus status : AlarmStatus.values()){
            if(status.name.equals(name)){
                return status;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
