package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2018/5/9
 */
@Inventory(mappingVOClass = TEConfigVO.class)
public class TEConfigInventory {

    private String uuid;
    private String tunnelUuid;
    private String traceType;
    private String source;
    private String target;
    private String inNodes;
    private String exNodes;
    private String blurryInNodes;
    private String blurryExNodes;
    private String optimizationEdges;
    private String command;
    private String status;
    private Integer isConnected;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    private List<TETraceInventory> teTraceVOS = new ArrayList<TETraceInventory>();

    public static TEConfigInventory valueOf(TEConfigVO vo){
        TEConfigInventory inv = new TEConfigInventory();
        inv.setUuid(vo.getUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setTraceType(vo.getTraceType().toString());
        inv.setSource(vo.getSource());
        inv.setTarget(vo.getTarget());
        inv.setInNodes(vo.getInNodes());
        inv.setExNodes(vo.getExNodes());
        inv.setBlurryInNodes(vo.getBlurryInNodes());
        inv.setBlurryExNodes(vo.getBlurryExNodes());
        inv.setOptimizationEdges(vo.getOptimizationEdges());
        inv.setCommand(vo.getCommand());
        inv.setStatus(vo.getStatus().toString());
        inv.setIsConnected(vo.getIsConnected());
        inv.setTeTraceVOS(TETraceInventory.valueOf(vo.getTeTraceVOS()));
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TEConfigInventory> valueOf(Collection<TEConfigVO> vos) {
        List<TEConfigInventory> lst = new ArrayList<TEConfigInventory>(vos.size());
        for (TEConfigVO vo : vos) {
            lst.add(TEConfigInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getTraceType() {
        return traceType;
    }

    public void setTraceType(String traceType) {
        this.traceType = traceType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getInNodes() {
        return inNodes;
    }

    public void setInNodes(String inNodes) {
        this.inNodes = inNodes;
    }

    public String getExNodes() {
        return exNodes;
    }

    public void setExNodes(String exNodes) {
        this.exNodes = exNodes;
    }

    public String getBlurryInNodes() {
        return blurryInNodes;
    }

    public void setBlurryInNodes(String blurryInNodes) {
        this.blurryInNodes = blurryInNodes;
    }

    public String getBlurryExNodes() {
        return blurryExNodes;
    }

    public void setBlurryExNodes(String blurryExNodes) {
        this.blurryExNodes = blurryExNodes;
    }

    public String getOptimizationEdges() {
        return optimizationEdges;
    }

    public void setOptimizationEdges(String optimizationEdges) {
        this.optimizationEdges = optimizationEdges;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(Integer isConnected) {
        this.isConnected = isConnected;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public List<TETraceInventory> getTeTraceVOS() {
        return teTraceVOS;
    }

    public void setTeTraceVOS(List<TETraceInventory> teTraceVOS) {
        this.teTraceVOS = teTraceVOS;
    }
}
