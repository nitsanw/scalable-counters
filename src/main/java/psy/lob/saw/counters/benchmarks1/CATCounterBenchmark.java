package psy.lob.saw.counters.benchmarks1;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

import psy.lob.saw.counters.cliff.ConcurrentAutoTable;
import psy.lob.saw.counters.jdk8.LongAdder;

@State(Scope.Group)
public class CATCounterBenchmark {
    private ConcurrentAutoTable counter;
    @Setup
    public void up() {
        counter = new ConcurrentAutoTable();
    }

    @GenerateMicroBenchmark
    @Group("rw")
    @Threads(1)
    public void inc() {
        counter.increment();
    }

    @GenerateMicroBenchmark
    @Group("rw")
    public long get() {
        return counter.get();
    }
}
