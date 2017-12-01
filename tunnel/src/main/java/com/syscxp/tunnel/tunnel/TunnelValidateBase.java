package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SQL;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.billing.APIGetHasNotifyMsg;
import com.syscxp.header.billing.APIGetHasNotifyReply;
import com.syscxp.header.billing.APIGetProductPriceMsg;
import com.syscxp.header.billing.APIGetProductPriceReply;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
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
import java.util.List;
import java.util.Objects;

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

        InterfaceVO iface = Q.New(InterfaceVO.class).eq(InterfaceVO_.uuid, msg.getUuid()).find();
        if (iface.getExpireDate().before(Timestamp.valueOf(LocalDateTime.now())))
            throw new ApiMessageInterceptionException(
                    argerr("The Interface[uuid:%s] has expired！", msg.getUuid()));
        Q q = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid, iface.getUuid());
        if (q.count() > 1)
            throw new ApiMessageInterceptionException(
                    argerr("The Interface[uuid:%s] has been used by two tunnel as least！", msg.getUuid()));
        SwitchPortVO switchPort = Q.New(SwitchPortVO.class).eq(SwitchPortVO_.uuid, iface.getSwitchPortUuid()).find();
        if (switchPort.getPortType().equals("SHARE"))
            throw new ApiMessageInterceptionException(
                    argerr("The type of Interface[uuid:%s] is %s, could not modify！", msg.getUuid(), switchPort.getPortType()));

        if (msg.getSwitchPortUuid().equals(iface.getSwitchPortUuid())
                && (msg.getSegments() == null || msg.getSegments().isEmpty())) {
            throw new ApiMessageInterceptionException(
                    argerr("The InnerVlans cannot be empty！"));
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


        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(new TunnelBase().getInterfacePriceUnit(msg.getPortOfferingUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("账户[uuid:%s]余额不足!", msg.getAccountUuid()));
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
        if (portType.equals("SHARE")) {
            String sql = "select a from InterfaceVO a,SwitchPortVO b " +
                    "where a.switchPortUuid = b.uuid " +
                    "and a.accountUuid = :accountUuid " +
                    "and a.endpointUuid = :endpointUuid " +
                    "and b.portType = 'SHARE'";
            TypedQuery<InterfaceVO> itq = dbf.getEntityManager().createQuery(sql, InterfaceVO.class);
            itq.setParameter("accountUuid", msg.getAccountUuid());
            itq.setParameter("endpointUuid", msg.getEndpointUuid());
            if (!itq.getResultList().isEmpty()) {
                throw new ApiMessageInterceptionException(argerr("一个用户在同一个连接点下只能购买一个共享口！ "));
            }
        }

        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(new TunnelBase().getInterfacePriceUnit(portType));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));

    }

    public void validate(APIUpdateInterfaceMsg msg) {

        InterfaceVO iface = Q.New(InterfaceVO.class).eq(InterfaceVO_.uuid, msg.getUuid()).find();
        if (iface.getExpireDate().before(Timestamp.valueOf(LocalDateTime.now())))
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

        //判断该产品是否有未完成订单
        checkOrderNoPay(iface.getAccountUuid(), msg.getUuid());
    }

    public void validate(APICreateTunnelMsg msg) {
        //BOSS创建验证物理接口的账户是否一致
        if(msg.getSession().getType() == AccountType.SystemAdmin){
            if(msg.getInterfaceAUuid() != null){
                String accountUuid = dbf.findByUuid(msg.getInterfaceAUuid(),InterfaceVO.class).getAccountUuid();
                if(!Objects.equals(msg.getAccountUuid(),accountUuid)){
                    throw new ApiMessageInterceptionException(argerr("物理接口A不属于该用户！"));
                }
            }
            if(msg.getInterfaceZUuid() != null){
                String accountUuid = dbf.findByUuid(msg.getInterfaceZUuid(),InterfaceVO.class).getAccountUuid();
                if(!Objects.equals(msg.getAccountUuid(),accountUuid)){
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
        //如果跨国,验证互联连接点和内外联交换机配置
        if (msg.getInnerConnectedEndpointUuid() != null) {
            validateInnerConnectEndpoint(msg.getInnerConnectedEndpointUuid());
        }
        //判断账户金额是否充足
        EndpointVO evoA = dbf.findByUuid(msg.getEndpointAUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(msg.getEndpointZUuid(), EndpointVO.class);

        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(new TunnelBase().getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), evoA.getNodeUuid(),
                evoZ.getNodeUuid(), msg.getInnerConnectedEndpointUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
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
        //获取互联设备的VLAN
        Integer innerVlan = ts.getVlanBySwitch(innerSwitch.getUuid());
        if (innerVlan == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口所属内联虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }
    }

    public void validate(APICreateTunnelManualMsg msg) {
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);
        //BOSS创建验证物理接口的账户是否一致

        if(!Objects.equals(msg.getAccountUuid(),interfaceVOA.getAccountUuid())){
            throw new ApiMessageInterceptionException(argerr("物理接口A不属于该用户！"));
        }

        if(!Objects.equals(msg.getAccountUuid(),interfaceVOZ.getAccountUuid())){
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

        //如果跨国,验证互联连接点和内外联交换机配置
        if (msg.getInnerConnectedEndpointUuid() != null) {
            validateInnerConnectEndpoint(msg.getInnerConnectedEndpointUuid());
        }

        //判断外部VLAN是否可用
        validateVlan(msg.getInterfaceAUuid(), msg.getaVlan());
        validateVlan(msg.getInterfaceZUuid(), msg.getzVlan());

        //如果是ACCESS或是QINQ的物理接口，判断该物理接口是否已经开通通道
        if (interfaceVOA.getType() == NetworkType.ACCESS || interfaceVOA.getType() == NetworkType.QINQ) {
            boolean exists = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getInterfaceAUuid())
                    .isExists();
            if (exists) {
                throw new ApiMessageInterceptionException(argerr("该物理接口不可复用"));
            }
        }
        if (interfaceVOZ.getType() == NetworkType.ACCESS || interfaceVOZ.getType() == NetworkType.QINQ) {
            boolean exists = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getInterfaceZUuid())
                    .isExists();
            if (exists) {
                throw new ApiMessageInterceptionException(argerr("该物理接口不可复用"));
            }
        }

        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select b.tunnelUuid from TunnelSwitchPortVO b where b.switchPortUuid = :switchPortUuid and b.type = 'QINQ') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        if (msg.getVlanSegment() != null) {
            if (interfaceVOA.getType() == NetworkType.QINQ) {
                List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
                for (InnerVlanSegment vlanSegment : vlanSegments) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("switchPortUuid", interfaceVOA.getSwitchPortUuid());
                    vq.setParameter("startVlan", vlanSegment.getStartVlan());
                    vq.setParameter("endVlan", vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
            if (interfaceVOZ.getType() == NetworkType.QINQ) {
                List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
                for (InnerVlanSegment vlanSegment : vlanSegments) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("switchPortUuid", interfaceVOZ.getSwitchPortUuid());
                    vq.setParameter("startVlan", vlanSegment.getStartVlan());
                    vq.setParameter("endVlan", vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
        }

        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(new TunnelBase().getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), evoA.getNodeUuid(),
                evoZ.getNodeUuid(), msg.getInnerConnectedEndpointUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
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

    }

    public void validate(APIUpdateTunnelBandwidthMsg msg) {
        //判断该产品是否有未完成订单
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());

        //调整次数当月是否达到上限
        LocalDateTime dateTime =
                LocalDate.now().withDayOfMonth(LocalDate.MIN.getDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(TunnelMotifyRecordVO.class).eq(TunnelMotifyRecordVO_.tunnelUuid, msg.getUuid())
                .gte(TunnelMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime)).count();
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
        TunnelVO vo = dbf.findByUuid(msg.getUuid(),TunnelVO.class);
        if(msg.getSession().getType() != AccountType.SystemAdmin && (vo.getState()==TunnelState.Enabled || vo.getState()==TunnelState.Disabled)){
            int deleteDays = TunnelGlobalConfig.PRODUCT_DELETE_DAYS.value(Integer.class);
            if (vo.getCreateDate().toLocalDateTime().plusDays(deleteDays).isAfter(LocalDateTime.now()))
                throw new ApiMessageInterceptionException(
                        argerr("云专线[uuid:%s]购买未超过%s天,不能删除 !", msg.getUuid(), deleteDays));
        }
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());
    }

    public void validate(APIDeleteForciblyTunnelMsg msg) {
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());
    }

    public void validate(APIUpdateTunnelStateMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        if (vo.getState() == msg.getState()) {
            throw new ApiMessageInterceptionException(argerr("该云专线[uuid:%s] 已是该状况，不可重复操作 ", msg.getUuid()));
        }
    }

    public void validate(APICreateQinqMsg msg) {

        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select b.tunnelUuid from TunnelSwitchPortVO b where b.switchPortUuid = :switchPortUuid and b.type = 'QINQ') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        TunnelSwitchPortVO tunnelSwitchPortVOA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortVOZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        if (tunnelSwitchPortVOA.getType() == NetworkType.QINQ) {
            TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
            vq.setParameter("switchPortUuid", tunnelSwitchPortVOA.getSwitchPortUuid());
            vq.setParameter("startVlan", msg.getStartVlan());
            vq.setParameter("endVlan", msg.getEndVlan());
            Long count = vq.getSingleResult();
            if (count > 0) {
                throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
            }
        }
        if (tunnelSwitchPortVOZ.getType() == NetworkType.QINQ) {
            TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
            vq.setParameter("switchPortUuid", tunnelSwitchPortVOZ.getSwitchPortUuid());
            vq.setParameter("startVlan", msg.getStartVlan());
            vq.setParameter("endVlan", msg.getEndVlan());
            Long count = vq.getSingleResult();
            if (count > 0) {
                throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
            }
        }

    }

    public void validate(APIDeleteQinqMsg msg) {
        QinqVO qinqVO = dbf.findByUuid(msg.getUuid(), QinqVO.class);
        Long count = Q.New(QinqVO.class)
                .eq(QinqVO_.tunnelUuid, qinqVO.getTunnelUuid())
                .count();
        if (count == 1) {
            throw new ApiMessageInterceptionException(argerr("该云专线[uuid:%s] 至少要有一个内部VLAN段，不能删！ ", qinqVO.getTunnelUuid()));
        }
    }

    public void validate(APIUpdateTunnelVlanMsg msg) {


        if (!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
            if (isCross(msg.getUuid(), msg.getOldInterfaceAUuid())) {
                throw new ApiMessageInterceptionException(argerr("该接口A为共点，不能修改配置！！"));
            }
            validateVlan(msg.getInterfaceAUuid(), msg.getaVlan());
        }

        if (!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
            if (isCross(msg.getUuid(), msg.getOldInterfaceZUuid())) {
                throw new ApiMessageInterceptionException(argerr("该接口Z为共点，不能修改配置！！"));
            }
            validateVlan(msg.getInterfaceZUuid(), msg.getzVlan());
        }

    }

    public void validate(APIUpdateForciblyTunnelVlanMsg msg) {

        if (!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
            if (isCross(msg.getUuid(), msg.getOldInterfaceAUuid())) {
                throw new ApiMessageInterceptionException(argerr("该接口A为共点，不能修改配置！！"));
            }
            validateVlan(msg.getInterfaceAUuid(), msg.getaVlan());
        }

        if (!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
            if (isCross(msg.getUuid(), msg.getOldInterfaceZUuid())) {
                throw new ApiMessageInterceptionException(argerr("该接口Z为共点，不能修改配置！！"));
            }
            validateVlan(msg.getInterfaceZUuid(), msg.getzVlan());
        }
    }

    /**
     * 判断物理接口是否是共点
     */

    private boolean isCross(String tunnelUuid, String interfaceUuid) {
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
     * 判断外部VLAN是否可用
     */
    private void validateVlan(String interfaceUuid, Integer vlan) {
        TunnelStrategy ts = new TunnelStrategy();
        //查询该TUNNEL的物理接口所属的虚拟交换机
        String switchUuid = ts.findSwitchByInterface(interfaceUuid);

        //查询该虚拟交换机下所有的Vlan段
        List<SwitchVlanVO> vlanList = ts.findSwitchVlanBySwitch(switchUuid);

        if (vlanList.isEmpty()) {
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下未配置VLAN，请联系系统管理员 "));
        }

        //查询该虚拟交换机下已经分配的Vlan
        List<Integer> allocatedVlans = ts.fingAllocateVlanBySwitch(switchUuid);

        //判断外部VLAN是否在该虚拟交换机的VLAN段中
        Boolean inner = false;
        for (SwitchVlanVO switchVlanVO : vlanList) {
            if (vlan >= switchVlanVO.getStartVlan() && vlan <= switchVlanVO.getEndVlan()) {
                inner = true;
                break;
            }
        }
        if (!inner) {
            throw new ApiMessageInterceptionException(argerr("avlan not in switchVlan"));
        }
        //判断外部vlan是否可用
        if (!allocatedVlans.isEmpty() && allocatedVlans.contains(vlan)) {
            throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", vlan));
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
    private void checkOrderNoPay(String accountUuid, String productUuid) {
        //判断该产品是否有未完成订单
        APIGetHasNotifyMsg apiGetHasNotifyMsg = new APIGetHasNotifyMsg();
        apiGetHasNotifyMsg.setAccountUuid(accountUuid);
        apiGetHasNotifyMsg.setProductUuid(productUuid);

        APIGetHasNotifyReply reply = new TunnelRESTCaller().syncJsonPost(apiGetHasNotifyMsg);
        if (reply.isInventory())
            throw new ApiMessageInterceptionException(
                    argerr("该订单[uuid:%s] 有未完成操作，请稍等！", productUuid));
    }
}
