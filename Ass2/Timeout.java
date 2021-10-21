public class Timeout extends Thread{
    long timer;

    public Timeout(long timer){
        this.timer = timer;
        this.setName("TIMEOUT");
    }

    @Override
    public void run(){
        try{
            Thread.sleep(this.timer);
        } catch ( InterruptedException e ){
            System.err.printf("%s - sono stato interrotto\n", Thread.currentThread().getName());
            return;
        }
    }
}
