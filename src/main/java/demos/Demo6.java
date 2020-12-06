package demos;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Demo for reactor context
 */
public class Demo6 {

    public static void main(String[] args) {
        Flux.just(1 , 2 , 3 , 4)
            .flatMap(value -> Mono.deferContextual(context -> Mono.just(context.get("prefix") + " " + value)))
            // use nearest context
            .contextWrite(context -> context.put("prefix", "value_prefix"))
            .contextWrite(context -> context.put("prefix", "duplicate_value_prefix"))
            .flatMap(string ->
                    Mono.deferContextual(context -> Mono.just(string + " " + context.get("suffix")))
                    .contextWrite(context -> context.put("suffix", "value_suffix")))
            .contextWrite(context -> context.put("suffix", "duplicate_value_suffix"))
            .subscribe(System.out::println);
    }

}
