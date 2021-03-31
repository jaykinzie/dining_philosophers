import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // the number of guests at the table. E.g. 4 philosophers.
        final int guests = 10;
        final int windowX = 1000;
        final int windowY = 1000;

        Semaphore pauseLock = new Semaphore(guests,true);
        AtomicLong[] messageNumber = new AtomicLong[1];
        messageNumber[0] = new AtomicLong(0);

        // networking socket
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(1234);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // initialize forks
        Fork[] forks = new Fork[guests];
        for(byte i=0;i<guests;i++){
            //initialize fork at index [i] to a fork id of [i]
            Fork currentFork = new Fork(i);
            forks[i] = currentFork;
            System.out.println("Fork " + i + " created");
        }

        // initialize the array of philosophers
        Philosopher[] philosophers = new Philosopher[guests];
        for(byte i=0;i<guests;i++){
            //initialize philosopher at index [i] to id of [i]
            Philosopher currentPhilosopher = new Philosopher(i,forks,pauseLock, messageNumber ,socket);
            philosophers[i] = currentPhilosopher;
            System.out.println("philosopher " + i + " created");
        }

        // create and start the load nodes
        final int numberOfRecieverNodes = 5;
        ReceiverNode[] recieverNodes = new ReceiverNode[numberOfRecieverNodes];
        for(int i=0;i<numberOfRecieverNodes;i++){
            recieverNodes[i] = new ReceiverNode(i,socket);
            recieverNodes[i].start();
        }

        // create and start the gui thread
        Gui gui = new Gui(forks, philosophers,pauseLock,windowX, windowY);
        gui.start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // pause
        pauseLock.acquireUninterruptibly(guests);

        // start the philosophers
        for(Philosopher currentPhilosopher : philosophers){
            currentPhilosopher.start();
        }

        // period to make sure all threads are fully setup
        try
        {
            Thread.sleep(200);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        System.out.println("Dinner can begin!" );

        pauseLock.release(guests);
    }
}
