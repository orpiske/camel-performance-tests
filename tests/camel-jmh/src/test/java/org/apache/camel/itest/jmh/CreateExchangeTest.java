package org.apache.camel.itest.jmh;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.PrototypeExchangeFactory;
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
//                .warmupTime(TimeValue.seconds(1))
//                .warmupIterations(2)
                .measurementTime(TimeValue.seconds(15))
                .measurementIterations(5)
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

        @Setup(Level.Trial)
        public void initialize() {
            context = new DefaultCamelContext();
            factory = new PrototypeExchangeFactory();

            factory.setCamelContext(context);
        }
    }


    @Benchmark
    @Measurement(batchSize = 10000000)
    public void benchmarkWithExchangeAndAdaptSet(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        final Exchange exchange = state.factory.create(true);

        exchange.adapt(ExtendedExchange.class).setFromRouteId("Something");
        bh.consume(exchange);
    }

    @Benchmark
    @Measurement(batchSize = 10000000)
    public void benchmarkWithExchangeAndSet(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        final Exchange exchange = state.factory.create(true);

        exchange.setExchangeId("lalala");
        bh.consume(exchange);
    }

    @Benchmark
    @Measurement(batchSize = 10000000)
    public void benchmarkWithExchangeNoSet(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        final Exchange exchange = state.factory.create(true);

        bh.consume(exchange);
    }

    @Benchmark
    @Measurement(batchSize = 10000000)
    public void benchmarkWithDefaultExchangeAndAdaptSet(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        final DefaultExchange exchange = new DefaultExchange(state.context);

        exchange.adapt(ExtendedExchange.class).setFromRouteId("Something");
        bh.consume(exchange);
    }

    @Benchmark
    @Measurement(batchSize = 10000000)
    public void benchmarkWithDefaultExchangeAndSet(CreateExchangeTest.BenchmarkState state, Blackhole bh) {
        final DefaultExchange exchange = new DefaultExchange(state.context);

        exchange.setFromRouteId("Something");
        bh.consume(exchange);
    }

}
