ALTER TABLE `syscxp_tunnel`.`SwitchVlanVO` ADD COLUMN `type` varchar(32) NOT NULL COMMENT 'vlan类型：L2，L3';

UPDATE `syscxp_tunnel`.`SwitchVlanVO` set type = 'L2';

##

ALTER TABLE `syscxp_tunnel`.`EdgeLineEO` ADD COLUMN `implementType` varchar(128) DEFAULT NULL COMMENT '最后一公里施工类型';
DROP VIEW IF EXISTS `syscxp_tunnel`.`EdgeLineVO`;
CREATE VIEW `syscxp_tunnel`.`EdgeLineVO` AS SELECT uuid, `number`, accountUuid, interfaceUuid, endpointUuid, type, destinationInfo, description, state, implementType, prices, expireDate, lastOpDate, createDate
                                            FROM `EdgeLineEO` WHERE deleted IS NULL;

ALTER TABLE `syscxp_tunnel`.`EdgeLineEO` ADD COLUMN `costPrices` int(11) DEFAULT NULL COMMENT '成本价：元/月';
DROP VIEW IF EXISTS `syscxp_tunnel`.`EdgeLineVO`;
CREATE VIEW `syscxp_tunnel`.`EdgeLineVO` AS SELECT uuid, `number`, accountUuid, interfaceUuid, endpointUuid, type, destinationInfo, description, state, implementType, costPrices, prices, expireDate, lastOpDate, createDate
                                            FROM `EdgeLineEO` WHERE deleted IS NULL;

ALTER TABLE `syscxp_tunnel`.`EdgeLineEO` ADD COLUMN `fixedCost` int(11) DEFAULT NULL COMMENT '一次性费用';
DROP VIEW IF EXISTS `syscxp_tunnel`.`EdgeLineVO`;
CREATE VIEW `syscxp_tunnel`.`EdgeLineVO` AS SELECT uuid, `number`, accountUuid, interfaceUuid, endpointUuid, type, destinationInfo, description, state, implementType, costPrices, prices, fixedCost, expireDate, lastOpDate, createDate
                                            FROM `EdgeLineEO` WHERE deleted IS NULL;

UPDATE `syscxp_tunnel`.`EdgeLineEO` set fixedCost = 0 where state = 'Opened' or (state = 'Applying' and prices is not null);

