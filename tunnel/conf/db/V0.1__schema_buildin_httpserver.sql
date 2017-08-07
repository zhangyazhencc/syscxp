use syscxp_account_rest;

CREATE TABLE  `syscxp_account_rest`.`RestAPIVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `apiMessageName` varchar(255) DEFAULT NULL,
    `state` varchar(255) NOT NULL,
    `result` text DEFAULT NULL,
    `lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP,
    `createDate` timestamp,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
