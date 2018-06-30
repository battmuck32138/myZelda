

import java.io.Serializable;
import java.util.Random;

/*
Draws hexagons onto a 2-d array of tiles.
*/
class Hexagon implements Serializable {

    private static final long serialVersionUID = 139876581L;
    private Random rand;
    private TETile[][] world;
    private int worldWidth;
    private int worldHeight;


    Hexagon(TETile[][] world, Random rand, int worldWidth, int worldHeight) {
        this.world = world;
        this.rand = rand;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }


    /*
    Draws 2 hexagons, one within the other.
    Hexagon sizes and locations vary at random within a range.
     */
    public void addTemple() {
        int coinToss = rand.nextInt(100);
        int bigY;
        int bigSize;
        int bigX;
        int size;
        int x;
        int y;
        int bx;
        int by;

        if (coinToss < 40) {  //Large Temple
            bigSize = 9;
            size = 5;
            bigX = worldWidth - (worldWidth / 4) - 5;
            bigY = coinToss < 20 ? worldHeight - 4 : worldHeight / 2;
            x = bigX + 6;
            y = bigY - 4;
            bx = x + size + 1;
            by = y - (2 * size);

        } else {  //Small Temple
            bigSize = 6;
            size = 4;
            bigX = worldWidth - (worldWidth / 5) - rand.nextInt(10);
            bigY = coinToss < 70 ? worldHeight / 4 + 3 : worldHeight - 4;
            x = bigX + 3;
            y = bigY - 2;
            bx = x + size;
            by = y - 2 * size;
        }

        addHex(world, bigSize, bigX, bigY, Tileset.WATER);
        addHex(world, size, x, y, Tileset.TEMPLE);
        world[bx][by + size] = Tileset.LOCKED_DOOR;
    }


    //x and y are the top left most corner of the blanks space for the hex.
    private static void addHex(TETile[][] world, int size, int x, int y, TETile tile) {
        topHex(world, tile, size, x, y);
        bottomHex(world, tile, size, x, y);
    }


    //Displays the top half of a hex and returns the width at center.
    private static void topHex(TETile[][] world, TETile tile, int size, int x, int y) {
        int bodyLength = size;
        int numBlanks = size - 1;

        for (int i = 0; i < size; i++) {
            displayBody(world, tile, bodyLength, numBlanks, x, y);
            y--;
            numBlanks--;
            bodyLength = bodyLength + 2;
        }
    }


    //Displays the bottom half of the hex.
    private static void bottomHex(TETile[][] world, TETile tile, int size,
                                  int x, int y) {
        int numBlanks = 0;
        y = y - size;
        int bodyLength = sizeAtCenter(size);

        for (int i = 0; i < bodyLength; i++) {
            displayBody(world, tile, bodyLength, numBlanks, x, y);
            numBlanks++;
            bodyLength = bodyLength - 2;
            y--;
        }
    }


    //Draws the body of a hexagon.
    private static void displayBody(TETile[][] world, TETile tile, int bodyLength,
                                    int numBlanks, int x, int y) {

        //skips blanks
        for (int i = 0; i < numBlanks; i++) {
            x++;
        }

        //displays body
        for (int i = 0; i < bodyLength; i++) {
            world[x][y] = tile;
            x++;
        }
    }


    //Calculates the width at the center of the hexagon.
    private static int sizeAtCenter(int size) {
        int center = size;

        for (int i = 1; i < size; i++) {
            center = center + 2;
        }

        return center;
    }


}
