package demos.web;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;


public interface WaggleReactiveRepository extends ReactiveCrudRepository<Waggle, Integer> {
    @Query("select id, name from waggle where id in (:idList)")
    Flux<Waggle> findByIdList(List<Integer> idList);
}