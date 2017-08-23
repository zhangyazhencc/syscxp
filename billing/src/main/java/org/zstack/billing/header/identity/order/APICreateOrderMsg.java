package org.zstack.billing.header.identity.order;

import org.zstack.billing.header.identity.balance.ProductChargeModel;
import org.zstack.billing.header.identity.balance.ProductType;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class APICreateOrderMsg extends APIMessage {

    @APIParam(emptyString = true,validValues = {"BUY","UPGRADE","DOWNGRADE","RENEW","SLA_COMPENSATION","UN_SUBCRIBE"})
    private OrderType type;

    @APIParam(emptyString = true)
    private String productName;

    @APIParam(emptyString = true)
    private ProductType productType;

    @APIParam(emptyString = true)
    private ProductChargeModel productChargeModel;

    @APIParam
    private String productDescription;

    @APIParam(emptyString = true)
    private String productUuid;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private int duration;

    @APIParam(emptyString = true)
    private String priceUnitUuid;

    @APIParam(required = false, resourceType = OrderVO.class, checkAccount = true)
    private String oldOrderUuid;

    public String getOldOrderUuid() {
        return oldOrderUuid;
    }

    public void setOldOrderUuid(String oldOrderUuid) {
        this.oldOrderUuid = oldOrderUuid;
    }

    public String getPriceUnitUuid() {
        return priceUnitUuid;
    }

    public void setPriceUnitUuid(String priceUnitUuid) {
        this.priceUnitUuid = priceUnitUuid;
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
}
