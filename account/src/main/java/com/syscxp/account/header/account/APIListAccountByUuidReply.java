package com.syscxp.account.header.account;


import com.syscxp.header.message.APIReply;

import java.util.List;
import java.util.Map;

public class APIListAccountByUuidReply extends APIReply {

    private List<Map<String,String>> accountList;

    public List<Map<String, String>> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Map<String, String>> accountList) {
        this.accountList = accountList;
    }

}
