package org.zstack.header.identity;

import org.zstack.header.rest.SDK;

/**
 * Created by zxhread on 17/8/3.
 */

@SDK(sdkClassName = "PolicyStatementEffect")
public enum StatementEffect {
    Allow,
    Deny,
}
