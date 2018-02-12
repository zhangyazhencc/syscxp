##香港HK2-阿里云
insert into `naas_cxp`.EndpointEO 
values 
('312AliYun','202','香港HK2-阿里云','HK_HK2_AliYun','CLOUD','AliYun','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('512AliYun','512','312AliYun','hk_hk2_S6720_3_Aliyun','hk_hk2_S6720_3_阿里云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '512AliYun' where uuid in ('1930','1992');

update `naas_cxp`.bsl_tunnel set switchUuid = '512AliYun',endpointUuid = '312AliYun'
where switchPortUuid in ('1930','1992');

##上海宝山阿里云
insert into `naas_cxp`.EndpointEO 
values 
('164AliYun','134','上海宝山阿里云','SH_BS_AliYun','CLOUD','AliYun','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('214AliYun','214','164AliYun','SH_BSLT_HWSW_S6720_Aliyun','传输-B-宝山联通-6720_阿里云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '214AliYun' where uuid in ('784','516','894','1978','1976','672');

update `naas_cxp`.bsl_tunnel set switchUuid = '214AliYun',endpointUuid = '164AliYun'
where switchPortUuid in ('784','516','894','1978','1976','672');

##杭州福地阿里云
insert into `naas_cxp`.EndpointEO 
values 
('120AliYun','17','杭州福地阿里云','HZ_FD_AliYun','CLOUD','AliYun','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('504AliYun','504','120AliYun','HZ-FD-6720-48-01_Aliyun','HZ-杭州福地-6720-48-01_阿里云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '504AliYun' where uuid in ('1792');

update `naas_cxp`.bsl_tunnel set switchUuid = '504AliYun',endpointUuid = '120AliYun'
where switchPortUuid in ('1792');

insert into `naas_cxp`.SwitchEO 
values 
('168AliYun','168','120AliYun','HZ_FD_HWSW_S6700_Aliyun','传输-H-杭州福地-6700_阿里云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '168AliYun' where uuid in ('666','1804');

update `naas_cxp`.bsl_tunnel set switchUuid = '168AliYun',endpointUuid = '120AliYun'
where switchPortUuid in ('666','1804');

##北京大兴阿里云 不用拆
update `naas_cxp`.EndpointEO set endpointType = 'CLOUD',cloudType = 'AliYun' where uuid in ('12','18');

##深圳福保阿里云
insert into `naas_cxp`.EndpointEO 
values 
('262AliYun','92','深圳福保阿里云','SZ_FB_AliYun','CLOUD','AliYun','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('308AliYun','308','262AliYun','SZ_FB_HWSW_S6720_2_Aliyun','传输-S-深圳福保-6720-1_阿里云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '308AliYun' where uuid in ('638','764','684','618');

update `naas_cxp`.bsl_tunnel set switchUuid = '308AliYun',endpointUuid = '262AliYun'
where switchPortUuid in ('638','764','684','618');

##上海金桥阿里云
insert into `naas_cxp`.EndpointEO 
values 
('168AliYun','37','上海金桥阿里云','SH_JQ_AliYun','CLOUD','AliYun','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('218AliYun','218','168AliYun','SH_JQ_HWSW_S6720_Aliyun','传输-J-金桥-6720_阿里云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '218AliYun' where uuid in ('556','1158','1170','1156');

update `naas_cxp`.bsl_tunnel set switchUuid = '218AliYun',endpointUuid = '168AliYun'
where switchPortUuid in ('556','1158','1170','1156');

##北京银科腾讯云（铁科院）
insert into `naas_cxp`.EndpointEO 
values 
('174Tencent','27','北京银科腾讯云-1','BJ_TKY_Tencent_1','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('224Tencent','224','174Tencent','BJ_TKY_HWSW_S9306_1_Tencent','城网-T-铁科院-9306-1_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '224Tencent' where uuid in ('1296','1612','2052');

update `naas_cxp`.bsl_tunnel set switchUuid = '224Tencent',endpointUuid = '174Tencent'
where switchPortUuid in ('1296','1612','2052');

insert into `naas_cxp`.EndpointEO 
values 
('124Tencent','27','北京银科腾讯云-2','BJ_TKY_Tencent_2','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('172Tencent','172','124Tencent','BJ_TKY_HWSW_S9312_Tencent','城网-T-铁科院-9312_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '172Tencent' where uuid in ('2054','902');

update `naas_cxp`.bsl_tunnel set switchUuid = '172Tencent',endpointUuid = '124Tencent'
where switchPortUuid in ('2054','902');

##北京西格玛腾讯云
insert into `naas_cxp`.EndpointEO 
values 
('302Tencent','194','北京西格玛腾讯云','BJ_TYY_Tencent','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('346Tencent','346','302Tencent','BJ_TYY_HWSW_S6720_1_Tencent','T-太阳园-S6720-01_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '346Tencent' where uuid in ('1014','1610');

update `naas_cxp`.bsl_tunnel set switchUuid = '346Tencent',endpointUuid = '302Tencent'
where switchPortUuid in ('1014','1610');

##虹漕路腾讯云
insert into `naas_cxp`.EndpointEO 
values 
('166Tencent','36','虹漕路腾讯云','SH_GPL_Tencent','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('216Tencent','216','166Tencent','SH_GPL_HWSW_S6700_Tencent','传输-G-桂平路-6700_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '216Tencent' where uuid in ('706');

update `naas_cxp`.bsl_tunnel set switchUuid = '216Tencent',endpointUuid = '166Tencent'
where switchPortUuid in ('706');

##上海腾大腾讯云
insert into `naas_cxp`.EndpointEO 
values 
('132Tencent','8','上海腾大腾讯云-2','SH_CBL_Tencent-2','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('210Tencent','210','132Tencent','SH_CBL_HWSW_S9303_2_Tencent','IDC-CBL-CTC-S9303_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '210Tencent' where uuid in ('850');

update `naas_cxp`.bsl_tunnel set switchUuid = '210Tencent',endpointUuid = '132Tencent'
where switchPortUuid in ('850');

insert into `naas_cxp`.EndpointEO 
values 
('230Tencent','8','上海腾大腾讯云-1','SH_CBL_Tencent-1','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('276Tencent','276','230Tencent','SH_CBL_HWSW_S6720_Tencent','上海-C-漕宝路-6720-1_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '276Tencent' where uuid in ('2132','670');

update `naas_cxp`.bsl_tunnel set switchUuid = '276Tencent',endpointUuid = '230Tencent'
where switchPortUuid in ('2132','670');

##深圳腾讯云
insert into `naas_cxp`.EndpointEO 
values 
('304Tencent','196','深圳腾讯云','SZ_KX_Tencent','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('348Tencent','348','304Tencent','SZ_KX_HWSW_S6720_1_1_Tencent','传输-S-深圳科兴-6720-1_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '348Tencent' where uuid in ('1554');

update `naas_cxp`.bsl_tunnel set switchUuid = '348Tencent',endpointUuid = '304Tencent'
where switchPortUuid in ('1554');

##香港HK2-3腾讯云
insert into `naas_cxp`.EndpointEO 
values 
('312Tencent','202','香港HK2-3腾讯云','HK_HK2_Tencent','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('512Tencent','512','312Tencent','hk_hk2_S6720_3_Tencent','hk_hk2_S6720_3_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '512Tencent' where uuid in ('2032');

update `naas_cxp`.bsl_tunnel set switchUuid = '512Tencent',endpointUuid = '312Tencent'
where switchPortUuid in ('2032');

##上海斜土腾讯云
insert into `naas_cxp`.EndpointEO 
values 
('114Tencent','68','上海斜土腾讯云','SH_XTL_Tencent','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('372Tencent','372','114Tencent','SH_XT_HWSW_S6720_1_Tencent','传输-X-斜土-6720-1_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '372Tencent' where uuid in ('1168','2134');

update `naas_cxp`.bsl_tunnel set switchUuid = '372Tencent',endpointUuid = '114Tencent'
where switchPortUuid in ('1168','2134');

insert into `naas_cxp`.SwitchEO 
values 
('162Tencent','162','114Tencent','SH_XTL_HWSW_S9306_Tencent','传输-S-上海斜土路-9306_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '162Tencent' where uuid in ('552','794');

update `naas_cxp`.bsl_tunnel set switchUuid = '162Tencent',endpointUuid = '114Tencent'
where switchPortUuid in ('552','794');

##广州科兴城腾讯云
insert into `naas_cxp`.EndpointEO 
values 
('280Tencent','178','广州科兴城腾讯云-1','GZ_DY_Tencent_1','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('322Tencent','322','280Tencent','GZ_DY_HWSW_S6720_1_Tencent','G-广州-DY-6720-1_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '322Tencent' where uuid in ('1848','1852','782');

update `naas_cxp`.bsl_tunnel set switchUuid = '322Tencent',endpointUuid = '280Tencent'
where switchPortUuid in ('1848','1852','782');

insert into `naas_cxp`.EndpointEO 
values 
('282Tencent','178','广州科兴城腾讯云-2','GZ_DY_Tencent_2','CLOUD','Tencent','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('324Tencent','324','282Tencent','GZ_DY_HWSW_S6720_2_Tencent','G-广州-DY-6720-2_腾讯云','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '324Tencent' where uuid in ('796');

update `naas_cxp`.bsl_tunnel set switchUuid = '324Tencent',endpointUuid = '282Tencent'
where switchPortUuid in ('796');

## UCloud
insert into `naas_cxp`.EndpointEO 
values 
('112UCloud','3','东方广场数据中心_UCloud','BJ_DFGC_UCloud','CLOUD','UCloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('548UCloud','548','112UCloud','bj_dfgc_S6720_48_01_UCloud','bj_东方广场_S6720_48_01_UCloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '548UCloud' where uuid in ('2026');

update `naas_cxp`.bsl_tunnel set switchUuid = '548UCloud',endpointUuid = '112UCloud'
where switchPortUuid in ('2026');

##京东云 不用拆
update `naas_cxp`.EndpointEO set endpointType = 'CLOUD',cloudType = 'JD' where uuid in ('314');

##金山云 不用拆
update `naas_cxp`.EndpointEO set endpointType = 'CLOUD',cloudType = 'Ksyun' where uuid in ('292');

##华为云 不用拆
update `naas_cxp`.EndpointEO set endpointType = 'CLOUD',cloudType = 'Huawei' where uuid in ('416','430');

##百度云 不用拆
update `naas_cxp`.EndpointEO set endpointType = 'CLOUD',cloudType = 'Baidu' where uuid in ('420');

## ECP
##
insert into `naas_cxp`.EndpointEO 
values 
('66Syscloud','144','X-香港-EQ1_Syscloud','HK_EQ1_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('320Syscloud','320','66Syscloud','HK_EQ1_HWSW_S6720_1_Syscloud','传输-X香港-EQ1-6720-1_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '320Syscloud' where uuid in ('1822','1834');

update `naas_cxp`.bsl_tunnel set switchUuid = '320Syscloud',endpointUuid = '66Syscloud'
where switchPortUuid in ('1822','1834');

insert into `naas_cxp`.SwitchEO 
values 
('68Syscloud','68','66Syscloud','HK_EQ1_HWSW_S6720_2_Syscloud','传输-X-香港-EQ1-6720-2_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '68Syscloud' where uuid in ('506');

update `naas_cxp`.bsl_tunnel set switchUuid = '68Syscloud',endpointUuid = '66Syscloud'
where switchPortUuid in ('506');

##
insert into `naas_cxp`.EndpointEO 
values 
('112Syscloud','3','东方广场数据中心_Syscloud','BJ_DFGC_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('160Syscloud','160','112Syscloud','BJ_DFGC_HWSW_S9312_Syscloud','IDC-DFGC-BGP-S9312*_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '160Syscloud' where uuid in ('458');

update `naas_cxp`.bsl_tunnel set switchUuid = '160Syscloud',endpointUuid = '112Syscloud'
where switchPortUuid in ('458');

##
insert into `naas_cxp`.EndpointEO 
values 
('116Syscloud','6','亦庄KDDI数据中心_Syscloud','BJ_KDDI_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('164Syscloud','164','116Syscloud','BJ_KDDI_HWSW_S9306_Syscloud','传输-B-北京KDDI-9306_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '164Syscloud' where uuid in ('464');

update `naas_cxp`.bsl_tunnel set switchUuid = '164Syscloud',endpointUuid = '116Syscloud'
where switchPortUuid in ('464');

##
insert into `naas_cxp`.EndpointEO 
values 
('118Syscloud','5','北京国门数据中心_Syscloud','BJ_GM_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('166Syscloud','166','118Syscloud','BJ_GM_HWSW_S9306_Syscloud','城网-G-国门-9306_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '166Syscloud' where uuid in ('468');

update `naas_cxp`.bsl_tunnel set switchUuid = '166Syscloud',endpointUuid = '118Syscloud'
where switchPortUuid in ('468');

##
insert into `naas_cxp`.EndpointEO 
values 
('120Syscloud','17','杭州福地数据中心_Syscloud','HZ_FD_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('168Syscloud','168','120Syscloud','HZ_FD_HWSW_S6700_Syscloud','传输-H-杭州福地-6700_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '168Syscloud' where uuid in ('472');

update `naas_cxp`.bsl_tunnel set switchUuid = '168Syscloud',endpointUuid = '120Syscloud'
where switchPortUuid in ('472');

##
insert into `naas_cxp`.EndpointEO 
values 
('132Syscloud','8','漕宝路数据中心_Syscloud','SH_CBL_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('210Syscloud','210','132Syscloud','SH_CBL_HWSW_S9303_2_Syscloud','IDC-CBL-CTC-S9303_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '210Syscloud' where uuid in ('498');

update `naas_cxp`.bsl_tunnel set switchUuid = '210Syscloud',endpointUuid = '132Syscloud'
where switchPortUuid in ('498');

##
insert into `naas_cxp`.EndpointEO 
values 
('228Syscloud','11','广州金发数据中心_Syscloud','GZ_JF_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('274Syscloud','274','228Syscloud','GZ_JF_HWSW_S6720_2_2_Syscloud','传输-G-广州金发-6720-2_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '274Syscloud' where uuid in ('590');

update `naas_cxp`.bsl_tunnel set switchUuid = '274Syscloud',endpointUuid = '228Syscloud'
where switchPortUuid in ('590');

##
insert into `naas_cxp`.EndpointEO 
values 
('262Syscloud','92','深圳福田保税区数据中心_Syscloud','SZ_FB_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('308Syscloud','308','262Syscloud','SZ_FB_HWSW_S6720_2_Syscloud','传输-S-深圳福保-6720-1_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '308Syscloud' where uuid in ('682');

update `naas_cxp`.bsl_tunnel set switchUuid = '308Syscloud',endpointUuid = '262Syscloud'
where switchPortUuid in ('682');

##
insert into `naas_cxp`.EndpointEO 
values 
('342Syscloud','230','日本－东京-TY8_Syscloud','JP-TY8_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('440Syscloud','440','342Syscloud','jp_tky_S6720_48_01_Syscloud','JP-东京－S6720-48-1_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '440Syscloud' where uuid in ('1444');

update `naas_cxp`.bsl_tunnel set switchUuid = '440Syscloud',endpointUuid = '342Syscloud'
where switchPortUuid in ('1444');

##
insert into `naas_cxp`.EndpointEO 
values 
('344Syscloud','232','新加坡－SG2_Syscloud','SIN-SG2_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('442Syscloud','442','344Syscloud','sin_sin_S6720_48_01_Syscloud','sin_新加坡_S6720_48_01_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '442Syscloud' where uuid in ('1472');

update `naas_cxp`.bsl_tunnel set switchUuid = '442Syscloud',endpointUuid = '344Syscloud'
where switchPortUuid in ('1472');

##
insert into `naas_cxp`.EndpointEO 
values 
('346Syscloud','234','德国－法兰克福_Syscloud','ger-fra3_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('444Syscloud','444','346Syscloud','ger_fra_S6720_48_01_Syscloud','ger_法兰克福_S6720_48_01_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '444Syscloud' where uuid in ('1470');

update `naas_cxp`.bsl_tunnel set switchUuid = '444Syscloud',endpointUuid = '346Syscloud'
where switchPortUuid in ('1470');

##
insert into `naas_cxp`.EndpointEO 
values 
('382Syscloud','268','美国_洛杉矶_LA_Syscloud','USA-LA_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('474Syscloud','474','382Syscloud','usa_la_S6720_48_01_Syscloud','usa_洛杉矶_S6720_48_01_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '474Syscloud' where uuid in ('1556');

update `naas_cxp`.bsl_tunnel set switchUuid = '474Syscloud',endpointUuid = '382Syscloud'
where switchPortUuid in ('1556');

##
insert into `naas_cxp`.EndpointEO 
values 
('384Syscloud','270','美国－硅谷－SV5_Syscloud','USA-SV5_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('476Syscloud','476','384Syscloud','usa_sv_S6720_48_01_Syscloud','usa-硅谷－6720-48-01_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '476Syscloud' where uuid in ('1574');

update `naas_cxp`.bsl_tunnel set switchUuid = '476Syscloud',endpointUuid = '384Syscloud'
where switchPortUuid in ('1574');

##
insert into `naas_cxp`.EndpointEO 
values 
('402Syscloud','306','台湾是方_Syscloud','TWN-TWN_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('494Syscloud','494','402Syscloud','TWN-TWN-6720-48-01_Syscloud','台湾是方-6720-48-01_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '494Syscloud' where uuid in ('1738');

update `naas_cxp`.bsl_tunnel set switchUuid = '494Syscloud',endpointUuid = '402Syscloud'
where switchPortUuid in ('1738');

##
insert into `naas_cxp`.EndpointEO 
values 
('410Syscloud','504','WH_武汉_银联_Syscloud','WH-YL_Syscloud','CLOUD','Syscloud','Enabled','Close',null,null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

insert into `naas_cxp`.SwitchEO 
values 
('506Syscloud','506','410Syscloud','wh_yl_S6720_48_01_Syscloud','wh_武汉银联_S6720_48_01_Syscloud','ACCESS',null,'Enabled','Connected',null,'2018-02-05 16:06:27','2018-02-05 16:06:27');

update `naas_cxp`.SwitchPortVO set switchUuid = '506Syscloud' where uuid in ('1904');

update `naas_cxp`.bsl_tunnel set switchUuid = '506Syscloud',endpointUuid = '410Syscloud'
where switchPortUuid in ('1904');







