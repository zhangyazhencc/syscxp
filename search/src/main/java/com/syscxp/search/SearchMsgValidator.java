package com.syscxp.search;

import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.GlobalApiMessageInterceptor;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.search.APISearchMessage;
import com.syscxp.header.search.APISearchMessage.NOLTriple;
import com.syscxp.header.search.APISearchMessage.NOVTriple;
import com.syscxp.header.search.SearchOp;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.List;

public class SearchMsgValidator implements GlobalApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(SearchMsgValidator.class);
    
    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException{
        if (msg instanceof APISearchMessage) {
            APISearchMessage smsg = (APISearchMessage)msg;
            try {
                for (NOLTriple t : smsg.getNameOpListTriples()) {
                    SearchOp.valueOf(t.getOp());
                }
                for (NOVTriple t : smsg.getNameOpValueTriples()) {
                    SearchOp.valueOf(t.getOp());
                }
            } catch (IllegalArgumentException e) {
                logger.warn("", e);
                //ErrorCode err = ErrorCodeFacade.generateErrorCode(ErrorCodeFacade.BuiltinErrors.INVALID_ARGRUMENT.toString(), e.getMessage());
                throw new ApiMessageInterceptionException(new ErrorCode());
            }
        }
        return msg;
    }

    @Override
    public List<Class> getMessageClassToIntercept() {
        return null;
    }

    @Override
    public InterceptorPosition getPosition() {
        return InterceptorPosition.FRONT;
    }

}
