package org.zstack.billing.header.order;

import org.zstack.billing.header.balance.ProductChargeModel;
import org.zstack.billing.header.balance.ProductType;
import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"order"}, accountOnly = true)
public class APICreateOrderMsg extends APIMessage {

    @APIParam(emptyString = false)
    private OrderType type;

    @APIParam(emptyString = false)
    private String productName;

    @APIParam(emptyString = false)
    private ProductType productType;

    @APIParam(emptyString = false)
    private ProductChargeModel productChargeModel;

    @APIParam
    private String productDescription;

    @APIParam(required = false)
    private String productUuid;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private int duration;

    @APIParam(emptyString = false)
    private  ProductPriceUnit productPriceUnit;

    @APIParam(required = false, resourceType = OrderVO.class, checkAccount = true)
    private String oldOrderUuid;

    public String getOldOrderUuid() {
        return oldOrderUuid;
    }

    public void setOldOrderUuid(String oldOrderUuid) {
        this.oldOrderUuid = oldOrderUuid;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
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

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ProductPriceUnit getProductPriceUnit() {
        return productPriceUnit;
    }

    public void setProductPriceUnit(ProductPriceUnit productPriceUnit) {
        this.productPriceUnit = productPriceUnit;
    }
}
