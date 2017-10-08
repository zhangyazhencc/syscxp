package com.syscxp.account.header.user;

/**
 * Created by frank on 3/1/2016.
 */
public interface AfterCreateUserExtensionPoint {
    void afterCreateUser(UserInventory user);
}
