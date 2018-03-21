package com.syscxp.sdk;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestEdgelineAction extends TestSDK {

    private String interfaceUuid = "b803575118be4b60a5a415ddf2189ea5";

    @Test
    public void testCreate() {
        CreateEdgeLineAction action = new CreateEdgeLineAction();
        action.interfaceUuid = "";
        action.type = "";
        action.destinationInfo = "";
        action.description = "api-test";

        CreateEdgeLineResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test(expectedExceptions = ApiException.class)
    public void testRenew() {

        RenewInterfaceAction action = new RenewInterfaceAction();
        action.uuid = interfaceUuid;
        action.duration = 1;
        action.productChargeModel = ProductChargeModel.BY_MONTH;

        RenewInterfaceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }
}
