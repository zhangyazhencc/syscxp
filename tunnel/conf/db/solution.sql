
DROP TABLE IF EXISTS `SolutionVO`;
#解决方案#
CREATE TABLE `SolutionVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `accountUuid` varchar(32) NOT  NULL COMMENT '账户uuid',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `totalCost` decimal(12,4) DEFAULT 0.0000 COMMENT '预估费用',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `SolutionInterfaceVO`;
#物理接口#
CREATE TABLE `SolutionInterfaceVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `cost` decimal(12,4) DEFAULT 0.0000 COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,

  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
  `portOfferingUuid` varchar(32) NOT NULL COMMENT '端口规格(类型)',

  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `SolutionTunnelVO`;
#云专线#
CREATE TABLE `SolutionTunnelVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `cost` decimal(12,4) DEFAULT 0.0000 COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,

  `endpointUuidA` varchar(32) NOT NULL COMMENT '连接点A',
  `endpointUuidZ` varchar(32) NOT NULL COMMENT '连接点Z',
  `bandwidthOfferingUuid` varchar(32) NOT NULL COMMENT '带宽Uuid',
  `innerEndpointUuid` varchar(32) DEFAULT NULL COMMENT '中间点UUID',

  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `SolutionVpnVO`;
#VPN网关#
CREATE TABLE SolutionVpnVO (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `cost` decimal(12,4) DEFAULT 0.0000 COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  `zoneUuid` varchar(32) NOT NULL COMMENT '区域',
  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
  `bandwidthOfferingUuid` BIGINT NOT NULL COMMENT '带宽',

  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;