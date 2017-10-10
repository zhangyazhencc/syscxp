
CREATE TABLE IF NOT EXISTS TunnelMonitorInterfaceVO (
  `uuid` VARCHAR(32) NOT NULL COMMENT '主键',
  `TunnelMonitorUuid` VARCHAR(32) NOT NULL COMMENT '监控通道uuid(TunnelMonitorVO.uuid)',
  `interfaceType` VARCHAR(32) NOT NULL COMMENT '类型("A","Z")',
  `hostUuid` VARCHAR(32) NOT NULL COMMENT '监控主机UUID(HostVO.uuid)',
  `monitorIp` VARCHAR(64) NOT NULL COMMENT '监控IP(NetworkVO.monitorCidr)',
  `lastOpDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`))
ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT '监控通道两端信息表';

ALTER TABLE TunnelMonitorInterfaceVO ADD interfaceUuid varchar(32) COMMENT '' AFTER interfaceType;

CREATE TABLE `SpeedRecordsVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'uuid',
  `tunnelUuid` varchar(32) NOT NULL COMMENT 'TunnelVO.uuid',
  `srcHostUuid` varchar(32) NOT NULL COMMENT '源监控机uuid',
  `srcMonitorIp` varchar(32) NOT NULL COMMENT '源监控IP',
  `dstHostUuid` varchar(32) NOT NULL COMMENT '目标监控机uuid',
  `dstMonitorIp` varchar(32) NOT NULL COMMENT '目标监控IP',
  `protocolType` varchar(32) NOT NULL COMMENT '协议',
  `duration` int(11) NOT NULL COMMENT '持续时间(秒)',
  `avgSpeed` int(11) DEFAULT '0' COMMENT '平均速度(k/s)',
  `maxSpeed` int(11) DEFAULT '0' COMMENT '最大速度(k/s)',
  `minSpeed` int(11) DEFAULT '0' COMMENT '最小速度(k/s)',
  `completed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '完成标识 0:未完成 1:已完成',
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='监控测速纪录';

CREATE TABLE IF NOT EXISTS `syscxp_tunnel`.`HostSwitchMonitorEO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `hostUuid` varchar(32) NOT NULL COMMENT '主机UUID(HostEO.uuid)',
  `physicalSwitchUuid` VARCHAR(32) NOT NULL COMMENT '物理交换机UUID(PhysicalSwitchVO.uuid)',
  `physicalSwitchPortName` VARCHAR(128) NOT NULL COMMENT '物理交换机端口名称',
  `interfaceName` VARCHAR(128) NOT NULL COMMENT '网卡名称',
  `deleted` INT(11) NOT NULL DEFAULT '0',
  `lastOpDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE current_timestamp COMMENT '最后一次操作时间',
  `createDate` TIMESTAMP NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`uuid`)
  )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT '监控主机与交换机接口映射';

create view syscxp_tunnel.HostSwitchMonitorVO as
select uuid,hostUuid,physicalSwitchUuid,physicalSwitchPortName,interfaceName,lastOpDate,createDate
  from syscxp_tunnel.HostSwitchMonitorEO
 where deleted = 0;

#######################################################################################################
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
CREATE TABLE `NodeEO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `name` varchar(255) NOT NULL COMMENT '节点名称',
  `code` varchar(128) NOT NULL COMMENT '节点编号',
  `description` varchar(255) DEFAULT NULL COMMENT '节点描述',
  `contact` varchar(128) NOT NULL COMMENT '联系人',
  `telephone` varchar(32) NOT NULL COMMENT '联系人电话',
  `province` varchar(128) NOT NULL COMMENT '省',
  `city` varchar(128) NOT NULL COMMENT '市',
  `address` varchar(256) NOT NULL COMMENT '地址',
  `longtitude` double(11,6) NOT NULL COMMENT '经度',
  `latitude` double(11,6) NOT NULL COMMENT '纬度',
  `property` varchar(128) NOT NULL COMMENT '节点类型',
  `status` varchar(16) NOT NULL COMMENT '是否开放',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `extensionInfoUuid` varchar(32) DEFAULT NULL COMMENT '节点额外信息',
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '网络节点';

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
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `syscxp_tunnel`.`EndpointVO` AS SELECT uuid, nodeUuid, name, code, endpointType, enabled, openToCustomers, description, lastOpDate, createDate
                            FROM `EndpointEO` WHERE deleted = 0;

##交换机型号
CREATE TABLE  `syscxp_tunnel`.`SwitchModelVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `brand` varchar(128) NOT NULL COMMENT '交换机品牌',
  `model` VARCHAR(128) NOT NULL COMMENT '交换机型号',
  `subModel` VARCHAR(128) DEFAULT NULL COMMENT '交换机子型号',
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
  `owner` varchar(128) NOT NULL COMMENT '交换机属主',
  `type` varchar(32) NOT NULL COMMENT '交换机类型：接入还是传输',
  `accessType` varchar(32) DEFAULT NULL COMMENT '接入类型：SDN还是MPLS',
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

CREATE VIEW `syscxp_tunnel`.`PhysicalSwitchVO` AS SELECT uuid, nodeUuid, switchModelUuid, name, code,  owner, type, accessType, rack, description, mIP, localIP, username, password, lastOpDate, createDate
                                          FROM `PhysicalSwitchEO` WHERE deleted = 0;

##物理交换机上联
CREATE TABLE  `syscxp_tunnel`.`PhysicalSwitchUpLinkRefVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `physicalSwitchUuid` varchar(32) NOT NULL COMMENT '目标物理交换机',
  `portName` varchar(128) NOT NULL COMMENT '目标交换机端口名称',
  `uplinkPhysicalSwitchUuid` varchar(32) NOT NULL COMMENT '上联物理交换机',
  `uplinkPhysicalSwitchPortName` varchar(128) NOT NULL COMMENT '上联交换机端口名称',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##交换机(虚拟交换机)
CREATE TABLE  `syscxp_tunnel`.`SwitchEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `physicalSwitchUuid` varchar(32) NOT NULL COMMENT '所属物理交换机',
  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点UUID',
  `code` varchar(128) NOT NULL COMMENT '交换机编号',
  `name` varchar(128) NOT NULL COMMENT '交换机名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `state` varchar(32) NOT NULL DEFAULT 'Enabled' COMMENT '状况',
  `status` varchar(32) NOT NULL DEFAULT 'Connected' COMMENT '状态',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `syscxp_tunnel`.`SwitchVO` AS SELECT uuid, physicalSwitchUuid, endpointUuid, name, code, description, state, status, lastOpDate, createDate
                                            FROM `SwitchEO` WHERE deleted = 0;


##交换机端口
CREATE TABLE  `syscxp_tunnel`.`SwitchPortVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `switchUuid` varchar(32) NOT NULL COMMENT '交换机UUID',
  `portNum` INT(11) DEFAULT NULL COMMENT '该交换机端口编号：1口，2口...',
  `portName` varchar(128) NOT NULL COMMENT '端口名称',
  `portType` varchar(128) NOT NULL COMMENT '端口类型：光口，电口',
  `portAttribute` varchar(128) NOT NULL COMMENT '独享：Exclusive 共享：Shared',
  `autoAllot` TINYINT(1)  NOT NULL DEFAULT '1' COMMENT '是否自动分配',
  `state` varchar(128) NOT NULL DEFAULT 'Enabled' COMMENT '是否启用',
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
CREATE TABLE  `syscxp_tunnel`.`NetworkVO` (
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
  `bandwidth` BIGINT NOT NULL COMMENT '带宽',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `state` varchar(32) NOT NULL DEFAULT 'Unpaid' COMMENT '状况:已支付,未支付',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `duration` int(11) NOT NULL COMMENT '最近一次购买时长',
  `productChargeModel` varchar(32) NOT NULL COMMENT '产品付费方式',
  `maxModifies` int(11) NOT NULL COMMENT '最大调整次数',
  `expiredDate` timestamp COMMENT '截止时间',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE VIEW `syscxp_tunnel`.`InterfaceVO` AS SELECT uuid, accountUuid, name, switchPortUuid, endpointUuid, bandwidth, description, state, duration, productChargeModel, maxModifies, expiredDate, lastOpDate, createDate
                                          FROM `InterfaceEO` WHERE deleted = 0;

##云专线
CREATE TABLE `syscxp_tunnel`.`TunnelEO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `accountUuid` VARCHAR(32) NOT NULL COMMENT '所属账户',
  `networkUuid` VARCHAR(32) NOT NULL COMMENT '所属专有网络',
  `name` varchar(128) NOT NULL COMMENT '通道名称',
  `bandwidth` BIGINT NOT NULL COMMENT '带宽',
  `distance` decimal(10,2) NOT NULL COMMENT '距离',
  `state` varchar(32) NOT NULL DEFAULT 'Unpaid' COMMENT '状况:开通，未开通,未支付',
  `status` varchar(32) NOT NULL DEFAULT 'Disconnected' COMMENT '状态',
  `monitorState` varchar(32) NOT NULL DEFAULT 'Disabled' COMMENT '是否开启监控',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `duration` int(11) NOT NULL COMMENT '最近一次购买时长',
  `productChargeModel` varchar(32) NOT NULL COMMENT '产品付费方式',
  `maxModifies` int(11) NOT NULL COMMENT '最大调整次数',
  `expiredDate` timestamp COMMENT '截止时间',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE VIEW `syscxp_tunnel`.`TunnelVO` AS SELECT uuid, accountUuid, networkUuid, name, bandwidth, distance, state, status, monitorState, description, duration, productChargeModel, maxModifies, expiredDate, lastOpDate, createDate
                                        FROM `TunnelEO` WHERE deleted = 0;

CREATE TABLE  `syscxp_tunnel`.`InterfaceMotifyRecordVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `interfaceUuid` varchar(32) NOT NULL COMMENT '物理接口Uuid',
  `motifyType` varchar(32) NOT NULL COMMENT '升级、降级',
  `opAccountUuid` varchar(32) NOT NULL UNIQUE COMMENT '操作人',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_tunnel`.`TunnelMotifyRecordVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `tunnelUuid` varchar(32) NOT NULL COMMENT '通道Uuid',
  `motifyType` varchar(32) NOT NULL COMMENT '升级、降级',
  `opAccountUuid` varchar(32) NOT NULL UNIQUE COMMENT '操作人',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##监控网段字典表
CREATE TABLE `syscxp_tunnel`.`MonitorCidrVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `monitorCidr` VARCHAR(32) NOT NULL COMMENT '监控网段IP',
  `startAddress` VARCHAR(32) NOT NULL COMMENT '起始网络位',
  `endAddress` VARCHAR(32) NOT NULL COMMENT '最后广播位',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##云专线端口信息表
CREATE TABLE  `syscxp_tunnel`.`TunnelInterfaceVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `tunnelUuid` varchar(32) NOT NULL COMMENT '所属云专线',
  `interfaceUuid` varchar(32) NOT NULL COMMENT '所属物理接口',
  `vlan` INT(11) NOT NULL COMMENT '端口外部vlan',
  `sortTag` varchar(32) NOT NULL COMMENT '排序',
  `qinqState` varchar(32) NOT NULL DEFAULT 'Disabled' COMMENT '是否开启qinq,共享为Disabled,独享才可以启用',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##Qinq模式网段
CREATE TABLE  `syscxp_tunnel`.`QinqVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `tunnelUuid` varchar(32) NOT NULL COMMENT '所属云专线',
  `startVlan` INT(11) NOT NULL COMMENT '起始VLAN',
  `endVlan` INT(11) NOT NULL COMMENT '结束VLAN',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##通道监控
CREATE TABLE IF NOT EXISTS `syscxp_tunnel`.`TunnelMonitorVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'uuid',
  `tunnelUuid` VARCHAR(32) NOT NULL COMMENT '通道uuid(TunnelVO.uuid)',
  `hostAUuid` VARCHAR(32) NOT NULL COMMENT 'A端监控主机(HostVO.uuid)',
  `monitorAIp` varchar(64) NOT NULL COMMENT 'A端监控IP',
  `hostZUuid` varchar(64) NOT NULL COMMENT 'Z端监控主机(HostVO.uuid)',
  `monitorZIp` varchar(64) NOT NULL COMMENT 'Z端监控IP',
  `status` varchar(32) NOT NULL COMMENT '状态(NORMAL,APPLYING,TERMINATED',
  `msg` varchar(1024) COMMENT '消息',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COMMENT '通道监控';

##监控机
CREATE TABLE `HostEO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `nodeUuid` varchar(32) DEFAULT NULL COMMENT '节点ID(NodeEO.uuid)',
  `name` varchar(128) NOT NULL COMMENT '监控机名称',
  `code` varchar(128) NOT NULL COMMENT '监控机编号',
  `hostIp` varchar(128) DEFAULT NULL,
  `username` varchar(128) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `state` varchar(32) NOT NULL DEFAULT 'Undeployed' COMMENT '监控状况：已部署，未部署',
  `status` varchar(32) NOT NULL DEFAULT 'Connected' COMMENT '监控状态',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '监控主机';

CREATE OR REPLACE
VIEW `syscxp_tunnel`.`HostVO` AS
    SELECT
        `syscxp_tunnel`.`HostEO`.`uuid` AS `uuid`,
        `syscxp_tunnel`.`HostEO`.`nodeUuid` AS `nodeUuid`,
        `syscxp_tunnel`.`HostEO`.`name` AS `name`,
        `syscxp_tunnel`.`HostEO`.`code` AS `code`,
        `syscxp_tunnel`.`HostEO`.`hostIp` AS `hostIp`,
        `syscxp_tunnel`.`HostEO`.`username` AS `username`,
        `syscxp_tunnel`.`HostEO`.`password` AS `password`,
        `syscxp_tunnel`.`HostEO`.`state` AS `state`,
        `syscxp_tunnel`.`HostEO`.`status` AS `status`,
        `syscxp_tunnel`.`HostEO`.`lastOpDate` AS `lastOpDate`,
        `syscxp_tunnel`.`HostEO`.`createDate` AS `createDate`
    FROM
        `syscxp_tunnel`.`HostEO`
    WHERE
        (`syscxp_tunnel`.`HostEO`.`deleted` = 0);

alter table syscxp_tunnel.HostEO add nodeUuid varchar(32) comment '节点ID(NodeEO.uuid)' after uuid;


##阿里云边界路由器
CREATE TABLE `syscxp_tunnel`.`AliEdgeRouterEO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `tunnelUuid` VARCHAR(32) NOT NULL COMMENT '云专线id',
  `accountUuid` VARCHAR(32) NOT NULL COMMENT '所属账户',
  `aliAccountUuid` VARCHAR(64) NOT NULL COMMENT '阿里云用户id',
  `aliRegionId` VARCHAR(64) NOT NULL COMMENT '阿里云区域',
  `name` varchar(128) NOT NULL COMMENT '边界路由器名字',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `vbrUuid` varchar(64) DEFAULT NULL COMMENT '虚拟边界路由器id',
  `physicalLineUuid` varchar(32) DEFAULT NULL COMMENT '物理专线id',
  `vlan` int(11) NOT NULL COMMENT '端口号',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE VIEW `syscxp_tunnel`.`ali_edge_routerVO` AS SELECT uuid, tunnelid, aliuuid, area, name, description, vbrid, physicalid, vlanid, lastOpDate, createDate
                                        FROM `ali_edge_routerEO` WHERE deleted = 0;

##配置表
CREATE TABLE `syscxp_tunnel`.`AliEdgeRouterConfigVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `aliRegionId` VARCHAR(32) NOT NULL COMMENT '区域id',
  `physicalLineUuid` varchar(32) DEFAULT NULL COMMENT '物理专线id',
  `switchportid` VARCHAR(32) NOT NULL COMMENT '交换机接口id',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##用户表
CREATE TABLE `syscxp_tunnel`.`AliUserVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `AccountUuid` VARCHAR(32) NOT NULL COMMENT '账户id',
  `AliAccessKeyID` varchar(32) DEFAULT NULL COMMENT 'AliAccessKeyID',
  `AliAccessKeySecret` VARCHAR(32) NOT NULL COMMENT 'AliAccessKeySecret',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
