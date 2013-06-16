package psy.lob.saw.counters.benchmarks23;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

import psy.lob.saw.counters.jdk8.LongAdder;

@State(Scope.Group)
public class LongAdderCounterBenchmark {
    private LongAdder counter;
    @Setup
    public void up() {
        counter = new LongAdder();
    }

    @GenerateMicroBenchmark
    @Group("rw")
    @Threads(23)
    public void inc() {
        counter.increment();
    }

    @GenerateMicroBenchmark
    @Group("rw")
    public long get() {
        return counter.sum();
    }
}
