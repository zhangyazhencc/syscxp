CREATE DATABASE /*!32312 IF NOT EXISTS*/`syscxp_vpn` /*!40100 DEFAULT CHARACTER SET utf8 */;

use syscxp_vpn;

CREATE TABLE  `syscxp_vpn`.`ManagementNodeVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `hostName` varchar(255) DEFAULT NULL,
    `port` int unsigned DEFAULT NULL,
    `state` varchar(128) NOT NULL,
    `joinDate` timestamp DEFAULT CURRENT_TIMESTAMP,
    `heartBeat` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`GlobalConfigVO` (
    `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `description` varchar(1024) DEFAULT NULL,
    `category` varchar(64) NOT NULL,
    `defaultValue` text DEFAULT NULL,
    `value` text DEFAULT NULL,
    PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`JobQueueVO` (
    `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
    `name` varchar(255) NOT NULL UNIQUE,
    `owner` varchar(255) DEFAULT NULL,
    `workerManagementNodeId` varchar(32) DEFAULT NULL,
    `takenDate` timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`JobQueueEntryVO` (
    `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `jobQueueId` bigint unsigned NOT NULL,
    `state` varchar(128) NOT NULL,
    `context` blob DEFAULT NULL,
    `owner` varchar(255) DEFAULT NULL,
    `issuerManagementNodeId` varchar(32) DEFAULT NULL,
    `restartable` tinyint(1) unsigned NOT NULL DEFAULT 0,
    `inDate` timestamp DEFAULT CURRENT_TIMESTAMP,
    `doneDate` timestamp,
    `errText` text DEFAULT NULL,
    PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# Foreign keys for table JobQueueEntryVO

ALTER TABLE JobQueueEntryVO ADD CONSTRAINT fkJobQueueEntryVOJobQueueVO FOREIGN KEY (jobQueueId) REFERENCES JobQueueVO (id) ON DELETE CASCADE;
ALTER TABLE JobQueueEntryVO ADD CONSTRAINT fkJobQueueEntryVOManagementNodeVO FOREIGN KEY (issuerManagementNodeId) REFERENCES ManagementNodeVO (uuid) ON DELETE SET NULL;

# Foreign keys for table JobQueueVO

ALTER TABLE JobQueueVO ADD CONSTRAINT fkJobQueueVOManagementNodeVO FOREIGN KEY (workerManagementNodeId) REFERENCES ManagementNodeVO (uuid) ON DELETE SET NULL;


CREATE TABLE  `syscxp_vpn`.`VpnVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`accountUuid` varchar(32) NOT NULL COMMENT '所属账户',
	`hostUuid` varchar(32) NOT NULL COMMENT '物理机',
	`name` varchar(255) NOT NULL COMMENT '名称',
	`description` varchar(255) DEFAULT NULL COMMENT '描述',
	`bandwidthOfferingUuid` VARCHAR(32) NOT NULL COMMENT '带宽',
	`interfaceUuid` VARCHAR(32) NOT NULL COMMENT '接口uuid',
	`port` INT(10) NOT NULL COMMENT 'VPN端口',
	`vlan` INT(10) NOT NULL COMMENT 'vlan',
	`state` VARCHAR(32) DEFAULT NULL COMMENT '启用状态',
	`status` VARCHAR(32) DEFAULT NULL COMMENT '运行状态',
	`duration` int(11) NOT NULL COMMENT '购买时长',
	`clientConf` TEXT DEFAULT NULL COMMENT 'clientConf',
	`sid` VARCHAR(32) NOT NULL COMMENT 'sid',
	`certKey` VARCHAR(32) NOT NULL COMMENT 'cert-key',
	`payment` VARCHAR(32) NOT NULL COMMENT '支付状态',
	`maxModifies` INT DEFAULT 5 COMMENT '最大调整次数',
	`vpnCertUuid` VARCHAR(32) COMMENT '',
	`expireDate` timestamp COMMENT '截止时间',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`VpnCertVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`accountUuid` varchar(32) NOT NULL COMMENT 'VPN',
	`name` VARCHAR(64) NOT NULL COMMENT '',
	`description` varchar(255) DEFAULT NULL COMMENT '描述',
	`caCert` TEXT DEFAULT NULL COMMENT '数字证书',
	`caKey` TEXT DEFAULT NULL COMMENT '',
	`clientCert` TEXT DEFAULT NULL COMMENT '客户端秘钥',
	`clientKey` TEXT DEFAULT NULL COMMENT '',
	`serverCert` TEXT DEFAULT NULL COMMENT '服务端秘钥',
	`serverKey` TEXT DEFAULT NULL COMMENT '',
	`dh1024Pem` TEXT DEFAULT NULL COMMENT '',
	`vpnNum` INT(2) DEFAULT 0 COMMENT '绑定VPN数量',
	`version` INT(5) DEFAULT 0 COMMENT '',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`ZoneVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`name` varchar(255) NOT NULL UNIQUE COMMENT '名称',
	`province` VARCHAR(32) NOT NULL COMMENT '省份',
	`description` varchar(255) DEFAULT NULL COMMENT '描述',
	`nodeUuid` VARCHAR(32) NOT NULL COMMENT '节点',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `syscxp_vpn`.`HostEO` (
	`uuid` varchar(32) NOT NULL COMMENT 'UUID',
	`name` varchar(128) NOT NULL COMMENT '名称',
	`code` varchar(128) NOT NULL COMMENT '编号',
	`hostIp` varchar(128) DEFAULT NULL,
	`hostType` VARCHAR(128) NOT NULL COMMENT 'host类型',
	`position` varchar(256) NOT NULL COMMENT '位置',
	`state` varchar(32) NOT NULL DEFAULT 'Disable',
	`status` varchar(32) NOT NULL DEFAULT 'Connected',
	`deleted` varchar(255) DEFAULT NULL,
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY (`uuid`),
	UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '监控主机';

CREATE VIEW `syscxp_vpn`.`HostVO` AS SELECT `uuid`,`name`,`code`,`hostIp`,`position`,`hostType`,`state`,`status`,`lastOpDate`,`createDate`
	FROM `HostEO` WHERE deleted IS NULL;

CREATE TABLE  `syscxp_vpn`.`VpnHostVO` (
	`uuid` varchar(32) NOT NULL COMMENT 'UUID',
	`publicIp` VARCHAR(32) NOT NULL COMMENT '公网IP',
	`sshPort` VARCHAR(10) NOT NULL COMMENT 'ssh端口',
	`username` VARCHAR(255) NOT NULL COMMENT '用户名',
	`password` VARCHAR(255) NOT NULL COMMENT '密码',
	`interfaceName` VARCHAR(255) NOT NULL COMMENT '',
	`startPort` INT COMMENT '起始端口',
	`endPort` INT COMMENT '末尾端口',
	`zoneUuid` VARCHAR(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`HostInterfaceVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`name` varchar(255) NOT NULL COMMENT '名称',
	`hostUuid` VARCHAR(32) NOT NULL COMMENT '物理机',
	`endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
	`interfaceUuid` varchar(32) NOT NULL COMMENT '物理接口',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`VpnMotifyRecordVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`vpnUuid` varchar(32) NOT NULL COMMENT 'vpnUuid',
	`motifyType` varchar(32) NOT NULL COMMENT '升级、降级',
	`opAccountUuid` varchar(32) NOT NULL COMMENT '操作人',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# 带宽配置表
CREATE TABLE `syscxp_vpn`.`BandwidthOfferingVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'uuid',
	`name` varchar(255) NOT NULL COMMENT 'bandwidth offering name',
	`description` varchar(255) DEFAULT NULL COMMENT '描述',
	`bandwidth` BIGINT NOT NULL COMMENT '带宽',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE VpnHostVO ADD CONSTRAINT fkVpnHostVOHostEO FOREIGN KEY (uuid) REFERENCES HostEO (uuid) ON UPDATE RESTRICT ON DELETE CASCADE;
ALTER TABLE VpnHostVO ADD CONSTRAINT fkVpnHostVOZoneVO FOREIGN KEY (zoneUuid) REFERENCES ZoneVO (uuid) ON DELETE RESTRICT;
ALTER TABLE HostInterfaceVO ADD CONSTRAINT fkHostInterfaceVOVpnHostVO FOREIGN KEY (hostUuid) REFERENCES VpnHostVO (uuid) ON DELETE CASCADE;
ALTER TABLE VpnVO ADD CONSTRAINT fkVpnVOVpnHostVO FOREIGN KEY (hostUuid) REFERENCES VpnHostVO (uuid) ON DELETE RESTRICT;
ALTER TABLE VpnVO ADD CONSTRAINT fkVpnVOVpnCertVO FOREIGN KEY (vpnCertUuid) REFERENCES VpnCertVO (uuid) ON DELETE RESTRICT;

INSERT INTO `syscxp_vpn`.`BandwidthOfferingVO` (`uuid`,`name`,`description`,`bandwidth`,`lastOpDate`,`createDate`)
VALUES ('2G','2G','',2147483648,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
	('10G','10G','',10737418240,'2017-11-01 13:51:31','2017-11-01 13:51:31'),
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