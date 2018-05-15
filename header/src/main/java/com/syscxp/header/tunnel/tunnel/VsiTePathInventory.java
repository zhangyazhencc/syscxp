package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2018/5/14
 */
@Inventory(mappingVOClass = VsiTePathVO.class)
public class VsiTePathInventory {

    private String uuid;
    private String tunnelUuid;
    private String name;
    private String source;
    private String destination;
    private String direction;
    private String tnlPolicyName;
    private String tnlPolicydestination;
    private Timestamp createDate;
    private Timestamp lastOpDate;
    private List<ExplicitPathInventory> explicitPath = new ArrayList<ExplicitPathInventory>();

    public static VsiTePathInventory valueOf(VsiTePathVO vo){
        VsiTePathInventory inv = new VsiTePathInventory();
        inv.setUuid(vo.getUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setName(vo.getName());
        inv.setSource(vo.getSource());
        inv.setDestination(vo.getDestination());
        inv.setDirection(vo.getDirection());
        inv.setTnlPolicyName(vo.getTnlPolicyName());
        inv.setTnlPolicydestination(vo.getTnlPolicydestination());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setExplicitPath(ExplicitPathInventory.valueOf(vo.getExplicitPathVOS()));
        return inv;
    }

    public static List<VsiTePathInventory> valueOf(Collection<VsiTePathVO> vos) {
        List<VsiTePathInventory> lst = new ArrayList<VsiTePathInventory>(vos.size());
        for (VsiTePathVO vo : vos) {
            lst.add(VsiTePathInventory.valueOf(vo));
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTnlPolicyName() {
        return tnlPolicyName;
    }

    public void setTnlPolicyName(String tnlPolicyName) {
        this.tnlPolicyName = tnlPolicyName;
    }

    public String getTnlPolicydestination() {
        return tnlPolicydestination;
    }

    public void setTnlPolicydestination(String tnlPolicydestination) {
        this.tnlPolicydestination = tnlPolicydestination;
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

    public List<ExplicitPathInventory> getExplicitPath() {
        return explicitPath;
    }

    public void setExplicitPath(List<ExplicitPathInventory> explicitPath) {
        this.explicitPath = explicitPath;
    }
}
