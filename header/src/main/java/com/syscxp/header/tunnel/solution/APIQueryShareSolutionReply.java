package com.syscxp.header.tunnel.solution;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryShareSolutionReply extends APIQueryReply {
    private List<ShareSolutionInventory> shareSolutionInventories;

    public List<ShareSolutionInventory> getShareSolutionInventories() {
        return shareSolutionInventories;
    }

    public void setShareSolutionInventories(List<ShareSolutionInventory> shareSolutionInventories) {
        this.shareSolutionInventories = shareSolutionInventories;
    }
}
