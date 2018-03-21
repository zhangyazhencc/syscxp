package com.syscxp.sdk;


import org.testng.annotations.Test;

public class TestBandwidthOfferingAction extends TestSDK {

    @Test
    public void testQuery() {

        QueryBandwidthOfferingAction action = new QueryBandwidthOfferingAction();

        QueryBandwidthOfferingResult result = action.call().throwExceptionIfError().value;

        System.out.println(prettyGson.toJson(result));

    }
}
