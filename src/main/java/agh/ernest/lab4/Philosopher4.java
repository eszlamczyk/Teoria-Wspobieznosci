package agh.ernest.lab4;

import java.util.Random;

public class Philosopher4 implements Runnable {
    private State state;
    private final Random random;

    private Chopstick leftChopstick;

    private Chopstick rightChopstick;
    private int number;

    private Arbiter arbiter;

    public Philosopher4(Chopstick leftChopstick, Chopstick rightChopstick, int number, Arbiter arbiter) {
        this.state = State.thinking;
        this.random = new Random();
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
        this.number = number;
        this.arbiter = arbiter;
    }


    private void think() throws InterruptedException {
        System.out.println("Philosopher " + this.number + " think");
        Thread.sleep(random.nextLong(1000,5000));
        this.state = state.next();
    }

    private void tryEating() throws InterruptedException {
        System.out.println("Philosopher " + this.number + " is trying to eat");
        arbiter.determineEating();
        leftChopstick.pickUp();
        Thread.sleep(5000);
        rightChopstick.pickUp();
        this.state = state.next();
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + this.number + " is eating");
        Thread.sleep(random.nextLong(1000,5000));
        leftChopstick.drop();
        rightChopstick.drop();
        this.state = state.next();
    }


    @Override
    public void run() {
        while (true) {
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
    }
}
