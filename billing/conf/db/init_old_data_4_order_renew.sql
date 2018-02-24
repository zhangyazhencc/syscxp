DELIMITER $$

USE `syscxp_billing`$$

DROP PROCEDURE IF EXISTS `generateOrder`$$

CREATE DEFINER=`root`@`%` PROCEDURE `generateOrder`()
  BEGIN
    DECLARE exitFlag INT DEFAULT 1;
    DECLARE pd VARCHAR(32);
    DECLARE dura INT;
    DECLARE pn VARCHAR(255);
    DECLARE bandw VARCHAR(128);
    DECLARE bw BIGINT;
    DECLARE category VARCHAR(50);
    DECLARE pcm VARCHAR(128);
    DECLARE acUuid VARCHAR(64);
    DECLARE exd TIMESTAMP;
    DECLARE a VARCHAR(255);
    DECLARE description VARCHAR(511);
    DECLARE priceInfo VARCHAR(511);
    DECLARE totpri  DECIMAL(12,4) DEFAULT 0;
    DECLARE connam VARCHAR(100);
    DECLARE unitp DECIMAL(12,4);
    DECLARE lcode VARCHAR(100);
    DECLARE cou INT;
    DECLARE couA INT;
    DECLARE couB INT;
    DECLARE couZ INT;
    DECLARE zoneA VARCHAR(100) DEFAULT 'A';
    DECLARE zoneB VARCHAR(100) DEFAULT 'B';
    DECLARE zoneZ VARCHAR(100) DEFAULT 'Z';
    DECLARE countri VARCHAR(50);
    DECLARE cityA VARCHAR(100);
    DECLARE cityB VARCHAR(100);
    DECLARE cityZ VARCHAR(100);
    DECLARE generateOrder CURSOR FOR SELECT `uuid`,`accountUuid`,`name`,bandwidthOffering,bandwidth,`type`,duration,productChargeModel,expireDate FROM syscxp_tunnel.`TunnelEO` WHERE accountUuid IS NOT NULL AND deleted IS NULL ORDER BY TYPE;
    DECLARE EXIT HANDLER FOR NOT FOUND SET exitFlag:=0;
    OPEN generateOrder;
    TRUNCATE TABLE syscxp_billing.`OrderVO`;
    TRUNCATE TABLE syscxp_billing.`RenewVO`;
    REPEAT
      FETCH generateOrder INTO pd,acUuid,pn,bandw,bw,category,dura,pcm,exd;
      SET description='{"datas":[';
      SET priceInfo ='[';
      SET totpri=0;
      SET lcode = 'A/Z';
      BEGIN
        DECLARE epUuid VARCHAR(32);
        DECLARE sportU VARCHAR(32);
        DECLARE sTag VARCHAR(20);
        DECLARE ef INT DEFAULT 1;
        DECLARE pt VARCHAR(255);
        DECLARE sUid VARCHAR(32);
        DECLARE eUid VARCHAR(32);
        DECLARE eNam VARCHAR(255);
        DECLARE concod VARCHAR(255);
        DECLARE disc INT;
        DECLARE sport CURSOR FOR  SELECT endpointUuid,switchPortUuid,sortTag FROM syscxp_tunnel.TunnelSwitchPortVO WHERE tunnelUuid = pd AND (sortTag='A' OR sortTag='Z');
        DECLARE EXIT HANDLER FOR NOT FOUND SET ef:=0;
        OPEN sport;
        REPEAT
          FETCH sport INTO epUuid,sportU,sTag;
          SELECT portType,switchUuid INTO pt,sUid FROM syscxp_tunnel.`SwitchPortVO` WHERE `uuid`= sportU;
          IF(pt='SHARE' ) THEN
            SET description = CONCAT_WS('',CONCAT_WS('-',CONCAT_WS('',description,'{"name":"端口'),sTag),'","value":"共享端口"},');
            SELECT endpointUuid INTO eUid FROM syscxp_tunnel.`SwitchEO` WHERE `uuid` = sUid;
            IF(bw < 524288000) THEN
              SET concod = 'LT500M';
            ELSEIF(bw>= 1073741824*2) THEN
              SET concod = 'GT2G';
            ELSE
              SET concod = 'GT500MLT2G';
            END IF;
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'SHARE' AND lineCode = eUid AND configCode= concod;
            # select ifnull(discount,100) into disc from syscxp_billing.`AccountDiscountVO` where accountUuid = acUuid and productCategoryUuid = 'SHARE'; 全部没有折扣
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));

          #ELSE
          #SET description = CONCAT_WS('',CONCAT_WS('-',CONCAT_WS('',description,'{"name":"端口'),sTag),'","value":"独享端口"},');
          #SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'EXCLUSIVE' AND configCode= pt;
          # select ifnull(discount,100) into disc from syscxp_billing.`AccountDiscountVO` where accountUuid = acUuid and productCategoryUuid = 'SHARE'; 全部没有折扣
          #SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));

          END IF;
          SET totpri = totpri+unitp;
          SELECT `name` INTO eNam FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` = epUuid;
          SET description = CONCAT_WS('',CONCAT_WS('-',CONCAT_WS('',description,'{"name":"连接点'),sTag),CONCAT_WS('','","value":",',CONCAT_WS('',eNam,'"},')));
        UNTIL ef=0 END REPEAT;
        CLOSE sport;
      END;

      IF(category ='CITY') THEN
        SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'CITY' AND configCode= bandw;
        SET totpri = totpri+unitp;
        SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
      ELSEIF (category='LONG') THEN
        SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'LONG' AND configCode= bandw;
        SET totpri = totpri+unitp;
        SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
      ELSEIF (category='REGION') THEN
        SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'REGION' AND areaCode =(SELECT zoneUuid FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` = (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag ='A'))) AND configCode= bandw;
        SET totpri = totpri+unitp;
        SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));

      ELSEIF (category='CHINA1ABROAD') THEN
        SELECT country INTO countri FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'A'));

        IF(countri='CHINA') THEN
          SELECT city INTO cityA FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'A'));
          SELECT city INTO cityB FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT innerEndpointUuid FROM syscxp_tunnel.`TunnelEO` WHERE `uuid` = pd ));
          SELECT COUNT(1) INTO couA FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'A'));
          SELECT COUNT(1) INTO couB FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT innerEndpointUuid FROM syscxp_tunnel.`TunnelEO` WHERE `uuid` = pd ));
          SET zoneA ='A';
          SET zoneB ='B';
          IF(couA>0) THEN
            SELECT zoneUuid INTO zoneA FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'A'));
          END IF;
          IF(couB>0) THEN
            SELECT zoneUuid INTO zoneB FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT innerEndpointUuid FROM syscxp_tunnel.`TunnelEO` WHERE `uuid` = pd ));
          END IF;
          IF(cityA=cityB) THEN
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'CITY' AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
          ELSEIF(zoneA=zoneB) THEN
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'REGION' AND areaCode = zoneA AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
          ELSE
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'LONG' AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));

          END IF;
          SELECT  REPLACE(GROUP_CONCAT(CASE WHEN country='CHINA' THEN city ELSE country END),',','/') INTO lcode FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN( SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid =pd  AND sortTag='Z'  UNION SELECT innerEndpointUuid FROM syscxp_tunnel.`TunnelEO` WHERE `uuid`= pd  ));
          SELECT COUNT(1) INTO cou FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'ABROAD' AND areaCode='CHINA2ABROAD'  AND lineCode = lcode AND configCode= bandw;
          IF(cou=0 ) THEN
            SET lcode = CONCAT_WS('/',SUBSTRING_INDEX(lcode,'/',-1),SUBSTRING_INDEX(lcode,'/',1));
          END IF;
          SELECT IFNULL(configName,''),IFNULL(unitPrice,0) INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'ABROAD' AND areaCode='CHINA2ABROAD'
                                                                                                                            AND lineCode = lcode
                                                                                                                            AND configCode= bandw;
          SET totpri = totpri+unitp;
          SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
        ELSE
          SELECT city INTO cityZ FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'Z'));
          SELECT city INTO cityB FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT innerEndpointUuid FROM syscxp_tunnel.`TunnelEO` WHERE `uuid` = pd ));
          SELECT COUNT(1) INTO couZ FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'Z'));
          SELECT COUNT(1) INTO couB FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT innerEndpointUuid FROM syscxp_tunnel.`TunnelEO` WHERE `uuid` = pd ));
          SET zoneZ ='Z';
          SET zoneB ='B';
          IF(couZ>0) THEN
            SELECT zoneUuid INTO zoneZ FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'Z'));
          END IF;
          IF(couB>0) THEN
            SELECT zoneUuid INTO zoneB FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT innerEndpointUuid FROM syscxp_tunnel.`TunnelEO` WHERE `uuid` = pd ));
          END IF;
          IF(cityZ=cityB) THEN
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'CITY' AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
          ELSEIF(zoneZ=zoneB) THEN
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'REGION' AND areaCode = zoneA AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
          ELSE
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'LONG' AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));

          END IF;
          SELECT  REPLACE(GROUP_CONCAT(CASE WHEN country='CHINA' THEN city ELSE country END),',','/') INTO lcode FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN( SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid =pd  AND  sortTag='A'  UNION SELECT innerEndpointUuid FROM syscxp_tunnel.`TunnelEO` WHERE `uuid`= pd ));
          SELECT COUNT(1) INTO cou FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'ABROAD' AND areaCode='CHINA2ABROAD'  AND lineCode = lcode AND configCode= bandw;
          IF(cou=0 ) THEN
            SET lcode = CONCAT_WS('/',SUBSTRING_INDEX(lcode,'/',-1),SUBSTRING_INDEX(lcode,'/',1));
          END IF;
          SELECT IFNULL(configName,''),IFNULL(unitPrice,0) INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'ABROAD' AND areaCode='CHINA2ABROAD'
                                                                                                                            AND lineCode = lcode
                                                                                                                            AND configCode= bandw;
          SET totpri = totpri+unitp;
          SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));

        END IF;

      ELSEIF(category ='CHINA2ABROAD')THEN
        SELECT country INTO countri FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'A'));
        IF(countri='CHINA') THEN
          SELECT city INTO cityA FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'A'));
          SELECT city INTO cityB FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'B'));
          SELECT COUNT(1) INTO couA FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'A'));
          SELECT COUNT(1) INTO couB FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'B'));
          SET zoneA ='A';
          SET zoneB ='B';
          IF(couA>0) THEN
            SELECT zoneUuid INTO zoneA FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'A'));
          END IF;
          IF(couB>0) THEN
            SELECT zoneUuid INTO zoneB FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'B'));
          END IF;
          IF(cityA=cityB) THEN
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'CITY' AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
          ELSEIF(zoneA=zoneB) THEN
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'REGION' AND areaCode = zoneA AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
          ELSE
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'LONG' AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));

          END IF;
          SELECT pd;
          SELECT  REPLACE(GROUP_CONCAT(CASE WHEN country='CHINA' THEN city ELSE country END),',','/') INTO lcode FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN( SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid =pd  AND (sortTag='B' OR sortTag='Z') ));
          SELECT COUNT(1) INTO cou FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'ABROAD' AND areaCode='CHINA2ABROAD'  AND lineCode = lcode AND configCode= bandw;
          IF(cou=0 ) THEN
            SET lcode = CONCAT_WS('/',SUBSTRING_INDEX(lcode,'/',-1),SUBSTRING_INDEX(lcode,'/',1));
          END IF;
          SELECT IFNULL(configName,''),IFNULL(unitPrice,0) INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'ABROAD' AND areaCode='CHINA2ABROAD'
                                                                                                                            AND lineCode = lcode
                                                                                                                            AND configCode= bandw;
          SET totpri = totpri+unitp;
          SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
        ELSE
          SELECT city INTO cityZ FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'Z'));
          SELECT city INTO cityB FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'B'));
          SELECT COUNT(1) INTO couZ FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'Z'));
          SELECT COUNT(1) INTO couB FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'B'));
          SET zoneZ ='Z';
          SET zoneB ='B';
          IF(couZ>0) THEN
            SELECT zoneUuid INTO zoneZ FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'Z'));
          END IF;
          IF(couB>0) THEN
            SELECT zoneUuid INTO zoneB FROM syscxp_tunnel.`ZoneNodeRefVO` WHERE nodeUuid =(SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd AND sortTag = 'B'));
          END IF;
          IF(cityZ=cityB) THEN
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'CITY' AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
          ELSEIF(zoneZ=zoneB) THEN
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'REGION' AND areaCode = zoneA AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));
          ELSE
            SELECT configName,unitPrice INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'LONG' AND configCode= bandw;
            SET totpri = totpri+unitp;
            SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));

          END IF;
          SELECT  REPLACE(GROUP_CONCAT(CASE WHEN country='CHINA' THEN city ELSE country END),',','/') INTO lcode FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN( SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid =pd  AND (sortTag='B' OR sortTag='A') ));
          SELECT COUNT(1) INTO cou FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'ABROAD' AND areaCode='CHINA2ABROAD'  AND lineCode = lcode AND configCode= bandw;
          IF(cou=0 ) THEN
            SET lcode = CONCAT_WS('/',SUBSTRING_INDEX(lcode,'/',-1),SUBSTRING_INDEX(lcode,'/',1));
          END IF;
          SELECT IFNULL(configName,''),IFNULL(unitPrice,0) INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'ABROAD' AND areaCode='CHINA2ABROAD'
                                                                                                                            AND lineCode = lcode
                                                                                                                            AND configCode= bandw;
          SET totpri = totpri+unitp;
          SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));

        END IF;
      ELSE

        SELECT  REPLACE(GROUP_CONCAT(country),',','/') INTO lcode FROM syscxp_tunnel.`NodeEO` WHERE `uuid` IN( SELECT nodeUuid FROM syscxp_tunnel.`EndpointEO` WHERE `uuid` IN (SELECT endpointUuid FROM syscxp_tunnel.`TunnelSwitchPortVO` WHERE tunnelUuid = pd  ));

        SELECT COUNT(1) INTO cou FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'ABROAD' AND areaCode='CHINA2ABROAD'  AND lineCode = lcode AND configCode= bandw;

        IF(cou=0 ) THEN
          SET lcode = CONCAT_WS('/',SUBSTRING_INDEX(lcode,'/',-1),SUBSTRING_INDEX(lcode,'/',1));
        END IF;
        SELECT IFNULL(configName,''),IFNULL(unitPrice,0) INTO connam,unitp FROM syscxp_billing.`ProductPriceUnitVO` WHERE productCategoryUuid = 'ABROAD' AND areaCode='CHINA2ABROAD'
                                                                                                                          AND lineCode = lcode
                                                                                                                          AND configCode= bandw;
        SET totpri = totpri+unitp;
        SET priceInfo=CONCAT_WS('',CONCAT_WS('',CONCAT_WS('',priceInfo,'{"configName":"'),CONCAT_WS('',connam,'","originalPrice":')),CONCAT_WS('',CONCAT_WS('',unitp,',"realPayPrice":'),CONCAT_WS('',unitp,',"discount":100},')));

      END IF;
      IF(pcm='BY_YEAR') THEN
        SET dura = dura*12;
      END IF;
      SET totpri = totpri*dura;
      SET description = CONCAT_WS('',CONCAT_WS('',description,'{"name":"带宽","value":"'),CONCAT_WS('',bandw,'"}]}'));
      SET priceInfo = CONCAT_WS('',priceInfo,']');

      INSERT INTO syscxp_billing.`OrderVO` VALUES(
        REPLACE(UUID(),'-',''),
        'BUY',
        CURRENT_TIMESTAMP(),
        'PAID',
        totpri,
        totpri,
        totpri,
        0,
        acUuid,
        CURRENT_TIMESTAMP(),
        exd,
        CURRENT_TIMESTAMP(),
        CURRENT_TIMESTAMP(),
        pd,
        pn,
        'TUNNEL',
        description,
        pcm,
        dura,
        1,
        '{"com.syscxp.header.tunnel.billingCallBack.CreateTunnelCallBack":{}}',
        priceInfo,
        0
      );

      INSERT INTO syscxp_billing.`RenewVO` VALUES(
        REPLACE(UUID(),'-',''),
        acUuid,
        1,
        pd,
        pn,
        'TUNNEL',
        description,
        pcm,
        CURRENT_TIMESTAMP(),
        CURRENT_TIMESTAMP(),
        totpri,
        exd,
        totpri
      );

    UNTIL exitFlag=0 END REPEAT;
    CLOSE generateOrder;
    COMMIT;
    SELECT 'SUCCESS';
  END$$

DELIMITER ;