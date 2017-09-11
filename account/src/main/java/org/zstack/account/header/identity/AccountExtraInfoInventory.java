package org.zstack.account.header.identity;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = AccountExtraInfoVO.class)

public class AccountExtraInfoInventory {

    private String uuid;
    private String grade;
    private String salesman;
    private String createWay;
    private String userUuid;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static AccountExtraInfoInventory valueOf(AccountExtraInfoVO vo) {
        AccountExtraInfoInventory inv = new AccountExtraInfoInventory();
        inv.setUuid(vo.getUuid());
        inv.setGrade(vo.getGrade().toString());
        inv.setSalesman(vo.getUserUuid());
        inv.setUserUuid(vo.getUserUuid());
        inv.setCreateWay(vo.getCreateWay());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<AccountExtraInfoInventory> valueOf(Collection<AccountExtraInfoVO> vos) {
        List<AccountExtraInfoInventory> invs = new ArrayList<AccountExtraInfoInventory>();
        for (AccountExtraInfoVO vo : vos) {
            invs.add(valueOf(vo));
        }

        return invs;
    }

    public void setCreateWay(String createWay) {
        this.createWay = createWay;
    }

    public String getCreateWay() {

        return createWay;
    }

    public String getUuid() {
        return uuid;
    }

    public String getGrade() {
        return grade;
    }

    public String getSalesman() {
        return salesman;
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

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }
}
