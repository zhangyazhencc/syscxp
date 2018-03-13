package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.query.ExpandedQueries;
import com.syscxp.header.query.ExpandedQuery;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.switchs.SwitchPortInventory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-08
 */
@Inventory(mappingVOClass = InterfaceVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "switchPort", inventoryClass = SwitchPortInventory.class,
                foreignKey = "switchPortUuid", expandedInventoryKey = "uuid"),

})
public class InterfaceInventory {
    private String uuid;
    private Long number;
    private String accountUuid;
    private String ownerAccountUuid;
    private String name;
    private String switchPortUuid;
    private String switchPortName;
    private String endpointUuid;
    private String endpointName;
    private String description;
    private String state;
    private String type;
    private String switchPortType;
    private Integer duration;
    private String productChargeModel;
    private Integer maxModifies;
    private Timestamp expireDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;
    private boolean expired;

    public static InterfaceInventory valueOf(InterfaceVO vo){
        InterfaceInventory inv = new InterfaceInventory();
        inv.setUuid(vo.getUuid());
        inv.setNumber(vo.getNumber());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setOwnerAccountUuid(vo.getOwnerAccountUuid());
        inv.setName(vo.getName());
        inv.setSwitchPortUuid(vo.getSwitchPortUuid());
        inv.setSwitchPortName(vo.getSwitchPortVO().getPortName());
        inv.setSwitchPortType(vo.getSwitchPortVO().getPortType());
        inv.setEndpointName(vo.getEndpointVO().getName());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setDescription(vo.getDescription());
        inv.setState(vo.getState().toString());
        inv.setType(vo.getType().toString());
        inv.setDuration(vo.getDuration());
        inv.setProductChargeModel(vo.getProductChargeModel().toString());
        inv.setMaxModifies(vo.getMaxModifies());
        inv.setExpireDate(vo.getExpireDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<InterfaceInventory> valueOf(Collection<InterfaceVO> vos) {
        List<InterfaceInventory> lst = new ArrayList<InterfaceInventory>(vos.size());
        for (InterfaceVO vo : vos) {
            lst.add(InterfaceInventory.valueOf(vo));
        }
        return lst;
    }

    public String getSwitchPortType() {
        return switchPortType;
    }

    public void setSwitchPortType(String switchPortType) {
        this.switchPortType = switchPortType;
    }

    public String getSwitchPortName() {
        return switchPortName;
    }

    public void setSwitchPortName(String switchPortName) {
        this.switchPortName = switchPortName;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(String productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;

        if (expireDate != null){
            if (expireDate.before(Timestamp.valueOf(LocalDateTime.now()))){
                this.expired = true;
            }
        }
    }

    public String getOwnerAccountUuid() {
        return ownerAccountUuid;
    }

    public void setOwnerAccountUuid(String ownerAccountUuid) {
        this.ownerAccountUuid = ownerAccountUuid;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
