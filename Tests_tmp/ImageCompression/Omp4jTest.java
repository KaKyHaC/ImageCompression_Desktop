package ImageCompression;
import org.junit.Test;
import org.omp4j.grammar.OMPParser;
import org.omp4j.*;


public class Omp4jTest {

    @Test
    public void ClientTest() {

    }

    @Test
    public void OMPParallel() {
        int foo = 5;
        // omp parallel
        {
            System.out.println("hello");
            System.out.println("world");
            System.out.println(foo);
        }

        System.out.println("last line");
    }
    @Test
    public void OmpSectionsTest() throws InterruptedException {
        // omp sections
        {
            // omp section
            {
                System.out.println("task1 start");
                Thread.sleep(1000);
                System.out.println("task1 stop");
            }

            // omp section
            {
                System.out.println("task2 start");
                Thread.sleep(500);
                System.out.println("task2 stop");
            }

            // omp section
            {
                System.out.println("task3 start");
                Thread.sleep(1);
                System.out.println("task3 stop");
            }
        }
    }
    @Test
    public void ThreadTest(){
        new Thread(() -> {
            System.out.println("task1 start");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("task1 stop");
        }).start();
        new Thread(() -> {
            System.out.println("task2 start");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("task2 stop");
        }).start();
        new Thread(() -> {
            System.out.println("task3 start");
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("task3 stop");
        }).start();
    }
    @Test
    public void RuntimeTest(){
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

}

