package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.SwitchConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by DCY on 2017-09-06
 */
public class APICreateSwitchModelMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 128)
    private String brand;
    @APIParam(emptyString = false,maxLength = 128)
    private String model;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String subModel;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSubModel() {
        return subModel;
    }

    public void setSubModel(String subModel) {
        this.subModel = subModel;
    }
}
