package com.syscxp.header.quota;

import com.syscxp.utils.data.SizeUnit;

public interface QuotaConstant {

    String QUOTA_GLOBAL_CONFIG_CATETORY = "quota";

    long QUOTA_INTERFACE_NUM = 2;
    long QUOTA_INTERFACE_BANDWIDTH = SizeUnit.MEGABYTE.toByte(100);

    long QUOTA_TUNNEL_NUM = 2;
    long QUOTA_TUNNEL_BANDWIDTH = SizeUnit.MEGABYTE.toByte(100);
}
