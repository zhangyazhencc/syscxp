package com.syscxp.header;

import org.apache.logging.log4j.ThreadContext;
import java.util.*;

public aspect HasThreadContextAspect {
    public Map<String, String> HasThreadContext.threadContext;
    public List<String> HasThreadContext.threadContextStack;

    after(HasThreadContext obj) : target(obj) && execution(com.syscxp.header.HasThreadContext+.new(..)) {
        obj.threadContext = ThreadContext.getContext();
        obj.threadContextStack = ThreadContext.getImmutableStack().asList();
    }
}