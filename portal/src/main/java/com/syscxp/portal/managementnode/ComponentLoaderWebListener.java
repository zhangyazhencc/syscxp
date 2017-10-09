package com.syscxp.portal.managementnode;

import org.springframework.web.context.support.WebApplicationContextUtils;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.utils.BootErrorLog;
import com.syscxp.utils.ExceptionDSL;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static com.syscxp.utils.ExceptionDSL.throwableSafe;

public class ComponentLoaderWebListener implements ServletContextListener {
    private static final CLogger logger = Utils.getLogger(ComponentLoaderWebListener.class);
    private static boolean isInit = false;
    private ManagementNodeManager node;
    private CloudBus bus;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        logger.warn("web listener issued context destroy event, start stopping process");
        if (isInit) {
            throwableSafe(new Runnable() {
                @Override
                public void run() {
                    node.stop();
                }
            });
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            if (!isInit) {
                Platform.createComponentLoaderFromWebApplicationContext(WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()));
                node = Platform.getComponentLoader().getComponent(ManagementNodeManager.class);
                bus = Platform.getComponentLoader().getComponent(CloudBus.class);
                node.startNode();
                isInit = true;
            }
        } catch (Throwable t) {
            logger.warn("failed to start management server", t);
            // have to call bus.stop() because its init has been called by spring
            if (bus != null) {
                bus.stop();
            }

            Throwable root = ExceptionDSL.getRootThrowable(t);
            new BootErrorLog().write(root.getMessage());
            if (CoreGlobalProperty.EXIT_JVM_ON_BOOT_FAILURE) {
                System.exit(1);
            } else {
                throw new CloudRuntimeException(t);
            }
        }
    }
}
