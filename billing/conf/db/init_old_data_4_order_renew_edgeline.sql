



DELIMITER $$

USE `syscxp_billing`$$

DROP PROCEDURE IF EXISTS `generateOrder4edgeLine`$$

CREATE DEFINER=`root`@`%` PROCEDURE `generateOrder4edgeLine`()
BEGIN
     DECLARE exitFlag INT DEFAULT 1;
     DECLARE pd VARCHAR(32);
     DECLARE exd TIMESTAMP;
     DECLARE acUuid VARCHAR(64);
     DECLARE pn VARCHAR(255);
     DECLARE des VARCHAR(511);
     DECLARE interid VARCHAR(32);
     DECLARE interna VARCHAR(255);
     DECLARE endpid VARCHAR(32);
      DECLARE ena VARCHAR(32);

     DECLARE generateOrder CURSOR FOR SELECT
     `uuid`,
     `expireDate`,
       '最后一公里-' AS NAME,
      `accountUuid`,
      `interfaceUuid`
      FROM  syscxp_tunnel.`EdgeLineEO` WHERE createDate< '2018-03-03 00:00:00';

     DECLARE EXIT HANDLER FOR NOT FOUND SET exitFlag:=0;
     OPEN generateOrder;
	 REPEAT
     FETCH generateOrder INTO pd,exd,pn,acUuid,interid;

     SET des = '{"datas":[{"name":"连接点","value":"';
     SELECT NAME,endpointUuid INTO interna, endpid FROM syscxp_tunnel.InterfaceEO WHERE `uuid`=interid;


     SELECT NAME INTO ena FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` = endpid;
     SET des = CONCAT_WS('',des,ena);
     SET des = CONCAT_WS('',des,'"},{"name":"物理接口","value":"');
     SET des = CONCAT_WS('',des,interna);
     SET des = CONCAT_WS('',des,'"}]}');

      INSERT INTO syscxp_billing.`OrderVO` VALUES(
         REPLACE(UUID(),'-',''),
         'BUY',
         '2018-03-01 00:00:00',
         'PAID',
         0,
         0,
         0,
         0,
         acUuid,
         '2018-03-01 00:00:00',
         exd,
         '2018-03-01 00:00:00',
         '2018-03-01 00:00:00',
         pd,
         CONCAT_WS('',pn,interna),
         'EDGELINE',
         des,
         'BY_MONTH',
         3,
         1,
         '{"com.syscxp.header.tunnel.billingCallBack.CreateEdgeLineCallBack":{}}',
         NULL,
         0
         );

     INSERT INTO syscxp_billing.`RenewVO` VALUES(
           REPLACE(UUID(),'-',''),
           acUuid,
           1,
           pd,
           CONCAT_WS('',pn,interna),
           'EDGELINE',
            des,
            'BY_MONTH',
            '2018-03-01 00:00:00',
            '2018-03-01 00:00:00',
            0,
            exd,
            0
     );

     UNTIL exitFlag=0 END REPEAT;
     CLOSE generateOrder;

    END$$

DELIMITER ;