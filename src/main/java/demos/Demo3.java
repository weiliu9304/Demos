package demos;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Batch processing with flux
 */
public class Demo3 {
    private static int batchAction(List<Integer> list) {
        try {
            Thread.sleep(500);
//            System.out.println("batch action in thread: " + Thread.currentThread().getName());
        } catch (Exception e) {
        }
        return 0;
    }

    private static int singeAction(int value) {
        try {
            Thread.sleep(50);
//            System.out.println("single action in thread: " + Thread.currentThread().getName());
        } catch (Exception e) {
        }
        return 0;
    }

    private static void example1(Flux<Integer> flux) {
        long startTime = System.currentTimeMillis();
        List<Integer> resultList = flux.buffer(100).publishOn(Schedulers.fromExecutor(executorService))
                .map(Demo3::batchAction).collectList().block();
        System.out.println(resultList.size());
        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("The running time is %dms" , runningTime));
    }

    private static void example2(Flux<Integer> flux) {
        long startTime = System.currentTimeMillis();
        List<Integer> resultList = flux.publishOn(Schedulers.fromExecutor(executorService))
                .map(Demo3::singeAction).collectList().block();
        System.out.println(resultList.size());
        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("The running time is %dms" , runningTime));
    }

    private static ExecutorService executorService = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        Flux<Integer> flux = Flux.range(1 , 500);

        System.out.println("----------- example1 -----------");
        example1(flux);
        System.out.println("----------- example1 -----------");

        System.out.println("----------- example2 -----------");
        example2(flux);
        System.out.println("----------- example2 -----------");

        executorService.shutdown();
    }
}
