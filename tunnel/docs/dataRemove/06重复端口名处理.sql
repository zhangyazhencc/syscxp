##重复未被使用的端口
select switchPortUuid from `naas_cxp`.bsl_samePortName where switchPortUuid not in (select distinct switchPortUuid from `naas_cxp`.bsl_tunnel); 

##再次检查未被使用的端口
select * from `naas`.tunnel_point_switch_port where switch_port_id in (select switchPortUuid from `naas_cxp`.bsl_samePortName where switchPortUuid not in (select distinct switchPortUuid from `naas_cxp`.bsl_tunnel));

##检查通过后删除未被使用的端口
delete from `naas_cxp`.SwitchPortVO where uuid in (select switchPortUuid from `naas_cxp`.bsl_samePortName where switchPortUuid not in (select distinct switchPortUuid from `naas_cxp`.bsl_tunnel));

##重复被使用的端口
create table `naas_cxp`.bsl_samePortName2 as
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

##手工做：SwitchPortVO 删除端口，bsl_tunnel换掉端口，交换机，连接点
##注意：bsl_tunnel 中 sortTag = 'S' 的连接点慎重考虑

delete	from `naas_cxp`.SwitchPortVO where uuid = '';

update	`naas_cxp`.bsl_tunnel set switchPortUuid = '' ,switchUuid = '',endpointUuid = ''
where	switchPortUuid = '';

##换完之后再次检查有无相同端口
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

##修改bsl_tunnel--更新endpointA,endpointB

update	`naas_cxp`.bsl_tunnel a,
	(select tunnel_id,endpointUuid from `naas_cxp`.bsl_tunnel where sortTag = 'A') b
set	a.endpointA = b.endpointUuid
where	a.tunnel_id = b.tunnel_id;

update	`naas_cxp`.bsl_tunnel a,
	(select tunnel_id,endpointUuid from `naas_cxp`.bsl_tunnel where sortTag = 'Z') b
set	a.endpointB = b.endpointUuid
where	a.tunnel_id = b.tunnel_id;

update	`naas_cxp`.bsl_tunnel a
set	a.sortTag = 'S'
where	a.endpointA = a.endpointB;
##检查sortTag = 'S' 的专线endpointA,endpointB

##修改bsl_tunnel里的sortTag为AZ
select * from `naas_cxp`.bsl_tunnel where sortTag = 'S' ORDER BY tunnel_id;















