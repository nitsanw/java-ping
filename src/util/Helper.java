package util;
import java.util.concurrent.locks.LockSupport;

public final class Helper {
    private static final long WAIT_NANOS = Long.getLong("waitNanos",0L) ;

    private static final boolean SHOULD_WAIT =  (WAIT_NANOS != 0L);

    private Helper(){}
    private static final boolean SHOULD_YIELD = Boolean.getBoolean("yield");
    public static void yield(){
        if(SHOULD_YIELD){
            Thread.yield();
        }
    }
    public static void waitSome(){
        if(SHOULD_WAIT){
        	waitSome(WAIT_NANOS);
        }
    }
    public static void waitSome(long nanos){
    	if(nanos < 1000) {
    		long until = System.nanoTime() + nanos - 40;
    		while(System.nanoTime() < until);
    		return;
    	}
    	else {
            LockSupport.parkNanos(nanos);
    	}
    }

}
