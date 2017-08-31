package org.zstack.header.identity;


public enum AccountType {
    SystemAdmin(0),
    Proxy(1),
    Normal(2);

    private int value;

    private AccountType(int value){
        this.value = value;
    }

    public static AccountType valueOf(int value) {
        switch (value) {
            case 0:
                return SystemAdmin;
            case 1:
                return Proxy;
            case 2:
                return Normal;
            default:
                return null;
        }
    }

    public int value() {
        return this.value;
    }
}
