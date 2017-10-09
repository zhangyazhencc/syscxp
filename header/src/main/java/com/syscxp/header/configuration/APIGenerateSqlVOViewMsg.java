package com.syscxp.header.configuration;

import com.syscxp.header.message.APIMessage;

import java.util.Arrays;
import java.util.List;

/**
 */
public class APIGenerateSqlVOViewMsg extends APIMessage {
    private List<String> basePackageNames;

    public List<String> getBasePackageNames() {
        if (basePackageNames == null) {
            return Arrays.asList("com.syscxp");
        }
        return basePackageNames;
    }

    public void setBasePackageNames(List<String> basePackageNames) {
        this.basePackageNames = basePackageNames;
    }
 
    public static APIGenerateSqlVOViewMsg __example__() {
        APIGenerateSqlVOViewMsg msg = new APIGenerateSqlVOViewMsg();


        return msg;
    }

}
