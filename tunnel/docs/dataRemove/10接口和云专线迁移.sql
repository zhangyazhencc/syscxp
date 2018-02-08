##根据bsl_tunnel 导入tunnel数据

insert	into `naas_cxp`.TunnelEO
select  distinct 
	tunnel_id as uuid,
	user_id as accountUuid,
	user_id as ownerAccountUuid,
	vni as vsi,
	null as monitorCidr,
	name,
	case when bandwidth*1024*1024 <= 2097152 then '2M'
	     when bandwidth*1024*1024 > 2097152 and bandwidth*1024*1024 <= 5242880 then '5M'
	     when bandwidth*1024*1024 > 5242880 and bandwidth*1024*1024 <= 10485760 then '10M'
	     when bandwidth*1024*1024 > 10485760 and bandwidth*1024*1024 <= 20971520 then '20M'
	     when bandwidth*1024*1024 > 20971520 and bandwidth*1024*1024 <= 52428800 then '50M'
	     when bandwidth*1024*1024 > 52428800 and bandwidth*1024*1024 <= 104857600 then '100M'
	     when bandwidth*1024*1024 > 104857600 and bandwidth*1024*1024 <= 209715200 then '200M'
	     when bandwidth*1024*1024 > 209715200 and bandwidth*1024*1024 <= 524288000 then '500M'
	     when bandwidth*1024*1024 > 524288000 and bandwidth*1024*1024 <= 1073741824 then '1G'
	     when bandwidth*1024*1024 > 1073741824 and bandwidth*1024*1024 <= 2147483648 then '2G'
	     when bandwidth*1024*1024 > 2147483648 and bandwidth*1024*1024 <= 5368709120 then '5G'
	     when bandwidth*1024*1024 > 5368709120 and bandwidth*1024*1024 <= 10737418240 then '10G'
	     when bandwidth*1024*1024 > 10737418240 then '20G' end as bandwidthOffering,
	bandwidth*1024*1024 as bandwidth,
	distance*1000,
	case when state = 'normal' or state = 'deployed' then 'Enabled' else 'Disabled' end as state,
	case when status = 'normal' and (state = 'normal' or state = 'deployed') then 'Connected' else 'Disconnected' end as status,
	'UNDO' as type,
	null as innerEndpointUuid,
	'Disabled' as monitorState,
	deleted_at as deleted,
	null as description,
	1 as duration,
	'BY_MONTH' as productChargeModel,
	5 as maxModifies,
	'2018-06-01 00:00:00' as expireDate,
	updated_at as lastOpDate,
	created_at as createDate
from	`naas_cxp`.bsl_tunnel;

##专线类型后续用java改

##根据bsl_tunnel 导入Interface数据 

insert	into `naas_cxp`.InterfaceEO
select   
	a.tunnel_point_id as uuid,
	a.user_id as accountUuid,
	a.user_id as ownerAccountUuid,
	concat(b.name,'_共享接口_',a.tunnel_point_id) as name,
	a.switchPortUuid,
	a.endpointUuid,
	null as description,
	'Up' as state,
	'TRUNK' as type,
	a.deleted_at as deleted,
	1 as duration,
	'BY_MONTH' as productChargeModel,
	5 as maxModifies,
	null as expireDate,
	a.updated_at as lastOpDate,
	a.created_at as createDate
from	(select * from `naas_cxp`.bsl_tunnel c where c.switchPortUuid in (select uuid from `naas_cxp`.SwitchPortVO where portType = 'SHARE')) a 
left join `naas_cxp`.EndpointEO b on a.endpointUuid = b.uuid

union all

select	max(aa.uuid) as uuid,
	max(aa.accountUuid) as accountUuid,
	max(aa.ownerAccountUuid) as ownerAccountUuid,
	max(aa.name) as name,
	aa.switchPortUuid,
	max(aa.endpointUuid) as endpointUuid,
	max(aa.description) as description,
	max(aa.state) as state,
	max(aa.type) as type,
	max(aa.deleted) as deleted,
	max(aa.duration) as duration,
	max(aa.productChargeModel) as productChargeModel,
	max(aa.maxModifies) as maxModifies,
	max(aa.expireDate) as expireDate,
	max(aa.lastOpDate) as lastOpDate,
	max(aa.createDate) as createDate
from	(
	select   
		a.tunnel_point_id as uuid,
		a.user_id as accountUuid,
		a.user_id as ownerAccountUuid,
		concat(b.name,'_接口_',a.tunnel_point_id) as name,
		a.switchPortUuid,
		a.endpointUuid,
		null as description,
		'Up' as state,
		case when a.port_type = 'ACCESS' then 'ACCESS' else 'TRUNK' end as type,
		a.deleted_at as deleted,
		1 as duration,
		'BY_MONTH' as productChargeModel,
		5 as maxModifies,
		'2018-06-01 00:00:00' as expireDate,
		a.updated_at as lastOpDate,
		a.created_at as createDate
	from	(select * from `naas_cxp`.bsl_tunnel c where c.switchPortUuid in (select uuid from `naas_cxp`.SwitchPortVO where portType != 'SHARE')) a 
	left join `naas_cxp`.EndpointEO b on a.endpointUuid = b.uuid
	) aa
group by aa.switchPortUuid;

##ACCESS检查是否唯一
SELECT count(*) from (select * from `naas_cxp`.InterfaceEO where type = 'ACCESS') a;
SELECT count(*) from (select distinct switchPortUuid from `naas_cxp`.InterfaceEO where type = 'ACCESS') a;
SELECT count(*) from (SELECT * FROM `naas_cxp`.InterfaceEO WHERE switchPortUuid in (select distinct switchPortUuid from `naas_cxp`.InterfaceEO where type = 'ACCESS')) a;

##根据InterfaceEO导入EdgeLineEO
insert	into `naas_cxp`.EdgeLineEO
select
	a.uuid,
	a.accountUuid as accountUuid,
	a.uuid as interfaceUuid,
	a.endpointUuid as endpointUuid,
	'到IDC' as type,
	'无' as destinationInfo,
	null as description,
	'Opened' as state,
	0 as prices,
	a.deleted as deleted,
	a.expireDate as expireDate,
	a.lastOpDate as lastOpDate,
	a.createDate as createDate
from	`naas_cxp`.InterfaceEO a 
where	a.expireDate is not null;

##根据bsl_tunnel 导入TunnelSwitchPortVO数据
insert	into `naas_cxp`.TunnelSwitchPortVO
select	
	a.tunnel_point_id as uuid,
	a.tunnel_id as tunnelUuid,
	a.tunnel_point_id as interfaceUuid,
	a.endpointUuid,
	a.switchPortUuid,
	a.port_type as type,
	a.vlanid as vlan,
	a.sortTag,
	a.physicalSwitchUuid as physicalSwitchUuid,
	a.physicalSwitchUuid as ownerMplsSwitchUuid,
	'UNDO' as peerMplsSwitchUuid,
	a.updated_at as lastOpDate,
	a.created_at as createDate
from	`naas_cxp`.bsl_tunnel a,
	(select distinct switchPortUuid from `naas_cxp`.InterfaceEO where expireDate is null) b
where	a.switchPortUuid = b.switchPortUuid

UNION ALL

select	distinct
	a.tunnel_point_id as uuid,
	a.tunnel_id as tunnelUuid,
	b.uuid as interfaceUuid,
	a.endpointUuid,
	a.switchPortUuid,
	a.port_type as type,
	a.vlanid as vlan,
	a.sortTag,
	a.physicalSwitchUuid as physicalSwitchUuid,
	a.physicalSwitchUuid as ownerMplsSwitchUuid,
	'UNDO' as peerMplsSwitchUuid,
	a.updated_at as lastOpDate,
	a.created_at as createDate
from	`naas_cxp`.bsl_tunnel a,
	(select distinct uuid,switchPortUuid from `naas_cxp`.InterfaceEO where expireDate is not null) b
where	a.switchPortUuid = b.switchPortUuid;

##TunnelSwitchPort 的peerMplsSwitchUuid
update	`naas_cxp`.TunnelSwitchPortVO aa,
	(
	select	a.uuid,b.physicalSwitchUuid as peerMplsSwitchUuid
	from	(
		select	uuid,tunnelUuid,physicalSwitchUuid
		from	`naas_cxp`.TunnelSwitchPortVO 
		WHERE	sortTag = 'A'
		) a,
		(
		select	uuid,tunnelUuid,physicalSwitchUuid
		from	`naas_cxp`.TunnelSwitchPortVO 
		WHERE	sortTag = 'Z'
		) b
	where	a.tunnelUuid = b.tunnelUuid

	union all

	select b.uuid,a.physicalSwitchUuid as peerMplsSwitchUuid
	from	(
		select	uuid,tunnelUuid,physicalSwitchUuid
		from	`naas_cxp`.TunnelSwitchPortVO 
		WHERE	sortTag = 'A'
		) a,
		(
		select	uuid,tunnelUuid,physicalSwitchUuid
		from	`naas_cxp`.TunnelSwitchPortVO 
		WHERE	sortTag = 'Z'
		) b
	where	a.tunnelUuid = b.tunnelUuid
	) bb 
set aa.peerMplsSwitchUuid = bb.peerMplsSwitchUuid
where aa.uuid = bb.uuid;

##导入QINQ
##用EXCEL手工做
select	distinct tunnel_id,inner_vlan 
from	`naas_cxp`.bsl_tunnel 
where	port_type = 'QINQ' ;

select	distinct tunnel_id,
	trim(substring_index(inner_vlan,'to',1)) as startVlan,
	trim(substring_index(inner_vlan,'to',-1)) as endVlan 
from	bsl_tunnel where port_type = 'QINQ' and inner_vlan like '%to%';









	


