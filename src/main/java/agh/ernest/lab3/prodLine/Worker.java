package agh.ernest.lab3.prodLine;

public class Worker extends Thread {

    private final Buffer recieveBuffer;

    private final Buffer sendBuffer;

    private final String name;

    public Worker(Buffer recieveBuffer, Buffer sendBuffer, String name) {
        this.recieveBuffer = recieveBuffer;
        this.sendBuffer = sendBuffer;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String s = recieveBuffer.get();
                System.out.println(name + " got " + s);
                sendBuffer.put(s);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
