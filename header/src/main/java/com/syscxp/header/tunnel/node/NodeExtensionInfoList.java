package com.syscxp.header.tunnel.node;

import java.util.List;

public class NodeExtensionInfoList {

    private String page_no;
    private String page_count;
    private String page_size;
    private String total;
    private String count;
    private List<Integer> page_range;
    private String nodeExtensionInfos;

    public List<Integer> getPage_range() {
        return page_range;
    }

    public void setPage_range(List<Integer> page_range) {
        this.page_range = page_range;
    }

    public String getPage_count() {
        return page_count;
    }

    public void setPage_count(String page_count) {
        this.page_count = page_count;
    }

    public String getPage_no() {
        return page_no;
    }

    public void setPage_no(String page_no) {
        this.page_no = page_no;
    }

    public String getPage_size() {
        return page_size;
    }

    public void setPage_size(String page_size) {
        this.page_size = page_size;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getNodeExtensionInfos() {
        return nodeExtensionInfos;
    }

    public void setNodeExtensionInfos(String nodeExtensionInfos) {
        this.nodeExtensionInfos = nodeExtensionInfos;
    }
}
