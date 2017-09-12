package org.zstack.tunnel.header.tunnel;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
@Inventory(mappingVOClass = TunnelVO.class)
public class TunnelToNetWorkAndSwitchPortInventory {

    private String uuid;
    private String accountUuid;
    private String netWorkUuid;
    private NetWorkInventory netWork;
    private String name;
    private Integer bandwidth;
    private Double distance;
    private TunnelState state;
    private TunnelStatus status;
    private String interfaceAUuid;
    private InterfaceToSwitchPortInventory interfaceA;
    private Integer aVlan;
    private Integer enableQinqA;
    private String interfaceZUuid;
    private InterfaceToSwitchPortInventory interfaceZ;
    private Integer zVlan;
    private Integer enableQinqZ;
    private Integer isMonitor;
    private Integer months;
    private String description;
    private Timestamp expiredDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TunnelToNetWorkAndSwitchPortInventory valueOf(TunnelVO vo){
        TunnelToNetWorkAndSwitchPortInventory inv = new TunnelToNetWorkAndSwitchPortInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setNetWorkUuid(vo.getNetWorkUuid());
        inv.setNetWork(NetWorkInventory.valueOf(vo.getNetWorkVO()));
        inv.setName(vo.getName());
        inv.setBandwidth(vo.getBandwidth());
        inv.setDistance(vo.getDistance());
        inv.setState(vo.getState());
        inv.setStatus(vo.getStatus());
        inv.setInterfaceAUuid(vo.getInterfaceAUuid());
        inv.setInterfaceA(InterfaceToSwitchPortInventory.valueOf(vo.getInterfaceAVO()));
        inv.setaVlan(vo.getaVlan());
        inv.setEnableQinqA(vo.getEnableQinqA());
        inv.setInterfaceZUuid(vo.getInterfaceZUuid());
        inv.setInterfaceZ(InterfaceToSwitchPortInventory.valueOf(vo.getInterfaceZVO()));
        inv.setzVlan(vo.getzVlan());
        inv.setEnableQinqZ(vo.getEnableQinqZ());
        inv.setIsMonitor(vo.getIsMonitor());
        inv.setMonths(vo.getMonths());
        inv.setDescription(vo.getDescription());
        inv.setExpiredDate(vo.getExpiredDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TunnelToNetWorkAndSwitchPortInventory> valueOf(Collection<TunnelVO> vos) {
        List<TunnelToNetWorkAndSwitchPortInventory> lst = new ArrayList<TunnelToNetWorkAndSwitchPortInventory>(vos.size());
        for (TunnelVO vo : vos) {
            lst.add(TunnelToNetWorkAndSwitchPortInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getNetWorkUuid() {
        return netWorkUuid;
    }

    public void setNetWorkUuid(String netWorkUuid) {
        this.netWorkUuid = netWorkUuid;
    }

    public NetWorkInventory getNetWork() {
        return netWork;
    }

    public void setNetWork(NetWorkInventory netWork) {
        this.netWork = netWork;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public TunnelState getState() {
        return state;
    }

    public void setState(TunnelState state) {
        this.state = state;
    }

    public TunnelStatus getStatus() {
        return status;
    }

    public void setStatus(TunnelStatus status) {
        this.status = status;
    }

    public String getInterfaceAUuid() {
        return interfaceAUuid;
    }

    public void setInterfaceAUuid(String interfaceAUuid) {
        this.interfaceAUuid = interfaceAUuid;
    }

    public InterfaceToSwitchPortInventory getInterfaceA() {
        return interfaceA;
    }

    public void setInterfaceA(InterfaceToSwitchPortInventory interfaceA) {
        this.interfaceA = interfaceA;
    }

    public Integer getaVlan() {
        return aVlan;
    }

    public void setaVlan(Integer aVlan) {
        this.aVlan = aVlan;
    }

    public Integer getEnableQinqA() {
        return enableQinqA;
    }

    public void setEnableQinqA(Integer enableQinqA) {
        this.enableQinqA = enableQinqA;
    }

    public String getInterfaceZUuid() {
        return interfaceZUuid;
    }

    public void setInterfaceZUuid(String interfaceZUuid) {
        this.interfaceZUuid = interfaceZUuid;
    }

    public InterfaceToSwitchPortInventory getInterfaceZ() {
        return interfaceZ;
    }

    public void setInterfaceZ(InterfaceToSwitchPortInventory interfaceZ) {
        this.interfaceZ = interfaceZ;
    }

    public Integer getzVlan() {
        return zVlan;
    }

    public void setzVlan(Integer zVlan) {
        this.zVlan = zVlan;
    }

    public Integer getEnableQinqZ() {
        return enableQinqZ;
    }

    public void setEnableQinqZ(Integer enableQinqZ) {
        this.enableQinqZ = enableQinqZ;
    }

    public Integer getIsMonitor() {
        return isMonitor;
    }

    public void setIsMonitor(Integer isMonitor) {
        this.isMonitor = isMonitor;
    }

    public Timestamp getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
