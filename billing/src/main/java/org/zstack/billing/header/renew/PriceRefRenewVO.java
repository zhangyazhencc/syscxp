package org.zstack.billing.header.renew;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class PriceRefRenewVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    private String renewUuid;

    @Column
    private String productPriceUnitUuid;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

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


    public String getProductPriceUnitUuid() {
        return productPriceUnitUuid;
    }

    public void setProductPriceUnitUuid(String productPriceUnitUuid) {
        this.productPriceUnitUuid = productPriceUnitUuid;
    }

    public String getRenewUuid() {
        return renewUuid;
    }

    public void setRenewUuid(String renewUuid) {
        this.renewUuid = renewUuid;
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
}
