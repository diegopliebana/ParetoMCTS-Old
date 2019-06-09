package contPuddleworld.game;

import utils.File2String;
import utils.Vector2d;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * This class represents the map where the game is played. An interesting method of this class is LineOfSight, that can be used
 * to check the line of sight between two points in the map.
 * PTSP-Competition
 * Created by Diego Perez, University of Essex.
 * Date: 19/12/11
 */
public class CpwBoard extends JComponent
{

    /**
     * Symbol for an edge of the map.
     */
    public static final char EDGE = 'E';

    /**
     * Symbol for the starting position of the ship in the map.
     */
    public static final char START = 'S';

    /**
     * Symbol for a waypoint in the map.
     */
    public static final char WAYPOINT = 'C';

    /**
     * Symbol for an empty space in the map.
     */
    public static final char NIL = '.';

    /**
     * Symbol for an empty space in the map.
     */
    public static final char LAVA = ':';

    /**
     * Array with all the elements of the map.
     */
    private static char m_mapChar[][];

    /**
     * Height, in pixels, of the map.
     */
    private static int m_height;

    /**
     * Width, in pixels, of the map.
     */
    private static int m_width;

    /**
     * Starting point of the ship in the map.
     */
    private static Vector2d m_startingPoint;

    /**
     * List of the positions of the waypoints.
     */
    private static Vector2d m_goalPosition;

    /**
     * Reference to the game.
     */
    private GameCPW m_gameState;

    /**
     * Filename where this map is read from.
     */
    private String m_filename;

    /**
     * Lava locations must be randomized
     */
    private boolean m_randomLava;

    /**
     * Random generator for lava.
     */
    private Random m_random;

    /**
     * Private constructor, only used by getCopy()
     */
    private CpwBoard()
    {
        m_goalPosition = new Vector2d();
    }

    /**
     * Map constructor
     * @param a_game reference to the game.
     * @param a_filename filename to read the map from.
     */
    public CpwBoard(GameCPW a_game, String a_filename, boolean randomizeLava, int seed)
    {
        this.m_gameState = a_game;
        m_filename = a_filename;
        m_startingPoint = new Vector2d();
        m_goalPosition = new Vector2d();
        m_randomLava = randomizeLava;
        readMap();

        if(m_randomLava)
        {
            m_random = new Random(seed);
            //generateRandomLava(new Vector2d(100,100),new Vector2d(360,360),15,40);
            generateRandomLava(new Vector2d(100,100),new Vector2d(360,360),25,25);
        }
    }


    /**
     * Map constructor, from data structures.
     * @param a_game reference to the game.
     * @param map map contents.
     * @param startingPoint Starting point of the ship.
     * @param goal Position of the goal in the map.
     */
    public CpwBoard(GameCPW a_game, char[][] map, Vector2d startingPoint, Vector2d goal)
    {
        this.m_gameState = a_game;
        m_startingPoint = startingPoint.copy();
        m_goalPosition = new Vector2d();

        m_mapChar = new char[map.length][map[0].length];
        for(int i = 0; i < map.length; ++i)
        {
            for(int j = 0; j < map[0].length; ++j)
            {
                m_mapChar[i][j] = map[i][j];
            }
        }

        m_width = map.length;
        m_height = map[0].length;
        m_goalPosition = goal;
        m_mapChar[(int) goal.x][(int) goal.y] = CpwBoard.WAYPOINT;
        m_mapChar[(int) m_startingPoint.x][(int)m_startingPoint.y] = CpwBoard.START;
    }

    /**
     * Reads the map.
     */
    private void readMap()
    {
        String[][] fileData = File2String.getArray(m_filename);

        int x = 0, xInMap = 0;
        String[] line;
        while(x < fileData.length)
        {
            line = fileData[x]; //Get following line.

            String first = line[0];
            if(first.equalsIgnoreCase("type"))
            {
                //Ignore
            }else if(first.equalsIgnoreCase("height"))
            {
                String h = line[1];
                m_height = Integer.parseInt(h);
            }
            else if(first.equalsIgnoreCase("width"))
            {
                String w = line[1];
                m_width = Integer.parseInt(w);
            }
            else if(first.equalsIgnoreCase("map"))
            {
                //Ignore ... but time to create the map
                m_mapChar = new char[m_width][m_height];
                //System.out.println("Map dimensions: " + m_width + "x" + m_height);
            }
            else
            {
                //MAP INFORMATION
                String lineStr = line[0];
                int yInMap = 0;
                int yInFile = 0;
                while(yInMap < lineStr.length())
                {
                    char data = lineStr.charAt(yInFile);

                    m_mapChar[yInMap][xInMap] = data;

                    if(m_randomLava && isLava(data))
                        m_mapChar[yInMap][xInMap] = CpwBoard.NIL;

                    processData(yInMap, xInMap, data);

                    ++yInMap;
                    ++yInFile;
                }
                ++xInMap;
            }

            ++x;
        }
    }

    /**
     * Generates random squares of lava in the maps.
     * @param min vector with the minimum values of X an Y for the top left corner of each square
     * @param max vector with the maximum values of X an Y for the top left corner of each square
     * @param n number of random squares to create
     * @param s size of the side the square.
     */
    private void generateRandomLava(Vector2d min, Vector2d max, int n, int s)
    {
        for(int i = 0; i < n; ++i)
        {
            double rX = min.x + m_random.nextDouble()* (max.x-min.x);
            double rY = min.y + m_random.nextDouble()* (max.y-min.y);

            int rIntX = (int) Math.round(rX);
            int rIntY = (int) Math.round(rY);

            for(int j = rIntX; j < rIntX+s; ++j)
            {
                for(int k = rIntY; k < rIntY+s; ++k)
                {
                    m_mapChar[j][k] = CpwBoard.LAVA;
                }
            }
        }
    }

    /**
     * Process a given character in a position
     * @param x x coordinate
     * @param y y coordinate
     * @param data data read on the map.
     */
    private void processData(int x, int y, char data)
    {
        if(data == START)
        {
            m_startingPoint.x=x;
            m_startingPoint.y=y;
        }
        else if(data == WAYPOINT)
        {
            m_goalPosition.x=x;
            m_goalPosition.y=y;
            m_gameState.setGoal(new Goal(m_gameState, new Vector2d(x, y)));
        }
    }

    /**
     * Checks if the given point is outside the bounds of the map.
     * @param a_x x ccoordinate
     * @param a_y y coordinate.
     * @return true if the position is outside the map.
     */
    public boolean isOutsideBounds(int a_x, int a_y)
    {
        return (a_x < 0 || a_x >= m_mapChar.length || a_y < 0 || a_y >= m_mapChar[a_x].length);
    }

    public boolean isOnLava(Vector2d pos)
    {
        int xRound = (int)Math.round(pos.x);
        int yRound = (int)Math.round(pos.y);

        if(isOutsideBounds(xRound, yRound))
            return false;

        char inMap = m_mapChar[xRound][yRound];
        return isLava(inMap);
    }


    public boolean isLava(char data)
    {
        if(data == CpwBoard.LAVA)
            return true;
        return false;
    }


    public boolean isObstacle(int a_x, int a_y)
    {
        char inMap = m_mapChar[a_x][a_y];
        return isObstacle(inMap);
    }


    private boolean isObstacle(char data)
    {
        if(data == '@' || data == 'T' || data == '\u0000' || data == CpwBoard.EDGE)
            return true;
        return false;
    }
    /**
     * Checks if the collision at a_x, a_y belongs to a vertical or an horizontal wall.
     * @param a_x x coordinate of the collision point
     * @param a_y y coordinate of the collision point
     * @return true if the collision is against a vertical wall
     */
    public boolean isCollisionUpDown(int a_x, int a_y)
    {
        int consUpDown = 1, consRightLeft = 1; //we suppose there is collision in (a_x, a_y)
        if(a_y+1 < m_mapChar[a_x].length)
        {
            consUpDown += isObstacle(m_mapChar[a_x][a_y+1])? 1:0;
        }
        if(a_y-1 >= 0)
        {
            consUpDown += isObstacle(m_mapChar[a_x][a_y-1])? 1:0;
        }

        if(a_x+1 < m_mapChar.length)
        {
            consRightLeft += isObstacle(m_mapChar[a_x+1][a_y])? 1:0;
        }
        if(a_x-1 >= 0)
        {
            consRightLeft += isObstacle(m_mapChar[a_x-1][a_y])? 1:0;
        }

        return consUpDown>consRightLeft;
    }


    /**
     * Gets the array representing the map
     * @return the array representing the map
     */
    public char[][] getMapChar() {return m_mapChar; }

    /**
     * Gets the starting point in the map
     * @return the starting point.
     */
    public Vector2d getStartingPoint() {return m_startingPoint.copy();}

    /**
     * Gets the height of the map.
     * @return the height of the map.
     */
    public int getMapHeight() {return m_height;}

    /**
     * Gets the width of the map.
     * @return the width of the map.
     */
    public int getMapWidth() {return m_width;}

    /**
     * Gets the map size
     */
    public Dimension getMapSize() {return new Dimension(m_width, m_height);}

    /**
     * Gets the filename this map was loaded from.
     * @return the filename this map was loaded from.
     */
    public String getFilename() {return m_filename;}


    /**
     * Sets the map character array.
     * @param a_mapChar the map character array.
     */
    public void setMapChar(char[][] a_mapChar){m_mapChar = a_mapChar;}

    /**
     * Sets the height of the map.
     * @param a_h the height of the map.
     */
    public void setHeight(int a_h) {m_height = a_h;}

    /**
     * Sets the width of the map.
     * @param a_w the width of the map.
     */
    public void setWidth(int a_w) {m_width = a_w;}

    /**
     * Sets the starting point of the ship in the map.
     * @param a_sp the starting point of the ship in the map.
     */
    public void setStartingPoint(Vector2d a_sp) {m_startingPoint = a_sp;}

    /**
     * Sets the game instance
     * @param a_game the game instance
     */
    public void setGame(GameCPW a_game) {m_gameState = a_game;}

    /**
     * Sets the filename to read the map from.
     * @param a_filename the filename where the map is.
     */
    public void setFilename(String a_filename) {m_filename = a_filename;}

    /**
     * Gets a copy of the map.
     * @param a_game the game isntance.
     * @return The map copy.
     */
    public CpwBoard getCopy(GameCPW a_game)
    {
        CpwBoard copied = new CpwBoard();

        //Copy the map
        /*char[][] mapChar = new char[m_mapChar.length][m_mapChar[0].length];
        for(int i = 0; i < mapChar.length; ++i)
            for(int j = 0; j < mapChar[0].length; ++j)
                mapChar[i][j] = m_mapChar[i][j];
        copied.setMapChar(mapChar);

        copied.setHeight(m_height);
        copied.setWidth(m_width);
        copied.setStartingPoint(m_startingPoint.copy());
        copied.setGoalPosition(m_goalPosition.copy());  */
        copied.setGame(a_game);
        //copied.setFilename(m_filename); //Not copied, no need.

        return copied;
    }

}

