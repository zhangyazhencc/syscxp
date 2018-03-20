package com.syscxp.billing.header.price;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.ProductCategory;
import com.syscxp.header.billing.ProductPriceUnitVO;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_PRICE, adminOnly = true)
public class APIDeleteVPNPriceMsg extends APIMessage {

    @APIParam(maxLength = 256,emptyString = false)
    private ProductType productType;

    @APIParam(maxLength = 256,emptyString = false)
    private ProductCategory category;

    @APIParam( maxLength = 256,emptyString = false)
    private String areaCode;

    @APIParam( maxLength = 256,emptyString = false)
    private String lineCode;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("DeleteVPNPrice")
                        .resource(null, ProductPriceUnitVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
