##bsl_tunnel中间表
create table `naas_cxp`.bsl_tunnel as

select * from (
	select	bbb.tunnel_point_id,
		aaa.tunnel_id,aaa.user_id,aaa.name,aaa.endpointA,aaa.endpointB,aaa.bandwidth,aaa.vni,aaa.distance,aaa.state,aaa.status,
		bbb.switchPortUuid,bbb.switchUuid,bbb.physicalSwitchUuid,bbb.endpointUuid,bbb.vlanid,bbb.inner_vlan,bbb.port_type,bbb.deleted_at,
		case when aaa.endpointA = aaa.endpointB then 'S' 
		     when aaa.endpointA = bbb.endpointUuid then 'A'
		     when aaa.endpointB = bbb.endpointUuid then 'Z'
		end  as sortTag,
		aaa.updated_at,aaa.created_at
	from	(select * from `naas`.tunnel where deleted = 0 and state != 'applying') aaa
	left join(
		select	aa.tunnel_point_id,aa.tunnel_id,bb.switchPortUuid,bb.switchUuid,bb.physicalSwitchUuid,bb.endpointUuid,aa.vlanid,aa.inner_vlan,aa.port_type,aa.deleted_at
		from	(
			select	a.tunnel_point_id,a.tunnel_id,b.switch_port_id,b.vlanid,b.inner_vlan,b.port_type,a.deleted_at
			from	(select tunnel_point_id,tunnel_id,deleted_at,updated_at,created_at from `naas`.tunnel_point where deleted = 0) a
				left join (
				select tunnel_point_id,switch_port_id,vlanid,inner_vlan,port_type 
				from `naas`.tunnel_point_switch_port where deleted = 0
				) b on a.tunnel_point_id = b.tunnel_point_id
			) aa,
			(
			select	d.uuid as switchUuid,d.physicalSwitchUuid,d.endpointUuid,e.uuid as switchPortUuid 
			from	`naas_cxp`.SwitchEO d,`naas_cxp`.SwitchPortVO e 
			where	d.uuid = e.switchUuid
			) bb
		where	aa.switch_port_id = bb.switchPortUuid
		) bbb on aaa.tunnel_id = bbb.tunnel_id	
) aaaa	where	aaaa.tunnel_point_id is not null;

##首先看switchPortUuid有没有NULL

##找出tunnel_id没有2条记录的
select * from `naas_cxp`.bsl_tunnel group by tunnel_id having count(tunnel_id) != 2;
/**
--340	妈妈帮	(删)
--986	奥飞	(只有一个tunnelPoint,删) 
--1444	迅游	(只有一个tunnelPoint,删) 
--确认以上tunnel是否只有一个tunnelpoint
**/

##删除错误数据
delete from `naas_cxp`.bsl_tunnel where tunnel_id in ('340','986','1444');

##删除存在的三层tunnel数据 
delete from `naas_cxp`.bsl_tunnel where tunnel_id in ('3272','2940');

/*结果：tunnel：649 tunnelPoint:1298 */

##建主键
alter table `naas_cxp`.bsl_tunnel add primary key (tunnel_point_id);

##修改 endpointA,endpointB
ALTER TABLE `naas_cxp`.bsl_tunnel MODIFY COLUMN endpointA varchar(32); 
ALTER TABLE `naas_cxp`.bsl_tunnel MODIFY COLUMN endpointB varchar(32); 