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

--主账号基本信息表
CREATE TABLE  `AccountVO` (
    `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
    `name` varchar(128) NOT NULL UNIQUE COMMENT '账户名称',
    `password` varchar(128) NOT NULL COMMENT '账户密码',
    `email` varchar(36) NOT NULL UNIQUE COMMENT '邮箱',
    `phone` varchar(11) NOT NULL UNIQUE COMMENT '手机号',
    `trueName` varchar(128) DEFAULT NULL COMMENT '姓名',
    `company` varchar(128) NOT NULL COMMENT '公司',
    `department` varchar(128) DEFAULT NULL COMMENT '部门',
    `industry` varchar(128) NOT NULL COMMENT '行业',
    `type` varchar(128) NOT NULL COMMENT 'account type',
    `status` varchar(128) NOT NULL COMMENT '状态',
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--代理商主账号关系表
CREATE TABLE  `ProxyAccountRefVO` (
	`proxyUuid` varchar(32) NOT NULL COMMENT '代理商（包括系统管理员）UUID',
    `accountUuid` varchar(32) NOT NULL COMMENT '由代理商（包括系统管理员）创建的主账号',
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
   
--主账号安全信息表
CREATE TABLE  `AccountSecurityVO` (
    `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
    `accountUuid` varchar(32) NOT NULL UNIQUE COMMENT '所属账户UUID',
    `publicKey` varchar(128) DEFAULT NULL COMMENT 'API密钥-公钥',
    `privateKey` varchar(128) DEFAULT NULL COMMENT 'API密钥-私钥',
    `allowIp` varchar(900) DEFAULT NULL COMMENT '允许访问IP的集合',
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--用户信息表
CREATE TABLE  `UserVO` (
    `uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
    `accountUuid` varchar(32) NOT NULL COMMENT '所属账户UUID',
    `name` varchar(128) NOT NULL UNIQUE COMMENT '用户名称',
    `password` varchar(128) NOT NULL COMMENT '用户密码',
    `email` varchar(36) NOT NULL UNIQUE COMMENT '邮箱',
    `phone` varchar(11) NOT NULL UNIQUE COMMENT '手机号',
    `trueName` varchar(128) NOT NULL COMMENT '姓名',
    `department` varchar(128) DEFAULT NULL COMMENT '部门',
    `status` varchar(128) NOT NULL COMMENT '状态',
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--角色策略表
CREATE TABLE `PolicyVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`name` varchar(128) NOT NULL UNIQUE COMMENT '角色名称',
	`description` varchar(255) DEFAULT NULL COMMENT '角色描述',
	`accountUuid` varchar(32) NOT NULL COMMENT '所属账户UUID',
	`policyStatement` text NOT NULL COMMENT '策略JSON字符串',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--用户角色关系表
--用户被删除或者角色被删除，这条关联关系也被删除
CREATE TABLE `UserPolicyRefVO` (
	`userUuid` varchar(32) NOT NULL COMMENT '用户UUID',
	`policyUuid` varchar(32) NOT NULL COMMENT '角色UUID',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
    `createDate` timestamp
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--Session表
--用户被删除，这条session记录也被删除
CREATE TABLE  `SessionVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `accountUuid` varchar(32) NOT NULL,
    `userUuid` varchar(32) DEFAULT NULL,
    `type` varchar(128) NOT NULL COMMENT 'account type',
    `expiredDate` timestamp NOT NULL,
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
