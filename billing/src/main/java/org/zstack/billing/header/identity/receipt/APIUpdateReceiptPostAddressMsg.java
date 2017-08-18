package org.zstack.billing.header.identity.receipt;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APIUpdateReceiptPostAddressMsg extends APIMessage {

    @APIParam(nonempty = true,resourceType = ReceiptPostAddressVO.class, checkAccount = true)
    private String uuid;

    @APIParam(required = false)
    private String name;

    @APIParam(required = false)
    private String telephone;

    @APIParam(required = false)
    private String address;

    @APIParam(required = false)
    private boolean isDefault;

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
