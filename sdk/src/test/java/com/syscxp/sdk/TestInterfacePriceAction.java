package com.syscxp.sdk;

import org.testng.annotations.Test;

public class TestInterfacePriceAction extends TestSDK {

    private String interfaceUuid= "01de0b10efe44099827076c20f011762";

    @Test
    public void testGetInterfacePriceAction() {

        GetInterfacePriceAction action = new GetInterfacePriceAction();
        action.duration = 1;
        action.portOfferingUuid = "RJ45_1G";
        action.productChargeModel = ProductChargeModel.BY_MONTH;

        GetInterfacePriceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testGetRenewInterfacePriceAction() {

        GetRenewInterfacePriceAction action = new GetRenewInterfacePriceAction();
        action.uuid = interfaceUuid;
        action.duration = 1;
        action.productChargeModel = ProductChargeModel.BY_MONTH;

        GetRenewInterfacePriceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    @Test
    public void testGetUnscribeInterfacePriceDiffAction() {

        GetUnscribeInterfacePriceDiffAction action = new GetUnscribeInterfacePriceDiffAction();
        action.uuid = interfaceUuid;

        GetUnscribeInterfacePriceDiffResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }

    
    
}
