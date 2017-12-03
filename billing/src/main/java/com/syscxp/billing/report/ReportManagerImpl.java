package com.syscxp.billing.report;

import com.syscxp.billing.header.report.APIGetDayExpenseReportMsg;
import com.syscxp.billing.header.report.APIGetDayExpenseReportReply;
import com.syscxp.billing.header.report.ReportOrderData;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Query;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportManagerImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(ReportManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

    @Override
    public void handleMessage(Message msg) {

        if (msg instanceof APIGetDayExpenseReportMsg) {
            handle((APIGetDayExpenseReportMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APIGetDayExpenseReportMsg msg) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(msg.getDateStart());
        LocalDate end = LocalDate.parse(msg.getDateEnd());
        List<ReportOrderData> list = new ArrayList<>();
        long duration = ChronoUnit.DAYS.between(start, end);
        for (int i = 0; i <= duration; i++) {
            ReportOrderData e = new ReportOrderData();
            e.setPayTime(start.format(f));
            list.add(e);
            start = start.plusDays(1);
        }

        String sql = "SELECT DATE_FORMAT(payTime,'%Y-%m-%d')AS payTime,TYPE,productType, SUM(IFNULL(originalPrice,0)) AS originalPrice ,SUM(IFNULL(price,0)) AS price,SUM(IFNULL(payPresent,0)) AS payPresent,SUM(IFNULL(payCash,0)) AS payCash\n" +
                "FROM OrderVO WHERE state = 'PAID' and DATE_FORMAT(createDate,'%Y-%m-%d') between :dateStart and :dateEnd GROUP BY  DATE_FORMAT(payTime,'%Y-%m-%d'),TYPE,productType ORDER BY payTime,TYPE,productType ";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("dateStart", msg.getDateStart());
        q.setParameter("dateEnd", msg.getDateEnd());
        List<Object[]> objs = q.getResultList();
        List<ReportOrderData> vos = objs.stream().map(ReportOrderData::new).collect(Collectors.toList());
        list.forEach(r -> {
            vos.forEach(v -> {
                if (r.getPayTime().equals(v.getPayTime())) {
                    r.setOriginalPrice(v.getOriginalPrice());
                    r.setPayCash(v.getPayCash());
                    r.setPayPresent(v.getPayPresent());
                    r.setPrice(v.getPrice());
                    r.setProductType(v.getProductType());
                    r.setType(v.getType());
                }
            });
        });

        APIGetDayExpenseReportReply reply = new APIGetDayExpenseReportReply();
        reply.setInventories(list);
        bus.reply(msg, reply);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID_REPORT);
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return msg;
    }
}
