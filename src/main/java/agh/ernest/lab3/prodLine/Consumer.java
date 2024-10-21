package agh.ernest.lab3.prodLine;

import java.util.Random;

public class Consumer extends Thread {

    private final Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Random rand = new Random();
                buffer.get();
                sleep(rand.nextInt(100,1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
