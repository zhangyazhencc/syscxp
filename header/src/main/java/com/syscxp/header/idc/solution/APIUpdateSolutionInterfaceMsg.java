package com.syscxp.header.idc.solution;

import com.syscxp.header.idc.IdcConstant;
import com.syscxp.header.idc.SolutionConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.math.BigDecimal;

/**
 * Create by DCY on 2018/4/12
 */
@Action(services = {IdcConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "update")
public class APIUpdateSolutionInterfaceMsg extends APIMessage {

    @APIParam(maxLength = 32, emptyString = false, resourceType = SolutionInterfaceVO.class)
    private String uuid;

    @APIParam
    private BigDecimal cost;

    @APIParam
    private BigDecimal discount;

    @APIParam
    private BigDecimal shareDiscount;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getShareDiscount() {
        return shareDiscount;
    }

    public void setShareDiscount(BigDecimal shareDiscount) {
        this.shareDiscount = shareDiscount;
    }
}
