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

/*Data for the table `ProductPriceUnitVO` */

insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('0221e896b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','50M','50M',8800,'2017-10-23 09:53:23','2017-10-23 09:53:23');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('07cf3480b7dd11e7be460242ac110003','VHOST','互联云','DISK','数据盘','DEFAULT','默认没有','DEFAULT','默认没有','200GB','200GB',200,'2017-10-23 10:29:20','2017-10-23 10:29:20');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('0c5ac2a0a6f94579b7d0aee5a673701f','VHOST','互联云','BANDWIDTH','带宽',NULL,NULL,NULL,NULL,'2核CPU/2G内存','2核CPU/2G内存',500,'2017-10-24 07:16:53','2017-10-24 07:16:53');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('0cc4c0b6b7d711e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','20M','20M',6800,'2017-10-23 09:46:31','2017-10-23 09:46:31');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('111443dab7d111e7aae30242ac110002','VHOST','互联云','BANDWIDTH','带宽','北京东方广场','北京东方广场','北京BGP','北京BGP','5M','5M',100,'2017-10-23 09:03:42','2017-10-23 09:03:42');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('13649472b7dd11e7be460242ac110003','VHOST','互联云','DISK','数据盘','DEFAULT','默认没有','DEFAULT','默认没有','300GB','300GB',300,'2017-10-23 10:29:39','2017-10-23 10:29:39');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('1777824ab7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','100M','100M',10800,'2017-10-23 09:53:59','2017-10-23 09:53:59');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('18e20801b7da11e7be460242ac110003','TUNNEL','专线','LONG','长传','DEFAULT','默认没有','DEFAULT','默认没有','5M','5M',4800,'2017-10-23 10:08:20','2017-10-23 10:08:20');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('18f35ae7b7da11e7be460242ac110003','TUNNEL','专线','LONG','长传','DEFAULT','默认没有','DEFAULT','默认没有','20M','20M',6800,'2017-10-23 10:08:20','2017-10-23 10:08:20');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('1904a1afb7da11e7be460242ac110003','TUNNEL','专线','LONG','长传','DEFAULT','默认没有','DEFAULT','默认没有','50M','50M',8800,'2017-10-23 10:08:20','2017-10-23 10:08:20');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('19146f3bb7da11e7be460242ac110003','TUNNEL','专线','LONG','长传','DEFAULT','默认没有','DEFAULT','默认没有','100M','100M',10800,'2017-10-23 10:08:20','2017-10-23 10:08:20');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('19245871b7da11e7be460242ac110003','TUNNEL','专线','LONG','长传','DEFAULT','默认没有','DEFAULT','默认没有','200M','200M',18800,'2017-10-23 10:08:21','2017-10-23 10:08:21');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('193636cab7da11e7be460242ac110003','TUNNEL','专线','LONG','长传','DEFAULT','默认没有','DEFAULT','默认没有','500M','500M',28800,'2017-10-23 10:08:21','2017-10-23 10:08:21');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('19484aa0b7da11e7be460242ac110003','TUNNEL','专线','LONG','长传','DEFAULT','默认没有','DEFAULT','默认没有','1G','1G',48800,'2017-10-23 10:08:21','2017-10-23 10:08:21');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('195de86cb7da11e7be460242ac110003','TUNNEL','专线','LONG','长传','DEFAULT','默认没有','DEFAULT','默认没有','5G','5G',128800,'2017-10-23 10:08:21','2017-10-23 10:08:21');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('1970b682b7da11e7be460242ac110003','TUNNEL','专线','LONG','长传','DEFAULT','默认没有','DEFAULT','默认没有','10G','10G',168800,'2017-10-23 10:08:21','2017-10-23 10:08:21');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('240d0d12b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','200M','200M',18800,'2017-10-23 09:54:20','2017-10-23 09:54:20');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('250307adf76b485caf04b130ca6bfdb3','VHOST','互联云','BANDWIDTH','带宽','昆山花桥','昆山花桥','电信','电信','5M','5M',200,'2017-10-24 07:04:36','2017-10-24 07:04:36');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('30d3720cb7d211e7aae30242ac110002','PORT','端口','DEFAULT','默认没有','DEFAULT','默认没有','DEFAULT','默认没有','SHAREPORT','共享端口',300,'2017-10-23 09:11:44','2017-10-23 09:11:44');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3bec0121b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','500M','500M',28800,'2017-10-23 09:55:00','2017-10-23 09:55:00');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3c6a0275b7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','4CPU2G','4核CPU/2G内存',200,'2017-10-23 10:23:39','2017-10-23 10:23:39');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3c7d24b5b7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','4CPU4G','4核CPU/4G内存',300,'2017-10-23 10:23:39','2017-10-23 10:23:39');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3c8e6bceb7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','4CPU6G','4核CPU/6G内存',400,'2017-10-23 10:23:39','2017-10-23 10:23:39');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3ca17f6db7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','4CPU12G','4核CPU/12G内存',500,'2017-10-23 10:23:39','2017-10-23 10:23:39');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3cb55e4cb7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','4CPU16G','4核CPU/16G内存',600,'2017-10-23 10:23:39','2017-10-23 10:23:39');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3d5947bab7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','2M','2M',3800,'2017-10-23 10:16:31','2017-10-23 10:16:31');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3d69cb24b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','5M','5M',4800,'2017-10-23 10:16:31','2017-10-23 10:16:31');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3d7a3000b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','10M','10M',6800,'2017-10-23 10:16:31','2017-10-23 10:16:31');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3d8ac0aeb7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','20M','20M',8800,'2017-10-23 10:16:31','2017-10-23 10:16:31');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3d9b4029b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','50M','50M',10800,'2017-10-23 10:16:31','2017-10-23 10:16:31');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3dabba4db7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','100M','100M',18800,'2017-10-23 10:16:31','2017-10-23 10:16:31');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3dbc2c49b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','200M','200M',28800,'2017-10-23 10:16:31','2017-10-23 10:16:31');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3dccaf3ab7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','500M','500M',38800,'2017-10-23 10:16:32','2017-10-23 10:16:32');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3ddd2e32b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','1G','1G',48800,'2017-10-23 10:16:32','2017-10-23 10:16:32');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3dedb849b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','2G','2G',88800,'2017-10-23 10:16:32','2017-10-23 10:16:32');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3dfe6c68b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','5G','5G',128800,'2017-10-23 10:16:32','2017-10-23 10:16:32');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('3e0f2552b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外到国外','美国/德国','美国/德国','10G','10G',168800,'2017-10-23 10:16:32','2017-10-23 10:16:32');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('4c1d5e31b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','1G','1G',48800,'2017-10-23 09:55:27','2017-10-23 09:55:27');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('56fbd8fbb7d711e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','50M','50M',8800,'2017-10-23 09:48:36','2017-10-23 09:48:36');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('5b668c4bb7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','2G','2G',88800,'2017-10-23 09:55:53','2017-10-23 09:55:53');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('64388024b7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','8CPU2G','8核CPU/2G内存',300,'2017-10-23 10:24:46','2017-10-23 10:24:46');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('644bf08db7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','8CPU4G','8核CPU/4G内存',400,'2017-10-23 10:24:46','2017-10-23 10:24:46');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('645d08a8b7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','8CPU6G','8核CPU/6G内存',500,'2017-10-23 10:24:46','2017-10-23 10:24:46');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('64736982b7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','8CPU12G','8核CPU/12G内存',600,'2017-10-23 10:24:46','2017-10-23 10:24:46');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('64898d67b7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','8CPU16G','8核CPU/16G内存',700,'2017-10-23 10:24:46','2017-10-23 10:24:46');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('64f7e897b7d711e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','100M','100M',10800,'2017-10-23 09:48:59','2017-10-23 09:48:59');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('687370556ec34a33a6a19f7372130d21','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/日本','上海/日本','50M','50M',2000,'2017-10-24 07:50:57','2017-10-24 07:50:57');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('69c10a17b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','5G','5G',128800,'2017-10-23 09:56:17','2017-10-23 09:56:17');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6b7a0b27b7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','5M','5M',4800,'2017-10-23 10:10:39','2017-10-23 10:10:39');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6d56ddc2b7dd11e7be460242ac110003','PORT','端口','DEFAULT','默认没有','DEFAULT','默认没有','DEFAULT','默认没有','EI-100M-1000M','电口100M/1000M自适应',200,'2017-10-24 17:02:24','2017-10-23 10:32:10');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6d8139bbb7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','2M','2M',3800,'2017-10-23 10:17:52','2017-10-23 10:17:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6d81e6ddb7ce11e7aae30242ac110002','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','2M','2M',2800,'2017-10-23 08:44:48','2017-10-23 08:44:48');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6d97bcb1b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','5M','5M',4800,'2017-10-23 10:17:52','2017-10-23 10:17:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6dac7d4bb7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','10M','10M',6800,'2017-10-23 10:17:52','2017-10-23 10:17:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6dc23d11b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','20M','20M',8800,'2017-10-23 10:17:52','2017-10-23 10:17:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6dd83682b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','50M','50M',10800,'2017-10-23 10:17:52','2017-10-23 10:17:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6dee4bb9b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','100M','100M',18800,'2017-10-23 10:17:52','2017-10-23 10:17:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6e0299d9b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','200M','200M',28800,'2017-10-23 10:17:52','2017-10-23 10:17:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6e178784b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','500M','500M',38800,'2017-10-23 10:17:53','2017-10-23 10:17:53');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6e287598b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','1G','1G',48800,'2017-10-23 10:17:53','2017-10-23 10:17:53');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6e392eedb7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','2G','2G',88800,'2017-10-23 10:17:53','2017-10-23 10:17:53');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6e4a0f32b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','5G','5G',128800,'2017-10-23 10:17:53','2017-10-23 10:17:53');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('6e5d6fb6b7db11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国外内部','新加坡/新加坡','新加坡/新加坡','10G','10G',168800,'2017-10-23 10:17:53','2017-10-23 10:17:53');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('71efa25db7d711e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','200M','200M',18800,'2017-10-23 09:49:21','2017-10-23 09:49:21');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('75bdab0ab7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','10G','10G',168800,'2017-10-23 09:56:37','2017-10-23 09:56:37');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('78d961eab7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','10M','10M',6800,'2017-10-23 10:11:01','2017-10-23 10:11:01');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('805aafc83ee2423b9be0a91230cdf387','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/日本','上海/日本','10M','10M',500,'2017-10-24 07:50:57','2017-10-24 07:50:57');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('8292ae59b7d711e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','500M','500M',28800,'2017-10-23 09:49:49','2017-10-23 09:49:49');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('8b8be80db7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','16CPU2G','16核CPU/2G内存',500,'2017-10-23 10:25:51','2017-10-23 10:25:51');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('8b9e78f4b7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','16CPU4G','16核CPU/4G内存',600,'2017-10-23 10:25:52','2017-10-23 10:25:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('8bb03172b7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','16CPU6G','16核CPU/6G内存',700,'2017-10-23 10:25:52','2017-10-23 10:25:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('8bc079a1b7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','20M','20M',8800,'2017-10-23 10:11:33','2017-10-23 10:11:33');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('8bc10908b7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','16CPU12G','16核CPU/12G内存',800,'2017-10-23 10:25:52','2017-10-23 10:25:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('8bd55a6db7dc11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','16CPU16G','16核CPU/16G内存',900,'2017-10-23 10:25:52','2017-10-23 10:25:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('8fa13c25b7d011e7aae30242ac110002','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','2M','2M',3800,'2017-10-23 09:00:04','2017-10-23 09:00:04');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('8fe292e4b7dd11e7be460242ac110003','PORT','端口','DEFAULT','默认没有','DEFAULT','默认没有','DEFAULT','默认没有','OP-1Gps','光口1Gps',300,'2017-10-24 17:02:33','2017-10-23 10:33:08');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('920bff80b7d711e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','1G','1G',48800,'2017-10-23 09:50:15','2017-10-23 09:50:15');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('96e7c865b7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','50M','50M',10800,'2017-10-23 10:11:52','2017-10-23 10:11:52');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('996ba83db7dd11e7be460242ac110003','PORT','端口','DEFAULT','默认没有','DEFAULT','默认没有','DEFAULT','默认没有','OP-10Gps','光口10Gps',500,'2017-10-24 17:02:40','2017-10-23 10:33:24');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('9bf49bc06ff64840be963764ac805138','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/日本','上海/日本','20M','20M',100,'2017-10-24 07:50:57','2017-10-24 07:50:57');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('9d77d22ab7d711e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','2G','2G',88800,'2017-10-23 09:50:34','2017-10-23 09:50:34');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('9f182aefb7d611e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','5M','5M',3800,'2017-10-23 09:43:27','2017-10-23 09:43:27');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('a2d720f7766c4c2aa4b75db4f83af8a9','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/日本','上海/日本','200M','200M',10000,'2017-10-24 07:50:57','2017-10-24 07:50:57');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('a503ddbab7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','100M','100M',18800,'2017-10-23 10:12:15','2017-10-23 10:12:15');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('a79fc19cb7d711e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','5G','5G',128800,'2017-10-23 09:50:51','2017-10-23 09:50:51');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('af9234265f19484cbf922b52679f31de','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/日本','上海/日本','5M','5M',200,'2017-10-24 07:50:57','2017-10-24 07:50:57');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b383e280b7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','200M','200M',28800,'2017-10-23 10:12:40','2017-10-23 10:12:40');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b5aa6603b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','长三角','DEFAULT','默认没有','2M','2M',2800,'2017-10-23 10:05:10','2017-10-23 09:58:24');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b5bcf83bb7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','长三角','DEFAULT','默认没有','5M','5M',3800,'2017-10-23 10:05:13','2017-10-23 09:58:24');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b5d224d9b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','长三角','DEFAULT','默认没有','10M','10M',4800,'2017-10-23 10:05:16','2017-10-23 09:58:24');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b5e180feb7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','长三角','DEFAULT','默认没有','20M','20M',6800,'2017-10-23 10:05:18','2017-10-23 09:58:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b5f0967bb7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','长三角','DEFAULT','默认没有','50M','50M',8800,'2017-10-23 10:05:20','2017-10-23 09:58:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b6009d31b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','长三角','DEFAULT','默认没有','100M','100M',10800,'2017-10-23 10:05:24','2017-10-23 09:58:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b6145fa8b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','长三角','DEFAULT','默认没有','200M','200M',18800,'2017-10-23 10:05:29','2017-10-23 09:58:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b628d0f5b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','长三角','DEFAULT','默认没有','500M','500M',28800,'2017-10-23 10:05:31','2017-10-23 09:58:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b63d64c4b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','长三角','DEFAULT','默认没有','1G','1G',48800,'2017-10-23 10:05:34','2017-10-23 09:58:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b64dcc83b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','京津冀','DEFAULT','默认没有','2G','2G',88800,'2017-10-23 09:58:25','2017-10-23 09:58:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b65cce6cb7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','京津冀','DEFAULT','默认没有','5G','5G',128800,'2017-10-23 09:58:25','2017-10-23 09:58:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b66bc770b7d811e7be460242ac110003','TUNNEL','专线','AREA','区域','CSJ','京津冀','DEFAULT','默认没有','10G','10G',168800,'2017-10-23 09:58:25','2017-10-23 09:58:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('b6d8ca0db7d711e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','10G','10G',168800,'2017-10-23 09:51:17','2017-10-23 09:51:17');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('c1e0fd83b7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','500M','500M',38800,'2017-10-23 10:13:04','2017-10-23 10:13:04');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('c4819520b7d111e7aae30242ac110002','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','2CPU2G','2核CPU/2G内存',100,'2017-10-23 09:08:43','2017-10-23 09:08:43');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('c6ac5381b7db11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','2CPU4G','2核CPU/4G内存',200,'2017-10-23 10:20:21','2017-10-23 10:20:21');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('cae5e5fdb7dc11e7be460242ac110003','VHOST','互联云','BANDWIDTH','带宽','杭州福地','杭州福地','联通','联通','5M','5M',100,'2017-10-23 10:27:38','2017-10-23 10:27:38');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d059a2bcb7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','1G','1G',48800,'2017-10-23 10:13:28','2017-10-23 10:13:28');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d46ca64fb7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','2M','2M',2800,'2017-10-23 10:06:25','2017-10-23 10:06:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d47cd46db7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','5M','5M',3800,'2017-10-23 10:06:25','2017-10-23 10:06:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d48cfa45b7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','10M','10M',4800,'2017-10-23 10:06:25','2017-10-23 10:06:25');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d49d3f55b7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','20M','20M',6800,'2017-10-23 10:06:26','2017-10-23 10:06:26');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d4b33c9cb7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','50M','50M',8800,'2017-10-23 10:06:26','2017-10-23 10:06:26');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d4c6fb7ab7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','100M','100M',10800,'2017-10-23 10:06:26','2017-10-23 10:06:26');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d4db025cb7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','200M','200M',18800,'2017-10-23 10:06:26','2017-10-23 10:06:26');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d4ebcf30b7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','500M','500M',28800,'2017-10-23 10:06:26','2017-10-23 10:06:26');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d4fbd940b7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','1G','1G',48800,'2017-10-23 10:06:26','2017-10-23 10:06:26');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d50bb0f5b7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','2G','2G',88800,'2017-10-23 10:06:26','2017-10-23 10:06:26');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d51cf834b7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','5G','5G',128800,'2017-10-23 10:06:26','2017-10-23 10:06:26');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d52eed52b7d911e7be460242ac110003','TUNNEL','专线','AREA','区域','ZSJ','珠三角','DEFAULT','默认没有','10G','10G',168800,'2017-10-23 10:06:27','2017-10-23 10:06:27');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d7628148b7ce11e7aae30242ac110002','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','2M','2M',2800,'2017-10-23 08:47:46','2017-10-23 08:47:46');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('d9a7e450b7db11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','2CPU8G','2核CPU/8G内存',300,'2017-10-23 10:22:02','2017-10-23 10:20:53');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('db4ecd42b7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','2G','2G',88800,'2017-10-23 10:13:46','2017-10-23 10:13:46');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('dbc0b19ab7d711e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','5M','5M',3800,'2017-10-23 09:52:19','2017-10-23 09:52:19');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('dfb3c246b7dc11e7be460242ac110003','VHOST','互联云','BANDWIDTH','带宽','上海漕宝路','上海漕宝路','联通','联通','5M','5M',100,'2017-10-23 10:28:13','2017-10-23 10:28:13');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('e527a67a0e8b471e8d728f65d8fc2af0','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/日本','上海/日本','100M','100M',3000,'2017-10-24 07:50:57','2017-10-24 07:50:57');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('e5d49c0fadaf462bbdaa2957b812f23e','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/日本','上海/日本','2M','2M',100,'2017-10-24 07:50:57','2017-10-24 07:50:57');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('e5ef88f4b7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','5G','5G',128800,'2017-10-23 10:14:04','2017-10-23 10:14:04');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('e740c197b7d111e7aae30242ac110002','VHOST','互联云','DISK','数据盘','DEFAULT','默认没有','DEFAULT','默认没有','100GB','100GB',100,'2017-10-23 09:09:41','2017-10-23 09:09:41');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('e8bde368b7d711e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','10M','10M',4800,'2017-10-23 09:52:40','2017-10-23 09:52:40');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('ec0dbe62b7db11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','2CPU12G','2核CPU/12G内存',400,'2017-10-23 10:21:24','2017-10-23 10:21:24');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('f3012b92b7da11e7be460242ac110003','TUNNEL','专线','ABROAD','跨国','DOMESTIC2ABROAD','国内到国外','上海/香港','上海/香港','10G','10G',168800,'2017-10-23 10:14:26','2017-10-23 10:14:26');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('f6119d1eb7db11e7be460242ac110003','VHOST','互联云','HOST','主机','DEFAULT','默认没有','DEFAULT','默认没有','2CPU16G','2核CPU/16G内存',500,'2017-10-23 10:21:41','2017-10-23 10:21:41');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('f6ed9ff5b7d711e7be460242ac110003','TUNNEL','专线','AREA','区域','JJJ','京津冀','DEFAULT','默认没有','20M','20M',6800,'2017-10-23 09:53:04','2017-10-23 09:53:04');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('f7aff852b7d611e7be460242ac110003','TUNNEL','专线','SHORT','同城','DEFAULT','默认没有','DEFAULT','默认没有','10M','10M',4800,'2017-10-23 09:47:46','2017-10-23 09:45:56');
insert  into `ProductPriceUnitVO`(`uuid`,`productTypeCode`,`productTypeName`,`categoryCode`,`categoryName`,`areaCode`,`areaName`,`lineCode`,`lineName`,`configCode`,`configName`,`unitPrice`,`lastOpDate`,`createDate`) values ('f9a58710b7ce11e7aae30242ac110002','TUNNEL','专线','LONG','长传','DEFAULT','默认没有','DEFAULT','默认没有','2M','2M',2800,'2017-10-23 08:48:43','2017-10-23 08:48:43');

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





