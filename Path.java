

import java.io.Serializable;
import java.util.ArrayList;

/*
A Path is a collection of tiles that exist on a given path
that leads from 1 room of a dungeon to another room in
a dungeon.
 */
public class Path implements Serializable {
    //Unique serialization number for this class.
    private static final long serialVersionUID = 1383L;
    private String orientation;
    private int roomA;
    private int roomB;
    private ArrayList<Pair> pathList = new ArrayList<>();


    //Finds the biggest x and Y and returns them as a pair.
    Pair getBiggest() {
        int biggestX = 0;
        int biggestY = 0;
        Pair xy;

        for (Pair aPathList : pathList) {
            xy = aPathList;
            biggestX = xy.getX() > biggestX ? xy.getX() : biggestX;
            biggestY = xy.getY() > biggestY ? xy.getY() : biggestY;
        }

        return new Pair(biggestX, biggestY);
    }


    //Finds the smallest x and Y and returns them as a pair.
    protected Pair getSmallest() {
        int smallestX = pathList.get(0).getX();
        int smallestY = pathList.get(0).getY();
        Pair xy;

        for (Pair aPathList : pathList) {
            xy = aPathList;

            smallestX = xy.getX() < smallestX ? xy.getX() : smallestX;
            smallestY = xy.getY() < smallestY ? xy.getY() : smallestY;
        }

        return new Pair(smallestX, smallestY);
    }


    public String getOrientation() {
        return orientation;
    }


    public int getRoomA() {
        return roomA;
    }


    public int getRoomB() {
        return roomB;
    }


    public void addPair(Pair pair) {
        pathList.add(pair);
    }


    public void setRoomA(int roomNumber) {
        roomA = roomNumber;
    }


    public void setRoomB(int roomNumber) {
        roomB = roomNumber;
    }


    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }


    @Override
    public String toString() {
        String s = "roomA: " + roomA + "  roomB: " + roomB
                + "  orientation: " + orientation + "  Path: ";

        for (Pair xy : pathList) {
            s += xy.toString() + " ";
        }

        return s;
    }


}

