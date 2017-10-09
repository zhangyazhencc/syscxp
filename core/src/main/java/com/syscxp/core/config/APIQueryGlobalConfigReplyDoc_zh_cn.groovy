package com.syscxp.core.config

import com.syscxp.header.errorcode.ErrorCode

doc {

	title "在这里输入结构的名称"

	ref {
		name "error"
		path "APIQueryGlobalConfigReply.error"
		desc "错误码，若不为null，则表示操作失败, 操作成功时该字段为null",false
		type "ErrorCode"
		since "0.6"
		clz ErrorCode.class
	}
	ref {
		name "inventories"
		path "APIQueryGlobalConfigReply.inventories"
		desc "null"
		type "List"
		since "0.6"
		clz GlobalConfigInventory.class
	}
}
