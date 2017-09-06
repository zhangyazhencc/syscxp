package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-29
 */
public class APICreateSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String endpointUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String code;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 32)
    private String switchAttributionUuid;
    @APIParam(emptyString = false,validValues = {"FABRIC", "INTERNET"})
    private SwitchUpperType upperType;
    @APIParam(emptyString = false)
    private Integer isPrivate;
    @APIParam(required = false,maxLength = 255)
    private String description;

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSwitchAttributionUuid() {
        return switchAttributionUuid;
    }

    public void setSwitchAttributionUuid(String switchAttributionUuid) {
        this.switchAttributionUuid = switchAttributionUuid;
    }

    public SwitchUpperType getUpperType() {
        return upperType;
    }

    public void setUpperType(SwitchUpperType upperType) {
        this.upperType = upperType;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
