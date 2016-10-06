import java.util.*;

/**
 * Created by SerP on 25.09.2016.
 */
public class SortMountains {
    LinkedList<Mountain>  mnt = new LinkedList<Mountain>();

    public static void main(String[] args) {
        new SortMountains().go();
    }

    private void go() {
        mnt.add(new Mountain("Лонг Рейдж", 14255));
        mnt.add(new Mountain("Элтберт", 14433));
        mnt.add(new Mountain("Марун", 14156));
        mnt.add(new Mountain("Касл", 14265));

        System.out.println("В порядке добавления: \r\n" + mnt);

        NameCompare nc = new NameCompare<Mountain>();
        Collections.sort(mnt, nc);
        System.out.println("Поназванию: \r\n" + mnt);

        HeightCompare hc = new HeightCompare<Mountain>();
        Collections.sort(mnt, hc);
        System.out.println("По высоте: \r\n" + mnt);
    }

    private class NameCompare<T> implements Comparator<Mountain> {

        public int compare(Mountain o1, Mountain o2) {
            return o1.name.compareTo(o2.name);
        }
    }

    private class HeightCompare<T> implements Comparator<Mountain> {

        public int compare(Mountain o1, Mountain o2) {
            return String.valueOf(o2.height).compareTo(String.valueOf(o1.height));
        }
    }
}
