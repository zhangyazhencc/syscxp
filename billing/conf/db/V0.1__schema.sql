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

/*Table structure for table `AccountDischargeVO` */

DROP TABLE IF EXISTS `AccountDischargeVO`;

CREATE TABLE `AccountDischargeVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账户id',
  `productType` varchar(50) DEFAULT NULL COMMENT '产品类型',
  `category` varchar(50) DEFAULT NULL COMMENT '产品小分类',
  `disCharge` tinyint(3) unsigned DEFAULT 100 COMMENT '折扣',
  `lastOpDate` timestamp NOT NULL  DEFAULT current_timestamp(),
  `createDate` timestamp  ,
  `categoryName` varchar(256) DEFAULT NULL,
   `productTypeName` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  KEY `Uni_accountUuid_productType` (`accountUuid`,`productType`,`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `AccountDischargeVO` */

/*Table structure for table `BillVO` */

DROP TABLE IF EXISTS `BillVO`;

CREATE TABLE `BillVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `timeStart` timestamp NULL DEFAULT NULL COMMENT '账单开始时间',
  `timeEnd` timestamp NULL DEFAULT NULL COMMENT '账单结束时间',
  `totolPayCash` decimal(12,4) DEFAULT NULL COMMENT '总支出(现金)',
  `totalPayPresent` decimal(12,4) DEFAULT NULL COMMENT '总支出(赠送)',
  `totalIncomeCash` decimal(12,4) DEFAULT NULL COMMENT '总收入(现金)',
  `totalIncomePresent` decimal(12,4) DEFAULT NULL COMMENT '总收入(赠送)',
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
  UNIQUE KEY `NewIndex1` (`outTradeNO`),
  UNIQUE KEY `NewIndex2` (`tradeNO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `DealDetailVO` */

/*Table structure for table `GlobalConfigVO` */

DROP TABLE IF EXISTS `GlobalConfigVO`;

CREATE TABLE `GlobalConfigVO` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `category` varchar(64) NOT NULL,
  `defaultValue` text DEFAULT NULL,
  `value` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

/*Table structure for table `JobQueueEntryVO` */

DROP TABLE IF EXISTS `JobQueueEntryVO`;

CREATE TABLE `JobQueueEntryVO` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `jobQueueId` bigint(20) unsigned NOT NULL,
  `state` varchar(128) NOT NULL,
  `context` blob DEFAULT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `issuerManagementNodeId` varchar(32) DEFAULT NULL,
  `restartable` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `inDate` timestamp NOT NULL DEFAULT current_timestamp(),
  `doneDate` timestamp NULL DEFAULT NULL,
  `errText` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `JobQueueEntryVO` */

/*Table structure for table `JobQueueVO` */

DROP TABLE IF EXISTS `JobQueueVO`;

CREATE TABLE `JobQueueVO` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `workerManagementNodeId` varchar(32) DEFAULT NULL,
  `takenDate` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `JobQueueVO` */

/*Table structure for table `ManagementNodeVO` */

DROP TABLE IF EXISTS `ManagementNodeVO`;

CREATE TABLE `ManagementNodeVO` (
  `uuid` varchar(32) NOT NULL,
  `hostName` varchar(255) DEFAULT NULL,
  `port` int(10) unsigned DEFAULT NULL,
  `state` varchar(128) NOT NULL,
  `joinDate` timestamp NOT NULL DEFAULT current_timestamp(),
  `heartBeat` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `ManagementNodeVO` */

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
  `productDescription` varchar(500) DEFAULT NULL COMMENT '产品说明，json格式',
  `productChargeModel` varchar(50) DEFAULT NULL COMMENT '计费方式--按月，按年',
  `duration` int(10) unsigned NOT NULL DEFAULT 0,
  `productStatus` tinyint(1) unsigned DEFAULT 1 COMMENT '产品是否开通',
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
  `productName` varchar(256) DEFAULT NULL COMMENT '价格-产品名称',
  `productType` varchar(50) DEFAULT NULL COMMENT '产品类型',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `config` varchar(128) DEFAULT NULL COMMENT '配置',
  `priceUnit` int(10) unsigned NOT NULL COMMENT '单价',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `lastOpDate` timestamp NOT NULL DEFAULT current_timestamp(),
  `createDate` timestamp ,
   `categoryName` varchar(200) DEFAULT NULL COMMENT 'category名称',
  `productTypeName` varchar(200) DEFAULT NULL COMMENT '产品分类名称',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `NewIndex1` (`productType`,`category`,`config`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `ProductPriceUnitVO` */

insert  into `ProductPriceUnitVO`(`uuid`,`productName`,`productType`,`category`,`config`,`priceUnit`,`comment`,`lastOpDate`,`createDate`) values ('05f9a139824711e797190242ac110002','光口(SFP) 10Gbps','PORT','SFP','10Gbps',800,'包含对应端口和光模块','2017-08-16 05:52:01','2017-08-16 05:52:01'),('10a56d5c824511e797190242ac110002','本地专线-50M','TUNNEL','SHORT','50M',4800,'本地专线','2017-08-16 05:38:00','2017-08-16 05:38:00'),('1d8fcaaf824511e797190242ac110002','本地专线-100M','TUNNEL','SHORT','100M',6800,'本地专线','2017-08-16 05:38:21','2017-08-16 05:38:21'),('24febbc9824511e797190242ac110002','本地专线-200M','TUNNEL','SHORT','200M',8800,'本地专线','2017-08-16 05:38:34','2017-08-16 05:38:34'),('307ef6fb824511e797190242ac110002','本地专线-500M','TUNNEL','SHORT','500M',10800,'本地专线','2017-08-16 05:38:53','2017-08-16 05:38:53'),('3e0fdc5c824511e797190242ac110002','本地专线-1G','TUNNEL','SHORT','1G',18800,'本地专线','2017-08-16 05:39:16','2017-08-16 05:39:16'),('44862efe824511e797190242ac110002','本地专线-2G','TUNNEL','SHORT','2G',28800,'本地专线','2017-08-16 05:39:27','2017-08-16 05:39:27'),('4f68ea88824511e797190242ac110002','本地专线-5G','TUNNEL','SHORT','5G',48800,'本地专线','2017-08-16 05:39:45','2017-08-16 05:39:45'),('5600ff26824511e797190242ac110002','本地专线-10G','TUNNEL','SHORT','10G',88800,'本地专线','2017-08-16 05:39:56','2017-08-16 05:39:56'),('6e990e95824511e797190242ac110002','长途专线-10M','TUNNEL','LONG','10M',6180,'长途专线','2017-08-16 05:40:37','2017-08-16 05:40:37'),('7c52a61c824511e797190242ac110002','长途专线-20M','TUNNEL','LONG','20M',7180,'长途专线','2017-08-16 05:41:00','2017-08-16 05:41:00'),('86cd842a824511e797190242ac110002','长途专线-50M','TUNNEL','LONG','50M',9800,'长途专线','2017-08-16 05:41:18','2017-08-16 05:41:18'),('927c3282824511e797190242ac110002','长途专线-100M','TUNNEL','LONG','100M',14800,'长途专线','2017-08-16 05:41:38','2017-08-16 05:41:38'),('99dfecdd824511e797190242ac110002','长途专线-200M','TUNNEL','LONG','200M',16800,'长途专线','2017-08-16 05:41:50','2017-08-16 05:41:50'),('a2572fd092c811e7bbff0242ac110003','阿里端口带宽','PORT','ALIBABA','5M',1000,'阿里端口带宽5M','2017-08-16 05:37:28','2017-08-16 05:37:28'),('a2cc4040824511e797190242ac110002','长途专线-500M','TUNNEL','LONG','500M',20800,'长途专线','2017-08-16 05:42:05','2017-08-16 05:42:05'),('aeb504c0824511e797190242ac110002','长途专线-1G','TUNNEL','LONG','1G',36800,'长途专线','2017-08-16 05:42:25','2017-08-16 05:42:25'),('bbcf7506824511e797190242ac110002','长途专线-2G','TUNNEL','LONG','2G',61800,'长途专线','2017-08-16 05:42:47','2017-08-16 05:42:47'),('c105f929824611e797190242ac110002','电口(RJ45) 10M/100M 自适应','PORT','RJ45','10M/100M-ADAPTION',200,'包含对应端口和双绞线','2017-08-16 05:50:05','2017-08-16 05:50:05'),('c90b30a1824511e797190242ac110002','长途专线-3G','TUNNEL','LONG','3G',87800,'长途专线','2017-08-16 05:43:09','2017-08-16 05:43:09'),('d5b48a80824511e797190242ac110002','长途专线-4G','TUNNEL','LONG','4G',112800,'长途专线','2017-08-16 05:43:30','2017-08-16 05:43:30'),('dec70486824511e797190242ac110002','长途专线-5G','TUNNEL','LONG','5G',134800,'长途专线','2017-08-16 05:43:46','2017-08-16 05:43:46'),('ea1d79f3824511e797190242ac110002','长途专线-10G','TUNNEL','LONG','10G',213800,'长途专线','2017-08-16 05:44:05','2017-08-16 05:44:05'),('ef8b5047824411e797190242ac110002','本地专线-10M','TUNNEL','SHORT','10M',2800,'本地专线','2017-08-16 05:37:04','2017-08-16 05:37:04'),('fb3268d7824611e797190242ac110002','光口(SFP) 1Gbps','PORT','SFP','1Gbps',300,'包含对应端口和光模块','2017-08-16 05:51:43','2017-08-16 05:51:43'),('fdce3926824411e797190242ac110002','本地专线-20M','TUNNEL','SHORT','20M',3800,'本地专线','2017-08-16 05:37:28','2017-08-16 05:37:28');


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
  `lastOpDate` timestamp NOT NULL DEFAULT  current_timestamp(),
  `createDate` timestamp ,
  `isDefault` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `comment` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ReceiptPostAddressVO` */

DROP TABLE IF EXISTS `ReceiptPostAddressVO`;

CREATE TABLE `ReceiptPostAddressVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账号id',
  `name` varchar(200) DEFAULT NULL COMMENT '姓名',
  `isDefault` tinyint(1) unsigned DEFAULT 0,
  `telephone` varchar(30) DEFAULT NULL COMMENT '电话号码',
  `address` varchar(500) DEFAULT NULL COMMENT '详细地址',
  `lastOpDate` timestamp NOT NULL DEFAULT current_timestamp(),
  `createDate` timestamp,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ReceiptVO` */

DROP TABLE IF EXISTS `ReceiptVO`;

CREATE TABLE `ReceiptVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `total` decimal(12,4) DEFAULT NULL COMMENT '开票金额',
  `applyTime` timestamp  NULL  DEFAULT NULL COMMENT '申请时间',
  `state` varchar(50) DEFAULT NULL COMMENT '状态',
  `receiptInfoUuid` varchar(32) DEFAULT NULL COMMENT '发票开票信息id',
  `receiptAddressUuid` varchar(32) DEFAULT NULL COMMENT '发票邮寄地址',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账户id',
  `lastOpDate` timestamp NOT NULL DEFAULT  current_timestamp(),
  `createDate` timestamp ,
  `commet` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `RenewVO` */

DROP TABLE IF EXISTS `RenewVO`;

CREATE TABLE `RenewVO` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账号主键',
  `isRenewAuto` tinyint(2) unsigned DEFAULT 1 COMMENT '是否自动续费，1，自动，2关闭',
  `productUuid` varchar(32) NOT NULL COMMENT '产品ID',
  `productName` varchar(100) NOT NULL COMMENT '产品名称',
  `productType` varchar(50) DEFAULT NULL COMMENT '产品类型',
  `productDescription` varchar(2000) DEFAULT NULL,
  `productChargeModel` varchar(50) DEFAULT NULL COMMENT '计费方式--按月，按年',
  `lastOpDate` timestamp NOT NULL DEFAULT  current_timestamp(),
  `createDate` timestamp ,
  `pricePerDay` decimal(12,4) DEFAULT NULL,
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
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `NotifyOrderVO`;
create table `NotifyOrderVO` (
	`uuid` varchar (32),
	`orderUuid` varchar (32),
	`status` varchar (255),
	`notifyTimes` tinyint (2),
	`createDate` timestamp ,
	`lastOpDate` timestamp ,
	`url` varchar (255)
);





