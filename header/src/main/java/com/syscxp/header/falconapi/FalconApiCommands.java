package com.syscxp.header.falconapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-02.
 * @Description: Falcon API 通信对象集合.
 */
public class FalconApiCommands {
    private String tunnel_id;

    public String getTunnel_id() {
        return tunnel_id;
    }

    public void setTunnel_id(String tunnel_id) {
        this.tunnel_id = tunnel_id;
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-11-02.
     * @Description: tunnel信息.
     */
    public static class Tunnel extends FalconApiCommands{
        private String user_id;
        private String endpointA_ip;
        private String endpointB_ip;
        private Integer endpointA_vid;
        private Integer endpointB_vid;
        private Long bandwidth;
        private List<Rule> rules;

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

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
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

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-11-02.
     * @Description: 策略同步.
     */
    public static class Strategy extends Tunnel{
        private List<Rule> rules;

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-11-02.
     * @Description: 规则.
     */
    public static class Rule{
        private String regulation_id;
        private String strategy_type;
        private String op;
        private String right_value;
        private Integer stay_time;

        public String getRegulation_id() {
            return regulation_id;
        }

        public void setRegulation_id(String regulation_id) {
            this.regulation_id = regulation_id;
        }

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
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-11-02.
     * @Description: Falcon API返回对象.
     */
    public static class RestResponse {
        private String msg;
        private boolean success;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

    public static class FalconTunnelInventory {
        private String tunnel_id;
        private String user_id;
        private String endpointA_ip;
        private String endpointB_ip;
        private Integer endpointA_vlan;
        private Integer endpointB_vlan;
        private Long bandwidth;

        public static FalconTunnelInventory valueOf(Tunnel vo){
            FalconTunnelInventory inv = new FalconTunnelInventory();
            inv.setTunnel_id(vo.getTunnel_id());
            inv.setUser_id(vo.getUser_id());
            inv.setEndpointA_ip(vo.getEndpointA_ip());
            inv.setEndpointA_vlan(vo.getEndpointA_vid());
            inv.setEndpointB_ip(vo.getEndpointB_ip());
            inv.setEndpointB_vlan(vo.getEndpointB_vid());
            inv.setBandwidth(vo.getBandwidth());
            return inv;
        }

        public static List<FalconTunnelInventory> valueOf(Collection<Tunnel> vos) {
            List<FalconTunnelInventory> lst = new ArrayList<FalconTunnelInventory>(vos.size());
            for (Tunnel vo : vos) {
                lst.add(FalconTunnelInventory.valueOf(vo));
            }
            return lst;
        }

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

        public Integer getEndpointA_vlan() {
            return endpointA_vlan;
        }

        public void setEndpointA_vlan(Integer endpointA_vlan) {
            this.endpointA_vlan = endpointA_vlan;
        }

        public Integer getEndpointB_vlan() {
            return endpointB_vlan;
        }

        public void setEndpointB_vlan(Integer endpointB_vlan) {
            this.endpointB_vlan = endpointB_vlan;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }
    }
}
