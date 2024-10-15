package agh.ernest;

public class IncrementThread implements Runnable{

    Counter counter;

    public IncrementThread(Counter counter){
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000000; i++) {
            counter.increment();
        }
    }
}
