/**
 * Created by SerP on 25.09.2016.
 */
class ThreadOne implements Runnable {
    public void run() {
        Accum a = Accum.getAccum();
        for (int i = 0; i < 98; i++) {
            a.updateCounter(1000);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("один " + a.getCounter());
    }
}
