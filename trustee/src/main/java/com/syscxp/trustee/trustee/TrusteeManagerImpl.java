package com.syscxp.trustee.trustee;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.message.Message;
import com.syscxp.trustee.header.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


public class TrusteeManagerImpl extends AbstractService implements TrusteeManager {

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    private VOAddAllOfMsg vOAddAllOfMsg = new VOAddAllOfMsg();
    private TrusteeBaseUtil trusteeBaseUtil = new TrusteeBaseUtil();
    @Override
    public void handleMessage(Message msg) {

        if(msg instanceof APICreateTrusteeMsg){
            handle((APICreateTrusteeMsg)msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }

    }

    @Transactional
    private void handle(APICreateTrusteeMsg msg) {

        APICreateTrusteeEvent event = new APICreateTrusteeEvent(msg.getId());
        TrusteeVO vo = new TrusteeVO();
        vOAddAllOfMsg.addAll(msg,vo);
        vo.setUuid(Platform.getUuid());
        vo.setExpireDate(trusteeBaseUtil.getExpireDate(dbf.getCurrentSqlTime(),msg.getProductChargeModel(),msg.getDuration()));
        dbf.getEntityManager().persist(vo);

        if(msg.getTrusteeDetails() != null && msg.getTrusteeDetails().size()>0){
            List<TrustDetailVO> detailList = new ArrayList<>();
            msg.getTrusteeDetails().forEach((K,V)->{
                TrustDetailVO detailVO = new TrustDetailVO();
                detailVO.setUuid(Platform.getUuid());
                detailVO.setCost(V);
                detailVO.setName(K);
                detailVO.setCreateDate(dbf.getCurrentSqlTime());
                detailVO.setLastOpDate(dbf.getCurrentSqlTime());
                detailList.add(detailVO);
            });

            detailList.stream().forEach((itVO)->{dbf.getEntityManager().persist(itVO);});
        }

        dbf.getEntityManager().flush();

    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(TrusteeConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }


}
