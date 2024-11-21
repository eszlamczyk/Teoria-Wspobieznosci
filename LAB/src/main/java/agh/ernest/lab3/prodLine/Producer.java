package agh.ernest.lab3.prodLine;

import java.util.Random;

public class Producer extends Thread {
    private final Buffer buffer;
    private final String producerName;

    public Producer(Buffer buffer, String producerName) {
        this.buffer = buffer;
        this.producerName = producerName;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Random rand = new Random();
                buffer.put(producerName + " " + i);
                sleep(rand.nextInt(100,500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Producer " + producerName + " finished");
    }
}
