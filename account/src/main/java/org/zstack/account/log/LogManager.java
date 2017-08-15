package org.zstack.account.log;

public interface LogManager {

    void send(OperLogBuilder builder);
}
