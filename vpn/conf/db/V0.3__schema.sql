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
	`l3EndpointUuid` VARCHAR(32) NOT NULL COMMENT '所属l3连接点',
  `workMode` varchar(32) DEFAULT NULL COMMENT '客户端模式',
  `startIp` varchar(128) DEFAULT NULL COMMENT '起始ip',
  `stopIp` varchar(128) DEFAULT NULL COMMENT '终止ip',
  `netmask` varchar(128) DEFAULT NULL COMMENT '子网掩码',
  `gateway` varchar(128) DEFAULT NULL COMMENT '网关',
	`port` INT(10) NOT NULL COMMENT 'VPN端口',
	`vlan` INT(10) NOT NULL COMMENT 'vlan',
	`state` VARCHAR(32) DEFAULT NULL COMMENT '启用状态',
	`status` VARCHAR(32) DEFAULT NULL COMMENT '运行状态',
	`duration` int(11) NOT NULL COMMENT '购买时长',
	`clientConf` TEXT DEFAULT NULL COMMENT 'clientConf',
	`secretId` VARCHAR(32) NOT NULL COMMENT 'secretId',
	`secretKey` VARCHAR(32) NOT NULL COMMENT '登录key',
	`payment` VARCHAR(32) NOT NULL COMMENT '支付状态',
	`maxModifies` INT DEFAULT 5 COMMENT '最大调整次数',
	`vpnCertUuid` VARCHAR(32) COMMENT '',
	`expireDate` timestamp NULL COMMENT '截止时间',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8;

ALTER TABLE L3VpnVO ADD CONSTRAINT fkL3VpnVOVpnHostVO FOREIGN KEY (hostUuid) REFERENCES VpnHostVO (uuid) ON DELETE RESTRICT;
ALTER TABLE L3VpnVO ADD CONSTRAINT fkL3VpnVOVpnCertVO FOREIGN KEY (vpnCertUuid) REFERENCES VpnCertVO (uuid) ON DELETE RESTRICT;


# 修改字段名
ALTER TABLE `syscxp_vpn`.`VpnVO` CHANGE `sid` `secretId` VARCHAR(32) NOT NULL;
ALTER TABLE `syscxp_vpn`.`VpnVO` CHANGE `certKey` `secretKey` VARCHAR(32) NOT NULL;
