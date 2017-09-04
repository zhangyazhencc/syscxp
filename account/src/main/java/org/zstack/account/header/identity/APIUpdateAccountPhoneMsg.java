package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/8.
 */
@Action(category = AccountConstant.ACTION_CATEGORY, names = {"acount_phone_email"}, accountOnly = true)
public class APIUpdateAccountPhoneMsg extends APIMessage implements AccountMessage{

    @APIParam
    private String oldphone;

    @APIParam
    private String oldcode;

    @APIParam
    private String newphone;

    @APIParam
    private String newcode;

    public void setOldphone(String oldphone) {
        this.oldphone = oldphone;
    }

    public void setOldcode(String oldcode) {
        this.oldcode = oldcode;
    }

    public void setNewphone(String newphone) {
        this.newphone = newphone;
    }

    public void setNewcode(String newcode) {
        this.newcode = newcode;
    }

    public String getOldphone() {

        return oldphone;
    }

    public String getOldcode() {
        return oldcode;
    }

    public String getNewphone() {
        return newphone;
    }

    public String getNewcode() {
        return newcode;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

}
