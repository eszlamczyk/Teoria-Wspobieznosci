package agh.ernest.lab1and2;

public class Counter{

    public static int x = 0;
    SemaforLicznikowy semafor = new SemaforLicznikowy(1);

    public Counter(){
    }

    public void increment(){
        semafor.sem_wait();
        x++;
        semafor.sem_notify();
    }
    public void decrement(){
        semafor.sem_wait();
        x--;
        semafor.sem_notify();
    }

    public int getX() {
        return x;
    }

}
