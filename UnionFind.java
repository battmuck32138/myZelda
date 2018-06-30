

import java.io.Serializable;
import java.util.Arrays;

//Standard union find from Sedgewick's Algorithm text book.
public class UnionFind implements Serializable {
    private static final long serialVersionUID = 1385L;  //Serialization number for this class.
    private int[] id; //access to component id (site index)
    private int numberOfComponents; //number of components


    UnionFind(int numComps) {
        numberOfComponents = numComps;
        id = new int[numComps];

        //initialize component id array to 0, 1, 2,...
        for (int i = 0; i < numComps; i++) {
            id[i] = i;
        }
    }


    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }


    public int getNumberOfComponents() {
        return numberOfComponents;
    }


    //finds whats in the element
    public int find(int p) {
        return id[p];
    }


    /*
    Changes the contents of p to the contents of q,
    and also changes every element with the same contents as p
    to match the contents of q
    Returns false if a new union didn't occur.
    */
    public boolean union(int p, int q) {
        int pID = find(p);
        int qID = find(q);

        //already p u Q
        if (pID == qID) {
            return false;
        }

        for (int i = 0; i < id.length; i++) {

            if (id[i] == pID) {
                id[i] = qID;
                numberOfComponents--;
            }
        }

        return true;
    }


    public String toString() {
        return "id[]: " + Arrays.toString(id) + "\n";
    }


}
