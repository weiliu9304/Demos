package demos;

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * R2DBC investigation
 */
public class Demo4 {

    private static final String SQL = "select SQL_NO_CACHE * from waggle limit 20000";

    /**
     * Simulate sn-reaction endpoints
     */
    private static void action(List<Integer> batchList) {
        try {
            Thread.sleep(200);
//            System.out.println(Thread.currentThread().getName() + " " + batchList.get(0));
        } catch (Exception e) {
        }
    }

    private static void example1() throws SQLException, ClassNotFoundException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Class.forName("com.mysql.cj.jdbc.Driver");
        java.sql.Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?" +
                "user=root&password=smartnews");

        long startTime = System.currentTimeMillis();
        List<Integer> resultList = new ArrayList<>();
        ResultSet resultSet = conn.createStatement().executeQuery(SQL);
        while (resultSet.next()) {
            resultList.add(resultSet.getInt("id"));
        }
        List<List<Integer>> batchList = resultList.stream().collect(Collectors.groupingBy(index -> (index - 1) / 100)).entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toUnmodifiableList());
        System.out.println("Example1 batch num: " + batchList.size());
        List<Future<?>> futureList = batchList.stream().map(batch -> executorService.submit(() -> action(batch))).collect(Collectors.toUnmodifiableList());
        futureList.stream().forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
            }
        });
        executorService.shutdown();
        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Running time is %dms" , runningTime));
    }

    private static void example2() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        MySqlConnectionConfiguration configuration = MySqlConnectionConfiguration.builder()
                .host("127.0.0.1")
                .user("root")
                .port(3306)
                .password("smartnews")
                .database("test")
                .build();
        ConnectionFactory connectionFactory = MySqlConnectionFactory.from(configuration);
        Mono<? extends Connection> connectionMono = Mono.just(Mono.from(connectionFactory.create()).block());

        long startTime = System.currentTimeMillis();
        connectionMono
                .flatMapMany(connect -> Flux.from(connect.createStatement(SQL).execute()))
                .flatMap(result -> result.map((row, metadata) -> (Integer) row.get("id")))
                .buffer(100)
                .map(batch ->
                    executorService.submit(() -> action(batch))
                )
                .collectList()
                .block()
                .stream()
                .forEach(future -> {
                    try {
                        future.get();
                    } catch (Exception e) {
                    }
                });
        executorService.shutdown();
        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Running time is %dms" , runningTime));
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        System.out.println("----------- example1 -----------");
        example1();
        System.out.println("----------- example1 -----------");

        System.out.println("----------- example2 -----------");
        example2();
        System.out.println("----------- example2 -----------");
    }
}
