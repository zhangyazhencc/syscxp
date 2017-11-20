package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = SolutionConstant.ACTION_CATEGORY, names = "create")
public class APICreateSolutionMsg extends  APIMessage {

    @APIParam(maxLength = 32)
    private String name;
    @APIParam(maxLength = 128,required = false)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
