ALTER TABLE `syscxp_billing`.`OrderVO` CHANGE `productEffectTimeEnd` `productEffectTimeEnd` timestamp NULL DEFAULT NULL COMMENT '产品使用结束时间';

ALTER TABLE `syscxp_billing`.`OrderVO` CHANGE `lastOpDate` `lastOpDate` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;


ALTER TABLE `syscxp_billing`.`SLACompensateVO` CHANGE `lastOpDate` `lastOpDate` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;



