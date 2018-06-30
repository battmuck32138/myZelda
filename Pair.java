
import java.io.Serializable;

public class Pair implements Serializable {

    //Unique serialization number for this class.
    private static final long serialVersionUID = 1382L;
    private int x;
    private int y;


    Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    @Override
    public String toString() {
        return "(" + x + ", " + y + ") ";
    }


}
