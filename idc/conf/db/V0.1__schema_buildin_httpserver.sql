CREATE DATABASE /*!32312 IF NOT EXISTS*/`syscxp_idc_rest` /*!40100 DEFAULT CHARACTER SET utf8 */;

use syscxp_idc_rest;

CREATE TABLE  `syscxp_idc_rest`.`RestAPIVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `apiMessageName` varchar(255) DEFAULT NULL,
    `state` varchar(255) NOT NULL,
    `result` text DEFAULT NULL,
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
