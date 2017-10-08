package com.syscxp.core.progress;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.errorcode.ErrorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.core.progress.APIGetTaskProgressMsg;
import com.syscxp.header.message.APIMessage;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.utils.StringDSL.isApiId;

/**
 * Created by miao on 17-5-16.
 */
public class ProgressApiInterceptor implements ApiMessageInterceptor {
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private CloudBus bus;

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APIGetTaskProgressMsg) {
            validate((APIGetTaskProgressMsg) msg);
        }

        return msg;
    }

    private void validate(APIGetTaskProgressMsg msg) {
        if (!isApiId(msg.getApiId())) {
            throw new ApiMessageInterceptionException(argerr("parameter apiId[%s] is not a valid uuid.", msg.getApiId()));
        }
    }
}
