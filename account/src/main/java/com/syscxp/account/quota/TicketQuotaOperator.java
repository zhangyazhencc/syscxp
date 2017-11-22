package com.syscxp.account.quota;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.ticket.APICreateTicketMsg;
import com.syscxp.account.header.ticket.APICreateTicketRecordMsg;
import com.syscxp.account.header.ticket.TicketVO;
import com.syscxp.account.header.ticket.TicketVO_;
import com.syscxp.core.db.Q;
import com.syscxp.core.identity.QuotaUtil;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.NeedQuotaCheckMessage;
import com.syscxp.header.quota.Quota;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TicketQuotaOperator implements Quota.QuotaOperator {

    public class TicketQuota {
        public long ticketNum;
        public long ticketNoSessionNum;
        public long ticketRecordNum;
    }

    @Override
    public void checkQuota(APIMessage msg, Map<String, Quota.QuotaPair> pairs) {
        if (msg instanceof APICreateTicketMsg) {
            check((APICreateTicketMsg) msg, pairs);
        } else if (msg instanceof APICreateTicketRecordMsg) {
            check((APICreateTicketRecordMsg) msg, pairs);
        }
    }

    @Transactional(readOnly = true)
    private void check(APICreateTicketRecordMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        String quotaName = AccountConstant.QUOTA_TICKET_RECORD_NUM;
        long quotaNum = pairs.get(quotaName).getValue();
        long currentUsed = getUsedTicketNum(currentAccountUuid);

        checkQuota(currentAccountUuid, currentAccountUuid, quotaName, quotaNum, currentUsed);
    }

    @Transactional(readOnly = true)
    private void check(APICreateTicketMsg msg, Map<String, Quota.QuotaPair> pairs) {
        String currentAccountUuid = "system";
        if (msg.getSession() != null)
            currentAccountUuid = msg.getSession().getAccountUuid();
        String quotaName = AccountConstant.QUOTA_TICKET_NUM;
        long quotaNum = pairs.get(quotaName).getValue();
        long currentUsed = getUsedTicketNum(currentAccountUuid);

        checkQuota(currentAccountUuid, currentAccountUuid, quotaName, quotaNum, currentUsed);
    }

    private void checkQuota(String currentAccountUuid, String ownerAccountUuid, String quotaName, long quotaNum, long currentUsed) {
        long askedNum = 1;
        QuotaUtil.QuotaCompareInfo quotaCompareInfo;
        {
            quotaCompareInfo = new QuotaUtil.QuotaCompareInfo();
            quotaCompareInfo.currentAccountUuid = currentAccountUuid;
            quotaCompareInfo.ownerAccountUuid = ownerAccountUuid;
            quotaCompareInfo.quotaName = quotaName;
            quotaCompareInfo.quotaValue = quotaNum;
            quotaCompareInfo.currentUsed = currentUsed;
            quotaCompareInfo.request = askedNum;
            new QuotaUtil().CheckQuota(quotaCompareInfo);
        }
    }

    @Transactional(readOnly = true)
    public TicketQuota getUsedTicket(String accountUUid) {
        TicketQuota quota = new TicketQuota();
        quota.ticketNum = getUsedTicketNum(accountUUid);
        quota.ticketRecordNum = getUsedTicketRecordNum(accountUUid);
        quota.ticketNoSessionNum = getUsedTicketNum("system");
        return quota;
    }

    @Transactional(readOnly = true)
    public long getUsedTicketNum(String accountUuid) {
        LocalDateTime dateTime =
                LocalDate.now().atTime(LocalTime.MIN);
        Q q = Q.New(TicketVO.class)
                .gte(TicketVO_.createDate, Timestamp.valueOf(dateTime))
                .lt(TicketVO_.createDate, Timestamp.valueOf(dateTime.plusDays(1)));
        if (!"system".equals(accountUuid))
            q = q.eq(TicketVO_.accountUuid, accountUuid);
        else
            q = q.isNull(TicketVO_.accountUuid);
        Long num = q.count();
        return num == null ? 0 : num;
    }

    @Transactional(readOnly = true)
    public long getUsedTicketRecordNum(String accountUuid) {
        Long num = Q.New(TicketVO.class)
                .eq(TicketVO_.accountUuid, accountUuid)
                .count();
        return num == null ? 0 : num;
    }

    @Override
    public void checkQuota(NeedQuotaCheckMessage msg, Map<String, Quota.QuotaPair> pairs) {

    }

    @Override
    public List<Quota.QuotaUsage> getQuotaUsageByAccount(String accountUuid) {
        List<Quota.QuotaUsage> usages = new ArrayList<>();

        TicketQuota quota = getUsedTicket(accountUuid);

        Quota.QuotaUsage usage = new Quota.QuotaUsage();
        usage.setName(AccountConstant.QUOTA_TICKET_NUM);
        usage.setUsed(quota.ticketNum);
        usages.add(usage);

        usage = new Quota.QuotaUsage();
        usage.setName(AccountConstant.QUOTA_TICKET_NO_SESSION_NUM);
        usage.setUsed(quota.ticketNoSessionNum);
        usages.add(usage);

        usage = new Quota.QuotaUsage();
        usage.setName(AccountConstant.QUOTA_TICKET_RECORD_NUM);
        usage.setUsed(quota.ticketRecordNum);
        usages.add(usage);

        return usages;
    }
}
