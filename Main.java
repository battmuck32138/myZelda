
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


/*
Main entry point for the game.
Either starts a new game or loads a saved game.
 */
public class Main {

    public static void main(String[] args) {
        Game game = new Game();
        game.playGame();

        if (game.getLoadedGame()) {
            game = loadGame();
            game.startLoadedGame();
        }

        saveGame(game);
        System.exit(0);
    }


    //Loads the saved game if there is one.  If not, the program ends.
    private static Game loadGame() {
        File f = new File("./game.txt");

        if (f.exists()) {

            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                Game loadGame = (Game) os.readObject();
                os.close();
                return loadGame;

            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);

            } catch (IOException e) {
                System.out.println("IO exception during load.");
                System.exit(0);

            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }

        //In the case that no Game has been saved yet, end program.
        System.exit(0);
        return new Game();  //Dummy return is unreachable.
    }


    private static void saveGame(Game game) {
        File f = new File("./game.txt");

        try {

            if (!f.exists()) {
                f.createNewFile();
            }

            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(game);
            os.close();

        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);

        } catch (IOException e) {

            System.out.println("IO exception during save.");
            System.exit(0);
        }
    }


}


