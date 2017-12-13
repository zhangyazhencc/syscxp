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
  `lastOpDate` timestamp NOT NULL DEFAULT current_timestamp(),
  `createDate` timestamp  ,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `AccountBalanceVO` */

/*Table structure for table `AccountDiscountVO` */

DROP TABLE IF EXISTS `AccountDiscountVO`;

CREATE TABLE `AccountDiscountVO` (
  `uuid` VARCHAR(32) NOT NULL COMMENT '主键',
  `accountUuid` VARCHAR(32) DEFAULT NULL COMMENT '账户id',
  `discount` TINYINT(3) UNSIGNED DEFAULT '100' COMMENT '折扣',
  `lastOpDate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
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
  `lastOpDate` timestamp NOT NULL DEFAULT  current_timestamp(),
  `createDate` timestamp ,
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
  `lastOpDate` timestamp NOT NULL DEFAULT  current_timestamp(),
  `createDate` timestamp ,
  `orderUuid` varchar(32) DEFAULT NULL,
  `opAccountUuid` varchar(32) DEFAULT NULL COMMENT '操作人',
   `comment` varchar(2000) DEFAULT NULL,
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
  `productEffectTimeEnd` datetime DEFAULT NULL COMMENT '产品使用结束时间',
  `lastOpDate` timestamp NOT NULL DEFAULT  current_timestamp(),
  `createDate` timestamp ,
  `productUuid` varchar(32) NOT NULL COMMENT '产品ID',
  `productName` varchar(100) NOT NULL COMMENT '产品名称',
  `productType` varchar(50) DEFAULT NULL COMMENT '产品类型',
  `descriptionData` varchar(500) DEFAULT NULL COMMENT '产品说明，json格式',
  `productChargeModel` varchar(50) DEFAULT NULL COMMENT '计费方式--按月，按年',
  `duration` int(10) unsigned NOT NULL DEFAULT 0,
  `productStatus` tinyint(1) unsigned DEFAULT 1 COMMENT '产品是否开通',
  `callBackData` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `PriceRefRenewVO` */

DROP TABLE IF EXISTS `PriceRefRenewVO`;

CREATE TABLE `PriceRefRenewVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账户id',
  `renewUuid` varchar(32) DEFAULT NULL COMMENT '续费id',
  `productPriceUnitUuid` varchar(32) DEFAULT NULL COMMENT '单价id',
  `lastOpDate` timestamp NOT NULL DEFAULT  current_timestamp(),
  `createDate` timestamp ,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `PriceRefRenewVO` */

/*Table structure for table `ProductPriceUnitVO` */

DROP TABLE IF EXISTS `ProductPriceUnitVO`;

CREATE TABLE `ProductPriceUnitVO` (
  `uuid` varchar(32) NOT NULL,
  `productTypeCode` varchar(50) DEFAULT NULL,
  `productTypeName` varchar(125) DEFAULT NULL,
  `categoryCode` varchar(50) DEFAULT NULL,
  `categoryName` varchar(125) DEFAULT NULL,
  `areaCode` varchar(50) DEFAULT NULL,
  `areaName` varchar(125) DEFAULT NULL,
  `lineCode` varchar(50) DEFAULT NULL,
  `lineName` varchar(125) DEFAULT NULL,
  `configCode` varchar(50) DEFAULT NULL,
  `configName` varchar(125) DEFAULT NULL,
  `unitPrice` int(10) DEFAULT NULL,
  `lastOpDate` timestamp,
  `createDate` timestamp,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `unique_01` (`productTypeCode`,`categoryCode`,`areaCode`,`lineCode`,`configCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `lastOpDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
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
  `lastOpDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
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
  `lastOpDate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
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
  `lastOpDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `priceOneMonth` decimal(12,4) DEFAULT NULL,
  `expiredTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `UNI_ACCOUNT_PRODUCT_ID` (`accountUuid`,`productUuid`)
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
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
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
  `timeStart` TIMESTAMP  ,
  `timeEnd` TIMESTAMP  ,
  `slaPrice` DECIMAL(12,4) DEFAULT NULL COMMENT '赔偿时价格',
  `createDate` TIMESTAMP ,
  `lastOpDate` TIMESTAMP  ,
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
  UNIQUE KEY `unique_1` (`productCategoryUuid`,`areaCode`,`lineCode`,`configCode`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;



