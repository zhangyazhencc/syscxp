package org.zstack.tunnel.header.tunnel;

import org.zstack.header.billing.ProductChargeModel;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.header.endpoint.EndpointVO;
import org.zstack.tunnel.header.switchs.SwitchPortAttribute;
import org.zstack.tunnel.header.switchs.SwitchPortVO;
import org.zstack.tunnel.manage.TunnelConstant;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
@Action(category = TunnelConstant.ACTION_CATEGORY)
public class APICreateInterfaceManualMsg extends APIMessage {


    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 32,resourceType = SwitchPortVO.class)
    private String switchPortUuid;
    @APIParam(emptyString = false,maxLength = 32,resourceType = EndpointVO.class)
    private String endpointUuid;
    @APIParam
    private Long bandwidth;
    @APIParam(emptyString = false,required = false,maxLength = 255)
    private String description;
    @APIParam
    private Integer duration;
    @APIParam(emptyString = false,validValues = {"BY_MONTH", "BY_YEAR","BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam(nonempty = true)
    private List<String> productPriceUnitUuids;

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

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
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

    public List<String> getProductPriceUnitUuids() {
        return productPriceUnitUuids;
    }

    public void setProductPriceUnitUuids(List<String> productPriceUnitUuids) {
        this.productPriceUnitUuids = productPriceUnitUuids;
    }
}
