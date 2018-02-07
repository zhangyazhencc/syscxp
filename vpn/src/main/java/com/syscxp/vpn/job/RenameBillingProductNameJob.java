package com.syscxp.vpn.job;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.job.*;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.billing.APIRenameProductNameMsg;
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

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class RenameBillingProductNameJob implements Job {
    private static final CLogger logger = Utils.getLogger(RenameBillingProductNameJob.class);

    @JobContext
    private String resourceUuid;

    @JobContext
    private String resourceName;

    @JobContext
    private String resourceType;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private RESTFacade restf;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {
        try {

            APIRenameProductNameMsg msg = new APIRenameProductNameMsg();
            msg.setProductUuid(resourceUuid);
            msg.setProductName(resourceName);

            String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.BILLING_SERVER_URL, RESTConstant.REST_API_CALL);
            InnerMessageHelper.setMD5(msg);

            RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(msg), RestAPIResponse.class, 0);
            APIReply reply = (APIReply) RESTApiDecoder.loads(rsp.getResult());

            if (reply.isSuccess()){
                completion.success(null);
            }else{
                completion.fail(reply.getError());
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

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public static RenameBillingProductNameJob executeJob(JobQueueFacade jobf, String resourceUuid, String resourceName){
        RenameBillingProductNameJob job = new RenameBillingProductNameJob();
        job.setResourceUuid(resourceUuid);
        job.setResourceName(resourceName);

        jobf.execute("updateBillingRenewName", Platform.getManagementServerId(), job);

        return job;
    }
}
