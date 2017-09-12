package org.zstack.tunnel.header.tunnel;

import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.switchs.SwitchPortInventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-08
 */
@Inventory(mappingVOClass = InterfaceVO.class)
public class InterfaceToSwitchPortInventory {

    private String uuid;
    private String accountUuid;
    private String name;
    private String switchPortUuid;
    private SwitchPortInventory switchPort;
    private String endpointUuid;
    private Integer bandwidth;
    private Integer isExclusive;
    private String description;
    private Integer months;
    private Timestamp expiredDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static InterfaceToSwitchPortInventory valueOf(InterfaceVO vo){
        InterfaceToSwitchPortInventory inv = new InterfaceToSwitchPortInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setName(vo.getName());
        inv.setSwitchPortUuid(vo.getSwitchPortUuid());
        inv.setSwitchPort(SwitchPortInventory.valueOf(vo.getSwitchPortVO()));
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setBandwidth(vo.getBandwidth());
        inv.setIsExclusive(vo.getIsExclusive());
        inv.setDescription(vo.getDescription());
        inv.setMonths(vo.getMonths());
        inv.setExpiredDate(vo.getExpiredDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<InterfaceToSwitchPortInventory> valueOf(Collection<InterfaceVO> vos) {
        List<InterfaceToSwitchPortInventory> lst = new ArrayList<InterfaceToSwitchPortInventory>(vos.size());
        for (InterfaceVO vo : vos) {
            lst.add(InterfaceToSwitchPortInventory.valueOf(vo));
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Integer getIsExclusive() {
        return isExclusive;
    }

    public void setIsExclusive(Integer isExclusive) {
        this.isExclusive = isExclusive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public SwitchPortInventory getSwitchPort() {
        return switchPort;
    }

    public void setSwitchPort(SwitchPortInventory switchPort) {
        this.switchPort = switchPort;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }
}
