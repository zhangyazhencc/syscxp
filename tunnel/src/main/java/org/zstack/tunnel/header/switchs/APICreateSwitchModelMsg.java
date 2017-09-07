package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-09-06
 */
public class APICreateSwitchModelMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 128)
    private String model;
    @APIParam(maxLength = 128)
    private String subModel;
    @APIParam(emptyString = false)
    private Integer mpls;

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

    public Integer getMpls() {
        return mpls;
    }

    public void setMpls(Integer mpls) {
        this.mpls = mpls;
    }
}
