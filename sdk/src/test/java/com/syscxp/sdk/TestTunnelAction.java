package com.syscxp.sdk;


import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestTunnelAction extends TestSDK {

    private String tunnelUuid = "14f0aeb31dcb4526add6bea4ba724ac2";
    // 昆山
    private String endpointAUuid = "bdaa7f043dcd48bf94f0e8ebbc185438";
    // 法国
    private String endpointZUuid = "139dd42654a34c169fc06ee8c0f8dd53";

    @Test(expectedExceptions = {ApiException.class})
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
        List<String> conditions = new ArrayList<>();
        action.conditions = conditions;
        action.uuid = "14f0aeb31dcb4526add6bea4ba724ac2";
        action.start = 0;
        action.limit = 2;
        action.replyWithCount = true;

        QueryTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testListCrossTunnel() {
        ListCrossTunnelAction action = new ListCrossTunnelAction();

        action.uuid = "17821444f3a241b18068bb515343c11c";

        ListCrossTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testListSwitchPortByType() {
        ListSwitchPortByTypeAction action = new ListSwitchPortByTypeAction();

        action.accountUuid = accountUuid;

        action.uuid = endpointAUuid;
//        action.uuid = endpointZUuid;
        action.type = "RJ45_1G";
        action.start = 0;
        action.limit = 20;


        ListSwitchPortByTypeResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testListInnerEndpoint() {
        ListInnerEndpointAction action = new ListInnerEndpointAction();

        action.endpointAUuid = endpointAUuid;

        action.endpointZUuid = endpointZUuid;


        ListInnerEndpointResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testGetVlan() {
        GetVlanAutoAction action = new GetVlanAutoAction();
        // 上海 深圳
        action.interfaceUuidA = "580927098d83487dacc627937d3da223";
        action.interfaceUuidZ = "788feb67227e4b879aed612a7ac33e60";

//        action.innerConnectedEndpointUuid = "";

        GetVlanAutoResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testGetVsi() {
        GetTunnelVsiAutoAction action = new GetTunnelVsiAutoAction();

        GetTunnelVsiAutoResult result = action.call().throwExceptionIfError().value;

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
        action.description = "api-test-update";

        UpdateTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testUpdateTunnelBandwidth() {
        UpdateTunnelBandwidthAction action = new UpdateTunnelBandwidthAction();

        action.uuid = tunnelUuid;
        action.bandwidthOfferingUuid = "20M";

        UpdateTunnelBandwidthResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testEnableTunnel() {
        EnableTunnelAction action = new EnableTunnelAction();
        action.uuid = tunnelUuid;

        EnableTunnelResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testDisableTunnel() {
        DisableTunnelAction action = new DisableTunnelAction();
        action.uuid = tunnelUuid;

        DisableTunnelResult result = action.call().throwExceptionIfError().value;

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
