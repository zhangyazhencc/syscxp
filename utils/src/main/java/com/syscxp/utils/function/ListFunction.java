package com.syscxp.utils.function;

import java.util.List;

/**
*/
public interface ListFunction<K, V> {
    List<K> call(V arg);
}
