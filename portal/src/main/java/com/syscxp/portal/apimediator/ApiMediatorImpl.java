package com.syscxp.portal.apimediator;

import com.syscxp.header.apimediator.*;
import com.syscxp.header.managementnode.*;
import com.syscxp.header.message.APICreateMessage;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.message.MessageReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.SyncTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.utils.StringDSL;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;

import static com.syscxp.core.Platform.argerr;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.syscxp.utils.CollectionDSL.e;
import static com.syscxp.utils.CollectionDSL.map;

public class ApiMediatorImpl extends AbstractService implements ApiMediator, GlobalApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(ApiMediator.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private DatabaseFacade dbf;

    private ApiMessageProcessor processor;

    private List<String> serviceConfigFolders;
    private int apiWorkerNum = 5;

    private void dispatchMessage(APIMessage msg) {
        logger.trace(String.format("[dispatch message]: %s [%s]", msg.getId(), msg.getClass().getName()));
        ApiMessageDescriptor desc = processor.getApiMessageDescriptor(msg);
        if (desc == null) {
            Map message = map(e(msg.getClass().getName(), msg));
            String err = String.format("no service configuration file declares message: %s", JSONObjectUtil.toJsonString(message));
            logger.warn(err);
            bus.replyErrorByMessageType(msg, errf.instantiateErrorCode(PortalErrors.NO_SERVICE_FOR_MESSAGE, err));
            return;
        }


        try {
            msg.setServiceId(null);
            msg = processor.process(msg);
        } catch (ApiMessageInterceptionException ie) {
            logger.debug(ie.getError().toString(), ie);
            bus.replyErrorByMessageType(msg, ie.getError());
            return;
        } catch (StopRoutingException e) {
            return;
        }

        if (msg.getServiceId() == null && desc.getServiceId() != null) {
            bus.makeLocalServiceId(msg, desc.getServiceId());
        }

        if (msg.getServiceId() == null) {
            String err = String.format("No service id found for API message[%s], message dump: %s", msg.getMessageName(), JSONObjectUtil.toJsonString(msg));
            logger.warn(err);
            bus.replyErrorByMessageType(msg, errf.stringToInternalError(err));
            return;
        }

        logger.trace(String.format("[route message ]: %s to:%s", msg.getId(), msg.getServiceId()));
        bus.route(msg);
    }


    @Override
    public void handleMessage(final Message msg) {
        thdf.syncSubmit(new SyncTask<Object>() {
            @Override
            public String getSyncSignature() {
                return "api.worker";
            }

            @Override
            public int getSyncLevel() {
                return apiWorkerNum;
            }

            @Override
            public String getName() {
                return "api.worker";
            }

            @MessageSafe
            public void handleMessage(Message msg) {
                if (msg instanceof APIIsReadyToGoMsg) {
                    handle((APIIsReadyToGoMsg) msg);
                } else if (msg instanceof APIGetVersionMsg) {
                    handle((APIGetVersionMsg) msg);
                } else if (msg instanceof APIGetCurrentTimeMsg) {
                    handle((APIGetCurrentTimeMsg) msg);
                } else if (msg instanceof APIMessage) {
                    dispatchMessage((APIMessage) msg);
                } else {
                    logger.debug("Not an APIMessage.Message ID is " + msg.getId());
                }
            }

            @Override
            public Object call() throws Exception {
                handleMessage(msg);
                return null;
            }
        });
    }

    @Transactional(readOnly = true)
    private void handle(APIGetVersionMsg msg) {
        String sql = "select v.version from schema_version v order by version_rank desc";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setMaxResults(1);
        String version = (String) q.getSingleResult();

        APIGetVersionReply reply = new APIGetVersionReply();
        reply.setVersion(version);
        bus.reply(msg, reply);
    }

    private void handle(APIGetCurrentTimeMsg msg) {
        Map<String, Long> ret = new HashMap<String, Long>();
        long currentTimeMillis = System.currentTimeMillis();
        long currentTimeSeconds = System.currentTimeMillis()/1000;
        ret.put("MillionSeconds", currentTimeMillis);
        ret.put("Seconds", currentTimeSeconds);
        APIGetCurrentTimeReply reply = new APIGetCurrentTimeReply();
        reply.setCurrentTime(ret);
        bus.reply(msg, reply);
    }

    private void handle(final APIIsReadyToGoMsg msg) {
        final APIIsReadyToGoReply areply = new APIIsReadyToGoReply();

        IsManagementNodeReadyMsg imsg = new IsManagementNodeReadyMsg();
        String nodeId = msg.getManagementNodeId();
        if (nodeId == null) {
            bus.makeLocalServiceId(imsg, ManagementNodeConstant.SERVICE_ID);
            nodeId = Platform.getManagementServerId();
        } else {
            bus.makeServiceIdByManagementNodeId(imsg, ManagementNodeConstant.SERVICE_ID, msg.getManagementNodeId());
        }

        final String fnodeId = nodeId;
        areply.setManagementNodeId(nodeId);
        bus.send(imsg, new CloudBusCallBack(msg) {
            @Override
            public void run(MessageReply reply) {
                if (!reply.isSuccess()) {
                    areply.setError(reply.getError());
                } else {
                    IsManagementNodeReadyReply r = (IsManagementNodeReadyReply) reply;
                    if (!r.isReady()) {
                        areply.setError(errf.instantiateErrorCode(SysErrors.NOT_READY_ERROR,
                                String.format("management node[uuid:%s] is not ready yet", fnodeId)));
                    }
                }
                bus.reply(msg, areply);
            }
        });
    }

    @Override
    public String getId() {
        return ApiMediatorConstant.SERVICE_ID;
    }

    @Override
    public boolean start() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("serviceConfigFolders", serviceConfigFolders);
        processor = new ApiMessageProcessorImpl(config);
        bus.registerService(this);
        return true;
    }

    @Override
    public boolean stop() {
        bus.unregisterService(this);
        return true;
    }

    public void setServiceConfigFolders(List<String> serviceConfigFolders) {
        this.serviceConfigFolders = serviceConfigFolders;
    }

    public void setApiWorkerNum(int apiWorkerNum) {
        this.apiWorkerNum = apiWorkerNum;
    }

    @Override
    public List<Class> getMessageClassToIntercept() {
        List<Class> lst = new ArrayList<Class>();
        lst.add(APICreateMessage.class);
        return lst;
    }

    @Override
    public InterceptorPosition getPosition() {
        return InterceptorPosition.FRONT;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateMessage) {
            APICreateMessage cmsg = (APICreateMessage) msg;
            if (cmsg.getResourceUuid() != null) {
                if (!StringDSL.issyscxpUuid(cmsg.getResourceUuid())) {
                    throw new ApiMessageInterceptionException(argerr("resourceUuid[%s] is not a valid uuid. A valid uuid is a UUID(v4 recommended) with '-' stripped. " +
                                    "see http://en.wikipedia.org/wiki/Universally_unique_identifier for format of UUID, the regular expression syscxp uses" +
                                    " to validate a UUID is '[0-9a-f]{8}[0-9a-f]{4}[1-5][0-9a-f]{3}[89ab][0-9a-f]{3}[0-9a-f]{12}'", cmsg.getResourceUuid()));
                }
            }
        }
        return msg;
    }
}
