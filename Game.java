
import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import edu.princeton.cs.introcs.StdDraw;


/*
My homage to the old NES Legend of Zelda Games.
Tile based game that creates a unique new world based on the seed that is
entered by the user i.e. same seed means the user will get the same world.
The worlds contain a dungeon structure that change drastically
each time the game is played and a temple that changes it's size and location.
Games can be saved and re-loaded again from the start menu by Serializing the game object.
Comment out all of the wavPlayer lines if you want to save a game.  JFrame is not Serializable.
 */
public class Game implements Serializable {

    //World set up
    private static final long serialVersionUID = 1381L;  //Serialization number for Game class.
    private WavPlayer wavPlayer = new WavPlayer();  //Plays audio during game.
    private final int windowWidth = 96;  //96 is max for my laptop.
    private final int windowHeight = 46;  //46 is max for my laptop.
    TETile[][] world;
    private final int worldWidth = windowWidth;
    private final int worldHeight = windowHeight - 3;  //World height is smaller due to HUD.
    private TERenderer ter = new TERenderer();
    private Random rand;  //rand is initialized with a seed, same seed will produce the same world.
    private int dungeonWidth;

    //Moving Pieces set up
    ArrayList<TETile> walkableTiles = new ArrayList<>();
    private ArrayList<Enemy> firstWaveEnemies = new ArrayList<>();
    private ArrayList<Enemy> secondWaveEnemies = new ArrayList<>();
    private ArrayList<Pair> secondWaveLocations = new ArrayList<>();
    private int mouseX = 0;
    private int mouseY = 0;
    private boolean gameLoopOn = true;
    private boolean loadedGame = false;
    int enemySpeed = 4;
    private int slow = 0;  //# of loops where firstWaveEnemies move in slow motion.
    private Pair beanStalkLocation;
    private Pair linkLocation;
    private TETile linkPrevTile = Tileset.FLOOR;
    private boolean hasTriforce = false;
    private boolean hasRope = false;
    private boolean hasSwimPotion = false;
    private boolean pickedFlower = false;

    /*
    Menu screens set up.
    The default Font that the renderFrame() uses is Monaco size 16.  Don't change the Font
    for drawing tiles because Monaco contains many unique chars that are used to build tiles
    that are not available in most Fonts.
    */
    private Font defaultFontFromRenderer = new Font("Monaco", Font.BOLD, 16);
    private Font giantFont = new Font("Copperplate Gothic Bold", Font.BOLD,
            (windowHeight / 10) * 15);
    private Font bigFont = new Font("Algerian", Font.BOLD, (windowHeight / 10) * 15);
    private Font mediumFont = new Font("Copperplate Gothic Bold", Font.BOLD,
            (windowHeight / 20) * 16);
    private Font smallFont = new Font("CopperPlate Gothic Bold", Font.BOLD,
            (windowHeight / 5) * 3);
    private int titleFirstLineY = windowHeight - (windowHeight / 15) * 2;
    private int titleSecondLineY = titleFirstLineY - (windowHeight / 15) * 2;
    private int menuLine1Y = (windowHeight / 20) * 15;
    private int menuLine2Y = menuLine1Y - (windowHeight / 10);
    private int menuLine3Y = menuLine2Y - (windowHeight / 10);
    private int menuLine4Y = menuLine3Y - (windowHeight / 10);
    private int menuLine5Y = menuLine4Y - (windowHeight / 10);
    private int menuLine6Y = menuLine5Y - (windowHeight / 10);
    private int menuLine7Y = menuLine6Y - (windowHeight / 10);


    //Method used for playing a new game. The game starts from the main menu.
    public void playGame() {
        //Renderer for the menu screen is full sized w*h.
        ter.initialize(windowWidth, windowHeight);
        //Play menu theme music;
        wavPlayer.playSoundFile("songOfTime.wav");
        displayStartScreen(' ');
        solicitCommandFromUser();
    }


    /* The game loop runs everything that moves during and
    listens for user commands during play mode.
    */
    private void startGameLoop() {
        char commandChar = ' ';  //Not a legal command.

        while (gameLoopOn) {  //Start workflow, listen for a legal command.

            commandChar = StdDraw.hasNextKeyTyped() ? StdDraw.nextKeyTyped() : commandChar;

            if (isLegalGameLoopCommand(commandChar)) {
                commandChar = Character.toUpperCase(commandChar);
                commandFlowForGameLoop(commandChar);
            }

            for (Enemy e : firstWaveEnemies) {
                e.moveEnemy();
            }

            if (pickedFlower) {  //Second-wave of enemies comes out of the cave.
                for (Enemy e : secondWaveEnemies) {
                    e.moveEnemy();
                }
            }

            updateMouseLocation();  //Updates the HUD of the mouse moves.
            drawEverything();
            commandChar = ' ';  //update to a non-legal command until the next key stroke.

            if (slow > 0) {
                StdDraw.pause(100);
                afterDrinkingSong();
                slow--;
            }
        }
    }


    //Handles the work flow for main menu.
    private void commandFlowMenuScreens(char commandChar) {
        long seed;
        commandChar = Character.toUpperCase(commandChar);

        switch (commandChar) {

            case 'N':  //New Game, display empty input String.
                displaySeedScreen("");
                seed = extractSeedFromUser();
                rand = new Random(seed);
                //Must Initialize again for the world.
                ter.initialize(windowWidth, windowHeight);
                world = initializeWorld();
                buildUpWorld();
                drawEverything();  //First time the world is drawn.
                //Link starts in the dungeon so play dungeon theme music.
                wavPlayer.playSoundFile("nocturneOfShadow.wav");
                startGameLoop();  //Every thing runs from here.
                break;

            case 'L':  //Load a saved game file.
                loadedGame = true;
                gameLoopOn = false;
                break;

            case 'Q':  //Quit and save game.
                //Nothing to save when you quit from the menu screen.
                System.exit(0);
                break;

            default:
        }
    }


    //Does not stop soliciting until the user enters a legal menu command.
    private void solicitCommandFromUser() {
        char inputChar = listenForCharFromUser();

        inputChar = inputChar != ':' ? Character.toUpperCase(inputChar) : inputChar;

        if (inputChar == 'N') {  //New Game
            displayStartScreen(inputChar);
            StdDraw.pause(500);  //Let user see his entry.
            commandFlowMenuScreens(inputChar);

        } else if (inputChar == 'L') {  //Load Game
            displayStartScreen(inputChar);
            StdDraw.pause(500);  //Let user see his entry.
            loadedGame = true;
            commandFlowMenuScreens(inputChar);

        } else if (inputChar == ':') {  // :Q to quit game.
            displayStartScreen(inputChar);
            StdDraw.pause(500);
            char secondChar = listenForCharFromUser();
            secondChar = Character.toUpperCase(secondChar);

            if (secondChar == 'Q') {
                displayStartScreen(secondChar);
                StdDraw.pause(500);
                commandFlowMenuScreens(secondChar);

            } else {
                displayStartScreen(' ');
                StdDraw.pause(500);
                solicitCommandFromUser();
            }

        } else {
            displayStartScreen(inputChar);
            StdDraw.pause(500);
            displayStartScreen(' ');
            StdDraw.pause(500);
            solicitCommandFromUser();
        }
    }


    /*
    Builds the seed string from user input and stores it in the object variable.
    Doesn't allow the user to leave until a legal command is entered.
    */
    private long extractSeedFromUser() {
        char inputChar;
        String seedString = "";
        boolean needFirstDigit = true;
        long seedLong = 1L;
        boolean haveNoS = true;

        while (needFirstDigit) {
            displaySeedScreen(seedString);
            inputChar = listenForCharFromUser();
            displaySeedScreen(seedString + Character.toString(inputChar));

            if (Character.isDigit(inputChar)) {  //Must be a digit.
                seedString += Character.toString(inputChar);
                displaySeedScreen(seedString);
                needFirstDigit = false;

            } else {
                displaySeedScreen("Enter an integer.");
                extractSeedFromUser();
            }
        }

        int numDigits = 0;

        while (haveNoS && numDigits < 9) {
            inputChar = listenForCharFromUser();

            if (Character.isDigit(inputChar)) {
                seedString += Character.toString(inputChar);
                displaySeedScreen(seedString);
                seedLong = Long.parseLong(seedString);
            }

            if (inputChar == 's' || inputChar == 'S') {
                haveNoS = false;
                displaySeedScreen("S");
                StdDraw.pause(500);
            }

            numDigits++;
        }

        displaySeedScreen("Try holding the cursor over objects for clues.");
        StdDraw.pause(5000);
        return seedLong;
    }


    //Listens for a char then returns it.
    private char listenForCharFromUser() {
        boolean playersTurn = true;
        char inputChar = ' ';  //Dummy value is never used.

        while (playersTurn) {
            StdDraw.pause(100);

            if (StdDraw.hasNextKeyTyped()) {
                inputChar = StdDraw.nextKeyTyped();
                playersTurn = false;
            }
        }

        return inputChar;
    }


    //Takes care of all the things that need to happen if a saved game is loaded.
    public void startLoadedGame() {
        ter.initialize(windowWidth, windowHeight);
        drawEverything();
        loadedGame = false;
        gameLoopOn = true;
        startGameLoop();
    }


    public boolean getLoadedGame() {
        return loadedGame;
    }


    //Returns true for a legal command, false otherwise.
    private boolean isLegalGameLoopCommand(char commandChar) {
        ArrayList<Character> legalInputChars = new ArrayList<>(Arrays.asList(
                'w', 'W', 's', 'S', 'a', 'A', 'd', 'D', 'Q', 'q', ':'));
        return legalInputChars.contains(commandChar);
    }


    /*
    Returns false if Link will hit an object that can't be passed on
    his next step based on his current direction, true otherwise.
    */
    private boolean linkCanWalkHere(char direction) {
        int x = linkLocation.getX();
        int y = linkLocation.getY();

        switch (direction) {

            case 'n':  //north

                if (walkableTiles.contains(world[x][y + 1])) {
                    return true;
                }
                break;

            case 's':  //south

                if (walkableTiles.contains(world[x][y - 1])) {
                    return true;
                }
                break;

            case 'e':  //east

                if (walkableTiles.contains(world[x + 1][y])) {
                    return true;
                }
                break;

            case 'w':

                if (walkableTiles.contains(world[x - 1][y])) {
                    return true;
                }
                break;

            default:
        }

        return false;
    }


    //Handles all scenarios that involve Link touching a special item.
    private TETile checkForSpecialItems(TETile nextStep) {

        if (nextStep.equals(Tileset.FLOWER)) {  //Enemies speed up if Link hits a flower.
            enemySpeed = 1;
            pickedFlower = true;
            wavPlayer.playSoundFile("crankenstein.wav");
            addSecondWaveEnemies();

        } else if (nextStep.equals(Tileset.ROPE)) {
            walkableTiles.add(Tileset.MOUNTAIN);
            nextStep = Tileset.GRASS;
            hasRope = true;

            //Link has everything he needs to finish the quest, play Link's theme.
        } else if (nextStep.equals(Tileset.SWIMPOTION)) {
            walkableTiles.add(Tileset.WATER);
            nextStep = Tileset.GRASS;
            hasSwimPotion = true;
            wavPlayer.playSoundFile("zeldaTheme.wav");

        } else if (nextStep.equals(Tileset.WINE)) {
            wavPlayer.playSoundFile("trimStopDrinking.wav");
            slow = 120;
            nextStep = Tileset.GRASS;

        } else if (nextStep.equals(Tileset.LOCKED_DOOR) && hasTriforce) {
            displayVictoryScreen();  //Player wins.

        } else if (nextStep.equals(Tileset.TRIFORCE)) {
            hasTriforce = true;
            nextStep = Tileset.FLOOR;
            displayTriforceScreen();

        } else if (nextStep.equals(Tileset.BEANSTALK) && hasTriforce) {
            int tmpX = linkLocation.getX();
            int tmpY = linkLocation.getY();
            nextStep = Tileset.BEANSTALK;
            int x = beanStalkLocation.getX();
            int y = beanStalkLocation.getY();
            linkLocation = new Pair(x, y);
            world[x][y] = Tileset.LINK;
            world[tmpX][tmpY] = Tileset.BEANSTALK;
            //Link leaves the dungeon so play Zelda theme music.
            wavPlayer.playSoundFile("lostWoods.wav");
        }

        return nextStep;
    }


    private void afterDrinkingSong() {

        if (hasSwimPotion && slow == 1) {
            wavPlayer.playSoundFile("zeldaTheme.wav");

        } else if (!hasSwimPotion && slow == 1) {
            wavPlayer.playSoundFile("lostWoods.wav");
        }
    }


    //Adds the second wave of enemies when Link is getting close to the end of the quest.
    private void addSecondWaveEnemies() {
        TETile[] creatures = new TETile[] {Tileset.SKULL, Tileset.GHOSTKING, Tileset.REDGHOST,
                Tileset.SUNGHOST, Tileset.SPIDER};

        for (int i = 0; i < creatures.length; i++) {
            Enemy e = new Enemy(this, creatures[i], Tileset.CAVE, secondWaveLocations.get(i), rand);
            secondWaveEnemies.add(e);
        }
    }


    //Workflow during game play.
    private void commandFlowForGameLoop(char commandChar) {
        commandChar = Character.toUpperCase(commandChar);
        int x = linkLocation.getX();
        int y = linkLocation.getY();
        TETile nextStep;

        //Commands that can be given during the game while the game loop is running.
        switch (commandChar) {

            case 'Q':  //Stop the game loop and go back to Main class.

                //Quit is disabled for now, can't save, audio JFrame not Serializable.
                //gameLoopOn = false;
                //return;
                break;

            //Next 4 cases move Link;
            case 'W':  //north

                if (linkCanWalkHere('n')) {
                    nextStep = world[x][y + 1];
                    world[x][y + 1] = Tileset.LINK;
                    linkLocation = new Pair(x, y + 1);
                    world[x][y] = linkPrevTile;
                    linkPrevTile = checkForSpecialItems(nextStep);
                }
                break;

            case 'S':  //south

                if (linkCanWalkHere('s')) {
                    nextStep = world[x][y - 1];
                    world[x][y - 1] = Tileset.LINK;
                    linkLocation = new Pair(x, y - 1);
                    world[x][y] = linkPrevTile;
                    linkPrevTile = checkForSpecialItems(nextStep);
                }
                break;

            case 'A':  //west

                if (linkCanWalkHere('w')) {
                    nextStep = world[x - 1][y];
                    world[x - 1][y] = Tileset.LINK;
                    linkLocation = new Pair(x - 1, y);
                    world[x][y] = linkPrevTile;
                    linkPrevTile = checkForSpecialItems(nextStep);

                }
                break;

            case 'D':  //east

                if (linkCanWalkHere('e')) {
                    nextStep = world[x + 1][y];
                    world[x + 1][y] = Tileset.LINK;
                    linkLocation = new Pair(x + 1, y);
                    world[x][y] = linkPrevTile;
                    linkPrevTile = checkForSpecialItems(nextStep);
                }
                break;

            default:
        }

        drawEverything();
    }


    //Always draw the world, then the HUD over it.
    private void drawEverything() {
        StdDraw.enableDoubleBuffering();
        ter.renderFrame(world);
        displayHud();
    }


    /*
    The displayHud() will display the description
    of the tile located at mouseX mouseY.
    */
    private void updateMouseLocation() {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();

        if (x < worldWidth && x > -1 && y < worldHeight && y > -1) {
            mouseX = (int) x;
            mouseY = (int) y;
        }
    }


    //Builds a plain world with mountains in the west and forrest in the east.
    private TETile[][] initializeWorld() {
        world = new TETile[worldWidth][worldHeight];  //Set up 2-d tile array.

        //Start with all walkable GRASS TILES.
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                world[x][y] = Tileset.GRASS;
            }
        }

        addMountains();
        addTrees();
        addFlowersWine();

        /*
        Draw a row of Nothing Tiles around the perimeter of the world
        to serve as a barrier (can't be walked on).
        */
        for (int i = 0; i < windowWidth; i++) {
            world[i][worldHeight - 1] = Tileset.NOTHING;
        }

        for (int i = 0; i < windowWidth; i++) {
            world[i][0] = Tileset.NOTHING;
        }

        for (int i = worldHeight - 1; i > 0; i--) {
            world[worldWidth - 1][i] = Tileset.NOTHING;
        }

        for (int i = worldHeight - 1; i > 0; i--) {
            world[0][i] = Tileset.NOTHING;
        }

        return world;
    }


    //Adds all of the features to the world, inside and outside.
    private void buildUpWorld() {
        int coin = rand.nextInt(100);
        dungeonWidth = coin < 65 ? (worldWidth / 2) : (worldWidth / 3);
        //Define what tiles can be walked on.
        walkableTiles.add(Tileset.FLOOR);
        walkableTiles.add(Tileset.GRASS);
        walkableTiles.add(Tileset.LOCKED_DOOR);
        walkableTiles.add(Tileset.TREE);
        walkableTiles.add(Tileset.TRIFORCE);
        walkableTiles.add(Tileset.BEANSTALK);
        walkableTiles.add(Tileset.FLOWER);
        walkableTiles.add(Tileset.TEMPLE);
        walkableTiles.add(Tileset.CAVE);
        walkableTiles.add(Tileset.SWIMPOTION);
        walkableTiles.add(Tileset.ROPE);
        walkableTiles.add(Tileset.WINE);

        //Build the dungeon.
        int maxWall = rand.nextInt(10) + 5;
        Structure dungeon = new Structure(world, rand, maxWall, dungeonWidth, worldHeight);
        dungeon.buildStructure();

        //Build the temple.
        Hexagon hex = new Hexagon(world, rand, worldWidth, worldHeight);
        hex.addTemple();

        addEnemiesAndSpecialItems();
    }


    /*
    Adds mountains at random locations on the west 1/2 of the world.
    The density of the mountains gets lighter as you approach
    the center of the world.
    */
    private void addMountains() {
        int coinToss;
        int x = 0;
        int mountainFootPrint = (worldWidth / 4);
        int percent = 37;

        for (int i = 0; i < 7; i++) {

            //Blend grass and mountains.
            while (x < mountainFootPrint) {
                for (int y = worldHeight - 2; y > 0; y--) {
                    coinToss = rand.nextInt(100);
                    world[x][y] = coinToss < percent ? Tileset.MOUNTAIN : world[x][y];
                }
                x++;
            }

            mountainFootPrint += 5;
            percent -= 6;
        }
    }


    /*
    Sprinkle in the trees on the east 1/3 of the world.
    Can adjust density with (coinToss < ____).
    */
    private void addTrees() {
        int coinToss;
        int x = worldWidth - 1;
        int footPrint = x - 5;
        int percent = 47;

        for (int i = 0; i < 8; i++) {

            //Blend grass and mountains.
            while (x > footPrint) {
                for (int y = worldHeight - 2; y > 0; y--) {
                    coinToss = rand.nextInt(100);
                    world[x][y] = coinToss < percent ? Tileset.TREE : world[x][y];
                }
                x--;
            }

            footPrint -= 5;
            percent -= 6;
        }
    }


    //Sprinkles in a few flowers and wine bottles here and there.
    private void addFlowersWine() {

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight - 1; y++) {
                int coin = rand.nextInt(400);
                world[x][y] = coin < 6 ? Tileset.FLOWER : world[x][y];
            }
        }

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight - 1; y++) {
                int coin = rand.nextInt(800);
                world[x][y] = coin < 6 ? Tileset.WINE : world[x][y];
            }
        }
    }


    /*
    Sets up all of the special items and firstWaveEnemies for the world and
    clears a walkable path through the mountains leading from the beanstalk.
    */
    private void addEnemiesAndSpecialItems() {
        setUpDungeon();
        setUpOutside();

        int x = (dungeonWidth);
        int y = rand.nextInt(worldHeight - 11) + 5;
        world[x][y] = Tileset.BEANSTALK;
        beanStalkLocation = new Pair(x, y);
        drawCave(x, y);

        for (int i = x + 1; i < x + 15; i++) {  //Leave a walkable path from beanstalk.
            for (int j = y + 1; j >= y; j--) {
                world[i][j] = Tileset.GRASS;
            }
        }
    }


    private void drawCave(int x, int y) {
        world[x - 1][y] = Tileset.MOUNTAIN;
        world[x - 2][y] = Tileset.MOUNTAIN;
        world[x - 3][y] = Tileset.MOUNTAIN;
        world[x][y + 1] = Tileset.MOUNTAIN;
        world[x - 1][y + 1] = Tileset.MOUNTAIN;
        world[x - 2][y + 1] = Tileset.MOUNTAIN;
        world[x - 3][y + 1] = Tileset.MOUNTAIN;
        secondWaveLocations.add(new Pair(x + 1, y - 1));
        secondWaveLocations.add(new Pair(x + 2, y - 1));
        secondWaveLocations.add(new Pair(x + 2, y - 2));
        secondWaveLocations.add(new Pair(x + 2, y - 2));
        secondWaveLocations.add(new Pair(x + 1, y - 2));
        secondWaveLocations.add(new Pair(x, y - 1));

        for (int hz = x - 3; hz < x + 3; hz++) {
            for (int vert = y - 1; vert > y - 5; vert--) {
                world[hz][vert] = Tileset.MOUNTAIN;
            }
        }

        for (int hz = x - 1; hz < x + 3; hz++) {
            for (int vert = y - 1; vert >= y - 2; vert--) {
                world[hz][vert] = Tileset.CAVE;
            }
        }
    }


    /*
    Adds the firstWaveEnemies and special items to the dungeon.
    */
    private void setUpDungeon() {
        int x = 0;  //Dummy values are never used.
        int y = 0;  //Dummy values are never used.
        TETile[] dungeonTiles = new TETile[5];
        dungeonTiles[0] = Tileset.LINK;
        dungeonTiles[1] = Tileset.TRIFORCE;
        dungeonTiles[2] = Tileset.BEANSTALK;
        dungeonTiles[3] = Tileset.SKULL;
        dungeonTiles[4] = Tileset.SPIDER;
        boolean inDungeon = false;

        for (TETile tile : dungeonTiles) {

            while (!inDungeon) {
                x = rand.nextInt(dungeonWidth);
                y = rand.nextInt(worldHeight - 3);

                if (world[x][y].equals(Tileset.FLOOR)) {
                    world[x][y] = tile;
                    inDungeon = true;
                }
            }
            inDungeon = false;

            if (world[x][y].equals(Tileset.LINK)) {  //Start Link in the dungeon.
                linkLocation = new Pair(x, y);

            } else if (world[x][y].equals(Tileset.SKULL)) {  //Start SKULL in the dungeon.
                Pair tmpLoc = new Pair(x, y);
                Enemy skull = new Enemy(this, Tileset.SKULL, Tileset.FLOOR, tmpLoc, rand);
                firstWaveEnemies.add(skull);

            } else if (world[x][y].equals(Tileset.SPIDER)) {  //Start GreenGhost in the dungeon.
                Pair tmpLoc = new Pair(x, y);
                Enemy greenGhost = new Enemy(this, Tileset.SPIDER, Tileset.FLOOR, tmpLoc, rand);
                firstWaveEnemies.add(greenGhost);
            }
        }
    }


    /*
    Adds the firstWaveEnemies and special items to the outdoors portion of the world.
    */
    private void setUpOutside() {
        int x;
        int y;
        int swimPotionY = rand.nextInt(worldHeight - 10) + 3;
        int ropeY = rand.nextInt(worldHeight - 10) + 3;

        TETile[] outsideTiles = new TETile[3];
        outsideTiles[0] = Tileset.SUNGHOST;
        outsideTiles[1] = Tileset.GHOSTKING;
        outsideTiles[2] = Tileset.REDGHOST;

        world[1][swimPotionY] = Tileset.SWIMPOTION;
        world[worldWidth - 2][ropeY] = Tileset.ROPE;

        //Add items to the outside world.
        for (int i = 0; i < outsideTiles.length; i++) {
            //Start ghosts near the center of the world.
            x = (worldWidth / 2) + i * 3;
            y = (worldHeight / 2) + i * 2;
            Pair tmpLoc = new Pair(x, y);

            if (world[x][y].equals(Tileset.GRASS) || world[x][y].equals(Tileset.MOUNTAIN)) {
                world[x][y] = outsideTiles[i];
            }

            if (world[x][y].equals(Tileset.SUNGHOST)) {
                Enemy sunGhost = new Enemy(this, Tileset.SUNGHOST, Tileset.GRASS, tmpLoc, rand);
                firstWaveEnemies.add(sunGhost);

            } else if (world[x][y].equals(Tileset.GHOSTKING)) {
                Enemy ghostKing = new Enemy(this, Tileset.GHOSTKING, Tileset.GRASS, tmpLoc, rand);
                firstWaveEnemies.add(ghostKing);

            } else  {
                Enemy redGhost = new Enemy(this, Tileset.REDGHOST, Tileset.GRASS, tmpLoc, rand);
                firstWaveEnemies.add(redGhost);
            }
        }
    }


    //Displays the game title and menu to the screen.
    private void displayStartScreen(char inputChar) {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);  //Always clear the screen before drawing.
        StdDraw.picture(windowWidth / 2, windowHeight / 2, "linkStare.jpeg");

        //Draw the Title.
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text((worldWidth / 20) * 16, titleFirstLineY, "The Legend of Zelda");
        StdDraw.text((worldWidth / 20) * 16, titleSecondLineY, "Quest for the Triforce");

        //Draw the menu options.
        StdDraw.setFont(mediumFont);
        StdDraw.setPenColor(Color.BLACK);  //green for go, red for something is wrong.
        StdDraw.text((worldWidth / 20) * 16, menuLine1Y, "New Game: (N)");
        StdDraw.text((worldWidth / 20) * 16, menuLine2Y, "Load Game: (L)");
        StdDraw.text((worldWidth / 20) * 16, menuLine3Y, "Quit: (:Q)");
        StdDraw.text((worldWidth / 20) * 16, menuLine4Y, Character.toString(inputChar));
        StdDraw.show();  //Display the menu frame.
    }


    //Give instructions on how to seed the Random object for the game.
    private void displaySeedScreen(String inputSt) {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);  //Always clear the screen before drawing.
        StdDraw.picture(windowWidth / 2, windowHeight / 2,
                "linkSeed.jpeg", windowWidth, windowHeight);
        int centerText = worldWidth / 3;

        //Draw the user instructions.
        StdDraw.setFont(mediumFont);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text(centerText, titleFirstLineY,
                "The Triforce has been stolen from the Temple,");
        StdDraw.text(centerText, titleSecondLineY,
                "without it the realm will surely fall into chaos!");
        StdDraw.setFont(smallFont);
        StdDraw.text(centerText, menuLine1Y,
                "Link has tracked the Triforce to the Citadel of Despair, set");
        StdDraw.text(centerText, menuLine2Y,
                "high atop the Misty Mountains. Using a magic seed, Link has");
        StdDraw.text(centerText, menuLine3Y,
                "grown a giant beanstalk and climbed up into the Citadel.");
        StdDraw.text(centerText, menuLine4Y,
                "How many cubits tall do you think the beanstalk is?");
        StdDraw.setFont(mediumFont);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(centerText, menuLine5Y,
                "Enter an integer followed by \"s\" for seed.");
        StdDraw.text(centerText, menuLine6Y, "Example: 138s");
        StdDraw.text(centerText, menuLine7Y, inputSt);
        StdDraw.show();  //Display the screen
    }


    /*
    Displays the HUD by drawing over the blank space at the
    top of the world.  renderFrame() clears everything so whenever I
    need to update the HUD, be sure to renderFrame(world) then draw
    the HUD on top of it.  This method doesn't StdDraw.clear().
    */
    private void displayHud() {
        StdDraw.enableDoubleBuffering();
        //Font must be the same as the default for the renderer class.
        //If not the world (rendered by renderFrame()) will look funny.
        StdDraw.setFont(defaultFontFromRenderer);
        StdDraw.setPenColor(Color.GREEN);
        StdDraw.text((windowWidth / 10) * 9, windowHeight - 2,
                ":Q Save   UP 'W'    DOWN 'S'    LEFT 'A'    RIGHT 'D'");
        StdDraw.setPenColor(Color.white);
        StdDraw.text(3, windowHeight - 2, "ITEMS ");
        StdDraw.text((windowWidth / 10) * 6, windowHeight - 2,
                world[mouseX][mouseY].description());  //Displays info about the tiles.

        if (hasTriforce) {
            StdDraw.setPenColor(Color.yellow);
            StdDraw.text((worldWidth / 10), windowHeight - 2,
                    "TRIFORCE ▲");
        }

        if (hasRope) {
            StdDraw.setPenColor(Color.orange);
            StdDraw.text((worldWidth / 10) * 2, windowHeight - 2,
                    "ROPE");
        }

        if (hasSwimPotion) {
            StdDraw.setPenColor(Color.blue);
            StdDraw.text((worldWidth / 10) * 3, windowHeight - 2,
                    "SWIM POTION Ϫ");
        }

        StdDraw.show();
    }


    /*
    Displays a screen that lets the player know that he has found the triforce
    and can now exit the dungeon.
     */
    private void displayTriforceScreen() {
        StdDraw.enableDoubleBuffering();
        wavPlayer.playSoundFile("triForceSong.wav");
        StdDraw.clear(Color.BLACK);  //Always clear the screen before drawing.
        StdDraw.picture(windowWidth / 2, windowHeight / 2, "triforce.jpg");

        StdDraw.setFont(giantFont);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(worldWidth / 2, menuLine7Y, "You found the TRIFORCE");
        //Display the menu frame.
        StdDraw.show();
        StdDraw.pause(7000);
        wavPlayer.playSoundFile("nocturneOfShadow.wav");
    }


    //Displays instructions if the user doesn't enter an appropriate command.
    void displayDeadLinkScreen() {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);  //Always clear the screen before drawing.
        StdDraw.picture(windowWidth / 2, windowHeight / 2, "zeldaGameOver.png");
        wavPlayer.playSoundFile("trimTypeO.wav");
        StdDraw.show();
        StdDraw.pause(30000);
        System.exit(0);
    }


    //Displays the final screen after the player has won!
    private void displayVictoryScreen() {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);  //Always clear the screen before drawing.
        StdDraw.picture(windowWidth / 2, windowHeight / 2, "linkAndZelda.jpeg");
        wavPlayer.playSoundFile("itFeelsGood.wav");
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(worldWidth / 2, titleFirstLineY, "You Returned the TRIFORCE");
        StdDraw.text(worldWidth / 2, titleSecondLineY, "to Princess Zelda.");
        StdDraw.text(worldWidth / 2, menuLine7Y, "Time to break out the elf wine!");
        //Display the menu frame.
        StdDraw.show();
        StdDraw.pause(600000);
        System.exit(0);
    }

}

