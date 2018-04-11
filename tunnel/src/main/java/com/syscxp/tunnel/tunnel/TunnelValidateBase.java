package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SQL;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.job.JobQueueEntryVO;
import com.syscxp.core.job.JobQueueEntryVO_;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.billing.APIGetHasNotifyMsg;
import com.syscxp.header.billing.APIGetHasNotifyReply;
import com.syscxp.header.billing.APIGetProductPriceMsg;
import com.syscxp.header.billing.APIGetProductPriceReply;
import com.syscxp.header.configuration.ResourceMotifyRecordVO;
import com.syscxp.header.configuration.ResourceMotifyRecordVO_;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.tunnel.edgeLine.EdgeLineVO;
import com.syscxp.header.tunnel.edgeLine.EdgeLineVO_;
import com.syscxp.header.tunnel.endpoint.EndpointType;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.network.L3EndpointState;
import com.syscxp.header.tunnel.network.L3EndpointVO;
import com.syscxp.header.tunnel.network.L3EndpointVO_;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.identity.TunnelGlobalConfig;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.StringUtils;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.syscxp.core.Platform.argerr;

/**
 * Create by DCY on 2017/12/1
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TunnelValidateBase {
    private static final CLogger logger = Utils.getLogger(TunnelValidateBase.class);

    @Autowired
    private DatabaseFacade dbf;

    public void validate(APIUpdateInterfacePortMsg msg) {
        TunnelBase tunnelBase = new TunnelBase();
        InterfaceVO iface = Q.New(InterfaceVO.class).eq(InterfaceVO_.uuid, msg.getUuid()).find();

        //过期接口不让改
        if (iface.getExpireDate() != null && iface.getExpireDate().before(Timestamp.valueOf(LocalDateTime.now())))
            throw new ApiMessageInterceptionException(
                    argerr("该物理接口[uuid:%s] 已经到期！", msg.getUuid()));

        //接口被两条专线或者3层网络使用，不让改
        Long tunnelNum = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid, iface.getUuid()).count();
        Long l3Num = Q.New(L3EndpointVO.class)
                .eq(L3EndpointVO_.interfaceUuid, iface.getUuid()).count();
        Long num = tunnelNum + l3Num;
        if (num > 1)
            throw new ApiMessageInterceptionException(
                    argerr("该物理接口[uuid:%s] 只能被一条专线或者一个3层连接点使用才能修改！", msg.getUuid()));

        //如果改的是端口
        if(!msg.getSwitchPortUuid().equals(iface.getSwitchPortUuid())){
            String oldPortType = Q.New(SwitchPortVO.class).eq(SwitchPortVO_.uuid, iface.getSwitchPortUuid()).select(SwitchPortVO_.portType).findValue();
            String portType = Q.New(SwitchPortVO.class).eq(SwitchPortVO_.uuid, msg.getSwitchPortUuid()).select(SwitchPortVO_.portType).findValue();
            if(!oldPortType.equals(portType)){
                throw new ApiMessageInterceptionException(
                        argerr("不支持修改为不同端口类型的端口！"));
            }
        }

        //如果改的是接口类型
        if(msg.getNetworkType() != iface.getType()){
            SwitchPortVO switchPort = Q.New(SwitchPortVO.class).eq(SwitchPortVO_.uuid, iface.getSwitchPortUuid()).find();
            if (switchPort.getPortType().equals("SHARE") || switchPort.getPortType().equals("EXTENDPORT")){
                if(msg.getNetworkType() == NetworkType.ACCESS){
                    throw new ApiMessageInterceptionException(
                            argerr("该物理接口[uuid:%s] 的端口是[%s] , 不能改为ACCESS模式！", msg.getUuid(), switchPort.getPortType()));
                }
            }
        }

        //验证接口所属的专线和L3

        //原物理交换机
        PhysicalSwitchVO oldPhysicalSwitch = tunnelBase.getPhysicalSwitchBySwitchPortUuid(iface.getSwitchPortUuid());
        //现物理交换机
        PhysicalSwitchVO physicalSwitch = tunnelBase.getPhysicalSwitchBySwitchPortUuid(msg.getSwitchPortUuid());

        TunnelSwitchPortVO tsPort = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid())
                .find();
        boolean isUsedByTunnel = tsPort != null;

        L3EndpointVO l3EndpointVO = Q.New(L3EndpointVO.class)
                .eq(L3EndpointVO_.interfaceUuid, msg.getUuid())
                .find();
        boolean isUsedByL3 = l3EndpointVO != null;

        if(isUsedByTunnel){
            TunnelVO vo = Q.New(TunnelVO.class)
                    .eq(TunnelVO_.uuid, tsPort.getTunnelUuid())
                    .find();

            //判断该专线是否还有未完成任务
            if(Q.New(JobQueueEntryVO.class).eq(JobQueueEntryVO_.resourceUuid, tsPort.getTunnelUuid()).eq(JobQueueEntryVO_.restartable, true).isExists()){
                throw new ApiMessageInterceptionException(argerr("该物理接口所属专线有未完成任务，请稍后再操作！"));
            }

            //判断该专线是否中止
            if(vo.getState() == TunnelState.Enabled){
                throw new ApiMessageInterceptionException(argerr("该接口已有运行的专线[uuid:%s]，请先断开连接！",vo.getUuid()));
            }

            //如果跨物理交换机，需先关闭监控
            if(!oldPhysicalSwitch.getUuid().equals(physicalSwitch.getUuid())){
                if(vo.getMonitorState() == TunnelMonitorState.Enabled){
                    throw new ApiMessageInterceptionException(argerr("所更换的端口跨了物理交换机，请先关闭该接口所属专线[uuid:%s]的监控！", vo.getUuid()));
                }
            }
        }

        if(isUsedByL3){
            if(!msg.getSwitchPortUuid().equals(iface.getSwitchPortUuid())){
                //判断L3连接点是否中止
                if(l3EndpointVO.getState() == L3EndpointState.Enabled){
                    throw new ApiMessageInterceptionException(argerr("该接口已有运行的L3连接点[uuid:%s]，请先断开连接！",l3EndpointVO.getUuid()));
                }

                if(l3EndpointVO.getState() == L3EndpointState.Deploying){
                    throw new ApiMessageInterceptionException(argerr("该接口所属的L3连接点[uuid:%s] 有未完成的任务！",l3EndpointVO.getUuid()));
                }
            }

            if(msg.getNetworkType() != iface.getType()){
                if(msg.getNetworkType() == NetworkType.ACCESS){
                    throw new ApiMessageInterceptionException(argerr("该接口所属的L3连接点[uuid:%s] 不能改为ACCESS模式！",l3EndpointVO.getUuid()));
                }
            }
        }

    }

    public void validate(APICreateInterfaceMsg msg) {
        //判断同一个用户的接口名称是否已经存在
        Q q1 = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.name, msg.getName())
                .eq(InterfaceVO_.accountUuid, msg.getAccountUuid());
        if (q1.isExists()) {
            throw new ApiMessageInterceptionException(argerr("物理接口名称【%s】已经存在!", msg.getName()));
        }

        if(!msg.getPortOfferingUuid().equals("SHARE") && !msg.getPortOfferingUuid().equals("EXTENDPORT")){
            //判断账户金额是否充足
            APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
            priceMsg.setAccountUuid(msg.getAccountUuid());
            priceMsg.setProductChargeModel(msg.getProductChargeModel());
            priceMsg.setDuration(msg.getDuration());
            priceMsg.setUnits(new TunnelBillingBase().getInterfacePriceUnit(msg.getPortOfferingUuid()));
            APIGetProductPriceReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
            if (!reply.isPayable())
                throw new ApiMessageInterceptionException(
                        argerr("账户[uuid:%s]余额不足!", msg.getAccountUuid()));
        }

    }

    public void validate(APICreateInterfaceManualMsg msg) {
        //判断同一个用户的接口名称是否已经存在
        Q q = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.name, msg.getName())
                .eq(InterfaceVO_.accountUuid, msg.getAccountUuid());

        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("物理接口名称【%s】已经存在!", msg.getName()));
        }

        String portType = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, msg.getSwitchPortUuid())
                .select(SwitchPortVO_.portType).find();
        //判断同一个用户在同一个连接点下是否已经购买共享端口
        if (portType.equals("SHARE") || portType.equals("EXTENDPORT")) {
            /*String sql = "select a from InterfaceVO a,SwitchPortVO b " +
                    "where a.switchPortUuid = b.uuid " +
                    "and a.accountUuid = :accountUuid " +
                    "and a.endpointUuid = :endpointUuid " +
                    "and b.portType = 'SHARE'";
            TypedQuery<InterfaceVO> itq = dbf.getEntityManager().createQuery(sql, InterfaceVO.class);
            itq.setParameter("accountUuid", msg.getAccountUuid());
            itq.setParameter("endpointUuid", msg.getEndpointUuid());
            if (!itq.getResultList().isEmpty()) {
                throw new ApiMessageInterceptionException(argerr("一个用户在同一个连接点下只能购买一个共享口！ "));
            }*/
        }else{
            //判断账户金额是否充足
            APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
            priceMsg.setAccountUuid(msg.getAccountUuid());
            priceMsg.setProductChargeModel(msg.getProductChargeModel());
            priceMsg.setDuration(msg.getDuration());
            priceMsg.setUnits(new TunnelBillingBase().getInterfacePriceUnit(portType));
            APIGetProductPriceReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
            if (!reply.isPayable())
                throw new ApiMessageInterceptionException(
                        argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
        }

    }

    public void validate(APIUpdateInterfaceMsg msg) {

        InterfaceVO iface = Q.New(InterfaceVO.class).eq(InterfaceVO_.uuid, msg.getUuid()).find();
        if (iface.getExpireDate() != null && iface.getExpireDate().before(Timestamp.valueOf(LocalDateTime.now())))
            throw new ApiMessageInterceptionException(
                    argerr("The Interface[uuid:%s] has expired！", msg.getUuid()));

        //判断同一个用户的物理接口名称是否已经存在
        if (!StringUtils.isEmpty(msg.getName()) && !msg.getName().equals(iface.getName())) {
            if (checkResourceName(InterfaceVO.class.getSimpleName(), msg.getName(), iface.getAccountUuid())) {
                throw new ApiMessageInterceptionException(argerr("物理接口名称【%s】已经存在!", msg.getName()));
            }
        }

    }

    public void validate(APISLAInterfaceMsg msg) {
        checkOrderNoPayForInterface(msg.getUuid());
    }

    public void validate(APIRenewInterfaceMsg msg) {
        checkOrderNoPayForInterface(msg.getUuid());
    }

    public void validate(APIRenewAutoInterfaceMsg msg) {
        checkOrderNoPayForInterface(msg.getUuid());
    }

    public void checkOrderNoPayForInterface(String productUuid) {
        String accountUuid = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.uuid, productUuid)
                .select(InterfaceVO_.accountUuid).findValue();
        checkOrderNoPay(accountUuid, productUuid);
    }

    public void validate(APIDeleteInterfaceMsg msg) {
        InterfaceVO iface = Q.New(InterfaceVO.class).eq(InterfaceVO_.uuid, msg.getUuid()).find();

        int deleteDays = TunnelGlobalConfig.PRODUCT_DELETE_DAYS.value(Integer.class);
        if (!msg.getSession().isAdminSession() && iface.getCreateDate().toLocalDateTime().plusDays(deleteDays).isAfter(LocalDateTime.now()))
            throw new ApiMessageInterceptionException(
                    argerr("物理接口[uuid:%s]购买未超过%s天,不能删除 !", msg.getUuid(), deleteDays));

        //判断云专线下是否有该物理接口
        Q q = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(
                    argerr("The interface[uuid:%s] is being used, cannot delete!", msg.getUuid()));
        }

        //判断物理接口有没有被最后一公里绑定
        if(Q.New(EdgeLineVO.class).eq(EdgeLineVO_.interfaceUuid,msg.getUuid()).isExists()){
            throw new ApiMessageInterceptionException(
                    argerr("该物理接口[uuid:%s] 被最后一公里绑定, 先删除最后一公里!", msg.getUuid()));
        }

        //判断物理接口有没有被L3使用
        if(Q.New(L3EndpointVO.class).eq(L3EndpointVO_.interfaceUuid, msg.getUuid()).isExists()){
            throw new ApiMessageInterceptionException(
                    argerr("该物理接口[uuid:%s] 被云网络绑定, 先删除云网络!", msg.getUuid()));
        }

        //判断该产品是否有未完成订单
        checkOrderNoPay(iface.getAccountUuid(), msg.getUuid());
    }

    public void validate(APICreateTunnelMsg msg) {
        //BOSS创建验证物理接口的账户是否一致
        if (msg.getSession().getType() == AccountType.SystemAdmin) {
            if (msg.getInterfaceAUuid() != null) {
                String accountUuid = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class).getAccountUuid();
                if (!Objects.equals(msg.getAccountUuid(), accountUuid)) {
                    throw new ApiMessageInterceptionException(argerr("物理接口A不属于该用户！"));
                }
            }
            if (msg.getInterfaceZUuid() != null) {
                String accountUuid = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class).getAccountUuid();
                if (!Objects.equals(msg.getAccountUuid(), accountUuid)) {
                    throw new ApiMessageInterceptionException(argerr("物理接口Z不属于该用户！"));
                }
            }
        }
        //判断同一个用户的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
        }

        //判断通道两端的连接点是否相同，不允许相同
        if (Objects.equals(msg.getEndpointAUuid(), msg.getEndpointZUuid())) {
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }
        //若果跨国，则互联连接点不能为空
        if (isTransnational(msg.getEndpointAUuid(), msg.getEndpointZUuid())) {
            if (msg.getInnerConnectedEndpointUuid() == null) {
                throw new ApiMessageInterceptionException(argerr("该通道是跨国通道，互联连接点不能为空！ "));
            }
        }
        //如果跨国,验证互联连接点和内外联交换机配置
        if (msg.getInnerConnectedEndpointUuid() != null) {
            EndpointVO endpointVO = dbf.findByUuid(msg.getInnerConnectedEndpointUuid(), EndpointVO.class);
            if(endpointVO.getEndpointType() == EndpointType.INTERCONNECTED){
                validateInnerConnectEndpoint(msg.getInnerConnectedEndpointUuid());
            }
        }
        //判断账户金额是否充足
        EndpointVO evoA = dbf.findByUuid(msg.getEndpointAUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(msg.getEndpointZUuid(), EndpointVO.class);

        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(new TunnelBillingBase().getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), msg.getInterfaceAUuid(), msg.getInterfaceZUuid(), evoA, evoZ, msg.getInnerConnectedEndpointUuid()));
        APIGetProductPriceReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));

    }

    public void validateInnerConnectEndpoint(String innerConnectedEndpointUuid) {
        TunnelStrategy ts = new TunnelStrategy();
        //通过互联连接点找到内联交换机和内联端口
        SwitchVO innerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.INNER)
                .find();
        if (innerSwitch == null) {
            throw new ApiMessageInterceptionException(argerr("该互联连接点下未添加内联逻辑交换机 "));
        }
        SwitchPortVO innerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, innerSwitch.getUuid())
                .find();
        if (innerSwitchPort == null) {
            throw new ApiMessageInterceptionException(argerr("该内联逻辑交换机下未添加端口 "));
        }
        //通过互联连接点找到外联交换机和外联端口
        SwitchVO outerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.OUTER)
                .find();
        if (outerSwitch == null) {
            throw new ApiMessageInterceptionException(argerr("该互联连接点下未添加外联逻辑交换机 "));
        }
        SwitchPortVO outerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, outerSwitch.getUuid())
                .find();
        if (outerSwitchPort == null) {
            throw new ApiMessageInterceptionException(argerr("该外联逻辑交换机下未添加端口 "));
        }

    }

    public void validate(APICreateTunnelManualMsg msg) {
        TunnelBase tunnelBase = new TunnelBase();

        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);
        SwitchPortVO switchPortVOA = dbf.findByUuid(interfaceVOA.getSwitchPortUuid(),SwitchPortVO.class);
        SwitchPortVO switchPortVOZ = dbf.findByUuid(interfaceVOZ.getSwitchPortUuid(),SwitchPortVO.class);

        //验证VSI
        if(!msg.isCrossA() && !msg.isCrossZ()){
            if(Q.New(TunnelVO.class).eq(TunnelVO_.vsi, msg.getVsi()).isExists()){
                throw new ApiMessageInterceptionException(argerr("该vsi已经被专线使用！"));
            }
        }

        //如果是同一个物理交换机的接入和接出，VLAN必须一样
        if(tunnelBase.isSamePhysicalSwitchForTunnel(switchPortVOA,switchPortVOZ)){
            if(!msg.getaVlan().equals(msg.getzVlan())){
                throw new ApiMessageInterceptionException(argerr("该专线是从同一个物理交换机进出，两端VLAN要一样！"));
            }
        }

        //接口只有是Trunk时才能开启QINQ
        if(interfaceVOA.getType() != NetworkType.TRUNK && msg.isQinqA()){
            throw new ApiMessageInterceptionException(argerr("接口A不是Trunk类型,不能开启QINQ！"));
        }
        if(interfaceVOZ.getType() != NetworkType.TRUNK && msg.isQinqZ()){
            throw new ApiMessageInterceptionException(argerr("接口Z不是Trunk类型,不能开启QINQ！"));
        }

        //BOSS创建验证物理接口的账户是否一致
        if (!Objects.equals(msg.getAccountUuid(), interfaceVOA.getAccountUuid())) {
            throw new ApiMessageInterceptionException(argerr("物理接口A不属于该用户！"));
        }

        if (!Objects.equals(msg.getAccountUuid(), interfaceVOZ.getAccountUuid())) {
            throw new ApiMessageInterceptionException(argerr("物理接口Z不属于该用户！"));
        }

        EndpointVO evoA = dbf.findByUuid(interfaceVOA.getEndpointUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(interfaceVOZ.getEndpointUuid(), EndpointVO.class);

        //判断同一个用户的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
        }

        //判断通道两端的连接点是否相同，不允许相同
        if (Objects.equals(evoA.getUuid(), evoZ.getUuid())) {
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }

        //若果跨国，则互联连接点不能为空
        if (isTransnational(interfaceVOA.getEndpointUuid(), interfaceVOZ.getEndpointUuid())) {
            if (msg.getInnerConnectedEndpointUuid() == null) {
                throw new ApiMessageInterceptionException(argerr("该通道是跨国通道，互联连接点不能为空！ "));
            }
        }

        //如果跨国,验证:互联连接点-内外联交换机配置-VLAN
        String switchUuidA = tunnelBase.findSwitchByInterface(msg.getInterfaceAUuid());
        String switchUuidZ = tunnelBase.findSwitchByInterface(msg.getInterfaceZUuid());
        if (msg.getInnerConnectedEndpointUuid() != null) {
            EndpointVO endpointVO = dbf.findByUuid(msg.getInnerConnectedEndpointUuid(), EndpointVO.class);
            if(endpointVO.getEndpointType() == EndpointType.INTERCONNECTED){
                validateInnerConnectEndpoint(msg.getInnerConnectedEndpointUuid());

                //通过互联连接点找到内联交换机和内联端口
                SwitchVO innerSwitch = Q.New(SwitchVO.class)
                        .eq(SwitchVO_.endpointUuid, msg.getInnerConnectedEndpointUuid())
                        .eq(SwitchVO_.type, SwitchType.INNER)
                        .find();
                //通过互联连接点找到外联交换机和外联端口
                SwitchVO outerSwitch = Q.New(SwitchVO.class)
                        .eq(SwitchVO_.endpointUuid, msg.getInnerConnectedEndpointUuid())
                        .eq(SwitchVO_.type, SwitchType.OUTER)
                        .find();

                NodeVO nvoA = dbf.findByUuid(evoA.getNodeUuid(), NodeVO.class);
                if(nvoA.getCountry().equals("CHINA")){
                    if(!msg.isCrossA()){
                        validateVlan(msg.getInterfaceAUuid(), innerSwitch.getUuid(), msg.getaVlan(), false);
                    }
                    if(!msg.isCrossZ()){
                        validateVlan(msg.getInterfaceZUuid(), outerSwitch.getUuid(), msg.getzVlan(), false);
                    }

                }else{
                    if(!msg.isCrossA()){
                        validateVlan(msg.getInterfaceAUuid(), outerSwitch.getUuid(), msg.getaVlan(), false);
                    }
                    if(!msg.isCrossZ()){
                        validateVlan(msg.getInterfaceZUuid(), innerSwitch.getUuid(), msg.getzVlan(), false);
                    }
                }

            }else{
                if(!msg.isCrossA()){
                    if(msg.isCrossZ()){
                        validateVlan(msg.getInterfaceAUuid(), switchUuidZ, msg.getaVlan(), true);
                    }else{
                        validateVlan(msg.getInterfaceAUuid(), switchUuidZ, msg.getaVlan(), false);
                    }

                }
                if(!msg.isCrossZ()){
                    if(msg.isCrossA()){
                        validateVlan(msg.getInterfaceZUuid(), switchUuidA, msg.getzVlan(), true);
                    }else{
                        validateVlan(msg.getInterfaceZUuid(), switchUuidA, msg.getzVlan(), false);
                    }

                }
            }
        }else{
            if(!msg.isCrossA()){
                if(msg.isCrossZ()){
                    validateVlan(msg.getInterfaceAUuid(), switchUuidZ, msg.getaVlan(), true);
                }else{
                    validateVlan(msg.getInterfaceAUuid(), switchUuidZ, msg.getaVlan(), false);
                }

            }
            if(!msg.isCrossZ()){
                if(msg.isCrossA()){
                    validateVlan(msg.getInterfaceZUuid(), switchUuidA, msg.getzVlan(), true);
                }else{
                    validateVlan(msg.getInterfaceZUuid(), switchUuidA, msg.getzVlan(), false);
                }

            }
        }

        //如果是ACCESS物理接口，判断该物理接口是否已经开通通道
        if(!msg.isCrossA()){
            if (interfaceVOA.getType() == NetworkType.ACCESS) {
                boolean exists = Q.New(TunnelSwitchPortVO.class)
                        .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getInterfaceAUuid())
                        .isExists();
                if (exists) {
                    throw new ApiMessageInterceptionException(argerr("该物理接口A是ACCESS口，不可复用"));
                }
            }
        }

        if(!msg.isCrossZ()){
            if (interfaceVOZ.getType() == NetworkType.ACCESS) {
                boolean exists = Q.New(TunnelSwitchPortVO.class)
                        .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getInterfaceZUuid())
                        .isExists();
                if (exists) {
                    throw new ApiMessageInterceptionException(argerr("该物理接口Z是ACCESS口，不可复用"));
                }
            }
        }


        //判断同一个switchPort下内部VLAN段是否有重叠
        validateInnerVlan(msg.isQinqA(), interfaceVOA.getSwitchPortUuid(), msg.isQinqZ(), interfaceVOZ.getSwitchPortUuid(), msg.getVlanSegment());

        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(new TunnelBillingBase().getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), msg.getInterfaceAUuid(), msg.getInterfaceZUuid(), evoA, evoZ, msg.getInnerConnectedEndpointUuid()));
        APIGetProductPriceReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
    }

    public void validateInnerVlan(boolean isQinqA, String switchPortUuidA, boolean isQinqZ, String switchPortUuidZ, List<InnerVlanSegment> vlanSegments){

        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select b.tunnelUuid from TunnelSwitchPortVO b where b.switchPortUuid = :switchPortUuid and b.type = 'QINQ') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        if (vlanSegments != null) {
            if (isQinqA) {
                for (InnerVlanSegment vlanSegment : vlanSegments) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("switchPortUuid", switchPortUuidA);
                    vq.setParameter("startVlan", vlanSegment.getStartVlan());
                    vq.setParameter("endVlan", vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("A接口的内部VLAN段在A端口有冲突"));
                    }
                }
            }
            if (isQinqZ) {
                for (InnerVlanSegment vlanSegment : vlanSegments) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("switchPortUuid", switchPortUuidZ);
                    vq.setParameter("startVlan", vlanSegment.getStartVlan());
                    vq.setParameter("endVlan", vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("Z接口的内部VLAN段在Z端口有冲突"));
                    }
                }
            }
        }
    }

    public void validateInnerVlan(boolean isQinq, String switchPortUuid, List<QinqVO> qinqVOs){

        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select b.tunnelUuid from TunnelSwitchPortVO b where b.switchPortUuid = :switchPortUuid and b.type = 'QINQ') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        if (qinqVOs != null) {
            if (isQinq) {
                for (QinqVO qinqVO : qinqVOs) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("switchPortUuid", switchPortUuid);
                    vq.setParameter("startVlan", qinqVO.getStartVlan());
                    vq.setParameter("endVlan", qinqVO.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("要切换端口的内部VLAN有冲突，不能切换。"));
                    }
                }
            }
        }
    }

    public void validate(APIUpdateTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        //判断同一个用户tunnel名称是否已经存在
        if (msg.getName() != null) {
            SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
            q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, vo.getAccountUuid());
            q.add(TunnelVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
            }
        }

        //判断是否为外采资源
        if(msg.getSession().getType() != AccountType.SystemAdmin){
            if(Q.New(OutsideResourceVO.class).eq(OutsideResourceVO_.resourceUuid, msg.getUuid()).isExists()){
                throw new ApiMessageInterceptionException(argerr("外采资源用户不可操作"));
            }
        }

    }

    public void validate(APIUpdateTunnelBandwidthMsg msg) {

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        //判断是否为外采资源
        if(msg.getSession().getType() != AccountType.SystemAdmin){
            if(Q.New(OutsideResourceVO.class).eq(OutsideResourceVO_.resourceUuid, msg.getUuid()).isExists()){
                throw new ApiMessageInterceptionException(argerr("外采资源用户不可操作"));
            }
        }
        //判断该产品是否有未完成订单
        checkOrderNoPay(vo.getOwnerAccountUuid(), msg.getUuid());

        //调整次数当月是否达到上限
        LocalDateTime dateTime =
                LocalDate.now().withDayOfMonth(LocalDate.MIN.getDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(ResourceMotifyRecordVO.class).eq(ResourceMotifyRecordVO_.resourceUuid, msg.getUuid())
                .gte(ResourceMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime)).count();
        Integer maxModifies =
                Q.New(TunnelVO.class).eq(TunnelVO_.uuid, msg.getUuid()).select(TunnelVO_.maxModifies)
                        .findValue();

        if (times >= maxModifies) {
            throw new ApiMessageInterceptionException(
                    argerr("The Tunnel[uuid:%s] has motified %s times.", msg.getUuid(), times));
        }
    }

    public void validate(APIRenewTunnelMsg msg) {
        String accountUuid = Q.New(TunnelVO.class)
                .eq(TunnelVO_.uuid, msg.getUuid())
                .select(TunnelVO_.accountUuid)
                .findValue();
        checkOrderNoPay(accountUuid, msg.getUuid());
    }

    public void validate(APIRenewAutoTunnelMsg msg) {
        String accountUuid = Q.New(TunnelVO.class)
                .eq(TunnelVO_.uuid, msg.getUuid())
                .select(TunnelVO_.accountUuid)
                .findValue();
        checkOrderNoPay(accountUuid, msg.getUuid());
    }

    public void validate(APISLATunnelMsg msg) {
        String accountUuid = Q.New(TunnelVO.class)
                .eq(TunnelVO_.uuid, msg.getUuid())
                .select(TunnelVO_.accountUuid)
                .findValue();
        checkOrderNoPay(accountUuid, msg.getUuid());
    }

    public void validate(APIDeleteTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        //判断是否为外采资源
        if(msg.getSession().getType() != AccountType.SystemAdmin){
            if(Q.New(OutsideResourceVO.class).eq(OutsideResourceVO_.resourceUuid, msg.getUuid()).isExists()){
                throw new ApiMessageInterceptionException(argerr("外采资源用户不可操作"));
            }
        }

        if (msg.getSession().getType() != AccountType.SystemAdmin && (vo.getState() == TunnelState.Enabled || vo.getState() == TunnelState.Disabled)) {
            int deleteDays = TunnelGlobalConfig.PRODUCT_DELETE_DAYS.value(Integer.class);
            if (vo.getCreateDate().toLocalDateTime().plusDays(deleteDays).isAfter(LocalDateTime.now()))
                throw new ApiMessageInterceptionException(
                        argerr("云专线[uuid:%s]购买未超过%s天,不能删除 !", msg.getUuid(), deleteDays));
        }
        checkOrderNoPay(vo.getOwnerAccountUuid(), msg.getUuid());
    }

    public void validate(APIDeleteForciblyTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        checkOrderNoPay(vo.getOwnerAccountUuid(), msg.getUuid());
    }

    public void validate(APIOpenTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        if(vo.getState() == TunnelState.Enabled){
            throw new ApiMessageInterceptionException(argerr("该专线已经开通，不可重复操作！"));
        }
    }

    public void validate(APIUnsupportTunnelMsg msg) {
    }

    public void validate(APIEnableTunnelMsg msg){
        //判断是否为外采资源
        if(msg.getSession().getType() != AccountType.SystemAdmin){
            if(Q.New(OutsideResourceVO.class).eq(OutsideResourceVO_.resourceUuid, msg.getUuid()).isExists()){
                throw new ApiMessageInterceptionException(argerr("外采资源用户不可操作"));
            }
        }

        if(msg.getSession().getType() != AccountType.SystemAdmin && msg.isSaveOnly()){
            throw new ApiMessageInterceptionException(argerr("只有系统管理员才能执行仅保存操作！"));
        }
    }

    public void validate(APIDisableTunnelMsg msg){
        //判断是否为外采资源
        if(msg.getSession().getType() != AccountType.SystemAdmin){
            if(Q.New(OutsideResourceVO.class).eq(OutsideResourceVO_.resourceUuid, msg.getUuid()).isExists()){
                throw new ApiMessageInterceptionException(argerr("外采资源用户不可操作"));
            }
        }

        if(msg.getSession().getType() != AccountType.SystemAdmin && msg.isSaveOnly()){
            throw new ApiMessageInterceptionException(argerr("只有系统管理员才能执行仅保存操作！"));
        }
    }

    public void validate(APIUpdateQinqMsg msg){
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        //判断该专线是否中止
        if(vo.getState() == TunnelState.Enabled){
            throw new ApiMessageInterceptionException(argerr("设置QINQ，请先断开连接！"));
        }

        //判断该专线是否还有未完成任务
        if(Q.New(JobQueueEntryVO.class).eq(JobQueueEntryVO_.resourceUuid, msg.getUuid()).eq(JobQueueEntryVO_.restartable, true).isExists()){
            throw new ApiMessageInterceptionException(argerr("该专线有未完成任务，请稍后再操作！"));
        }

        //只有Trunk的物理接口才能设置QINQ
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceUuidA(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceUuidZ(), InterfaceVO.class);

        if(interfaceVOA.getType() != NetworkType.TRUNK && msg.isQinqA()){
            throw new ApiMessageInterceptionException(argerr("接口A不是Trunk类型,不能设置QINQ！"));
        }
        if(interfaceVOZ.getType() != NetworkType.TRUNK && msg.isQinqZ()){
            throw new ApiMessageInterceptionException(argerr("接口Z不是Trunk类型,不能设置QINQ！"));
        }

        //检查内部VLAN段
        if(msg.isQinqA() || msg.isQinqZ()){
            if(msg.getVlanSegment() == null || msg.getVlanSegment().isEmpty()){
                throw new ApiMessageInterceptionException(argerr("设置QINQ专线,内部VLAN段不能为空！"));
            }
        }

        //验证输入的内部VLAN是否冲突
        if(msg.getVlanSegment() != null && !msg.getVlanSegment().isEmpty()){
            List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
            Set<Integer> s = new HashSet<>();
            for(InnerVlanSegment innerVlanSegment : vlanSegments){
                if(innerVlanSegment.getEndVlan() < innerVlanSegment.getStartVlan()){
                    throw new ApiMessageInterceptionException(argerr("所填的内部起始VLAN要小于等于结束VLAN！"));
                }else{
                    if(Objects.equals(innerVlanSegment.getStartVlan(), innerVlanSegment.getEndVlan())){
                        boolean success = s.add(innerVlanSegment.getStartVlan());
                        if(!success){
                            throw new ApiMessageInterceptionException(argerr("所填的内部VLAN有冲突！"));
                        }
                    }else{
                        for(int i = innerVlanSegment.getStartVlan(); i <= innerVlanSegment.getEndVlan(); i++){
                            boolean success = s.add(i);
                            if(!success){
                                throw new ApiMessageInterceptionException(argerr("所填的内部VLAN有冲突！"));
                            }
                        }
                    }
                }
            }

        }

    }


    public void validate(APIGetVlanAutoMsg msg){
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceUuidA(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceUuidZ(), InterfaceVO.class);
        //若果跨国，则互联连接点不能为空
        if (isTransnational(interfaceVOA.getEndpointUuid(), interfaceVOZ.getEndpointUuid())) {
            if (msg.getInnerConnectedEndpointUuid() == null) {
                throw new ApiMessageInterceptionException(argerr("该通道是跨国通道，互联连接点不能为空！ "));
            }
        }
    }

    public void validate(APIUpdateTunnelVlanMsg msg) {
        TunnelBase tunnelBase = new TunnelBase();

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        //判断该专线是否中止
        if(vo.getState() == TunnelState.Enabled){
            throw new ApiMessageInterceptionException(argerr("修改专线配置，请先断开连接！"));
        }

        //判断该专线是否还有未完成任务
        if(Q.New(JobQueueEntryVO.class).eq(JobQueueEntryVO_.resourceUuid, msg.getUuid()).eq(JobQueueEntryVO_.restartable, true).isExists()){
            throw new ApiMessageInterceptionException(argerr("该专线有未完成任务，请稍后再操作！"));
        }

        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(),InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(),InterfaceVO.class);
        InterfaceVO oldInterfaceVOA = dbf.findByUuid(msg.getOldInterfaceAUuid(),InterfaceVO.class);
        InterfaceVO oldInterfaceVOZ = dbf.findByUuid(msg.getOldInterfaceZUuid(),InterfaceVO.class);

        //验证共点和VLAN
        if (!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
            if (isCross(msg.getUuid(), msg.getOldInterfaceAUuid())) {
                throw new ApiMessageInterceptionException(argerr("该接口A为共点，不能修改配置！！"));
            }

            if(vo.getType() == TunnelType.CHINA2ABROAD){
                String switchPortUuidB = Q.New(TunnelSwitchPortVO.class)
                        .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                        .eq(TunnelSwitchPortVO_.sortTag, "B")
                        .select(TunnelSwitchPortVO_.switchPortUuid)
                        .findValue();
                String switchUuidB = dbf.findByUuid(switchPortUuidB, SwitchPortVO.class).getSwitchUuid();

                validateVlanForUpdateVlanOrInterface(tunnelBase.findSwitchByInterface(msg.getInterfaceAUuid()),
                        switchUuidB,
                        tunnelBase.findSwitchByInterface(msg.getOldInterfaceAUuid()),
                        msg.getaVlan(),
                        msg.getOldAVlan());
            }else{
                validateVlanForUpdateVlanOrInterface(tunnelBase.findSwitchByInterface(msg.getInterfaceAUuid()),
                        tunnelBase.findSwitchByInterface(msg.getInterfaceZUuid()),
                        tunnelBase.findSwitchByInterface(msg.getOldInterfaceAUuid()),
                        msg.getaVlan(),
                        msg.getOldAVlan());
            }

        }

        if (!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
            if (isCross(msg.getUuid(), msg.getOldInterfaceZUuid())) {
                throw new ApiMessageInterceptionException(argerr("该接口Z为共点，不能修改配置！！"));
            }

            if(vo.getType() == TunnelType.CHINA2ABROAD){
                String switchPortUuidC = Q.New(TunnelSwitchPortVO.class)
                        .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                        .eq(TunnelSwitchPortVO_.sortTag, "C")
                        .select(TunnelSwitchPortVO_.switchPortUuid)
                        .findValue();
                String switchUuidC = dbf.findByUuid(switchPortUuidC, SwitchPortVO.class).getSwitchUuid();

                validateVlanForUpdateVlanOrInterface(tunnelBase.findSwitchByInterface(msg.getInterfaceZUuid()),
                        switchUuidC,
                        tunnelBase.findSwitchByInterface(msg.getOldInterfaceZUuid()),
                        msg.getzVlan(),
                        msg.getOldZVlan());
            }else{
                validateVlanForUpdateVlanOrInterface(tunnelBase.findSwitchByInterface(msg.getInterfaceZUuid()),
                        tunnelBase.findSwitchByInterface(msg.getInterfaceAUuid()),
                        tunnelBase.findSwitchByInterface(msg.getOldInterfaceZUuid()),
                        msg.getzVlan(),
                        msg.getOldZVlan());
            }

        }

        //验证ACCESS接口
        if(!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid())){
            if(interfaceVOA.getType() == oldInterfaceVOA.getType()){
                if((interfaceVOA.getType() == NetworkType.ACCESS)
                        && Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.interfaceUuid,interfaceVOA.getUuid()).isExists()){
                    throw new ApiMessageInterceptionException(argerr("ACCESS的物理接口已经开通了专线，不能切换成该物理接口！！"));
                }
            }else{
                throw new ApiMessageInterceptionException(argerr("切换物理接口，不能切换接口模式！！"));
            }
        }

        if(!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid())){
            if(interfaceVOZ.getType() == oldInterfaceVOZ.getType()){
                if((interfaceVOZ.getType() == NetworkType.ACCESS)
                        && Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.interfaceUuid,interfaceVOZ.getUuid()).isExists()){
                    throw new ApiMessageInterceptionException(argerr("ACCESS的物理接口已经开通了专线，不能切换成该物理接口！！"));
                }
            }else{
                throw new ApiMessageInterceptionException(argerr("切换物理接口，不能切换接口模式！！"));
            }
        }

        //验证QINQ接口
        List<QinqVO> qinqVOs = Q.New(QinqVO.class)
                .eq(QinqVO_.tunnelUuid,vo.getUuid())
                .list();
        if(!interfaceVOA.getSwitchPortUuid().equals(oldInterfaceVOA.getSwitchPortUuid())){
            TunnelSwitchPortVO tunnelSwitchPortVOA = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag, "A")
                    .find();
            if(tunnelSwitchPortVOA.getType() == NetworkType.QINQ){
                validateInnerVlan(true, interfaceVOA.getSwitchPortUuid(), qinqVOs);
            }
        }

        if(!interfaceVOZ.getSwitchPortUuid().equals(oldInterfaceVOZ.getSwitchPortUuid())){
            TunnelSwitchPortVO tunnelSwitchPortVOZ = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag, "Z")
                    .find();
            if(tunnelSwitchPortVOZ.getType() == NetworkType.QINQ){
                validateInnerVlan(true, interfaceVOZ.getSwitchPortUuid(), qinqVOs);
            }
        }

    }

    /**
     * 判断物理接口是否是共点
     */

    public boolean isCross(String tunnelUuid, String interfaceUuid) {
        TunnelVO vo = dbf.findByUuid(tunnelUuid, TunnelVO.class);
        Integer vsi = vo.getVsi();

        String sql = "select b from TunnelVO a, TunnelSwitchPortVO b " +
                "where a.uuid = b.tunnelUuid " +
                "and a.vsi = :vsi " +
                "and b.interfaceUuid = :interfaceUuid";
        TypedQuery<TunnelSwitchPortVO> vq = dbf.getEntityManager().createQuery(sql, TunnelSwitchPortVO.class);
        vq.setParameter("vsi", vsi);
        vq.setParameter("interfaceUuid", interfaceUuid);
        if (vq.getResultList().size() == 1) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * 验证通道是否跨国
     */
    private boolean isTransnational(String endpointUuidA, String endpointUuidZ) {
        EndpointVO endpointVOA = dbf.findByUuid(endpointUuidA, EndpointVO.class);
        EndpointVO endpointVOZ = dbf.findByUuid(endpointUuidZ, EndpointVO.class);

        NodeVO nodeVOA = dbf.findByUuid(endpointVOA.getNodeUuid(), NodeVO.class);
        NodeVO nodeVOZ = dbf.findByUuid(endpointVOZ.getNodeUuid(), NodeVO.class);

        boolean isTransnational = false;
        if (nodeVOA.getCountry().equals("CHINA") && !nodeVOZ.getCountry().equals("CHINA")) {
            isTransnational = true;
        }
        if (!nodeVOA.getCountry().equals("CHINA") && nodeVOZ.getCountry().equals("CHINA")) {
            isTransnational = true;
        }
        return isTransnational;
    }

    /**
     * 判断外部VLAN是否可用
     */
    private void validateVlan(String interfaceUuid, String peerSwitchUuid, Integer vlan, boolean isPeerCross) {
        TunnelStrategy ts = new TunnelStrategy();
        TunnelBase tunnelBase = new TunnelBase();

        List<Integer> allocatedVlans;
        //查询该TUNNEL的物理接口所属的虚拟交换机
        String switchUuid = tunnelBase.findSwitchByInterface(interfaceUuid);

        //查询该虚拟交换机所属的物理交换机已经分配的Vlan
        if(isPeerCross){
            allocatedVlans = ts.fingAllocateVlanBySwitchForCross(switchUuid);
        }else{
            allocatedVlans = ts.fingAllocateVlanBySwitch(switchUuid, peerSwitchUuid);
        }

        //判断外部vlan是否可用
        if (!allocatedVlans.isEmpty() && allocatedVlans.contains(vlan)) {
            throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", vlan));
        }
    }

    /**
     * 修改物理接口或VLAN时判断外部VLAN是否可用
     */
    public void validateVlanForUpdateVlanOrInterface(String switchUuid, String peerSwitchUuid, String oldSwitchUuid, Integer vlan, Integer oldVlan){
        TunnelStrategy ts = new TunnelStrategy();

        String physicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid,switchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();
        String oldPhysicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid,oldSwitchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();

        //查询该虚拟交换机所属物理交换机下Tunnel已经分配的Vlan
        List<Integer> allocatedVlans = ts.fingAllocateVlanBySwitch(switchUuid, peerSwitchUuid);

        if(notNeedValidateVlanForUpdateInterface(physicalSwitchUuid,oldPhysicalSwitchUuid)){
            if(!oldVlan.equals(vlan)){
                if (!allocatedVlans.isEmpty() && allocatedVlans.contains(vlan)) {
                    throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", vlan));
                }
            }
        }else{
            if (!allocatedVlans.isEmpty() && allocatedVlans.contains(vlan)) {
                throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", vlan));
            }
        }
    }

    public boolean notNeedValidateVlanForUpdateInterface(String physicalSwitchUuid,String oldPhysicalSwitchUuid){
        if(physicalSwitchUuid.equals(oldPhysicalSwitchUuid)){
            return true;
        }else{
            PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(physicalSwitchUuid,PhysicalSwitchVO.class);
            PhysicalSwitchVO oldPhysicalSwitchVO = dbf.findByUuid(oldPhysicalSwitchUuid,PhysicalSwitchVO.class);
            if(physicalSwitchVO.getType() == PhysicalSwitchType.MPLS && oldPhysicalSwitchVO.getType() == PhysicalSwitchType.MPLS){
                return false;
            }else if(physicalSwitchVO.getType() == PhysicalSwitchType.SDN && oldPhysicalSwitchVO.getType() == PhysicalSwitchType.SDN){
                String mplsPhysicalSwitchUuid = Q.New(PhysicalSwitchUpLinkRefVO.class)
                        .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchUuid)
                        .select(PhysicalSwitchUpLinkRefVO_.uplinkPhysicalSwitchUuid)
                        .findValue();
                String oldMplsPhysicalSwitchUuid = Q.New(PhysicalSwitchUpLinkRefVO.class)
                        .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,oldPhysicalSwitchUuid)
                        .select(PhysicalSwitchUpLinkRefVO_.uplinkPhysicalSwitchUuid)
                        .findValue();
                if(mplsPhysicalSwitchUuid.equals(oldMplsPhysicalSwitchUuid)){
                    return true;
                }else{
                    return false;
                }
            }else{
                if(physicalSwitchVO.getType() == PhysicalSwitchType.SDN){
                    String mplsPhysicalSwitchUuid = Q.New(PhysicalSwitchUpLinkRefVO.class)
                            .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchUuid)
                            .select(PhysicalSwitchUpLinkRefVO_.uplinkPhysicalSwitchUuid)
                            .findValue();
                    if(mplsPhysicalSwitchUuid.equals(oldPhysicalSwitchUuid)){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    String oldMplsPhysicalSwitchUuid = Q.New(PhysicalSwitchUpLinkRefVO.class)
                            .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,oldPhysicalSwitchUuid)
                            .select(PhysicalSwitchUpLinkRefVO_.uplinkPhysicalSwitchUuid)
                            .findValue();
                    if(oldMplsPhysicalSwitchUuid.equals(physicalSwitchUuid)){
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        }
    }

    /**
     * 判断同一用户下产品名称是否重复
     */
    private boolean checkResourceName(String resourceType, String name, String accountUuid) {
        String uuid = SQL.New(String.format("SELECT r.uuid FROM %s r WHERE r.name = :name AND r.accountUuid = :accountUuid", resourceType), String.class)
                .param("accountUuid", accountUuid)
                .param("name", name).find();
        return uuid != null;
    }

    /**
     * 判断该产品是否有未完成订单
     */
    public void checkOrderNoPay(String accountUuid, String productUuid) {
        //判断该产品是否有未完成订单
        APIGetHasNotifyMsg apiGetHasNotifyMsg = new APIGetHasNotifyMsg();
        apiGetHasNotifyMsg.setAccountUuid(accountUuid);
        apiGetHasNotifyMsg.setProductUuid(productUuid);

        APIGetHasNotifyReply reply = new BillingRESTCaller().syncJsonPost(apiGetHasNotifyMsg);
        if (reply.isInventory())
            throw new ApiMessageInterceptionException(
                    argerr("该订单[uuid:%s] 有未完成操作，请稍等！", productUuid));
    }

    /**
     * 设置为外采资源
     * */
    public void validate(APICreateOutsideResourceMsg msg) {
        if(Q.New(OutsideResourceVO.class)
                .eq(OutsideResourceVO_.resourceUuid, msg.getResourceUuid())
                .isExists()){
            throw new ApiMessageInterceptionException(
                    argerr("该资源已经是外采资源！"));
        }
    }
}
