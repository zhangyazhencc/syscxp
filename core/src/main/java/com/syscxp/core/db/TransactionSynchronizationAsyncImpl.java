package com.syscxp.core.db;

import com.syscxp.core.thread.AsyncThread;
import org.springframework.transaction.support.TransactionSynchronization;

public class TransactionSynchronizationAsyncImpl implements TransactionSynchronization {
    private final Class<?>[] clazzs;
    private final TransactionalCallback callback;
    private final TransactionalCallback.Operation op;
    
    TransactionSynchronizationAsyncImpl(TransactionalCallback cb, TransactionalCallback.Operation op, Class<?>...clazzs) {
        this.clazzs = clazzs;
        this.callback = cb;
        this.op = op;
    }
    
    @Override
    @AsyncThread
    public void suspend() {
        callback.suspend(clazzs);
    }

    @Override
    @AsyncThread
    public void resume() {
        callback.resume(clazzs);
    }

    @Override
    @AsyncThread
    public void flush() {
        callback.flush(clazzs);
    }

    @Override
    @AsyncThread
    public void beforeCommit(boolean readOnly) {
        callback.beforeCommit(op, readOnly, clazzs);
    }

    @Override
    @AsyncThread
    public void beforeCompletion() {
        callback.beforeCompletion(op, clazzs);
    }

    @Override
    @AsyncThread
    public void afterCommit() {
        callback.afterCommit(op, clazzs);
    }

    @Override
    @AsyncThread
    public void afterCompletion(int status) {
        callback.afterCompletion(op, status, clazzs);
    }
}
