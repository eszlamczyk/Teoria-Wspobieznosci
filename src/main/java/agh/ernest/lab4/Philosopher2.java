package agh.ernest.lab4;

import java.util.Random;

public class Philosopher2 implements Runnable {
    private State state;
    private final Random random;

    private Chopstick leftChopstick;

    private Chopstick rightChopstick;
    private int number;

    private final PhilosopherObserver observer;

    public Philosopher2(Chopstick leftChopstick, Chopstick rightChopstick, int number, PhilosopherObserver observer) {
        this.observer = observer;
        this.state = State.thinking;
        this.random = new Random();
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
        this.number = number;
    }

    private void notifyStateChange() {
        observer.recordStateChange(number, this.state.next());
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + this.number + " think");
        Thread.sleep(random.nextLong(10,5000));
        notifyStateChange();
        this.state = state.next();
    }

    private void tryEating() throws InterruptedException {
        System.out.println("Philosopher " + this.number + " is trying to eat");
        leftChopstick.pickUp();
        Thread.sleep(5000);
        if(rightChopstick.tryPickUp()){
            notifyStateChange();
            this.state = state.next();
            return;
        }
        leftChopstick.drop();
        Thread.sleep(random.nextLong(10,5000));
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + this.number + " is eating");
        Thread.sleep(random.nextLong(10,5000));
        leftChopstick.drop();
        rightChopstick.drop();
        notifyStateChange();
        this.state = state.next();
    }


    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                switch (state) {
                    case thinking -> think();
                    case hungry -> tryEating();
                    case eating -> eat();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("PHILOSOPHER " + number + " ENDED");
    }
}