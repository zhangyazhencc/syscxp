package com.syscxp.billing.header.sla;

import com.syscxp.billing.BillingGlobalProperty;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.tunnel.tunnel.*;

public class ProductCaller {
    private String productUrl;

    private ProductType type;

    public ProductCaller(ProductType type){
        this.type = type;
        initMsg(type);
    }


    private void initMsg(ProductType type){
        switch (type){
            case TUNNEL:
                this.productUrl = BillingGlobalProperty.TUNNEL_SERVER_URL;
                break;
            case PORT:
                this.productUrl = BillingGlobalProperty.TUNNEL_SERVER_URL;
                break;
            case DISK :
                this.productUrl = BillingGlobalProperty.ECP_SERVER_URL;
                break;
                case HOST :
                this.productUrl = BillingGlobalProperty.ECP_SERVER_URL;
                break;
        }
    }

    public String getProductUrl() {
        return productUrl;
    }

    public ProductType getType() {
        return type;
    }

}
