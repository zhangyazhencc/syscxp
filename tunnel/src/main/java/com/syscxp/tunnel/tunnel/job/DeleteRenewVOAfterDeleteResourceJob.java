package com.syscxp.tunnel.tunnel.job;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.job.Job;
import com.syscxp.core.job.JobContext;
import com.syscxp.core.job.RestartableJob;
import com.syscxp.core.job.UniqueResourceJob;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.billing.APIDeleteExpiredRenewMsg;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2018/1/5
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class DeleteRenewVOAfterDeleteResourceJob implements Job {
    private static final CLogger logger = Utils.getLogger(DeleteRenewVOAfterDeleteResourceJob.class);

    @JobContext
    private String resourceUuid;

    @JobContext
    private String resourceType;

    @JobContext
    private String accountUuid;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private RESTFacade restf;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            logger.info("开始执行JOB【清除RenewVO】");

            APIDeleteExpiredRenewMsg msg = new APIDeleteExpiredRenewMsg();
            msg.setProductUuid(resourceUuid);
            msg.setAccountUuid(accountUuid);

            String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.BILLING_SERVER_URL,RESTConstant.REST_API_CALL);
            InnerMessageHelper.setMD5(msg);

            RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(msg), RestAPIResponse.class);
            APIReply reply = (APIReply) RESTApiDecoder.loads(rsp.getResult());

            if (!reply.isSuccess()){
                logger.warn("【清除RenewVO】失败");
                completion.fail(reply.getError());
            }else{
                completion.success(null);
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);

            completion.fail(errf.throwableToInternalError(e));
        }

    }

    @Override
    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

}
