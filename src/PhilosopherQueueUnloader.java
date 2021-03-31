import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PhilosopherQueueUnloader extends Thread{

    private ConcurrentLinkedQueue<Message> outputQueue;
    private DatagramSocket socket;

    public PhilosopherQueueUnloader(ConcurrentLinkedQueue<Message> inOutputQueue, DatagramSocket inSocket) {
        outputQueue = inOutputQueue;
        socket = inSocket;
    }


    public void run() {

        Message message;

        InetAddress client = null;
        try {
            client = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // loop sending the queue to the networking interface
        while(true){

            // check if the queue has data in it
            if( null != outputQueue.peek() ){

                // send data through socket
               // socket.
                message = outputQueue.poll();
                message.print();

                try {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(message);
                    oos.flush();

                    // get the byte array of the object

                    byte[] Buf = new byte[200];//baos.toByteArray();

                    byte[] tempBuf = baos.toByteArray();

                    for(int i=0;i<tempBuf.length;i++)
                    {
                        Buf[i] = tempBuf[i];
                    }


                    DatagramPacket packet = new DatagramPacket(Buf,Buf.length,client,1234);
                    socket.send(packet);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                // wait if the queue is empty
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
