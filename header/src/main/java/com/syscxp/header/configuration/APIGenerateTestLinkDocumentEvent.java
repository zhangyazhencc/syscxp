package com.syscxp.header.configuration;

import com.syscxp.header.message.APIEvent;

public class APIGenerateTestLinkDocumentEvent extends APIEvent {
    private String outputDir;

    public APIGenerateTestLinkDocumentEvent() {
        super(null);
    }

    public APIGenerateTestLinkDocumentEvent(String apiId) {
        super(apiId);
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
 
    public static APIGenerateTestLinkDocumentEvent __example__() {
        APIGenerateTestLinkDocumentEvent event = new APIGenerateTestLinkDocumentEvent();


        return event;
    }

}
