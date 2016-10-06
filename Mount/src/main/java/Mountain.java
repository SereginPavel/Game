/**
 * Created by SerP on 25.09.2016.
 */
public class Mountain {
    public String name;
    public int height;

    public Mountain(String name, int height) {
        this.name = name;
        this.height = height;
    }

    @Override
    public String toString() {
        return name + " " + height;
    }
}
