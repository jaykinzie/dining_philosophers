import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReceiverNode extends Thread{

    public int Id;
    private ConcurrentLinkedQueue<Message> inputQueue;
    private DatagramSocket socket;

    public ReceiverNode(int inId,DatagramSocket inSocket) {
        Id = inId;
        inputQueue = new ConcurrentLinkedQueue<Message>();
        socket = inSocket;
    }


    public void run(){

        while(true){
            try {
                byte[] data = new byte[200];
                DatagramPacket packet = new DatagramPacket(data, 200);
                socket.receive(packet);

                ByteArrayInputStream baos = new ByteArrayInputStream(data);
                ObjectInputStream oos = new ObjectInputStream(baos);
                Message message = (Message) oos.readObject();
                System.out.println("Receiver Node: " + Id);
                message.print();

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
