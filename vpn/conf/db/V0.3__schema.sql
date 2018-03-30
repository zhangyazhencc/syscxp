use syscxp_vpn;
## L3 VPN
CREATE TABLE  `syscxp_vpn`.`L3VpnVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`accountUuid` varchar(32) DEFAULT NULL COMMENT '所属账户',
	`hostUuid` varchar(32) NOT NULL COMMENT '物理机',
	`name` varchar(255) NOT NULL COMMENT '名称',
	`description` varchar(255) DEFAULT NULL COMMENT '描述',
	`bandwidthOfferingUuid` VARCHAR(32) NOT NULL COMMENT '带宽',
	`l3NetworkUuid` VARCHAR(32) COMMENT '所属云网络',
	`l3endpointUuid` VARCHAR(32) NOT NULL COMMENT '所属l3连接点',
  `type` varchar(32) NOT NULL DEFAULT 'routetype' COMMENT '客户端模式',
	`port` INT(10) NOT NULL COMMENT 'VPN端口',
	`vlan` INT(10) NOT NULL COMMENT 'vlan',
	`state` VARCHAR(32) DEFAULT NULL COMMENT '启用状态',
	`status` VARCHAR(32) DEFAULT NULL COMMENT '运行状态',
	`duration` int(11) NOT NULL COMMENT '购买时长',
	`clientConf` TEXT DEFAULT NULL COMMENT 'clientConf',
	`sid` VARCHAR(32) NOT NULL COMMENT 'sid',
	`certKey` VARCHAR(32) NOT NULL COMMENT '登录key',
	`payment` VARCHAR(32) NOT NULL COMMENT '支付状态',
	`maxModifies` INT DEFAULT 5 COMMENT '最大调整次数',
	`vpnCertUuid` VARCHAR(32) COMMENT '',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`expireDate` timestamp COMMENT '截止时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8;

CREATE TABLE `L3RouteVO` (
  `uuid` VARCHAR(32) NOT NULL UNIQUE COMMENT 'UUID',
  `L3VpnUuid` VARCHAR(32) COMMENT '所属L3VPN',
  `l3endpointUuid` VARCHAR(32) NOT NULL COMMENT '所属l3连接点',
  `startIp` varchar(128) DEFAULT NULL COMMENT '起始ip',
  `stopIp` varchar(128) DEFAULT NULL COMMENT '终止ip',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

