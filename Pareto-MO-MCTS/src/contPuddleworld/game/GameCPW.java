package contPuddleworld.game;

import contPuddleworld.players.KeyPlayer;
import dst.game.DstConstants;
import spgame.Player;
import spgame.State;
import utils.JEasyFrame;
import utils.StatSummary;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
public class GameCPW implements State
{
    public CpwBoard m_board;
    public Goal m_goal;
    public Ship m_ship;
    public Player m_player;

    public boolean m_gameStarted;
    public boolean m_gameOver;
    public static ArrayList m_moves;

    /**
     * Onjectives: Number of moves and cycles over lava.
     */
    private int m_numMoves;
    private int m_numLava;

    public GameCPW()
    {

    }

    public GameCPW(String a_filename, boolean randomizeLava, int seed)
    {
        m_board = new CpwBoard(this, a_filename, randomizeLava, seed);
        m_ship = new Ship(this, m_board.getStartingPoint());
        m_numMoves = 0;
        m_numLava = 0;
        m_gameStarted = false;
        m_gameOver = false;
        m_player = null;

        m_moves = new ArrayList();
        m_moves.add(Controller.ACTION_NO_FRONT);
        m_moves.add(Controller.ACTION_NO_LEFT);
        m_moves.add(Controller.ACTION_NO_RIGHT);
        m_moves.add(Controller.ACTION_THR_FRONT);
        m_moves.add(Controller.ACTION_THR_LEFT);
        m_moves.add(Controller.ACTION_THR_RIGHT);
    }

    public GameCPW(CpwBoard a_b,  Goal a_goal, Ship a_ship)
    {
        m_board = a_b;
        m_goal = a_goal;
        m_ship = a_ship;
    }

    public GameCPW copy()
    {
        GameCPW g = new GameCPW();
        g.m_gameStarted = this.m_gameStarted;
        g.m_gameOver = this.m_gameOver;
        g.m_numMoves = this.m_numMoves;
        g.m_numLava = this.m_numLava;

        g.m_board = this.m_board.getCopy(g);
        g.m_goal = this.m_goal.getCopy(g);
        g.m_ship = this.m_ship.getCopy(g);

        return g;
    }

    public double[] run(boolean visuals, double delay)
    {
        CPWView view = null;
        JEasyFrame frame = null;

        if(visuals)
        {
            view = new CPWView(this,m_board.getMapSize(),m_board, m_ship, null);
            frame = new JEasyFrame(view, "Continuous Puddleworld");


            //If we are going to play the game with the cursor keys, add the listener for that.
            if(m_player instanceof KeyPlayer)
            {
                frame.addKeyListener(((KeyPlayer)m_player).getInput());
            }
        }


        if(visuals)
            waitStep(2000);

        StatSummary ss = new StatSummary();

        while(!isTerminal())
        {
            long then = System.currentTimeMillis();
            long due = then+Controller.ACTION_TIME_MS;

            int move = m_player.getMove(this.copy());
            this.next(move);

            long now = System.currentTimeMillis();

            /*long howManyTotal = (now-then);
            if(howManyTotal > 1)
            {
                double realHowMany = howManyTotal / Controller.MACRO_ACTION_LENGTH;
                if(realHowMany > 30 && realHowMany < 50)
                {
                    System.out.println("MILLISECONDS: " + realHowMany);
                    ss.add(realHowMany);
                }else System.out.println("leaving out " + realHowMany);
            }*/

            int remaining = (int) Math.max(0, delay - (now-then));     //To adjust to the proper framerate.

            //Wait until de next cycle.

            if(visuals)
            {
                waitStep(remaining);
                view.repaint();
            }

        }

        //System.out.format("%.2f %.2f %.2f\n", ss.mean(), ss.sd(), ss.stdErr());

        return this.value();
    }


    public void next(int a_action)
    {
        m_ship.update(a_action);

        //if(m_gameStarted)
        {
            if(m_board.isOnLava(m_ship.s))
            {
                m_numLava++;
            }

            m_numMoves++;
        }
    }


    public boolean isTerminal()
    {
        return m_gameOver || (m_numMoves  > Controller.MAX_TIME_STEPS);
    }

    public double[] value()
    {
        double dist = m_ship.s.dist(m_goal.s);
        //System.out.println("Dist: " + dist);

        double mod = 0;
        if(m_goal.isCollected())   //if(dist < Goal.RADIUS)
        {
            mod = 1;
        }else{
            mod = 1 - (dist / Controller.MAX_DISTANCE);
            mod = Math.max(0, mod); //shouldn;t be necessary, but just in case.
        }

        double val[] = new double[]{mod*(Controller.MAX_TIME_STEPS - m_numMoves), mod*(Controller.MAX_TIME_STEPS - m_numLava)};
        //double val[] = new double[]{mod*(Controller.MAX_TIME_STEPS - m_numMoves), mod*(Controller.MAX_TIME_STEPS - m_numMoves)};
        return val;
    }

    public int nActions()
    {
        return 6;
    }

    public double[][] getValueBounds()
    {
        double[][] bounds = new double[2][2];
        bounds[0][0] = 0;
        bounds[0][1] = Controller.MAX_TIME_STEPS;//*Controller.MAX_DISTANCE;
        bounds[1][0] = 0;
        bounds[1][1] = Controller.MAX_TIME_STEPS;//*Controller.MAX_DISTANCE;

        return bounds;
    }

    public Ship getShip() {return m_ship;}
    public void setPlayer(Player a_player){m_player = a_player;}
    public void setGoal(Goal a_goal){ m_goal = a_goal; }
    public Goal getGoal () {return m_goal;}
    public void goalReached() {m_gameOver = true;}
    public void go() {m_gameStarted = true;}
    public int getNumMoves() {return m_numMoves;}
    public int getNumLava(){ return m_numLava; }
    public int getNumTargets() {return 2;}
    public CpwBoard getBoard () {return m_board;}

     /**
     * Waits until the next step.
     * @param duration Amount of time to wait for.
     */
    protected static void waitStep(int duration) {

        try
        {
            Thread.sleep(duration);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

}
