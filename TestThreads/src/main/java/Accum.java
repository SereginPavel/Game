/**
 * Created by SerP on 25.09.2016.
 */
class Accum {
    private int counter = 0;
    private static Accum a = new Accum();

    private Accum(){}

    public void updateCounter(int add){
        counter += add;
    }

    public int getCounter() {
        return counter;
    }

    public static Accum getAccum() {
        return a;
    }
}
