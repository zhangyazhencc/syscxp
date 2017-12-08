package com.syscxp.billing.header.bill;

import com.syscxp.billing.header.balance.DealType;
import com.syscxp.billing.header.balance.DealWay;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.billing.AccountBalanceVO_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.syscxp.header.billing.AccountBalanceVO;
import com.syscxp.core.Platform;
import com.syscxp.core.db.GLock;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Lazy(false)
public class BillJob {

    @Autowired
    private DatabaseFacade dbf;

    public BillJob(){}

    public BillJob(DatabaseFacade dbf){
        this.dbf = dbf;
    }

    private static final CLogger logger = Utils.getLogger(BillJob.class);

    @Scheduled(cron = "0 0 10 1 * ? ")
    public void generateBill() {

        GLock lock = new GLock(String.format("id-%s", "generateBill"), 1);
        lock.lock();
        try {
            logger.info("generate Bill start ..............");
            Timestamp currentSqlTime = dbf.getCurrentSqlTime();
            Timestamp startTime = getLastMonthFirstDay(currentSqlTime);
            Timestamp endTime = getLastMonthLastDay(currentSqlTime);

            List<BillStatistics> bills = getBillList(currentSqlTime,startTime,endTime);
            Map<String, String> map = new HashMap<>();
            for (BillStatistics bill : bills) {
                String accountUuid = bill.getAccountUuid();
                if (map.get(accountUuid) != null) {
                    BillVO bVO = dbf.findByUuid(map.get(accountUuid), BillVO.class);
                    calculateBalance(bill, bVO);
                    dbf.updateAndRefresh(bVO);

                } else {
                    BillVO bVO = getBillVO(bill.getAccountUuid(),startTime,endTime);
                    if (bVO == null) {
                        String uuid = Platform.getUuid();
                        map.put(accountUuid, uuid);
                        BillVO vo = new BillVO();
                        vo.setUuid(uuid);
                        vo.setAccountUuid(accountUuid);
                        vo.setBillDate(currentSqlTime);
                        vo.setTimeStart(startTime);
                        vo.setTimeEnd(endTime);
                        calculateBalance(bill, vo);
                        AccountBalanceVO abVO =getAccountBalance(accountUuid);
                        BigDecimal balance = abVO.getCashBalance();
                        if (balance.compareTo(BigDecimal.ZERO) < 0) {
                            vo.setRepay(balance);
                        } else {
                            vo.setRepay(BigDecimal.ZERO);
                        }
                        vo.setCashBalance(balance);
                        dbf.persistAndRefresh(vo);
                    }

                }
            }
            logger.info("generate Bill end ..............");
        } catch (Exception e) {
            e.printStackTrace();
//            try {
//                TimeUnit.HOURS.sleep(1);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
//            generateBill();
        } finally {
            lock.unlock();
        }
    }

    private BillVO getBillVO(String accountUuid,Timestamp startTime,Timestamp endTime) {
        SimpleQuery<BillVO> sq = dbf.createQuery(BillVO.class);
        sq.add(BillVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        sq.add(BillVO_.timeStart, SimpleQuery.Op.EQ, startTime);
        sq.add(BillVO_.timeEnd, SimpleQuery.Op.EQ, endTime);
        return sq.find();
    }

    private AccountBalanceVO getAccountBalance(String uuid) {
        SimpleQuery<AccountBalanceVO> sq = dbf.createQuery(AccountBalanceVO.class);
        sq.add(AccountBalanceVO_.uuid, SimpleQuery.Op.EQ, uuid);
        return sq.find();
    }

    private List getBillList(Timestamp currentSqlTime,Timestamp startTime,Timestamp endTime) {
        String sql = "select accountUuid,dealWay,type, sum(expend)as expend,sum(income)as income from DealDetailVO where  state = 'SUCCESS' and finishTime between :dateStart and  :dateEnd  group by accountUuid,dealWay,type";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("dateStart", startTime);
        q.setParameter("dateEnd", endTime);
        List<Object[]> objs = q.getResultList();
        return  objs.stream().map(BillStatistics::new).collect(Collectors.toList());
    }


    public Timestamp getLastMonthFirstDay(Timestamp currentTimestamp){
//        LocalDateTime lastDayOfMonth = currentTimestamp.toLocalDateTime().minusMonths(1);
        LocalDateTime lastDayOfMonth = currentTimestamp.toLocalDateTime().minusDays(1);
//        LocalDate date = LocalDate.of(lastDayOfMonth.getYear(), lastDayOfMonth.getMonth(), 1);
        LocalDate date = LocalDate.of(lastDayOfMonth.getYear(), lastDayOfMonth.getMonth(), lastDayOfMonth.getDayOfMonth());
        LocalDateTime lastMonthFirstDay = LocalDateTime.of(date, LocalTime.MIN);

        return Timestamp.valueOf(lastMonthFirstDay);
    }

    public Timestamp getLastMonthLastDay(Timestamp currentTimestamp){
//        LocalDateTime lastDayOfMonth = currentTimestamp.toLocalDateTime().minusMonths(1);
        LocalDateTime lastDayOfMonth = currentTimestamp.toLocalDateTime().minusDays(1);
//        LocalDate date = LocalDate.of(lastDayOfMonth.getYear(), lastDayOfMonth.getMonth(), 1);
        LocalDate date = LocalDate.of(lastDayOfMonth.getYear(), lastDayOfMonth.getMonth(), lastDayOfMonth.getDayOfMonth());
        LocalDateTime lastMonthFirstDay = LocalDateTime.of(date, LocalTime.MIN);

//        return Timestamp.valueOf(lastMonthFirstDay.plusMonths(1).minusNanos(1));
        return Timestamp.valueOf(lastMonthFirstDay.plusDays(1).minusNanos(1));
    }



    private void calculateBalance(BillStatistics bill, BillVO bVO) {
        if (bill.getDealWay().equals(DealWay.PRESENT_BILL)) {
            if(bill.getType()== DealType.DEDUCTION){
                bVO.setTotalDeductionPayPresent(bill.getExpend());
            }
            if(bill.getType()== DealType.PROXY_RECHARGE){
                bVO.setTotalRechargeIncomePresent(bill.getIncome());
            }
            if(bill.getType()== DealType.REFUND){
                bVO.setTotalRefundIncomePresent(bill.getIncome());
            }

        } else if (bill.getDealWay().equals(DealWay.CASH_BILL)) {
            if(bill.getType()== DealType.DEDUCTION){
                bVO.setTotalDeductionPayCash(bill.getExpend());
            }
            if(bill.getType()== DealType.PROXY_RECHARGE || bill.getType() == DealType.RECHARGE){
                bVO.setTotalRechargeIncomeCash((bVO.getTotalRechargeIncomeCash()==null?BigDecimal.ZERO:bVO.getTotalRechargeIncomeCash()).add(bill.getIncome()));
            }
            if(bill.getType()== DealType.REFUND){
                bVO.setTotalRefundIncomeCash(bill.getIncome());
            }
        }
    }

}
