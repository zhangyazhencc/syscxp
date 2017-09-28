package org.zstack.tunnel.header.tunnel;

import org.zstack.header.billing.ProductChargeModel;
import org.zstack.header.identity.AccountType;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.util.List;

/**
 * Create by DCY on 2017/9/28
 */
public class APIUpdateInterfaceBandwidthMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = InterfaceVO.class)
    private String uuid;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;
    @APIParam
    private Long bandwidth;
    @APIParam(nonempty = true)
    private List<String> productPriceUnitUuids;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        if(getSession().getType() == AccountType.SystemAdmin){
            return accountUuid;
        }else{
            return getSession().getAccountUuid();
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public List<String> getProductPriceUnitUuids() {
        return productPriceUnitUuids;
    }

    public void setProductPriceUnitUuids(List<String> productPriceUnitUuids) {
        this.productPriceUnitUuids = productPriceUnitUuids;
    }
}
