create table `naas_cxp`.bsl_abroad as
select	aaa.tunnel_point_id,
	aaa.tunnel_id,
	aaa.name,
	aaa.endpointUuid,
	ccc.country,
	aaa.sortTag,
	bbb.tunnelType
FROM	`naas_cxp`.bsl_tunnel aaa,
	(select
				aa.tunnel_id,
				case when (aa.country = 'CHINA' and bb.country != 'CHINA') or (aa.country != 'CHINA' and bb.country = 'CHINA') THEN 'CHINA1ABROAD' ELSE 'no' end as tunnelType
	from (
				select a.tunnel_point_id,a.tunnel_id,a.name,a.endpointUuid,a.sortTag,b.country
				from (select * from `naas_cxp`.bsl_tunnel where sortTag = 'A') a 
				LEFT JOIN (select c.uuid,d.country from `naas_cxp`.EndpointEO c,`naas_cxp`.NodeEO d where c.nodeUuid = d.uuid) b on a.endpointUuid = b.uuid
				) aa,(
				select a.tunnel_point_id,a.tunnel_id,a.name,a.endpointUuid,a.sortTag,b.country
				from (select * from `naas_cxp`.bsl_tunnel where sortTag = 'Z') a 
				LEFT JOIN (select c.uuid,d.country from `naas_cxp`.EndpointEO c,`naas_cxp`.NodeEO d where c.nodeUuid = d.uuid) b on a.endpointUuid = b.uuid
				) bb
	where aa.tunnel_id = bb.tunnel_id
	) bbb,
	`naas_cxp`.NodeEO ccc,
	`naas_cxp`.EndpointEO ddd
where	bbb.tunnelType = 'CHINA1ABROAD'
AND 	aaa.tunnel_id = bbb.tunnel_id
and	aaa.endpointUuid = ddd.uuid
and	ddd.nodeUuid = ccc.uuid
order by aaa.tunnel_id;

##虚拟互联连接点（未加）

##tunnelType
update	`naas_cxp`.TunnelEO set type = 'CHINA1ABROAD' where uuid in (select distinct tunnel_id from `naas_cxp`.bsl_abroad);