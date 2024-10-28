package agh.ernest.lab4;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        initializePhilosophers(Philosopher.class, "Philosopher1");
        initializePhilosophers(Philosopher2.class, "Philosopher2");
        initializePhilosophers(Philosopher3.class, "Philosopher3");
        initializePhilosophersWithArbiter(Philosopher4.class, "Philosopher4", 5);
    }

    public static <T extends Runnable> void initializePhilosophers(Class<T> philosopherClass, String philosopherName) {
        ArrayList<Chopstick> chopsticks = new ArrayList<>();
        ArrayList<T> philosophers = new ArrayList<>();
        ArrayList<Thread> philosopherThreads = new ArrayList<>();

        PhilosopherObserver observer = new PhilosopherObserver();

        for (int i = 0; i < 5; i++) {
            chopsticks.add(new Chopstick());
        }

        try {
            for (int i = 1; i < 5; i++) {
                philosophers.add(philosopherClass.getConstructor(Chopstick.class, Chopstick.class, int.class, boolean.class, PhilosopherObserver.class)
                        .newInstance(chopsticks.get(i - 1), chopsticks.get(i), i - 1, false, observer));
            }
            philosophers.add(philosopherClass.getConstructor(Chopstick.class, Chopstick.class, int.class, boolean.class, PhilosopherObserver.class)
                    .newInstance(chopsticks.get(4), chopsticks.get(0), 4, false, observer));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        philosophers.forEach(philosopher -> {
            Thread thread = new Thread(philosopher);
            philosopherThreads.add(thread);
            thread.start();
        });

        philosopherThreads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        observer.generateReport(philosopherName);
    }

    public static <T extends Runnable> void initializePhilosophersWithArbiter(Class<T> philosopherClass, String philosopherName, int numPhilosophers) {
        ArrayList<Chopstick> chopsticks = new ArrayList<>();
        ArrayList<T> philosophers = new ArrayList<>();
        ArrayList<Thread> philosopherThreads = new ArrayList<>();

        Arbiter arbiter = new Arbiter(numPhilosophers);
        PhilosopherObserver observer = new PhilosopherObserver();

        // Create chopsticks
        for (int i = 0; i < numPhilosophers; i++) {
            chopsticks.add(new Chopstick());
        }

        // Create philosophers and assign chopsticks with Arbiter and Observer
        try {
            for (int i = 1; i < numPhilosophers; i++) {
                philosophers.add(philosopherClass.getConstructor(Chopstick.class, Chopstick.class, int.class, boolean.class, PhilosopherObserver.class, Arbiter.class)
                        .newInstance(chopsticks.get(i - 1), chopsticks.get(i), i - 1, false, observer, arbiter));
            }
            philosophers.add(philosopherClass.getConstructor(Chopstick.class, Chopstick.class, int.class, boolean.class, PhilosopherObserver.class, Arbiter.class)
                    .newInstance(chopsticks.get(numPhilosophers - 1), chopsticks.get(0), numPhilosophers - 1, false, observer, arbiter));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Start philosopher threads
        philosophers.forEach(philosopher -> {
            Thread thread = new Thread(philosopher);
            philosopherThreads.add(thread);
            thread.start();
        });

        // Join philosopher threads
        philosopherThreads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Generate report after all threads have finished
        observer.generateReport(philosopherName);
    }
}
