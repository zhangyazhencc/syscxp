package com.syscxp.header.configuration;

import com.syscxp.header.message.APIMessage;

public class APIGenerateTestLinkDocumentMsg extends APIMessage {
    private String outputDir;

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
 
    public static APIGenerateTestLinkDocumentMsg __example__() {
        APIGenerateTestLinkDocumentMsg msg = new APIGenerateTestLinkDocumentMsg();


        return msg;
    }

}
