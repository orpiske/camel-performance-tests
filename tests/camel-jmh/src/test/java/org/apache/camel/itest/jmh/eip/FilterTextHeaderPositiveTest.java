/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.itest.jmh.eip;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Endpoint;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * This tests a content-based-router when using a text body for the routing decision. This is suitable for most cases when
 * a large machine with too many cores is not available (as it limits to a maximum of 4 consumers + 4 producers).
 */
public class FilterTextHeaderPositiveTest {
    private static String DATA = "HELLO";
    private static final String HEADER = "filter";
    private static final String POSITIVE = "positive";
    private static final String NEGATIVE = "negative";

    @Test
    public void launchBenchmark() throws Exception {
        Options opt = new OptionsBuilder()
                // Specify which benchmarks to run.
                // You can be more specific if you'd like to run only one benchmark per test.
                .include(this.getClass().getName() + ".*")
                // Set the following options as needed
                .measurementIterations(10)
                .warmupIterations(5)
                .forks(1)
                .resultFormat(ResultFormatType.JSON)
                .result(this.getClass().getSimpleName() + ".jmh.json")
                .build();

        new Runner(opt).run();
    }

    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        CamelContext context;
        ProducerTemplate producerTemplate;
        ConsumerTemplate consumerTemplate;

        Endpoint endpoint;
        Endpoint consumerPositiveEndpoint;

        @Setup(Level.Trial)
        public void initialize() throws Exception {
            context = new DefaultCamelContext();

            producerTemplate = context.createProducerTemplate();
            consumerTemplate = context.createConsumerTemplate();

            endpoint = context.getEndpoint("disruptor:test");
            consumerPositiveEndpoint = context.getEndpoint("disruptor:positive");

            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    from(endpoint)
                            .filter(simple("${header.filter} == 'positive'"))
                                .to(consumerPositiveEndpoint)
                                .end()
                            .to("log:?level=OFF");
                }
            });

            context.start();
        }
    }


    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    @Benchmark
    public void sendPositive(Blackhole bh, BenchmarkState state) {
        state.producerTemplate.sendBodyAndHeader(state.endpoint, DATA, HEADER, POSITIVE);

        bh.consume(state.consumerTemplate.receive(state.consumerPositiveEndpoint));
    }


}
