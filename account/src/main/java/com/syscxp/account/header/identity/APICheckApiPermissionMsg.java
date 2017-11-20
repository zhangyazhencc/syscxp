package com.syscxp.account.header.identity;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.user.UserVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.List;

import static com.syscxp.utils.CollectionDSL.list;

/**
 * Created by xing5 on 2016/3/10.
 */
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APICheckApiPermissionMsg extends APISyncCallMessage {
    @APIParam(required = false, resourceType = UserVO.class)
    private String userUuid;
    @APIParam(nonempty = true)
    private List<String> apiNames;

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public List<String> getApiNames() {
        return apiNames;
    }

    public void setApiNames(List<String> apiNames) {
        this.apiNames = apiNames;
    }
 
    public static APICheckApiPermissionMsg __example__() {
        APICheckApiPermissionMsg msg = new APICheckApiPermissionMsg();
        msg.setApiNames(list("APICheckApiPermissionMsg"));
        msg.setUserUuid(uuid());

        return msg;
    }

}
