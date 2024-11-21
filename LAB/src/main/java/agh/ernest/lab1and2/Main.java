package agh.ernest.lab1and2;


public class Main {

    public static void main(String[] args) throws InterruptedException {

        Counter counter1 = new Counter();

        IncrementThread incrementThread = new IncrementThread(counter1);
        DecrentThread decrentThread = new DecrentThread(counter1);

        System.out.println("Starting threads");
        Thread thread = new Thread(incrementThread);
        Thread thread1 = new Thread(decrentThread);
        thread.start();
        thread1.start();

        thread1.join();
        thread.join();

        System.out.println(counter1.getX());
    }
}