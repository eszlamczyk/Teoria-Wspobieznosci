package agh.ernest.lab4;

public class Arbiter {

    private int N_philosophers;

    private int current_eating;

    public Arbiter(int N_philosophers) {
        this.N_philosophers = N_philosophers;
        this.current_eating = 0;
    }

    public synchronized void determineEating() throws InterruptedException {
        while (current_eating == N_philosophers - 1) {
            wait();
        }
        current_eating++;
    }

    public synchronized void stopEating() throws InterruptedException {
        current_eating--;
        notifyAll();
    }


}
