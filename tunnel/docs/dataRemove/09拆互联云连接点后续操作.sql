##将剩下的连接点的类型设为 ACCESSIN
update `naas_cxp`.EndpointEO set endpointType = 'ACCESSIN' where endpointType = 'UNDO';

##再次修改bsl_tunnel--更新endpointA,endpointB

update	`naas_cxp`.bsl_tunnel a,
	(select tunnel_id,endpointUuid from `naas_cxp`.bsl_tunnel where sortTag = 'A') b
set	a.endpointA = b.endpointUuid
where	a.tunnel_id = b.tunnel_id;

update	`naas_cxp`.bsl_tunnel a,
	(select tunnel_id,endpointUuid from `naas_cxp`.bsl_tunnel where sortTag = 'Z') b
set	a.endpointB = b.endpointUuid
where	a.tunnel_id = b.tunnel_id;

##检查是否还有同连接点专线
select * from `naas_cxp`.bsl_tunnel where endpointA = endpointB;