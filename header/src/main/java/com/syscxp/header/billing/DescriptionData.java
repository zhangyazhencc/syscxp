package com.syscxp.header.billing;

import java.util.List;

public class DescriptionData {

    private List<DescriptionItem> datas;

    public List<DescriptionItem> getDatas() {
        return datas;
    }

    public void setDatas(List<DescriptionItem> datas) {
        this.datas = datas;
    }

    public void add(DescriptionItem item) {
        datas.add(item);
    }

    public void addAll(List<DescriptionItem> items) {
        datas.addAll(items);
    }
}
