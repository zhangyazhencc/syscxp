package org.zstack.account.header.identity;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.account.header.user.UserVO;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.UserCredentialCheck;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.util.List;

import static org.zstack.utils.CollectionDSL.list;

/**
 * Created by xing5 on 2016/3/10.
 */
@UserCredentialCheck
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
