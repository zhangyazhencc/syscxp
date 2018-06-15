package com.syscxp.kvm;

import com.syscxp.header.message.APIReply;


public class APIGetKVMHostReply extends APIReply {
    private KVMHostInventory inventory;

    private Integer cpuNum;

    private Long totalPhysicalMemory;

    public KVMHostInventory getInventory() {
        return inventory;
    }

    public void setInventory(KVMHostInventory inventory) {
        this.inventory = inventory;
    }

    public Integer getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(Integer cpuNum) {
        this.cpuNum = cpuNum;
    }

    public Long getTotalPhysicalMemory() {
        return totalPhysicalMemory;
    }

    public void setTotalPhysicalMemory(Long totalPhysicalMemory) {
        this.totalPhysicalMemory = totalPhysicalMemory;
    }
}