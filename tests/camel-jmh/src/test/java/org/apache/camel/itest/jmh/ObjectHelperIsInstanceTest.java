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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
public class ObjectHelperIsInstanceTest {

    private Object hashMap = new HashMap<>();
    private Object concurrentHashMap = new ConcurrentHashMap<>();
    private Object treeMap = new HashMap<>();
    private Object linkedHashMap = new LinkedHashMap<>();

    private Object someString = new String("abc");
    private Object someInteger = Integer.valueOf(1);
    private Object someLong = Long.valueOf(2);
    private Object array = new byte[10];


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


    private boolean fastIsMap(Object obj) {
        return obj.getClass() == HashMap.class
                || obj.getClass() == ConcurrentHashMap.class
                || obj.getClass() == ConcurrentSkipListMap.class
                || obj.getClass() == EnumMap.class
                || obj.getClass() == LinkedHashMap.class
                || obj.getClass() == TreeMap.class
                || obj.getClass() == WeakHashMap.class;

    }

    private boolean isInstanceOfMap(Object obj) {
        return obj instanceof Map;
    }

    @Threads(4)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testMapEqualsPositive(Blackhole bh) {
        bh.consume(fastIsMap(hashMap));
        bh.consume(fastIsMap(concurrentHashMap));
        bh.consume(fastIsMap(treeMap));
        bh.consume(fastIsMap(linkedHashMap));
    }

    @Threads(4)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testMapEqualsNegative(Blackhole bh) {
        bh.consume(fastIsMap(someString));
        bh.consume(fastIsMap(someInteger));
        bh.consume(fastIsMap(someLong));
        bh.consume(fastIsMap(array));
    }

    @Threads(4)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testMapInstancePositive(Blackhole bh) {
        bh.consume(isInstanceOfMap(hashMap));
        bh.consume(isInstanceOfMap(concurrentHashMap));
        bh.consume(isInstanceOfMap(treeMap));
        bh.consume(isInstanceOfMap(linkedHashMap));
    }

    @Threads(4)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testMapInstanceNegative(Blackhole bh) {
        bh.consume(isInstanceOfMap(someString));
        bh.consume(isInstanceOfMap(someInteger));
        bh.consume(isInstanceOfMap(someLong));
        bh.consume(isInstanceOfMap(array));
    }


}
