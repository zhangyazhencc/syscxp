package com.syscxp.core.notification

import com.syscxp.header.query.APIQueryMessage

doc {
    title "QueryNotificationSubscription"

    category "未知类别"

    desc """在这里填写API描述"""

    rest {
        request {
			url "GET /v1/notifications/subscriptions"
			url "GET /v1/notifications/subscriptions/{uuid}"

			header (Authorization: 'OAuth the-session-uuid')


            clz APIQueryNotificationSubscriptionMsg.class

            desc """"""
            
			params APIQueryMessage.class
        }

        response {
            clz APIQueryNotificationSubscriptionReply.class
        }
    }
}