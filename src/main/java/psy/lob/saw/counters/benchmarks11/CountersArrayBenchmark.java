package psy.lob.saw.counters.benchmarks11;

import java.util.concurrent.atomic.AtomicInteger;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

import psy.lob.saw.util.UnsafeAccess;

public class CountersArrayBenchmark {
    private final static int LONGS_IN_CACHELINE = Integer.getInteger("longs.in.cacheline", 8);
    private final static int NUMBER_OF_THREADS = Integer.getInteger("threads", 32);
    private final static AtomicInteger THREAD_INDEX = new AtomicInteger(0);

    @State(Scope.Benchmark)
    public static class ScalableCounters {
        private static final int base = UnsafeAccess.UNSAFE.arrayBaseOffset(long[].class);
        private static final int shift;
        private final long[] array = new long[LONGS_IN_CACHELINE * (NUMBER_OF_THREADS + 2)];

        static {
            int scale = UnsafeAccess.UNSAFE.arrayIndexScale(long[].class);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            shift = 31 - Integer.numberOfLeadingZeros(scale);
        }

        private static long byteOffset(int i) {
            return ((long) i << shift) + base;
        }

        public final long volatileGet(int i) {
            return UnsafeAccess.UNSAFE.getLongVolatile(array, byteOffset(i));
        }

        public final long plainGet(int i) {
            return UnsafeAccess.UNSAFE.getLong(array, byteOffset(i));
        }

        public final void lazySet(int i, long v) {
            UnsafeAccess.UNSAFE.putOrderedLong(array, byteOffset(i), v);
        }

        public final long sum() {
            long sum = 0;
            int numberOfThreads = THREAD_INDEX.get();
            for (int i = 1; i < numberOfThreads + 1; i++) {
                sum += volatileGet(LONGS_IN_CACHELINE * i);
            }
            return sum;
        }
    }

    @State(Scope.Thread)
    public static class ThreadIndex {
        private final int threadIndex = THREAD_INDEX.getAndIncrement();
        private final int counterIndex = LONGS_IN_CACHELINE + threadIndex * LONGS_IN_CACHELINE;
    }

    @GenerateMicroBenchmark
    @Group("rw")
    @Threads(11)
    public void inc(ScalableCounters counters, ThreadIndex index) {
        long value = counters.plainGet(index.counterIndex);
        counters.lazySet(index.counterIndex, value + 1);
    }

    @GenerateMicroBenchmark
    @Group("rw")
    public long get(ScalableCounters counters) {
        return counters.sum();
    }

}
