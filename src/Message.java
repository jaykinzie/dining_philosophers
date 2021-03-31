import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable {

    public long Id;
    public long queueSize;
    public byte[] message;

    public Message(long inQueueSize, long inId, byte philosopher, byte fork1, byte fork2) {
        Id = inId;
        queueSize = inQueueSize;
        message = new byte[]{philosopher, fork1, fork2};
    }

    public void print() {
        System.out.println("ID: " + Id + "  Philosopher:" + message[0] + "  Fork: " + message[1] + "  Fork: " + message[2] + "  Queue Size: " + queueSize);
    }

    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        inputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        //perform the default serialization for all non-transient, non-static fields
        outputStream.defaultWriteObject();
    }

    }
