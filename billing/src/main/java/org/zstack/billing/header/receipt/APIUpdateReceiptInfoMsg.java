package org.zstack.billing.header.receipt;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
@Action(category = BillingConstant.ACTION_CATEGORY, names = {"receipt"})
public class APIUpdateReceiptInfoMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = ReceiptInfoVO.class, checkAccount = true)
    private String uuid;

    @APIParam(required = false)
    private ReceiptType type;

    @APIParam(required = false)
    private String title;

    @APIParam(required = false)
    private String bankName;

    @APIParam(required = false)
    private String bankAccountNumber;

    @APIParam(required = false)
    private String telephone;

    @APIParam(required = false)
    private String identifyNumber;

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

    public ReceiptType getType() {
        return type;
    }

    public void setType(ReceiptType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIdentifyNumber() {
        return identifyNumber;
    }

    public void setIdentifyNumber(String identifyNumber) {
        this.identifyNumber = identifyNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
