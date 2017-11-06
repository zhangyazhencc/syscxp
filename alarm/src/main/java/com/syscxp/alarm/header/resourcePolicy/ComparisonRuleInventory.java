package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ComparisonRuleVO.class)
public class ComparisonRuleInventory {
    private String uuid;
    private String comparisonName;
    private String comparisonValue;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static ComparisonRuleInventory valueOf(ComparisonRuleVO vo) {
        ComparisonRuleInventory inv = new ComparisonRuleInventory();
        inv.setUuid(vo.getUuid());
        inv.setComparisonName(vo.getComparisonName());
        inv.setComparisonValue(vo.getComparisonValue());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<ComparisonRuleInventory> valueOf(Collection<ComparisonRuleVO> vos) {
        List<ComparisonRuleInventory> lst = new ArrayList<>(vos.size());
        for (ComparisonRuleVO vo : vos) {
            lst.add(ComparisonRuleInventory.valueOf(vo));
        }
        return lst;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getComparisonName() {
        return comparisonName;
    }

    public void setComparisonName(String comparisonName) {
        this.comparisonName = comparisonName;
    }

    public String getComparisonValue() {
        return comparisonValue;
    }

    public void setComparisonValue(String comparisonValue) {
        this.comparisonValue = comparisonValue;
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
}
