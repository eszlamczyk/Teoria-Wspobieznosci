package agh.ernest.lab4;

public class Chopstick {

    private boolean pickedUp = false;

    public Chopstick() {

    }

    public synchronized void pickUp() throws InterruptedException {
        while (pickedUp){
            wait();
        }
        pickedUp = true;
    }

    public synchronized void drop() throws InterruptedException {
        pickedUp = false;
        notifyAll();
    }

    public synchronized boolean tryPickUp() throws InterruptedException {
        if (!pickedUp){
            pickedUp = true;
            return true;
        }
        return false;
    }

}
