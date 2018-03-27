CREATE TABLE syscxp_trustee.TrusteeEO (
		`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
    `name` varchar(128) NOT NULL COMMENT '名称',
		`description` varchar(255),
    `accountName` varchar(128) NOT NULL COMMENT '用户名',
    `accountUuid` varchar(32) NOT NULL COMMENT '用户uuid',
    `company` varchar(128) NOT NULL COMMENT '用户公司',
    `contractNum` varchar(128) NOT NULL COMMENT '合同号',
    `nodeUuid` varchar(32) NOT NULL COMMENT '节点uuid',
		`nodeName` varchar(128) NOT NULL COMMENT '节点名',
		`productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
		`duration` int(11) NOT NULL COMMENT '最近一次购买时长',
		`totalCost` decimal(12,4) DEFAULT 0 COMMENT '总价',
		`expireDate` timestamp NULL DEFAULT NULL COMMENT '截止时间',
		`deleted` varchar(32) DEFAULT NULL,
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `TrusteeVO` AS SELECT
    `uuid`, `name`, `description`, `accountName`,`accountUuid`,`company`,
		`contractNum`,`nodeUuid`,`nodeName`,`productChargeModel`,`totalCost`,
		`expireDate`, `lastOpDate`, `createDate`
FROM `TrusteeEO` WHERE deleted IS NULL;

CREATE TABLE syscxp_trustee.TrustDetailVO (
		`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
		`name` varchar(32) NOT NULL COMMENT '名称',
    `TrusteeUuid` varchar(32) NOT NULL COMMENT '托管uuid',
		`cost` decimal(12,4) DEFAULT 0 COMMENT '费用',
		`description` varchar(255),
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
