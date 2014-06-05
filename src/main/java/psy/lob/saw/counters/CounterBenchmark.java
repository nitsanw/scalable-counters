package psy.lob.saw.counters;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Group)
public class CounterBenchmark {
    private Counter counter;
    @Param(value={"Atomic",
        "ConcAutoTable",
        "LongAdder7",
        "LongAdder8",
        "ThreadLocal"})
    String counterName;
    @Setup
    public void buildMeCounterHearty() {
        counter = CounterFactory.build(counterName);
    }

    @GenerateMicroBenchmark
    @Group("rw")
    public void inc() {
        counter.inc();
    }

    @GenerateMicroBenchmark
    @Group("rw")
    public long get() {
        return counter.get();
    }
}
