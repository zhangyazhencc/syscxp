package org.zstack.billing.header.identity.order;

import org.zstack.billing.header.identity.balance.ProductChargeModel;
import org.zstack.billing.header.identity.balance.ProductType;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class APICreateOrderMsg extends APIMessage {

    @APIParam(nonempty = true,validValues = {"BUY","UPGRADE","DOWNGRADE","RENEW","SLA_COMPENSATION","UN_SUBCRIBE"})
    private OrderType type;

    @APIParam(nonempty = true)
    private OrderState state;

    @APIParam(nonempty = true)
    private Timestamp productEffectTimeStart;

    @APIParam(nonempty = true)
    private Timestamp productEffectTimeEnd;

    @APIParam(nonempty = true)
    private String productName;

    @APIParam(nonempty = true)
    private ProductType productType;

    @APIParam(nonempty = true,numberRange={0,100})
    private BigDecimal productDiscount;

    @APIParam(nonempty = true)
    private ProductChargeModel productChargeModel;

    @APIParam(nonempty = true)
    private String productDescription;

    @APIParam(nonempty = true)
    private BigDecimal price;

    @APIParam(nonempty = true)
    private BigDecimal originalPrice;

    @APIParam(nonempty = true)
    private String productUuid;

    @APIParam(nonempty = true)
    private int duration;

    @APIParam(nonempty = true)
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

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public Timestamp getProductEffectTimeStart() {
        return productEffectTimeStart;
    }

    public void setProductEffectTimeStart(Timestamp productEffectTimeStart) {
        this.productEffectTimeStart = productEffectTimeStart;
    }

    public Timestamp getProductEffectTimeEnd() {
        return productEffectTimeEnd;
    }

    public void setProductEffectTimeEnd(Timestamp productEffectTimeEnd) {
        this.productEffectTimeEnd = productEffectTimeEnd;
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

    public BigDecimal getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(BigDecimal productDiscount) {
        this.productDiscount = productDiscount;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
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
