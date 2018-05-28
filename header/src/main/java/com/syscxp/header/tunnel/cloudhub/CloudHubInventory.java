package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.search.Inventory;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = CloudHubVO.class)
public class CloudHubInventory {

    private String uuid;
    private Long number;
    private String name;
    private String accountUuid;
    private String interfaceUuid;
    private String endpointUuid;
    private String cloudHubOfferingUuid;
    private Long bandwidth;
    private String bandwidthDesc;
    private Integer tunnelNumber;
    private Integer duration;
    private String productChargeModel;
    private Integer maxModifies;
    private Timestamp expireDate;
    private Timestamp createDate;
    private Timestamp lastOpDate;
    private boolean expired;


    public static CloudHubInventory valueOf(CloudHubVO vo) {
        CloudHubInventory inv = new CloudHubInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setNumber(vo.getNumber());
        inv.setBandwidth(vo.getBandwidth());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setInterfaceUuid(vo.getInterfaceUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setCloudHubOfferingUuid(vo.getCloudHubOfferingUuid());
        inv.setTunnelNumber(vo.getTunnelNumber());
        inv.setDuration(vo.getDuration());
        inv.setProductChargeModel(vo.getProductChargeModel().toString());
        inv.setMaxModifies(vo.getMaxModifies());
        inv.setExpireDate(vo.getExpireDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<CloudHubInventory> valueOf(Collection<CloudHubVO> vos) {
        List<CloudHubInventory> lst = new ArrayList<CloudHubInventory>(vos.size());
        for (CloudHubVO vo : vos) {
            lst.add(CloudHubInventory.valueOf(vo));
        }
        return lst;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getCloudHubOfferingUuid() {
        return cloudHubOfferingUuid;
    }

    public void setCloudHubOfferingUuid(String cloudHubOfferingUuid) {
        this.cloudHubOfferingUuid = cloudHubOfferingUuid;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
        if(bandwidth < 1073741824){
            this.bandwidthDesc = Long.toString(bandwidth/1048576)+"M";
        }else{
            this.bandwidthDesc = Long.toString(bandwidth/1073741824)+"G";
        }
    }

    public Integer getTunnelNumber() {
        return tunnelNumber;
    }

    public void setTunnelNumber(Integer tunnelNumber) {
        this.tunnelNumber = tunnelNumber;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public String getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(String productChargeModel) {
        this.productChargeModel = productChargeModel;
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

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getBandwidthDesc() {
        return bandwidthDesc;
    }

    public void setBandwidthDesc(String bandwidthDesc) {
        this.bandwidthDesc = bandwidthDesc;
    }
}
