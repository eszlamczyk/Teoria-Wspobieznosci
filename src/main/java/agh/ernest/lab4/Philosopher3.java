package agh.ernest.lab4;

import java.util.Random;

public class Philosopher3 implements Runnable {
    private State state;
    private final Random random;

    private Chopstick leftChopstick;

    private Chopstick rightChopstick;
    private int number;

    private boolean pickupLeft;

    public Philosopher3(Chopstick leftChopstick, Chopstick rightChopstick, int number) {
        this.state = State.thinking;
        this.random = new Random();
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
        this.number = number;
        this.pickupLeft = number % 2 == 0;
    }


    private void think() throws InterruptedException {
        System.out.println("Philosopher " + this.number + " think");
        Thread.sleep(random.nextLong(10,5000));
        this.state = state.next();
    }

    private void tryEating() throws InterruptedException {
        System.out.println("Philosopher " + this.number + " is trying to eat");
        if (pickupLeft) {
            leftChopstick.pickUp();
            Thread.sleep(5000);
            rightChopstick.pickUp();
        } else {
            rightChopstick.pickUp();
            Thread.sleep(5000);
            leftChopstick.pickUp();
        }
        this.state = state.next();
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + this.number + " is eating");
        Thread.sleep(random.nextLong(10,5000));
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