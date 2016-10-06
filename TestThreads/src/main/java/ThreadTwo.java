/**
 * Created by SerP on 25.09.2016.
 */
class ThreadTwo implements Runnable{
    public void run() {
        Accum a = Accum.getAccum();
        for (int i = 0; i < 99; i++) {
            a.updateCounter(1);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("два " + a.getCounter());
    }
}
