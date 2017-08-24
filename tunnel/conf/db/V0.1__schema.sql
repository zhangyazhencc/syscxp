use syscxp_tunnel;

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

#########################################################################################

## 节点
CREATE TABLE  `NodeEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `name` varchar(255) NOT NULL UNIQUE COMMENT '节点名称',
  `code` varchar(128) NOT NULL UNIQUE COMMENT '节点编号',
  `description` varchar(255) DEFAULT NULL COMMENT '节点描述',
  `contact` varchar(128) NOT NULL COMMENT '联系人',
  `telephone` varchar(32) NOT NULL COMMENT '联系人电话',
  `province` varchar(128) NOT NULL COMMENT '省',
  `city` varchar(128) NOT NULL COMMENT '市',
  `address` VARCHAR(256) NOT NULL COMMENT '地址',
  `longtitude` DECIMAL(10,5) NOT NULL COMMENT '经度',
  `latitude` DECIMAL(10,5) NOT NULL COMMENT '纬度',
  `property` varchar(128) NOT NULL COMMENT '节点类型',
  `status` varchar(16) NOT NULL COMMENT '是否开放',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `extensionInfoUuid` varchar(32)  DEFAULT NULL COMMENT '节点额外信息',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `NodeVO` AS SELECT uuid, name, code, description, contact, telephone, province, city, address, longtitude, latitude, property, status, extensionInfoUuid, lastOpDate, createDate
                        FROM `NodeEO` WHERE deleted = 0;

## 连接点
CREATE TABLE  `EndpointEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `nodeUuid` varchar(32) NOT NULL COMMENT '节点id',
  `name` varchar(255) NOT NULL UNIQUE COMMENT '连接点名称',
  `code` varchar(128) NOT NULL UNIQUE COMMENT '连接点编号',
  `enabled` TINYINT(1)  NOT NULL DEFAULT '1' COMMENT '是否启用',
  `openToCustomers` TINYINT(1)  NOT NULL DEFAULT '0' COMMENT '是否对外开放',
  `description` varchar(255)  DEFAULT NULL COMMENT '描述',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `EndpointVO` AS SELECT uuid, nodeUuid, name, code, enabled, openToCustomers, description, lastOpDate, createDate
                            FROM `EndpointEO` WHERE deleted = 0;

##交换机
CREATE TABLE  `SwitchVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `endpointUuid` varchar(32) DEFAULT NULL COMMENT '连接点UUID',
  `code` varchar(128) DEFAULT NULL COMMENT '交换机编号',
  `name` varchar(128) DEFAULT NULL COMMENT '交换机名称',
  `brand` varchar(128) DEFAULT NULL COMMENT '交换机品牌',
  `switchModelUuid` varchar(32) DEFAULT NULL COMMENT '交换机型号UUID',
  #`model` varchar(32) DEFAULT NULL COMMENT '交换机型号',
  #`subModel` varchar(32) DEFAULT NULL COMMENT '交换机子型号',
  `upperType` varchar(32) DEFAULT NULL COMMENT '上联类型：物理专线/互联网',
  `enabled` TINYINT(1) DEFAULT NULL COMMENT '是否启用',
  `owner` varchar(128) DEFAULT NULL COMMENT '交换机属主',
  `rack` varchar(32) DEFAULT NULL COMMENT '交换机位置',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  #`vlanBegin` INT(11) DEFAULT NULL COMMENT '起始VLAN',
  #`vlanEnd` INT(11) DEFAULT NULL COMMENT '结束VLAN',
  `mIP` varchar(128) DEFAULT NULL COMMENT '管理IP',
  `username` varchar(128) DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) DEFAULT NULL COMMENT '密码',
  #`vxlanSupport` TINYINT(1) DEFAULT NULL ,
  `status` varchar(16) DEFAULT NULL COMMENT '状态',
  `isPrivate` varchar(16) DEFAULT NULL COMMENT '专用还是公用',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##交换机型号
CREATE TABLE `SwitchModelVO` (
  `uuid` VARCHAR(32) NOT NULL COMMENT 'UUID',
  `model` VARCHAR(128) DEFAULT NULL COMMENT '交换机型号',
  `subModel` VARCHAR(128) DEFAULT NULL COMMENT '交换机子型号',
  `mpls` VARCHAR(128) DEFAULT NULL COMMENT '是否支持mpls交换机',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##交换机端口
CREATE TABLE  `SwitchPortVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `switchUuid` varchar(32) DEFAULT NULL COMMENT '交换机UUID',
  `portNum` INT(11) DEFAULT NULL COMMENT '该交换机端口编号：1口，2口...',
  `portName` varchar(128) DEFAULT NULL COMMENT '端口名称',
  `label` varchar(128) DEFAULT NULL COMMENT '端口用途：接入/监控',
  #`vlan` INT(11) DEFAULT NULL ,
  #`endVlan` INT(11) DEFAULT NULL ,
  `reuse` TINYINT(32) DEFAULT NULL COMMENT '是否复用',
  `autoAlloc` TINYINT(1) DEFAULT NULL COMMENT '是否自动分配',
  `enabled` TINYINT(1) DEFAULT NULL COMMENT '是否启用',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##交换机VLAN
CREATE TABLE  `SwitchVlanVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `switchUuid` varchar(32) DEFAULT NULL COMMENT '交换机UUID',
  `startVlan` INT(11) DEFAULT NULL COMMENT '起始VLAN',
  `endVlan` INT(11) DEFAULT NULL COMMENT '结束VLAN',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##通道
CREATE TABLE `TunnelVO` (
  `uuid` VARCHAR(32) NOT NULL,
  #`networkTypeUuid` VARCHAR(32) DEFAULT NULL,
  #`projectUuid` varchar(32) DEFAULT NULL,
  #`code` varchar(128) DEFAULT NULL,
  `accountUuid` VARCHAR(32),
  `userUuid` VARCHAR(32),
  `name` varchar(256) DEFAULT NULL,
  `bandWidth` int(11) DEFAULT NULL,
  `vni` int(11) DEFAULT NULL,
  `distance` decimal(10,2) DEFAULT NULL,
  `state` varchar(32) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  #`endpointA` VARCHAR(32) DEFAULT NULL,
  #`endpointB` VARCHAR(32) DEFAULT NULL,
  #`endpointAType` varchar(64) DEFAULT NULL,
  #`endpointBType` varchar(64) DEFAULT NULL,
  `priExclusive` varchar(32) DEFAULT 'false',
  #`alarmed` TINYINT(11) NOT NULL DEFAULT '0',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0',
  `billingExpiredDate` timestamp NULL DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

## 通道端口
CREATE TABLE `TunnelRefSwitchVO` (
  `uuid` VARCHAR(32) NOT NULL ,
  `tunnelUuid` VARCHAR(32) DEFAULT NULL,
  `groupUuid` VARCHAR(32) DEFAULT NULL,
  `endpointUuid` VARCHAR(32) DEFAULT NULL,
  `switchUuid` VARCHAR(32) DEFAULT NULL,
  `switchPortUuid` VARCHAR(32) DEFAULT NULL,
  `switchVlanUuid` VARCHAR(32) DEFAULT NULL,
  `innerVlan` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT '0',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

## 通道关联
CREATE TABLE `TunnelConnectionVO` (
  `uuid` VARCHAR(32) NOT NULL ,
  `tunnelUuid` VARCHAR(32) DEFAULT NULL,
  `tunnelRefSwitchUuidA` VARCHAR(32) DEFAULT NULL,
  `tunnelRefSwitchUuidB` VARCHAR(32) DEFAULT NULL,
  `state` varchar(32) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##速度测试
CREATE TABLE `SpeedRecordsVO` (
  `uuid` VARCHAR(32) NOT NULL,
  `tunnelConnectionUuid` VARCHAR(32) DEFAULT NULL,
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
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##通道监控
CREATE TABLE `TunnelMonitorVO` (
  `uuid` VARCHAR(32) NOT NULL,
  `tunnelConnectionUuid` VARCHAR(32) DEFAULT NULL,
  `monitorAIp` varchar(64) DEFAULT NULL,
  `monitorBIp` varchar(64) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `msg` varchar(1024) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##监控机
CREATE TABLE  `HostVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `name` varchar(128) DEFAULT NULL COMMENT '监控机名称',
  `code` varchar(128) DEFAULT NULL COMMENT '监控机编号',
  `ip` varchar(128) DEFAULT NULL COMMENT '管理IP',
  `username` varchar(128) DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) DEFAULT NULL COMMENT '密码',
  `monitorState` varchar(32) DEFAULT NULL COMMENT '监控状况：已部署，未部署',
  `monitorStatus` varchar(32) DEFAULT NULL COMMENT '监控状态',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##监控机交换机监控
CREATE TABLE `HostSwitchMonitorVO` (
  `uuid` VARCHAR(32) NOT NULL COMMENT 'UUID',
  `hostUuid` VARCHAR(32) DEFAULT NULL COMMENT '监控机UUID',
  `switchUuid` VARCHAR(32) DEFAULT NULL COMMENT '交换机UUID',
  `interfaceName` varchar(128)  DEFAULT NULL COMMENT '接口名称',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

