package com.syscxp.billing.renew;

import com.syscxp.billing.header.renew.RenewVO;
import com.syscxp.billing.header.renew.RenewVO_;
import com.syscxp.billing.header.sla.ProductCaller;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.billing.*;
import com.syscxp.header.rest.*;
import com.syscxp.header.tunnel.edgeLine.APIRenewAutoEdgeLineMsg;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.header.vpn.vpn.APIRenewAutoVpnMsg;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.GLock;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class RenewJob implements Job {

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;

    private static final CLogger logger = Utils.getLogger(RenewJob.class);

    public RenewJob() {
    }

    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        GLock lock = new GLock(String.format("id-%s", "createRenew"), 600);
        lock.lock();
        try {

            SimpleQuery<RenewVO> q = dbf.createQuery(RenewVO.class);
            q.add(RenewVO_.isRenewAuto, SimpleQuery.Op.EQ, true);
            q.add(RenewVO_.expiredTime, SimpleQuery.Op.LT, dbf.getCurrentSqlTime());
            q.add(RenewVO_.expiredTime, SimpleQuery.Op.GT, Timestamp.valueOf(dbf.getCurrentSqlTime().toLocalDateTime().minusDays(7)));
            List<RenewVO> renewVOs = q.list();
            if (renewVOs == null || renewVOs.size() == 0) {
                logger.info("there is no activity renew product");
                return;
            }
            logger.info("the demon thread was going to autoRenew");
            ListIterator<RenewVO> ite = renewVOs.listIterator();
            while (ite.hasNext()) {

                RenewVO renewVO = ite.next();

                GLock lockid = new GLock(String.format("id-%s-%s", "createRenew", renewVO.getUuid()), 600);
                lockid.lock();

                try {
                    RenewVO renew = dbf.findByUuid(renewVO.getUuid(), RenewVO.class);
                    if (renew.getExpiredTime().after(Timestamp.valueOf(LocalDateTime.now()))) {
                        continue;
                    }
                    ProductCaller caller = new ProductCaller(renewVO.getProductType());

                    if (renewVO.getProductType().equals(ProductType.TUNNEL)) {
                        APIRenewAutoTunnelMsg aMsg = new APIRenewAutoTunnelMsg();

                        aMsg.setUuid(renewVO.getProductUuid());
                        aMsg.setDuration(1);
                        aMsg.setProductChargeModel(renewVO.getProductChargeModel());
                        InnerMessageHelper.setMD5(aMsg);
                        String gstr = RESTApiDecoder.dump(aMsg);
                        RestAPIResponse rsp = restf.syncJsonPost(caller.getProductUrl(), gstr, null, RestAPIResponse.class);

                        //                    if (!rsp.getState().equals(RestAPIState.Done.toString())) {
                        //                        throw new RuntimeException("unknown mistake");
                        //                    }
                    } else if (renewVO.getProductType().equals(ProductType.EDGELINE)) {
                        APIRenewAutoEdgeLineMsg msg = new APIRenewAutoEdgeLineMsg();
                        msg.setUuid(renewVO.getProductUuid());
                        msg.setDuration(1);
                        msg.setProductChargeModel(renewVO.getProductChargeModel());
                        InnerMessageHelper.setMD5(msg);
                        String gstr = RESTApiDecoder.dump(msg);
                        RestAPIResponse rsp = restf.syncJsonPost(caller.getProductUrl(), gstr, null, RestAPIResponse.class);

                    } else if (renewVO.getProductType().equals(ProductType.VPN)) {
                        APIRenewAutoVpnMsg msg = new APIRenewAutoVpnMsg();
                        msg.setUuid(renewVO.getProductUuid());
                        msg.setDuration(1);
                        msg.setProductChargeModel(renewVO.getProductChargeModel());
                        InnerMessageHelper.setMD5(msg);
                        String gstr = RESTApiDecoder.dump(msg);
                        RestAPIResponse rsp = restf.syncJsonPost(caller.getProductUrl(), gstr, null, RestAPIResponse.class);

                    } else if (renewVO.getProductType().equals(ProductType.PORT)) {
                        APIRenewAutoInterfaceMsg aMsg = new APIRenewAutoInterfaceMsg();

                        aMsg.setUuid(renewVO.getProductUuid());
                        aMsg.setDuration(1);
                        aMsg.setProductChargeModel(renewVO.getProductChargeModel());
                        InnerMessageHelper.setMD5(aMsg);
                        String gstr = RESTApiDecoder.dump(aMsg);
                        restf.syncJsonPost(caller.getProductUrl(), gstr, null, RestAPIResponse.class);
                        RestAPIResponse rsp = restf.syncJsonPost(caller.getProductUrl(), gstr, null, RestAPIResponse.class);

                        //                    if (!rsp.getState().equals(RestAPIState.Done.toString())) {
                        //                        throw new RuntimeException("unknown mistake");
                        //                    }
                    } else if (renewVO.getProductType().equals(ProductType.DISK)) {
                        Map<String, String> header = new HashMap<>();
                        header.put(RESTConstant.COMMAND_PATH, "autoRenewEcpDisk");
                        RenewCmd renewCmd = new RenewCmd();
                        renewCmd.setAccountUuid(renewVO.getAccountUuid());
                        renewCmd.setDuration(1);
                        renewCmd.setProductChargeModel(renewVO.getProductChargeModel());
                        renewCmd.setUuid(renewVO.getProductUuid());
                        String body = JSONObjectUtil.toJsonString(renewCmd);
                        restf.syncJsonPost(caller.getProductUrl(), body, header, RestAPIResponse.class);
                    } else if (renewVO.getProductType().equals(ProductType.HOST)) {
                        Map<String, String> header = new HashMap<>();
                        header.put(RESTConstant.COMMAND_PATH, "autoRenewEcpHost");
                        RenewCmd renewCmd = new RenewCmd();
                        renewCmd.setAccountUuid(renewVO.getAccountUuid());
                        renewCmd.setDuration(1);
                        renewCmd.setProductChargeModel(renewVO.getProductChargeModel());
                        renewCmd.setUuid(renewVO.getProductUuid());
                        String body = JSONObjectUtil.toJsonString(renewCmd);
                        restf.syncJsonPost(caller.getProductUrl(), body, header, RestAPIResponse.class);
                    } else if (renewVO.getProductType().equals(ProductType.RESOURCEPOOL)) {
                        Map<String, String> header = new HashMap<>();
                        header.put(RESTConstant.COMMAND_PATH, "autoRenewEcpResourcePool");
                        RenewCmd renewCmd = new RenewCmd();
                        renewCmd.setAccountUuid(renewVO.getAccountUuid());
                        renewCmd.setDuration(1);
                        renewCmd.setProductChargeModel(renewVO.getProductChargeModel());
                        renewCmd.setUuid(renewVO.getProductUuid());
                        String body = JSONObjectUtil.toJsonString(renewCmd);
                        restf.syncJsonPost(caller.getProductUrl(), body, header, RestAPIResponse.class);
                    } else if (renewVO.getProductType().equals(ProductType.IP)) {
                        Map<String, String> header = new HashMap<>();
                        header.put(RESTConstant.COMMAND_PATH, "autoRenewElasticIp");
                        RenewCmd renewCmd = new RenewCmd();
                        renewCmd.setAccountUuid(renewVO.getAccountUuid());
                        renewCmd.setDuration(1);
                        renewCmd.setProductChargeModel(renewVO.getProductChargeModel());
                        renewCmd.setUuid(renewVO.getProductUuid());
                        String body = JSONObjectUtil.toJsonString(renewCmd);
                        restf.syncJsonPost(caller.getProductUrl(), body, header, RestAPIResponse.class);
                    }

                } finally {
                    lockid.unlock();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

}
