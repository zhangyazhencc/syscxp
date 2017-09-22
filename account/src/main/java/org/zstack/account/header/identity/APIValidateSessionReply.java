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

    private AccountInventory accountInventory;

    private UserInventory UserInventory;

    public AccountInventory getAccountInventory() {
        return accountInventory;
    }

    public UserInventory getUserInventory() {
        return UserInventory;
    }

    public void setAccountInventory(AccountInventory accountInventory) {
        this.accountInventory = accountInventory;
    }

    public void setUserInventory(UserInventory userInventory) {
        UserInventory = userInventory;
    }

    public boolean isValidSession() {
        return validSession;
    }

    public void setValidSession(boolean validSession) {
        this.validSession = validSession;
    }

    public static APIValidateSessionReply __example__() {
        APIValidateSessionReply reply = new APIValidateSessionReply();
        reply.setValidSession(true);
        return reply;
    }

}
