package com.syscxp.tunnel.network;

import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.configuration.ResourceMotifyRecordVO;
import com.syscxp.header.configuration.ResourceMotifyRecordVO_;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.syscxp.core.Platform.argerr;

/**
 * Create by DCY on 2018/3/12
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class L3NetworkValidateBase {
    private static final CLogger logger = Utils.getLogger(L3NetworkValidateBase.class);

    @Autowired
    private DatabaseFacade dbf;

    public void validate(APICreateL3NetworkMsg msg){
        //判断同一个用户的云网络名称是否已经存在
        Q q1 = Q.New(L3NetworkVO.class)
                .eq(L3NetworkVO_.name, msg.getName())
                .eq(L3NetworkVO_.accountUuid, msg.getAccountUuid());
        if (q1.isExists()) {
            throw new ApiMessageInterceptionException(argerr("该用户云网络名称【%s】已经存在!", msg.getName()));
        }

    }

    public void validate(APIUpdateL3NetworkMsg msg){
        L3NetworkVO vo = dbf.findByUuid(msg.getUuid(), L3NetworkVO.class);
        //判断同一个用户的云网络名称是否已经存在
        if (msg.getName() != null) {
            SimpleQuery<L3NetworkVO> q = dbf.createQuery(L3NetworkVO.class);
            q.add(L3NetworkVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q.add(L3NetworkVO_.accountUuid, SimpleQuery.Op.EQ, vo.getAccountUuid());
            q.add(L3NetworkVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("该用户云网络名称【%s】已经存在!", msg.getName()));
            }
        }
    }

    public void validate(APIDeleteL3NetworkMsg msg){
        if(Q.New(L3EndPointVO.class).eq(L3EndPointVO_.l3NetworkUuid, msg.getUuid()).isExists()){
            throw new ApiMessageInterceptionException(argerr("请先删除该云网络下的连接点!"));
        }

    }

    public void validate(APICreateL3EndPointMsg msg){

        if(Q.New(L3EndPointVO.class)
                .eq(L3EndPointVO_.l3NetworkUuid, msg.getL3NetworkUuid())
                .eq(L3EndPointVO_.endpointUuid, msg.getEndpointUuid())
                .isExists()){
            throw new ApiMessageInterceptionException(argerr("该云网络已经添加过该连接点!"));
        }
    }

    public void validate(APIUpdateL3EndpointIPMsg msg){


    }

    public void validate(APIUpdateL3EndpointBandwidthMsg msg){

        L3EndPointVO vo = dbf.findByUuid(msg.getUuid(), L3EndPointVO.class);
        //调整次数当月是否达到上限
        LocalDateTime dateTime =
                LocalDate.now().withDayOfMonth(LocalDate.MIN.getDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(ResourceMotifyRecordVO.class).eq(ResourceMotifyRecordVO_.resourceUuid, vo.getL3NetworkUuid())
                .gte(ResourceMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime)).count();
        Integer maxModifies =
                Q.New(L3NetworkVO.class).eq(L3NetworkVO_.uuid, vo.getL3NetworkUuid()).select(L3NetworkVO_.maxModifies)
                        .findValue();

        if (times >= maxModifies) {
            throw new ApiMessageInterceptionException(
                    argerr("该云网络[uuid:%s] 已经调整带宽 %s 次.", msg.getUuid(), times));
        }
    }

    public void validate(APIDeleteL3EndPointMsg msg){

    }

    public void validate(APICreateL3RouteMsg msg){
        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        //目标网段不可重复添加
        if(Q.New(L3RouteVO.class)
                .eq(L3RouteVO_.l3EndPointUuid, msg.getL3EndPointUuid())
                .eq(L3RouteVO_.cidr, msg.getCidr())
                .isExists()){
            throw new ApiMessageInterceptionException(
                    argerr("该目标网段在该L3连接点下已经存在."));
        }

        //判断路由条目是否已达上限
        L3EndPointVO l3EndPointVO = dbf.findByUuid(msg.getL3EndPointUuid(), L3EndPointVO.class);
        Integer max = l3EndPointVO.getMaxRouteNum();

        Long count = Q.New(L3RouteVO.class)
                .eq(L3RouteVO_.l3EndPointUuid, msg.getL3EndPointUuid())
                .count();
        if(count >= max){
            throw new ApiMessageInterceptionException(
                    argerr("该连接点下路由条目已达上限."));
        }

        //设置路由必须先设置互联IP
        if(!l3NetworkBase.isControllerReady(l3EndPointVO)){
            throw new ApiMessageInterceptionException(
                    argerr("设置路由必须先设置互联IP."));
        }

    }

    public void validate(APIDeleteL3RouteMsg msg){

    }
}
