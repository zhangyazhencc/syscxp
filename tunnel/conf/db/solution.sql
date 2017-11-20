#解决方案#
CREATE TABLE `SolutionVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `totalCost` varchar(20) DEFAULT NULL COMMENT '预估费用',
  `lastOpDate` timestCamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#物理接口#
CREATE TABLE `SolutionInterfaceVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `cost` varchar(20) DEFAULT NULL COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,

  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
  `switchPortUuid` varchar(32) NOT NULL COMMENT '对应交换机端口',

  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#云专线#
CREATE TABLE `SolutionTunnelVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `cost` varchar(20) DEFAULT NULL COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,

  `endpointUuidA` varchar(32) NOT NULL COMMENT '连接点A',
  `endpointUuidZ` varchar(32) NOT NULL COMMENT '连接点Z',
  `bandwidth` BIGINT NOT NULL COMMENT '带宽',

  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#VPN网关#
CREATE TABLE SolutionVpnVO (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `cost` varchar(20) DEFAULT NULL COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,

  `zoneUuid` varchar(32) NOT NULL COMMENT '区域',
  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
  `bandwidth` BIGINT NOT NULL COMMENT '带宽',

  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;