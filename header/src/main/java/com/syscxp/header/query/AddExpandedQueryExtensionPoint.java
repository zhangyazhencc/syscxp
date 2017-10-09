package com.syscxp.header.query;

import java.util.List;

/**
 */
public interface AddExpandedQueryExtensionPoint {
    List<ExpandedQueryStruct> getExpandedQueryStructs();

    List<ExpandedQueryAliasStruct> getExpandedQueryAliasesStructs();
}
