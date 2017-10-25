package com.syscxp.header.billing;

public class ProductPriceUnitFactory {

    public static ProductPriceUnit createVpnPriceUnit(String configCode) {
        ProductPriceUnit unit = new ProductPriceUnit();
        unit.setCategoryCode(Category.VPN);
        unit.setProductTypeCode(ProductType.VPN);
        unit.setConfigCode(configCode);
        unit.setAreaCode(null);
        unit.setLineCode(null);
        return unit;
    }
}
