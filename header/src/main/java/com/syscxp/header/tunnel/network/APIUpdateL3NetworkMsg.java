package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIParam;

import java.sql.Timestamp;

public class APIUpdateL3NetworkMsg {


    @APIParam
    private String uuid;

    @APIParam
    private String accountUuid;
    @APIParam
    private String ownerAccountUuid;
    @APIParam
    private String name;
    @APIParam
    private String code;
    @APIParam
    private Long vid;
    @APIParam
    private String type;
    @APIParam
    private String status;
    @APIParam
    private Long endPointNum;
    @APIParam
    private String description;
    @APIParam
    private Long duration;
    @APIParam
    private String productChargeModel;
    @APIParam
    private Long maxModifies;
    @APIParam
    private Timestamp expireDate;
}
