package com.syscxp.vpn.vpn;

import com.syscxp.core.Platform;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.CancelablePeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.utils.DebugUtils;
import com.syscxp.vpn.exception.VpnServiceException;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.vpn.VpnAgentCommand;
import com.syscxp.header.vpn.VpnAgentResponse;
import com.syscxp.header.vpn.VpnAgentResponse.TaskResult;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.concurrent.TimeUnit;


@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class VpnRESTCaller {
    private static final CLogger logger = Utils.getLogger(VpnRESTCaller.class);
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private ErrorFacade errf;

    private String baseUrl;

    public VpnRESTCaller(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public VpnRESTCaller() {
        this(CoreGlobalProperty.VPN_BASE_URL);
    }

    public void checkStatus(final String path, final VpnAgentCommand cmd,
                            final ReturnValueCompletion<VpnAgentResponse> completion, final long interval, final long timeout) {
        String url = buildUrl(path);
        class CheckStatus implements CancelablePeriodicTask {
            private long count;

            CheckStatus() {
                this.count = timeout / interval;
                DebugUtils.Assert(count != 0, String.format("invalid timeout[%s], interval[%s]", timeout, interval));
            }

            @Override
            public boolean run() {
                try {
                    VpnAgentResponse rsp = restf.syncJsonPost(url, cmd, VpnAgentResponse.class);
                    logger.debug(String.format("successfully post %s", url));
                    completion.success(rsp);
                    return true;
                } catch (Exception e) {
                    String info = String.format("still unable to post %s, will try %s times. %s", url, count, e.getMessage());
                    logger.debug(info);
                    if (--count <= 0) {
                        completion.fail(Platform.operr("unable to post %s in %sms", url, timeout));
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public TimeUnit getTimeUnit() {
                return TimeUnit.MILLISECONDS;
            }

            @Override
            public long getInterval() {
                return interval;
            }

            @Override
            public String getName() {
                return "CheckStatus";
            }
        }

        thdf.submitCancelablePeriodicTask(new CheckStatus());
    }


    public void checkStatus(String path, VpnAgentCommand cmd, ReturnValueCompletion<VpnAgentResponse> completion) {
        checkStatus(path, cmd, completion, TimeUnit.SECONDS.toMillis(5), TimeUnit.MINUTES.toMillis(5));
    }

    /**
     * 获取任务处理结果
     */
    public TaskResult syncPostForResult(String path, VpnAgentCommand cmd) {
        return syncPostForVPN(path, cmd).getResult();
    }

    public VpnAgentResponse syncPostForVPN(String path, VpnAgentCommand cmd) {
        return restf.syncJsonPost(buildUrl(path), cmd, VpnAgentResponse.class);
    }

    /**
     * 生成URL
     */
    private String buildUrl(String path) {
        return URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.VPN_ROOT_PATH, path);
    }

    /**
     * http调用内部服务
     */
    public APIReply syncJsonPost(APIMessage innerMsg) {
        String url = URLBuilder.buildUrlFromBase(baseUrl, RESTConstant.REST_API_CALL);
        InnerMessageHelper.setMD5(innerMsg);

        RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(innerMsg), RestAPIResponse.class);
        return (APIReply) RESTApiDecoder.loads(rsp.getResult());
    }

    public void sendCommand(String path, VpnAgentCommand cmd, final Completion completion) {
        try {
            TaskResult result = syncPostForResult(path, cmd);
            logger.debug(String.format("successfully post %s", path));
            if (result.isSuccess()) {
                completion.success();
            } else {
                completion.fail(Platform.operr("failed to execute the command[%s]. %s", path, result.getMessage()));
            }
        } catch (Exception e) {
            String info = String.format("unable to post %s. %s", path, e.getMessage());
            logger.debug(info);
            completion.fail(Platform.operr("unable to post %s. %s", path, e.getMessage()));
        }
    }
}

