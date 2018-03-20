package com.syscxp.sdk;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestInterfaceAction extends TestSDK {

    @Test
    public void testQuery() {

        QueryInterfaceAction action = new QueryInterfaceAction();
        List<String> conditions = new ArrayList<>();
        conditions.add("uuid=cb0f4a9a8ed841b1bc3f5a3651b24b09");
        action.conditions = conditions;

        QueryInterfaceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }


    @Test(expectedExceptions = ApiException.class)
    public void testCreate() {
        CreateInterfaceAction action = new CreateInterfaceAction();
        action.endpointUuid = "e3bf7d8d049e47cdb28203217a3ee16f";
        action.name = "api-test";
        action.description = "api-test";
        action.duration = 1;
        action.portOfferingUuid = "RJ45_1G";
        action.productChargeModel = ProductChargeModel.BY_MONTH;

        CreateInterfaceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdate() {

        UpdateInterfaceAction action = new UpdateInterfaceAction();
        action.uuid = "cb0f4a9a8ed841b1bc3f5a3651b24b09";
        action.name = "api-test-update";

        UpdateInterfaceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test(expectedExceptions = ApiException.class)
    public void testDelete() {

        DeleteInterfaceAction action = new DeleteInterfaceAction();
        action.uuid = "cb0f4a9a8ed841b1bc3f5a3651b24b09";

        DeleteInterfaceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }
}
