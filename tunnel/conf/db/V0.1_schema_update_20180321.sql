ALTER TABLE `syscxp_tunnel`.`SwitchVlanVO` ADD COLUMN `type` varchar(32) NOT NULL COMMENT 'vlan类型：L2，L3';

ALTER TABLE `syscxp_tunnel`.`EdgeLineEO` ADD COLUMN `implementType` varchar(128) DEFAULT NULL COMMENT '最后一公里施工类型';
DROP VIEW IF EXISTS `syscxp_tunnel`.`EdgeLineVO`;
CREATE VIEW `syscxp_tunnel`.`EdgeLineVO` AS SELECT uuid, `number`, accountUuid, interfaceUuid, endpointUuid, type, destinationInfo, description, state, implementType, prices, expireDate, lastOpDate, createDate
                                            FROM `EdgeLineEO` WHERE deleted IS NULL;

ALTER TABLE `syscxp_tunnel`.`EdgeLineEO` ADD COLUMN `costPrices` int(11) DEFAULT NULL COMMENT '成本价：元/月';
DROP VIEW IF EXISTS `syscxp_tunnel`.`EdgeLineVO`;
CREATE VIEW `syscxp_tunnel`.`EdgeLineVO` AS SELECT uuid, `number`, accountUuid, interfaceUuid, endpointUuid, type, destinationInfo, description, state, implementType, costPrices, prices, expireDate, lastOpDate, createDate
                                            FROM `EdgeLineEO` WHERE deleted IS NULL;

