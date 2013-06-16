package psy.lob.saw.counters;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import psy.lob.saw.util.UnsafeAccess;

public class ThreadLocalCounter {
    private static final long VALUE_OFFSET;

    static {
        try {
            VALUE_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(AtomicLong.class.getDeclaredField("value"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private final AtomicLong deadThreadSum = new AtomicLong(); 
    static class ThreadAtomicLong extends AtomicLong{
        final Thread t = Thread.currentThread();
    }
    public static long plainGet(AtomicLong al){
        return UnsafeAccess.UNSAFE.getLong(al, VALUE_OFFSET);
    }
    private final CopyOnWriteArrayList<ThreadAtomicLong> counters = new CopyOnWriteArrayList<ThreadAtomicLong>();
    private final ThreadLocal<ThreadAtomicLong> tlc = new ThreadLocal<ThreadAtomicLong>(){
        @Override
        protected ThreadAtomicLong initialValue() {
            ThreadAtomicLong lc = new ThreadAtomicLong();
            counters.add(lc);
            for(ThreadAtomicLong tal: counters){
                if(!tal.t.isAlive()){
                    deadThreadSum.addAndGet(tal.get());
                    counters.remove(tal);
                }
            }
            return lc;
        }
    };
    public void increment() {
        AtomicLong lc = tlc.get();
        lc.lazySet(plainGet(lc) + 1);
    }
    public long get(){
        long dts;
        long sum;
        do {
            dts = deadThreadSum.get();
            sum = 0;
            for(AtomicLong lc:counters){
                sum += lc.get();
            }
        } while(dts != deadThreadSum.get());
        return sum + dts;
    }

}
