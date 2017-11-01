package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductCategoryInventory;

import java.util.List;

public class ProductDataDictionary {
    private String code;
    private String name;

    private List<ProductCategoryInventory> categories;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductCategoryInventory> getCategories() {
        return categories;
    }

    public void setCategories(List<ProductCategoryInventory> categories) {
        this.categories = categories;
    }
}
