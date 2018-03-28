/*
SQLyog Enterprise Trial - MySQL GUI v7.11
MySQL - 5.5.5-10.2.7-MariaDB-10.2.7+maria~jessie : Database - syscxp_billing
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;



CREATE DATABASE /*!32312 IF NOT EXISTS*/`syscxp_billing` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `syscxp_billing`;


CREATE TABLE  `syscxp_billing`.`ManagementNodeVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `hostName` varchar(255) DEFAULT NULL,
    `port` int unsigned DEFAULT NULL,
    `state` varchar(128) NOT NULL,
    `joinDate` timestamp DEFAULT CURRENT_TIMESTAMP,
    `heartBeat` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `syscxp_billing`.`GlobalConfigVO` (
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


/*Table structure for table `AccountBalanceVO` */

DROP TABLE IF EXISTS `AccountBalanceVO`;

CREATE TABLE `AccountBalanceVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键和账号表的uuid保持一致',
  `presentBalance` decimal(12,4) NOT NULL DEFAULT 0.0000 COMMENT '赠送余额',
  `creditPoint` decimal(12,4) NOT NULL DEFAULT 0.0000 COMMENT '信用额度',
  `cashBalance` decimal(12,4) NOT NULL DEFAULT 0.0000 COMMENT '现金余额',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `AccountBalanceVO` */

/*Table structure for table `AccountDiscountVO` */

DROP TABLE IF EXISTS `AccountDiscountVO`;

CREATE TABLE `AccountDiscountVO` (
  `uuid` VARCHAR(32) NOT NULL COMMENT '主键',
  `accountUuid` VARCHAR(32) DEFAULT NULL COMMENT '账户id',
  `discount` TINYINT(3) UNSIGNED DEFAULT '100' COMMENT '折扣',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  `productCategoryUuid` VARCHAR(32) DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `account_unique` (`accountUuid`,`productCategoryUuid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*Data for the table `AccountDiscountVO` */

/*Table structure for table `BillVO` */

DROP TABLE IF EXISTS `BillVO`;

CREATE TABLE `BillVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `timeStart` timestamp NULL DEFAULT NULL COMMENT '账单开始时间',
  `timeEnd` timestamp NULL DEFAULT NULL COMMENT '账单结束时间',
  `totalDeductionPayCash` decimal(13,4) DEFAULT NULL COMMENT '总消费支出(现金)',
  `totalDeductionPayPresent` decimal(13,4) DEFAULT NULL COMMENT '总消费支出(赠送)',
  `totalRefundIncomeCash` decimal(13,4) DEFAULT NULL COMMENT '总退费收入(现金)',
  `totalRefundIncomePresent` decimal(13,4) DEFAULT NULL COMMENT '总退费收入(赠送)',
  `totalRechargeIncomeCash` decimal(13,4) DEFAULT NULL COMMENT '充值收入现金',
  `totalRechargeIncomePresent` decimal(13,4) DEFAULT NULL COMMENT '充值收入赠送',
  `repay` decimal(12,4) DEFAULT NULL COMMENT '本期应还',
  `cashBalance` decimal(12,4) DEFAULT NULL COMMENT '期末现金余额',
  `billDate` timestamp NULL DEFAULT NULL COMMENT '账单日',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账户uuid',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `DealDetailVO` */

DROP TABLE IF EXISTS `DealDetailVO`;

CREATE TABLE `DealDetailVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `outTradeNO` varchar(128) DEFAULT NULL COMMENT '流水号',
  `tradeNO` varchar(128) DEFAULT NULL COMMENT '支付宝流水号',
  `type` varchar(50) DEFAULT NULL COMMENT '交易类型',
  `expend` decimal(12,4) DEFAULT NULL COMMENT '支出',
  `income` decimal(12,4) DEFAULT NULL COMMENT '收入',
  `dealWay` varchar(50) DEFAULT NULL COMMENT '交易方式',
  `state` varchar(50) DEFAULT NULL COMMENT '交易状态',
  `finishTime` timestamp NULL DEFAULT NULL COMMENT '交易完成时间',
  `balance` decimal(12,4) DEFAULT NULL COMMENT '余额',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '用户id',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  `orderUuid` varchar(32) DEFAULT NULL,
  `opAccountUuid` varchar(32) DEFAULT NULL COMMENT '操作人',
  `comment` varchar(2000) DEFAULT NULL,
  `opUserUuid` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `NewIndex1` (`outTradeNO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `DealDetailVO` */

/*Table structure for table `OrderVO` */

DROP TABLE IF EXISTS `OrderVO`;

CREATE TABLE `OrderVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `type` varchar(50) DEFAULT NULL COMMENT '订单类型',
  `payTime` timestamp NULL DEFAULT NULL COMMENT '购买时间',
  `state` varchar(50) DEFAULT NULL COMMENT '订单状态',
  `originalPrice` decimal(12,4) DEFAULT 0.0000 COMMENT '产品总价',
  `price` decimal(12,4) DEFAULT 0.0000 COMMENT '折扣后总价',
  `payPresent` decimal(12,4) DEFAULT NULL COMMENT '订单实付赠送金额',
  `payCash` decimal(12,4) DEFAULT NULL COMMENT '订单实付现金金额',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账户id',
  `productEffectTimeStart` timestamp NULL DEFAULT NULL COMMENT '产品使用开始时间',
  `productEffectTimeEnd` timestamp DEFAULT NULL COMMENT '产品使用结束时间',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  `productUuid` varchar(32) NOT NULL COMMENT '产品ID',
  `productName` varchar(100) NOT NULL COMMENT '产品名称',
  `productType` varchar(50) DEFAULT NULL COMMENT '产品类型',
  `descriptionData` varchar(500) DEFAULT NULL COMMENT '产品说明，json格式',
  `productChargeModel` varchar(50) DEFAULT NULL COMMENT '计费方式--按月，按年',
  `duration` int(10) unsigned NOT NULL DEFAULT 0,
  `productStatus` tinyint(1) unsigned DEFAULT 1 COMMENT '产品是否开通',
  `callBackData` varchar(1000) DEFAULT NULL,
  `productPriceDiscountDetail` varchar(1000) DEFAULT NULL COMMENT '产品价格信息',
  `lastPriceOneMonth` decimal(12,4) DEFAULT NULL  COMMENT '产品价格变动前价格',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `PriceRefRenewVO` */

DROP TABLE IF EXISTS `PriceRefRenewVO`;

CREATE TABLE `PriceRefRenewVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账户id',
  `renewUuid` varchar(32) DEFAULT NULL COMMENT '续费id',
  `productPriceUnitUuid` varchar(32) DEFAULT NULL COMMENT '单价id',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `PriceRefRenewVO` */

/*Table structure for table `ProductPriceUnitVO` */


/*Table structure for table `ReceiptInfoVO` */

DROP TABLE IF EXISTS `ReceiptInfoVO`;

CREATE TABLE `ReceiptInfoVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `type` varchar(50) DEFAULT NULL COMMENT '发票类型',
  `title` varchar(200) DEFAULT NULL COMMENT '发票抬头',
  `bankName` varchar(200) DEFAULT NULL COMMENT '开户银行名',
  `bankAccountNumber` varchar(30) DEFAULT NULL COMMENT '基本开户账号',
  `telephone` varchar(20) DEFAULT NULL COMMENT '电话',
  `identifyNumber` varchar(30) DEFAULT NULL COMMENT '纳税人识别号',
  `address` varchar(300) DEFAULT NULL COMMENT '注册场地地址',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账号id',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  `isDefault` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `comment` varchar(500) DEFAULT NULL,
  `isShow` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ReceiptPostAddressVO` */

DROP TABLE IF EXISTS `ReceiptPostAddressVO`;

CREATE TABLE `ReceiptPostAddressVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账号id',
  `name` varchar(200) DEFAULT NULL COMMENT '姓名',
  `isDefault` tinyint(1) unsigned DEFAULT '0',
  `telephone` varchar(30) DEFAULT NULL COMMENT '电话号码',
  `address` varchar(500) DEFAULT NULL COMMENT '详细地址',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  `isShow` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ReceiptVO` */

DROP TABLE IF EXISTS `ReceiptVO`;
CREATE TABLE `ReceiptVO` (
  `uuid` VARCHAR(32) NOT NULL COMMENT '主键',
  `total` DECIMAL(12,4) DEFAULT NULL COMMENT '开票金额',
  `applyTime` TIMESTAMP NULL DEFAULT NULL COMMENT '申请时间',
  `state` VARCHAR(50) DEFAULT NULL COMMENT '状态',
  `receiptInfoUuid` VARCHAR(32) DEFAULT NULL COMMENT '发票开票信息id',
  `receiptAddressUuid` VARCHAR(32) DEFAULT NULL COMMENT '发票邮寄地址',
  `accountUuid` VARCHAR(32) DEFAULT NULL COMMENT '账户id',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  `commet` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `receiptNO` VARCHAR(128) DEFAULT NULL COMMENT '发票编号',
  `opMan` VARCHAR(32) DEFAULT NULL COMMENT '处理人',
  PRIMARY KEY (`uuid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*Table structure for table `RenewVO` */

DROP TABLE IF EXISTS `RenewVO`;

CREATE TABLE `RenewVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账号主键',
  `isRenewAuto` tinyint(2) unsigned DEFAULT '1' COMMENT '是否自动续费，1，自动，2关闭',
  `productUuid` varchar(32) NOT NULL COMMENT '产品ID',
  `productName` varchar(100) NOT NULL COMMENT '产品名称',
  `productType` varchar(50) DEFAULT NULL COMMENT '产品类型',
  `descriptionData` varchar(2000) DEFAULT NULL,
  `productChargeModel` varchar(50) DEFAULT NULL COMMENT '计费方式--按月，按年',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  `priceOneMonth` decimal(12,4) DEFAULT NULL,
  `priceDiscount` decimal(12,4) DEFAULT NULL,
  `expiredTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `UNI_ACCOUNT_PRODUCT_ID` (`accountUuid`,`productUuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `RenewPriceLogVO`;
  CREATE TABLE `RenewPriceLogVO` (
  `uuid` varchar(32) NOT NULL,
  `accountUuid` varchar(32) DEFAULT NULL,
  `productUuid` varchar(32) DEFAULT NULL,
  `opAccountUuid` varchar(32) DEFAULT NULL,
  `opUserUuid` varchar(32) DEFAULT NULL,
  `originPrice` decimal(12,4) DEFAULT NULL,
  `nowPrice` decimal(12,4) DEFAULT NULL,
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Data for the table `RenewVO` */

/*Table structure for table `SLACompensateVO` */

DROP TABLE IF EXISTS `SLACompensateVO`;

CREATE TABLE `SLACompensateVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账号id',
  `productType` varchar(50) DEFAULT NULL COMMENT '产品类型',
  `productUuid` varchar(32) DEFAULT NULL COMMENT '产品uuid',
  `productName` varchar(128) DEFAULT NULL,
  `reason` varchar(128) DEFAULT NULL COMMENT '赔偿原因',
  `description` varchar(255) DEFAULT NULL,
  `comment` varchar(1000) DEFAULT NULL COMMENT '赔偿说明',
  `duration` int(11) DEFAULT 0 COMMENT '赔偿天数',
  `timeStart` timestamp NULL DEFAULT NULL COMMENT '赔偿起始时间',
  `timeEnd` timestamp NULL DEFAULT NULL COMMENT '赔偿终止时间',
  `state` varchar(50) DEFAULT NULL COMMENT '状态',
  `lastOpDate` timestamp NOT NULL DEFAULT  current_timestamp(),
  `createDate` timestamp ,
  `applyTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `NotifyOrderVO`;
CREATE TABLE `NotifyOrderVO` (
  `uuid` varchar(32) NOT NULL,
  `orderUuid` varchar(32) NOT NULL COMMENT '订单id',
  `status` varchar(255) DEFAULT NULL,
  `notifyTimes` tinyint(2) DEFAULT '0' COMMENT '通知次数',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  `url` varchar(255) NOT NULL COMMENT '通知路径',
  `accountUuid` varchar(32) DEFAULT NULL,
  `productUuid` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `SLALogVO`;
CREATE TABLE `SLALogVO` (
  `uuid` VARCHAR(32) NOT NULL COMMENT '主键',
  `accountUuid` VARCHAR(32) DEFAULT NULL,
  `productUuid` VARCHAR(32) DEFAULT NULL,
  `timeStart` TIMESTAMP,
  `timeEnd` TIMESTAMP,
  `slaPrice` DECIMAL(12,4) DEFAULT NULL COMMENT '赔偿时价格',
  `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp,
  `duration` INT(11) DEFAULT NULL,
  `consumePrice` DECIMAL(12,4) DEFAULT NULL COMMENT '用户在赔偿时段的消费价格',
  PRIMARY KEY (`uuid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ProductCategoryVO`;
CREATE TABLE `ProductCategoryVO` (
  `uuid` VARCHAR(32) NOT NULL COMMENT 'uuid',
  `code` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `productTypeCode` VARCHAR(255) NOT NULL,
  `productTypeName` VARCHAR(255) NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  `lastOpDate` TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last operation date',
  `createDate` TIMESTAMP NOT NULL ,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`code`,`productTypeCode`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

LOCK TABLES `ProductCategoryVO` WRITE;
INSERT INTO `ProductCategoryVO` (uuid, code, name, productTypeCode, productTypeName, status, lastOpDate, createDate)
VALUES ('VPN', 'VPN', 'VPN', 'VPN', 'VPN', 'enable', '2018-01-02 09:42:29', '2017-10-30 15:46:12'),
('ABROAD', 'ABROAD', '跨国', 'TUNNEL', '专线网络', 'enable', '2018-01-18 13:21:54', '2017-10-30 17:18:12'),
('CITY', 'CITY', '同城', 'TUNNEL', '专线网络', 'enable', '2018-01-18 13:21:54', '2017-10-30 15:46:12'),
('LONG', 'LONG', '长传', 'TUNNEL', '专线网络', 'enable', '2018-01-18 13:21:54', '2017-10-30 15:46:12'),
('REGION', 'REGION', '区域', 'TUNNEL', '专线网络', 'enable', '2018-01-18 13:21:54', '2017-10-30 15:46:12'),

('EXCLUSIVE', 'EXCLUSIVE', '独享端口', 'PORT', '端口', 'enable', '2018-01-10 15:59:59', '2017-10-30 15:46:12'),
('SHARE', 'SHARE', '共享端口', 'PORT', '端口', 'enable', '2018-01-10 16:00:28', '2017-10-30 15:46:12'),

('BANDWIDTH', 'BANDWIDTH', '公网带宽', 'ECP', '互联云', 'enable', '2018-01-18 13:21:54', '2017-10-30 17:18:12'),
('DISK', 'DISK', '数据盘', 'ECP', '互联云', 'enable', '2018-01-02 09:43:41', '2017-10-30 15:46:12'),
('HOST', 'HOST', '云服务器', 'ECP', '互联云', 'enable', '2018-01-18 13:21:54', '2017-10-30 15:46:12'),
('POOLNETWORK', 'POOLNETWORK', '资源池网络', 'ECP', '互联云', 'enable', '2018-01-18 13:21:54', '2018-01-02 09:42:29'),
('RESOURCEPOOL', 'RESOURCEPOOL', '资源池', 'ECP', '互联云', 'enable', '2018-01-02 09:46:37', '2017-10-30 15:46:12'),
('IP', 'IP', '公网IP', 'ECP', '互联云', 'enable', '2018-01-18 13:21:54', '2017-10-30 17:18:12');

update `ProductCategoryVO` set productTypeCode='BANDWIDTH', productTypeName='互联云带宽' where uuid='BANDWIDTH';
update `ProductCategoryVO` set productTypeCode='DISK', productTypeName='互联云磁盘' where uuid='DISK';
update `ProductCategoryVO` set productTypeCode='HOST', productTypeName='互联云云主机' where uuid='HOST';
update `ProductCategoryVO` set productTypeCode='POOLNETWORK', productTypeName='互联云宿主机网络' where uuid='POOLNETWORK';
update `ProductCategoryVO` set productTypeCode='RESOURCEPOOL', productTypeName='互联云宿主机' where uuid='RESOURCEPOOL';
update `ProductCategoryVO` set productTypeCode='IP', productTypeName='互联云公网IP' where uuid='IP';

UNLOCK TABLES;

DROP TABLE IF EXISTS `ProductPriceUnitVO`;
CREATE TABLE `ProductPriceUnitVO` (
  `uuid` VARCHAR(32) NOT NULL,
  `productCategoryUuid` VARCHAR(32) DEFAULT NULL,
  `areaCode` VARCHAR(50) DEFAULT NULL,
  `areaName` VARCHAR(125) DEFAULT NULL,
  `lineCode` VARCHAR(50) DEFAULT NULL,
  `lineName` VARCHAR(125) DEFAULT NULL,
  `configCode` VARCHAR(50) DEFAULT NULL,
  `configName` VARCHAR(125) DEFAULT NULL,
  `unitPrice` INT(10) DEFAULT NULL,
  `lastOpDate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `createDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `ukProductPriceUnitVO` (`productCategoryUuid`,`areaCode`,`lineCode`,`configCode`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

## 专线网络 同城
INSERT INTO `ProductPriceUnitVO` ( uuid, productCategoryUuid, areaCode, areaName, lineCode, lineName, configCode, configName, unitPrice, lastOpDate, createDate)
VALUES ('64f7e897b7d711e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '100M', '100M', '9600', '2018-01-18 13:44:14', '2017-10-23 09:48:59'),
('b6d8ca0db7d711e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '10G', '10G', '17000', '2018-01-18 13:44:14', '2017-10-23 09:51:17'),
('b7d8ca0db7d711e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '20G', '20G', '17000', '2018-01-18 13:44:14', '2017-10-23 09:51:17'),
('6d81e6ddb7ce11e7areae30242ac1100', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '10M', '10M', '6670', '2018-01-18 13:44:14', '2017-10-23 08:44:48'),
('920bff80b7d711e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '1G', '1G', '13800', '2018-01-18 13:44:14', '2017-10-23 09:50:15'),
('71efa25db7d711e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '200M', '200M', '11800', '2018-01-18 13:44:14', '2017-10-23 09:49:21'),
('0cc4c0b6b7d711e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '20M', '20M', '7800', '2018-01-18 13:44:14', '2017-10-23 09:46:31'),
('9d77d22ab7d711e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '2G', '2G', '14800', '2018-01-18 13:44:14', '2017-10-23 09:50:34'),
('6d81e6ddb7ce11e7aae30242ac110002', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '2M', '2M', '3300', '2018-01-18 13:44:14', '2017-10-23 08:44:48'),
('8292ae59b7d711e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '500M', '500M', '12800', '2018-01-18 13:44:14', '2017-10-23 09:49:49'),
('56fbd8fbb7d711e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '50M', '50M', '8800', '2018-01-18 13:44:14', '2017-10-23 09:48:36'),
('a79fc19cb7d711e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '5G', '5G', '15800', '2018-01-18 13:44:14', '2017-10-23 09:50:51'),
('9f182aefb7d611e7be460242ac110003', 'CITY', 'DEFAULT', '默认', 'DEFAULT', '默认', '5M', '5M', '5800', '2018-01-18 13:44:14', '2017-10-23 09:43:27');
## 专线网络 区域
INSERT INTO `ProductPriceUnitVO` ( uuid, productCategoryUuid, areaCode, areaName, lineCode, lineName, configCode, configName, unitPrice, lastOpDate, createDate)
VALUES ('b6009d31b7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '100M', '100M', '11800', '2018-01-18 13:44:14', '2017-10-23 09:58:25'),
('b66bc770b7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '10G', '10G', '170000', '2018-01-18 13:44:14', '2017-10-23 09:58:25'),
('b76bc770b7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '20G', '20G', '170000', '2018-01-18 13:44:14', '2017-10-23 09:58:25'),
('b5d224d9b7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '10M', '10M', '5000', '2018-01-18 13:44:14', '2017-10-23 09:58:24'),
('b63d64c4b7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '1G', '1G', '48800', '2018-01-18 13:44:14', '2017-10-23 09:58:25'),
('b6145fa8b7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '200M', '200M', '18800', '2018-01-18 13:44:14', '2017-10-23 09:58:25'),
('b5e180feb7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '20M', '20M', '7800', '2018-01-18 13:44:14', '2017-10-23 09:58:25'),
('b64dcc83b7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '2G', '2G', '88800', '2018-01-18 13:44:14', '2017-10-23 09:58:25'),
('b5aa6603b7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '2M', '2M', '1300', '2018-01-18 13:44:14', '2017-10-23 09:58:24'),
('b628d0f5b7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '500M', '500M', '28800', '2018-01-18 13:44:14', '2017-10-23 09:58:25'),
('b5f0967bb7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '50M', '50M', '8900', '2018-01-18 13:44:14', '2017-10-23 09:58:25'),
('b65cce6cb7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '5G', '5G', '128800', '2018-01-18 13:44:14', '2017-10-23 09:58:25'),
('b5bcf83bb7d811e7be460242ac110003', 'REGION', 'CSJ', '长三角', 'DEFAULT', '默认', '5M', '5M', '1500', '2018-01-18 13:44:14', '2017-10-23 09:58:24'),
('1777824ab7d811e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '100M', '100M', '10800', '2018-01-18 13:44:14', '2017-10-23 09:53:59'),
('75bdab0ab7d811e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '10G', '10G', '168800', '2018-01-18 13:44:14', '2017-10-23 09:56:37'),
('75bdab0ab7d811e7be460243ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '20G', '20G', '168800', '2018-01-18 13:44:14', '2017-10-23 09:56:37'),
('e8bde368b7d711e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '10M', '10M', '5800', '2018-01-18 13:44:14', '2017-10-23 09:52:40'),
('4c1d5e31b7d811e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '1G', '1G', '48800', '2018-01-18 13:44:14', '2017-10-23 09:55:27'),
('240d0d12b7d811e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '200M', '200M', '18000', '2018-01-18 13:44:14', '2017-10-23 09:54:20'),
('f6ed9ff5b7d711e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '20M', '20M', '7000', '2018-01-18 13:44:14', '2017-10-23 09:53:04'),
('5b668c4bb7d811e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '2G', '2G', '88800', '2018-01-18 13:44:14', '2017-10-23 09:55:53'),
('d7628148b7ce11e7aae30242ac110002', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '2M', '2M', '2800', '2018-01-18 13:44:14', '2017-10-23 08:47:46'),
('3bec0121b7d811e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '500M', '500M', '28800', '2018-01-18 13:44:14', '2017-10-23 09:55:00'),
('0221e896b7d811e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '50M', '50M', '8800', '2018-01-18 13:44:14', '2017-10-23 09:53:23'),
('69c10a17b7d811e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '5G', '5G', '128800', '2018-01-18 13:44:14', '2017-10-23 09:56:17'),
('dbc0b19ab7d711e7be460242ac110003', 'REGION', 'JJJ', '京津冀', 'DEFAULT', '默认', '5M', '5M', '3800', '2018-01-18 13:44:14', '2017-10-23 09:52:19'),
('d4c6fb7ab7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '100M', '100M', '12000', '2018-01-18 13:44:14', '2017-10-23 10:06:26'),
('d52eed52b7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '10G', '10G', '168800', '2018-01-18 13:44:14', '2017-10-23 10:06:27'),
('d52eed52b7d911e7be461242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '20G', '20G', '168800', '2018-01-18 13:44:14', '2017-10-23 10:06:27'),
('d48cfa45b7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '10M', '10M', '5000', '2018-01-18 13:44:14', '2017-10-23 10:06:25'),
('d4fbd940b7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '1G', '1G', '48800', '2018-01-18 13:44:14', '2017-10-23 10:06:26'),
('d4db025cb7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '200M', '200M', '16000', '2018-01-18 13:44:14', '2017-10-23 10:06:26'),
('d49d3f55b7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '20M', '20M', '6000', '2018-01-18 13:44:14', '2017-10-23 10:06:26'),
('d50bb0f5b7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '2G', '2G', '10888', '2018-01-18 13:44:14', '2017-10-23 10:06:26'),
('d46ca64fb7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '2M', '2M', '3900', '2018-01-18 13:44:14', '2017-10-23 10:06:25'),
('d4ebcf30b7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '500M', '500M', '28800', '2018-01-18 13:44:14', '2017-10-23 10:06:26'),
('d4b33c9cb7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '50M', '50M', '8888', '2018-01-18 13:44:14', '2017-10-23 10:06:26'),
('d51cf834b7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '5G', '5G', '128800', '2018-01-18 13:44:14', '2017-10-23 10:06:26'),
('d47cd46db7d911e7be460242ac110003', 'REGION', 'ZSJ', '珠三角', 'DEFAULT', '默认', '5M', '5M', '3000', '2018-01-18 13:44:14', '2017-10-23 10:06:25');
## 专线网络 长传
INSERT INTO `ProductPriceUnitVO` ( uuid, productCategoryUuid, areaCode, areaName, lineCode, lineName, configCode, configName, unitPrice, lastOpDate, createDate)
VALUES ('19146f3bb7da11e7be460242ac110003', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '100M', '100M', '9800', '2018-01-18 13:44:14', '2017-10-23 10:08:20'),
('1970b682b7da11e7be460242ac110003', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '10G', '10G', '168800', '2018-01-18 13:44:14', '2017-10-23 10:08:21'),
('1970b682b7da11e7be450242ac110003', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '20G', '20G', '168800', '2018-01-18 13:44:14', '2017-10-23 10:08:21'),
('0221e896b7d811e7be4602fsdeac1100', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '10M', '10M', '7800', '2018-01-18 13:44:14', '2017-10-23 09:53:23'),
('19484aa0b7da11e7be460242ac110003', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '1G', '1G', '13800', '2018-01-18 13:44:14', '2017-10-23 10:08:21'),
('19245871b7da11e7be460242ac110003', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '200M', '200M', '11800', '2018-01-18 13:44:14', '2017-10-23 10:08:21'),
('18f35ae7b7da11e7be460242ac110003', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '20M', '20M', '8800', '2018-01-18 13:44:14', '2017-10-23 10:08:20'),
('0221e896bew7d811e7be4602fsdeac11', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '2G', '2G', '14800', '2018-01-18 13:44:14', '2017-10-23 09:53:23'),
('f9a58710b7ce11e7aae30242ac110002', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '2M', '2M', '3100', '2018-01-18 13:44:14', '2017-10-23 08:48:43'),
('193636cab7da11e7be460242ac110003', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '500M', '500M', '12800', '2018-01-18 13:44:14', '2017-10-23 10:08:21'),
('1904a1afb7da11e7be460242ac110003', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '50M', '50M', '8888', '2018-01-18 13:44:14', '2017-10-23 10:08:20'),
('195de86cb7da11e7be460242ac110003', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '5G', '5G', '15800', '2018-01-18 13:44:14', '2017-10-23 10:08:21'),
('18e20801b7da11e7be460242ac110003', 'LONG', 'DEFAULT', '默认', 'DEFAULT', '默认', '5M', '5M', '6800', '2018-01-18 13:44:14', '2017-10-23 10:08:20');

## 跨国专线ABROAD 国外到国外默认价格
INSERT INTO `ProductPriceUnitVO` ( uuid, productCategoryUuid, areaCode, areaName, lineCode, lineName, configCode, configName, unitPrice, lastOpDate, createDate)
VALUES('64f7e897b7d7d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '100M', '100M', '9600', '2018-01-18 13:44:14', '2017-10-23 09:48:59'),
('b6d8ca0db7d7d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '10G', '10G', '17000', '2018-01-18 13:44:14', '2017-10-23 09:51:17'),
('b7d8ca0db7d7d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '20G', '20G', '17000', '2018-01-18 13:44:14', '2017-10-23 09:51:17'),
('6d81e6ddb7ced1e7areae30242ac1100', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '10M', '10M', '6670', '2018-01-18 13:44:14', '2017-10-23 08:44:48'),
('920bff80b7d7d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '1G', '1G', '13800', '2018-01-18 13:44:14', '2017-10-23 09:50:15'),
('71efa25db7d7d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '200M', '200M', '11800', '2018-01-18 13:44:14', '2017-10-23 09:49:21'),
('0cc4c0b6b7d7d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '20M', '20M', '7800', '2018-01-18 13:44:14', '2017-10-23 09:46:31'),
('9d77d22ab7d7d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '2G', '2G', '14800', '2018-01-18 13:44:14', '2017-10-23 09:50:34'),
('6d81e6ddb7ced1e7aae30242ac110002', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '2M', '2M', '3300', '2018-01-18 13:44:14', '2017-10-23 08:44:48'),
('8292ae59b7d7d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '500M', '500M', '12800', '2018-01-18 13:44:14', '2017-10-23 09:49:49'),
('56fbd8fbb7d7d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '50M', '50M', '8800', '2018-01-18 13:44:14', '2017-10-23 09:48:36'),
('a79fc19cb7d7d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '5G', '5G', '15800', '2018-01-18 13:44:14', '2017-10-23 09:50:51'),
('9f182aefb7d6d1e7be460242ac110003', 'ABROAD', 'ABROAD', '国外', 'DEFAULT', '默认', '5M', '5M', '5800', '2018-01-18 13:44:14', '2017-10-23 09:43:27');

## 跨国专线CHINA2ABROAD 国内到国外默认价格
INSERT INTO `ProductPriceUnitVO` ( uuid, productCategoryUuid, areaCode, areaName, lineCode, lineName, configCode, configName, unitPrice, lastOpDate, createDate)
VALUES('64f7e897b7d7dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '100M', '100M', '9600', '2018-01-18 13:44:14', '2017-10-23 09:48:59'),
('b6d8ca0db7d7dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '10G', '10G', '17000', '2018-01-18 13:44:14', '2017-10-23 09:51:17'),
('b7d8ca0db7d7dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '20G', '20G', '17000', '2018-01-18 13:44:14', '2017-10-23 09:51:17'),
('6d81e6ddb7cedae7areae30242ac1100', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '10M', '10M', '6670', '2018-01-18 13:44:14', '2017-10-23 08:44:48'),
('920bff80b7d7dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '1G', '1G', '13800', '2018-01-18 13:44:14', '2017-10-23 09:50:15'),
('71efa25db7d7dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '200M', '200M', '11800', '2018-01-18 13:44:14', '2017-10-23 09:49:21'),
('0cc4c0b6b7d7dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '20M', '20M', '7800', '2018-01-18 13:44:14', '2017-10-23 09:46:31'),
('9d77d22ab7d7dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '2G', '2G', '14800', '2018-01-18 13:44:14', '2017-10-23 09:50:34'),
('6d81e6ddb7cedae7aae30242ac110002', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '2M', '2M', '3300', '2018-01-18 13:44:14', '2017-10-23 08:44:48'),
('8292ae59b7d7dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '500M', '500M', '12800', '2018-01-18 13:44:14', '2017-10-23 09:49:49'),
('56fbd8fbb7d7dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '50M', '50M', '8800', '2018-01-18 13:44:14', '2017-10-23 09:48:36'),
('a79fc19cb7d7dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '5G', '5G', '15800', '2018-01-18 13:44:14', '2017-10-23 09:50:51'),
('9f182aefb7d6dae7be460242ac110003', 'ABROAD', 'CHINA2ABROAD', '国内到国外', 'DEFAULT', '默认', '5M', '5M', '5800', '2018-01-18 13:44:14', '2017-10-23 09:43:27');

## 独享端口
INSERT INTO `ProductPriceUnitVO` ( uuid, productCategoryUuid, areaCode, areaName, lineCode, lineName, configCode, configName, unitPrice, lastOpDate, createDate)
VALUES ('996ba83db7dd11e7be460242ac110003', 'EXCLUSIVE', 'DEFAULT', '默认', 'DEFAULT', '默认', 'SFP_10G', '光口万兆', '1500', '2018-01-18 13:44:14', '2017-10-23 10:33:24'),
('8fe292e4b7dd11e7be460242ac110003', 'EXCLUSIVE', 'DEFAULT', '默认', 'DEFAULT', '默认', 'SFP_1G', '光口千兆', '1000', '2018-01-18 13:44:14', '2017-10-23 10:33:08'),
('30d3720cb7d211e7aae30242ac110002', 'EXCLUSIVE', 'DEFAULT', '默认', 'DEFAULT', '默认', 'SHARE', '共享端口', '0', '2018-01-18 13:44:14', '2017-10-23 09:11:44'),
('6d56ddc2b7dd11e7be460242ac110003', 'EXCLUSIVE', 'DEFAULT', '默认', 'DEFAULT', '默认', 'RJ45_1G', '电口千兆', '500', '2018-01-18 13:44:14', '2017-10-23 10:32:10');
## 共享端口
INSERT INTO `ProductPriceUnitVO` ( uuid, productCategoryUuid, areaCode, areaName, lineCode, lineName, configCode, configName, unitPrice, lastOpDate, createDate)
VALUES
('b93ead68966a4f799db2446f20d63e54', 'SHARE', 'DEFAULT', '默认（勿删除）', 'DEFAULT', '默认', 'GT2G', '大于2G', '0', '2018-02-26 13:31:14', '2018-01-24 10:47:30'),
('3f41f26718104698b519e57ef7e5a48a', 'SHARE', 'DEFAULT', '默认（勿删除）', 'DEFAULT', '默认', 'GT500MLT2G', '500M~2G', '0', '2018-02-26 13:31:14', '2018-01-24 10:47:30'),
('9a0a0e381eff45e3abc8c11fea8ba31d', 'SHARE', 'DEFAULT', '默认（勿删除）', 'DEFAULT', '默认', 'LT500M', '小于500M', '0', '2018-02-26 13:31:14', '2018-01-24 10:47:30');

## 互联云 默认 每M带宽价格
INSERT INTO `ProductPriceUnitVO` ( uuid, productCategoryUuid, areaCode, areaName, lineCode, lineName, configCode, configName, unitPrice, lastOpDate, createDate)
VALUES ('b1db71b17d684f6eb1759c9bdf4df4b8', 'BANDWIDTH', 'DEFAULT', '默认', 'DEFAULT', '默认', '1M', '1M', '100', '2018-01-18 14:39:47', '2018-01-12 17:41:46');

## VPN 默认不分连接点规格价格
INSERT INTO `ProductPriceUnitVO` ( uuid, productCategoryUuid, areaCode, areaName, lineCode, lineName, configCode, configName, unitPrice, lastOpDate, createDate)
VALUES ('b593934ee71811e799ff5254004b5c82', 'VPN', 'DEFAULT', '默认', 'DEFAULT', '默认', '100M', '100M', '6000', '2018-01-18 13:44:14', '2017-12-22 13:05:00'),
('b59397b7e71811e799ff5254004b5c82', 'VPN', 'DEFAULT', '默认', 'DEFAULT', '默认', '10M', '10M', '3000', '2018-01-18 13:44:14', '2017-12-22 13:05:00'),
('b5939a11e71811e799ff5254004b5c82', 'VPN', 'DEFAULT', '默认', 'DEFAULT', '默认', '200M', '200M', '7000', '2018-01-18 13:44:14', '2017-12-22 13:05:00'),
('b5939b4ce71811e799ff5254004b5c82', 'VPN', 'DEFAULT', '默认', 'DEFAULT', '默认', '20M', '20M', '4000', '2018-01-18 13:44:14', '2017-12-22 13:05:00'),
('b5939d4ee71811e799ff5254004b5c82', 'VPN', 'DEFAULT', '默认', 'DEFAULT', '默认', '2M', '2M', '1000', '2018-01-18 13:44:14', '2017-12-22 13:05:00'),
('b5939f44e71811e799ff5254004b5c82', 'VPN', 'DEFAULT', '默认', 'DEFAULT', '默认', '50M', '50M', '5000', '2018-01-18 13:44:14', '2017-12-22 13:05:00'),
('b593a440e71811e799ff5254004b5c82', 'VPN', 'DEFAULT', '默认', 'DEFAULT', '默认', '5M', '5M', '2000', '2018-01-18 13:44:14', '2017-12-22 13:05:00');




