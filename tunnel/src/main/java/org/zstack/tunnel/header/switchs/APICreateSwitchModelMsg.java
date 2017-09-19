package org.zstack.tunnel.header.switchs;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-09-06
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)

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
