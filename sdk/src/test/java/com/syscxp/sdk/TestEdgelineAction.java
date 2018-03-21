package com.syscxp.sdk;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestEdgelineAction extends TestSDK {

    @Test(expectedExceptions = {ApiException.class})
    public void testCreate() {
        CreateEdgeLineAction action = new CreateEdgeLineAction();
        action.interfaceUuid = "";
        action.type = "";
        action.destinationInfo = "";
        action.description = "api-test";

        CreateEdgeLineResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    private String edgeLineUuid = "76a4678d83a54056b2e8af134cd2cdfc";
    @Test
    public void testQuery() {
        QueryEdgeLineAction action = new QueryEdgeLineAction();

        List<String> conditions = new ArrayList<>();
        conditions.add("uuid=" + edgeLineUuid);
        action.conditions = conditions;

        QueryEdgeLineResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testRenew() {

        RenewEdgeLineAction action = new RenewEdgeLineAction();
        action.uuid = edgeLineUuid;
        action.duration = 1;
        action.productChargeModel = ProductChargeModel.BY_MONTH;

        RenewEdgeLineResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test
    public void testUnscribe() {

        GetUnscribeEdgeLinePriceDiffAction action = new GetUnscribeEdgeLinePriceDiffAction();
        action.uuid = edgeLineUuid;

        GetUnscribeEdgeLinePriceDiffResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }
}
