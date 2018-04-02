package com.syscxp.idc.header;

import com.syscxp.header.billing.NotifyCallBackData;

import java.util.List;

public class CreateIdcCallback extends NotifyCallBackData {

    private IdcVO trusteeVO;

    private List<IdcDetailVO> trustDetailList;

    public IdcVO getTrusteeVO() {
        return trusteeVO;
    }

    public void setTrusteeVO(IdcVO trusteeVO) {
        this.trusteeVO = trusteeVO;
    }

    public List<IdcDetailVO> getTrustDetailList() {
        return trustDetailList;
    }

    public void setTrustDetailList(List<IdcDetailVO> trustDetailList) {
        this.trustDetailList = trustDetailList;
    }
}
