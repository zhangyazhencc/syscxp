package com.syscxp.core.config

import com.syscxp.header.query.APIQueryMessage

doc {
    title "QueryGlobalConfig"

    category "globalConfig"

    desc """在这里填写API描述"""

    rest {
        request {
			url "GET /v1/global-configurations"

			header (Authorization: 'OAuth the-session-uuid')


            clz APIQueryGlobalConfigMsg.class

            desc """"""
            
			params APIQueryMessage.class
        }

        response {
            clz APIQueryGlobalConfigReply.class
        }
    }
}