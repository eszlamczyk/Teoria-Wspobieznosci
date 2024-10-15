package agh.ernest;

public class DecrentThread implements Runnable {

    Counter counter;

    public DecrentThread(Counter counter){
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000000; i++) {
            counter.decrement();
        }
        System.out.println("Decrement end");
    }
}
