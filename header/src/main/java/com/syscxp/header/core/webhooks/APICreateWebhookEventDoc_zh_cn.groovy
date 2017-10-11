package com.syscxp.header.core.webhooks

import com.syscxp.header.errorcode.ErrorCode

doc {

	title "在这里输入结构的名称"

	ref {
		name "error"
		path "APICreateWebhookEvent.error"
		desc "错误码，若不为null，则表示操作失败, 操作成功时该字段为null",false
		type "ErrorCode"
		since "0.6"
		clz ErrorCode.class
	}
	ref {
		name "inventory"
		path "APICreateWebhookEvent.inventory"
		desc "null"
		type "WebhookInventory"
		since "0.6"
		clz WebhookInventory.class
	}
}