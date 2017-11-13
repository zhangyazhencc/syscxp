package com.syscxp.core;

/**
 */
@GlobalPropertyDefinition
public class CoreGlobalProperty {
    @GlobalProperty(name = "unitTestOn", defaultValue = "false")
    public static boolean UNIT_TEST_ON;
    @GlobalProperty(name = "beanRefContextConf", defaultValue = "beanRefContext.xml")
    public static String BEAN_REF_CONTEXT_CONF;
    @GlobalProperty(name = "vmTracerOn", defaultValue = "true")
    public static boolean VM_TRACER_ON;
    @GlobalProperty(name = "profiler.workflow", defaultValue = "false")
    public static boolean PROFILER_WORKFLOW;
    @GlobalProperty(name = "profiler.httpCall", defaultValue = "false")
    public static boolean PROFILER_HTTP_CALL;
    @GlobalProperty(name = "exitJVMOnBootFailure", defaultValue = "true")
    public static boolean EXIT_JVM_ON_BOOT_FAILURE;
    @GlobalProperty(name = "checkBoxTypeInInventory", defaultValue = "false")
    public static boolean CHECK_BOX_TYPE_IN_INVENTORY;
    @GlobalProperty(name = "pidFilePath", defaultValue = "{user.home}/management-server.pid")
    public static String PID_FILE_PATH;
    @GlobalProperty(name = "consoleProxyOverriddenIp", defaultValue = "0.0.0.0")
    public static String CONSOLE_PROXY_OVERRIDDEN_IP;
    @GlobalProperty(name = "exposeSimulatorType", defaultValue = "false")
    public static boolean EXPOSE_SIMULATOR_TYPE;
    @GlobalProperty(name = "exitJVMOnStop", defaultValue = "true")
    public static boolean EXIT_JVM_ON_STOP;
    @GlobalProperty(name = "locale", defaultValue = "zh_CN")
    public static String LOCALE;
    @GlobalProperty(name = "user.home")
    public static String USER_HOME;
    @GlobalProperty(name = "RESTFacade.readTimeout", defaultValue = "300000")
    public static int REST_FACADE_READ_TIMEOUT;
    @GlobalProperty(name = "RESTFacade.connectTimeout", defaultValue = "15000")
    public static int REST_FACADE_CONNECT_TIMEOUT;
    @GlobalProperty(name = "upgradeStartOn", defaultValue = "false")
    public static boolean IS_UPGRADE_START;
    @GlobalProperty(name = "shadowEntityOn", defaultValue = "false")
    public static boolean SHADOW_ENTITY_ON;
    @GlobalProperty(name = "consoleProxyPort", defaultValue = "4900")
    public static int CONSOLE_PROXY_PORT;


    @GlobalProperty(name = "cleanExpiredProductInterval", defaultValue = "1")
    public static int CLEAN_EXPIRED_PRODUCT_INTERVAL;

    @GlobalProperty(name = "innerMessageMD5Key", defaultValue = "asfdsghajsdgkasg")
    public static String INNER_MESSAGE_MD5_KEY;
    @GlobalProperty(name = "innerMessageExpire", defaultValue = "600")
    public static long INNER_MESSAGE_EXPIRE;

    public static int SESSION_CLEANUP_INTERVAL = 3600;

    @GlobalProperty(name = "billingServerUrl", defaultValue = "http://192.168.211.99:8082")
    public static String BILLING_SERVER_URL;

    @GlobalProperty(name = "interfaceMaxMotifies", defaultValue = "5")
    public static Integer INTERFACE_MAX_MOTIFIES;

    @GlobalProperty(name = "tunnelMaxMotifies", defaultValue = "5")
    public static Integer TUNNEL_MAX_MOTIFIES;

    @GlobalProperty(name = "expiredProductCloseTime", defaultValue = "3")
    public static Integer EXPIRED_PRODUCT_CLOSE_TIME;

    @GlobalProperty(name = "expiredProductDeleteTime", defaultValue = "10")
    public static Integer EXPIRED_PRODUCT_DELETE_TIME;

    @GlobalProperty(name = "controllerManagerUrl", defaultValue = "http://192.168.211.224:8080")
    public static String CONTROLLER_MANAGER_URL;

    @GlobalProperty(name = "startVsi", defaultValue = "20000")
    public static Integer START_VSI;

    @GlobalProperty(name = "falconApiIp", defaultValue = "192.168.211.96")
    public static String FALCON_API_IP;

    @GlobalProperty(name = "falconApiPort", defaultValue = "6892")
    public static int FALCON_API_PORT;
}