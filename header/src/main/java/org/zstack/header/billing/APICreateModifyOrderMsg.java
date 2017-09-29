package org.zstack.header.billing;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.InnerCredentialCheck;
import org.zstack.header.message.APIParam;

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
