package com.syscxp.core.config

import com.syscxp.header.errorcode.ErrorCode

doc {

	title "更新全局配置返回信息"

	ref {
		name "error"
		path "APIUpdateGlobalConfigEvent.error"
		desc "错误码，若不为null，则表示操作失败, 操作成功时该字段为null",false
		type "ErrorCode"
		since "0.6"
		clz ErrorCode.class
	}
	ref {
		name "inventory"
		path "APIUpdateGlobalConfigEvent.inventory"
		desc "null"
		type "GlobalConfigInventory"
		since "0.6"
		clz GlobalConfigInventory.class
	}
}
