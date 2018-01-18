package com.syscxp.alarm.header.log;

import com.alibaba.fastjson.annotation.JSONField;
import com.syscxp.alarm.header.BaseVO;
import com.syscxp.alarm.header.resourcePolicy.PolicyVO;
import com.syscxp.alarm.header.resourcePolicy.RegulationVO;

import javax.persistence.*;
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
    @Enumerated(EnumType.STRING)
    private AlarmStatus status;

    @Column
    private String leftValue;

    @Column
    private Timestamp eventTime;

    @Column
    private String currentStep;

    @Column
    private String regulationUuid;

    @Column
    private String productUuid;

    @Transient
    private String productName;

    @Transient
    private String accountUuid;

    @Transient
    private Map pushedTags;

    @Transient
    private Expression expression;

    @Transient
    private PolicyVO policyVO;

    @Transient
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

    public String getRegulationUuid() {
        return regulationUuid;
    }

    public void setRegulationUuid(String regulationUuid) {
        this.regulationUuid = regulationUuid;
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

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
