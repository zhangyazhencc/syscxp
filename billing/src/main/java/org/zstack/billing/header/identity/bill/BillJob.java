package org.zstack.billing.header.identity.bill;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.zstack.billing.header.identity.balance.AccountBalanceVO;
import org.zstack.billing.header.identity.balance.DealWay;
import org.zstack.billing.header.identity.balance.ExpenseGross;
import org.zstack.core.Platform;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BillJob extends QuartzJobBean {

    private DatabaseFacade databaseFacade;

    private static final CLogger logger = Utils.getLogger(BillJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Timestamp currentTimestamp = databaseFacade.getCurrentSqlTime();

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(currentTimestamp);
        calendar1.set(Calendar.HOUR_OF_DAY,0);
        calendar1.set(Calendar.MINUTE,0);
        calendar1.set(Calendar.SECOND,0);
        calendar1.set(Calendar.MILLISECOND,0);
        calendar1.add(Calendar.MONTH, -1);
        calendar1.set(Calendar.DAY_OF_MONTH,1);
        calendar1.set(calendar1.get(Calendar.YEAR),calendar1.get(Calendar.MONTH),calendar1.get(Calendar.DAY_OF_MONTH),0,0,0);
        Timestamp startTime =  new Timestamp(calendar1.getTime().getTime());
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.DAY_OF_MONTH,0);
        calendar2.set(Calendar.HOUR_OF_DAY,23);
        calendar2.set(Calendar.MINUTE,59);
        calendar2.set(Calendar.SECOND,59);
        calendar2.set(Calendar.MILLISECOND,999);
        Timestamp endTime =  new Timestamp(calendar2.getTime().getTime());

        String sql = "select accountUuid,dealWay, sum(expend)as expend,sum(income)as income from DealDetailVO where  state = 'SUCCESS' and finishTime between :dateStart and  :dateEnd  group by accountUuid,dealWay";
        Query q =  databaseFacade.getEntityManager().createNativeQuery(sql);
        q.setParameter("dateStart", startTime);
        q.setParameter("dateEnd", endTime);
        List<Object[]> objs  = q.getResultList();
        List<BillStatistics> bills = objs.stream().map(BillStatistics::new).collect(Collectors.toList());
        Map<String,String> map = new HashMap<>();
        for(BillStatistics bill : bills){
            String accountUuid = bill.getAccountUuid();
            if(map.get(accountUuid)!=null){
                String uuid =  map.get(accountUuid);
                BillVO bVO = databaseFacade.findByUuid(uuid,BillVO.class);
                caculateBalance(bill, bVO);
                databaseFacade.updateAndRefresh(bVO);

            }else{
                String uuid = Platform.getUuid();
                map.put(accountUuid,uuid);
                BillVO vo = new BillVO();
                vo.setUuid(uuid);
                vo.setAccountUuid(accountUuid);
                vo.setBillDate(currentTimestamp);
                vo.setTimeStart(startTime);
                vo.setTimeEnd(endTime);
                caculateBalance(bill, vo);
                AccountBalanceVO abVO = databaseFacade.findByUuid(accountUuid,AccountBalanceVO.class);
                BigDecimal balance = abVO.getCashBalance();
                if(balance.compareTo(BigDecimal.ZERO)<0){
                    vo.setRepay(balance);
                    vo.setCashBalance(BigDecimal.ZERO);
                }else{
                    vo.setRepay(BigDecimal.ZERO);
                    vo.setCashBalance(balance);
                }
                databaseFacade.persistAndRefresh(vo);
                //todo here send message to user

            }

        }
    }

    private void caculateBalance(BillStatistics bill, BillVO bVO) {
        if(bill.getDealWay().equals(DealWay.BALANCE_BILL)){
            bVO.setTotalPayPresent(bill.getExpend());
            bVO.setTotalIncomePresent(bill.getIncome());
        }else if(bill.getDealWay().equals(DealWay.CASH_BILL)){
            bVO.setTotalIncomeCash(bill.getIncome());
            bVO.setTotolPayCash(bill.getExpend());
        }
    }

    public DatabaseFacade getDatabaseFacade() {
        return databaseFacade;
    }

    public void setDatabaseFacade(DatabaseFacade databaseFacade) {
        this.databaseFacade = databaseFacade;
    }
}
