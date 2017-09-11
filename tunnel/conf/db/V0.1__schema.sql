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
CREATE TABLE  `syscxp_tunnel`.`NodeEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `name` varchar(255) NOT NULL  COMMENT '节点名称',
  `code` varchar(128) NOT NULL  COMMENT '节点编号',
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

CREATE VIEW `syscxp_tunnel`.`NodeVO` AS SELECT uuid, name, code, description, contact, telephone, province, city, address, longtitude, latitude, property, status, extensionInfoUuid, lastOpDate, createDate
                        FROM `NodeEO` WHERE deleted = 0;

## 连接点
CREATE TABLE  `syscxp_tunnel`.`EndpointEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `nodeUuid` varchar(32) NOT NULL COMMENT '节点id',
  `name` varchar(255) NOT NULL  COMMENT '连接点名称',
  `code` varchar(128) NOT NULL  COMMENT '连接点编号',
  `endpointType` varchar(128) NOT NULL  COMMENT '连接点类型',
  `enabled` TINYINT(1)  NOT NULL DEFAULT '1' COMMENT '是否启用',
  `openToCustomers` TINYINT(1)  NOT NULL DEFAULT '0' COMMENT '是否对外开放',
  `description` varchar(255)  DEFAULT NULL COMMENT '描述',
  `status` varchar(16) NOT NULL DEFAULT 'NORMAL' COMMENT '状态',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `syscxp_tunnel`.`EndpointVO` AS SELECT uuid, nodeUuid, name, code, endpointType, enabled, openToCustomers, description, status, lastOpDate, createDate
                            FROM `EndpointEO` WHERE deleted = 0;

##交换机型号
CREATE TABLE  `syscxp_tunnel`.`SwitchModelVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `model` VARCHAR(128) NOT NULL COMMENT '交换机型号',
  `subModel` VARCHAR(128) DEFAULT NULL COMMENT '交换机子型号',
  `mpls` TINYINT(1) NOT NULL COMMENT '是否支持mpls交换机',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##交换机归属(物理交换机)
CREATE TABLE  `syscxp_tunnel`.`PhysicalSwitchEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `nodeUuid` varchar(32) NOT NULL COMMENT '节点',
  `switchModelUuid` varchar(32) NOT NULL COMMENT '交换机型号',
  `code` varchar(128) NOT NULL COMMENT '交换机编号',
  `name` varchar(128) NOT NULL COMMENT '交换机名称',
  `brand` varchar(128) NOT NULL COMMENT '交换机品牌',
  `owner` varchar(128) NOT NULL COMMENT '交换机属主',
  `type` varchar(32) NOT NULL COMMENT '交换机类型：接入还是输出',
  `rack` varchar(32) NOT NULL COMMENT '交换机位置',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `mIP` varchar(128) NOT NULL COMMENT '管理IP',
  `localIP` varchar(128) NOT NULL COMMENT '本地IP',
  `username` varchar(128) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `syscxp_tunnel`.`PhysicalSwitchVO` AS SELECT uuid, switchModelUuid, name, code, brand, owner, type, rack, description, mIP, localIP, username, password, lastOpDate, createDate
                                          FROM `PhysicalSwitchEO` WHERE deleted = 0;

##交换机(虚拟交换机)
CREATE TABLE  `syscxp_tunnel`.`SwitchEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `physicalSwitchUuid` varchar(32) NOT NULL COMMENT '所属物理交换机',
  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点UUID',
  `code` varchar(128) NOT NULL COMMENT '交换机编号',
  `name` varchar(128) NOT NULL COMMENT '交换机名称',
  `upperType` varchar(32) NOT NULL COMMENT '上联类型：物理专线/互联网',
  `enabled` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` varchar(16) NOT NULL DEFAULT 'NORMAL' COMMENT '状态',
  `isPrivate` TINYINT(1) NOT NULL COMMENT '1:专用 0:公用',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `syscxp_tunnel`.`SwitchVO` AS SELECT uuid, physicalSwitchUuid, endpointUuid, name, code, upperType, enabled, description, status, isPrivate, lastOpDate, createDate
                                            FROM `SwitchEO` WHERE deleted = 0;


##交换机端口
CREATE TABLE  `syscxp_tunnel`.`SwitchPortVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `switchUuid` varchar(32) NOT NULL COMMENT '交换机UUID',
  `portNum` INT(11) DEFAULT NULL COMMENT '该交换机端口编号：1口，2口...',
  `portName` varchar(128) NOT NULL COMMENT '端口名称',
  `portType` varchar(128) NOT NULL COMMENT '端口类型：光口，电口',
  `isExclusive` TINYINT(1) NOT NULL COMMENT '如果是CLOUD端口，是否独享',
  `enabled` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##交换机VLAN
CREATE TABLE  `syscxp_tunnel`.`SwitchVlanVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `switchUuid` varchar(32) NOT NULL COMMENT '交换机UUID',
  `startVlan` INT(11) NOT NULL COMMENT '起始VLAN',
  `endVlan` INT(11) NOT NULL COMMENT '结束VLAN',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##专有网络
CREATE TABLE  `syscxp_tunnel`.`NetWorkVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `accountUuid` varchar(32) NOT NULL COMMENT '所属账户',
  `name` varchar(128) NOT NULL COMMENT '网络名称',
  `vsi` INT(11) NOT NULL COMMENT 'VSI',
  `monitorCidr` varchar(32) NOT NULL COMMENT '监控网段',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##物理接口
CREATE TABLE  `syscxp_tunnel`.`InterfaceEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `accountUuid` varchar(32) NOT NULL COMMENT '所属账户',
  `name` varchar(128) NOT NULL COMMENT '接口名称',
  `switchPortUuid` varchar(32) NOT NULL COMMENT '对应交换机端口',
  `endpointUuid` varchar(32) NOT NULL COMMENT '对应连接点',
  `bandwidth` int(11) NOT NULL COMMENT '带宽',
  `isExclusive` TINYINT(1) NOT NULL COMMENT '如果是CLOUD端口，是否独享',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `months` int(11) NOT NULL COMMENT '最近一次购买时长',
  `expiredDate` timestamp NOT NULL COMMENT '截止时间',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE VIEW `syscxp_tunnel`.`InterfaceVO` AS SELECT uuid, accountUuid, name, switchPortUuid, endpointUuid, bandwidth, isExclusive, description, months, expiredDate, lastOpDate, createDate
                                          FROM `InterfaceEO` WHERE deleted = 0;

##云专线
CREATE TABLE `syscxp_tunnel`.`TunnelEO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `accountUuid` VARCHAR(32) NOT NULL COMMENT '所属账户',
  `netWorkUuid` VARCHAR(32) NOT NULL COMMENT '所属专有网络',
  `name` varchar(128) NOT NULL COMMENT '通道名称',
  `bandwidth` int(11) NOT NULL COMMENT '带宽',
  `distance` decimal(10,2) DEFAULT NULL COMMENT '距离',
  `state` varchar(32) NOT NULL DEFAULT 'UNPAID' COMMENT '状况:开通，未开通,未支付',
  `status` varchar(32) NOT NULL DEFAULT 'BREAK' COMMENT '状态：正常，中断，异常',
  `interfaceAUuid` VARCHAR(32) NOT NULL COMMENT '选择物理接口A',
  `aVlan` INT(11)  COMMENT 'A点VLAN',
  `enableQinqA` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'A是否开启Qinq',
  `interfaceZUuid` VARCHAR(32) NOT NULL COMMENT '选择物理接口Z',
  `zVlan` INT(11)  COMMENT 'Z点VLAN',
  `enableQinqZ` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Z是否开启Qinq',
  `isMonitor` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否开启监控',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `months` int(11) NOT NULL COMMENT '最近一次购买时长',
  `expiredDate` timestamp DEFAULT NULL COMMENT '截止时间',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE VIEW `syscxp_tunnel`.`TunnelVO` AS SELECT uuid, accountUuid, netWorkUuid, name, bandwidth, distance, state, status, interfaceAUuid, aVlan, enableQinqA, interfaceZUuid, zVlan, enableQinqZ, isMonitor, description, months, expiredDate, lastOpDate, createDate
                                        FROM `TunnelEO` WHERE deleted = 0;

##Qinq模式网段
CREATE TABLE  `syscxp_tunnel`.`QinqVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `tunnelUuid` varchar(32) NOT NULL COMMENT '所属云专线',
  `interfaceUuid` varchar(32) NOT NULL COMMENT '所属物理接口',
  `startVlan` INT(11) NOT NULL COMMENT '起始VLAN',
  `endVlan` INT(11) NOT NULL COMMENT '结束VLAN',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


##速度测试
CREATE TABLE `syscxp_tunnel`.`SpeedRecordsVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `tunnelUuid` VARCHAR(32) DEFAULT NULL,
  `protocol` varchar(32) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `srcDirection` int(11) DEFAULT NULL,
  `dstDirection` int(11) DEFAULT NULL,
  `avgSpeed` int(11) DEFAULT NULL,
  `maxSpeed` int(11) DEFAULT NULL,
  `minSpeed` int(11) DEFAULT NULL,
  `completed` TINYINT(1) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##通道监控
CREATE TABLE `syscxp_tunnel`.`TunnelMonitorVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `tunnelUuid` VARCHAR(32) DEFAULT NULL,
  `monitorAIp` varchar(64) DEFAULT NULL,
  `monitorBIp` varchar(64) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `msg` varchar(1024) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##监控机
CREATE TABLE  `syscxp_tunnel`.`HostEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `name` varchar(128) NOT NULL COMMENT '监控机名称',
  `code` varchar(128) NOT NULL COMMENT '监控机编号',
  `ip` varchar(128) NOT NULL COMMENT '管理IP',
  `username` varchar(128) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `state` varchar(32) NOT NULL DEFAULT 'UNDEPLOYED' COMMENT '监控状况：已部署，未部署',
  `status` varchar(32) DEFAULT NULL COMMENT '监控状态',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `syscxp_tunnel`.`HostVO` AS SELECT uuid, name, code, ip, username, password, state, status, lastOpDate, createDate
                                                     FROM `HostEO` WHERE deleted = 0;

##监控机监控
CREATE TABLE `syscxp_tunnel`.`HostMonitorEO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `hostUuid` VARCHAR(32) NOT NULL COMMENT '监控机UUID',
  `switchPortUuid` VARCHAR(32) NOT NULL COMMENT '交换机UUID',
  `interfaceName` varchar(128)  NOT NULL COMMENT '接口名称',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `syscxp_tunnel`.`HostMonitorVO` AS SELECT uuid, hostUuid, switchPortUuid, interfaceName, lastOpDate, createDate
                                          FROM `HostMonitorEO` WHERE deleted = 0;

