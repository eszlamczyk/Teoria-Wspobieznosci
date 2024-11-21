package agh.ernest.lab3;

import agh.ernest.lab3.prodLine.Buffer;
import agh.ernest.lab3.prodLine.Consumer;
import agh.ernest.lab3.prodLine.Producer;
import agh.ernest.lab3.prodLine.Worker;

public class Main2 {
    public static void main(String[] args) {
        Buffer buffer1 = new Buffer(2);
        Buffer buffer2 = new Buffer(2);
        Buffer buffer3 = new Buffer(2);

        Producer producer1 = new Producer(buffer1, "Producer 1");
        Producer producer2 = new Producer(buffer2, "Producer 2");

        Worker workerA1 = new Worker(buffer1,buffer2,"worker A1");
        Worker workerA2 = new Worker(buffer1,buffer2,"worker A2");

        Worker workerB1 = new Worker(buffer2,buffer3,"worker B1");
        Worker workerB2 = new Worker(buffer2,buffer3,"worker B2");

        Consumer consumer1 = new Consumer(buffer3);
        Consumer consumer2 = new Consumer(buffer3);


        // Start producer threads
        producer1.start();
        producer2.start();

        workerA1.start();
        workerA2.start();
        workerB1.start();
        workerB2.start();

        consumer1.start();
        consumer2.start();

        try {
            producer1.join();
            producer2.join();
            workerA1.join();
            workerA2.join();
            workerB1.join();
            workerB2.join();
            consumer1.join();
            consumer2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
