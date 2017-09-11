package org.zstack.tunnel.header.tunnel;

import org.zstack.header.vo.ForeignKey;
import org.zstack.tunnel.header.switchs.SwitchPortVO;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
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
    @ForeignKey(parentEntityClass = SwitchPortVO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String switchPortUuid;

    @Column
    private String endpointUuid;

    @Column
    private Integer bandwidth;

    @Column
    private Integer isExclusive;

    @Column
    private String description;

    @Column
    private Timestamp expiredDate;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

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
}
