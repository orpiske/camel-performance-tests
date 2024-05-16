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

package org.apache.camel.itest.jmh.other;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class ArrayCopyBenchmarkTest {

    private byte statefulAdvices;
    private Object[] sourceArray;
    private Object[] destArray;
    private Object val;

    @Setup
    public void setup() {
        sourceArray = new Object[statefulAdvices];
        destArray = new Object[statefulAdvices];
        val = null;

//        System.out.println("Array size: " + sourceArray.length);
    }

    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void systemArrayCopy(Blackhole bh) {
        System.arraycopy(sourceArray, 0, destArray, 0, statefulAdvices);
        bh.consume(destArray);
    }

    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void arraysFill(Blackhole bh) {
        Arrays.fill(destArray, val);
        bh.consume(destArray);
    }

    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void unrolled(Blackhole bh) {
        destArray[0] = val;
        bh.consume(destArray);
    }

    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void baseline(Blackhole bh) {
        bh.consume(destArray);
    }

    @Test
    public void launchBenchmark() throws Exception {
        Options opt = new OptionsBuilder()
                // Specify which benchmarks to run.
                // You can be more specific if you'd like to run only one benchmark per test.
                .include(this.getClass().getName() + ".*")
                .forks(1)
                .resultFormat(ResultFormatType.JSON)
                .result(this.getClass().getSimpleName() + ".jmh.json")
                .build();

        new Runner(opt).run();
    }
}
