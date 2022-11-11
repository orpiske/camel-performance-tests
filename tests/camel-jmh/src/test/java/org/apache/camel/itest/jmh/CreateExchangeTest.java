package org.apache.camel.itest.jmh;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.PrototypeExchangeFactory;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class CreateExchangeTest {

    @Test
    public void launchBenchmark() throws Exception {
        Options opt = new OptionsBuilder()
                // Specify which benchmarks to run.
                // You can be more specific if you'd like to run only one benchmark per test.
                .include(this.getClass().getName() + ".*")
                // Set the following options as needed
                .mode(Mode.SingleShotTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .measurementIterations(20)
                .warmupIterations(5)
                .threads(Runtime.getRuntime().availableProcessors())
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

    // Proposed solution
    @Benchmark
    @Measurement(batchSize = 50000)
    public void benchmarkWithCachedContext(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        if (state.extendedCamelContext == null) {
            state.extendedCamelContext = state.context.adapt(ExtendedCamelContext.class);
        }

        if (state.modelCamelContext == null) {
            state.modelCamelContext = state.context.adapt(ModelCamelContext.class);
        }

        state.extendedCamelContext.setDescription("extended");
        state.modelCamelContext.setManagementName("model");

        bh.consume(state.extendedCamelContext);
        bh.consume(state.modelCamelContext);
    }

    // What may happen in unhappy paths
    @Benchmark
    @Measurement(batchSize = 50000)
    public void benchmarkWithInstanceOfAndAdapt(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        if (state.context instanceof ExtendedCamelContext) {
            final ExtendedCamelContext extendedCamelContext = state.context.adapt(ExtendedCamelContext.class);
            extendedCamelContext.setDescription("extended");
            bh.consume(extendedCamelContext);
        }

        if (state.context instanceof ModelCamelContext) {
            final ModelCamelContext modelCamelContext = state.context.adapt(ModelCamelContext.class);
            modelCamelContext.setManagementName("model");
            bh.consume(modelCamelContext);
        }
    }


    // More closely represents the happy path in many of the Camel code
    @Benchmark
    @Measurement(batchSize = 50000)
    public void benchmarkWithNoInstanceOfAndAdapt(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        final ExtendedCamelContext extendedCamelContext = state.context.adapt(ExtendedCamelContext.class);
        extendedCamelContext.setDescription("extended");
        bh.consume(extendedCamelContext);

        final ModelCamelContext modelCamelContext = state.context.adapt(ModelCamelContext.class);
        modelCamelContext.setManagementName("model");
        bh.consume(modelCamelContext);

    }

}
