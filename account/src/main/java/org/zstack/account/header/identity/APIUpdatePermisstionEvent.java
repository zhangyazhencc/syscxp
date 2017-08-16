package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
<<<<<<< HEAD:account/src/main/java/org/zstack/account/header/identity/APIUpdatePermisstionEvent.java
public class APIUpdatePermisstionEvent extends APIEvent {
    private AuthorityInventory inventory;
=======
public class APIUpdateAuthorityEvent extends APIEvent {
    private PermissionInventory inventory;
>>>>>>> 4993a761587240fd1e76817a47f0a28c5c180319:account/src/main/java/org/zstack/account/header/identity/APIUpdateAuthorityEvent.java

    public APIUpdatePermisstionEvent(String apiId) {
        super(apiId);
    }

    public APIUpdatePermisstionEvent() {
        super(null);
    }

    public PermissionInventory getInventory() {
        return inventory;
    }

    public void setInventory(PermissionInventory inventory) {
        this.inventory = inventory;
    }

}
