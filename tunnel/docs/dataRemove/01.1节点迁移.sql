##导入NodeEO数据                   
insert	into `naas_cxp`.NodeEO
select  node_id as uuid,
        name,
        code,
        description,
        contact,
        telephone,
        null as country,
        province,
        city,
        address,
        longitude, 
        latitude,
        property,
        status,
        deleted_at as deleted,
        extension_info_id as extensionInfoUuid,
        updated_at as lastOpDate,
        created_at as createDate
from	`naas`.node       
where	deleted = 0;

##添加国家信息：根据原先省份distinct出来再处理  
select distinct province from `naas_cxp`.NodeEO;
update `naas_cxp`.NodeEO a set a.country = 'CHINA' WHERE a.province not in ('香港','海外','台湾');
update `naas_cxp`.NodeEO a set a.country = 'CHINA-HK' WHERE a.province = '香港';
update `naas_cxp`.NodeEO a set a.country = 'CHINA-TW' WHERE a.province = '台湾';

update `naas_cxp`.NodeEO a set a.country = 'CHINA-HK',a.province = '香港' ,a.city = '香港' WHERE a.uuid in ('292','294');
update `naas_cxp`.NodeEO a set a.country = '德国',a.province = '德国' ,a.city = '德国' WHERE a.uuid in ('414','252','234');
update `naas_cxp`.NodeEO a set a.country = '美国',a.province = '美国' ,a.city = '美国' WHERE a.uuid in ('286','302','300','298','270','268','532','284');
update `naas_cxp`.NodeEO a set a.country = '日本',a.province = '日本' ,a.city = '日本' WHERE a.uuid in ('248','230','290','452');
update `naas_cxp`.NodeEO a set a.country = '新加坡',a.province = '新加坡' ,a.city = '新加坡' WHERE a.uuid in ('496','232','250','288');


##修改status：Close Open
update `naas_cxp`.NodeEO a set a.status = 'Close' WHERE a.status = 'close' or a.status is null or a.status = '' or a.status = 'available';
update `naas_cxp`.NodeEO a set a.status = 'Open' WHERE a.status = 'open';

select distinct status from `naas_cxp`.NodeEO;

##修改property：CLOUD ACCESSIN IDC VPN ECP EXCHANGE
update `naas_cxp`.NodeEO a set a.property = 'IDC' WHERE a.property = 'idc_node';
update `naas_cxp`.NodeEO a set a.property = 'CLOUD' WHERE a.property = 'cloud_node';
update `naas_cxp`.NodeEO a set a.property = 'ACCESSIN' WHERE a.property = 'accessin_node';
update `naas_cxp`.NodeEO a set a.property = 'EXCHANGE' WHERE a.property = 'exchange_node';
update `naas_cxp`.NodeEO a set a.property = 'ECP' WHERE a.property = 'ecp_node';
update `naas_cxp`.NodeEO a set a.property = 'ACCESSIN' WHERE a.property = '';
update `naas_cxp`.NodeEO a set a.property = 'ACCESSIN,EXCHANGE' WHERE a.property = 'accessin_node exchange_node';
update `naas_cxp`.NodeEO a set a.property = 'ACCESSIN,IDC,EXCHANGE' WHERE a.property = 'accessin_node idc_node exchange_node';
update `naas_cxp`.NodeEO a set a.property = 'CLOUD,ACCESSIN,IDC,EXCHANGE' WHERE a.property = 'cloud_node accessin_node idc_node exchange_node';
update `naas_cxp`.NodeEO a set a.property = 'ACCESSIN,IDC' WHERE a.property = 'accessin_node idc_node ';
update `naas_cxp`.NodeEO a set a.property = 'CLOUD,ACCESSIN,IDC' WHERE a.property = 'cloud_node accessin_node idc_node ';

select distinct property from `naas_cxp`.NodeEO;

##数据验证：code唯一性
select count(*) from `naas_cxp`.NodeEO;/*290*/
select count(*) from (select distinct code from `naas_cxp`.NodeEO) a;/*280*/

##找出20条记录code不唯一
update `naas_cxp`.NodeEO a set a.code = 'CR_BJ_SJHL_1' WHERE a.uuid = '200';
update `naas_cxp`.NodeEO a set a.code = 'CR_BJ_SJHL_2' WHERE a.uuid = '206';
update `naas_cxp`.NodeEO a set a.code = 'DC_HK_CMI_1' WHERE a.uuid = '426';
update `naas_cxp`.NodeEO a set a.code = 'DC_HK_CMI_2' WHERE a.uuid = '166';
update `naas_cxp`.NodeEO a set a.code = 'DC_HK_PCCW_1' WHERE a.uuid = '432';
update `naas_cxp`.NodeEO a set a.code = 'DC_HK_PCCW_2' WHERE a.uuid = '168';
update `naas_cxp`.NodeEO a set a.code = 'DC_SH_MJ_1' WHERE a.uuid = '488';
update `naas_cxp`.NodeEO a set a.code = 'DC_SH_MJ_2' WHERE a.uuid = '310';
update `naas_cxp`.NodeEO a set a.code = 'DC_SZ_BGS_1' WHERE a.uuid = '506';
update `naas_cxp`.NodeEO a set a.code = 'DC_SZ_BGS_2' WHERE a.uuid = '516';
update `naas_cxp`.NodeEO a set a.code = 'DC_SZ_TC_1' WHERE a.uuid = '518';
update `naas_cxp`.NodeEO a set a.code = 'DC_SZ_TC_2' WHERE a.uuid = '304';
update `naas_cxp`.NodeEO a set a.code = 'DC_USA_SILI_1' WHERE a.uuid = '270';
update `naas_cxp`.NodeEO a set a.code = 'DC_USA_SILI_2' WHERE a.uuid = '284';
update `naas_cxp`.NodeEO a set a.code = 'DC_WH_YL_1' WHERE a.uuid = '538';
update `naas_cxp`.NodeEO a set a.code = 'DC_WH_YL_2' WHERE a.uuid = '504';
update `naas_cxp`.NodeEO a set a.code = 'DC-cd_cdzl_1' WHERE a.uuid = '23';
update `naas_cxp`.NodeEO a set a.code = 'DC-cd_cdzl_2' WHERE a.uuid = '47';
update `naas_cxp`.NodeEO a set a.code = 'LXJF-HZ_1' WHERE a.uuid = '63';
update `naas_cxp`.NodeEO a set a.code = 'LXJF-HZ_2' WHERE a.uuid = '104';

select * from `naas_cxp`.NodeEO where code in (select code from `naas_cxp`.NodeEO group by code having count(code) > 1);
