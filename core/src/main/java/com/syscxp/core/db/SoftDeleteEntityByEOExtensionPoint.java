package com.syscxp.core.db;

import java.util.Collection;
import java.util.List;

/**
 */
public interface SoftDeleteEntityByEOExtensionPoint {
    List<Class> getEOClassForSoftDeleteEntityExtension();

    void postSoftDelete(Collection entityIds, Class EOClass);
}
