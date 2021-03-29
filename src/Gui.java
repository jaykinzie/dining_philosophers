import javax.sound.midi.SysexMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;


public class Gui extends Thread {

    JFrame frame;
    int windowX;
    int windowY;
    JButton[] buttons;

    JButton startButton;

    Fork[] forks;
    Philosopher[] philosophers;

    // semaphore used to control permits
    Semaphore pauseLock;

    // number of acquired permits the gui has from the locking semaphore
    Integer guiAcquiredLocks;

    public Gui(Fork[] inForks, Philosopher[] inPhilosophers, Semaphore inPauseLock, int inWindowX, int inWindowY) {

        forks = inForks;
        philosophers = inPhilosophers;
        windowX = inWindowX;
        windowY = inWindowY;

        pauseLock = inPauseLock;
        guiAcquiredLocks = 0;

        frame = new JFrame("Dining Philosophers");
        frame.setSize(new Dimension(windowX, windowY));
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // define the start button
        startButton = new JButton("Stopping...");
        // get the perfect size of the button
        Dimension size = startButton.getPreferredSize();
        startButton.setText("Running");
        // define size and place button
        startButton.setBounds(((windowX / 2) - (size.width / 2)), ((windowY / 2) - (size.height)), size.width, size.height);
        // place button in frame
        frame.add(startButton);
        // button click mechanism
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickStart();
            }
        });

        //find the angle between the philosophers
        double angle = 360.0 / this.philosophers.length;
        // radius that the philosophers reside around
        double tableRadius = (windowY * .35);
        // radius of the the circle that represents the philosopher

        // display the philosophers
        double accumulatedAngle = 0; //270
        for (int i = 0; i < this.philosophers.length; i++) {
            JButton button = new JButton("Philosopher " + i + "................");
            size = button.getPreferredSize();
            button.setText("Philosopher " + i);

            // associate button with the philosopher
            philosophers[i].setButton(button);

            int xButton = (int) (tableRadius * Math.cos(Math.toRadians(accumulatedAngle)));
            int yButton = (int) (tableRadius * Math.sin(Math.toRadians(accumulatedAngle)));

            button.setBounds((xButton + (windowX / 2) - (size.width / 2)), (yButton + (windowY / 2) - (size.height)), size.width, size.height);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clickPhilosopher();
                }
            });
            frame.add(button);
            accumulatedAngle += angle;
        }

        // display the forks at an angle that is shifted half off phase from the philosophers
        accumulatedAngle = -angle / 2;
        for (int i = 0; i < forks.length; i++) {
            JButton button = new JButton("Fork " + i + " Philosopher......");
            size = button.getPreferredSize();
            button.setText("Fork " + i);

            //associate the gui button to the correct fork object
            forks[i].setButton(button);

            int xButton = (int) (tableRadius * Math.cos(Math.toRadians(accumulatedAngle)));
            int yButton = (int) (tableRadius * Math.sin(Math.toRadians(accumulatedAngle)));


            button.setBounds((xButton + (windowX / 2) - (size.width / 2)), (yButton + (windowY / 2) - (size.height)), size.width, size.height);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clickFork();
                }
            });

            frame.add(button);
            accumulatedAngle += angle;
        }
        frame.setVisible(true);
    }

    private void clickPhilosopher() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                System.out.println("Clicked Philosopher!");
                return null;
            }
        };
        worker.execute();
    }

    private void clickFork() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                System.out.println("Clicked Fork!");
                return null;
            }
        };
        worker.execute();
    }

    private void clickStart() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // alternate "Start" "Stop" text on button
                // going from Running to Stopped
                if (startButton.getText().equals("Running")) {
                    //start the dining
                    //set button text to stop
                    startButton.setEnabled(false);
                    startButton.setText("Stopping...");
                    System.out.println("Pausing Dinner!");

                    pauseLock.acquire(philosophers.length);

                    startButton.setText("Stopped");
                    startButton.setEnabled(true);

                    //Going from Stopped to Running
                } else {
                    // set text to Running
                    startButton.setText("Running");
                    System.out.println("Starting Dinner!");
                    pauseLock.release(philosophers.length);
                }
                return null;
            }
        };
        worker.execute();
    }
}
