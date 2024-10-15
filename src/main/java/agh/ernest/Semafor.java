package agh.ernest;

class Semafor {
    private boolean stan = true ;
    public Semafor () {
    }
    public synchronized void sem_wait() {
        while (!stan){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Wątek został przerwany.");
            }
        }
        stan = false;
    }
    public synchronized void sem_notify() {

        stan = true;
        notify();
    }
}