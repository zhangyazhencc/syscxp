

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

CREATE TABLE  `syscxp_vpn`.`VpnGatewayVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`accountUuid` varchar(32) NOT NULL COMMENT '所属账户',
	`hostUuid` varchar(32) NOT NULL COMMENT '物理机',
	`name` varchar(255) NOT NULL COMMENT '名称',
	`description` varchar(255) DEFAULT NULL COMMENT '描述',
	`vpnCidr` VARCHAR(32) NOT NULL COMMENT 'VPN网段',
	`bandwidth` int(11) NOT NULL COMMENT '带宽',
	`endpointUuid` VARCHAR(32) NOT NULL COMMENT '连接点uuid',
	`status` VARCHAR(10) DEFAULT NULL COMMENT '启动状态',
	`months` int(11) NOT NULL COMMENT '购买时长',
	`expiredDate` timestamp DEFAULT NULL COMMENT '截止时间',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`tunnelIfaceVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`gatewayUuid` VARCHAR(32) NOT NULL COMMENT 'VPN网关',
	`name` varchar(255) NOT NULL COMMENT '名称',
	`description` varchar(255) DEFAULT NULL COMMENT '描述',
	`tunnelUuid` VARCHAR(128) NOT NULL COMMENT '专线网络uuid',
	`tunnelName` VARCHAR(128) NOT NULL COMMENT '专线网络',
	`serverIP` varchar(128) NOT NULL COMMENT '服务端IP',
	`clientIP` varchar(128) NOT NULL COMMENT '客户IP',
	`mask` VARCHAR(128) NOT NULL COMMENT '子网掩码',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`tunnelIfaceVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`gatewayUuid` VARCHAR(32) NOT NULL COMMENT 'VPN网关',
	`type` varchar(32) NOT NULL COMMENT '类型',
	`nextIfaceUuid` VARCHAR(32) NOT NULL COMMENT '下一跳接口',
	`nextIfaceName` VARCHAR(32) NOT NULL COMMENT '',
	`targetCidr` VARCHAR(32) NOT NULL COMMENT '目标网段',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_vpn`.`VpnHostVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`name` varchar(255) NOT NULL COMMENT '名称',
	`description` varchar(255) DEFAULT NULL COMMENT '描述',
	`publicIface` VARCHAR(255) NOT NULL COMMENT '公共物理接口',
	`tunnelIface` VARCHAR(255) NOT NULL COMMENT '云专线物理接口',
	`hostIp` VARCHAR(128) NOT NULL COMMENT '物理机IP',
	`sshPort` VARCHAR(10) DEFAULT '' COMMENT 'ssh端口',
	`username` VARCHAR(255) NOT NULL COMMENT '用户名',
	`password` VARCHAR(255) NOT NULL COMMENT '密码',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;