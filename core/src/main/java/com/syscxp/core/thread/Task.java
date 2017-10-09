package com.syscxp.core.thread;

import com.syscxp.header.HasThreadContext;

import java.util.concurrent.Callable;

public interface Task<T> extends Callable<T>, HasThreadContext {
    String getName();
}
