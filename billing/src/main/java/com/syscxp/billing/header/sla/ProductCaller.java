package com.syscxp.billing.header.sla;

import com.syscxp.billing.BillingGlobalProperty;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.tunnel.tunnel.*;

public class ProductCaller {
    private String productUrl;
    private APIUpdateExpireDateMsg callMsg;
    private APIUpdateExpireDateReply callReply;

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
                this.callReply = new APIUpdateTunnelExpireDateReply();
                break;
            case PORT:
                this.productUrl = BillingGlobalProperty.TUNNEL_SERVER_URL;
                this.callMsg = new APIUpdateInterfaceExpireDateMsg();
                this.callReply = new APIUpdateInterfaceExpireDateReply();
                break;
        }
    }

    public String getProductUrl() {
        return productUrl;
    }

    public APIUpdateExpireDateMsg getCallMsg() {
        return callMsg;
    }

    public ProductType getType() {
        return type;
    }

    public APIUpdateExpireDateReply getCallReply() {
        return callReply;
    }
}
