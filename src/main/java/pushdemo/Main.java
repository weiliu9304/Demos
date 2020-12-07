package pushdemo;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(4);
        // prepare resources
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        Pusher pusher = new Pusher();
        // execute all threads
        service.execute(new RandomPublisher(queue));
//        service.execute(new MockPublisher(queue));
        service.execute(new MockClient(queue, pusher));
    }
}
