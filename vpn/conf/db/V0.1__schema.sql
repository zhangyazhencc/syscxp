

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
    `doneDate` timestamp NULL,
    `errText` text DEFAULT NULL,
    PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`VpnVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`accountUuid` varchar(32) NOT NULL COMMENT '所属账户',
	`hostUuid` varchar(32) NOT NULL COMMENT '物理机',
	`name` varchar(255) NOT NULL COMMENT '名称',
	`description` varchar(255) DEFAULT NULL COMMENT '描述',
	`vpnCidr` VARCHAR(32) NOT NULL COMMENT 'VPN网段',
	`bandwidth` BIGINT NOT NULL COMMENT '带宽',
	`endpointUuid` VARCHAR(32) NOT NULL COMMENT '连接点uuid',
	`port` VARCHAR(10) NOT NULL COMMENT 'VPN端口',
	`state` VARCHAR(32) DEFAULT NULL COMMENT '启用状态',
	`status` VARCHAR(32) DEFAULT NULL COMMENT '运行状态',
	`duration` int(11) NOT NULL COMMENT '购买时长',
	`memo` VARCHAR(255) DEFAULT NULL COMMENT '备注',
	`sid` varchar(32) NOT NULL COMMENT 'SID',
	`key` VARCHAR(32) NOT NULL COMMENT 'cert-key',
	`payment` VARCHAR(32) NOT NULL COMMENT '支付状态',
	`expireDate` timestamp COMMENT '截止时间',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.VpnInterfaceVO (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`vpnUuid` VARCHAR(32) NOT NULL COMMENT 'VPN网关',
	`name` varchar(255) NOT NULL COMMENT '名称',
	`networkUuid` VARCHAR(128) NOT NULL COMMENT '专线网络uuid',
	`localIp` varchar(128) NOT NULL COMMENT '接口地址',
	`netmask` VARCHAR(128) NOT NULL COMMENT '子网掩码',
	`vlan` INT NOT NULL COMMENT '端口外部vlan',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`VpnRouteVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`vpnUuid` VARCHAR(32) NOT NULL COMMENT 'VPN网关',
	`routeType` varchar(32) NOT NULL COMMENT '类型',
	`nextInterface` VARCHAR(32) NOT NULL COMMENT '下一跳接口',
	`targetCidr` VARCHAR(32) NOT NULL COMMENT '目标网段',
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

CREATE TABLE  `syscxp_vpn`.`VpnHostVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`name` varchar(255) NOT NULL UNIQUE COMMENT '名称',
	`zoneUuid` VARCHAR(32) NOT NULL COMMENT '区域',
	`description` varchar(255) DEFAULT NULL COMMENT '描述',
	`publicInterface` VARCHAR(255) NOT NULL COMMENT '公网物理接口',
	`publicIp` VARCHAR(32) NOT NULL COMMENT '公网IP',
	`state` VARCHAR(32) NOT NULL COMMENT '启用状态',
	`status` VARCHAR(32) NOT NULL COMMENT '运行状态',
	`manageIp` VARCHAR(128) NOT NULL COMMENT '管理网IP',
	`sshPort` VARCHAR(10) NOT NULL COMMENT 'ssh端口',
	`username` VARCHAR(255) NOT NULL COMMENT '用户名',
	`password` VARCHAR(255) NOT NULL COMMENT '密码',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`HostInterfaceVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`name` varchar(255) NOT NULL UNIQUE COMMENT '名称',
	`hostUuid` VARCHAR(32) NOT NULL COMMENT '物理机',
	`endpointUuid` varchar(32) NOT NULL COMMENT '连接点',
	`interfaceUuid` varchar(32) NOT NULL COMMENT '物理接口',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE VpnHostVO ADD CONSTRAINT fkVpnHostVOZoneVO FOREIGN KEY (zoneUuid) REFERENCES ZoneVO (uuid) ON DELETE RESTRICT;
ALTER TABLE HostInterfaceVO ADD CONSTRAINT fkHostInterfaceVOVpnHostVO FOREIGN KEY (hostUuid) REFERENCES VpnHostVO (uuid) ON DELETE CASCADE;
ALTER TABLE VpnVO ADD CONSTRAINT fkVpnVOVpnHostVO FOREIGN KEY (hostUuid) REFERENCES VpnHostVO (uuid) ON DELETE RESTRICT;
ALTER TABLE VpnInterfaceVO ADD CONSTRAINT fkTunnelIfaceVOVpnVO FOREIGN KEY (vpnUuid) REFERENCES VpnVO (uuid) ON DELETE CASCADE;
ALTER TABLE VpnRouteVO ADD CONSTRAINT fkVpnRouteVOVpnVO FOREIGN KEY (vpnUuid) REFERENCES VpnVO (uuid) ON DELETE CASCADE;