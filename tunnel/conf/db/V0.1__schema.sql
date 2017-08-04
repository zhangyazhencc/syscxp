use syscxp_;

CREATE TABLE  `syscxp_tunnel`.`ManagementNodeVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `hostName` varchar(255) DEFAULT NULL,
    `port` int unsigned DEFAULT NULL,
    `state` varchar(128) NOT NULL,
    `joinDate` timestamp DEFAULT CURRENT_TIMESTAMP,
    `heartBeat` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_tunnel`.`GlobalConfigVO` (
    `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `description` varchar(1024) DEFAULT NULL,
    `category` varchar(64) NOT NULL,
    `defaultValue` text DEFAULT NULL,
    `value` text DEFAULT NULL,
    PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_tunnel`.`JobQueueVO` (
    `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
    `name` varchar(255) NOT NULL UNIQUE,
    `owner` varchar(255) DEFAULT NULL,
    `workerManagementNodeId` varchar(32) DEFAULT NULL,
    `takenDate` timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_tunnel`.`JobQueueEntryVO` (
    `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `jobQueueId` bigint unsigned NOT NULL,
    `state` varchar(128) NOT NULL,
    `context` blob DEFAULT NULL,
    `owner` varchar(255) DEFAULT NULL,
    `issuerManagementNodeId` varchar(32) DEFAULT NULL,
    `restartable` tinyint(1) unsigned NOT NULL DEFAULT 0,
    `inDate` timestamp DEFAULT CURRENT_TIMESTAMP,
    `doneDate` timestamp NULL,
    `errText` text DEFAULT NULL,
    PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


## 专线通道连接点
CREATE TABLE  `EndpointVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `nodeUuid` varchar(32) DEFAULT NULL,
  `name` varchar(255) NOT NULL UNIQUE COMMENT '连接点名称',
  `code` varchar(128) NOT NULL COMMENT '连接点编号',
  `enabled` varchar(32)  DEFAULT NULL,
  `openToCustomers` varchar(32) DEFAULT 'false',
  `status` varchar(32) DEFAULT NULL,
  `subType` varchar(64) DEFAULT NULL,
  `description` varchar(4000)  DEFAULT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT '0',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


##主机
CREATE TABLE  `HostVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `name` varchar(128) DEFAULT NULL ,
  `code` varchar(128) DEFAULT NULL,
  `ip` varchar(128) DEFAULT NULL,
  `username` varchar(128) DEFAULT NULL ,
  `password` varchar(128) DEFAULT NULL ,
  `monitorState` varchar(32) DEFAULT NULL COMMENT '监控状况',
  `monitorStatus` varchar(32) DEFAULT NULL COMMENT '监控状态',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


##交换机
CREATE TABLE  `SwitchVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `endpointUuid` varchar(32) DEFAULT NULL ,
  `code` varchar(128) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `brand` varchar(128) DEFAULT NULL,
  `model` varchar(32) DEFAULT NULL,
  `subModel` varchar(32) DEFAULT NULL,
  `upperType` varchar(32) DEFAULT NULL,
  `enabled` TINYINT(1) DEFAULT NULL,
  `owner` varchar(128) DEFAULT NULL,
  `rack` varchar(32) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `vlanBegin` INT(11) DEFAULT NULL,
  `vlanEnd` INT(11) DEFAULT NULL,
  `mIP` varchar(128) DEFAULT NULL,
  `username` varchar(128) DEFAULT NULL ,
  `password` varchar(128) DEFAULT NULL ,
  `vxlanSupport` TINYINT(1) DEFAULT NULL ,
  `status` varchar(16) DEFAULT NULL COMMENT '状态',
  `isPrivate` varchar(16) DEFAULT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT '0',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


##主机交换机监控
CREATE TABLE `HostSwitchMonitorVO` (
  `uuid` VARCHAR(32) NOT NULL,
  `hostUuid` VARCHAR(32) DEFAULT NULL,
  `switchUuid` VARCHAR(32) DEFAULT NULL,
  `endpointUuid` VARCHAR(32) DEFAULT NULL,
  `interfaceName` varchar(128)  DEFAULT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT '0',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


##agent
CREATE TABLE `agentVO` (
  `uuid` VARCHAR(32) NOT NULL AUTO_INCREMENT,
  `endpointUuid` VARCHAR(32) DEFAULT NULL,
  `switchUuid` VARCHAR(32) DEFAULT NULL,
  `code` varchar(128) DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `enabled` TINYINT(1)  DEFAULT NULL,
  `status` varchar(32)  DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ;



##交换机端口
CREATE TABLE  `SwitchPortVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `switchUuid` varchar(32) DEFAULT NULL ,
  `portNum` INT(11) DEFAULT NULL ,
  `portName` varchar(128) DEFAULT NULL,
  `label` varchar(128) DEFAULT NULL ,
  `vlan` INT(11) DEFAULT NULL ,
  `endVlan` INT(11) DEFAULT NULL ,
  `reuse` TINYINT(32) DEFAULT NULL,
  `autoAlloc` TINYINT(1) DEFAULT NULL,
  `enabled` TINYINT(1) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


##交换机VLAN
CREATE TABLE  `SwitchVlanVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `switchUuid` varchar(32) DEFAULT NULL ,
  `startVlan` INT(11) DEFAULT NULL ,
  `endVlan` INT(11) DEFAULT NULL ,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##专线通道任务
CREATE TABLE  `TaskVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `name` varchar(32) DEFAULT NULL ,
  `status` VARCHAR(32) DEFAULT NULL ,
  `objectUuid` VARCHAR(32) DEFAULT NULL,
  `objectType` VARCHAR(64) DEFAULT NULL ,
  `body` VARCHAR(4000) DEFAULT NULL ,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##专线通道子任务
CREATE TABLE  `SubTaskVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `taskUuid` varchar(32) DEFAULT NULL ,
  `agentUuid` varchar(32) DEFAULT NULL ,
  `name` varchar(32) DEFAULT NULL ,
  `seq` INT(11) DEFAULT NULL ,
  `body` VARCHAR(4000) DEFAULT NULL ,
  `result` VARCHAR(4000) DEFAULT NULL ,
  `status` VARCHAR(32) DEFAULT NULL ,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


##部署任务
CREATE TABLE `DeployTaskVO` (
  `uuid` VARCHAR(32) NOT NULL,
  `tunnelPointUuid` VARCHAR(32) DEFAULT NULL,
  `state` varchar(32) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `description` varchar(4000)  DEFAULT NULL,
  `comment` varchar(4000) DEFAULT NULL,
  `finishBy` varchar(64)  DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

## 专线通道节点
CREATE TABLE  `NodeVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `extensionInfoUuid` varchar(32)  DEFAULT NULL,
  `name` varchar(255) NOT NULL UNIQUE COMMENT '节点名称',
  `code` varchar(128) NOT NULL COMMENT '节点编号',
  `description` varchar(255) NOT NULL COMMENT '节点描述',
  `contact` varchar(128) NOT NULL COMMENT '联系人',
  `telephone` varchar(32) NOT NULL COMMENT '联系人电话',
  `province` varchar(128) NOT NULL COMMENT '省',
  `city` varchar(128) NOT NULL COMMENT '市',
  `address` VARCHAR(256) DEFAULT NULL COMMENT '地址',
  `longtitude` DECIMAL(10,5) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(10,5) DEFAULT NULL COMMENT '纬度',
  `property` varchar(128) DEFAULT NULL,
  `status` varchar(16) DEFAULT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT '0',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


##网络类型
CREATE TABLE  `NetworkTypeVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `name` varchar(255) NOT NULL UNIQUE COMMENT '连接点名称',
  `code` varchar(128) NOT NULL COMMENT '连接点编号',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##agent网络类型
CREATE TABLE `AgentNetworkTypeVO` (
  `uuid`varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `networkTypeUuid` varchar(32) DEFAULT NULL,
  `agentUuid` varchar(32) DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `vxlanPort` varchar(32) DEFAULT NULL,
  `gatewayIp` varchar(32) DEFAULT NULL,
  `gatewayMac` varchar(32) DEFAULT NULL,
  `physicalPort` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##
CREATE TABLE `SpeedRecordVO` (
  `uuid` VARCHAR(32) NOT NULL,
  `tunnelUuid` VARCHAR(32) DEFAULT NULL,
  `protocol` varchar(32) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `srcDirection` int(11) DEFAULT NULL,
  `dstDirection` int(11) DEFAULT NULL,
  `avgSpeed` int(11) DEFAULT NULL,
  `maxSpeed` int(11) DEFAULT NULL,
  `minSpeed` int(11) DEFAULT NULL,
  `completed` TINYINT(1) DEFAULT NULL,
  `deleted` TINYINT(11) NOT NULL DEFAULT '0',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uudid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##通道
CREATE TABLE `TunnelVO` (
  `uuid` VARCHAR(32) NOT NULL,
  `networkTypeUuid` VARCHAR(32) DEFAULT NULL,
  `projectUuid` varchar(32) DEFAULT NULL,
  `code` varchar(128) DEFAULT NULL,
  `name` varchar(256) DEFAULT NULL,
  `bandwidth` int(11) DEFAULT NULL,
  `vni` int(11) DEFAULT NULL,
  `distance` decimal(10,2) DEFAULT NULL,
  `state` varchar(32) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `endpointA` VARCHAR(32) DEFAULT NULL,
  `endpointB` VARCHAR(32) DEFAULT NULL,
  `endpointAType` varchar(64) DEFAULT NULL,
  `endpointBType` varchar(64) DEFAULT NULL,
  `priExclusive` varchar(32) DEFAULT 'false',
  `alarmed` TINYINT(11) NOT NULL DEFAULT '0',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0',
  `billingDate` timestamp NULL DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


##通道端点
CREATE TABLE `TunnelPointVO` (
  `uuid` VARCHAR(32) NOT NULL ,
  `tunnelUuid` VARCHAR(32) DEFAULT NULL,
  `agentUuid` VARCHAR(32) DEFAULT NULL,
  `switchUuid` VARCHAR(32) DEFAULT NULL,
  `hostingUuid` VARCHAR(32) DEFAULT NULL,
  `bridgeName` VARCHAR(32) DEFAULT NULL,
  `meter` int(11) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `role` varchar(32) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `hostingType` varchar(32) DEFAULT NULL,
  `isAttached` TINYINT(1) DEFAULT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT '0',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

## 通道端口
CREATE TABLE `TunnelPointSwitchPortVO` (
  `uuid` VARCHAR(32) NOT NULL ,
  `switchUuid` VARCHAR(32) DEFAULT NULL,
  `switchPortUuid` VARCHAR(32) DEFAULT NULL,
  `tunnelPointUuid` VARCHAR(32) DEFAULT NULL,
  `groupUuid` VARCHAR(32) DEFAULT NULL,
  `portNum` int(11) DEFAULT NULL,
  `portName` varchar(128) DEFAULT NULL,
  `portType` varchar(32) DEFAULT NULL,
  `vlan` int(11) DEFAULT NULL,
  `innerVlan` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT '0',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##通道监控
CREATE TABLE `TunnelMonitorVO` (
  `uuid` VARCHAR(32) NOT NULL,
  `tunnelUuid` VARCHAR(32) DEFAULT NULL,
  `tunnelPointAUuid` VARCHAR(32) DEFAULT NULL,
  `tunnelPointBUuid` VARCHAR(32) DEFAULT NULL,
  `monitorAIp` varchar(64) DEFAULT NULL,
  `monitorBIp` varchar(64) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `msg` varchar(1024) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SwitchModelVO` (
  `uuid` VARCHAR(32) NOT NULL,
  `model` VARCHAR(128) DEFAULT NULL,
  `subModel` VARCHAR(128) DEFAULT NULL,
  `mpls` VARCHAR(128) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

