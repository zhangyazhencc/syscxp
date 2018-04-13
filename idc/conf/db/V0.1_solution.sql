#解决方案#
CREATE TABLE `SolutionVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `accountUuid` varchar(32) NOT  NULL COMMENT '账户uuid',
  `shareAccountUuid` varchar(32) DEFAULT NULL COMMENT '共享账户',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#物理接口#
CREATE TABLE `SolutionInterfaceVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `name` varchar(128) COMMENT '名称',
  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
  `portOfferingUuid` varchar(32) NOT NULL COMMENT '端口规格(类型)',
  `cost` decimal(12,4) DEFAULT 0 COMMENT '费用',
  `discount` decimal(12,4) DEFAULT 0 COMMENT '折扣费用',
  `shareDiscount` decimal(12,4) DEFAULT 0 COMMENT '共享账户折扣费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`),
  CONSTRAINT `fkSolutionInterfaceVO` FOREIGN KEY (`solutionUuid`) REFERENCES `SolutionVO` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#云专线#
CREATE TABLE `SolutionTunnelVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `name` varchar(128) COMMENT '名称',
  `interfaceUuidA` varchar(32) COMMENT '所属物理接口',
  `interfaceUuidZ` varchar(32) COMMENT '所属物理接口',
  `endpointUuidA` varchar(32) NOT NULL COMMENT '连接点A',
  `endpointUuidZ` varchar(32) NOT NULL COMMENT '连接点Z',
  `bandwidthOfferingUuid` varchar(32) NOT NULL COMMENT '带宽Uuid',
  `innerEndpointUuid` varchar(32) DEFAULT NULL COMMENT '互联连接点',
  `type` varchar(32) NOT NULL COMMENT '专线类型',
  `cost` decimal(12,4) DEFAULT 0 COMMENT '费用',
  `discount` decimal(12,4) DEFAULT 0 COMMENT '折扣费用',
  `shareDiscount` decimal(12,4) DEFAULT 0 COMMENT '共享账户折扣费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`),
  CONSTRAINT `fkSolutionTunnelVO` FOREIGN KEY (`solutionUuid`) REFERENCES `SolutionVO` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#VPN网关#
CREATE TABLE SolutionVpnVO (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `name` varchar(128) COMMENT '名称',
  `solutionTunnelUuid` varchar(32) NOT NULL COMMENT '专线',
  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
  `bandwidthOfferingUuid` varchar(32) NOT NULL COMMENT '带宽Uuid',
  `cost` decimal(12,4) DEFAULT 0 COMMENT '费用',
  `discount` decimal(12,4) DEFAULT 0 COMMENT '折扣费用',
  `shareDiscount` decimal(12,4) DEFAULT 0 COMMENT '共享账户折扣费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`),
  CONSTRAINT `fkSolutionVpnVO` FOREIGN KEY (`solutionUuid`) REFERENCES `SolutionVO` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

