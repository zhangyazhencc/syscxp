package com.syscxp.header.tunnel.solution;

import com.syscxp.header.query.APIQueryReply;

public class APIRecountInterfacePriceReply extends APIQueryReply {
    private SolutionInterfaceInventory interfaceInventory;
    private SolutionInventory solutionInventory;

    public SolutionInterfaceInventory getInterfaceInventory() {
        return interfaceInventory;
    }

    public void setInterfaceInventory(SolutionInterfaceInventory interfaceInventory) {
        this.interfaceInventory = interfaceInventory;
    }

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
