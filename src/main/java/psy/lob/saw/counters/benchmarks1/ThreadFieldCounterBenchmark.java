package psy.lob.saw.counters.benchmarks1;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

import psy.lob.saw.counters.CounterThread;


@State(Scope.Group)
public class ThreadFieldCounterBenchmark {

    @GenerateMicroBenchmark
    @Group("rw")
    @Threads(1)
    public void inc() {
        CounterThread.increment();
    }

    @GenerateMicroBenchmark
    @Group("rw")
    public long get() {
        return CounterThread.get();
    }
}
