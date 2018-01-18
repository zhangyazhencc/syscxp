package com.syscxp.alarm.header.log;

import java.util.Map;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-01-15.
 * @Description: .
 */
public class Expression {
    private String id;
    private String metric;
    private Map tags;
    private String func;
    private String operator;
    private String rightValue;
    private String maxStep;
    private String priority;
    private String note;
    private String actionId;
    private String regulationId;
    private String resourceUuid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Map getTags() {
        return tags;
    }

    public void setTags(Map tags) {
        this.tags = tags;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRightValue() {
        return rightValue;
    }

    public void setRightValue(String rightValue) {
        this.rightValue = rightValue;
    }

    public String getMaxStep() {
        return maxStep;
    }

    public void setMaxStep(String maxStep) {
        this.maxStep = maxStep;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getRegulationId() {
        return regulationId;
    }

    public void setRegulationId(String regulationId) {
        this.regulationId = regulationId;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }
}
