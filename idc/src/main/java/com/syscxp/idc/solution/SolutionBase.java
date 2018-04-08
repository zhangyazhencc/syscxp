package com.syscxp.idc.solution;

import com.syscxp.core.Platform;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SQL;
import com.syscxp.header.idc.solution.APICreateSolutionTunnelMsg;
import com.syscxp.header.idc.solution.SolutionInterfaceVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.math.BigDecimal;
/**
 * Create by DCY on 2018/4/4
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class SolutionBase {
    private static final CLogger logger = Utils.getLogger(SolutionBase.class);

    @Autowired
    private DatabaseFacade dbf;

    /**
     * 创建云专线的新购物理接口-共享端口
     */
    public SolutionInterfaceVO createInterfaceByTunnel(String endpointUuid, String endpointName, APICreateSolutionTunnelMsg msg){
        SolutionInterfaceVO interfaceVO = new SolutionInterfaceVO();
        interfaceVO.setUuid(Platform.getUuid());
        interfaceVO.setSolutionUuid(msg.getSolutionUuid());
        interfaceVO.setName(endpointName + "_共享接口_" + Platform.getUuid().substring(0, 6));
        interfaceVO.setEndpointUuid(endpointUuid);
        interfaceVO.setPortOfferingUuid("SHARE");
        interfaceVO.setDuration(msg.getDuration());
        interfaceVO.setProductChargeModel(msg.getProductChargeModel());
        interfaceVO.setCost(BigDecimal.ZERO);

        dbf.getEntityManager().persist(interfaceVO);
        return interfaceVO;
    }

    //计算方案总价
    private BigDecimal totalCost(String solutionUuid){
        BigDecimal totalCost = new BigDecimal(0);

        totalCost = totalCost.add( SQL.New("select ifnull(sum(cost), 0) from SolutionInterfaceVO where solutionUuid = :solutionUuid", BigDecimal.class)
                .param("solutionUuid", solutionUuid)
                .find());

        totalCost = totalCost.add( SQL.New("select ifnull(sum(cost), 0) from SolutionTunnelVO where solutionUuid = :solutionUuid", BigDecimal.class)
                .param("solutionUuid", solutionUuid)
                .find());

        totalCost = totalCost.add( SQL.New("select ifnull(sum(cost), 0) from SolutionVpnVO where solutionUuid = :solutionUuid", BigDecimal.class)
                .param("solutionUuid", solutionUuid)
                .find());

        return totalCost;
    }


}
