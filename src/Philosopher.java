import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class Philosopher extends Thread {

    // identifier for the Philosopher
    byte Id;

    //the 2 forks that are next to the philosopher
    public Fork[] adjacentForks;

    // array of mutex forks
    public Fork[] forks;

    // button that is associated with the philosopher from the gui
    public JButton button;

    // used to control available permits for threads to pause and unpause operation
    Semaphore pauseLock;

    public static final String broadcastIp = "239.255.255.255";
    public static final int broadcastPort = 2222;

    // non blocking queue
    private ConcurrentLinkedQueue<Message> outputQueue;

    //Message Number
    AtomicLong[] messageNumber;

    // thread unloads the queue
    PhilosopherQueueUnloader unloader;


    public Philosopher(byte inId, Fork[] inForks, Semaphore inPauseLock, AtomicLong[] inMessageNumber, DatagramSocket inSocket) {
        Id = inId;
        forks = inForks;
        pauseLock = inPauseLock;
        // adjacent forks
        adjacentForks = new Fork[2];
        adjacentForks[0] = inForks[Id];
        // modulo is for the last philosopher in the list having one of the
        // adjacent forks be fork 0
        adjacentForks[1] = inForks[ (Id+1) % inForks.length];
        // output queue used to store
        outputQueue = new ConcurrentLinkedQueue<Message>();

        messageNumber = inMessageNumber;

        // start thread that unloads the queue
        unloader = new PhilosopherQueueUnloader(outputQueue, inSocket);
        unloader.start();
    }

    // associate the philosopher with a button in the gui
    public void setButton(JButton inButton) {
        button = inButton;
    }

    // used to provide a bounded range of dealy; commonly to simulate eating
    private void randomDelay(int inDelayMax, int inDelayMin) {
        Random r = new Random();
        try {
            Thread.sleep(r.nextInt(inDelayMax - inDelayMin) + inDelayMin);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        //randomDelay(1000, 100);
        System.out.println("Philosopher " + this.Id + " is ready!");


        //index to keep track of which fork we are working on
        int forkIndex = 0;
        // a place to save the message number until later
        long messageAccumulator = 0;
        while (true) {

            // TODO: make sure to trim forkIndex if it gets too big
            forkIndex++;
            //acquire the lock from the semaphore to run the job
            try {
                pauseLock.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pauseLock.release();

            if(adjacentForks[forkIndex % 2].tryLock(this)){
                //a lock is acquired; advance the counter to get the other fork
                forkIndex++;
                if(adjacentForks[forkIndex % 2].tryLock(this)){

                    button.setEnabled(false);
                    button.setText("Philosopher " + Id + " Eating!");
                    //get the message number
                    messageAccumulator = messageNumber[0].getAndIncrement();

                    //add a message to the non blocking output queue
                    outputQueue.add( new Message(outputQueue.size(),messageAccumulator,Id,adjacentForks[0].Id,adjacentForks[1].Id ) );

                    randomDelay(200, 50);
                    //System.out.println("Philosopher " + this.Id + " is eating!");
                    //randomDelay(500, 100);
                    //System.out.println("Philosopher " + this.Id + " is done eating!");


                    adjacentForks[forkIndex % 2].release();
                    button.setText("Philosopher " + Id);
                    button.setEnabled(true);
                }

                // advance counter to operate on the original fork
                forkIndex++;
                adjacentForks[forkIndex % 2].release();
                randomDelay(1000, 800);
            }
            // ensures the switching index is never over-run
            forkIndex = forkIndex % 100;
        }
    }
}







