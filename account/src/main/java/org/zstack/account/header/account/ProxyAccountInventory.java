package org.zstack.account.header.account;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * Created by wangeg on 2017/09/26.
 */
@Inventory(mappingVOClass = ProxyAccountRefVO.class)
public class ProxyAccountInventory {
    private long id;
    private String accountUuid;
    private String customerAccountUuid;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    private AccountInventory accountInventory;

    public static ProxyAccountInventory valueOf(ProxyAccountRefVO vo) {
        ProxyAccountInventory inv = new ProxyAccountInventory();
        inv.setId(vo.getId());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setCustomerAccountUuid(vo.getCustomerAccountUuid());
        inv.setAccountInventory(AccountInventory.valueOf(vo.getProxyAccountVO()));
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<ProxyAccountInventory> valueOf(Collection<ProxyAccountRefVO> vos) {
        List<ProxyAccountInventory> lst = new ArrayList<ProxyAccountInventory>(vos.size());
        for (ProxyAccountRefVO vo : vos) {
            lst.add(ProxyAccountInventory.valueOf(vo));
        }
        return lst;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getCustomerAccountUuid() {
        return customerAccountUuid;
    }

    public void setCustomerAccountUuid(String customerAccountUuid) {
        this.customerAccountUuid = customerAccountUuid;
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

    public AccountInventory getAccountInventory() {
        return accountInventory;
    }

    public void setAccountInventory(AccountInventory accountInventory) {
        this.accountInventory = accountInventory;
    }
}
