
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/*
A structure is a series of 1 or more rectangular rooms
connected by hallways which are 1 tile wide.
*/
public class Structure implements Serializable {
    private static final long serialVersionUID = 1384L;  //Serialization number for this class.
    private TETile[][] world;
    private Random rand;
    private int maxWall;  //Max length or width of rooms.
    private double roomsToSpaceRate;
    private final int widthOfDungeon;
    private final int heightOfDungeon;
    private int roomId = 1;  //Each room has unique id
    private int[][] rooms;  //(x, y) contains the roomNumber of the room drawn there.
    private int floorTilesUsed = 0;  //Number of floor tiles = inside area of the dungeon.
    private boolean inside = false;  //True when drawing floor tiles only.
    private boolean drawingHallway = false;
    private ArrayList<Path> hallways = new ArrayList<>();  //All possible paths between rooms.


    Structure(TETile[][] world, Random rand, int maxWall,
                     int widthOfDungeon, int heightOfDungeon) {
        this.world = world;
        this.rand = rand;
        this.maxWall = maxWall;
        this.widthOfDungeon = widthOfDungeon;
        this.heightOfDungeon = heightOfDungeon;
        this.rooms = new int[widthOfDungeon][heightOfDungeon];
        int coin = rand.nextInt(100);
        this.roomsToSpaceRate = coin < 50 ? 0.4 : 0.2;
    }


    public void buildStructure() {

        while (!hasEnoughRooms()) {
            drawSingleRoom();
            roomId++;
        }

        drawHallways();
    }


    /*
    Returns true if the dungeon structure's density has reached the specified
    RoomsToSpaceRate i.e. enough rooms already exist.
    */
    private boolean hasEnoughRooms() {
        double roomsToSpace = floorTilesUsed / (double) (widthOfDungeon * heightOfDungeon);
        return roomsToSpace > roomsToSpaceRate;  //Adjust up for a denser dungeon structure.
    }


    //Draws a room object on the world.
    private void drawSingleRoom() {
        int x;
        int y;
        int roomWidth;
        int roomHeight;

        do {
            Pair location = findLocation();
            Pair dimensions = roomDimensions();
            x = location.getX();
            y = location.getY();
            roomWidth = dimensions.getX();
            roomHeight = dimensions.getY();
        } while (!goodLocation(x, y, roomWidth, roomHeight));

        drawRoomWalls(x, y, roomWidth, roomHeight);
        drawRoomFloor(x, y, roomWidth, roomHeight);
    }


    /*
    Draws hallways between rooms randomly until all of the rooms
    that are connected in at least 2 places using a union find algorithm.
    */
    private void drawHallways() {

        //Adjust the number of loops to control the number of hallways per room.
        for (int i = 0; i < 2; i++) {
            UnionFind uf = new UnionFind(roomId);
            findPaths();

            while (hallways.size() > 0) {
                int index = rand.nextInt(hallways.size());
                Path path = hallways.get(index);
                int roomA = path.getRoomA();
                int roomB = path.getRoomB();

                //If the rooms aren't already connected in someway, draw the hallway.
                if (!uf.connected(roomA, roomB)) {
                    uf.union(roomA, roomB);
                    drawSingleHallway(path);
                }

                hallways.remove(index);
            }
        }
    }


    //Returns a list of all possible connections (hallways) between rooms.
    private void findPaths() {
        findHorizontalPaths();
        findVerticalPaths();
    }


    //Draws full block for walls.
    private void drawRoomWalls(int x, int y, int roomWidth, int roomHeight) {
        int bottomOfRoom = y - roomHeight;
        int vert = y;

        while (vert > bottomOfRoom) {
            drawRow(x, vert, roomWidth, Tileset.WALL);
            vert--;
        }
    }


    //Draws smaller block for floor.
    private void drawRoomFloor(int x, int y, int roomWidth, int roomHeight) {
        inside = true;
        int hz = x + 1;
        roomWidth = roomWidth - 2;
        int vert = y - 1;
        roomHeight = roomHeight - 1;
        int bottomOfRoom = y - roomHeight;

        while (vert > bottomOfRoom) {
            drawRow(hz, vert, roomWidth, Tileset.FLOOR);
            vert--;
        }

        inside = false;
    }


    /*
    Draws one row of room or hallway object.
    Does NOT draw over floor tiles.
    */
    private void drawRow(int x, int y, int rowWidth, TETile tile) {
        int rightCorner = x + rowWidth;

        while (x < rightCorner) {

            if (!world[x][y].equals(Tileset.FLOOR)) {  //Don't draw over floor tiles.
                world[x][y] = tile;

                if (!drawingHallway) {
                    rooms[x][y] = roomId;  //Adds room id number to each tile inside the room.
                }

                if (!inside) {
                    floorTilesUsed++;
                }

            }
            x++;
        }
    }


    //Draws a horizontal or vertical hallway.
    private void drawSingleHallway(Path hallway) {
        drawingHallway = true;
        String direction = hallway.getOrientation();
        int x;
        int y;
        int roomWidth;
        int roomHeight;

        //Set up for horizontal hallway.
        if (direction.equals("hz")) {
            x = hallway.getSmallest().getX();
            y = hallway.getSmallest().getY() + 1;  //+1 for wall thickness.
            roomWidth = hallway.getBiggest().getX() - x + 1;
            roomHeight = 3;

        } else {  //Set up for vertical hallway.
            x = hallway.getBiggest().getX() - 1;  //-1 for wall thickness.
            y = hallway.getBiggest().getY();
            roomWidth = 3;
            roomHeight = y - hallway.getSmallest().getY() + 1;
        }

        //Draw block for walls.
        int bottomOfRoom = y - roomHeight;
        int hz = x;
        int vert = y;

        while (vert > bottomOfRoom) {
            drawRow(hz, vert, roomWidth, Tileset.WALL);
            vert--;
        }

        //Draw smaller horizontal block for the hallway floor.
        inside = true;

        if (direction.equals("hz")) {
            vert = y - 1;
            drawRow(hz, vert, roomWidth, Tileset.FLOOR);

        } else {  //Hallway object is vertical.
            hz = x + 1;
            roomWidth = 1;
            vert = y;
            bottomOfRoom = y - roomHeight;

            while (vert > bottomOfRoom) {
                drawRow(hz, vert, roomWidth, Tileset.FLOOR);
                vert--;
            }
        }

        inside = false;
        drawingHallway = false;
        roomId++;
    }


    //Chooses random  locations for the rooms.
    private Pair findLocation() {
        int x = rand.nextInt((widthOfDungeon - maxWall) - 2) + 2;
        int y = rand.nextInt((heightOfDungeon - maxWall) - 5) + maxWall + 1;
        return new Pair(x, y);
    }


    /*
    Calculates random room dimensions;
    Minimum wall size is currently 4, i.e. (+ 4).
    maxWall size is set in Game.java.
    */
    private Pair roomDimensions() {
        int roomWidth = rand.nextInt(maxWall - 3) + 4;
        int roomHeight = rand.nextInt(maxWall - 3) + 4;
        return new Pair(roomWidth, roomHeight);
    }


    /*
    Returns false if there is another room in the same
    area as the proposed room.  Hallways can overlap and cross
    but rooms do not overlap.
    */
    private boolean goodLocation(int x, int y,
                                 int roomWidth, int roomHeight) {
        int hz = x;
        int vert;
        int stopX = x + roomWidth;
        int stopY = y - roomHeight;

        while (hz < stopX) {
            vert = y;

            while (vert > stopY) {

                if (rooms[hz][vert] != 0) {
                    return false;
                }
                vert--;
            }
            hz++;
        }

        return true;
    }


    //Finds and collects all possible horizontal paths between rooms.
    private void findHorizontalPaths() {
        boolean inRoom1 = false;
        boolean inRoom2 = false;
        boolean onPath = false;
        boolean lock2 = false;
        boolean lock3 = false;
        boolean lock4 = false;
        Path pathObj = new Path();

        //Find all horizontal paths.
        for (int y = heightOfDungeon - 1; y >= 0; y--) {

            for (int x = 0; x < widthOfDungeon; x++) {

                //In room1
                if (world[x][y].equals(Tileset.FLOOR)) {
                    inRoom1 = true;
                }

                //Leaving room1, start my path here.
                if (world[x][y].equals(Tileset.WALL) && inRoom1
                        && !lock2) {
                    pathObj.setRoomA(rooms[x][y]);
                    onPath = true;
                    lock2 = true;
                }

                //In here whenever I'm on a path.
                if (onPath && !lock3) {
                    Pair xy = new Pair(x, y);
                    pathObj.addPair(xy);  //Store locations.
                }

                //Made it to another room if I hit a wall and the next tile is floor. Save the path.
                if (world[x][y].equals(Tileset.WALL) && inRoom1 && onPath
                        && world[x + 1][y].equals(Tileset.FLOOR) && !lock4) {
                    pathObj.setRoomB(rooms[x][y]);  //This is where the hall will stop.
                    pathObj.setOrientation("hz");  //Set the hz.
                    hallways.add(pathObj);  //Store the path in a static class list.
                    lock3 = true;  //Close off the path.
                    lock4 = true;
                    inRoom2 = true;
                }

                //Left the second room, reset for next path.
                if (world[x][y].equals(Tileset.WALL) && inRoom1 && inRoom2) {
                    pathObj = new Path();
                    inRoom1 = false;
                    onPath = false;
                    lock2 = false;
                    lock3 = false;
                    lock4 = false;
                }
            }

            //end of a row, reset and start over.
            pathObj = new Path();
            inRoom1 = false;
            inRoom2 = false;
            onPath = false;
            lock2 = false;
            lock3 = false;
            lock4 = false;
        }
    }


    //Finds and collects the possible vertical paths between rooms.
    private void findVerticalPaths() {
        boolean inRoom1 = false;
        boolean inRoom2 = false;
        boolean onPath = false;
        boolean lock1 = false;
        boolean lock2 = false;
        boolean lock3 = false;
        boolean lock4 = false;
        Path pathObj = new Path();

        //Find all vertical paths.
        for (int x = 0; x < widthOfDungeon; x++) {

            for (int y = heightOfDungeon - 1; y > 0; y--) {

                //In room1
                if (world[x][y].equals(Tileset.FLOOR) && !lock1) {
                    lock1 = true;
                    inRoom1 = true;
                }

                //Leaving room1, start my path here.
                if (world[x][y].equals(Tileset.WALL) && inRoom1
                        && !lock2) {
                    pathObj.setRoomA(rooms[x][y]);
                    onPath = true;
                    lock2 = true;
                }

                //In here whenever I'm on a path.
                if (onPath && !lock3) {
                    Pair xy = new Pair(x, y);
                    pathObj.addPair(xy);  //Store locations.
                }

                //Made it to another room if I hit a wall, and the tile after the wall is floor.
                if (world[x][y].equals(Tileset.WALL) && inRoom1 && onPath
                        && world[x][y - 1].equals(Tileset.FLOOR) && !lock4) {
                    pathObj.setRoomB(rooms[x][y]);  //This is where the hall will stop.
                    pathObj.setOrientation("vert");  //Set the hz.
                    hallways.add(pathObj);  //Store the path in a static class list.
                    lock3 = true;  //Close off the path.
                    lock4 = true;
                    inRoom2 = true;
                }

                //Left the second room, reset for next path.
                if (world[x][y].equals(Tileset.WALL) && inRoom1 && inRoom2) {
                    pathObj = new Path();
                    inRoom1 = false;
                    onPath = false;
                    lock1 = false;
                    lock2 = false;
                    lock3 = false;
                    lock4 = false;
                }
            }

            //end of a row, reset and start over.
            pathObj = new Path();
            inRoom1 = false;
            inRoom2 = false;
            onPath = false;
            lock1 = false;
            lock2 = false;
            lock3 = false;
            lock4 = false;
        }
    }

}
