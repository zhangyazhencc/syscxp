package org.zstack.billing.header.identity.receipt;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APICreateReceiptInfoMsg extends APIMessage {

    @APIParam(nonempty = true)
    private ReceiptType type;

    @APIParam(nonempty = true)
    private String title;

    @APIParam(nonempty = true)
    private String bankName;

    @APIParam(nonempty = true)
    private String bankAccountNumber;

    @APIParam(nonempty = true)
    private String telephone;

    @APIParam(nonempty = true)
    private String identifyNumber;

    @APIParam(nonempty = true)
    private String address;

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
