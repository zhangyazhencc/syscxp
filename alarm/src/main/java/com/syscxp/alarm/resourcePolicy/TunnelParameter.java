package com.syscxp.alarm.resourcePolicy;

import java.util.List;

public class TunnelParameter {

    private String tunnel_id;
    private String user_id;
    private Integer endpointA_vid;
    private String endpointA_ip;
    private Integer endpointB_vid;
    private String endpointB_ip;
    private Long bandwidth;
    private List<Rule> rules;

    public String getTunnel_id() {
        return tunnel_id;
    }

    public void setTunnel_id(String tunnel_id) {
        this.tunnel_id = tunnel_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEndpointA_ip() {
        return endpointA_ip;
    }

    public void setEndpointA_ip(String endpointA_ip) {
        this.endpointA_ip = endpointA_ip;
    }

    public String getEndpointB_ip() {
        return endpointB_ip;
    }

    public void setEndpointB_ip(String endpointB_ip) {
        this.endpointB_ip = endpointB_ip;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public Integer getEndpointA_vid() {
        return endpointA_vid;
    }

    public void setEndpointA_vid(Integer endpointA_vid) {
        this.endpointA_vid = endpointA_vid;
    }

    public Integer getEndpointB_vid() {
        return endpointB_vid;
    }

    public void setEndpointB_vid(Integer endpointB_vid) {
        this.endpointB_vid = endpointB_vid;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}

class Rule {
    private String alarm_rule_id;
    private String strategy_type;
    private String op;
    private String right_value;
    private Integer stay_time;

    public String getStrategy_type() {
        return strategy_type;
    }

    public void setStrategy_type(String strategy_type) {
        this.strategy_type = strategy_type;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getRight_value() {
        return right_value;
    }

    public void setRight_value(String right_value) {
        this.right_value = right_value;
    }

    public Integer getStay_time() {
        return stay_time;
    }

    public void setStay_time(Integer stay_time) {
        this.stay_time = stay_time;
    }

    public String getAlarm_rule_id() {
        return alarm_rule_id;
    }

    public void setAlarm_rule_id(String alarm_rule_id) {
        this.alarm_rule_id = alarm_rule_id;
    }
}