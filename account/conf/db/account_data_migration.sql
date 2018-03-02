DROP TABLE IF EXISTS `AccountVO`;
CREATE TABLE `AccountVO` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID',
  `name` varchar(128) NOT NULL COMMENT '账户名称',
  `password` varchar(128) NOT NULL COMMENT '账户密码',
  `email` varchar(36) NOT NULL COMMENT '邮箱',
  `emailStatus` varchar(36) NOT NULL COMMENT '邮箱是否认证',
  `phone` varchar(32) NOT NULL COMMENT '手机号',
  `phoneStatus` varchar(36) NOT NULL COMMENT '手机是否认证',
  `trueName` varchar(128) DEFAULT NULL COMMENT '姓名',
  `company` varchar(128) DEFAULT NULL COMMENT '公司',
  `industry` varchar(128) DEFAULT NULL COMMENT '行业',
  `type` varchar(128) NOT NULL COMMENT 'account type',
  `status` varchar(128) NOT NULL COMMENT '状态',
  `description` varchar(255) DEFAULT NULL,
  `lastOpDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp ,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `AccountExtraInfoVO`;
CREATE TABLE `AccountExtraInfoVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`grade` varchar(36) DEFAULT NULL COMMENT '客户等级',
	`userUuid` varchar(36) DEFAULT NULL COMMENT '业务员uuid',
  `createWay` varchar(36) NOT NULL COMMENT '注册渠道',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
  `createDate` timestamp ,
  PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `AccountApiSecurityVO`;
CREATE TABLE  `AccountApiSecurityVO` (
	`uuid` varchar(32) NOT NULL UNIQUE COMMENT 'UUID',
	`accountUuid` varchar(32) NOT NULL UNIQUE COMMENT '所属账户UUID',
	`secretId` varchar(128) DEFAULT NULL COMMENT 'API密钥-公钥',
	`secretKey` varchar(128) DEFAULT NULL COMMENT 'API密钥-私钥',
	`allowIp` text DEFAULT NULL COMMENT '允许访问IP的集合',
	`lastOpDate` timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次操作时间',
	`createDate` timestamp ,
	PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP FUNCTION IF EXISTS `fn_parseJson`;
CREATE FUNCTION fn_parseJson(p_jsonstr VARCHAR(255) character set utf8, p_key VARCHAR(255)) RETURNS VARCHAR(255)
BEGIN
	DECLARE rtnVal VARCHAR(255) DEFAULT '';
  DECLARE v_key VARCHAR(255);
  SET v_key = CONCAT('"', p_key, '":');
  SET @v_flag = p_jsonstr REGEXP v_key;

	IF(@v_flag = 0)
		THEN
			SET rtnVal = '';
		ELSE
			SELECT val INTO rtnVal FROM (
			SELECT @start_pos := locate(v_key, p_jsonstr),
			@end_pos := @start_pos + length(v_key),
			@tail_pos := if(locate(",", p_jsonstr, @end_pos) = 0, locate("}", p_jsonstr, @end_pos), locate(",", p_jsonstr, @end_pos)),
			substring(p_jsonstr, @end_pos + 1, @tail_pos - @end_pos - 2) as val) as t;
  END IF;
  RETURN rtnVal;
END ;

drop procedure if exists account_data_migration;
CREATE PROCEDURE account_data_migration()
	BEGIN
		DECLARE _uuid VARCHAR(32);
		DECLARE _name VARCHAR(32);
		DECLARE _extra VARCHAR(128);
		DECLARE _password VARCHAR(128);
		DECLARE _enabled VARCHAR(16);
		DECLARE _created_at VARCHAR(32);
		DECLARE _telephone VARCHAR(32);
		DECLARE _email VARCHAR(32);
		DECLARE _company VARCHAR(32);
		DECLARE _status VARCHAR(32);
		DECLARE migration_status int default 0;
		DECLARE cursor_name CURSOR FOR select id,name,extra,password,enabled,created_at,telephone,email,company from keystone.`user`;
		DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET migration_status = 1;
		OPEN cursor_name;
			fetch  cursor_name into _uuid,_name,_extra,_password,_enabled,_created_at,_telephone,_email,_company;
			while migration_status <> 1 do
				if(_enabled='1')
					THEN set _enabled='Available';
					ELSE set _enabled='Disabled';
				end if;
				if ((SELECT creator from billing.`account` where user_id = _uuid) IS NULL)
					THEN set _status = "Validated";
					ELSE set _status = 'Unvalidated';
				end if;
				if(_email is NULL || _email = '')
					THEN SET _email = CONCAT('null_data',_uuid);
				end if;
				if(_telephone is NULL || _telephone = '')
					THEN SET _telephone = CONCAT('null_data',_uuid);
				end if;
				set _extra = fn_parseJson(replace(_extra,' ',''), 'industry');
				INSERT INTO `AccountVO`(
								`uuid`, `name`, `password`, `email`, `emailStatus`, `phone`, `phoneStatus`,
								`trueName`, `company`, `industry`, `type`, `status`, `description`, `lastOpDate`, `createDate`)
							VALUES (_uuid, _name, _password, _email, _status, _telephone, _status,
								'trueName', _company, _extra, 'Normal', _enabled, "旧系统老用户", SYSDATE(), _created_at);
			  INSERT INTO `AccountApiSecurityVO` (
			          `uuid`, `accountUuid`, `secretId`, `secretKey`, `allowIp`, `lastOpDate`, `createDate`)
			          VALUES (select replace(uuid(), '-', ''), _uuid, SELECT LEFT((select replace(uuid(), '-', '')),30),
			            SELECT LEFT((select replace(uuid(), '-', '')),16), NULL, SYSDATE(), _created_at);
        INSERT INTO `AccountExtraInfoVO` (
                `uuid`, `grade`, `userUuid`, `createWay`, `lastOpDate`, `createDate`)
                VALUES (_uuid, "Normal", NULL, "SystemAdmin", SYSDATE(), _created_at);
				fetch  cursor_name into _uuid,_name,_extra,_password,_enabled,_created_at,_telephone,_email,_company;
			end while;
		CLOSE cursor_name ;
	END;

CALL account_data_migration()