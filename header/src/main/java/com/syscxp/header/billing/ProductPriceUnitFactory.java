package com.syscxp.header.billing;

public class ProductPriceUnitFactory {

    public static ProductPriceUnit createVpnPriceUnit(String configCode) {
        return createProductPriceUnit(configCode, "DEFAULT", "DEFAULT");
    }

    public static ProductPriceUnit createVpnPriceUnit(String configCode, String areaCode) {
        return createProductPriceUnit(configCode, areaCode, "DEFAULT");
    }

    private static ProductPriceUnit createProductPriceUnit(ProductCategory category, ProductType type, String configCode,
                                                           String areaCode, String lineCode){
        ProductPriceUnit unit = new ProductPriceUnit();
        unit.setCategoryCode(category);
        unit.setProductTypeCode(type);
        unit.setConfigCode(configCode);
        unit.setAreaCode(areaCode);
        unit.setLineCode(lineCode);
        return unit;
    }

    private static ProductPriceUnit createProductPriceUnit(String configCode,
                                                           String areaCode, String lineCode){
        return createProductPriceUnit(ProductCategory.VPN, ProductType.VPN, configCode, areaCode, lineCode);

    }
}
