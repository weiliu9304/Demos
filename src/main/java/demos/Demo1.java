package demos;

import reactor.core.publisher.Mono;
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

    public static void main(String[] args) {
        System.out.println("--------- example1 ---------");
        example1();
        System.out.println("--------- example1 ---------");

        System.out.println("--------- example2 ---------");
        example2();
        System.out.println("--------- example2 ---------");

        System.out.println("--------- example3 ---------");
        /**
         * In sn-api we wrap http call into mono with a common thread pool
         */
        example3();
        System.out.println("--------- example3 ---------");

        service1.shutdown();
        service2.shutdown();
        service3.shutdown();
    }
}
