import javax.swing.*;
import java.util.concurrent.locks.ReentrantLock;

public class Fork {
    // identifier
    public int Id;
    // associated button in the gui
    public JButton button;
    // lock to represent if the fork is being held by a philosopher
    public ReentrantLock lock;

    public Fork(int inId) {
        Id = inId;
        lock = new ReentrantLock();
    }

    // button to associate a fork with a button in the gui
    public void setButton(JButton inButton) {
        button = inButton;
    }

    public boolean tryLock(Philosopher inPhilosopher) {

        // String currentText = button.getText();

        // try to get the lock
        if (lock.tryLock()) {
            // disable the button
            button.setText("Fork " + Id + " Philosopher " + inPhilosopher.Id);
            button.setEnabled(false);
            return true;
        }
        return false;
    }

    public void release() {
        // release the lock for the fork to become available again
        lock.unlock();
        // enable the button
        button.setEnabled(true);
        // set the original text
        button.setText("Fork " + Id);
    }
}
