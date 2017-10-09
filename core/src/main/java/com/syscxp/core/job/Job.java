package com.syscxp.core.job;

import com.syscxp.header.core.ReturnValueCompletion;

import java.io.Serializable;

public interface Job extends Serializable {
    void run(ReturnValueCompletion<Object> completion);
}
