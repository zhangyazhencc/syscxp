package com.syscxp.alarm.header.log;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.sql.Timestamp;

@Action(category = AlarmConstant.ACTION_CATEGORY_ALARM_LOG)
public class APICreateAlarmLogMsg extends APIMessage{

    @APIParam(emptyString = false)
    private String tunnel_id;

    @APIParam(emptyString = false)
    private String tunnel_name;

    @APIParam(emptyString = false)
    private String user_id;

    @APIParam(emptyString = false)
    private String problem;

    @APIParam(emptyString = false)
    private String status;

    @APIParam
    private Timestamp created;

    @APIParam
    private Timestamp resumed;

    public String getTunnel_id() {
        return tunnel_id;
    }

    public void setTunnel_id(String tunnel_id) {
        this.tunnel_id = tunnel_id;
    }

    public String getTunnel_name() {
        return tunnel_name;
    }

    public void setTunnel_name(String tunnel_name) {
        this.tunnel_name = tunnel_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getResumed() {
        return resumed;
    }

    public void setResumed(Timestamp resumed) {
        this.resumed = resumed;
    }
}
