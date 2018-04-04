package com.syscxp.header.idc;

import com.syscxp.header.configuration.PythonClass;

/**
 * Created by wangwg on 2017/11/20.
 */

@PythonClass
public interface SolutionConstant {
    String SERVICE_ID = "solution";
    String ACTION_CATEGORY = "solution";

    String QUOTA_SOLUTION_NUM = "solution.num";
    String QUOTA_SOLUTION_INTERFACE_NUM = "solutionInterface.num";
    String QUOTA_SOLUTION_TUNNEL_NUM = "solutionTunnel.num";
    String QUOTA_SOLUTION_VPN_NUM = "solutionVpn.num";
}
