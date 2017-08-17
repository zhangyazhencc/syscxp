package org.zstack.account.header.identity;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = AccountVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "user", inventoryClass = UserInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "accountUuid"),
        @ExpandedQuery(expandedField = "policy", inventoryClass = PolicyInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "accountUuid"),
})
public class AccountInventory {
    private String uuid;
    private String name;
    private String email;
    private String phone;
    private String trueName;
    private String company;
    private String industry;
    private String status;
    private String description;
    private String type;

    private String grade;
    private String companyNature;
    private String salesman;
    private String contacts;
    private String contactNumber;

    private Timestamp createDate;
    private Timestamp lastOpDate;
    
    public static AccountInventory valueOf(AccountVO vo,AccountExtraInfoVO aeivo) {
        AccountInventory inv = new AccountInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setTrueName(vo.getTrueName());
        inv.setCompany(vo.getCompany());
        inv.setDescription(vo.getDescription());
        inv.setEmail(vo.getEmail());
        inv.setPhone(vo.getPhone());
        inv.setIndustry(vo.getIndustry().toString());
        inv.setType(vo.getType().toString());

        inv.setGrade(aeivo.getGrade().toString());
        inv.setCompanyNature(aeivo.getCompanyNature().toString());
        inv.setSalesman(aeivo.getSalesman());
        inv.setContacts(aeivo.getContacts());
        inv.setContactNumber(aeivo.getContactNumber());

        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static AccountInventory valueOf(AccountVO vo) {
        AccountInventory inv = new AccountInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setTrueName(vo.getTrueName());
        inv.setCompany(vo.getCompany());
        inv.setDescription(vo.getDescription());
        inv.setEmail(vo.getEmail());
        inv.setPhone(vo.getPhone());
        inv.setIndustry(vo.getIndustry().toString());
        inv.setType(vo.getType().toString());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<AccountInventory> valueOf(Collection<AccountVO> vos) {
        List<AccountInventory> lst = new ArrayList<AccountInventory>(vos.size());
        for (AccountVO vo : vos) {
            lst.add(AccountInventory.valueOf(vo));
        }
        return lst;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getTrueName() {
        return trueName;
    }

    public String getCompany() {
        return company;
    }

    public String getIndustry() {
        return industry;
    }

    public String getGrade() {
        return grade;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyNature() {
        return companyNature;
    }

    public String getSalesman() {
        return salesman;
    }

    public String getContacts() {
        return contacts;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setCompanyNature(String companyNature) {
        this.companyNature = companyNature;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
