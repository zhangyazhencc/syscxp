package com.syscxp.alarm.header.log;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.syscxp.alarm.header.BaseVO;
import com.syscxp.alarm.header.resourcePolicy.PolicyVO;
import com.syscxp.alarm.header.resourcePolicy.RegulationVO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-01-15.
 * @Description: .
 */
@Entity
@Table
public class AlarmEventVO extends BaseVO {
    @Column
    private String id;

    @Column
    private String expressionUuid;

    @Column
    private String endpoint;

    @Column
    private AlarmStatus status;

    @Column
    private String leftValue;

    @Column
    private Timestamp eventTime;

    @Column
    private String currentStep;

    @Column
    private String regulationId;

    @Column
    private String resourceUuid;

    private String resourceName;

    private String accountUuid;

    private Map pushedTags;

    private Expression expression;

    private PolicyVO policyVO;

    private RegulationVO regulationVO;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getExpressionUuid() {
        return expressionUuid;
    }

    public void setExpressionUuid(String expressionUuid) {
        this.expressionUuid = expressionUuid;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public AlarmStatus getStatus() {
        return status;
    }

    public void setStatus(AlarmStatus status) {
        this.status = status;
    }

    public String getLeftValue() {
        return leftValue;
    }

    public void setLeftValue(String leftValue) {
        this.leftValue = leftValue;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public Map getPushedTags() {
        return pushedTags;
    }

    public void setPushedTags(Map pushedTags) {
        this.pushedTags = pushedTags;
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

    public PolicyVO getPolicyVO() {
        return policyVO;
    }

    public void setPolicyVO(PolicyVO policyVO) {
        this.policyVO = policyVO;
    }

    public RegulationVO getRegulationVO() {
        return regulationVO;
    }

    public void setRegulationVO(RegulationVO regulationVO) {
        this.regulationVO = regulationVO;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
