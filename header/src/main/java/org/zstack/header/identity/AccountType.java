package org.zstack.header.identity;


public enum AccountType {
    SystemAdmin(0),
    Proxy(1),
    Normal(2);

    private Integer value;

    private AccountType(Integer value){
        this.value = value;
    }

    public static AccountType valueOf(Integer value) {
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

    public Integer value() {
        return this.value;
    }
}
