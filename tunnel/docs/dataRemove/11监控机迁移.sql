/*bsl_host中间表*/ 
create	table `naas_cxp`.bsl_host as

select	aa.host_switch_monitor_id,aa.host_id,aa.interface,aa.switch_id,aa.physicalSwitchUuid,aa.endpoint_id,aa.nodeUuid,aa.port_name,
	bb.name,bb.code,bb.ip,bb.username,bb.password,bb.deleted_at,bb.updated_at,bb.created_at
from	(
	SELECT a.host_switch_monitor_id,a.host_id,a.interface,a.switch_id,c.physicalSwitchUuid,a.endpoint_id,d.nodeUuid,b.port_name
	from   (select * from `naas`.host_switch_monitor where deleted = 0 ) a
	left join (select * from `naas`.switch_port where label = 'monitor') b on a.switch_id = b.switch_id
	left join `naas_cxp`.SwitchEO c on a.switch_id = c.uuid
	left join `naas_cxp`.EndpointEO d on a.endpoint_id = d.uuid
	where b.port_name is not null
	) aa,
	(
	select * from `naas`.host where deleted = 0 and monitor_state = 'deployed'
	) bb
where aa.host_id = bb.host_id;

/****************************************************************************************************************************************/
/*根据bsl_host 导入HostEO数据 */
insert	into `naas_cxp`.HostEO
select	distinct
	a.host_id as uuid,
	a.name,
	a.code,
	a.ip as hostIp,
	'MONITOR' as hostType,
	b.city as position,
	'Enabled' as state,
	'Connected' as status,
	a.deleted_at as deleted,
	a.updated_at as lastOpDate,
	a.created_at as createDate
from	`naas_cxp`.bsl_host a left join `naas_cxp`.NodeEO b on a.nodeUuid = b.uuid;

/*根据bsl_host 导入HostSwitchMonitorEO数据 */
insert	into `naas_cxp`.HostSwitchMonitorEO
select  distinct
	a.host_switch_monitor_id as uuid,
	a.host_id as hostUuid,
	a.physicalSwitchUuid,
	a.port_name as physicalSwitchPortName,
	a.interface as interfaceName,
	a.deleted_at as deleted,
	a.updated_at as lastOpDate,
	a.created_at as createDate
from	`naas_cxp`.bsl_host a;

/*根据bsl_host 导入MonitorHostVO数据 */
insert	into `naas_cxp`.MonitorHostVO
select	distinct
	a.host_id as uuid,
	a.nodeUuid,
	a.username,
	a.password,
	22 as sshPort,
	'TUNNEL' as monitorType
from	`naas_cxp`.bsl_host a;