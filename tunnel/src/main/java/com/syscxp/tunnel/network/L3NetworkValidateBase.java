package com.syscxp.tunnel.network;

import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.configuration.ResourceMotifyRecordVO;
import com.syscxp.header.configuration.ResourceMotifyRecordVO_;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.header.tunnel.tunnel.InterfaceVO;
import com.syscxp.tunnel.tunnel.TunnelBase;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    public void validate(APICreateL3NetworkManualMsg msg){
        //判断同一个用户的云网络名称是否已经存在
        Q q1 = Q.New(L3NetworkVO.class)
                .eq(L3NetworkVO_.name, msg.getName())
                .eq(L3NetworkVO_.accountUuid, msg.getAccountUuid());
        if (q1.isExists()) {
            throw new ApiMessageInterceptionException(argerr("该用户云网络名称【%s】已经存在!", msg.getName()));
        }

        //验证vid
        if(Q.New(L3NetworkEO.class).eq(L3NetworkEO_.vid, msg.getVid()).isExists()){
            throw new ApiMessageInterceptionException(argerr("该vid已经被云网络使用！"));
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
        if(Q.New(L3EndpointVO.class).eq(L3EndpointVO_.l3NetworkUuid, msg.getUuid()).isExists()){
            throw new ApiMessageInterceptionException(argerr("请先删除该云网络下的连接点!"));
        }

    }

    public void validate(APICreateL3EndPointMsg msg){

        if(Q.New(L3EndpointVO.class)
                .eq(L3EndpointVO_.l3NetworkUuid, msg.getL3NetworkUuid())
                .eq(L3EndpointVO_.endpointUuid, msg.getEndpointUuid())
                .isExists()){
            throw new ApiMessageInterceptionException(argerr("该云网络已经添加过该连接点!"));
        }
    }

    public void validate(APICreateL3EndpointManualMsg msg){
        TunnelBase tunnelBase = new TunnelBase();
        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        if(Q.New(L3EndpointVO.class)
                .eq(L3EndpointVO_.l3NetworkUuid, msg.getL3NetworkUuid())
                .eq(L3EndpointVO_.endpointUuid, msg.getEndpointUuid())
                .isExists()){
            throw new ApiMessageInterceptionException(argerr("该云网络已经添加过该连接点!"));
        }

        InterfaceVO interfaceVO = dbf.findByUuid(msg.getInterfaceUuid(), InterfaceVO.class);
        String physicalSwitchUuid = tunnelBase.getPhysicalSwitchBySwitchPortUuid(interfaceVO.getSwitchPortUuid()).getUuid();
        List<Integer> allocatedVlans = l3NetworkBase.findAllocateVlanByPhysicalSwitch(physicalSwitchUuid);

        if (!allocatedVlans.isEmpty() && allocatedVlans.contains(msg.getVlan())) {
            throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", msg.getVlan()));
        }
    }

    public void validate(APIUpdateL3EndpointIPMsg msg){
        L3EndpointVO vo = dbf.findByUuid(msg.getUuid(), L3EndpointVO.class);

        if(!NetworkUtils.isIpv4Address(msg.getLocalIP())){
            throw new ApiMessageInterceptionException(argerr("该犀思云端IP不是合法的IPV4地址！"));
        }

        if(!NetworkUtils.isIpv4Address(msg.getRemoteIp())){
            throw new ApiMessageInterceptionException(argerr("该客户端IP不是合法的IPV4地址！"));
        }

        if(!NetworkUtils.isNetmask(msg.getNetmask())){
            throw new ApiMessageInterceptionException(argerr("该子网掩码不合法！"));
        }

        if(msg.getLocalIP().equals(msg.getRemoteIp())){
            throw new ApiMessageInterceptionException(argerr("客户端IP和犀思云端IP不能相同！"));
        }

        if(!NetworkUtils.isIpv4sInNetmask(msg.getLocalIP(),msg.getRemoteIp(),msg.getNetmask())){
            throw new ApiMessageInterceptionException(argerr("客户端IP和犀思云端IP必须属于同一网段！"));
        }

        //同一网络的所有连接点网段唯一
        String ipCidr = NetworkUtils.getIpCidrFromIpv4Netmask(msg.getLocalIP(),msg.getNetmask());
        if(Q.New(L3EndpointVO.class)
                .eq(L3EndpointVO_.l3NetworkUuid, vo.getL3NetworkUuid())
                .notEq(L3EndpointVO_.uuid, msg.getUuid())
                .eq(L3EndpointVO_.ipCidr, ipCidr)
                .isExists()){
            throw new ApiMessageInterceptionException(argerr("该网段已经被该云网络的其他连接点使用！"));
        }

        //验证网段的第一个IP和最后一个不可用
        String ipPart1 = msg.getLocalIP();
        String ipPart2 = NetworkUtils.intFromNetmask(msg.getNetmask());
        String ipPart = ipPart1 + "/" + ipPart2;

        String[] ipFirstAndEnd = NetworkUtils.ipSplit(ipPart);
        if(msg.getLocalIP().equals(ipFirstAndEnd[0]) || msg.getLocalIP().equals(ipFirstAndEnd[1])){
            throw new ApiMessageInterceptionException(argerr("该犀思云端IP不能是网段的起始IP或结束IP！"));
        }
        if(msg.getRemoteIp().equals(ipFirstAndEnd[0]) || msg.getRemoteIp().equals(ipFirstAndEnd[1])){
            throw new ApiMessageInterceptionException(argerr("该客户端IP不能是网段的起始IP或结束IP！"));
        }


        if(vo.getState() == L3EndpointState.Enabled){
            throw new ApiMessageInterceptionException(argerr("设置互联IP，请先断开连接！"));
        }

        if(vo.getState() == L3EndpointState.Deploying){
            throw new ApiMessageInterceptionException(argerr("该连接点有未完成任务，稍后再试！"));
        }
    }

    public void validate(APIUpdateL3EndpointBandwidthMsg msg){

        L3EndpointVO vo = dbf.findByUuid(msg.getUuid(), L3EndpointVO.class);
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

        if(vo.getState() == L3EndpointState.Deploying){
            throw new ApiMessageInterceptionException(argerr("该连接点有未完成任务，稍后再试！"));
        }

    }

    public void validate(APIDeleteL3EndPointMsg msg){

        L3EndpointVO vo = dbf.findByUuid(msg.getUuid(), L3EndpointVO.class);

        if(vo.getState() == L3EndpointState.Enabled){
            throw new ApiMessageInterceptionException(argerr("删除L3连接点，请先断开连接！"));
        }

        if(vo.getState() == L3EndpointState.Deploying){
            throw new ApiMessageInterceptionException(argerr("该连接点有未完成任务，稍后再试！"));
        }
    }

    public void validate(APICreateL3RouteMsg msg){
        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        if(!NetworkUtils.isCidr(msg.getCidr())){
            throw new ApiMessageInterceptionException(argerr("该目标网段不合法！"));
        }

        L3EndpointVO l3EndPointVO = dbf.findByUuid(msg.getL3EndPointUuid(), L3EndpointVO.class);

        List<String> l3EndpointUuids = Q.New(L3EndpointVO.class)
                .eq(L3EndpointVO_.l3NetworkUuid, l3EndPointVO.getL3NetworkUuid())
                .select(L3EndpointVO_.uuid)
                .listValues();

        String[] cidrArr = msg.getCidr().split("/");
        String truthCidr = NetworkUtils.getIpCidrFromIpv4Netmask(cidrArr[0],NetworkUtils.netmaskFromInt(cidrArr[1]));

        //目标网段不可重复添加
        if(Q.New(L3RouteVO.class)
                .in(L3RouteVO_.l3EndPointUuid, l3EndpointUuids)
                .eq(L3RouteVO_.truthCidr, truthCidr)
                .isExists()){
            throw new ApiMessageInterceptionException(
                    argerr("该目标网段在该云网络下已经存在."));
        }

        //判断路由条目是否已达上限

        Integer max = l3EndPointVO.getMaxRouteNum();

        Long count = Q.New(L3RouteVO.class)
                .eq(L3RouteVO_.l3EndPointUuid, msg.getL3EndPointUuid())
                .count();
        if(count >= max){
            throw new ApiMessageInterceptionException(
                    argerr("该连接点下路由条目已达上限."));
        }

        if(l3EndPointVO.getState() == L3EndpointState.Deploying){
            throw new ApiMessageInterceptionException(argerr("该连接点有未完成任务，稍后再试！"));
        }

        if(!l3NetworkBase.isControllerReady(l3EndPointVO)){
            throw new ApiMessageInterceptionException(argerr("设置路由，请先设置互联IP！"));
        }

    }

    public void validate(APIDeleteL3RouteMsg msg){
        L3RouteVO l3RouteVO = dbf.findByUuid(msg.getUuid(), L3RouteVO.class);
        L3EndpointVO l3EndPointVO = dbf.findByUuid(l3RouteVO.getL3EndPointUuid(), L3EndpointVO.class);

        if(l3EndPointVO.getState() == L3EndpointState.Deploying){
            throw new ApiMessageInterceptionException(argerr("该连接点有未完成任务，稍后再试！"));
        }
    }

    public void validate(APIEnableL3EndpointMsg msg){
        if(msg.getSession().getType() != AccountType.SystemAdmin && msg.isSaveOnly()){
            throw new ApiMessageInterceptionException(argerr("只有系统管理员才能执行仅保存操作！"));
        }

        L3EndpointVO vo = dbf.findByUuid(msg.getUuid(), L3EndpointVO.class);
        L3NetworkBase l3NetworkBase = new L3NetworkBase();
        if(!l3NetworkBase.isControllerReady(vo)){
            throw new ApiMessageInterceptionException(argerr("未设置互联IP！"));
        }

        if(vo.getState() == L3EndpointState.Deploying){
            throw new ApiMessageInterceptionException(argerr("该连接点有未完成任务，稍后再试！"));
        }
    }

    public void validate(APIDisableL3EndpointMsg msg){
        if(msg.getSession().getType() != AccountType.SystemAdmin && msg.isSaveOnly()){
            throw new ApiMessageInterceptionException(argerr("只有系统管理员才能执行仅保存操作！"));
        }

        L3EndpointVO vo = dbf.findByUuid(msg.getUuid(), L3EndpointVO.class);
        if(vo.getState() == L3EndpointState.Deploying){
            throw new ApiMessageInterceptionException(argerr("该连接点有未完成任务，稍后再试！"));
        }
    }
}
