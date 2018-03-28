ALTER TABLE `syscxp_alarm`.`ContactVO` CHANGE `lastOpDate` `lastOpDate` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE `syscxp_alarm`.`ContactVO` CHANGE `createDate` `createDate` TIMESTAMP default CURRENT_TIMESTAMP;

ALTER TABLE `syscxp_alarm`.`ResourcePolicyRefVO` CHANGE `lastOpDate` `lastOpDate` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE `syscxp_alarm`.`ResourcePolicyRefVO` CHANGE `createDate` `createDate` TIMESTAMP default CURRENT_TIMESTAMP;

