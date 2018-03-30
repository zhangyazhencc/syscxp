package com.syscxp.sdk;

import org.testng.annotations.Test;

public class TestMonitorAction extends TestSDK {


    @Test
    public void testQueryNettoolMonitorHost() {
        QueryNettoolMonitorHostAction action = new QueryNettoolMonitorHostAction();

        QueryNettoolMonitorHostResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testQuerySpeedRecords() {
        QuerySpeedRecordsAction action = new QuerySpeedRecordsAction();

        action.start = 0;
        action.limit = 10;
        QuerySpeedRecordsResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testStartTunnelMonitor() {
        StartTunnelMonitorAction action = new StartTunnelMonitorAction();

        action.tunnelUuid = "9ea7c950ba2f4bcd9a0c02c8a183ec6d";
        action.monitorCidr = "";
        action.msg = "";

        StartTunnelMonitorResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }
    @Test
    public void testStopTunnelMonitor() {
        StopTunnelMonitorAction action = new StopTunnelMonitorAction();

        action.tunnelUuid = "9ea7c950ba2f4bcd9a0c02c8a183ec6d";

        StopTunnelMonitorResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testRestartTunnelMonitor() {
        RestartTunnelMonitorAction action = new RestartTunnelMonitorAction();

        action.tunnelUuid = "9ea7c950ba2f4bcd9a0c02c8a183ec6d";
        action.monitorCidr = "";
        RestartTunnelMonitorResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }
}
