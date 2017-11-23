package com.syscxp.core.identity;

import com.syscxp.core.db.SQL;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.quota.QuotaVO;
import com.syscxp.header.quota.QuotaVO_;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.identity.*;

import javax.persistence.Tuple;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class QuotaUtil {
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private DatabaseFacade dbf;

    public static class QuotaCompareInfo {
        public String currentAccountUuid;
        public String ownerAccountUuid;
        public String quotaName;
        public long quotaValue;
        public long currentUsed;
        public long request;
    }

    @Transactional(readOnly = true)
    public String getResourceOwnerAccountUuid(String resourceUuid, String resourceType) {
        String owner = SQL.New(String.format("select r.accountUuid from %s r where r.uuid = :resourceUuid ",
                resourceType), String.class).find();

        if (owner == null || owner.equals("")) {
            throw new CloudRuntimeException(
                    String.format("cannot find owner account uuid for resource[uuid:%s]", resourceUuid));
        } else {
            return owner;
        }
    }

    public void CheckQuota(QuotaCompareInfo quotaCompareInfo) {
        if (quotaCompareInfo.currentUsed + quotaCompareInfo.request > quotaCompareInfo.quotaValue) {
            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.QUOTA_EXCEEDING,
                    String.format("quota exceeding. Current account is [uuid: %s]. " +
                                    "The resource target owner account[uuid: %s] exceeds a quota[name: %s, value: %s], " +
                                    "Current used:%s, Request:%s. ",
                            quotaCompareInfo.currentAccountUuid, quotaCompareInfo.ownerAccountUuid,
                            quotaCompareInfo.quotaName, quotaCompareInfo.quotaValue,
                            quotaCompareInfo.currentUsed, quotaCompareInfo.request)
            ));
        }
    }

    public void CheckQuota(String quotaName, long currentUsed, long quotaValue) {
        if (currentUsed + 1 > quotaValue) {
            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.QUOTA_EXCEEDING,
                    String.format("The resource  exceeds a quota[name: %s, value: %s], Current used:%s, Request:1. ",
                            quotaName, quotaValue, currentUsed)
            ));
        }
    }

    public Map<String, Quota.QuotaPair> makeQuotaPairs(String accountUuid) {
        SimpleQuery<QuotaVO> q = dbf.createQuery(QuotaVO.class);
        q.select(QuotaVO_.name, QuotaVO_.value);
        q.add(QuotaVO_.identityUuid, SimpleQuery.Op.EQ, accountUuid);
        List<Tuple> ts = q.listTuple();

        Map<String, Quota.QuotaPair> pairs = new HashMap<>();
        for (Tuple t : ts) {
            String name = t.get(0, String.class);
            long value = t.get(1, Long.class);
            Quota.QuotaPair p = new Quota.QuotaPair();
            p.setName(name);
            p.setValue(value);
            pairs.put(name, p);
        }

        return pairs;
    }

}
