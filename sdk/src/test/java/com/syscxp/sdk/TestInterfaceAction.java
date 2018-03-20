package com.syscxp.sdk;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestInterfaceAction extends TestSDK {

    private String interfaceUuid;

    @Test
    public void testQuery() {

        QueryInterfaceAction action = new QueryInterfaceAction();
        List<String> conditions = new ArrayList<>();
        conditions.add("uuid=cb0f4a9a8ed841b1bc3f5a3651b24b09");
        action.conditions = conditions;

        QueryInterfaceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }
    @Test
    public void testQueryPortOffering() {

        QueryPortOfferingAction action = new QueryPortOfferingAction();

        QueryPortOfferingResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }
    @Test
    public void testGetInterfaceType() {

        GetInterfaceTypeAction action = new GetInterfaceTypeAction();
        // Endpoint
        action.uuid = "";

        GetInterfaceTypeResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));
    }


    @Test
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
        interfaceUuid = result.inventory.uuid;

    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdate() {

        UpdateInterfaceAction action = new UpdateInterfaceAction();
        action.uuid = interfaceUuid;
        action.name = "api-test-update";

        UpdateInterfaceResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }

    @Test(expectedExceptions = ApiException.class)
    public void testDelete() {

        DeleteInterfaceAction action = new DeleteInterfaceAction();
        action.uuid = interfaceUuid;

        DeleteInterfaceResult result = action.call().throwExceptionIfError().value;

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
