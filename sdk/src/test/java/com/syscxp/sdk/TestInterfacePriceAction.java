package com.syscxp.sdk;

import org.testng.annotations.Test;

public class TestInterfacePriceAction extends TestSDK {

    private String interfaceUuid= "11f521d844b14daa819864f7ea095cc5";

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
