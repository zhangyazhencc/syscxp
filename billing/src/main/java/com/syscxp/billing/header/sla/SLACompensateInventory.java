package com.syscxp.billing.header.sla;


import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = SLACompensateVO.class)
public class SLACompensateInventory {

    private String uuid;

    private String accountUuid;

    private String productUuid;

    private ProductType productType;

    private String productName;

    private String reason;

    private String comment;

    private int duration;

    private Timestamp timeStart;

    private Timestamp timeEnd;

    private SLAState state;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private Timestamp applyTime;


    public static SLACompensateInventory valueOf(SLACompensateVO vo) {
        SLACompensateInventory inv = new SLACompensateInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setComment(vo.getComment());
        inv.setDuration(vo.getDuration());
        inv.setProductName(vo.getProductName());
        inv.setProductType(vo.getProductType());
        inv.setProductUuid(vo.getProductUuid());
        inv.setReason(vo.getReason());
        inv.setState(vo.getState());
        inv.setTimeStart(vo.getTimeStart());
        inv.setTimeEnd(vo.getTimeEnd());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setApplyTime(vo.getApplyTime());

        return inv;
    }

    public static List<SLACompensateInventory> valueOf(Collection<SLACompensateVO> vos) {
        List<SLACompensateInventory> lst = new ArrayList<>(vos.size());
        for (SLACompensateVO vo : vos) {
            lst.add(SLACompensateInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Timestamp getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Timestamp timeStart) {
        this.timeStart = timeStart;
    }

    public Timestamp getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Timestamp timeEnd) {
        this.timeEnd = timeEnd;
    }

    public SLAState getState() {
        return state;
    }

    public void setState(SLAState state) {
        this.state = state;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Timestamp applyTime) {
        this.applyTime = applyTime;
    }
}
