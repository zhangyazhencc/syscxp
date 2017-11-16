package com.syscxp.header.billing;

import com.syscxp.header.search.Inventory;
import com.syscxp.utils.gson.JSONObjectUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = OrderVO.class)
public class OrderInventory {

    private String uuid;

    private OrderType type;

    private Timestamp payTime;

    private OrderState state;

    private BigDecimal payPresent;

    private BigDecimal payCash;

    private String accountUuid;

    private Timestamp productEffectTimeStart;

    private Timestamp productEffectTimeEnd;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private String productName;

    private ProductType productType;

    private ProductChargeModel productChargeModel;

    private DescriptionData descriptionData;
    private CallBackData callBackData;

    private String productUuid;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private int duration;

    private int productStatus;

    public static OrderInventory valueOf(OrderVO vo) {
        OrderInventory inv = new OrderInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setPayCash(vo.getPayCash());
        inv.setPayPresent(vo.getPayPresent());
        inv.setState(vo.getState());
        inv.setType(vo.getType());
        inv.setPayTime(vo.getPayTime());
        inv.setProductChargeModel(vo.getProductChargeModel());
        inv.setDescriptionData(JSONObjectUtil.toObject(vo.getDescriptionData(),DescriptionData.class));
        inv.setProductEffectTimeEnd(vo.getProductEffectTimeEnd());
        inv.setProductEffectTimeStart(vo.getProductEffectTimeStart());
        inv.setProductName(vo.getProductName());
        inv.setProductType(vo.getProductType());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setPrice(vo.getPrice());
        inv.setOriginalPrice(vo.getOriginalPrice());
        inv.setProductUuid(vo.getProductUuid());
        inv.setDuration(vo.getDuration());
        inv.setProductStatus(vo.getProductStatus());
        inv.setCallBackData(JSONObjectUtil.toObject(vo.getCallBackData(),CallBackData.class));
        return inv;
    }

    public static List<OrderInventory> valueOf(Collection<OrderVO> vos) {
        List<OrderInventory> lst = new ArrayList<OrderInventory>(vos.size());
        for (OrderVO vo : vos) {
            lst.add(OrderInventory.valueOf(vo));
        }
        return lst;
    }

    public int getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(int productStatus) {
        this.productStatus = productStatus;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public BigDecimal getPayPresent() {
        return payPresent;
    }

    public void setPayPresent(BigDecimal payPresent) {
        this.payPresent = payPresent;
    }

    public BigDecimal getPayCash() {
        return payCash;
    }

    public void setPayCash(BigDecimal payCash) {
        this.payCash = payCash;
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

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public DescriptionData getDescriptionData() {
        return descriptionData;
    }

    public void setDescriptionData(DescriptionData descriptionData) {
        this.descriptionData = descriptionData;
    }

    public CallBackData getCallBackData() {
        return callBackData;
    }

    public void setCallBackData(CallBackData callBackData) {
        this.callBackData = callBackData;
    }
}
