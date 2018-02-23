##142
create table `naas_cxp`.bsl_sharePort as
select	distinct
	a.uuid as physicalSwitchUuid,
	a.code as physicalSwitchCode,
	a.name as physicalSwitchName,
	c.uuid as endpointUuid,
	c.code as endpointCode,
	c.name as endpointName,
	b.uuid as switchUuid,
	b.code as switchCode,
	b.name as switchName,
	d.uuid as switchPortUuid,
	d.portName as switchPortName
from	`naas_cxp`.PhysicalSwitchEO a,
	`naas_cxp`.SwitchEO b,
	`naas_cxp`.EndpointEO c,
	`naas_cxp`.SwitchPortVO d
where	a.uuid = b.physicalSwitchUuid
and	c.uuid = b.endpointUuid
and	b.uuid = d.switchUuid
and	d.uuid in (
		##同一个端口被不同用户用了,肯定为共享口
		select	aa.switchPortUuid 
		from	(select a.switchPortUuid, a.user_id from `naas_cxp`.bsl_tunnel a group by a.switchPortUuid, a.user_id) aa 
		group by aa.switchPortUuid HAVING count(aa.switchPortUuid) > 1	

		union all

		##找出ECP端口
		select	switch_port_id as switchPortUuid from `naas`.switch_port where label = 'ecp'

		union all

		##手动找出互联云端口
		select	uuid as switchPortUuid 
		from	`naas_cxp`.SwitchPortVO 
		where	uuid in (
			'1930','1992','784','516','894','1978','1976','672','1792','666',
			'1804','628','442','1042','1588','1586','444','1050','632','638',
			'764','684','618','556','1158','1170','1156','1296','1612','2052',
			'2054','902','1014','1610','706','850','2132','670','1554','2032',
			'1168','2134','552','794','796','1848','1852','782','2026','1072',
			'1074','900','1422','1968','1970','1972','2006','2016','2000','2002'
			)
		);

##修改SwitchPortVO的portType,首先默认云端口是共享口
update	`naas_cxp`.SwitchPortVO set portType = 'SHARE' where uuid in (select switchPortUuid from `naas_cxp`.bsl_sharePort);
update	`naas_cxp`.SwitchPortVO set portType = 'SFP_10G' where uuid not in (select switchPortUuid from `naas_cxp`.bsl_sharePort);

##修改SwitchPortVO的portType,云端口中有ACCESS端口，为云的独享口
update	`naas_cxp`.SwitchPortVO set portType = 'SFP_10G' where uuid in (select switchPortUuid from `naas_cxp`.bsl_tunnel where port_type = 'ACCESS');





