package pushdemo;

import java.util.concurrent.BlockingQueue;

public class MockPublisher implements Runnable {
    private BlockingQueue<Integer> queue;

    public MockPublisher(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0;i < 1000;i ++) {
            try {
                queue.put(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
