package com.syscxp.utils.data;

public interface FieldPrinter {
    String print(Object obj);
	
	String print(Object obj, boolean recursive);
}
