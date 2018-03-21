use syscxp_tunnel;

CREATE TABLE `syscxp_tunnel`.`SpeedRecordsVO` (
  `uuid` varchar(32) NOT NULL UNIQUE,
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账户uuid',
  `tunnelUuid` varchar(32) NOT NULL COMMENT 'TunnelVO.uuid',
  `srcTunnelMonitorUuid` varchar(32) NOT NULL COMMENT '源监控通道uuid',
  `dstTunnelMonitorUuid` varchar(32) NOT NULL COMMENT '目标监控通道uuid',
  `srcNodeUuid` varchar(32) NOT NULL COMMENT '源节点uuid',
  `dstNodeUuid` varchar(32) NOT NULL COMMENT '目标节点uuid',
  `protocolType` varchar(32) NOT NULL COMMENT '协议',
  `duration` int(11) NOT NULL COMMENT '持续时间(秒)',
  `avgSpeed` int(11) DEFAULT '0' COMMENT '平均速度(k/s)',
  `maxSpeed` int(11) DEFAULT '0' COMMENT '最大速度(k/s)',
  `minSpeed` int(11) DEFAULT '0' COMMENT '最小速度(k/s)',
  `status` varchar(32) NOT NULL DEFAULT 'TESTING' COMMENT '测速状态（COMPLETED\FAILURE\TESTING)',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='监控测速纪录';


CREATE TABLE `syscxp_tunnel`.`SpeedTestTunnelVO` (
  `uuid` varchar(32) NOT NULL UNIQUE,
  `tunnelUuid` varchar(32) NOT NULL COMMENT 'TunnelVO.uuid',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='测速专线列表';


CREATE TABLE IF NOT EXISTS `syscxp_tunnel`.`HostSwitchMonitorEO` (
  `uuid` varchar(32) NOT NULL UNIQUE,
  `hostUuid` varchar(32) NOT NULL COMMENT '主机UUID(HostEO.uuid)',
  `physicalSwitchUuid` VARCHAR(32) NOT NULL COMMENT '物理交换机UUID(PhysicalSwitchVO.uuid)',
  `physicalSwitchPortName` VARCHAR(128) NOT NULL COMMENT '物理交换机端口名称',
  `interfaceName` VARCHAR(128) NOT NULL COMMENT '网卡名称',
  `deleted` varchar(255) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
)ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT '监控主机与交换机接口映射';

create view syscxp_tunnel.HostSwitchMonitorVO as
select uuid,hostUuid,physicalSwitchUuid,physicalSwitchPortName,interfaceName,lastOpDate,createDate
  from syscxp_tunnel.HostSwitchMonitorEO
 where deleted IS NULL;

#######################################################################################################

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
  `jobData` text NOT NULL,
  `jobQueueId` bigint unsigned NOT NULL,
  `resourceUuid` varchar(32) DEFAULT NULL,
  `uniqueResource` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `state` varchar(128) NOT NULL,
  `context` blob DEFAULT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `issuerManagementNodeId` varchar(32) DEFAULT NULL,
  `restartable` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `inDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  `takenDate` timestamp NULL,
  `takenTimes` bigint unsigned DEFAULT 0,
  `doneDate` timestamp NULL,
  `errText` text DEFAULT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# Foreign keys for table JobQueueEntryVO

ALTER TABLE JobQueueEntryVO ADD CONSTRAINT fkJobQueueEntryVOJobQueueVO FOREIGN KEY (jobQueueId) REFERENCES JobQueueVO (id) ON DELETE CASCADE;
ALTER TABLE JobQueueEntryVO ADD CONSTRAINT fkJobQueueEntryVOManagementNodeVO FOREIGN KEY (issuerManagementNodeId) REFERENCES ManagementNodeVO (uuid) ON DELETE SET NULL;

# Foreign keys for table JobQueueVO

ALTER TABLE JobQueueVO ADD CONSTRAINT fkJobQueueVOManagementNodeVO FOREIGN KEY (workerManagementNodeId) REFERENCES ManagementNodeVO (uuid) ON DELETE SET NULL;

#########################################################################################

## 节点
CREATE TABLE `syscxp_tunnel`.`NodeEO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `name` varchar(255) NOT NULL COMMENT '节点名称',
  `code` varchar(128) NOT NULL COMMENT '节点编号',
  `description` varchar(255) DEFAULT NULL COMMENT '节点描述',
  `contact` varchar(128) NOT NULL COMMENT '联系人',
  `telephone` varchar(32) NOT NULL COMMENT '联系人电话',
  `country` varchar(128) NOT NULL COMMENT '国家',
  `province` varchar(128) NOT NULL COMMENT '省',
  `city` varchar(128) NOT NULL COMMENT '市',
  `address` varchar(256) NOT NULL COMMENT '地址',
  `longitude` double(11,6) NOT NULL COMMENT '经度',
  `latitude` double(11,6) NOT NULL COMMENT '纬度',
  `property` varchar(128) NOT NULL COMMENT '节点类型',
  `status` varchar(16) NOT NULL COMMENT '是否开放',
  `deleted` varchar(255) DEFAULT NULL,
  `extensionInfoUuid` varchar(32) DEFAULT NULL COMMENT '节点额外信息',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `syscxp_tunnel`.`NodeVO` AS SELECT uuid, name, code, description, contact, telephone, country, province, city, address, longitude, latitude, property, status, extensionInfoUuid, lastOpDate, createDate
                        FROM `NodeEO` WHERE deleted IS NULL;

## 区域字典表
CREATE TABLE `syscxp_tunnel`.`ZoneVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `name` varchar(128) NOT NULL COMMENT '区域名称',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `syscxp_tunnel`.`ZoneVO` (`uuid`,`name`,`lastOpDate`,`createDate`)
VALUES ('CSJ','长三角','2017-11-02 12:59:56','2017-11-01 10:26:39'),
  ('ZSJ','珠三角','2017-11-02 13:00:01','2017-11-01 10:27:23'),
  ('JJJ','京津冀','2017-11-02 13:00:06','2017-11-01 10:27:50');


##节点区域关系表
CREATE TABLE `syscxp_tunnel`.`ZoneNodeRefVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `nodeUuid` varchar(32) NOT NULL COMMENT '节点UUID',
  `zoneUuid` varchar(32) NOT NULL COMMENT '区域UUID',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


## 连接点
CREATE TABLE  `syscxp_tunnel`.`EndpointEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `nodeUuid` varchar(32) NOT NULL COMMENT '节点id',
  `name` varchar(255) NOT NULL  COMMENT '连接点名称',
  `code` varchar(128) NOT NULL  COMMENT '连接点编号',
  `endpointType` varchar(128) NOT NULL  COMMENT '连接点类型',
  `cloudType` VARCHAR(32) DEFAULT NULL COMMENT '云类型',
  `state` varchar(32) NOT NULL COMMENT '启用|禁用 Enable|Disable',
  `status` varchar(32) NOT NULL COMMENT '开发|未开放 Open|Close',
  `description` varchar(255)  DEFAULT NULL COMMENT '描述',
  `deleted` varchar(255) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE OR REPLACE VIEW `syscxp_tunnel`.`EndpointVO` AS SELECT uuid, nodeUuid, name, code, endpointType, cloudType, state, status, description, lastOpDate, createDate
                            FROM `EndpointEO` WHERE deleted IS NULL;

##互联连接点配置表
CREATE TABLE  `syscxp_tunnel`.`InnerConnectedEndpointVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `endpointUuid` varchar(32) NOT NULL COMMENT '互联连接点',
  `connectedEndpointUuid` VARCHAR(32) NOT NULL COMMENT '目的连接点',
  `name` VARCHAR(128) NOT NULL COMMENT '名称：如高速通道',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `type` varchar(32) NOT NULL COMMENT '交换机类型：SDN还是MPLS',
  `accessType` varchar(32) DEFAULT NULL COMMENT '接入类型：接入还是传输',
  `rack` varchar(32) NOT NULL COMMENT '交换机位置',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `mIP` varchar(128) NOT NULL COMMENT '管理IP',
  `localIP` varchar(128) NOT NULL COMMENT '本地IP',
  `protocol` varchar(32) NOT NULL COMMENT '远程协议',
  `port` INT(11) NOT NULL COMMENT '协议端口号',
  `username` varchar(128) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `deleted` varchar(255) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `syscxp_tunnel`.`PhysicalSwitchVO` AS SELECT uuid, nodeUuid, switchModelUuid, name, code,  owner, type, accessType, rack, description, mIP, localIP, protocol, port, username, password, lastOpDate, createDate
                                          FROM `PhysicalSwitchEO` WHERE deleted IS NULL;

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
  `type` varchar(32) NOT NULL COMMENT '交换机类型：ACCESS,INNER,OUTER',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `state` varchar(32) NOT NULL DEFAULT 'Enabled' COMMENT '状况',
  `status` varchar(32) NOT NULL DEFAULT 'Connected' COMMENT '状态',
  `deleted` varchar(255) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE VIEW `syscxp_tunnel`.`SwitchVO` AS SELECT uuid, physicalSwitchUuid, endpointUuid, code, name, type, description, state, status, lastOpDate, createDate
                                            FROM `SwitchEO` WHERE deleted IS NULL;


##交换机端口
CREATE TABLE  `syscxp_tunnel`.`SwitchPortVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `switchUuid` varchar(32) NOT NULL COMMENT '交换机UUID',
  `portNum` INT(11) DEFAULT NULL COMMENT '该交换机端口编号：1口，2口...',
  `portName` varchar(128) NOT NULL COMMENT '端口名称',
  `portType` varchar(128) NOT NULL COMMENT '端口类型：光口，电口',
  `portAttribute` varchar(128) DEFAULT NULL COMMENT '属性',
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

##物理接口
CREATE TABLE  `syscxp_tunnel`.`InterfaceEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `accountUuid` VARCHAR(32) COMMENT '分配账户',
  `ownerAccountUuid` VARCHAR(32) NOT NULL COMMENT '所属账户',
  `name` varchar(128) NOT NULL COMMENT '接口名称',
  `switchPortUuid` varchar(32) NOT NULL COMMENT '对应交换机端口',
  `endpointUuid` varchar(32) NOT NULL COMMENT '对应连接点',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `state` varchar(32) NOT NULL DEFAULT 'Unpaid' COMMENT '状况:未支付,Up,Down',
  `type` varchar(32) NOT NULL DEFAULT 'TRUNK' COMMENT '用途:TRUNK,ACCESS,QINQ',
  `deleted` varchar(255) DEFAULT NULL,
  `duration` int(11) NOT NULL COMMENT '最近一次购买时长',
  `productChargeModel` varchar(32) NOT NULL COMMENT '产品付费方式',
  `maxModifies` int(11) NOT NULL COMMENT '最大调整次数',
  `expireDate` timestamp NULL DEFAULT NULL COMMENT '截止时间',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE VIEW `syscxp_tunnel`.`InterfaceVO` AS SELECT uuid, accountUuid, ownerAccountUuid, name, switchPortUuid, endpointUuid, description, state, type, duration, productChargeModel, maxModifies, expireDate, lastOpDate, createDate
                                          FROM `InterfaceEO` WHERE deleted IS NULL;

##最后一公里
CREATE TABLE  `syscxp_tunnel`.`EdgeLineEO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `accountUuid` VARCHAR(32) NOT NULL COMMENT '分配账户',
  `interfaceUuid` VARCHAR(32) NOT NULL COMMENT '关联物理接口',
  `endpointUuid` VARCHAR(32) NOT NULL COMMENT '连接点',
  `type` VARCHAR(32) NOT NULL COMMENT '类型',
  `destinationInfo` varchar(255) NOT NULL COMMENT '终点信息',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `state` varchar(32) NOT NULL COMMENT '状况:申请中，已开通',
  `prices` int(11) DEFAULT NULL COMMENT '价格：元/月',
  `deleted` varchar(255) DEFAULT NULL,
  `expireDate` timestamp NULL DEFAULT NULL COMMENT '截止时间',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE VIEW `syscxp_tunnel`.`EdgeLineVO` AS SELECT uuid, accountUuid, interfaceUuid, endpointUuid, type, destinationInfo, description, state, prices, expireDate, lastOpDate, createDate
                                             FROM `EdgeLineEO` WHERE deleted IS NULL;

##云专线
CREATE TABLE `syscxp_tunnel`.`TunnelEO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `accountUuid` VARCHAR(32) COMMENT '分配账户',
  `ownerAccountUuid` VARCHAR(32) NOT NULL COMMENT '所属账户',
  `vsi` INT(11) NOT NULL COMMENT 'VSI',
  `monitorCidr` varchar(128) DEFAULT NULL COMMENT '监控网段',
  `name` varchar(128) NOT NULL COMMENT '通道名称',
  `bandwidthOffering` VARCHAR(32) NOT NULL COMMENT '带宽规格',
  `bandwidth` BIGINT NOT NULL COMMENT '带宽',
  `distance` decimal(10,2) NOT NULL COMMENT '距离',
  `state` varchar(32) NOT NULL DEFAULT 'Unpaid' COMMENT '状况:开通，未开通,未支付',
  `status` varchar(32) NOT NULL DEFAULT 'Disconnected' COMMENT '状态',
  `type` varchar(32) NOT NULL COMMENT '专线类型',
  `innerEndpointUuid` varchar(32) DEFAULT NULL COMMENT '互联连接点',
  `monitorState` varchar(32) NOT NULL DEFAULT 'Disabled' COMMENT '是否开启监控',
  `deleted` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `duration` int(11) NOT NULL COMMENT '最近一次购买时长',
  `productChargeModel` varchar(32) NOT NULL COMMENT '产品付费方式',
  `maxModifies` int(11) NOT NULL COMMENT '最大调整次数',
  `expireDate` timestamp NULL DEFAULT NULL COMMENT '截止时间',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE VIEW `syscxp_tunnel`.`TunnelVO` AS SELECT uuid, accountUuid, ownerAccountUuid, vsi, monitorCidr, name, bandwidthOffering, bandwidth, distance, state, status, type, innerEndpointUuid, monitorState, description, duration, productChargeModel, maxModifies, expireDate, lastOpDate, createDate
                                        FROM `TunnelEO` WHERE deleted IS NULL;
##带宽配置表
CREATE TABLE `syscxp_tunnel`.`BandwidthOfferingVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'uuid',
  `name` varchar(255) NOT NULL UNIQUE COMMENT 'bandwidth offering name',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `bandwidth` BIGINT NOT NULL COMMENT '带宽',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `syscxp_tunnel`.`BandwidthOfferingVO` (`uuid`,`name`,`description`,`bandwidth`,`lastOpDate`,`createDate`)
VALUES ('2G','2G','',2147483648,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('10G','10G','',10737418240,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('20G','20G','',21474836480,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('1G','1G','',1073741824,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('10M','10M','',10485760,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('20M','20M','',20971520,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('100M','100M','',104857600,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('5M','5M','',5242880,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('50M','50M','',52428800,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('500M','500M','',524288000,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('5G','5G','',5368709120,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('200M','200M','',209715200,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('2M','2M','',2097152,'2017-11-01 13:51:31','2017-11-01 13:51:31');

##端口配置表
CREATE TABLE `syscxp_tunnel`.`PortOfferingVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'uuid',
  `name` varchar(255) NOT NULL UNIQUE COMMENT 'port offering name',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `syscxp_tunnel`.`PortOfferingVO` (`uuid`,`name`,`description`,`lastOpDate`,`createDate`)
VALUES ('SFP_10G','光口万兆','光口万兆','2017-11-01 13:51:31','2017-10-30 15:39:20'),
  ('SHARE','共享端口','共享端口','2017-11-01 16:10:18','2017-10-30 15:35:40'),
  ('EXTENDPORT','扩展端口','扩展端口','2017-11-01 16:10:18','2017-10-30 15:35:40'),
  ('SFP_1G','光口千兆','光口千兆','2017-11-01 13:51:36','2017-10-30 15:39:20'),
  ('RJ45_1G','电口千兆','电口千兆','2017-11-01 13:51:39','2017-10-30 15:35:59');


##产品订单生效表
CREATE TABLE `syscxp_tunnel`.`ResourceOrderEffectiveVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `resourceUuid` varchar(32) NOT NULL COMMENT '产品UUID',
  `resourceType` varchar(255) NOT NULL COMMENT '产品表名',
  `orderUuid` varchar(32) NOT NULL COMMENT '订单UUID',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##产品下发任务表
CREATE TABLE `syscxp_tunnel`.`TaskResourceVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `accountUuid` varchar(32) NOT NULL COMMENT '资源所属UUID',
  `resourceUuid` varchar(32) NOT NULL COMMENT '产品UUID',
  `resourceType` varchar(255) NOT NULL COMMENT '产品表名',
  `taskType` varchar(255) NOT NULL COMMENT '任务类型',
  `body` varchar(4000)  DEFAULT NULL COMMENT '请求消息体',
  `result` varchar(4000)  DEFAULT NULL COMMENT '请求返回',
  `status` varchar(32)  NOT NULL COMMENT '任务状态',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##TraceRoute
CREATE TABLE `syscxp_tunnel`.`TraceRouteVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `tunnelUuid` varchar(32) NOT NULL COMMENT 'tunnelUuid',
  `traceSort` int(11) NOT NULL COMMENT '排序',
  `routeIP` varchar(255) NOT NULL COMMENT '路由IP',
  `timesFirst` varchar(255) NOT NULL COMMENT '第一次时间',
  `timesSecond` varchar(255)  NOT NULL COMMENT '第二次时间',
  `timesThird` varchar(255)  NOT NULL COMMENT '第三次时间',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE TraceRouteVO ADD CONSTRAINT fkTraceRouteVOTunnelEO FOREIGN KEY (tunnelUuid) REFERENCES TunnelEO (uuid) ON DELETE CASCADE;


CREATE TABLE  `syscxp_tunnel`.`ResourceMotifyRecordVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `resourceUuid` varchar(32) NOT NULL COMMENT 'ResourceUuid',
  `resourceType` varchar(32) NOT NULL,
  `motifyType` varchar(32) NOT NULL COMMENT '升级、降级',
  `opAccountUuid` varchar(32) NOT NULL COMMENT '操作人',
  `opUserUuid` varchar(32) DEFAULT NULL COMMENT '操作人',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##云专线端口信息表
CREATE TABLE  `syscxp_tunnel`.`TunnelSwitchPortVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `tunnelUuid` varchar(32) NOT NULL COMMENT '所属云专线',
  `interfaceUuid` varchar(32) COMMENT '所属物理接口',
  `endpointUuid` varchar(32) NOT NULL COMMENT '所属连接点',
  `switchPortUuid` varchar(32) NOT NULL COMMENT '所属端口',
  `type` varchar(32) NOT NULL DEFAULT 'TRUNK' COMMENT '用途:TRUNK,ACCESS,QINQ',
  `vlan` INT(11) NOT NULL COMMENT '端口外部vlan',
  `sortTag` varchar(32) NOT NULL COMMENT '排序',
  `physicalSwitchUuid` varchar(32) DEFAULT NULL COMMENT '物理交换机',
  `ownerMplsSwitchUuid` varchar(32) DEFAULT NULL COMMENT 'MPLS交换机',
  `peerMplsSwitchUuid` varchar(32) DEFAULT NULL COMMENT '对端MPLS交换机',
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

############################################################################################################################
##########################################监控##############################################################################
##通道监控
DROP TABLE IF EXISTS TunnelMonitorVO;
CREATE TABLE IF NOT EXISTS `syscxp_tunnel`.`TunnelMonitorVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'uuid',
  `tunnelUuid` VARCHAR(32) NOT NULL COMMENT '通道uuid(TunnelVO.uuid)',
  `tunnelSwitchPortUuid` VARCHAR(32) NOT NULL COMMENT '通道交换机uuid(TunnelSwitchPortVO.uuid)',
  `hostUuid` varchar(32) NOT NULL COMMENT '监控主机uuid(HostVO.uuid)',
  `monitorIp` varchar(64) NOT NULL COMMENT '监控IP',
  `msg` varchar(1024) COMMENT '消息',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '通道监控';


##监控机
CREATE TABLE `HostEO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `name` varchar(128) NOT NULL COMMENT '监控机名称',
  `code` varchar(128) NOT NULL COMMENT '监控机编号',
  `hostIp` varchar(128) DEFAULT NULL,
  `hostType` VARCHAR(128) NOT NULL COMMENT 'host类型',
  `position` varchar(256) NOT NULL COMMENT '位置',
  `state` varchar(32) NOT NULL DEFAULT 'Enabled',
  `status` varchar(32) NOT NULL DEFAULT 'Connected',
  `deleted` varchar(255) DEFAULT NULL,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '监控主机';

CREATE OR REPLACE
VIEW `syscxp_tunnel`.`HostVO` AS
    SELECT
        `syscxp_tunnel`.`HostEO`.`uuid` AS `uuid`,
        `syscxp_tunnel`.`HostEO`.`name` AS `name`,
        `syscxp_tunnel`.`HostEO`.`code` AS `code`,
        `syscxp_tunnel`.`HostEO`.`hostIp` AS `hostIp`,
        `syscxp_tunnel`.`HostEO`.`position` AS `position`,
        `syscxp_tunnel`.`HostEO`.`hostType` AS `hostType`,
        `syscxp_tunnel`.`HostEO`.`state` AS `state`,
        `syscxp_tunnel`.`HostEO`.`status` AS `status`,
        `syscxp_tunnel`.`HostEO`.`lastOpDate` AS `lastOpDate`,
        `syscxp_tunnel`.`HostEO`.`createDate` AS `createDate`
    FROM
        `syscxp_tunnel`.`HostEO`
    WHERE
        (`syscxp_tunnel`.`HostEO`.deleted IS NULL);

##阿里云边界路由器
CREATE TABLE `syscxp_tunnel`.`AliEdgeRouterEO` (
	  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
	  `tunnelUuid` VARCHAR(32) NOT NULL COMMENT '云专线id',
	  `accountUuid` VARCHAR(32) NOT NULL COMMENT '所属账户',
	  `aliAccountUuid` VARCHAR(64) NOT NULL COMMENT '阿里云用户id',
	  `aliRegionId` VARCHAR(64) NOT NULL COMMENT '阿里云区域',
	  `name` varchar(128) DEFAULT NULL COMMENT '边界路由器名字',
	  `description` varchar(255) DEFAULT NULL COMMENT '描述',
	  `vbrUuid` varchar(64) NOT NULL COMMENT '虚拟边界路由器id',
	  `physicalLineUuid` varchar(32) NOT NULL COMMENT '物理专线id',
	  `status` VARCHAR(128) NOT NULL COMMENT '边界路由器状态',
	  `isCreateFlag` tinyint(1) unsigned DEFAULT 0 COMMENT '是否确认创建',
	  `vlan` int(11) NOT NULL COMMENT '端口号',
	  `deleted` varchar(255) DEFAULT NULL COMMENT '是否删除',
	  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE VIEW `syscxp_tunnel`.`AliEdgeRouterVO` AS SELECT uuid, tunnelUuid, accountUuid, aliAccountUuid, aliRegionId, name, description, vbrUuid, physicalLineUuid, vlan, status, isCreateFlag, lastOpDate, createDate FROM `AliEdgeRouterEO` WHERE deleted is null;
ALTER TABLE AliEdgeRouterEO ADD CONSTRAINT fkAliEdgeRouterEOTunnelEO FOREIGN KEY (tunnelUuid) REFERENCES TunnelEO (uuid) ON DELETE CASCADE;

##配置表
CREATE TABLE `syscxp_tunnel`.`AliEdgeRouterConfigVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `aliRegionId` VARCHAR(32) NOT NULL COMMENT '区域id',
  `aliRegionName` VARCHAR(32) NOT NULL COMMENT '区域Name',
  `physicalLineUuid` varchar(32) DEFAULT NULL COMMENT '物理专线id',
  `switchPortUuid` VARCHAR(32) NOT NULL COMMENT '交换机接口id',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##用户表
CREATE TABLE `syscxp_tunnel`.`AliUserVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `accountUuid` VARCHAR(32) NOT NULL COMMENT '账户id',
  `aliAccountUuid` VARCHAR(64) NOT NULL COMMENT '阿里云用户id',
  `AliAccessKeyID` varchar(32) DEFAULT NULL COMMENT 'AliAccessKeyID',
  `AliAccessKeySecret` VARCHAR(32) NOT NULL COMMENT 'AliAccessKeySecret',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

## 云配置表
CREATE TABLE `syscxp_tunnel`.`CloudVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `name` VARCHAR(64) NOT NULL COMMENT '名称',
  `description` varchar(32) DEFAULT NULL COMMENT '描述',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `syscxp_tunnel`.`CloudVO` (`uuid`,`name`,`description`,`lastOpDate`,`createDate`)
VALUES ('Syscloud','犀思互联云','','2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('AliYun','阿里云','','2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('Tencent','腾讯云','','2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('Huawei','华为云','','2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('Baidu','百度云','','2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('Ksyun','金山云','','2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('JD','京东云','','2017-11-01 13:51:31','2017-11-01 13:51:31'),
  ('UCloud','UCloud','','2017-11-01 13:51:31','2017-11-01 13:51:31');

CREATE TABLE  `syscxp_tunnel`.`MonitorHostVO` (
	`uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'host uuid',
	`nodeUuid` varchar(32) DEFAULT NULL COMMENT '节点ID(NodeEO.uuid)',
	`username` varchar(128) NOT NULL COMMENT '用户名',
	`password` varchar(128) NOT NULL COMMENT '密码',
	`sshPort` INT NOT NULL DEFAULT 22 COMMENT 'ssh端口',
	`monitorType` VARCHAR(32) DEFAULT 'TUNNEL',
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

########################################################
#解决方案#
DROP TABLE IF EXISTS `SolutionVO`;
CREATE TABLE `SolutionVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `accountUuid` varchar(32) NOT  NULL COMMENT '账户uuid',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `totalCost` decimal(12,4) DEFAULT 0 COMMENT '预估费用',
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
  `cost` decimal(12,4) DEFAULT 0 COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
  `portOfferingUuid` varchar(32) NOT NULL COMMENT '端口规格(类型)',
  PRIMARY KEY  (`uuid`),
  CONSTRAINT `fkSolutionInterfaceVO` FOREIGN KEY (`solutionUuid`) REFERENCES `SolutionVO` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#云专线#
DROP TABLE IF EXISTS `SolutionTunnelVO`;
CREATE TABLE `SolutionTunnelVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `name` varchar(128) COMMENT '名称',
  `cost` decimal(12,4) DEFAULT 0 COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  `isShareA` TINYINT(1)  NOT NULL DEFAULT '1' COMMENT 'A端是否共享端口',
  `isShareZ` TINYINT(1)  NOT NULL DEFAULT '1' COMMENT 'Z端是否共享端口',
  `endpointUuidA` varchar(32) NOT NULL COMMENT '连接点A',
  `endpointUuidZ` varchar(32) NOT NULL COMMENT '连接点Z',
  `bandwidthOfferingUuid` varchar(32) NOT NULL COMMENT '带宽Uuid',
  `innerConnectedEndpointUuid` varchar(32) DEFAULT NULL COMMENT '中间点UUID',
  PRIMARY KEY  (`uuid`),
  CONSTRAINT `fkSolutionTunnelVO` FOREIGN KEY (`solutionUuid`) REFERENCES `SolutionVO` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#VPN网关#
DROP TABLE IF EXISTS `SolutionVpnVO`;
CREATE TABLE SolutionVpnVO (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `solutionUuid` varchar(32) NOT NULL COMMENT '方案UUID',
  `name` varchar(128) COMMENT '名称',
  `cost` decimal(12,4) DEFAULT 0 COMMENT '费用',
  `productChargeModel` varchar(32) NOT NULL COMMENT '付费方式',
  `duration` int(11) NOT NULL COMMENT '购买时长',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  `solutionTunnelUuid` varchar(32) NOT NULL COMMENT '专线',
  `endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
  `bandwidthOfferingUuid` varchar(32) NOT NULL COMMENT '带宽Uuid',
  PRIMARY KEY  (`uuid`),
  CONSTRAINT `fkSolutionVpnVO` FOREIGN KEY (`solutionUuid`) REFERENCES `SolutionVO` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE SolutionVpnVO ADD CONSTRAINT fkSolutionVpnVOSolutionTunnelVO FOREIGN KEY (solutionTunnelUuid) REFERENCES SolutionTunnelVO (uuid) ON UPDATE RESTRICT ON DELETE CASCADE;

########################################################

CREATE INDEX idxTaskResourceVOcreateDate ON TaskResourceVO (lastOpDate);


#######################################################

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

ALTER TABLE `syscxp_tunnel`.`TunnelEO` ADD COLUMN `number` bigint unsigned AUTO_INCREMENT Unique, AUTO_INCREMENT=5000;
DROP VIEW IF EXISTS `syscxp_tunnel`.`TunnelVO`;
CREATE VIEW `syscxp_tunnel`.`TunnelVO` AS SELECT uuid, `number`, accountUuid, ownerAccountUuid, vsi, monitorCidr, name, bandwidthOffering, bandwidth, distance, state, status, type, innerEndpointUuid, monitorState, description, duration, productChargeModel, maxModifies, expireDate, lastOpDate, createDate FROM `TunnelEO` WHERE deleted IS NULL;

ALTER TABLE `syscxp_tunnel`.`InterfaceEO` ADD COLUMN `number` bigint unsigned AUTO_INCREMENT Unique, AUTO_INCREMENT=1000;
DROP VIEW IF EXISTS `syscxp_tunnel`.`InterfaceVO`;
CREATE VIEW `syscxp_tunnel`.`InterfaceVO` AS SELECT uuid, `number`, accountUuid, ownerAccountUuid, name, switchPortUuid, endpointUuid, description, state, type, duration, productChargeModel, maxModifies, expireDate, lastOpDate, createDate FROM `InterfaceEO` WHERE deleted IS NULL;

ALTER TABLE `syscxp_tunnel`.`EdgeLineEO` ADD COLUMN `number` bigint unsigned AUTO_INCREMENT Unique, AUTO_INCREMENT=1000;
DROP VIEW IF EXISTS `syscxp_tunnel`.`EdgeLineVO`;
CREATE VIEW `syscxp_tunnel`.`EdgeLineVO` AS SELECT uuid, `number`, accountUuid, interfaceUuid, endpointUuid, type, destinationInfo, description, state, prices, expireDate, lastOpDate, createDate FROM `EdgeLineEO` WHERE deleted IS NULL;

