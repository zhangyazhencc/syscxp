package com.syscxp.core.keyvalue;

import com.syscxp.core.db.DatabaseFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.utils.Bucket;
import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.DebugUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.function.Function;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.serializable.SerializableHelper;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class KeyValueQuery<T> {
    private static CLogger logger = Utils.getLogger(KeyValueQuery.class);

    private class Condition {
        String condName;
        Object value;
        String sql;
        String entityName;
    }

    @Autowired
    private DatabaseFacade dbf;

    private T entityProxy;
    private KeyValueEntityProxy<T> proxy;
    private List<Bucket> opAndVals = new ArrayList<>();
    private Class entityClass;
    private int counter;
    private int entityNameCounter;
    private List<Condition> conditions = new ArrayList<>();
    private String sql;

    public KeyValueQuery(Class<T> clz) {
        proxy = new KeyValueEntityProxy<T>(clz);
        entityProxy = proxy.getProxyEntity();
        entityClass = clz;
    }

    private String makeValueName() {
        return String.format("value%s", counter++);
    }

    private String makeEntityName() {
        return String.format("e%s", entityNameCounter++);
    }

    public T entity() {
        return entityProxy;
    }

    public void and(Object _, Op op, Object...vals) {
        opAndVals.add(Bucket.newBucket(op, vals));
    }

    private void done() {
        for (int i=0; i<proxy.getPaths().size(); i++) {
            String key = proxy.getPaths().get(i);
            Bucket b = opAndVals.get(i);
            Op op = b.get(0);
            Object[] vals = b.safeGet(1);

            if (op == Op.IN) {
                DebugUtils.Assert(vals.length > 0, String.format("condition[%s] requires at least one parameter", op));
                condIn(key, vals);
            } else if (op == Op.NOT_IN) {
                DebugUtils.Assert(vals.length > 0, String.format("condition[%s] requires at least one parameter", op));
                condNotIn(key, vals);
            } else if (op == Op.NULL) {
                condNull(key);
            } else if (op == Op.NOT_NULL) {
                condNotNull(key);
            } else  {
                DebugUtils.Assert(vals.length == 1, String.format("condition[%s] requires one parameter", op));
                Condition c = new Condition();
                c.condName = makeValueName();
                c.entityName = makeEntityName();
                c.sql = String.format("(%s.entityKey like '%s' and %s.entityValue %s :%s)", c.entityName, key, c.entityName, op, c.condName);
                DebugUtils.Assert(vals[0] != null, String.format("condition[%s] doesn't support NULL value", op));
                c.value = vals[0].toString();
                conditions.add(c);
            }
        }

        List<String> conds = new ArrayList<>(opAndVals.size());
        List<String> entityNameList = new ArrayList<>();
        List<String> joinConds = new ArrayList<>();
        for (Condition c : conditions) {
            conds.add(c.sql);
            entityNameList.add(String.format("KeyValueVO %s", c.entityName));
            joinConds.add(String.format("e.uuid = %s.uuid", c.entityName));
        }

        sql = "select distinct e.uuid from KeyValueVO e, %s where e.className = :clz and %s and %s";
        sql = String.format(sql, StringUtils.join(entityNameList, ","), StringUtils.join(joinConds, " and "), StringUtils.join(conds, " and "));

        if (logger.isTraceEnabled()) {
            logger.trace(sql);
        }
    }

    @Transactional(readOnly = true)
    private List<KeyValueBinaryVO> listBinaryVO() {
        TypedQuery<String> q = dbf.getEntityManager().createQuery(sql, String.class);
        q.setParameter("clz", entityClass.getName());
        for (Condition c : conditions) {
            if (c.condName != null) {
                q.setParameter(c.condName, c.value);
            }
        }
        List<String> uuids = q.getResultList();
        if (uuids.isEmpty()) {
            return new ArrayList<>();
        }

        TypedQuery<KeyValueBinaryVO> bq = dbf.getEntityManager().createQuery("select e from KeyValueBinaryVO e where e.uuid in (:uuids)", KeyValueBinaryVO.class);
        bq.setParameter("uuids", uuids);
        return bq.getResultList();
    }

    private List<T> listObject() {
        done();
        List<KeyValueBinaryVO> vos = listBinaryVO();
        if (vos.isEmpty()) {
            return new ArrayList<T>();
        }

        return CollectionUtils.transformToList(vos, (Function<T, KeyValueBinaryVO>) arg -> {
            try {
                return SerializableHelper.readObject(arg.getContents());
            } catch (Exception e) {
                throw new CloudRuntimeException(e);
            }
        });
    }

    private void condNotNull(String key) {
        Condition c = new Condition();
        c.entityName = makeEntityName();
        c.sql = String.format("(%s.entityKey like '%s')", c.entityName, key);
        conditions.add(c);
    }

    private void condNull(String key) {
        Condition c = new Condition();
        c.entityName = makeEntityName();
        c.sql = String.format("(%s.uuid not in (select %s_.uuid from KeyValueVO %s_ where %s_.entityKey like '%s'))",
                c.entityName, c.entityName, c.entityName, c.entityName, key);
        conditions.add(c);
    }

    private void condNotIn(String key, Object[] vals) {
        List<String> values = new ArrayList<>(vals.length);
        for (Object v : vals) {
            DebugUtils.Assert(v!=null, "values for condition 'NOT IN' cannot be null");
            values.add(v.toString());
        }

        Condition c = new Condition();
        c.condName = makeValueName();
        c.value = values;
        c.entityName = makeEntityName();
        c.sql = String.format("(%s.entityKey like '%s' and %s.entityValue not in (:%s))", c.entityName, key, c.entityName, c.condName);
        conditions.add(c);
    }

    private void condIn(String key, Object[] vals) {
        List<String> values = new ArrayList<>(vals.length);
        for (Object v : vals) {
            DebugUtils.Assert(v!=null, "values for condition 'IN' cannot be null");
            values.add(v.toString());
        }

        Condition c = new Condition();
        c.condName = makeValueName();
        c.entityName = makeEntityName();
        c.value = values;
        c.sql = String.format("(%s.entityKey like '%s' and %s.entityValue in (:%s))", c.entityName, key, c.entityName, c.condName);
        conditions.add(c);
    }

    public List<T> list() {
        return listObject();
    }

    public T find() {
        List<T> lst = listObject();
        if (lst.isEmpty()) {
            return null;
        }

        DebugUtils.Assert(lst.size() == 1, String.format("find expects only one result, but %s got", lst.size()));
        return lst.get(0);
    }
}
