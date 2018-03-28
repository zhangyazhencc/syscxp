package com.syscxp.trustee.trustee;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.header.AbstractService;
import com.syscxp.header.message.Message;
import com.syscxp.trustee.header.*;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


public class TrusteeManagerImpl extends AbstractService implements TrusteeManager {

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    private final VOAddAllOfMsg vOAddAllOfMsg = new VOAddAllOfMsg();

    private final TrusteeBaseUtil trusteeBaseUtil = new TrusteeBaseUtil();

    @Override
    public void handleMessage(Message msg) {

        if(msg instanceof APICreateTrusteeMsg){
            handle((APICreateTrusteeMsg)msg);
        } else if(msg instanceof APIDeleteTrusteeMsg){
            handle((APIDeleteTrusteeMsg)msg);
        } else if(msg instanceof APIUpdateTrusteeMsg){
            handle((APIUpdateTrusteeMsg)msg);
        } else if(msg instanceof APITrusteeRenewalMsg){
            handle((APITrusteeRenewalMsg)msg);
        } else if(msg instanceof APICreateTrusteeDetailMsg){
            handle((APICreateTrusteeDetailMsg)msg);
        } else if(msg instanceof APIDeleteTrusteeDetailMsg){
            handle((APIDeleteTrusteeDetailMsg)msg);
        } else{
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APIDeleteTrusteeDetailMsg msg) {

        APIDeleteTrusteeDetailEvent event = new APIDeleteTrusteeDetailEvent(msg.getId());
        UpdateQuery.New(TrustDetailVO.class).condAnd(TrustDetailVO_.uuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
        bus.publish(event);
    }

    private void handle(APICreateTrusteeDetailMsg msg) {

        APICreateTrusteeDetailEvent event = new APICreateTrusteeDetailEvent(msg.getId());
        TrustDetailVO vo = new TrustDetailVO();
        vOAddAllOfMsg.addAll(msg,vo);
        vo.setUuid(Platform.getUuid());
        vo.setLastOpDate(dbf.getCurrentSqlTime());
        vo.setCreateDate(dbf.getCurrentSqlTime());
        event.setInventory(TrusteeDetailInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);
    }

    private void handle(APITrusteeRenewalMsg msg) {

        APITrusteeRenewalEvent event = new APITrusteeRenewalEvent(msg.getId());
        /*
        * 续费
        *
        * */
        TrusteeVO vo = dbf.findByUuid(msg.getUuid(), TrusteeVO.class);
        vOAddAllOfMsg.addAll(msg, vo);
        vo.setExpireDate(trusteeBaseUtil.getExpireDate(vo.getExpireDate(),msg.getProductChargeModel(),msg.getDuration()));
        vo.setTotalCost(vo.getTotalCost().add(msg.getCost()));
        event.setInventory(TrusteeInventory.valueOf(dbf.updateAndRefresh(vo)));
        bus.publish(event);

    }

    private void handle(APIUpdateTrusteeMsg msg) {

        APIUpdateTrusteeEvent event = new APIUpdateTrusteeEvent(msg.getId());
        TrusteeVO vo = dbf.findByUuid(msg.getUuid(), TrusteeVO.class);
        vOAddAllOfMsg.addAll(msg, vo);

        if(msg.getTotalCost() != null ){
            if(msg.getTotalCost().compareTo(vo.getTotalCost()) > 0 ){
                /*
                * 扣费
                *
                * */
            }else if(msg.getTotalCost().compareTo(vo.getTotalCost()) < 0){
                /*
                * 退费
                *
                * */
            }
        }
        event.setInventory(TrusteeInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);

    }

    private void handle(APIDeleteTrusteeMsg msg) {

        APIDeleteTrusteeEvent event = new APIDeleteTrusteeEvent(msg.getId());

        /*
        * 校验
        *
        * */
        UpdateQuery.New(TrustDetailVO.class).condAnd(TrustDetailVO_.trusteeUuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
        UpdateQuery.New(TrusteeVO.class).condAnd(TrusteeVO_.uuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();

        bus.publish(event);
    }

    @Transactional
    private void handle(APICreateTrusteeMsg msg) {

        APICreateTrusteeEvent event = new APICreateTrusteeEvent(msg.getId());
        /*
        * 扣费
        *
        * */

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

            dbf.persistCollection(detailList);
//            detailList.stream().forEach((itVO)->{dbf.getEntityManager().persist(itVO);});
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
