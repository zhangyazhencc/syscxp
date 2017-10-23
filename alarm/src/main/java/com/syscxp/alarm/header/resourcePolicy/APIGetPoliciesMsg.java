package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.QueryCondition;

import java.util.List;

@Action(adminOnly = true,category = AlarmConstant.ACTION_CATEGORY_RESOURCE_POLICY)
public class APIGetPoliciesMsg extends APISyncCallMessage{

    @APIParam
    private List<QueryCondition> conditions;

    @APIParam
    private Integer limit = 1000;

    @APIParam
    private Integer start;


    public List<QueryCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<QueryCondition> conditions) {
        this.conditions = conditions;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

}
