package com.syscxp.sdk;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestTunnelPriceAction extends TestSDK {

    @DataProvider(name = "TunnelPrice")
    public Object[][] user() {
        return new Object[][]{
                // 深圳   上海
                {"0970ba99b5ce4b8786e3f1e4c2875af3", "ba14f17d40b34a2788b4bcf1843decdd", null, null, null},
                {"0970ba99b5ce4b8786e3f1e4c2875af3", "ba14f17d40b34a2788b4bcf1843decdd", "e8a7e7337dbb435b81c39f877a3c5755", null, null},
                // 深圳   昆山
                {"0970ba99b5ce4b8786e3f1e4c2875af3", "bdaa7f043dcd48bf94f0e8ebbc185438", "e8a7e7337dbb435b81c39f877a3c5755", "17821444f3a241b18068bb515343c11c", null},
                // 深圳   法国
                {"0970ba99b5ce4b8786e3f1e4c2875af3", "139dd42654a34c169fc06ee8c0f8dd53", "e8a7e7337dbb435b81c39f877a3c5755", "e45e18f51879425f90838e77763de5a3", null},
                {"0970ba99b5ce4b8786e3f1e4c2875af3", "139dd42654a34c169fc06ee8c0f8dd53", "e8a7e7337dbb435b81c39f877a3c5755", "e45e18f51879425f90838e77763de5a3", "ff179da85aaa45509057edfcb8df6d7d"},
        };
    }

    @Test(dataProvider = "TunnelPrice")
    public void testGetTunnelPriceAction(String endpointAUuid, String endpointZUuid,
                                            String interfaceAUuid, String interfaceZUuid, String innerEndpointUuid) {

        GetTunnelPriceAction action = new GetTunnelPriceAction();

        action.endpointAUuid = endpointAUuid;
        action.endpointZUuid = endpointZUuid;

        action.interfaceAUuid = interfaceAUuid;
        action.interfaceZUuid = interfaceZUuid;

        action.innerEndpointUuid = innerEndpointUuid;

        action.bandwidthOfferingUuid = "10M";
        action.duration = 1;
        action.productChargeModel = ProductChargeModel.BY_MONTH;

        GetTunnelPriceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    private String tunnelUuid = "e687b1f11fbd481a929aa968139e4c82";

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
        action.bandwidthOfferingUuid = "20M";

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
