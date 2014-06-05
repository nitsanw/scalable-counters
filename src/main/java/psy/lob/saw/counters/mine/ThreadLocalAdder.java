package psy.lob.saw.counters.mine;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import psy.lob.saw.util.UnsafeAccess;

public class ThreadLocalAdder {
    private final AtomicLong deadThreadSum = new AtomicLong();
    static class PaddedLong1{
        long p1,p2,p3,p4,p6,p7;
    }
    static class PaddedLong2 extends PaddedLong1{
        private static final long VALUE_OFFSET;
        static {
            try {
                VALUE_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(PaddedLong2.class.getDeclaredField("value"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        volatile long value;
        public long get() {
            return value;
        }
        public long plainGet(){
            return UnsafeAccess.UNSAFE.getLong(this, VALUE_OFFSET);
        }
        public void lazySet(long v){
            UnsafeAccess.UNSAFE.putOrderedLong(this, VALUE_OFFSET, v);
        }

    }
    static class PaddedLong3 extends PaddedLong2{
        long p9,p10,p11,p12,p13,p14;
    }
    static final class ThreadAtomicLong extends PaddedLong3{
        final Thread t = Thread.currentThread();
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
        ThreadAtomicLong lc = tlc.get();
        lc.lazySet(lc.plainGet() + 1);
    }
    public long get(){
        long dts;
        long sum;
        do {
            dts = deadThreadSum.get();
            sum = 0;
            for(ThreadAtomicLong lc:counters){
                sum += lc.get();
            }
        } while(dts != deadThreadSum.get());
        return sum + dts;
    }

}
