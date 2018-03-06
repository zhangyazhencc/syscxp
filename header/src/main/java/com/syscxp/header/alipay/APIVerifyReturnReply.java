package com.syscxp.header.alipay;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;

public class APIVerifyReturnReply  extends APIReply {

    private boolean inventory;
    private BigDecimal addMoney = BigDecimal.ZERO;

    public boolean isInventory() {
        return inventory;
    }

    public BigDecimal getAddMoney() {
        return addMoney;
    }

    public void setAddMoney(BigDecimal addMoney) {
        this.addMoney = addMoney;
    }

    public boolean getInventory() {
        return inventory;
    }

    public void setInventory(boolean inventory) {
        this.inventory = inventory;
    }
}