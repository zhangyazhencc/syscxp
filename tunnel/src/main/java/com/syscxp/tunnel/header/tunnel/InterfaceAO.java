package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.vo.ForeignKey;
import com.syscxp.tunnel.header.endpoint.EndpointEO;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-08
 */
@MappedSuperclass
public class InterfaceAO {

    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    private String name;

    @Column
    private String switchPortUuid;

    @Column
    @ForeignKey(parentEntityClass = EndpointEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String endpointUuid;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private InterfaceState state;

    @Column
    @Enumerated(EnumType.STRING)
    private NetworkType type;

    @Column
    private Integer duration;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductChargeModel productChargeModel;

    @Column
    private Integer maxModifies;

    @Column
    private Timestamp expireDate;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
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

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public InterfaceState getState() {
        return state;
    }

    public void setState(InterfaceState state) {
        this.state = state;
    }

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public NetworkType getType() {
        return type;
    }

    public void setType(NetworkType type) {
        this.type = type;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }
}
