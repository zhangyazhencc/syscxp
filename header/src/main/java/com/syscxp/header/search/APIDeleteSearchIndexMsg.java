package com.syscxp.header.search;

import com.syscxp.header.message.APIMessage;

public class APIDeleteSearchIndexMsg extends APIMessage {
    private String indexName;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
 
    public static APIDeleteSearchIndexMsg __example__() {
        APIDeleteSearchIndexMsg msg = new APIDeleteSearchIndexMsg();


        return msg;
    }

}
