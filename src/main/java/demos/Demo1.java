package demos;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Demo for reactor thread context management, async boundary
 */
public class Demo1 {

    private static ExecutorService service1 = Executors.newSingleThreadExecutor();
    private static ExecutorService service2 = Executors.newSingleThreadExecutor();
    private static ExecutorService service3 = Executors.newSingleThreadExecutor();

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    private static int action1(int value) {
        try {
            Thread.sleep(1000);
            System.out.println("action1 " + Thread.currentThread().getName());
        } catch (Exception e) {
        }
        return value;
    }

    private static int action2(int value) {
        try {
            Thread.sleep(2000);
            System.out.println("action2 " + Thread.currentThread().getName());
        } catch (Exception e) {
        }
        return value;
    }

    private static int action3(int value) {
        try {
            Thread.sleep(3000);
            System.out.println("action3 " + Thread.currentThread().getName());
        } catch (Exception e) {
        }
        return value;
    }

    private static void example1() {
        long startTime = System.currentTimeMillis();

        Mono<Integer> source = Mono.just(1);

        Mono<Integer> sink1 = source.map(Demo1::action1);
        Mono<Integer> sink2 = source.map(Demo1::action2);
        Mono<Integer> sink3 = source.map(Demo1::action3);

        Mono.zip(sink1, sink2, sink3).map(ignored -> {
            System.out.println("block " + Thread.currentThread().getName());
            return ignored;
        }).block();

        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Running time is %dms" , runningTime));
    }

    private static void example2() {
        long startTime = System.currentTimeMillis();

        Mono<Integer> source = Mono.just(1);

        Mono<Integer> sink1 = source.map(Demo1::action1).publishOn(Schedulers.fromExecutor(service1));
        Mono<Integer> sink2 = source.map(Demo1::action2).publishOn(Schedulers.fromExecutor(service2));
        Mono<Integer> sink3 = source.map(Demo1::action3).publishOn(Schedulers.fromExecutor(service3));

        Mono.zip(sink1, sink2, sink3).map(ignored -> {
            System.out.println("block " + Thread.currentThread().getName());
            return ignored;
        }).block();

        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Running time is %dms" , runningTime));
    }

    private static void example3() {
        long startTime = System.currentTimeMillis();

        Mono<Integer> source = Mono.just(1);

        Mono<Integer> sink1 = source.publishOn(Schedulers.fromExecutor(service1)).map(Demo1::action1);
        Mono<Integer> sink2 = source.publishOn(Schedulers.fromExecutor(service2)).map(Demo1::action2);
        Mono<Integer> sink3 = source.publishOn(Schedulers.fromExecutor(service3)).map(Demo1::action3);

        Mono.zip(sink1, sink2, sink3).map(ignored -> {
            System.out.println("block " + Thread.currentThread().getName());
            return ignored;
        }).block();

        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Running time is %dms" , runningTime));
    }

    private static void example4() {
        /**
         * sn-api use similar design
         * 1. Mono.create to wrap a synchronous http call
         * 2. Register callback in mono
         * 3. Allocate a thread pool with 256 threads
         * 4. Use thread pool to execute http call
         *
         * WebClient should be an alternative
         * 1. Highly integrated with Spring Webflux
         * 2. NIO event loop with less threads (share same event loop with netty by default)
         * 3. Support Streaming data processing in application layer
         * 4. Could resilient features be included in WebClient? eg: CircuitBreaker
         */

        long startTime = System.currentTimeMillis();

        Scheduler scheduler = Schedulers.fromExecutor(executorService);
        Mono<Integer> source = Mono.just(1);

        Mono<Integer> sink1 = source.flatMap(value -> Mono.fromCallable(() -> action1(value)).publishOn(scheduler));
        Mono<Integer> sink2 = source.flatMap(value -> Mono.fromCallable(() -> action2(value)).publishOn(scheduler));
        Mono<Integer> sink3 = source.flatMap(value -> Mono.fromCallable(() -> action3(value)).publishOn(scheduler));

        Mono.zip(sink1, sink2, sink3).map(ignored -> {
            System.out.println("block " + Thread.currentThread().getName());
            return ignored;
        }).block();

        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Running time is %dms" , runningTime));
    }

    public static void main(String[] args) {
        System.out.println("--------- example1 ---------");
        example1();
        System.out.println("--------- example1 ---------");

        System.out.println("--------- example2 ---------");
        example2();
        System.out.println("--------- example2 ---------");

        System.out.println("--------- example3 ---------");
        example3();
        System.out.println("--------- example3 ---------");

        System.out.println("--------- example4 ---------");
        example4();
        System.out.println("--------- example4 ---------");

        service1.shutdown();
        service2.shutdown();
        service3.shutdown();
        executorService.shutdown();
    }
}
