import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Frame {
    int x, y;
    int px, py;
    int next = 1;
    int nextSize = 5;
    int numSteps;
    int state;
    int turningPoint = 1;
    int totalSteps;
    ArrayList<Integer> primes;

    public static boolean isPrime(int num, ArrayList<Integer> primes) {
        if (num <= 1) {
            return false;
        }
        for (int prime : primes) {
            if (prime > Math.sqrt(num)) {
                break;
            }
            if (num % prime == 0) {
                return false;
            }
        }
        return true;
    }
    public void savePrimesToFile() {
        try {
            DataOutputStream output = new DataOutputStream(new FileOutputStream("SavedPrimeNumbers.bin"));

            for (int i = 1; i <= 4; i++) {
                ArrayList<Integer> temp = new ArrayList<>();

                for (int prime : primes) {
                    if (prime < Math.pow(2, i * 8)) {
                        temp.add(prime);
                    }
                }

                output.writeLong(temp.size());

                for (int prime : temp) {
                    output.write(getBytes(prime, i));
                }
            }

            output.close();
        } catch (IOException e) {
            System.err.println("Error saving primes to binary file.");
            e.printStackTrace();
        }
    }

    public byte[] getBytes(int num, int numBytes) {
        byte[] bytes = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            bytes[i] = (byte) (num >> (i * 8));
        }
        return bytes;
    }

    Main() {
        setSize(750, 750);
        setVisible(true);

        int cols = getWidth() / nextSize;
        int rows = getHeight() / nextSize;
        totalSteps = cols * rows;

        x = getWidth() / 2;
        y = getHeight() / 2;
        px = x;
        py = y;
        numSteps = 1;
        state = 0;
        turningPoint = 1;

        primes = new ArrayList<>();
        primes.add(2);
        for (int num = 3; num < totalSteps * 100; num += 2) {
            if (isPrime(num, primes)) {
                primes.add(num);
            }
        }

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int newSize = Math.min(getWidth(), getHeight());
                setSize(newSize, newSize);

                int newCols = getWidth() / nextSize;
                int newRows = getHeight() / nextSize;

                totalSteps = newCols * newRows;
                x = getWidth() / 2;
                y = getHeight() / 2;
                px = x;
                py = y;
                numSteps = 1;
                next = 1;
                state = 0;
                turningPoint = 1;

                repaint();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void paint(Graphics g) {
        super.paint(g);

        while (next <= totalSteps) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.PINK);
            g2.setStroke(new BasicStroke(1));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isPrime(next, primes )) {
                Ellipse2D.Double circle = new Ellipse2D.Double(x - nextSize / 2, y - nextSize / 2, nextSize, nextSize);
                g2.fill(circle);
                g2.draw(circle);
            }

            px = x;
            py = y;
            switch (state) {
                case 0:
                    x += nextSize;
                    break;
                case 1:
                    y -= nextSize;
                    break;
                case 2:
                    x -= nextSize;
                    break;
                case 3:
                    y += nextSize;
                    break;
            }

            if (next % numSteps == 0) {
                state = (state + 1) % 4;
                turningPoint++;
                if (turningPoint % 2 == 0) {
                    numSteps++;
                }
            }
            next++;

            if (next > totalSteps) {
                break;
            }
        }
    }


    public static void main(String[] args) {
        Main ulamSpiral = new Main();
        ulamSpiral.setVisible(true);

        ulamSpiral.savePrimesToFile();
    }
}
