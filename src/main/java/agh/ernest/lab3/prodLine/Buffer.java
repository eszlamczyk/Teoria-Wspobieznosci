package agh.ernest.lab3.prodLine;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {

    private final int N;

    private int currentAvailableSpace;

    private final Queue<String> buffer;

    public Buffer(int N) {
        this.N = N;
        this.currentAvailableSpace = N;
        this.buffer = new LinkedList<>();
    }

    public synchronized void put(String s) throws InterruptedException {
        while (currentAvailableSpace == 0){
            wait();
        }
        currentAvailableSpace--;
        System.out.println("Buffer got new stuff: " + s +", current available space: " + currentAvailableSpace);
        buffer.add(s);
        notifyAll();
    }

    public synchronized String get() throws InterruptedException {
        while (currentAvailableSpace == N){
            wait();
        }
        String returnValue = buffer.remove();
        currentAvailableSpace++;
        System.out.println("Buffer gave new stuff: " + returnValue +", current available space: " + currentAvailableSpace);
        notifyAll();
        return returnValue;
    }

}
