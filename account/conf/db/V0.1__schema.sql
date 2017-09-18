use syscxp_account;

CREATE TABLE  `syscxp_account`.`ManagementNodeVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `hostName` varchar(255) DEFAULT NULL,
    `port` int unsigned DEFAULT NULL,
    `state` varchar(128) NOT NULL,
    `joinDate` timestamp DEFAULT CURRENT_TIMESTAMP,
    `heartBeat` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_account`.`GlobalConfigVO` (
    `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `description` varchar(1024) DEFAULT NULL,
    `category` varchar(64) NOT NULL,
    `defaultValue` text DEFAULT NULL,
    `value` text DEFAULT NULL,
    PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `JobQueueVO` (
    `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
    `name` varchar(255) NOT NULL UNIQUE,
    `owner` varchar(255) DEFAULT NULL,
    `workerManagementNodeId` varchar(32) DEFAULT NULL,
    `takenDate` timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `JobQueueEntryVO` (
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


CREATE TABLE  `AccountVO` (
    `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
    `name` varchar(128) NOT NULL UNIQUE COMMENT '账户名称',
    `password` varchar(128) NOT NULL COMMENT '账户密码',
    `email` varchar(36) NOT NULL UNIQUE COMMENT '邮箱',
    `emailStatus` varchar(36) NOT NULL COMMENT '邮箱是否认证',
    `phone` varchar(32) NOT NULL UNIQUE COMMENT '手机号',
    `phoneStatus` varchar(36) NOT NULL COMMENT '手机是否认证',
    `trueName` varchar(128) DEFAULT NULL COMMENT '姓名',
    `company` varchar(128) DEFAULT NULL COMMENT '公司',
    `industry` varchar(128) DEFAULT NULL COMMENT '行业',
    `type` varchar(128) NOT NULL COMMENT 'account type',
    `status` varchar(128) NOT NULL COMMENT '状态',
    `description` varchar(255),
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AccountExtraInfoVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`grade` varchar(36) DEFAULT NULL COMMENT '客户等级',
	`userUuid` varchar(36) DEFAULT NULL COMMENT '业务员uuid',
  `createWay` varchar(36) NOT NULL COMMENT '注册渠道',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp ,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AccountContactsVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`accountUuid` varchar(32) NOT NULL COMMENT '账户uuid',
	`name` varchar(128) DEFAULT NULL COMMENT '联系人',
	`phone` varchar(36) DEFAULT NULL COMMENT '联系电话',
	`email` varchar(36) DEFAULT NULL COMMENT '邮箱',
	`description` varchar(255) DEFAULT NULL COMMENT '备注',
	`noticeWay` varchar(128) DEFAULT NULL COMMENT '通知方式',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp ,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `ProxyAccountRefVO` (
  `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
	`accountUuid` varchar(32) NOT NULL COMMENT '代理商（包括系统管理员）UUID',
  `customerAcccountUuid` varchar(32) NOT NULL COMMENT '由代理商（包括系统管理员）创建的主账号',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp ,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `AccountApiSecurityVO` (
    `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
    `accountUuid` varchar(32) NOT NULL UNIQUE COMMENT '所属账户UUID',
    `publicKey` varchar(128) DEFAULT NULL COMMENT 'API密钥-公钥',
    `privateKey` varchar(128) DEFAULT NULL COMMENT 'API密钥-私钥',
    `allowIp` text DEFAULT NULL COMMENT '允许访问IP的集合',
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp ,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `UserVO` (
    `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
    `accountUuid` varchar(32) NOT NULL COMMENT '所属账户UUID',
    `name` varchar(128) NOT NULL UNIQUE COMMENT '用户名称',
    `password` varchar(128) NOT NULL COMMENT '用户密码',
    `email` varchar(36) NOT NULL COMMENT '邮箱',
    `emailStatus` varchar(36) NOT NULL COMMENT '邮箱是否认证',
    `phoneStatus` varchar(36) NOT NULL COMMENT '手机是否认证',
    `phone` varchar(11) NOT NULL COMMENT '手机号',
    `trueName` varchar(128) NOT NULL COMMENT '姓名',
    `department` varchar(128) DEFAULT NULL COMMENT '部门',
    `status` varchar(128) NOT NULL COMMENT '状态',
    `userType` varchar(36) NOT NULL COMMENT '用户类型（业务员，普通用户）',
    `description` varchar(255),
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp ,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `RoleVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`name` varchar(128) NOT NULL COMMENT '角色名称',
	`description` varchar(255) DEFAULT NULL COMMENT '角色描述',
	`accountUuid` varchar(32) NOT NULL COMMENT '所属账户UUID',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp ,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `UserRoleRefVO` (
  `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
	`userUuid` varchar(32) NOT NULL COMMENT '用户UUID',
	`roleUuid` varchar(32) NOT NULL COMMENT '角色UUID',
  `createDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `SessionVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `accountUuid` varchar(32) NOT NULL,
    `userUuid` varchar(32) DEFAULT NULL,
    `type` varchar(128) NOT NULL COMMENT 'account type',
    `expiredDate` timestamp NOT NULL,
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `NoticeVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `title` varchar(255) NOT NULL COMMENT '标题',
    `link` varchar(255) DEFAULT NULL COMMENT '链接',
    `status` varchar(32) DEFAULT NULL COMMENT '状态',
    `startTime` timestamp NULL DEFAULT NULL COMMENT '开始时间',
    `endTime` timestamp NULL DEFAULT NULL COMMENT '结束时间',
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `AlarmContactVO` (
  `uuid` varchar(32) NOT NULL UNIQUE,
  `name` varchar(32) NOT NULL COMMENT '姓名',
  `phone` varchar(32) NOT NULL COMMENT '手机号',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `accountName` varchar(32) NOT NULL COMMENT '账户名',
  `company` varchar(128) NOT NULL COMMENT '公司名',
  `channel` VARCHAR(32) NOT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE  `NotificationVO` (
    `uuid` VARCHAR(32) NOT NULL UNIQUE,
    `accountUuid` VARCHAR(32) DEFAULT NULL,
    `userUuid` VARCHAR(32) DEFAULT NULL,
    `name` VARCHAR(255) DEFAULT NULL COMMENT '消息类型',
	`category` VARCHAR(32) DEFAULT NULL,
    `content` VARCHAR(128) DEFAULT NULL,
    `msgfields` TEXT DEFAULT NULL,
    `sender` VARCHAR(32) NOT NULL,
	`remoteIp` VARCHAR(20) NOT NULL,
	`success` BOOLEAN DEFAULT FALSE COMMENT '操作状态',
    `status` VARCHAR(32) DEFAULT NULL,
    `resourceUuid` VARCHAR(32) DEFAULT NULL,
    `resourceType` VARCHAR(32) NOT NULL,
    `type` VARCHAR(32) DEFAULT NULL,
    `time` BIGINT UNSIGNED,
    `opaque` TEXT DEFAULT NULL,
    `lastOpDate` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` TIMESTAMP,
    `dateTime` TIMESTAMP,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SmsVO` (
  `id` bigint unsigned NOT NULL UNIQUE AUTO_INCREMENT,
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账户UUID',
  `userUuid` varchar(32) DEFAULT NULL COMMENT '用户UUID',
  `ip` varchar(30) DEFAULT NULL,
  `phone` varchar(512) DEFAULT NULL,
  `templateId` varchar(20) DEFAULT NULL,
  `appId` varchar(255) DEFAULT NULL,
  `data` varchar(1024) DEFAULT NULL,
  `statusCode` varchar(20) DEFAULT NULL,
  `statusMsg` varchar(255) DEFAULT NULL,
  `dateCreated` varchar(20) DEFAULT NULL,
  `smsMessagesId` varchar(255) DEFAULT NULL,
  `msgEntrance` int(10) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `PolicyVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`name` varchar(128) NOT NULL COMMENT '权限名称',
	`description` varchar(255) DEFAULT NULL COMMENT '权限描述',
	`permission` text NOT NULL COMMENT '权限字符串',
	`type` varchar(32) DEFAULT NULL COMMENT '权限类型',
	`sortId` int(10) DEFAULT 0 COMMENT '排序ID',
	`accountType` varchar(32) DEFAULT NULL COMMENT '',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp ,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `RolePolicyRefVO` (
	`id` INT UNIQUE AUTO_INCREMENT,
	`roleUuid` varchar(32) NOT NULL COMMENT '角色UUID',
    `policyUuid` varchar(32) NOT NULL COMMENT '权限uuid',
	`createDate` timestamp DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE UserVO ADD CONSTRAINT fkUserVOAccountVO FOREIGN KEY (accountUuid) REFERENCES AccountVO (uuid) ON DELETE RESTRICT;
ALTER TABLE AccountExtraInfoVO ADD CONSTRAINT fkAccountExtraInfoVOAccountVO FOREIGN KEY (uuid) REFERENCES AccountVO (uuid) ON DELETE CASCADE;
ALTER TABLE AccountContactsVO ADD CONSTRAINT fkAccountContactsVOAccountVO FOREIGN KEY (accountUuid) REFERENCES AccountVO (uuid) ON DELETE CASCADE;
ALTER TABLE AccountApiSecurityVO ADD CONSTRAINT fkAccountApiSecurityVOAccountVO FOREIGN KEY (accountUuid) REFERENCES AccountVO (uuid) ON DELETE CASCADE;

ALTER TABLE ProxyAccountRefVO ADD CONSTRAINT fkProxyAccountRefVOAccountVO FOREIGN KEY (accountUuid) REFERENCES AccountVO (uuid) ON DELETE CASCADE;

ALTER TABLE RoleVO ADD CONSTRAINT fkRoleVOAccountVO FOREIGN KEY (accountUuid) REFERENCES AccountVO (uuid) ON DELETE CASCADE;
ALTER TABLE UserRoleRefVO ADD CONSTRAINT fkUserRoleRefVORoleVO FOREIGN KEY (roleUuid) REFERENCES RoleVO (uuid) ON DELETE RESTRICT;
ALTER TABLE UserRoleRefVO ADD CONSTRAINT fkUserRoleRefVOUserVO FOREIGN KEY (userUuid) REFERENCES UserVO (uuid) ON DELETE RESTRICT;
ALTER TABLE RolePolicyRefVO ADD CONSTRAINT fkRolePolicyRefVORoleVO FOREIGN KEY (roleUuid) REFERENCES RoleVO (uuid) ON DELETE RESTRICT;
ALTER TABLE RolePolicyRefVO ADD CONSTRAINT fkRolePolicyRefVOPolicyVO FOREIGN KEY (policyUuid) REFERENCES PolicyVO (uuid) ON DELETE RESTRICT;

INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('TunnelReadOnlyAccess','只读访问专线网络的权限','tunnel','Normal',0,'{"actions":["tunnel:read","node:read","monitor:read"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('TunnelFullAccess','管理专线网络权限','tunnel','Normal','1','{"actions":["tunnel:.*","node:read","monitor:.*"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('NodeReadOnlyAccess','只读访问节点的权限','tunnel','SystemAdmin','0','{"actions":["node:read"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('NodeFullAccess','管理节点权限','tunnel','SystemAdmin','1','{"actions":["node:.*"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('SwitchReadOnlyAccess','只读访问交换机的权限','tunnel','SystemAdmin','0','{"actions":["switch:read"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('SwitchFullAccess','管理交换机权限','tunnel','SystemAdmin','1','{"actions":["switch:.*","node:read"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('TunnelHostReadOnlyAccess','只读访问监控主机的权限','tunnel','SystemAdmin','0','{"actions":["tunnelHost:read"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('TunnelHostFullAccess','管理监控主机权限','tunnel','SystemAdmin','1','{"actions":["tunnelHost:.*","node:read"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('VPNReadOnlyAccess','只读访问VPN权限','vpn','Normal','0	','{"actions":["vpn:read"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('VPNFullAccess','管理VPN权限','vpn','Normal','1','{"actions":["vpn:.*"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('BillingReadOnlyAccess','只读访问费用中心的权限','billing','Normal','0','{"actions":["order:read","recharge:read","receipt:read","renew:read","sla:read"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('BillingFullAccess','管理费用中心的权限','billing','Normal','1','{"actions":["order:.*","recharge:.*","receipt:.*","renew:.*","sla:.*"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('AccountReadOnlyAccess','只读访问账户中心的权限','account','Normal','0','{"actions":["account:read"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('AccountFullAccess','管理账户中心的权限','account','Normal','1','{"actions":["account:.*", "user:.*"],"effect":"Allow"}');
INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission) VALUES ('UserFullAccess','管理User的权限','account','Normal','2','{"actions": ["user:.*"],"effect":"Allow"}');

UPDATE `PolicyVO` p set p.createDate = current_timestamp();