package org.zstack.billing.header.identity;

import org.zstack.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = OrderVO.class)
public class OrderInventory {

    private String uuid;

    private OrderType orderType;

    private Timestamp payTime;

    private OrderState orderState;

    private BigDecimal orderPayPresent;

    private BigDecimal orderPayCash;

    private String accountUuid;

    private Timestamp productEffectTimeStart;

    private Timestamp productEffectTimeEnd;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private String productName;

    private ProductType productType;

    private BigDecimal productDiscount;

    private ProductChargeModel productChargeModel;

    private String productDescription;

    public static OrderInventory valueOf(OrderVO vo) {
        OrderInventory inv = new OrderInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setOrderPayCash(vo.getOrderPayCash());
        inv.setOrderPayPresent(vo.getOrderPayPresent());
        inv.setOrderState(vo.getOrderState());
        inv.setOrderType(vo.getOrderType());
        inv.setPayTime(vo.getPayTime());
        inv.setProductChargeModel(vo.getProductChargeModel());
        inv.setProductDescription(vo.getProductDescription());
        inv.setProductDiscount(vo.getProductDiscount());
        inv.setProductEffectTimeEnd(vo.getProductEffectTimeEnd());
        inv.setProductEffectTimeStart(vo.getProductEffectTimeStart());
        inv.setProductName(vo.getProductName());
        inv.setProductType(vo.getProductType());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<OrderInventory> valueOf(Collection<OrderVO> vos) {
        List<OrderInventory> lst = new ArrayList<OrderInventory>(vos.size());
        for (OrderVO vo : vos) {
            lst.add(OrderInventory.valueOf(vo));
        }
        return lst;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public BigDecimal getOrderPayPresent() {
        return orderPayPresent;
    }

    public void setOrderPayPresent(BigDecimal orderPayPresent) {
        this.orderPayPresent = orderPayPresent;
    }

    public BigDecimal getOrderPayCash() {
        return orderPayCash;
    }

    public void setOrderPayCash(BigDecimal orderPayCash) {
        this.orderPayCash = orderPayCash;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
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
}
