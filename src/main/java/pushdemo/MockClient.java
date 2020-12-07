package pushdemo;



import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockClient implements Runnable {
    private BlockingQueue<Integer> queue;
    private Pusher pusher;

    public MockClient(BlockingQueue<Integer> queue, Pusher pusher) {
        this.queue = queue;
        this.pusher = pusher;
    }

    @Override
    public void run() {
        while (true) {
            // Each batch will be finished within one second, so it can not guarantee
            // batch size, just for test
            try {
                List<Integer> bufferList = IntStream.range(0 , 10).boxed().map(idx -> {
                    try {
                        return queue.poll(100, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                       return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
                // processing hook
                processRecords(bufferList);
            } catch (Exception e) { }
        }
    }

    // fake kinesis client interface
    private void processRecords(List<Integer> values) {
        List<Boolean> resultList = values.stream().map(value -> pusher.add(value)).collect(Collectors.toUnmodifiableList());
        long successCount = resultList.stream().filter(value -> value).count();
        long failureCount = resultList.stream().filter(value -> !value).count();
        System.out.println(String.format("Total count: %d, successCount: %d, failedCount: %d", values.size(), successCount, failureCount));
    }
}
