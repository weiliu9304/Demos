package demos.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.sql.DataSource;


@RestController
@RequiredArgsConstructor
public class TestController {
    private final WaggleReactiveRepository waggleReactiveRepository;
    private final DataSource dataSource;

    @GetMapping(value = "/waggles", consumes = MediaType.APPLICATION_NDJSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Waggle> getWagglesReactive(@RequestBody Flux<Integer> waggleIds) {
        return Flux.concat(waggleIds.distinct().buffer(10).map(ids -> waggleReactiveRepository.findByIdList(ids)));
    }

    @GetMapping(value = "/test")
    public Mono<String> test() {
        try {
            dataSource.getConnection();
            return Mono.just("true");
        } catch (Exception e) {
            return Mono.just("false");
        }
    }
}
