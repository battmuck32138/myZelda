import java.io.Serializable;
import java.util.Random;

/*
Enemy objects can be altered to have their own unique look
and personality (movement behaviours).
 */
public class Enemy implements Serializable {

    //Serialization number for Enemy class.
    private static final long serialVersionUID = 138189745210L;
    private Game game;
    private TETile tile;
    private TETile prevTile;
    private Pair location;
    private char direction;
    private int numSteps;
    private int numLoops;
    private Random rand;


    Enemy(Game game, TETile tile, TETile prevTile, Pair location, Random rand) {
        this.game = game;
        this.tile = tile;
        this.prevTile = prevTile;
        this.location = location;
        this.rand = rand;
        this.numSteps = enemyBehavior();
        this.numLoops = game.enemySpeed;
    }


    /*
    Moves the ghosts through the world randomly.
    */
    public void moveEnemy() {
        int x = location.getX();
        int y = location.getY();
        char[] directions = new char[] {'n', 's', 'e', 'w'};
        TETile tmp;

        if (numLoops < game.enemySpeed) {  //Adjust speed.
            numLoops++;
            return;
        }

        if (!noCollision(direction, location) || numSteps <= 0) {
            direction = directions[rand.nextInt(4)];
        }

        numSteps = (numSteps <= 0) ? enemyBehavior() : numSteps;

        switch (direction) {

            case 'n':  //north

                if (noCollision('n', location)) {
                    tmp = game.world[x][y + 1];
                    game.world[x][y + 1] = tile;
                    location = new Pair(x, y + 1);
                    game.world[x][y] = prevTile;
                    prevTile = tmp;
                    numLoops = 0;
                }
                break;

            case 's':  //south

                if (noCollision('s', location)) {
                    tmp = game.world[x][y - 1];
                    game.world[x][y - 1] = tile;
                    location = new Pair(x, y - 1);
                    game.world[x][y] = prevTile;
                    prevTile = tmp;
                    numLoops = 0;
                }
                break;

            case 'w':  //west

                if (noCollision('w', location)) {
                    tmp = game.world[x - 1][y];
                    game.world[x - 1][y] = tile;
                    location = new Pair(x - 1, y);
                    game.world[x][y] = prevTile;
                    prevTile = tmp;
                    numLoops = 0;
                }
                break;

            case 'e':  //east

                if (noCollision('e', location)) {
                    tmp = game.world[x + 1][y];
                    game.world[x + 1][y] = tile;
                    location = new Pair(x + 1, y);
                    game.world[x][y] = prevTile;
                    prevTile = tmp;
                    numLoops = 0;
                }
                break;

            default:
        }

        numSteps--;
        numLoops = 0;
    }


    //Chooses the number of steps the enemy takes before changing direction.
    private int enemyBehavior() {
        int coin = rand.nextInt(100);
        int steps;

        if (coin < 25) {  //25%
            steps = rand.nextInt(25);

        } else if (coin < 50) {  //25%
            steps = rand.nextInt(3);

        } else {  //50%
            steps = rand.nextInt(1);  //Adjust low to make ghost change directions often.
        }

        return steps;
    }


    /*
    Returns true if the enemy will not hit anything on it's next step, false if the
    enemy will hit something.
     */
    private boolean noCollision(char enemyDirection, Pair enemeyLocation) {
        int x = enemeyLocation.getX();
        int y = enemeyLocation.getY();

        switch (enemyDirection) {

            case 'n':  //north

                if (game.world[x][y + 1].equals(Tileset.LINK)) {
                    game.displayDeadLinkScreen();
                }

                if (game.walkableTiles.contains(game.world[x][y + 1])) {
                    return true;
                }
                break;

            case 's':  //south

                if (game.world[x][y - 1].equals(Tileset.LINK)) {
                    game.displayDeadLinkScreen();
                }

                if (game.walkableTiles.contains(game.world[x][y - 1])) {
                    return true;
                }
                break;

            case 'e':  //east

                if (game.world[x + 1][y].equals(Tileset.LINK)) {
                    game.displayDeadLinkScreen();
                }

                if (game.walkableTiles.contains(game.world[x + 1][y])) {
                    return true;
                }
                break;

            case 'w':

                if (game.world[x - 1][y].equals(Tileset.LINK)) {
                    game.displayDeadLinkScreen();
                }

                if (game.walkableTiles.contains(game.world[x - 1][y])) {
                    return true;
                }
                break;

            default:
        }

        return false;
    }


}
