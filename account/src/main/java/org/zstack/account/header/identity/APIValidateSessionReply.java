package org.zstack.account.header.identity;

import org.zstack.account.header.account.AccountInventory;
import org.zstack.account.header.user.UserInventory;
import org.zstack.header.message.APIReply;

/**
 * Created with IntelliJ IDEA.
 * User: frank
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class APIValidateSessionReply extends APIReply {

    private boolean validSession;

    private UserInventory userInventory;

    private AccountInventory accountInventory;

    public boolean isValidSession() {
        return validSession;
    }

    public void setValidSession(boolean validSession) {
        this.validSession = validSession;
    }

    public UserInventory getUserInventory() {
        return userInventory;
    }

    public void setUserInventory(UserInventory userInventory) {
        this.userInventory = userInventory;
    }

    public AccountInventory getAccountInventory() {
        return accountInventory;
    }

    public void setAccountInventory(AccountInventory accountInventory) {
        this.accountInventory = accountInventory;
    }
}
