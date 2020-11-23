package demos.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequiredArgsConstructor
public class TestController {
    private final WaggleReactiveRepository waggleReactiveRepository;

    @GetMapping(value = "/waggles", consumes = MediaType.APPLICATION_NDJSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Waggle> getWagglesReactive(@RequestBody Flux<Integer> waggleIds) {
        return Flux.merge(waggleIds.buffer(10).map(ids -> waggleReactiveRepository.findByIdList(ids)));
    }
}
