package com.syscxp.alarm.resourcePolicy;

import java.util.List;

public class TunnelParameter {

    private String tunnel_id;
    private String user_id;
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


    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

}

class Rule {
    private String strategy_type;
    private String op;
    private int right_value;
    private int stay_time;

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

    public int getRight_value() {
        return right_value;
    }

    public void setRight_value(int right_value) {
        this.right_value = right_value;
    }

    public int getStay_time() {
        return stay_time;
    }

    public void setStay_time(int stay_time) {
        this.stay_time = stay_time;
    }
}