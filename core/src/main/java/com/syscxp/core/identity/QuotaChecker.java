package com.syscxp.core.identity;

import com.syscxp.core.Platform;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.config.GlobalConfigFacade;
import com.syscxp.core.config.GlobalConfigVO;
import com.syscxp.core.config.GlobalConfigVO_;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.quota.*;
import com.syscxp.utils.DebugUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.GlobalApiMessageInterceptor;
import com.syscxp.header.quota.Quota.QuotaPair;
import com.syscxp.header.message.APIMessage;

import java.util.*;

public class QuotaChecker implements GlobalApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(QuotaChecker.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private GlobalConfigFacade gcf;
    @Autowired
    protected PluginRegistry pluginRgty;

    private Map<Class, List<Quota>> messageQuotaMap = new HashMap<>();
    private Map<String, Quota> nameQuotaMap = new HashMap<>();
    private List<Quota> definedQuotas = new ArrayList<>();

    public void init() {
        logger.debug("QuotaChecker init.");
        try {
            collectDefaultQuota();
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
    }

    @Override
    public List<Class> getMessageClassToIntercept() {
        return null;
    }

    @Override
    public InterceptorPosition getPosition() {
        return InterceptorPosition.END;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        // login, logout
        if (msg.getSession() == null) {
            return msg;
        }

        // skip admin. if needed, another quota check will be issued in AccountManagerImpl
        if (msg.getSession().isAdminSession()) {
            return msg;
        }

        List<Quota> quotas = getMessageQuotaMap().get(msg.getClass());
        if (quotas == null || quotas.size() == 0) {
            return msg;
        }
        for (Quota q : quotas) {
            check(msg, q);
        }
        return msg;
    }

    private void check(APIMessage msg, Quota quota) {
        // 暂时不做每个账户的单独限额

        // Map<String, QuotaPair> pairs = new QuotaUtil().makeQuotaPairs(msg.getSession().getAccountUuid());
        // 查询默认配置
        List<GlobalConfigVO> vos = Q.New(GlobalConfigVO.class)
                .eq(GlobalConfigVO_.category, QuotaConstant.QUOTA_GLOBAL_CONFIG_CATETORY).list();

        Map<String, QuotaPair> pairs = new HashMap<>();
        for (GlobalConfigVO vo:vos){
            QuotaPair pair = new QuotaPair();
            pair.setName(vo.getName());
            pair.setValue(Long.valueOf(vo.getValue()));
        }

        quota.getOperator().checkQuota(msg, pairs);
        if (quota.getQuotaValidators() != null) {
            for (Quota.QuotaValidator q : quota.getQuotaValidators()) {
                q.checkQuota(msg, pairs);
            }
        }
    }


    public Map<Class, List<Quota>> getMessageQuotaMap() {
        return messageQuotaMap;
    }

    public List<Quota> getQuotas() {
        return definedQuotas;
    }

    private void collectDefaultQuota() {
        Map<String, Long> defaultQuota = new HashMap<>();

        // Add quota and quota checker
        for (ReportQuotaExtensionPoint ext : pluginRgty.getExtensionList(ReportQuotaExtensionPoint.class)) {
            List<Quota> quotas = ext.reportQuota();
            DebugUtils.Assert(quotas != null, String.format("%s.getQuotaPairs() returns null", ext.getClass()));

            definedQuotas.addAll(quotas);

            for (Quota quota : quotas) {
                DebugUtils.Assert(quota.getQuotaPairs() != null,
                        String.format("%s reports a quota containing a null quotaPairs", ext.getClass()));

                for (QuotaPair p : quota.getQuotaPairs()) {
                    if (defaultQuota.containsKey(p.getName())) {
                        throw new CloudRuntimeException(String.format("duplicate DefaultQuota[resourceType: %s] reported by %s", p.getName(), ext.getClass()));
                    }

                    defaultQuota.put(p.getName(), p.getValue());
                    nameQuotaMap.put(p.getName(), quota);
                }

                for (Class clz : quota.getMessagesNeedValidation()) {
                    if (messageQuotaMap.containsKey(clz)) {
                        messageQuotaMap.get(clz).add(quota);
                    } else {
                        ArrayList<Quota> quotaArrayList = new ArrayList<>();
                        quotaArrayList.add(quota);
                        messageQuotaMap.put(clz, quotaArrayList);
                    }

                }
            }
        }

        // Add additional quota checker to quota
        for (RegisterQuotaCheckerExtensionPoint ext : pluginRgty.getExtensionList(RegisterQuotaCheckerExtensionPoint.class)) {
            // Map<quota name,Set<QuotaValidator>>
            Map<String, Set<Quota.QuotaValidator>> m = ext.registerQuotaValidator();
            for (Map.Entry<String, Set<Quota.QuotaValidator>> entry : m.entrySet()) {
                Quota quota = nameQuotaMap.get(entry.getKey());
                quota.addQuotaValidators(entry.getValue());
                for (Quota.QuotaValidator q : entry.getValue()) {
                    for (Class clz : q.getMessagesNeedValidation()) {
                        if (messageQuotaMap.containsKey(clz)) {
                            messageQuotaMap.get(clz).add(quota);
                        } else {
                            ArrayList<Quota> quotaArrayList = new ArrayList<>();
                            quotaArrayList.add(quota);
                            messageQuotaMap.put(clz, quotaArrayList);
                        }
                    }
                }
            }

        }

        // complete default quota
        SimpleQuery<GlobalConfigVO> q = dbf.createQuery(GlobalConfigVO.class);
        q.select(GlobalConfigVO_.name);
        q.add(GlobalConfigVO_.category, SimpleQuery.Op.EQ, QuotaConstant.QUOTA_GLOBAL_CONFIG_CATETORY);
        List<String> existingQuota = q.listValue();

        List<GlobalConfigVO> quotaConfigs = new ArrayList<>();
        for (Map.Entry<String, Long> e : defaultQuota.entrySet()) {
            String rtype = e.getKey();
            Long value = e.getValue();
            if (existingQuota.contains(rtype)) {
                continue;
            }

            GlobalConfigVO g = new GlobalConfigVO();
            g.setCategory(QuotaConstant.QUOTA_GLOBAL_CONFIG_CATETORY);
            g.setDefaultValue(value.toString());
            g.setValue(g.getDefaultValue());
            g.setName(rtype);
            g.setDescription(String.format("default quota for %s", rtype));
            quotaConfigs.add(g);

            if (logger.isTraceEnabled()) {
                logger.trace(String.format("create default quota[name: %s, value: %s] global config", rtype, value));
            }
        }

        for (GlobalConfigVO vo : quotaConfigs) {
            gcf.createGlobalConfig(vo);
        }

        // 完善每个账户的配额
//        repairAccountQuota(defaultQuota);
    }

    private void repairAccountQuota(Map<String, Long> defaultQuota) {
//        SimpleQuery<AccountVO> queryAccounts = dbf.createQuery(AccountVO.class);
//        queryAccounts.select(AccountVO_.uuid);
//        queryAccounts.add(AccountVO_.type, SimpleQuery.Op.EQ, AccountType.Normal);
//        List<String> normalAccounts = queryAccounts.listValue();
        List<String> normalAccounts = new ArrayList<>();

        List<QuotaVO> quotas = new ArrayList<>();
        for (String nA : normalAccounts) {
            SimpleQuery<QuotaVO> queryAccountQuotas = dbf.createQuery(QuotaVO.class);
            queryAccountQuotas.select(QuotaVO_.name);
            queryAccountQuotas.add(QuotaVO_.identityUuid, SimpleQuery.Op.EQ, nA);
            List<String> existingQuota = queryAccountQuotas.listValue();


            for (Map.Entry<String, Long> e : defaultQuota.entrySet()) {
                String rtype = e.getKey();
                Long value = e.getValue();
                if (existingQuota.contains(rtype)) {
                    continue;
                }

                QuotaVO q = new QuotaVO();
                q.setUuid(Platform.getUuid());
                q.setName(rtype);
                q.setIdentityUuid(nA);
//                q.setIdentityType(AccountVO.class.getSimpleName());
                q.setValue(value);
                quotas.add(q);

                if (logger.isTraceEnabled()) {
                    logger.trace(String.format("create default quota[name: %s, value: %s] global config", rtype, value));
                }
            }
        }

        if (!quotas.isEmpty()) {
            dbf.persistCollection(quotas);
        }
    }

}