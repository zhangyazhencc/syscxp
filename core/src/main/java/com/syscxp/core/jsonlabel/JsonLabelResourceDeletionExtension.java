package com.syscxp.core.jsonlabel;

import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.thread.AsyncThread;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.db.HardDeleteEntityExtensionPoint;
import com.syscxp.core.db.SoftDeleteEntityExtensionPoint;
import com.syscxp.core.db.UpdateQuery;

import java.util.Collection;
import java.util.List;

/**
 * Created by xing5 on 2016/9/14.
 */
public class JsonLabelResourceDeletionExtension implements SoftDeleteEntityExtensionPoint,
        HardDeleteEntityExtensionPoint {

    @Autowired
    private DatabaseFacade dbf;

    @Override
    public List<Class> getEntityClassForHardDeleteEntityExtension() {
        // hook all
        return null;
    }

    @AsyncThread
    private void delete(Collection ids) {
        // the resourceUuid must be in type of String
        if (!(ids.iterator().next() instanceof  String)) {
            return;
        }

        UpdateQuery q = UpdateQuery.New(JsonLabelVO.class);
        q.condAnd(JsonLabelVO_.resourceUuid, SimpleQuery.Op.IN, ids);
        q.delete();
    }

    @Override
    public void postHardDelete(Collection entityIds, Class entityClass) {
        if (entityClass.isAssignableFrom(JsonLabelVO.class)) {
            return;
        }

        if (!entityIds.isEmpty()) {
            delete(entityIds);
        }
    }

    @Override
    public List<Class> getEntityClassForSoftDeleteEntityExtension() {
        // hook all
        return null;
    }

    @Override
    public void postSoftDelete(Collection entityIds, Class entityClass) {
        if (entityClass.isAssignableFrom(JsonLabelVO.class)) {
            return;
        }

        if (!entityIds.isEmpty()) {
            delete(entityIds);
        }
    }
}
