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

package org.apache.camel.itest.jmh;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.camel.util.URISupport;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
public class StringIsBlank {

    @Test
    public void launchBenchmark() throws Exception {
        Options opt = new OptionsBuilder()
                // Specify which benchmarks to run.
                // You can be more specific if you'd like to run only one benchmark per test.
                .include(this.getClass().getName() + ".*")
                .forks(1)
                .warmupIterations(10)
                .measurementIterations(20)
                .resultFormat(ResultFormatType.JSON)
                .result(this.getClass().getSimpleName() + ".jmh.json")
                .build();

        new Runner(opt).run();
    }

    // We may need to keep these here: we want to try to prevent constant-folding from kicking in!
    private String largeString = "log:foo?level=INFO&logMask=false&exchangeFormatter=#myFormatter";
    private String blankString = "";
    private String largeBlankString = "                 ";

    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void testLargeStringIsBlank(Blackhole bh) {
        bh.consume(largeString.isBlank());
    }

    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void testLargeStringTrim(Blackhole bh) {
        bh.consume(largeString.trim().isEmpty());
    }


    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void testBlankStringIsBlank(Blackhole bh) {
        bh.consume(blankString.isBlank());
    }

    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void testBlankStringTrim(Blackhole bh) {
        bh.consume(blankString.trim().isEmpty());
    }

    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void testLargeBlankStringIsBlank(Blackhole bh) {
        bh.consume(largeBlankString.isBlank());
    }

    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void testLargeBlankStringTrim(Blackhole bh) {
        bh.consume(largeBlankString.trim().isEmpty());
    }
}
