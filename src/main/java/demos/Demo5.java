package demos;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * Hot & Cold, unicast & multicast publishers
 */
public class Demo5 {

    private static void coldPublisher() {
        // Wrap hold publisher into cold publisher, print useful logs, timestamp should be different
        Mono<Integer> mono = Mono.defer(() -> {
            System.out.println("Current timestamp: " + System.currentTimeMillis());
            return Mono.just(10);
        });

        mono.subscribe();
        mono.subscribe();
    }

    private static void hotPublisher() {
        // Use cache to turn cold publisher into hot publisher again, timestamp should be printed only once
        Mono<Integer> mono = Mono.defer(() -> {
            System.out.println("Current timestamp: " + System.currentTimeMillis());
            return Mono.just(10);
        }).cache();

        mono.subscribe();
        mono.subscribe();
    }

    private static void sinkExample() {
        Sinks.Many<Integer> sink = Sinks.many().multicast().directBestEffort();
        Flux<Integer> flux = sink.asFlux();

        flux.subscribe(value -> System.out.println("subscriber one: " + value));

        sink.tryEmitNext(1);
        sink.tryEmitNext(2);
        sink.tryEmitNext(3);

        flux.subscribe(value -> System.out.println("subscriber two: " + value));

        sink.tryEmitNext(4);
        sink.tryEmitNext(5);

        sink.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);
    }

    private static void turnUnicastToMulticastWithShare() {
        Sinks.Many<Integer> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Integer> flux = sink.asFlux().share();

        flux.subscribe(value -> System.out.println("flux one: " + value));

        sink.tryEmitNext(1);
        sink.tryEmitNext(2);
        sink.tryEmitNext(3);

        // second subscribe still can not get any data since it is hot publisher
        flux.subscribe(value -> System.out.println("flux two: " + value));
    }

    private static void turnUnicastToMulticastWithCache() {
        Sinks.Many<Integer> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Integer> flux = sink.asFlux().cache();

        flux.subscribe(value -> System.out.println("flux one: " + value));

        sink.tryEmitNext(1);
        sink.tryEmitNext(2);
        sink.tryEmitNext(3);

        // second subscriber can get data now
        flux.subscribe(value -> System.out.println("flux two: " + value));
    }

    private static void unicastSink() {
        Sinks.Many<Integer> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Integer> flux = sink.asFlux();

        flux.subscribe();
        //  flux.subscribe();    unicast can not support more than one subscriber
    }

    public static void main(String[] args) {
        System.out.println("--------------- cold publisher ---------------");
        coldPublisher();
        System.out.println("--------------- cold publisher ---------------");

        System.out.println("--------------- hot publisher ---------------");
        hotPublisher();
        System.out.println("--------------- hot publisher ---------------");

        System.out.println("--------------- sink publisher ---------------");
        sinkExample();
        System.out.println("--------------- sink publisher ---------------");

        System.out.println("--------------- turn unicast to multicast with share ---------------");
        turnUnicastToMulticastWithShare();
        System.out.println("--------------- turn unicast to multicast with share ---------------");

        System.out.println("--------------- turn unicast to multicast with cache ---------------");
        turnUnicastToMulticastWithCache();
        System.out.println("--------------- turn unicast to multicast with cache ---------------");

        System.out.println("--------------- unicast sink ------------------");
        unicastSink();
        System.out.println("--------------- unicast sink ------------------");
    }
}
