package com.syscxp.alarm.header.resourcePolicy;

import java.util.List;

public class TunnelParameter {
    private String tunnel_id;
    private String user_id;
    private String endpointA_vlan;
    private String endpointA_ip;
    private String endpointZ_vlan;
    private String endpointZ_ip;
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

    public String getEndpointA_vlan() {
        return endpointA_vlan;
    }

    public void setEndpointA_vlan(String endpointA_vlan) {
        this.endpointA_vlan = endpointA_vlan;
    }

    public String getEndpointA_ip() {
        return endpointA_ip;
    }

    public void setEndpointA_ip(String endpointA_ip) {
        this.endpointA_ip = endpointA_ip;
    }

    public String getEndpointZ_vlan() {
        return endpointZ_vlan;
    }

    public void setEndpointZ_vlan(String endpointZ_vlan) {
        this.endpointZ_vlan = endpointZ_vlan;
    }

    public String getEndpointZ_ip() {
        return endpointZ_ip;
    }

    public void setEndpointZ_ip(String endpointZ_ip) {
        this.endpointZ_ip = endpointZ_ip;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}

class Rule{
    private String strategy_type;
    private String op;
    private String right_value;
    private String stay_time;

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

    public String getStay_time() {
        return stay_time;
    }

    public void setStay_time(String stay_time) {
        this.stay_time = stay_time;
    }
}