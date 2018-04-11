#解决方案#
DROP TABLE IF EXISTS `SolutionVO`;
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
DROP TABLE IF EXISTS `SolutionInterfaceVO`;
CREATE TABLE `SolutionInterfaceVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `name` varchar(128) COMMENT '名称',
  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
  `portOfferingUuid` varchar(32) NOT NULL COMMENT '端口规格(类型)',
  `cost` decimal(12,4) DEFAULT 0 COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`),
  CONSTRAINT `fkSolutionInterfaceVO` FOREIGN KEY (`solutionUuid`) REFERENCES `SolutionVO` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#云专线#
DROP TABLE IF EXISTS `SolutionTunnelVO`;
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
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`),
  CONSTRAINT `fkSolutionTunnelVO` FOREIGN KEY (`solutionUuid`) REFERENCES `SolutionVO` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#VPN网关#
DROP TABLE IF EXISTS `SolutionVpnVO`;
CREATE TABLE SolutionVpnVO (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `name` varchar(128) COMMENT '名称',
  `solutionTunnelUuid` varchar(32) NOT NULL COMMENT '专线',
  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
  `bandwidthOfferingUuid` varchar(32) NOT NULL COMMENT '带宽Uuid',
  `cost` decimal(12,4) DEFAULT 0 COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`),
  CONSTRAINT `fkSolutionVpnVO` FOREIGN KEY (`solutionUuid`) REFERENCES `SolutionVO` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `ShareSolutionVO`;
#分享方案#
CREATE TABLE  `ShareSolutionVO` (
    `uuid` varchar(32) NOT NULL COMMENT 'UUID',
    `accountUuid` varchar(32) NOT NULL,
    `ownerAccountUuid` varchar(32) NOT NULL,
    `solutionUuid` varchar(32) NOT NULL,
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`),
    UNIQUE KEY `ukShareSolutionVO` (`accountUuid`,`ownerAccountUuid`,`solutionUuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `SolutionVO` ADD COLUMN `isShare` tinyint(1) unsigned DEFAULT 0 COMMENT '是否共享';

ALTER TABLE `totalCost` ADD COLUMN `totalCost` decimal(12,4) DEFAULT '0.0000' COMMENT '预估费用';

