package org.zstack.header.message;

import org.zstack.header.identity.SessionInventory;
import org.zstack.header.rest.APINoSee;
import org.zstack.utils.FieldUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public abstract class InnerAPIMessage extends APIMessage {

    private String signature;
    
}
