package com.syscxp.sdk;

import org.testng.annotations.Test;

public class TestTunnelPriceAction extends TestSDK {

    private String tunnelUuid = "e687b1f11fbd481a929aa968139e4c82";

    @Test
    public void testGetInterfacePriceAction() {

        GetTunnelPriceAction action = new GetTunnelPriceAction();

        action.endpointAUuid = "";
        action.endpointZUuid = "";

        action.interfaceAUuid = "";
        action.interfaceZUuid = "";
        action.innerEndpointUuid = "";

        action.bandwidthOfferingUuid = "10M";
        action.duration = 1;
        action.productChargeModel = ProductChargeModel.BY_MONTH;

        GetTunnelPriceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testGetModifyBandwidthNumAction() {

        GetModifyBandwidthNumAction action = new GetModifyBandwidthNumAction();
        action.uuid = tunnelUuid;

        GetModifyBandwidthNumResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }


    @Test
    public void testGetModifyTunnelPriceDiffAction() {

        GetModifyTunnelPriceDiffAction action = new GetModifyTunnelPriceDiffAction();

        action.uuid = tunnelUuid;
        action.bandwidthOfferingUuid = "";

        GetModifyTunnelPriceDiffResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testGetRenewTunnelPriceAction() {

        GetRenewTunnelPriceAction action = new GetRenewTunnelPriceAction();

        action.uuid = tunnelUuid;
        action.duration = 1;
        action.productChargeModel = ProductChargeModel.BY_MONTH;

        GetRenewTunnelPriceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testGetUnscribeTunnelPriceDiffAction() {

        GetUnscribeTunnelPriceDiffAction action = new GetUnscribeTunnelPriceDiffAction();

        action.uuid = tunnelUuid;

        GetUnscribeTunnelPriceDiffResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

}
