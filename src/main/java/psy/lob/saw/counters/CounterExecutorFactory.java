package psy.lob.saw.counters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.openjdk.jmh.runner.BaseMicroBenchmarkHandler.ExecutorFactory;



public class CounterExecutorFactory implements ExecutorFactory {
    public static class CounterThreadFactory implements ThreadFactory {

        private final AtomicInteger counter = new AtomicInteger();
        private final String prefix;

        public CounterThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            int threadIndex = counter.incrementAndGet();
            Thread thread = new CounterThread(r, threadIndex);
            thread.setName(prefix + "-worker" + threadIndex);
            thread.setDaemon(true);
            return thread;
        }
    }
    @Override
    public ExecutorService createExecutor(int maxThreads, String prefix) {
        return Executors.newFixedThreadPool(maxThreads, new CounterThreadFactory(prefix));
    }

    @Override
    public boolean shutdownAllowed() {
        return true;
    }

}
