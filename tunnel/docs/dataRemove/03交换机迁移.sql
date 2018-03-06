##创建交换机型号
##导入SwitchModelVO数据
insert	into `naas_cxp`.SwitchModelVO
select	a.switch_model_id as uuid,
	'华为' as brand,
	a.model,
	a.submodel as subModel,
	'2017-12-07 13:37:03' as lastOpDate,
	'2017-12-07 13:37:03' as createDate
from	`naas`.switch_model a where a.model != 'linux_ether' and a.model != 'centec_v580';

##创建物理交换机
##导入PhysicalSwitchEO数据--去掉linux和centec
insert into `naas_cxp`.PhysicalSwitchEO
select * from (
	select  
		a.switch_id as uuid,
		b.nodeUuid as nodeUuid,
		case when a.submodel = 'huawei_S5700-hi' then '3' 
		     when a.submodel = 'huawei_S6700' then '4' 
		     when a.submodel = 'huawei_S9300' then '5' 
		     else '6' end as switchModelUuid,
		a.code as code,
		a.name as name,
		a.owner as owner,
		'MPLS' as type,
		'BOTH' as accessType,
		a.rack as rack,
		a.description as description,
		a.m_ip as mIP,
		a.m_ip as localIP,
		'TELNET' as protocol,
		23 as port,
		a.username as username,
		a.password as password,
		a.deleted_at as deleted,
		a.updated_at as lastOpDate,
		a.created_at as createDate
	from	(select * from `naas`.switch 
		where deleted = 0 
		and model != 'linux_ether' 
		and model != 'centec_v580' 
		and switch_id in (select min(c.switch_id) as switch_id from `naas`.switch c where c.deleted = 0 and c.model != 'linux_ether' and c.model != 'centec_v580' group by c.m_ip)
		) a 
	LEFT join `naas_cxp`.EndpointEO b on a.endpoint_id = b.uuid
	) aa where aa.nodeUuid is not null;



##数据验证：code唯一性
select count(*) from `naas_cxp`.PhysicalSwitchEO;/*184*/ 
select count(*) from (select distinct code from `naas_cxp`.PhysicalSwitchEO) a;/*184*/ 

/*********************************************************************************************/

##创建逻辑交换机  
##导入SwitchEO数据 
insert into `naas_cxp`.SwitchEO
select * from (
	select  
		a.switch_id as uuid,
		b.uuid as physicalSwitchUuid,
		c.uuid as endpointUuid,
		a.code,
		a.name,
		'ACCESS' as type,
		a.description,
		case when a.enabled = 'true' then 'Enabled' else 'Disabled' end as state,
		'Connected' as status,
		a.deleted_at as deleted,
		a.updated_at as lastOpDate,
		a.created_at as createDate
	from	(select * from `naas`.switch where deleted = 0 and model != 'linux_ether' and model != 'centec_v580') a 
	left join `naas_cxp`.PhysicalSwitchEO b on a.m_ip = b.mIP
	left join `naas_cxp`.EndpointEO c on a.endpoint_id = c.uuid
	) aa where aa.endpointUuid is not null;

##数据验证：code唯一性
select count(*) from `naas_cxp`.SwitchEO;/*212*/ 
select count(*) from (select distinct code from `naas_cxp`.SwitchEO) a;/*212*/ 
