package com.syscxp.core.db;

import com.syscxp.core.db.TransactionalCallback.Operation;
import com.syscxp.header.message.APIListMessage;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface DatabaseFacade extends Serializable {
    EntityManager getEntityManager();
    
    <T> T findById(long id, Class<T> entityClass);
    
    <T> T findByUuid(String uuid, Class<T> entityClass);

    <T> T find(Query q);
    
    <T> T persist(T entity);

    void persistCollection(Collection entities);
    
    <T> T persistAndRefresh(T entity);
    
    <T> void update(T entity);
    
    <T> T updateAndRefresh(T entity);
    
    <T> T reload(T entity);

    void updateCollection(Collection entities);

    void remove(Object entity);

    void removeCollection(Collection entities, Class entityClazz);

    void removeByPrimaryKeys(Collection priKeys, Class entityClazz);

    void removeByPrimaryKey(Object primaryKey, Class<?> entityClass);

    void hardDeleteCollectionSelectedBySQL(String sql, Class entityClass);

    CriteriaBuilder  getCriteriaBuilder();
    
    <T> SimpleQuery<T> createQuery(Class<T> entityClass);

    long count(Class<?> entityClass);
    
    void entityForTranscationCallback(Operation op, Class<?>...entityClass);
    
    long generateSequenceNumber(Class<?> seqTable);

    <T> List<T> listAll(Class<T> clazz);
    
    <T> List<T> listAll(int offset, int length, Class<T> clazz);
    
    <T> List<T> listByPrimaryKeys(Collection priKeys, Class<T> clazz);
    
    <T> List<T> listByPrimaryKeys(Collection priKeys, int offset, int length, Class<T> clazz);

    <T> List<T> listByApiMessage(APIListMessage msg, Class<T> clazz);
    
    boolean isExist(Object id, Class<?> clazz);

    void eoCleanup(Class VOClazz);

    DataSource getDataSource();

    DataSource getExtraDataSource();

    Timestamp getCurrentSqlTime();

    String getDbVersion();

    void installEntityLifeCycleCallback(Class entityClass, EntityEvent evt, EntityLifeCycleCallback cb);
}
