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

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class BooleanConversions {

    @Param({"true", "TRUE", "false", "FALSE", "FaLsE", "somethingElse"})
    private String booleanArg;

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

    private static Boolean customParseBoolean(String str) {
        int len = str.length();
        // fast check the value as-is in lower case which is most common
        if (len == 4) {
            if ("true".equals(str)) {
                return Boolean.TRUE;
            }

            if ("TRUE".equals(str.toUpperCase())) {
                return Boolean.TRUE;
            }

            return null;
        }

        if (len == 5) {
            if ("false".equals(str)) {
                return Boolean.FALSE;
            }

            if ("FALSE".equals(str.toUpperCase())) {
                return Boolean.FALSE;
            }

            return null;
        }

        return null;
    }

    private static Boolean booleanValueOf(String str) {
        return Boolean.valueOf(str);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testCustomParseBoolean(Blackhole bh) {
        bh.consume(customParseBoolean(booleanArg));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testBooleanValueOf(Blackhole bh) {
        bh.consume(booleanValueOf(booleanArg));
    }

}
