package psy.lob.saw.counters;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import psy.lob.saw.util.UnsafeAccess;

public class CounterThread extends Thread {
    private static final CopyOnWriteArrayList<CounterThread> counters = new CopyOnWriteArrayList<CounterThread>();
    private static final AtomicLong sumDeadThreads = new AtomicLong();
    private static final long VALUE_OFFSET;
    private static final long PROBE_OFFSET;

    static {
        try {
            VALUE_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(CounterThread.class.getDeclaredField("counter"));
            PROBE_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(CounterThread.class.getDeclaredField("probe"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void increment() {
        Thread t = Thread.currentThread();
        UnsafeAccess.UNSAFE.putOrderedLong(t, VALUE_OFFSET, getCounter(t) + 1);
    }

    public static long getCounter(Thread t) {
        return UnsafeAccess.UNSAFE.getLong(t, VALUE_OFFSET);
    }
    public static long get() {
        long sum = sumDeadThreads.get();
        for (CounterThread lc : counters) {
            long tlc = lc.counter;
            sum += tlc;
        }
        return sum;
    }

    public static void join(CounterThread currentThread) {
        for (CounterThread ct : counters) {
            long tfc = ct.counter;
            if(ct.getState() == State.TERMINATED) {
                counters.remove(ct);
                sumDeadThreads.addAndGet(tfc);
            }
        }
        counters.add(currentThread);
    }
    public static int getProbe(){
        return UnsafeAccess.UNSAFE.getInt(Thread.currentThread(), PROBE_OFFSET);
    }
    public static void setProbe(int p){
        UnsafeAccess.UNSAFE.putInt(Thread.currentThread(), PROBE_OFFSET, p);
    }
    private int probe;
    private volatile long counter;

    public CounterThread(Runnable r, int threadIndex) {
        super(r);
        probe = threadIndex;
        join(this);
    }
    
}