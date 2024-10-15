package agh.ernest;

public class SemaforLicznikowy {
    private int currentAvailableSlots;

    public SemaforLicznikowy (int poziom) {
        this.currentAvailableSlots = poziom;
    }
    public synchronized void sem_wait() {
        while (currentAvailableSlots == 0){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Wątek został przerwany.");
            }
        }
        currentAvailableSlots--;
    }
    public synchronized void sem_notify() {
        currentAvailableSlots++;
        notifyAll();

    }
}

