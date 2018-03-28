use syscxp_tunnel;

## L3 MPLS VPN
CREATE TABLE `L3NetworkVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `number` bigint unsigned AUTO_INCREMENT Unique,
  `accountUuid` VARCHAR(32) COMMENT '分配账户',
  `ownerAccountUuid` VARCHAR(32) NOT NULL COMMENT '所属账户',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `code` varchar(128) NOT NULL COMMENT '客户code，必须为字母或数字',
  `vid` INT(11) NOT NULL UNIQUE COMMENT 'vid，100000以上唯一',
  `type` varchar(32) NOT NULL DEFAULT 'MPLSVPN' COMMENT '网络类型',
  `endpointNum` INT(11) NOT NULL COMMENT '连接点数量',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `duration` int(11) NOT NULL COMMENT '最近一次购买时长',
  `productChargeModel` varchar(32) NOT NULL COMMENT '产品付费方式',
  `maxModifies` int(11) NOT NULL COMMENT '最大调整次数',
  `expireDate` timestamp NULL DEFAULT NULL COMMENT '截止时间',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8;

CREATE TABLE `L3EndpointVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `l3NetworkUuid` VARCHAR(32),
  `endpointUuid` varchar(32) NOT NULL COMMENT '所属连接点',
  `bandwidthOffering` VARCHAR(32) NOT NULL COMMENT '带宽规格',
  `bandwidth` BIGINT NOT NULL COMMENT '带宽',
  `routeType` varchar(32) NOT NULL DEFAULT 'STATIC' COMMENT '路由类型',
  `state` varchar(32) NOT NULL COMMENT '状况',
  `status` varchar(32) NOT NULL DEFAULT 'Connected' COMMENT '状态',
  `maxRouteNum` INT(11) NOT NULL COMMENT '改点允许最大路由数量',
  `localIP` varchar(128) DEFAULT NULL COMMENT '犀思云端 ip',
  `remoteIp` varchar(128) DEFAULT NULL COMMENT '客户端 ip',
  `monitorIp` varchar(128) DEFAULT NULL COMMENT '监控 ip',
  `netmask` varchar(128) DEFAULT NULL COMMENT '子网掩码',
  `ipCidr` varchar(128) DEFAULT NULL COMMENT '互联地址所属网段',
  `interfaceUuid` varchar(32) COMMENT '所属物理接口',
  `switchPortUuid` varchar(32) NOT NULL COMMENT '所属端口',
  `physicalSwitchUuid` varchar(32) DEFAULT NULL COMMENT '物理交换机',
  `vlan` INT(11) NOT NULL COMMENT '端口vlan',
  `rd` varchar(50) NOT NULL DEFAULT '1:1' COMMENT 'rd',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `L3RtVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `l3EndpointUuid` VARCHAR(32) COMMENT '所属L3连接点',
  `impor` varchar(50) NOT NULL DEFAULT '1:1' COMMENT 'import',
  `export` varchar(50) NOT NULL DEFAULT '1:1' COMMENT 'export',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `L3RouteVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `l3EndpointUuid` VARCHAR(32) COMMENT '所属L3连接点',
  `cidr` varchar(50) NOT NULL COMMENT 'cidr',
  `truthCidr` varchar(50) NOT NULL COMMENT '真实网段',
  `nextIp` varchar(50) NOT NULL COMMENT 'nextIp',
  `indexNum` INT(11) NOT NULL COMMENT 'index',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# REST API
CREATE TABLE  `syscxp_tunnel_rest`.`AsyncRestVO` (
  `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
  `requestData` TEXT NOT NULL,
  `state` varchar(32) NOT NULL,
  `result` TEXT DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;