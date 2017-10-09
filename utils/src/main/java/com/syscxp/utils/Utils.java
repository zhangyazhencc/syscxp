package com.syscxp.utils;

import com.syscxp.utils.data.ArraySpliter;
import com.syscxp.utils.data.ArraySpliterImpl;
import com.syscxp.utils.data.FieldPrinter;
import com.syscxp.utils.data.FieldPrinterImpl;
import com.syscxp.utils.filelocater.FileLocator;
import com.syscxp.utils.filelocater.FileLocatorImpl;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.logging.CLoggerImpl;
import com.syscxp.utils.path.PathUtilImpl;
import com.syscxp.utils.path.PathUtils;
import com.syscxp.utils.stopwatch.StopWatch;
import com.syscxp.utils.stopwatch.StopWatchImpl;

public class Utils {
	private static ArraySpliter arraySpliter = null;
	private static FieldPrinter fieldPrinter = null;
	private static PathUtils pathUtil = null;
	
	static {
		arraySpliter = new ArraySpliterImpl();
		fieldPrinter = new FieldPrinterImpl();
		pathUtil = new PathUtilImpl();
	}
	
    public static StopWatch getStopWatch() {
        return new StopWatchImpl();
    }
    
    public static CLogger getLogger(String className) {
        return CLoggerImpl.getLogger(className);
     }
     
    public static CLogger getLogger(Class<?> clazz) {
        return CLoggerImpl.getLogger(clazz);
    }
    
    public static FileLocator createFileLocator() {
        return new FileLocatorImpl();
    }
    
    public static ArraySpliter getArraySpliter() {
    	return arraySpliter;
    }
    
    public static FieldPrinter getFieldPrinter() {
    	return fieldPrinter;
    }
    
    public static PathUtils getPathUtil() {
    	return pathUtil;
    }
}
