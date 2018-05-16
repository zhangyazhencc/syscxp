package com.syscxp.header.billing.productimpl;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.vpn.vpn.VpnVO;

import java.sql.Timestamp;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-04-26.
 * @Description: .
 */

@InnerCredentialCheck
public class APIUpdateExpireDateMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,resourceType = VpnVO.class)
    private String uuid;

    @APIParam
    private Timestamp expireDate;

    private ProductType productType;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
