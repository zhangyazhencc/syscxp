package com.syscxp.idc.header.trustee;

import com.syscxp.header.billing.NotifyCallBackData;

import java.util.List;

public class CreateTrusteeCallback extends NotifyCallBackData {

    private TrusteeVO trusteeVO;

    private List<TrustDetailVO> trustDetailList;

    public TrusteeVO getTrusteeVO() {
        return trusteeVO;
    }

    public void setTrusteeVO(TrusteeVO trusteeVO) {
        this.trusteeVO = trusteeVO;
    }

    public List<TrustDetailVO> getTrustDetailList() {
        return trustDetailList;
    }

    public void setTrustDetailList(List<TrustDetailVO> trustDetailList) {
        this.trustDetailList = trustDetailList;
    }
}
