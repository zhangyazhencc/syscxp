package com.syscxp.tunnel.host;

import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.tunnel.host.*;
import com.syscxp.header.tunnel.switchs.PhysicalSwitchVO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.TypedQuery;

import java.util.List;

import static com.syscxp.core.Platform.argerr;


public class MonitorApiInterceptor implements ApiMessageInterceptor {

    @Autowired
    private DatabaseFacade dbf;

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateMonitorHostMsg) {
            validate((APICreateMonitorHostMsg) msg);
        } else if (msg instanceof APICreateHostSwitchMonitorMsg) {
            validate((APICreateHostSwitchMonitorMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateMonitorHostMsg msg) {
        msg.setHostType(MonitorHostConstant.HOST_TYPE);
    }

    private void validate(APICreateHostSwitchMonitorMsg msg) {
        //判断监控机和物理交换机所属节点是否一样
        MonitorHostVO hostVO = dbf.findByUuid(msg.getHostUuid(), MonitorHostVO.class);
        String hostNodeUuid = hostVO.getNodeUuid();
        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(msg.getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
        String physicalNodeUuid = physicalSwitchVO.getNodeUuid();
        if (!hostNodeUuid.equals(physicalNodeUuid)) {
            throw new ApiMessageInterceptionException(argerr("该监控机不能监控非该节点下的物理交换机 "));
        }

        //判断监控口在该物理交换机下是否开了业务
        String sql = "select count(a.uuid) from SwitchPortVO a, SwitchVO b " +
                "where a.switchUuid = b.uuid " +
                "and b.physicalSwitchUuid = :physicalSwitchUuid and a.portName = :portName ";
        TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
        vq.setParameter("physicalSwitchUuid", msg.getPhysicalSwitchUuid());
        vq.setParameter("portName", msg.getPhysicalSwitchPortName());
        Long count = vq.getSingleResult();
        if (count > 0) {
            throw new ApiMessageInterceptionException(argerr("该端口已经在业务口被录用，不能创建监控口 "));
        }

        //判断监控口名称在该物理交换机下是否存在
        SimpleQuery<HostSwitchMonitorVO> q = dbf.createQuery(HostSwitchMonitorVO.class);
        q.add(HostSwitchMonitorVO_.physicalSwitchUuid, SimpleQuery.Op.EQ, msg.getPhysicalSwitchUuid());
        q.add(HostSwitchMonitorVO_.physicalSwitchPortName, SimpleQuery.Op.EQ, msg.getPhysicalSwitchPortName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr("physicalSwitchPortName %s is already exist ", msg.getPhysicalSwitchPortName()));

        //同一个监控机的网卡名称要唯一
        SimpleQuery<HostSwitchMonitorVO> q2 = dbf.createQuery(HostSwitchMonitorVO.class);
        q2.add(HostSwitchMonitorVO_.hostUuid, SimpleQuery.Op.EQ, msg.getHostUuid());
        q2.add(HostSwitchMonitorVO_.interfaceName, SimpleQuery.Op.EQ, msg.getInterfaceName());
        if (q2.isExists())
            throw new ApiMessageInterceptionException(argerr("interfaceName %s is already exist ", msg.getInterfaceName()));

        List<HostSwitchMonitorVO> hostSwitchMonitorVOS = Q.New(HostSwitchMonitorVO.class)
                .eq(HostSwitchMonitorVO_.hostUuid,msg.getHostUuid())
                .eq(HostSwitchMonitorVO_.physicalSwitchUuid,msg.getPhysicalSwitchUuid())
                .list();

        if(!hostSwitchMonitorVOS.isEmpty())
            throw new ApiMessageInterceptionException(argerr(" duplicated host & switch existed!"));
    }
}
