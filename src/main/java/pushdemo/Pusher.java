package pushdemo;


import reactor.core.publisher.Flux;

import java.util.concurrent.*;
import java.util.stream.IntStream;


/**
 * In current implementation event's order can not be guaranteed, but it will leverage concurrency as much possible
 */
public class Pusher {
    private static int READ_PARALLELISM = 4;

    private BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(1000000);
    // leverage thread pool to fetch data from blockingQueue
    private ExecutorService executorService = Executors.newFixedThreadPool(READ_PARALLELISM);
    private FluxAssembler fluxAssembler = new FluxAssembler();

    public Pusher() {
        buildFlux();
    }

    public boolean add(Integer value) {
        // thread safe version
        try {
            // blocking put here
            queue.put(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void buildFlux() {
        // Flux create can support multi-thread sinks
        Flux.<Integer>create(sink ->
                    // Use four threads to fetch data
                    IntStream.range(0, READ_PARALLELISM).forEach(idx ->
                        executorService.execute(() -> {
                            while (true) {
                                try {
                                    int value = queue.take();
                                    sink.next(value);
                                } catch (Exception e) {
                                    System.err.println(String.format("Sink failed, exception is: %s", e.getMessage()));
                                }
                            }
                        })
                    )
        ).transform(fluxAssembler::build)
         .subscribe(value -> System.out.println("Final result: " + value),
                    error -> System.err.println("Final error: " + error));
    }
}
