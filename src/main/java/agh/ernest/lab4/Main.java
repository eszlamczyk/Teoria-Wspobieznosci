package agh.ernest.lab4;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
//        ArrayList<Chopstick> chopsticks = new ArrayList<>();
//        ArrayList<Philosopher> philosophers = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            chopsticks.add(new Chopstick());
//        }
//        for (int i = 1; i < 5; i++) {
//            philosophers.add(new Philosopher(chopsticks.get(i-1), chopsticks.get(i), i-1));
//        }
//        philosophers.add(new Philosopher(chopsticks.get(4), chopsticks.get(0), 4));
//
//        philosophers.forEach(philosopher -> new Thread(philosopher).start());

        ArrayList<Chopstick> chopsticks = new ArrayList<>();
        ArrayList<Philosopher2> philosophers = new ArrayList<>();
        ArrayList<Thread> philosopherThreads = new ArrayList<>();

        PhilosopherObserver philosopherObserver = new PhilosopherObserver();

        for (int i = 0; i < 5; i++) {
            chopsticks.add(new Chopstick());
        }

        for (int i = 1; i < 5; i++) {
            philosophers.add(new Philosopher2(chopsticks.get(i-1), chopsticks.get(i), i-1, philosopherObserver));
        }
        philosophers.add(new Philosopher2(chopsticks.get(4), chopsticks.get(0), 4, philosopherObserver));

        for (Philosopher2 philosopher : philosophers) {
            Thread philosopherThread = new Thread(philosopher);
            philosopherThreads.add(philosopherThread);
            philosopherThread.start();
        }

        // Join all philosopher threads
        philosopherThreads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Generate report after all threads have finished
        philosopherObserver.generateReport();

//
//        ArrayList<Chopstick> chopsticks = new ArrayList<>();
//        ArrayList<Philosopher3> philosophers = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            chopsticks.add(new Chopstick());
//        }
//        for (int i = 1; i < 5; i++) {
//            philosophers.add(new Philosopher3(chopsticks.get(i-1), chopsticks.get(i), i-1));
//        }
//        philosophers.add(new Philosopher3(chopsticks.get(4), chopsticks.get(0), 4));
//
//        philosophers.forEach(philosopher -> new Thread(philosopher).start());
//
//
//
//        ArrayList<Chopstick> chopsticks = new ArrayList<>();
//        ArrayList<Philosopher4> philosophers = new ArrayList<>();
//
//        Arbiter arbiter = new Arbiter(5);
//
//        for (int i = 0; i < 5; i++) {
//            chopsticks.add(new Chopstick());
//        }
//        for (int i = 1; i < 5; i++) {
//            philosophers.add(new Philosopher4(chopsticks.get(i-1), chopsticks.get(i), i-1, arbiter));
//        }
//        philosophers.add(new Philosopher4(chopsticks.get(4), chopsticks.get(0), 4, arbiter));
//
//        philosophers.forEach(philosopher -> new Thread(philosopher).start());
    }

}
