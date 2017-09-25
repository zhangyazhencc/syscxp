package org.zstack.billing.header.bill;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.header.billing.AccountBalanceVO;
import org.zstack.billing.header.balance.DealWay;
import org.zstack.core.Platform;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.GLock;
import org.zstack.core.db.SimpleQuery;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BillJob extends QuartzJobBean {

    private DatabaseFacade databaseFacade;
    private ThreadLocal<DatabaseFacade> connThreadLocal = new ThreadLocal<DatabaseFacade>();
    private static final CLogger logger = Utils.getLogger(BillJob.class);

    @Transactional
    public void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        GLock lock = new GLock(String.format("id-%s","createBill"), 120);
        lock.lock();
        try {
            Timestamp billTimestamp = databaseFacade.getCurrentSqlTime();
            logger.info(billTimestamp+"########");
            LocalDateTime localDateTime =  billTimestamp.toLocalDateTime();
            LocalDate date = LocalDate.of(localDateTime.getYear(),localDateTime.getMonth(),1);
            LocalTime time = LocalTime.MIN;
            localDateTime =  LocalDateTime.of(date,time);

            Timestamp startTime =  Timestamp.valueOf(localDateTime);
            Timestamp endTime = Timestamp.valueOf(localDateTime.plusMonths(1).minusSeconds(1));

            String sql = "select accountUuid,dealWay, sum(expend)as expend,sum(income)as income from DealDetailVO where  state = 'SUCCESS' and finishTime between :dateStart and  :dateEnd  group by accountUuid,dealWay";
            Query q = databaseFacade.getEntityManager().createNativeQuery(sql);
            q.setParameter("dateStart",startTime);
            q.setParameter("dateEnd", endTime);
            List<Object[]> objs = q.getResultList();
            List<BillStatistics> bills = objs.stream().map(BillStatistics::new).collect(Collectors.toList());
            Map<String, String> map = new HashMap<>();
            for (BillStatistics bill : bills) {
                String accountUuid = bill.getAccountUuid();
                if (map.get(accountUuid) != null) {
                    String uuid = map.get(accountUuid);
                    BillVO bVO = databaseFacade.findByUuid(uuid, BillVO.class);
                    caculateBalance(bill, bVO);
                    databaseFacade.updateAndRefresh(bVO);

                } else {
                    SimpleQuery<BillVO> sq = databaseFacade.createQuery(BillVO.class);
                    sq.add(BillVO_.accountUuid, SimpleQuery.Op.EQ, bill.getAccountUuid());
                    sq.add(BillVO_.timeStart, SimpleQuery.Op.EQ, startTime);
                    sq.add(BillVO_.timeEnd, SimpleQuery.Op.EQ, endTime);
                    BillVO bVO = sq.find();
                    if (bVO == null) {
                        String uuid = Platform.getUuid();
                        map.put(accountUuid, uuid);
                        BillVO vo = new BillVO();
                        vo.setUuid(uuid);
                        vo.setAccountUuid(accountUuid);
                        vo.setBillDate(billTimestamp);
                        vo.setTimeStart(startTime);
                        vo.setTimeEnd(endTime);
                        caculateBalance(bill, vo);
                        AccountBalanceVO abVO = databaseFacade.findByUuid(accountUuid, AccountBalanceVO.class);
                        BigDecimal balance = abVO.getCashBalance();
                        if (balance.compareTo(BigDecimal.ZERO) < 0) {
                            vo.setRepay(balance);
                            vo.setCashBalance(BigDecimal.ZERO);
                        } else {
                            vo.setRepay(BigDecimal.ZERO);
                            vo.setCashBalance(balance);
                        }
                        databaseFacade.persistAndRefresh(vo);
                    }

                }
            }
            //todo here send message to user
        }catch (Exception e){
            JobExecutionException ej = new JobExecutionException(e);
            e.printStackTrace();
            try {
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            ej.setRefireImmediately(true);
            throw ej;
        }
        finally {
            lock.unlock();
        }



    }

    private void caculateBalance(BillStatistics bill, BillVO bVO) {
        if (bill.getDealWay().equals(DealWay.PRESENT_BILL)) {
            bVO.setTotalPayPresent(bill.getExpend()==null?BigDecimal.ZERO:bill.getExpend());
            bVO.setTotalIncomePresent(bill.getIncome()==null?BigDecimal.ZERO:bill.getIncome());
        } else if (bill.getDealWay().equals(DealWay.CASH_BILL)) {
            bVO.setTotalIncomeCash(bill.getIncome()==null?BigDecimal.ZERO:bill.getIncome());
            bVO.setTotolPayCash(bill.getExpend()==null?BigDecimal.ZERO:bill.getExpend());
        }
    }

    public DatabaseFacade getDatabaseFacade() {
        return databaseFacade;
    }

    public void setDatabaseFacade(DatabaseFacade databaseFacade) {
        if (connThreadLocal.get() == null) {
            connThreadLocal.set(databaseFacade);
            this.databaseFacade = databaseFacade;
        } else {
            this.databaseFacade = connThreadLocal.get();
        }
    }

    public ThreadLocal<DatabaseFacade> getConnThreadLocal() {
        return connThreadLocal;
    }

    public void setConnThreadLocal(ThreadLocal<DatabaseFacade> connThreadLocal) {
        this.connThreadLocal = connThreadLocal;
    }
}
