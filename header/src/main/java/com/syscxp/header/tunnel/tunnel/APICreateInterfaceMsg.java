package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.host.APIChangeHostStateEvent;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

/**
 * Created by DCY on 2017-09-08
 */

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APICreateInterfaceEvent.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"create"})
public class APICreateInterfaceMsg extends APIMessage {

    @APIParam(required = false, maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false, maxLength = 128)
    private String name;
    @APIParam(emptyString = false, maxLength = 32, resourceType = EndpointVO.class)
    private String endpointUuid;
    @APIParam(emptyString = false,maxLength = 32,resourceType = PortOfferingVO.class)
    private String portOfferingUuid;
    @APIParam(required = false, maxLength = 255)
    private String description;
    @APIParam(required = false)
    private Integer duration;
    @APIParam(required = false,validValues = {"BY_MONTH", "BY_YEAR", "BY_DAY"})
    private ProductChargeModel productChargeModel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAccountUuid() {
        if (getSession().getType() == AccountType.SystemAdmin) {
            return accountUuid;
        } else {
            return getSession().getAccountUuid();
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getPortOfferingUuid() {
        return portOfferingUuid;
    }

    public void setPortOfferingUuid(String portOfferingUuid) {
        this.portOfferingUuid = portOfferingUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateInterfaceEvent) evt).getInventory().getUuid();
                }

                ntfy("Create InterfaceVO")
                        .resource(uuid, InterfaceVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
