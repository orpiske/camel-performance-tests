package org.apache.camel.itest.jmh;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.PrototypeExchangeFactory;
import org.apache.camel.model.ModelCamelContext;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class CreateExchangeTest {

    @Test
    public void launchBenchmark() throws Exception {
        Options opt = new OptionsBuilder()
                // Specify which benchmarks to run.
                // You can be more specific if you'd like to run only one benchmark per test.
                .include(this.getClass().getName() + ".*")
                // Set the following options as needed
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.MICROSECONDS)
                .measurementIterations(20)
                .warmupIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @State(Scope.Benchmark)
    public static class BenchmarkState {

        CamelContext context;
        PrototypeExchangeFactory factory;
        ExtendedCamelContext extendedCamelContext;
        ModelCamelContext modelCamelContext;

        @Setup(Level.Trial)
        public void initialize() {
            context = new DefaultCamelContext();
            factory = new PrototypeExchangeFactory();
        }
    }

    private static void cached(BenchmarkState state, Blackhole bh) {
        if (state.extendedCamelContext == null) {
            state.extendedCamelContext = state.context.adapt(ExtendedCamelContext.class);
        }

        if (state.modelCamelContext == null) {
            state.modelCamelContext = state.context.adapt(ModelCamelContext.class);
        }

        state.extendedCamelContext.setDescription("extended567");
        state.modelCamelContext.setManagementName("model567");

        bh.consume(state.extendedCamelContext);
        bh.consume(state.modelCamelContext);
    }

    private static void instanceOfAndAdapt(BenchmarkState state, Blackhole bh) {
        if (state.context instanceof ExtendedCamelContext) {
            final ExtendedCamelContext extendedCamelContext = state.context.adapt(ExtendedCamelContext.class);
            extendedCamelContext.setDescription("extended345");
            bh.consume(extendedCamelContext);
        }

        if (state.context instanceof ModelCamelContext) {
            final ModelCamelContext modelCamelContext = state.context.adapt(ModelCamelContext.class);
            modelCamelContext.setManagementName("model345");
            bh.consume(modelCamelContext);
        }
    }

    private static void noInstanceOf(BenchmarkState state, Blackhole bh) {
        final ExtendedCamelContext extendedCamelContext = state.context.adapt(ExtendedCamelContext.class);
        extendedCamelContext.setDescription("extended123");
        bh.consume(extendedCamelContext);

        final ModelCamelContext modelCamelContext = state.context.adapt(ModelCamelContext.class);
        modelCamelContext.setManagementName("model123");
        bh.consume(modelCamelContext);
    }

    // Proposed solution
    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(1)
    public void benchmarkWithCachedContext_1(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        cached(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(2)
    public void benchmarkWithCachedContext_2(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        cached(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(4)
    public void benchmarkWithCachedContext_4(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        cached(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(8)
    public void benchmarkWithCachedContext_8(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        cached(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(16)
    public void benchmarkWithCachedContext_16(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        cached(state, bh);
    }

    // What may happen in unhappy paths
    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(1)
    public void benchmarkWithInstanceOfAndAdapt_1(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        instanceOfAndAdapt(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(2)
    public void benchmarkWithInstanceOfAndAdapt_2(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        instanceOfAndAdapt(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(4)
    public void benchmarkWithInstanceOfAndAdapt_4(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        instanceOfAndAdapt(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(8)
    public void benchmarkWithInstanceOfAndAdapt_8(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        instanceOfAndAdapt(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(16)
    public void benchmarkWithInstanceOfAndAdapt_16(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        instanceOfAndAdapt(state, bh);
    }

    // More closely represents the happy path in many of the Camel code
    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(1)
    public void benchmarkWithNoInstanceOfAndAdapt_1(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        noInstanceOf(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(2)
    public void benchmarkWithNoInstanceOfAndAdapt_2(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        noInstanceOf(state, bh);
    }


    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(4)
    public void benchmarkWithNoInstanceOfAndAdapt_4(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        noInstanceOf(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(8)
    public void benchmarkWithNoInstanceOfAndAdapt_8(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        noInstanceOf(state, bh);
    }

    @Benchmark
    // @Measurement(batchSize = 50000)
    @Threads(16)
    public void benchmarkWithNoInstanceOfAndAdapt_16(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        noInstanceOf(state, bh);
    }



}
