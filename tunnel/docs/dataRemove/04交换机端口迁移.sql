##导入SwitchPortVO数据                 
insert into `naas_cxp`.SwitchPortVO 
select * from (
	select	a.switch_port_id as uuid,
		b.uuid as switchUuid,
		null as portNum,
		a.port_name as portName,
		'UNDO' as portType,
		null as portAttribute,
		0 as autoAllot,
		case when a.enabled = 'true' then 'Enabled' else 'Disabled' end as state,
		a.updated_at as lastOpDate,
		a.created_at as createDate
	from	(select * from `naas`.switch_port where label in ('accessin','ecp')) a
	left join `naas_cxp`.SwitchEO b on a.switch_id = b.uuid
	) aa where aa.switchUuid is not null;

##数据验证：端口名称在一个物理交换机下要唯一
create table `naas_cxp`.bsl_samePortName as
select	bb.uuid as switchPortUuid,bb.portName,bb.switchUuid,bb.name as switchName,bb.physicalSwitchUuid,bb.endpointUuid
from	(
	select	a.uuid,a.portName,a.switchUuid,b.name,b.physicalSwitchUuid,b.endpointUuid
	from	`naas_cxp`.SwitchPortVO a 
	left join `naas_cxp`.SwitchEO b on a.switchUuid = b.uuid
	) bb ,
	(
	select	aa.portName,aa.physicalSwitchUuid 
	from(
		select a.uuid,a.portName,b.physicalSwitchUuid 
		from `naas_cxp`.SwitchPortVO a 
		left join `naas_cxp`.SwitchEO b on a.switchUuid = b.uuid
	) aa
	group by aa.portName,aa.physicalSwitchUuid
	having count(aa.portName) > 1
	) cc
where	bb.portName = cc.portName and bb.physicalSwitchUuid = cc.physicalSwitchUuid
order by bb.physicalSwitchUuid,bb.portName;
##57条记录同一个物理交换机下端口名重复