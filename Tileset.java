
import java.awt.Color;
import java.io.Serializable;


/*
Tileset objects are tiles made off any given color that are
overlaid with a given character / symbol of any given color.
The 2-D tile based Game is constructed with these Tileset Objects.
*/
public class Tileset implements Serializable {
    //Unique serialization number for this class.
    private static final long serialVersionUID = 1386L;


    public static final TETile WALL = new TETile('#',
            new Color(216, 128, 128), Color.darkGray, "WALL");

    public static final TETile FLOOR = new TETile('·',
            new Color(128, 192, 128), Color.black, "FLOOR");

    public static final TETile NOTHING = new TETile(' ',
            Color.black, Color.black, "NOTHING");

    public static final TETile GRASS = new TETile('"',
            Color.green, Color.black, "GRASS");

    public static final TETile WATER = new TETile('≈',
            Color.blue, Color.black, "ELVES HATE THE WATER");

    public static final TETile TRIFORCE = new TETile('▼',
            Color.yellow, Color.black,
            "PRINCESS ZELDA'S TRIFORCE");

    public static final TETile FLOWER = new TETile('❀',
            Color.magenta, Color.black,
            "I DARE YOU TO PICK THE FLOWER");

    public static final TETile BEANSTALK = new TETile('֍',
            Color.black, Color.green,
            "LINK CLIMBED UP THE BEANSTALK");

    public static final TETile SWIMPOTION = new TETile('Ϫ',
            Color.black, Color.blue,
            "MAGIC SWIM POTION");


    public static final TETile ROPE = new TETile('Ϭ',
            Color.black, Color.orange,
            "MOUNTAIN CLIMBING ROPE");


    public static final TETile WINE = new TETile('w',
            Color.black, Color.red,
            "ELF WINE IS CRAZY STRONG");


    public static final TETile LOCKED_DOOR = new TETile('▄',
            Color.black, Color.yellow,
            "LOCKED DOOR WITH A TRIANGLE KEY HOLE");

    public static final TETile CAVE = new TETile('☻',
            Color.black, Color.black, "CAVE LEADS TO THE CITADEL");

    public static final TETile BRIDGE = new TETile('▒', Color.cyan,
            Color.black, "BRIDGE");

    public static final TETile MOUNTAIN = new TETile('▲', Color.gray,
            Color.black, "MISTY MOUNTAINS");

    public static final TETile TREE = new TETile('♠', Color.green,
            Color.black, "ELVES LOVE THE LOST FORREST");

    public static final TETile LINK = new TETile('L', Color.BLACK,
            Color.green, "LINK IS BRAVE");

    public static final TETile SPIDER = new TETile('҈', Color.BLACK,
            Color.white, "SPIDER");

    public static final TETile REDGHOST = new TETile('҈', Color.black,
            Color.red, "REDGHOST");

    public static final TETile SKULL = new TETile('☠', Color.black,
            Color.white, "SKULL CREATURE");

    public static final TETile GHOSTKING = new TETile('♛', Color.black,
            Color.blue, "KING GHOST USED TO RULE");

    public static final TETile SUNGHOST = new TETile('Ѫ', Color.black,
            Color.orange, "SUNGHOST");

    public static final TETile FULLHEART = new TETile('❤', Color.red,
            Color.black, "FULLHEART");

    public static final TETile EMPTYHEART = new TETile('♡', Color.red,
            Color.black, "EMPTYHEART");

    public static final TETile TEMPLE = new TETile('ʘ', Color.black,
            Color.lightGray, "PRINCESS ZELDA'S TEMPLE");

}

