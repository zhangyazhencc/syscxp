ALTER TABLE AccountVO add column `expiredClean` boolean NOT NULL COMMENT '是否过期清理';

INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission, lastOpDate, createDate)
VALUES ('L3NetworkOnlyFullAccess','管理云网络权限','tunnel','Normal','2','{"actions":["tunnel:.*:read","tunnel:l3Network:.*","tunnel:edgeLine:.*","tunnel:monitor:.*"],"effect":"Allow"}', current_timestamp(), current_timestamp());

INSERT INTO PolicyVO (uuid, name, type, accountType, sortId, permission, lastOpDate, createDate)
VALUES ('TunnelOnlyFullAccess','管理云专线权限','tunnel','Normal','2','{"actions":["tunnel:.*:read","tunnel:tunnel:.*","tunnel:edgeLine:.*","tunnel:monitor:.*"],"effect":"Allow"}', current_timestamp(), current_timestamp());