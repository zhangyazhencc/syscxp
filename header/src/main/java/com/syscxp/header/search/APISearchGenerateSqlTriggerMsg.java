package com.syscxp.header.search;

import com.syscxp.header.message.APIMessage;

public class APISearchGenerateSqlTriggerMsg extends APIMessage {
    private String resultPath;

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }
 
    public static APISearchGenerateSqlTriggerMsg __example__() {
        APISearchGenerateSqlTriggerMsg msg = new APISearchGenerateSqlTriggerMsg();


        return msg;
    }

}
