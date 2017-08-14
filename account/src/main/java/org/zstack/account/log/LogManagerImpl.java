package org.zstack.account.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.zstack.account.header.log.OperLogVO;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.CloudBusEventListener;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.config.GlobalConfigFacade;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.message.*;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

import javax.persistence.EntityManagerFactory;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LogManagerImpl extends AbstractService implements LogManager, CloudBusEventListener, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(LogManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private EventFacade evtf;
    @Autowired
    private GlobalConfigFacade gcf;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Set<String> basePkgNames;

    private Map<String, SessionInventory> sessions = new ConcurrentHashMap<>();

    void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Set<APIEvent> boundEvents = new HashSet<APIEvent>(100);
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        scanner.resetFilters(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(APIEvent.class));
        for (String pkg : getBasePkgNames()) {
            for (BeanDefinition bd : scanner.findCandidateComponents(pkg)) {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                if (clazz == APIEvent.class) {
                    continue;
                }
                APIEvent evt = (APIEvent) clazz.newInstance();
                boundEvents.add(evt);
            }
        }

        for (APIEvent e : boundEvents) {
            bus.subscribeEvent(this, e);
        }
    }

    public Set<String> getBasePkgNames() {
        if (basePkgNames == null) {
            basePkgNames = new HashSet<String>();
            basePkgNames.add("org.zstack");
        }
        return basePkgNames;
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean start() {

        return true;
    }

    private OperLogVO saveOperLog(APIEvent ae, String des) {

        SessionInventory session = sessions.get(ae.getApiId());
        sessions.remove(ae.getApiId());
        OperLogVO operLogVO = new OperLogVO();
        operLogVO.setUuid(Platform.getUuid());
        operLogVO.setAccountUuid(session.getAccountUuid());
        operLogVO.setUserUuid(session.getUserUuid());
        operLogVO.setResourceUuid("");
        operLogVO.setResourceType(ae.getClass().getSimpleName());
        operLogVO.setAction("api");
        operLogVO.setCategory(ae.getServiceId() + "");
        operLogVO.setState(ae.isSuccess() ? "sucess" : "fail");
        operLogVO.setDescription(des);
        return dbf.persistAndRefresh(operLogVO);

    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        sessions.put(msg.getId(),msg.getSession());
        return msg;
    }

    @Override
    public boolean handleEvent(Event e) {
        try {
            if (!(e instanceof APIEvent)) {
                return false;
            }

            APIEvent ae = (APIEvent) e;

            String des = "asd ";

            OperLogVO log = saveOperLog(ae, des);

            System.out.println("api>>> " + JSONObjectUtil.dumpPretty(log));

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return false;
    }



}
