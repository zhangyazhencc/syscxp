package com.syscxp.header.billing;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;

import java.sql.Timestamp;
import java.util.List;

@Action(category = BillingConstant.ACTION_CATEGORY_ORDER)
@InnerCredentialCheck
public class APICreateModifyOrderMsg extends APICreateOrderMsg {
    @APIParam(emptyString = false)
    private String productName;

    @APIParam(nonempty = true)
    private List<ProductPriceUnit> units;

    @APIParam(emptyString = false)
    private ProductType productType;

    @APIParam(emptyString = false)
    private String productUuid;

    @APIParam
    private String productDescription;

    @APIParam
    private Timestamp startTime;
    @APIParam
    private Timestamp expiredTime;

    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Timestamp expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public List<ProductPriceUnit> getUnits() {
        return units;
    }

    public void setUnits(List<ProductPriceUnit> units) {
        this.units = units;
    }
}
