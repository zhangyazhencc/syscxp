DELIMITER $$

USE `syscxp_billing`$$

DROP PROCEDURE IF EXISTS `generateOrder4Port`$$

CREATE DEFINER=`root`@`%` PROCEDURE `generateOrder4Port`()
BEGIN
     DECLARE exitFlag INT DEFAULT 1;
     DECLARE pd VARCHAR(32);
     DECLARE exd TIMESTAMP;
     DECLARE pcm VARCHAR(128);
     DECLARE acUuid VARCHAR(64);
     DECLARE pn VARCHAR(255);
 
     DECLARE generateOrder CURSOR FOR SELECT 
     `uuid`, 
     `expireDate`,
      `productChargeModel`, 
      `name`,
      `accountUuid`
      FROM  syscxp_tunnel.`InterfaceEO` WHERE expireDate IS NOT NULL AND createDate< '2018-03-03 00:00:00'; 
     
     DECLARE EXIT HANDLER FOR NOT FOUND SET exitFlag:=0;
     OPEN generateOrder;
	 REPEAT
     FETCH generateOrder INTO pd,exd,pcm,pn,acUuid;
     
      INSERT INTO syscxp_billing.`OrderVO` VALUES(
         REPLACE(UUID(),'-',''),
         'BUY',
         '2018-03-01 00:00:00',
         'PAID',
         2400,
         2400,
         2400,
         0,
         acUuid,
         '2018-03-01 00:00:00',
         exd,
         '2018-03-01 00:00:00',
         '2018-03-01 00:00:00',
         pd,
         pn,
         'PORT',
         '{"datas":[{"name":"端口类型","value":"RJ45_10G"}]}',
         pcm,
         3,
         1,
         '{"com.syscxp.header.tunnel.billingCallBack.CreateInterfaceCallBack":{}}',
         '[{"configName":"光口万兆","originalPrice":800.00,"realPayPrice":800.00,"discount":100}]',
         0      
         );
         
     INSERT INTO syscxp_billing.`RenewVO` VALUES(
           REPLACE(UUID(),'-',''),
           acUuid,
           1,
           pd,
           pn ,
           'PORT',
            '{"datas":[{"name":"端口类型","value":"RJ45_10G"}]}',
            pcm,
            '2018-03-01 00:00:00',
            '2018-03-01 00:00:00',
            800,
            exd,
            800
     );
     
     UNTIL exitFlag=0 END REPEAT;
     CLOSE generateOrder;
    
    END$$

DELIMITER ;