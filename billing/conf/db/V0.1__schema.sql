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
  `productPriceDiscountDetail` varchar(1000) DEFAULT NULL COMMENT '产品价格信息',
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
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
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

LOCK TABLES `ProductCategoryVO` WRITE;
INSERT INTO `ProductCategoryVO` VALUES ('ABROAD','ABROAD','跨国','TUNNEL','专线网络','enable','2018-01-18 05:21:54','2017-10-30 09:18:12'),('BANDWIDTH','BANDWIDTH','公网带宽','ECP','互联云','enable','2018-01-18 05:21:54','2017-10-30 09:18:12'),('CITY','CITY','同城','TUNNEL','专线网络','enable','2018-01-18 05:21:54','2017-10-30 07:46:12'),('DISK','DISK','数据盘','ECP','互联云','enable','2018-01-02 01:43:41','2017-10-30 07:46:12'),('EXCLUSIVE','EXCLUSIVE','独享端口','PORT','端口','enable','2018-01-10 07:59:59','2017-10-30 07:46:12'),('HOST','HOST','云服务器','ECP','互联云','enable','2018-01-18 05:21:54','2017-10-30 07:46:12'),('LONG','LONG','长传','TUNNEL','专线网络','enable','2018-01-18 05:21:54','2017-10-30 07:46:12'),('POOLNETWORK','POOLNETWORK','资源池网络','ECP','互联云','enable','2018-01-18 05:21:54','2018-01-02 01:42:29'),('REGION','REGION','区域','TUNNEL','专线网络','enable','2018-01-18 05:21:54','2017-10-30 07:46:12'),('RESOURCEPOOL','RESOURCEPOOL','资源池','ECP','互联云','enable','2018-01-02 01:46:37','2017-10-30 07:46:12'),('SHARE','SHARE','共享端口','PORT','端口','enable','2018-01-10 08:00:28','2017-10-30 07:46:12'),('VPN','VPN','VPN','VPN','VPN','enable','2018-01-02 01:42:29','2017-10-30 07:46:12');
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
  UNIQUE KEY `unique_1` (`productCategoryUuid`,`areaCode`,`lineCode`,`configCode`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

LOCK TABLES `ProductPriceUnitVO` WRITE;

insert  into `ProductPriceUnitVO`(`uuid`,`productCategoryUuid`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('01af55a079fc436984795f2912dfcf0d','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','50M','50M',7,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('0221e896b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','50M','50M',8800,'2017-11-02 14:31:24','2017-10-23 09:53:23'),('0221e896b7d811e7be4602fsdeac1100','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','10M','10M',7800,'2017-12-12 13:50:06','2017-10-23 09:53:23'),('0221e896bew7d811e7be4602fsdeac11','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','2G','2G',14800,'2017-12-11 13:44:29','2017-10-23 09:53:23'),('069a91321995415793ee9b540cb19685','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','2G','2G',6,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('07cf3480b7dd11e7be460242ac110003','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','fa37e53d2a1849ae91bd437adb77f999','200GB',400,'2017-12-11 15:16:04','2017-10-23 10:29:20'),('07cf3480b7dd11e7be460242ac110004','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','4a29bca42a8f425a9398006ea75c4ab2','700GB',1400,'2017-12-11 15:16:04','2017-12-11 15:16:04'),('0cc4c0b6b7d711e7be460242ac110003','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','20M','20M',7800,'2017-11-03 14:23:12','2017-10-23 09:46:31'),('0ce9851aeaa84a6586a60820f76e9acc','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','DEFAULT','DEFAULT','DEFAULT','0ce9851aeaa84a6586a60820f76e9acc','误删',100,'2017-10-23 10:25:51','2017-10-23 10:25:51'),('0f9c5e1888884143ba96b4917989db92','1f33d476bd4611e7a08b525400c2a7e2','8c142d46ccdc4dffbb4700d323cc7eb3','北京','cecfa2491b864609a990141fae0eb484','北京L3网络','1M','1M',321,'2017-12-07 15:08:02','2017-11-10 13:38:59'),('104b1baae9ed43eeaeeaa55cca82fd00','1f33d476bd4611e7a08b525400c2a7e2','222acda790f64ec282b7da7fb0a92a42','1','104b1baae9ed43eeaeeaa55cca82fd00','误删','1M','1M',100,'2017-12-12 19:06:36','2017-11-10 13:38:59'),('122c3057117641339616624f9cf003ee','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','200M','200M',6,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('13168ec3434b45ddb136c489b2f7fdf8','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','200M','200M',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('13649472b7dd11e7be460242ac110003','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','395b366ead364cc891d4d82b8399ca00','300GB',600,'2017-12-11 15:16:08','2017-10-23 10:29:39'),('1644bf08db7dc11e7be460242ac1100','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','78c50580458941b5927bbdd6677a5a4b','8核CPU/8G内存',600,'2017-12-12 17:19:35','2017-12-12 17:00:41'),('1777824ab7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','100M','100M',10800,'2017-11-02 14:31:24','2017-10-23 09:53:59'),('18e20801b7da11e7be460242ac110003','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','5M','5M',6800,'2017-11-06 15:12:43','2017-10-23 10:08:20'),('18e67b1f09ba4688ab408b9c903eed3d','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','500M','500M',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('18f35ae7b7da11e7be460242ac110003','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','20M','20M',8800,'2017-12-12 13:50:13','2017-10-23 10:08:20'),('1904a1afb7da11e7be460242ac110003','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','50M','50M',8888,'2017-11-06 15:12:49','2017-10-23 10:08:20'),('19146f3bb7da11e7be460242ac110003','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','100M','100M',9800,'2017-11-03 14:42:47','2017-10-23 10:08:20'),('19245871b7da11e7be460242ac110003','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','200M','200M',11800,'2017-12-11 13:44:12','2017-10-23 10:08:21'),('193636cab7da11e7be460242ac110003','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','500M','500M',12800,'2017-11-03 14:42:52','2017-10-23 10:08:21'),('19484aa0b7da11e7be460242ac110003','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','1G','1G',13800,'2017-11-03 14:42:57','2017-10-23 10:08:21'),('195de86cb7da11e7be460242ac110003','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','5G','5G',15800,'2017-11-03 14:43:03','2017-10-23 10:08:21'),('1970b682b7da11e7be460242ac110003','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','10G','10G',168800,'2017-11-02 14:31:24','2017-10-23 10:08:21'),('19d373cfadb4472daef37d6652ee205c','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','500M','500M',3,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('1f3abc69f4b549fd99c0acb01987d692','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','5M','5M',9,'2017-11-09 15:26:43','2017-11-09 15:26:43'),('1f68eb0198c643e08af9a6f732ae8ce4','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','10G','10G',6,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('20eb00e9edb84327910fd2904d0a2aad','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','20M','20M',6,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('21eaa70bcda942199ceba638cc0598c4','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','50M','50M',6,'2017-11-09 15:26:43','2017-11-09 15:26:43'),('240d0d12b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','200M','200M',18800,'2017-11-02 14:31:24','2017-10-23 09:54:20'),('24ec49b874fd4578909cc8a3a2bdd4ad','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','5M','5M',7,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('256896342ecc42d3a0f2c7b43538435f','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','500M','500M',7,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('27c465617d5642cf85ef459ef50d5aef','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','5G','5G',3,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('28fbd11ac50448df80b20f3dc8b7f2fd','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','50M','50M',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('2928159c6cee4d16acb7deaa3c937a6f','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','2G','2G',3,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('2a9a5112542042fd947cbc1dc5d03c02','1f33d476bd4611e7a08b525400c2a7e2','c68fc68c79ed4ac690679dfc4e202148','1','714db554f1a14421beb614b3a6435b40','1','1M','1M',33,'2017-12-07 15:09:42','2017-11-10 13:38:59'),('2ab1fab176ff473683bc08f8d01f71df','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','20M','20M',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('2b392fa05db44ee9a3709300df805a00','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','20M','20M',5,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('2b8fbc9660e540819b387c6303eafffb','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','200M','200M',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('2f4758ecbdb741399f8734d63ba76846','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','1G','1G',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('3025eddd89bb414aa396b8a9070a195a','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','5G','5G',6,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('30d3720cb7d211e7aae30242ac110002','02b70538bd4511e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','SHARE','共享端口',0,'2017-12-12 13:50:33','2017-10-23 09:11:44'),('313d6d3009fb4388939f205f90c5ae44','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','100M','100M',3,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('3367ec7f840341f986aaef72262c63fe','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','10M','10M',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('3535601cfb424baaaf8a87aaedecb93d','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','100M','100M',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('3575883fbd4611e7a08b50000c2a7e2','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','dbaa1edc46a74a1baca71f9c2b410ab0','4核CPU/4G内存',100,'2017-12-12 16:39:23','2017-12-09 11:31:28'),('36e630017ee440e4be03cbebcb20f04e','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','5G','5G',2,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('37ea7b5f3da44b9cb6969660ae39bc42','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','100M','100M',6,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('38b0498d222c44d29aaacd6ff705de0a','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','100M','100M',3,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('3a6b961cb3104941be70e23275347e2a','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','20M','20M',5,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('3afddb563d2342fead7c9e779fd415d5','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','5G','5G',4,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('3bec0121b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','500M','500M',28800,'2017-11-02 14:31:24','2017-10-23 09:55:00'),('3c1d09b00fac46e49cea66fc489056d0','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','2M','2M',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('3cb55e4cb7dc11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','107ba9e34d8042a3aba763443fd19960','4核CPU/16G内存',300,'2017-12-12 17:56:15','2017-10-23 10:23:39'),('3db27aae88484eb597d905b4fab400cb','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','100M','100M',20,'2017-11-17 15:27:38','2017-11-06 16:22:54'),('3fb1fe25ef8c4c36941b684c971f6713','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','5M','5M',9,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('4051aafad6394cdba3b49f37c64d70de','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','1G','1G',8,'2017-11-08 15:38:21','2017-11-08 15:38:21'),('40ccdda35b0c4801ac05f63abee7b32c','1f33d476bd4611e7a08b525400c2a7e2','c68fc68c79ed4ac690679dfc4e202148','上海','5ab81d8796964113a37768042913270e','上海L3网络','1M','1M',100,'2017-12-07 15:07:40','2017-11-10 13:39:06'),('41414a450d1849639a5e25155417d7da','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','10G','10G',5,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('4385b742518a48d0b5e5720802d59727','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','2G','2G',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('47a85e396ea74b93bf3c9bd8e0389c76','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','500M','500M',2,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('495a3fe0b0db41359ceb34decd5614f9','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','500M','500M',6,'2017-11-09 15:26:43','2017-11-09 15:26:43'),('4c1d5e31b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','1G','1G',48800,'2017-11-02 14:31:24','2017-10-23 09:55:27'),('4dc52fc555d34d43b8faf43132e5900f','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','1G','1G',4,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('4dd2014c856a4789aef16ba33789282b','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','50M','50M',4,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('4fe1b50308da4b238c5c1e986cd3222c','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','50M','50M',3,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('54124e01eff34f268b863d66e51cc28a','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','2M','2M',969,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('548b5d54911041e88bf46850958fe2bb','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','10G','10G',6,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('55d0a557c7674d08a382d50fe66a6677','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','2G','2G',5,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('56fbd8fbb7d711e7be460242ac110003','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','50M','50M',4800,'2017-11-10 13:43:18','2017-10-23 09:48:36'),('5784c369b18845a296cd5bba731e5338','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','5G','5G',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('584454934c524200bd9151199a05483d','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','20M','20M',6,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('59b5f829cda64df5b83cf06c6b82f6b9','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','10M','10M',7,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('5b668c4bb7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','2G','2G',88800,'2017-11-02 14:31:24','2017-10-23 09:55:53'),('6063d54dcacd40e0882146806278ee58','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','1G','1G',3,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('61af7591a73340ffb527fa32d99ad019','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','100M','100M',25,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('64388024b7dc11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','311f77fc266a42b8b142d6b0578bf269','8核CPU/12G内存',400,'2017-12-12 17:21:59','2017-10-23 10:24:46'),('644bf08db7dc11e7be460242ac110004','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','9065a74f573d496d8592906a467cfaf1','4核CPU/12G内存',480,'2017-12-12 16:53:02','2017-12-12 16:48:50'),('645d08a8b7dc11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','1221988d23d945778772931e6dff92a5','8核CPU/32G内存',500,'2017-12-11 15:36:00','2017-10-23 10:24:46'),('64736982b7dc11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','63dff35bdd194c9fa3968ba981050c91','1核CPU/2G内存',200,'2017-12-12 16:37:28','2017-10-23 10:24:46'),('64898d67b7dc11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','73e549bd29a3482c824ac6b86a035122','8核CPU/16G内存',700,'2017-12-11 15:38:06','2017-10-23 10:24:46'),('64f7e897b7d711e7be460242ac110003','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','100M','100M',9600,'2017-12-11 13:42:22','2017-10-23 09:48:59'),('6523810a5eeb4d6f82f5adab8dbe91af','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','2G','2G',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('6832cc89b8c3433787b4bb436d70a332','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','10M','10M',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('69c10a17b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','5G','5G',128800,'2017-11-02 14:31:24','2017-10-23 09:56:17'),('69f5e8f6e9a144dc8b0610aa19f515fb','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','10M','10M',6,'2017-11-09 15:26:43','2017-11-09 15:26:43'),('6d56ddc2b7dd11e7be460242ac110003','02b70538bd4511e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','RJ45_1G','电口千兆',2000,'2017-12-12 10:00:54','2017-10-23 10:32:10'),('6d81e6ddb7ce11e7aae30242ac110002','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','2M','2M',3200,'2017-12-12 11:18:33','2017-10-23 08:44:48'),('6d81e6ddb7ce11e7areae30242ac1100','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','10M','10M',6670,'2017-11-10 13:52:17','2017-10-23 08:44:48'),('6e7c04c844fb411798229fa7c7d8bff8','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','5G','5G',8,'2017-11-08 15:38:21','2017-11-08 15:38:21'),('70ca697da547414d8249e6af1fdedf50','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','66f3ef7d634846bfae606af7d0a247a8','4核CPU/8G内存',1000,'2017-12-12 17:57:40','2017-11-02 13:30:18'),('71efa25db7d711e7be460242ac110003','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','200M','200M',11800,'2017-11-03 15:16:37','2017-10-23 09:49:21'),('74b1be8f0e214b06bfc4482b3d617c0f','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','20M','20M',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('75bdab0ab7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','10G','10G',168800,'2017-11-02 14:31:24','2017-10-23 09:56:37'),('7bc8bee0fb374724a492f9da729ea78a','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','100M','100M',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('7d30f48c3b6c4e109e3f5a9ff136d757','8fb5502ddbef11e7b42e060400ef5315','DEFAULT','默认没有','DEFAULT','默认没有','hbb37873ec8c4afbb8394a7e0cdfaeeb','8CPU16GRAM',2000,'2017-11-02 14:31:24','2017-10-23 09:53:23'),('7fe019df93884ed58c5dc8070ead447f','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','100M','100M',2,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('8292ae59b7d711e7be460242ac110003','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','500M','500M',12800,'2017-11-03 15:16:42','2017-10-23 09:49:49'),('8410f62c6b0e430ea45690b19cb55fde','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','5M','5M',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('85146e576216403ea3a70993bec77eaa','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','1G','1G',7,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('891b128e1acf442cbb542ed9b7cd9c62','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','200M','200M',4,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('8a4c03ecb85241149a965570182c46e6','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','500M','500M',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('8b8be80db7dc11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','eeb45baa0aa544d7ad85ef54cdbb04d0','16核CPU/24G内存',800,'2017-12-12 17:24:43','2017-10-23 10:25:51'),('8b9e78f4b7dc11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','7fffc1d08d5b49359bb94ac1e30d913e','16核CPU/32G内存',600,'2017-12-12 17:25:39','2017-10-23 10:25:52'),('8bd55a6db7dc11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','60ff47001bf145ce9b4be7f164900f0d','16核CPU/16G内存',900,'2017-12-12 17:23:26','2017-10-23 10:25:52'),('8fe292e4b7dd11e7be460242ac110003','02b70538bd4511e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','SFP_1G','光口千兆',15000,'2017-12-12 11:30:05','2017-10-23 10:33:08'),('91e4431193474fa499087b670cc6278d','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','5M','5M',6,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('920bff80b7d711e7be460242ac110003','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','1G','1G',13800,'2017-11-03 15:16:46','2017-10-23 09:50:15'),('946a4403352b4c4cae3a2f8bed963d04','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','100M','100M',8,'2017-11-08 15:38:21','2017-11-08 15:38:21'),('96e504d54b154002a8e0b8efbe32a50a','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','5G','5G',5,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('97b1defb98a945a88091fe3acd735112','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','2M','2M',7,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('98a854b0e1244e91a68eafb7fb5f8d72','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','10G','10G',6,'2017-11-09 15:26:43','2017-11-09 15:26:43'),('98b57e4b150e4b4da1f56014bc7f9497','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','2G','2G',3,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('996ba83db7dd11e7be460242ac110003','02b70538bd4511e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','SFP_10G','光口万兆',15000,'2017-12-11 18:28:53','2017-10-23 10:33:24'),('9b07593c354e48d8a28cbd8bb1512217','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','10M','10M',6,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('9b5e0363d6794a779551dcd05c36bd5a','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','2M','2M',9,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('9c1ef5e69b3942aaa18885ee8b56f8ae','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','10G','10G',5,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('9d77d22ab7d711e7be460242ac110003','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','2G','2G',14800,'2017-11-03 15:16:51','2017-10-23 09:50:34'),('9f182aefb7d611e7be460242ac110003','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','5M','5M',5800,'2017-11-07 14:12:42','2017-10-23 09:43:27'),('9f7c3a9f7c4a464cbdbf5c9aa0bbda3e','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','20M','20M',7,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('a07e0404e1054ff8be4841a6957a5c19','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','200M','200M',7,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('a08073f0520a489d87277e6f114072c1','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','10M','10M',3,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('a79fc19cb7d711e7be460242ac110003','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','5G','5G',15800,'2017-11-03 15:16:56','2017-10-23 09:50:51'),('a8dcd6fda91a46169fe3ff503ef31822','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','200M','200M',2,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('a9d235bebd4611e7a08b523200c2a7eb','19d625bebd4611e7a08b523200c2a7e3','DEFAULT','默认没有','DEFAULT','默认没有','20M','20M',301,'2017-12-08 21:55:31','2017-12-08 21:51:29'),('aaea0700ec994839b5ee6989d3bf6f87','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','5M','5M',7,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('ad86c9a8f33a40a8a15ae9509a956804','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','5M','5M',6,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('ae7ef012306849289caabdedceec9503','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','1G','1G',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('b19e20d9837643d2b4371ae3492e2ffd','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','1G','1G',3,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('b1db71b17d684f6eb1759c9bdf4df4b8','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','10M','10M',9,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('b2e870d483024501b93436573c0d2451','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','2M','2M',7,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('b3a54fe91d424402994543b7e2a03f08','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','5G','5G',5,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('b4d22f93ed2c4c60a7c1d3a6f9bb6f1a','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','2M','2M',9,'2017-11-09 15:26:43','2017-11-09 15:26:43'),('b5aa6603b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','2M','2M',2800,'2017-12-11 13:43:21','2017-10-23 09:58:24'),('b5bcf83bb7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','5M','5M',3810,'2017-12-12 11:15:12','2017-10-23 09:58:24'),('b5d224d9b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','10M','10M',4800,'2017-11-02 14:31:24','2017-10-23 09:58:24'),('b5e180feb7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','20M','20M',7800,'2017-11-03 17:05:21','2017-10-23 09:58:25'),('b5f0967bb7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','50M','50M',8800,'2017-11-02 14:31:24','2017-10-23 09:58:25'),('b6009d31b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','100M','100M',11800,'2017-11-07 14:25:18','2017-10-23 09:58:25'),('b6145fa8b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','200M','200M',18800,'2017-11-02 14:31:24','2017-10-23 09:58:25'),('b628d0f5b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','500M','500M',28800,'2017-11-02 14:31:24','2017-10-23 09:58:25'),('b63d64c4b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','1G','1G',48800,'2017-11-02 14:31:24','2017-10-23 09:58:25'),('b64dcc83b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','2G','2G',88800,'2017-11-02 14:31:24','2017-10-23 09:58:25'),('b65cce6cb7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','5G','5G',128800,'2017-11-02 14:31:24','2017-10-23 09:58:25'),('b66bc770b7d811e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','CSJ','长三角','DEFAULT','默认没有','10G','10G',170000,'2017-11-07 14:25:11','2017-10-23 09:58:25'),('b6d8ca0db7d711e7be460242ac110003','4c4f62e9bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','10G','10G',16800,'2017-11-03 15:17:01','2017-10-23 09:51:17'),('bc2508539ee245849f314b7b348ac36e','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','1G','1G',5,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('bccf97a4c61b48bd90e52be7005704bf','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','5M','5M',9,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('bd27babfbc30407fa3c7be0f843b766b','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','10G','10G',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('c3362676c73442bbbc47579c05645bff','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','5M','5M',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('c4819520b7d111e7aae30242ac110002','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','59a8deae9f8f46ebbf9a5e834551c5bd','2核CPU/2G内存',100,'2017-12-07 14:47:17','2017-10-23 09:08:43'),('c6ac5381b7db11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','caf2e033ace5435baa7cc45d1849cb9d','2核CPU/4G内存',200,'2017-12-07 14:46:56','2017-10-23 10:20:21'),('c7f5bc9a32f54953aeb20a93dec3adaf','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','10G','10G',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('c830fab3aadc4c6ba58c41c47a6b06db','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','5G','5G',3,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('c97fd6ef344246d3ac443ddab8b03854','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','10M','10M',6,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('c9d27073396e41ac94a1476766ba0bbb','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','2G','2G',5,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('ca3dad14ec2e4eabadce5648106ed324','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','2M','2M',6,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('cd0dc08b5f104a458e1289c8dd3a419a','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','2M','2M',5,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('cd44b013cfd2488fa17d93cb74f960c1','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','200M','200M',3,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('cf26efc2deb843ce8a12f3dd4d6f301d','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','10M','10M',4,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('d28d739e84114dec825f94d4db161a3a','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','100M','100M',5,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('d4049eeb840b4ad6950ed4701163dbb7','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','500M','500M',6,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('d4227411c3854afc8a3ed9c93f9fe9c7','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','50M','50M',3,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('d46ca64fb7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','2M','2M',2800,'2017-11-02 14:31:24','2017-10-23 10:06:25'),('d47cd46db7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','5M','5M',3800,'2017-11-02 14:31:24','2017-10-23 10:06:25'),('d48cfa45b7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','10M','10M',4800,'2017-11-02 14:31:24','2017-10-23 10:06:25'),('d49d3f55b7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','20M','20M',6800,'2017-11-02 14:31:24','2017-10-23 10:06:26'),('d4b33c9cb7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','50M','50M',8888,'2017-11-06 15:10:33','2017-10-23 10:06:26'),('d4c6fb7ab7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','100M','100M',10800,'2017-11-02 14:31:24','2017-10-23 10:06:26'),('d4db025cb7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','200M','200M',18800,'2017-11-02 14:31:24','2017-10-23 10:06:26'),('d4ea94e07f3c41979b2aaa4b0747645e','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','20M','20M',8,'2017-11-08 15:38:21','2017-11-08 15:38:21'),('d4ebcf30b7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','500M','500M',28800,'2017-11-02 14:31:24','2017-10-23 10:06:26'),('d4fbd940b7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','1G','1G',48800,'2017-11-02 14:31:24','2017-10-23 10:06:26'),('d50bb0f5b7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','2G','2G',10888,'2017-11-03 14:33:44','2017-10-23 10:06:26'),('d51cf834b7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','5G','5G',128800,'2017-11-02 14:31:24','2017-10-23 10:06:26'),('d526454cac8940bdb5fa158e6fba482d','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','10G','10G',8,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('d52eed52b7d911e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','ZSJ','珠三角','DEFAULT','默认没有','10G','10G',168800,'2017-11-02 14:31:24','2017-10-23 10:06:27'),('d75bfb0cbb6b45c48cb00bc619686efb','1f33d476bd4611e7a08b525400c2a7e2','8c142d46ccdc4dffbb4700d323cc7eb3','1','d0d9d5333d4c41e68d1b7e968f691169','1','1M','1M',44,'2017-12-07 15:09:55','2017-11-10 13:38:59'),('d7628148b7ce11e7aae30242ac110002','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','2M','2M',2800,'2017-12-11 13:43:14','2017-10-23 08:47:46'),('d79b7878cbfa4652a460fb10ad26ba39','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','20M','20M',9,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('d79d6eea283d42759c377e533ab6f2be','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','2G','2G',9,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('d977a61835d44607b320667cba2ce182','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','10M','10M',9,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('d9a7e450b7db11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','005a7e6d1c354d81bf35a7f8afda5dcf','2核CPU/8G内存',450,'2017-12-07 14:46:16','2017-10-23 10:20:53'),('daeba9ff511f4ba9b1f6719460b43e9e','8fb5502ddbef11e7b42e060400ef5315','DEFAULT','默认没有','DEFAULT','默认没有','0c737873ec8c4afbb8394a7e0cdfaeeb','4CPU16GRAM',1000,'2017-11-02 14:31:24','2017-10-23 09:53:23'),('dbc0b19ab7d711e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','5M','5M',3800,'2017-11-02 14:31:24','2017-10-23 09:52:19'),('dd1b9f796bed40188a311544ee545622','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','10G','10G',7,'2017-11-10 13:08:12','2017-11-10 13:08:12'),('e1387f092a0f476bba9cbbeda8c7d5d3','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','200M','200M',3,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('e283b62ae02d45caae4c8565ad32eede','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','200M','200M',6,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('e2b1ba5abef04c19b4e6642fd629ccdc','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','2G','2G',6,'2017-11-09 15:26:43','2017-11-09 15:26:43'),('e62633e89e2b4afb8054e08c4d7150e8','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','5G','5G',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('e6fafaa6c0d94d2abe080dc0e06f6db7','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','5M','5M',9,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('e71b8feac9464ad09a83199f63d9989b','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','1G','1G',3,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('e740c197b7d111e7aae30242ac110001','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','71cd5ed52dac407ba13d696a638a3bb9','400GB',800,'2017-12-11 15:18:03','2017-12-11 15:17:57'),('e740c197b7d111e7aae30242ac110002','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','a810441b24e94a5e80def649a8a097b2','100GB',200,'2017-12-11 16:37:27','2017-10-23 09:09:41'),('e740c197b7d111e7aae30242ac110003','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','e3de9fa919a748b7959754843aaa0f30','600GB',1200,'2017-12-11 15:18:29','2017-12-11 15:18:00'),('e740c197b7d111e7aae30242ac110004','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','c974e9446da44ce1a449add2c1767756','800GB',1600,'2017-12-11 15:19:57','2017-12-11 15:19:52'),('e740c197b7d111e7aae30242ac110006','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','ce38221f82464be48ebb918fbe75cb38','1000GB',2000,'2017-12-11 15:21:38','2017-12-11 15:21:36'),('e740c197b7d111e7aae30242ac110007','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','109b517f76b041b29eb631317c4f7eab','900GB',1800,'2017-12-11 15:21:35','2017-12-11 15:21:34'),('e8bde368b7d711e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','10M','10M',5800,'2017-11-07 12:25:38','2017-10-23 09:52:40'),('e9ef6e0b40c64d8aa7fe918b73e547e0','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','2M','2M',9,'2017-11-13 10:55:41','2017-11-08 15:39:03'),('e9f153abb0964ca0a521687841fd133d','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','500M','500M',3,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('ec0dbe62b7db11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','005a7e6d1c354d81bf35a7f8afda5dca','1核CPU/1G内存',100,'2017-12-11 15:30:46','2017-10-23 10:21:24'),('edb5881e410741d1af44ca8107d4f922','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','1460673b27db4faab272e3488e0a8622','500GB',1000,'2017-12-11 15:16:14','2017-11-17 18:31:53'),('ee611240178a4c26992dfdc357c0c843','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/美国','日本/美国','500M','500M',6,'2017-11-06 16:22:54','2017-11-06 16:22:54'),('ee915166172748e39cf0effd0efee4b1','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','1G','1G',6,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('ef543b174c144e04ab016aeddb216b55','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','日本/加拿大','日本/加拿大','50M','50M',7,'2017-11-08 16:49:23','2017-11-08 16:49:23'),('f2127cd6ca374139a410a57962ba8020','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','20M','20M',3,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('f21a38946cf44f24bfeff4cbde5ef708','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','深圳/法国','深圳/法国','50M','50M',3,'2017-11-08 16:43:05','2017-11-08 16:43:05'),('f414e90339204cd4987e1bc526fe0a5f','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','青岛/美国','青岛/美国','200M','200M',8,'2017-11-08 15:38:21','2017-11-08 15:38:21'),('f6117a0db5404dc3bacea0a7e589ee88','417add07bd4611e7a08b525400c2a7e2','ABROAD','国外','美国/法国','美国/法国','10G','10G',6,'2017-11-08 15:39:03','2017-11-08 15:39:03'),('f6119d1eb7db11e7be460242ac110003','3575883fbd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','bb0cf98ad5d349278b7a9009bedcfcdf','2核CPU/12G内存',500,'2017-12-11 15:47:41','2017-10-23 10:21:41'),('f6ed9ff5b7d711e7be460242ac110003','59d625bebd4611e7a08b525400c2a7e2','JJJ','京津冀','DEFAULT','默认没有','20M','20M',6800,'2017-11-02 14:31:24','2017-10-23 09:53:04'),('f730723feac94945a0c4e80179f5474a','29dacd2ebd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','f730723feac94945a0c4e80179f5474a','145',100,'2017-12-11 15:16:04','2017-12-11 15:16:04'),('f7d82fc6f2bf4cdb9cd828242403653d','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','北京/加拿大','北京/加拿大','50M','50M',5,'2017-11-07 16:56:30','2017-11-07 16:56:30'),('f93652c470e8435bbebdb9c15738ca9e','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/america','上海/america','50M','50M',6,'2017-11-17 15:27:14','2017-11-17 15:27:14'),('f9a58710b7ce11e7aae30242ac110002','53d2e637bd4611e7a08b525400c2a7e2','DEFAULT','默认没有','DEFAULT','默认没有','2M','2M',3100,'2017-12-13 09:48:50','2017-10-23 08:48:43'),('fc249c0827e64aff9776823e9e1cafff','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','南京/美国','南京/美国','2M','2M',1,'2017-11-10 13:12:23','2017-11-10 13:12:23'),('fce967e09d3b4afbb98d73f59aa60126','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','上海/法国','上海/法国','500M','500M',5,'2017-11-07 16:58:43','2017-11-07 16:58:43'),('ffbd9a6f84dc40ee82de974f914a61f6','417add07bd4611e7a08b525400c2a7e2','CHINA2ABROAD','国内到国外','昆山/法国','昆山/法国','2G','2G',7,'2017-11-10 13:08:12','2017-11-10 13:08:12');

UNLOCK TABLES;




