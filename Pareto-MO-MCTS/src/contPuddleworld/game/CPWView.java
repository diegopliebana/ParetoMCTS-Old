package contPuddleworld.game;


import utils.Vector2d;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;

/**
 * This class paints the GameCPW on the screen. It is used from the Runner class, who knows if graphics are enabled or not
 * for the execution.
 * PTSP-Competition
 * Created by Diego Perez, University of Essex.
 * Date: 20/12/11
 */
public class CPWView extends JComponent
{

    /**
     * Reference to the GameCPW to be painted.
     */
    private GameCPW m_GameCPW;

    /**
     * Reference to the ship of the GameCPW.
     */
    private Ship m_ship;

    /**
     * reference to the CpwBoard instance where the GameCPW is being played.
     */
    private CpwBoard m_CpwBoard;

    /**
     * Dimensions of the CpwBoard and, hence, of the window.
     */
    private Dimension m_size;

    /**
     * Font for the stats of the GameCPW (time left, time spent).
     */
    private Font m_font;

    /**
     * Font for the results of the GameCPW.
     */
    private Font m_font2;

    /**
     * List of positions where the ship has been located. Used to draw the ship's trajectory.
     */
    private LinkedList<Vector2d> m_positions;

    /**
     * Random number generator.
     */
    private Random m_rnd;

    //Colors:
    //Paper easy-read format:
    /*private Color background = Color.white;
    private Color trajectory = Color.black;
    private Color obstacle = Color.darkGray;
    private Color finalResult = Color.yellow;
    private Color fontColor = Color.red;
    private Color hudBackground = new Color(30,30,30);
    private Color lava1 = new Color(195,195,195);
    private Color lava2 = new Color(128,128,128);         */

    //Execution format:
    private Color background = Color.black;                      //Dark brown
    private Color trajectory = Color.white;
    private Color obstacle = Color.gray;
    private Color fontColor = Color.MAGENTA;
    private Color lava1 = new Color(255,127,39);          //Orange
    private Color lava2 = new Color(255,201,14);          //Yellow-ish orange
    private Color hudBackground = new Color(30,30,30);    //Dark grey

    /**
     * Signals the first time the GameCPW is pained.
     */
    private boolean m_firstDraw;

    /**
     * Buffer for the image of the CpwBoard.
     */
    private BufferedImage m_CpwBoardImage;

    /**
     * Controller, used for debug printing.
     */
    private Controller m_controller;

    /**
     * Extra space for HUD
     */
    private static int HUD_SPACE = 80;

    /**
     * Constructor of the class.
     * @param a_GameCPW GameCPW to paint.
     * @param a_size Size of the CpwBoard.
     * @param a_CpwBoard CpwBoard to be painted.
     * @param a_ship Ship of the GameCPW.
     * @param a_controller Controller of the ship.
     */
    public CPWView(GameCPW a_GameCPW, Dimension a_size, CpwBoard a_CpwBoard, Ship a_ship, Controller a_controller) {
        m_GameCPW = a_GameCPW;
        m_CpwBoard = a_CpwBoard;
        m_size = a_size;   m_size.height += HUD_SPACE;   //We add some space for the HUD.
        m_ship = a_ship;
        m_font = new Font("Courier", Font.PLAIN, 14);
        m_font2 = new Font("Courier", Font.BOLD, 14);
        m_positions = new LinkedList<Vector2d>();
        m_firstDraw = true;
        m_CpwBoardImage = null;
        m_controller = a_controller;
        m_rnd = new Random();
    }

    /**
     * Main method to paint the GameCPW
     * @param gx Graphics object.
     */
    public void paintComponent(Graphics gx)
    {
        Graphics2D g = (Graphics2D) gx;

        //For a better graphics, enable this: (be aware this could bring performance issues depending on your HW & OS).
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(obstacle);
        g.fillRect(0, m_size.height - HUD_SPACE, m_size.width, m_size.height);

        //g.setColor(background);
       // g.fillRect(0, 0, m_size.width, m_size.height - HUD_SPACE);

        //Draw the CpwBoard.
        if(m_firstDraw)
        {
            //Copy to the buffer only the first time the GameCPW is drawn.
            m_CpwBoardImage = new BufferedImage(m_size.width, m_size.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D gImage = m_CpwBoardImage.createGraphics();
            //gImage.setColor(background);
            //gImage.fillRect(0, 0, m_size.width, m_size.height);

            gImage.setColor(hudBackground);
            gImage.fillRect(0, m_size.height - HUD_SPACE, m_size.width, m_size.height);

            gImage.setColor(background);
            gImage.fillRect(0, 0, m_size.width, m_size.height - HUD_SPACE);

            for(int i = 0; i < m_CpwBoard.getMapChar().length; ++i)
            {
                for(int j = 0; j < m_CpwBoard.getMapChar()[i].length; ++j)
                {
                    if(m_CpwBoard.isObstacle(i,j))
                    {
                        gImage.setColor(obstacle);
                        gImage.fillRect(i,j,1,1);
                    }else if(m_CpwBoard.isLava(m_CpwBoard.getMapChar()[i][j]))
                    {
                        if(m_rnd.nextFloat() < 0.25f)
                            gImage.setColor(lava1);
                        else
                            gImage.setColor(lava2);
                        gImage.fillRect(i,j,1,1);
                    }
                }
            }
            m_firstDraw = false;

        } else {
            //Just paint the buffer from the 2nd time on.
            g.drawImage(m_CpwBoardImage,0,0,null);
        }

        //Paint all objects of the GameCPW.
        m_GameCPW.getGoal().draw(g);
        m_GameCPW.getShip().draw(g);

        //Update positions to draw trajectory.
        if(m_ship.ps.x != m_ship.s.x || m_ship.ps.y != m_ship.s.y)
        {
            m_positions.add(m_ship.s.copy());
        }

        //Draw the trajectory
        g.setColor(trajectory);
        Vector2d oldPos = null;
        for(Vector2d pos : m_positions)
        {
            if(oldPos == null)
            {
                oldPos = pos;
            }else
            {
                g.drawLine((int) Math.round(oldPos.x),(int) Math.round(oldPos.y),(int) Math.round(pos.x),(int) Math.round(pos.y));
                oldPos = pos;
            }
        }

        //Paint stats of the m_GameCPW.
        paintStats(g);

        //Draw controller paint stuff
        if(m_controller != null)
            m_controller.paint(g);
    }

    /**
     * Paints texts on the GameCPW, as the total and time left, and results.
     * @param g Graphics device.
     */
    private void paintStats(Graphics2D g)
    {
        g.setColor(fontColor);
        g.setFont(m_font);
        g.drawString("Total time: " + m_GameCPW.getNumMoves(), 10, m_size.height - 60);
        g.drawString("Lava time: " + m_GameCPW.getNumLava(),  m_size.width - 180, m_size.height - 60);

    }

    /**
     * Gets the dimensions of the window.
     * @return the dimensions of the window.
     */
    public Dimension getPreferredSize() {
        return m_size;
    }

}
