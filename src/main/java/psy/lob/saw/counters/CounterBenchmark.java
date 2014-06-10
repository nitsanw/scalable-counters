package psy.lob.saw.counters;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Group)
public class CounterBenchmark {
    private Counter counter;

    @Param// This will default to running through all the counter types
    CounterFactory.CounterType counterType;
    
    @Setup
    public void buildMeCounterHearty() {
        counter = CounterFactory.build(counterType);
    }

    @Benchmark
    @Group("rw")
    public void inc() {
        counter.inc();
    }

    @Benchmark
    @Group("rw")
    public long get() {
        return counter.get();
    }
}
