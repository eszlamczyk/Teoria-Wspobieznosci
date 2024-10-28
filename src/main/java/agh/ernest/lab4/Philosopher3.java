package agh.ernest.lab4;

import java.util.Random;

public class Philosopher3 implements Runnable {
    private State state;
    private final Random random;

    private final Chopstick leftChopstick;

    private final Chopstick rightChopstick;
    private final int number;

    private final boolean pickupLeft;

    private final boolean debugMode;

    private final PhilosopherObserver observer;



    public Philosopher3(Chopstick leftChopstick, Chopstick rightChopstick, int number, boolean debugMode, PhilosopherObserver observer) {
        this.debugMode = debugMode;
        this.observer = observer;
        this.state = State.thinking;
        this.random = new Random();
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
        this.number = number;
        this.pickupLeft = number % 2 == 0;
    }

    private void notifyStateChange() {
        observer.recordStateChange(number, this.state.next());
    }


    private void think() throws InterruptedException {
        if(debugMode){
            System.out.println("Philosopher " + this.number + " think");
        }
        Thread.sleep(random.nextLong(10,50));
        notifyStateChange();
        this.state = state.next();
    }

    private void tryEating() throws InterruptedException {
        if (debugMode){
            System.out.println("Philosopher " + this.number + " is trying to eat");
        }
        if (pickupLeft) {
            leftChopstick.pickUp();
            if(debugMode){
                Thread.sleep(5000);
            }
            rightChopstick.pickUp();
        } else {
            rightChopstick.pickUp();
            if(debugMode){
                Thread.sleep(5000);
            }

            leftChopstick.pickUp();
        }
        notifyStateChange();
        this.state = state.next();
    }

    private void eat() throws InterruptedException {
        if(debugMode){
            System.out.println("Philosopher " + this.number + " is eating");
        }
        Thread.sleep(random.nextLong(10,50));
        leftChopstick.drop();
        rightChopstick.drop();
        notifyStateChange();
        this.state = state.next();
    }


    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
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