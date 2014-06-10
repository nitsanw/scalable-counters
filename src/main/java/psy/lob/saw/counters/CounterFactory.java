package psy.lob.saw.counters;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.CompilerControl.Mode;

import psy.lob.saw.counters.cliff.ConcurrentAutoTable;
import psy.lob.saw.counters.jdk7.LongAdderBackport;
import psy.lob.saw.counters.mine.ThreadLocalAdder;

public class CounterFactory {
    public enum CounterType {
        Atomic,
        ConcAutoTable,
        LongAdder7,
        LongAdder8,
        ThreadLocal
    }
    static final Counter build(String counterName) {
        return build(CounterType.valueOf(counterName));
    }
    static final Counter build(CounterType type) {
        switch (type) {
        case Atomic:
            return new AtomicLongCounter();
        case ConcAutoTable:
            return new ConcAutoTableCounter();
        case LongAdder7:
            return new LongAdder7Counter();
        case LongAdder8:
            return new LongAdder8Counter();
        case ThreadLocal:
            return new ThreadLocalCounter();
        default:
            throw new IllegalArgumentException();
        } 
    }
    
    @SuppressWarnings("serial")
    static class AtomicLongCounter extends AtomicLong implements Counter {
    
        @Override
        @CompilerControl(Mode.INLINE)
        public void inc() {
            super.incrementAndGet();
        }
    }
    @SuppressWarnings("serial")
    static class ConcAutoTableCounter extends ConcurrentAutoTable implements Counter {

        @Override
        @CompilerControl(Mode.INLINE)
        public void inc() {
            super.increment();
        }

        @Override
        @CompilerControl(Mode.INLINE)
        public long get() {
            return super.get();
        }        
    
    }
    @SuppressWarnings("serial")
    static class LongAdder7Counter extends LongAdderBackport implements Counter {
        @Override
        @CompilerControl(Mode.INLINE)
        public void inc() {
            super.increment();
        }

        @Override
        @CompilerControl(Mode.INLINE)
        public long get() {
            return super.sum();
        }
        
    }
    @SuppressWarnings("serial")
    static class LongAdder8Counter extends LongAdder implements Counter {
        @Override
        @CompilerControl(Mode.INLINE)
        public void inc() {
            super.increment();
        }

        @Override
        @CompilerControl(Mode.INLINE)
        public long get() {
            return super.sum();
        }        
    }

    static class ThreadLocalCounter extends ThreadLocalAdder implements Counter {
        @Override
        @CompilerControl(Mode.INLINE)
        public void inc() {
            super.increment();
        }

        @Override
        @CompilerControl(Mode.INLINE)
        public long get() {
            return super.get();
        }        
    }
}
