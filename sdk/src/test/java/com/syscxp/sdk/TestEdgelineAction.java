package com.syscxp.sdk;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestEdgelineAction extends TestSDK {


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
    @Test
    public void testQuery() {
        QueryEdgeLineAction action = new QueryEdgeLineAction();

        List<String> conditions = new ArrayList<>();
        conditions.add("name=api-test");
        action.conditions = conditions;

        QueryEdgeLineResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test(expectedExceptions = ApiException.class)
    public void testRenew() {

        RenewInterfaceAction action = new RenewInterfaceAction();
        action.uuid = "";
        action.duration = 1;
        action.productChargeModel = ProductChargeModel.BY_MONTH;

        RenewInterfaceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test(expectedExceptions = ApiException.class)
    public void testUnscribe() {

        GetUnscribeEdgeLinePriceDiffAction action = new GetUnscribeEdgeLinePriceDiffAction();
        action.uuid = "";

        GetUnscribeEdgeLinePriceDiffResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }
}
