package agh.ernest.lab3;

import agh.ernest.lab3.prodLine.Buffer;
import agh.ernest.lab3.prodLine.Consumer;
import agh.ernest.lab3.prodLine.Producer;

public class Main1 {
    public static void main(String[] args) throws InterruptedException {
        Buffer buffer = new Buffer(2);
        //Producer producer = new Producer(buffer, "Producer1");
        Producer producer2 = new Producer(buffer, "Producer2");
        Consumer consumer = new Consumer(buffer);

        //Thread threadProducer = new Thread(producer);
        //threadProducer.start();
        Thread threadProducer2 = new Thread(producer2);
        threadProducer2.start();
        Thread threadConsumer = new Thread(consumer);
        threadConsumer.start();

        threadConsumer.join();
        //threadProducer.join();
        threadProducer2.join();
    }

}
