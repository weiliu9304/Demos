package pushdemo;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class FluxAssembler {
    /**
     * business logic implementation, now we can consolidate all push logic into one flux dag
      */
    public Flux<Integer> build(Flux<Integer> flux) {
        // tumbling time window
        return flux.window(Duration.ofSeconds(10))
                .concatMap(this::batchProcessing);
    }

    private Flux<Integer> batchProcessing(Flux<Integer> flux) {
        return flux.distinct();
//        .doOnNext(value -> System.out.println("Value is: " + value));
    }
}
