package psy.lob.saw.counters.benchmarks11;

import java.util.concurrent.atomic.AtomicLong;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

@State(Scope.Group)
public class AtomicCounterBenchmark {
    private AtomicLong counter;
    @Setup
    public void up() {
        counter = new AtomicLong();
    }

    @GenerateMicroBenchmark
    @Group("rw")
    @Threads(11)
    public void inc() {
        counter.incrementAndGet();
    }

    @GenerateMicroBenchmark
    @Group("rw")
    public long get() {
        return counter.get();
    }
}
