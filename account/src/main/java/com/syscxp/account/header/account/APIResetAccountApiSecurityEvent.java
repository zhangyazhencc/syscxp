package com.syscxp.account.header.account;

import com.syscxp.header.message.APIEvent;

/**
 * Created by wangwg on 2017/08/23.
 */
public class APIResetAccountApiSecurityEvent extends APIEvent {
    private AccountApiSecurityInventory inventory;

    public AccountApiSecurityInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountApiSecurityInventory inventory) {
        this.inventory = inventory;
    }

    public APIResetAccountApiSecurityEvent() {
    }

    public APIResetAccountApiSecurityEvent(String apiId) {
        super(apiId);
    }


}