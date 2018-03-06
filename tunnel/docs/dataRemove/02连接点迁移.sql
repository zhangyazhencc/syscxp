##导入EndpointEO数据  
insert into `naas_cxp`.EndpointEO
select * from (
	select  a.endpoint_id as uuid,
		b.uuid as nodeUuid,
		a.name,
		a.code,
		'UNDO' as endpointType,
		null as cloudType,
		case when a.enabled = 'true' then 'Enabled' else 'Disabled' end as state,
		case when a.open_to_customers = 'true' then 'Open' else 'Close' end as status,
		a.description,
		a.deleted_at as deleted,
		a.updated_at as lastOpDate,
		a.created_at as createDate
	from (select * from `naas`.endpoint where deleted = 0) a 
	left join `naas_cxp`.NodeEO b on a.node_id = b.uuid
	) aa where aa.nodeUuid is not null  

/*数据验证：code唯一性*/
select count(*) from `naas_cxp`.EndpointEO;/*176*/
select count(*) from (select distinct code from `naas_cxp`.EndpointEO) a;/*176*/

