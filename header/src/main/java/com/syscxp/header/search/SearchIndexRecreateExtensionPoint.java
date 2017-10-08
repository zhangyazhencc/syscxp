package com.syscxp.header.search;

import java.util.List;


public interface SearchIndexRecreateExtensionPoint {
    List<String> returnUuidToReindex(String inventoryName);
}
