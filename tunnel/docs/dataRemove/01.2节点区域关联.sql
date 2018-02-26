insert into `naas_cxp`.ZoneNodeRefVO
select	a.uuid,
	a.uuid as nodeUuid,
	case when a.province in ('北京','河北省','天津') then 'JJJ' 
	     when a.province in ('江苏省','上海','浙江省') then 'CSJ' 
	     when a.province in ('广东省') then 'ZSJ' end as zoneUuid,
	'2017-12-07 13:37:03' as lastOpDate,
	'2017-12-07 13:37:03' as createDate
from	`naas_cxp`.NodeEO a where a.province in ('北京','河北省','天津','江苏省','上海','浙江省','广东省');