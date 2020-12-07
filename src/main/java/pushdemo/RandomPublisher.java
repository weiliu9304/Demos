package pushdemo;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class RandomPublisher implements Runnable {
    private BlockingQueue<Integer> queue;
    private Random rand = new Random();

    public RandomPublisher(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            // publish rate is random
            try {
                int input = rand.nextInt(10);
                queue.offer(input);
                Thread.sleep(rand.nextInt(200));
//                Thread.sleep(3000);
            } catch (Exception e) { }
        }
    }
}
