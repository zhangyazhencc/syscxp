package com.syscxp.sdk;


import org.testng.annotations.Test;

public class TestTunnelAction extends TestSDK {

    private String tunnelUuid;

    @Test
    public void testGet() {

    }

    @Test
    public void testCreate() {
        CreateTunnelAction action = new CreateTunnelAction();

        action.name = "apt-test";
        action.bandwidthOfferingUuid = "";
        action.endpointAUuid = "";
        action.endpointZUuid = "";

        action.interfaceAUuid = "";
        action.interfaceZUuid = "";

        action.duration = 1;
        action.productChargeModel = ProductChargeModel.BY_MONTH;
        action.description = "";

        action.innerConnectedEndpointUuid = "";
        action.crossInterfaceUuid = "";
        action.crossTunnelUuid = "";

        CreateTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

        tunnelUuid = result.inventory.uuid;
    }

    @Test
    public void testQuery() {
        QueryTunnelAction action = new QueryTunnelAction();

        QueryTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testListCrossTunnel() {
        ListCrossTunnelAction action = new ListCrossTunnelAction();

        action.uuid = tunnelUuid;


        ListCrossTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testListSwitchPortByType() {
        ListSwitchPortByTypeAction action = new ListSwitchPortByTypeAction();

        action.uuid = "";
        action.type = "";
        action.start = 0;
        action.limit = 20;


        ListSwitchPortByTypeResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testListInnerEndpoint() {
        ListInnerEndpointAction action = new ListInnerEndpointAction();

        action.endpointAUuid = "";
        action.endpointZUuid = "";


        ListInnerEndpointResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testGetVlan() {
        GetVlanAutoAction action = new GetVlanAutoAction();

        action.interfaceUuidA = "apt-monitor";
        action.interfaceUuidZ = "";
        action.innerConnectedEndpointUuid = "";

        GetVlanAutoResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testQueryBandwidthOffering() {
        QueryBandwidthOfferingAction action = new QueryBandwidthOfferingAction();

        QueryBandwidthOfferingResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testUpdate() {
        UpdateTunnelAction action = new UpdateTunnelAction();

        action.uuid = tunnelUuid;
        action.name = "";
        action.description = "";

        UpdateTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testUpdateTunnelBandwidth() {
        UpdateTunnelBandwidthAction action = new UpdateTunnelBandwidthAction();

        action.uuid = tunnelUuid;
        action.bandwidthOfferingUuid = "";

        UpdateTunnelBandwidthResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testEnable() {
        EnableTunnelAction action = new EnableTunnelAction();
        action.uuid = tunnelUuid;

        action.saveOnly = false;

        EnableTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testRenew() {

        RenewTunnelAction action = new RenewTunnelAction();
        action.uuid = tunnelUuid;
        action.duration = 1;
        action.productChargeModel = ProductChargeModel.BY_MONTH;

        RenewTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testDelete() {
        DeleteTunnelAction action = new DeleteTunnelAction();
        action.uuid = tunnelUuid;

        DeleteTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }
}
