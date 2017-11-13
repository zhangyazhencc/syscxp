package com.syscxp.billing.header.sla;

import com.syscxp.billing.BillingGlobalProperty;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.tunnel.tunnel.APIUpdateInterfaceExpireDateMsg;
import com.syscxp.header.tunnel.tunnel.APIUpdateInterfaceExpireDateReply;
import com.syscxp.header.tunnel.tunnel.APIUpdateTunnelExpireDateMsg;
import com.syscxp.header.tunnel.tunnel.APIUpdateTunnelExpireDateReply;

public class ProductCaller {
    private String productUrl;
    private APIUpdateTunnelExpireDateMsg callMsg;

    private ProductType type;

    public ProductCaller(ProductType type){
        this.type = type;
        initMsg(type);
    }


    private void initMsg(ProductType type){
        switch (type){
            case TUNNEL:
                this.productUrl = BillingGlobalProperty.TUNNEL_SERVER_URL;
                this.callMsg = new APIUpdateTunnelExpireDateMsg();
                break;
            case PORT:
                this.productUrl = BillingGlobalProperty.TUNNEL_SERVER_URL;
                this.callMsg = new APIUpdateTunnelExpireDateMsg();
                break;
        }
    }

    public String getProductUrl() {
        return productUrl;
    }

    public APIUpdateTunnelExpireDateMsg getCallMsg() {
        return callMsg;
    }

    public ProductType getType() {
        return type;
    }
}
