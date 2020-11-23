package demos;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use case from sn-reaction
 */
public class Demo2 {

    // current sn-reaction implementation
    private static void example1(Flux<Integer> flux , Mono<Set<Integer>> setMono) {
        Mono<List<Integer>> listMono = flux.collectList();
        Mono<List<Integer>> resultMono = Mono.zip(listMono, setMono)
                .map(tuple -> tuple.getT1().stream().filter(value -> tuple.getT2().contains(value))
                        .collect(Collectors.toUnmodifiableList()));
        long startTime = System.currentTimeMillis();
        List<Integer> resultList = resultMono.block();
        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("The running time is: %dms" , runningTime));
        System.out.println(resultList);
    }

    // alternative
    private static void example2(Flux<Integer> flux , Mono<Set<Integer>> setMono) {
        Flux<Set<Integer>> setFlux = setMono.flux().repeat();
        long startTime = System.currentTimeMillis();
        List<Tuple2<Integer , Set<Integer>>> list = Flux.zip(flux, setFlux).filter(tuple -> tuple.getT2().contains(tuple.getT1())).collectList().block();
        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("The running time is: %dms" , runningTime));
        System.out.println(list.stream().map(Tuple2::getT1).collect(Collectors.toUnmodifiableList()));
    }

    public static void main(String[] args) {
//        int limit = 2000_000_00;
        int limit = 100;
        Mono<Set<Integer>> mono = Mono.just(Set.of(1 , 10 , 3 , 4));
        Flux<Integer> flux = Flux.range(1, limit);

        System.out.println("------------ example1 ------------");
        example1(flux, mono);
        System.out.println("------------ example1 ------------");

        System.out.println("------------ example2 ------------");
        example2(flux, mono);
        System.out.println("------------ example2 ------------");
    }
}
