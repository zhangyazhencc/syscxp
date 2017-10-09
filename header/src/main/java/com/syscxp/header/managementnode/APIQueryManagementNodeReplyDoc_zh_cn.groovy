package com.syscxp.header.managementnode

import com.syscxp.header.errorcode.ErrorCode

doc {

    title "管理节点清单"

    ref {
        name "error"
        path "APIQueryManagementNodeReply.error"
        desc "错误码，若不为null，则表示操作失败, 操作成功时该字段为null", false
        type "ErrorCode"
        since "0.6"
        clz ErrorCode.class
    }
    ref {
        name "inventories"
        path "APIQueryManagementNodeReply.inventories"
        desc "管理节点清单"
        type "List"
        since "0.6"
        clz ManagementNodeInventory.class
    }
}
