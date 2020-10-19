import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Executor {

    public static void main(String args[]){

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.schedule(new Thread1("palani"), 5, TimeUnit.SECONDS);
        executor.schedule(new Thread1("meenu"), 5, TimeUnit.SECONDS);
    }

}

class Thread1 implements Runnable{

    private String name;

    public Thread1(String name){
        this.name = name;
    }
    @Override
    public void run() {
        System.out.println("hi " + name);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Yes");

    }
}
