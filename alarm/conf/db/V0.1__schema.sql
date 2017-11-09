/*
SQLyog
MySQL - 5.5.56-MariaDB : Database - syscxp_alarm
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`syscxp_alarm` /*!40100 DEFAULT CHARACTER SET utf8 */;

/*Table structure for table `AlarmLogVO` */
DROP TABLE IF EXISTS `AlarmLogVO`;

CREATE TABLE `AlarmLogVO` (
  `uuid` varchar(32) NOT NULL,
  `productUuid` varchar(32) DEFAULT NULL,
  `productType` varchar(255) DEFAULT NULL,
  `duration` int(10) DEFAULT NULL COMMENT '持续时间',
  `alarmContent` varchar(256) DEFAULT NULL COMMENT '报警内容',
  `status` varchar(127) DEFAULT NULL COMMENT '报警状态',
  `accountUuid` varchar(32) DEFAULT NULL COMMENT '账户UUID',
  `smsContent` varchar(256) DEFAULT NULL COMMENT '短信内容',
  `mailContent` varchar(256) DEFAULT NULL COMMENT '邮件内容',
  `policyName` varchar(255) DEFAULT NULL COMMENT '策略名字',
  `eventId` varchar(128) DEFAULT NULL COMMENT '事件ID',
  `alarmTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `resumeTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `createDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `regulationUuid` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `AlarmLogVO` */


/*Table structure for table `AlarmTemplateVO` */

DROP TABLE IF EXISTS `AlarmTemplateVO`;

CREATE TABLE `AlarmTemplateVO` (
  `uuid` varchar(32) NOT NULL,
  `productType` varchar(255) DEFAULT NULL COMMENT '产品类型',
  `regulationType` varchar(128) DEFAULT NULL COMMENT '规则ID',
  `template` varchar(255) DEFAULT NULL COMMENT '模板',
  `status` varchar(127) DEFAULT NULL COMMENT '报警状态',
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `AlarmTemplateVO` */


/*Table structure for table `ComparisonRuleVO` */
DROP TABLE IF EXISTS `ComparisonRuleVO`;

CREATE TABLE `ComparisonRuleVO` (
  `uuid` varchar(32) DEFAULT NULL,
  `productType` varchar(255) DEFAULT NULL,
  `comparisonName` varchar(127) DEFAULT NULL,
  `comparisonValue` varchar(127) DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `ComparisonRuleVO` */

insert  into `ComparisonRuleVO`(`uuid`,`productType`,`comparisonName`,`comparisonValue`,`createDate`,`lastOpDate`) values ('7e577d2ab4a111e79e8e525400c2a7e2','TUNNEL','大于等于','>=','2017-10-19 15:51:58','2017-10-19 15:45:36');
insert  into `ComparisonRuleVO`(`uuid`,`productType`,`comparisonName`,`comparisonValue`,`createDate`,`lastOpDate`) values ('86f5b551b4a111e79e8e525400c2a7e2','TUNNEL','小于等于','<=','2017-10-19 15:52:00','2017-10-19 15:45:36');

/*Table structure for table `ContactNotifyWayRefVO` */
DROP TABLE IF EXISTS `ContactNotifyWayRefVO`;

CREATE TABLE `ContactNotifyWayRefVO` (
  `uuid` varchar(32) NOT NULL,
  `contactUuid` varchar(32) DEFAULT NULL,
  `notifyWayUuid` varchar(32) DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ContactVO` */
DROP TABLE IF EXISTS `ContactVO`;

CREATE TABLE `ContactVO` (
  `uuid` varchar(32) NOT NULL,
  `name` varchar(127) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `email` varchar(127) DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `accountUuid` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `unique_email` (`email`),
  UNIQUE KEY `unique_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `GlobalConfigVO` */
DROP TABLE IF EXISTS `GlobalConfigVO`;

CREATE TABLE `GlobalConfigVO` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `category` varchar(64) NOT NULL,
  `defaultValue` text,
  `value` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;


/*Table structure for table `JobQueueEntryVO` */
DROP TABLE IF EXISTS `JobQueueEntryVO`;

CREATE TABLE `JobQueueEntryVO` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `jobQueueId` bigint(20) unsigned NOT NULL,
  `state` varchar(128) NOT NULL,
  `context` blob,
  `owner` varchar(255) DEFAULT NULL,
  `issuerManagementNodeId` varchar(32) DEFAULT NULL,
  `restartable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `inDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `doneDate` timestamp NULL DEFAULT NULL,
  `errText` text,
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
  `takenDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
  `joinDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `heartBeat` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `MonitorTargetVO` */
DROP TABLE IF EXISTS `MonitorTargetVO`;

CREATE TABLE `MonitorTargetVO` (
  `uuid` varchar(32) DEFAULT NULL,
  `productType` varchar(255) DEFAULT NULL,
  `targetName` varchar(128) DEFAULT NULL,
  `targetValue` varchar(127) DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `MonitorTargetVO` */

insert  into `MonitorTargetVO`(`uuid`,`productType`,`targetName`,`targetValue`,`createDate`,`lastOpDate`) values ('1601ddcbb4a211e79e8e525400c2a7e2','TUNNEL','丢包','loss-package','2017-10-19 15:51:45','2017-10-19 15:49:50');
insert  into `MonitorTargetVO`(`uuid`,`productType`,`targetName`,`targetValue`,`createDate`,`lastOpDate`) values ('406589bdb4a211e79e8e525400c2a7e2','TUNNEL','宽带使用率','net-usage-percent','2017-10-19 15:51:46','2017-10-19 15:49:50');
insert  into `MonitorTargetVO`(`uuid`,`productType`,`targetName`,`targetValue`,`createDate`,`lastOpDate`) values ('5312c4d0b4a211e79e8e525400c2a7e2','TUNNEL','延迟','delay','2017-10-19 15:51:49','2017-10-19 15:49:50');

/*Table structure for table `NotifyWayVO` */
DROP TABLE IF EXISTS `NotifyWayVO`;

CREATE TABLE `NotifyWayVO` (
  `uuid` varchar(32) NOT NULL,
  `code` varchar(127) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `unique_code` (`code`),
  UNIQUE KEY `unique_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `NotifyWayVO` */

insert  into `NotifyWayVO`(`uuid`,`code`,`name`,`createDate`,`lastOpDate`) values ('8d72774bb30311e78816525400c2a7e2','email','邮件','2017-10-17 15:53:17','2017-10-17 14:22:29');
insert  into `NotifyWayVO`(`uuid`,`code`,`name`,`createDate`,`lastOpDate`) values ('9a0c922db30311e78816525400c2a7e2','mobile','手机','2017-10-17 15:53:18','2017-10-17 14:22:29');

/*Table structure for table `PolicyVO` */
DROP TABLE IF EXISTS `PolicyVO`;

CREATE TABLE `PolicyVO` (
  `uuid` varchar(32) NOT NULL,
  `productType` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `accountUuid` varchar(32) DEFAULT NULL,
  `bindResources` int(4) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `RegulationVO` */
DROP TABLE IF EXISTS `RegulationVO`;

CREATE TABLE `RegulationVO` (
  `uuid` varchar(32) NOT NULL,
  `policyUuid` varchar(32) DEFAULT NULL,
  `comparisonRuleUuid` varchar(32) DEFAULT NULL,
  `monitorTargetUuid` varchar(32) DEFAULT NULL,
  `alarmThreshold` int(10) DEFAULT NULL,
  `detectPeriod` int(10) DEFAULT NULL,
  `triggerPeriod` int(10) DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `ResourcePolicyRefVO` */
DROP TABLE IF EXISTS `ResourcePolicyRefVO`;

CREATE TABLE `ResourcePolicyRefVO` (
  `uuid` varchar(32) NOT NULL,
  `resourceUuid` varchar(32) DEFAULT NULL,
  `policyUuid` varchar(32) DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `unique` (`resourceUuid`,`policyUuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `SmsVO` */
DROP TABLE IF EXISTS `SmsVO`;

CREATE TABLE `SmsVO` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
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
  `lastOpDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `createDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
