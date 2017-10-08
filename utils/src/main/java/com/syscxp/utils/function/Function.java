package com.syscxp.utils.function;

/**
*/
public interface Function<K, V> {
    K call(V arg);
}
