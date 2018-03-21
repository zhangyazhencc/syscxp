package com.syscxp.sdk;

import org.testng.annotations.Test;

public class TestMonitorAction extends TestSDK {
    @Test
    public void testCreate() {
        CreateHostSwitchMonitorAction action = new CreateHostSwitchMonitorAction();

        action.hostUuid = "apt-monitor";
        action.physicalSwitchUuid = "";
        action.physicalSwitchPortName = "";
        action.interfaceName = "";

        CreateHostSwitchMonitorResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testQueryNettoolMonitorHost() {
        QueryNettoolMonitorHostAction action = new QueryNettoolMonitorHostAction();

        QueryNettoolMonitorHostResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testQuerySpeedRecords() {
        QuerySpeedRecordsAction action = new QuerySpeedRecordsAction();


        QuerySpeedRecordsResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testStartTunnelMonitor() {
        StartTunnelMonitorAction action = new StartTunnelMonitorAction();

        action.tunnelUuid = "";
        action.monitorCidr = "";
        action.msg = "";

        StartTunnelMonitorResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }
    @Test
    public void testStopTunnelMonitor() {
        StopTunnelMonitorAction action = new StopTunnelMonitorAction();

        action.tunnelUuid = "";

        StopTunnelMonitorResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testRestartTunnelMonitor() {
        RestartTunnelMonitorAction action = new RestartTunnelMonitorAction();

        action.tunnelUuid = "";
        action.monitorCidr = "";
        RestartTunnelMonitorResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }
}
