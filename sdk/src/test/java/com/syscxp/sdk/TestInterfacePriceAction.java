package com.syscxp.sdk;

import org.testng.annotations.Test;

public class TestInterfacePriceAction extends TestSDK {



    @Test
    public void testGet() {

        GetInterfacePriceAction action = new GetInterfacePriceAction();
        action.duration = 1;
        action.portOfferingUuid = "RJ45_1G";
        action.productChargeModel = ProductChargeModel.BY_MONTH;


        GetInterfacePriceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }
}
