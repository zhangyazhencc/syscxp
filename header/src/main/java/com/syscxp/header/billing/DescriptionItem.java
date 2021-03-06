package com.syscxp.header.billing;

public class DescriptionItem {

    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DescriptionItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public DescriptionItem() {
    }
}
