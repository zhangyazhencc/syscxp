package com.syscxp.billing.header.sla;

import com.syscxp.billing.AlipayGlobalProperty;
import com.syscxp.header.billing.ProductType;

public class ProductCaller {
    private String productUrl;

    private ProductType type;

    public ProductCaller(ProductType type) {
        this.type = type;
        initMsg(type);
    }


    private void initMsg(ProductType type) {
        switch (type) {
            case TUNNEL:
                this.productUrl = AlipayGlobalProperty.TUNNEL_SERVER_URL;
                break;
            case PORT:
                this.productUrl = AlipayGlobalProperty.TUNNEL_SERVER_URL;
                break;
            case VPN:
                this.productUrl = AlipayGlobalProperty.VPN_SERVER_URL;
                break;
            case EDGELINE:
                this.productUrl = AlipayGlobalProperty.TUNNEL_SERVER_URL;
                break;
            case DISK:
                this.productUrl = AlipayGlobalProperty.ECP_SERVER_URL;
                break;
            case HOST:
                this.productUrl = AlipayGlobalProperty.ECP_SERVER_URL;
                break;
            case RESOURCEPOOL:
                this.productUrl = AlipayGlobalProperty.ECP_SERVER_URL;
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
