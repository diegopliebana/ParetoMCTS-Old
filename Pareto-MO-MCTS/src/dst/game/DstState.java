package dst.game;

import spgame.State;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
public class DstState implements State
{

    /**
     * Current board state.
     */
    private DstBoard m_board;

    public LinkedList<Integer> m_moves;

    /**
     * Number of plays.
     */
    private int m_numMoves;


    public DstState(DstBoard a_b)
    {
        m_board = a_b;
        m_numMoves = 0;
        m_moves = new LinkedList<Integer>();
    }


    public DstState(DstBoard a_b, int a_numMoves, LinkedList<Integer> moves)
    {
        m_board = a_b;
        m_numMoves = a_numMoves;
        m_moves = new LinkedList<Integer>();
        /*for(int i = 0; i < moves.size(); ++i)
        {
            int m = moves.get(i);
            m_moves.add(m);
        }                                    **/
    }

    public void next(int a_action)
    {
        m_board.playMove(a_action);
        //m_moves.add(a_action);
        m_numMoves++;
    }

    public DstState copy()
    {
        return new DstState(m_board.copy(),m_numMoves, m_moves);
    }

    public boolean isTerminal()
    {
        if(m_numMoves >= DstConstants.MAX_MOVES)
            return true;
        return m_board.isTerminal();
    }

    public double[] value()
    {
        double val[] = new double[]{DstConstants.MAX_MOVES - m_numMoves, m_board.value()};
        return val;
    }

    public DstBoard getBoard () {return m_board;}

    public int nActions()
    {
        return m_board.m_moves.size();
    }

    public int getNumMoves() {return m_numMoves;}
    public int getNumTargets() {return 2;}

    public double[][] getValueBounds()
    {
        //min and max value for each objective.
        double[][] d = new double[2][2];
        d[0][0] = 0;
        d[0][1] = DstConstants.MAX_MOVES;
        d[1][0] = m_board.getMinValue();
        d[1][1] = m_board.getMaxValue();
        return d;
    }

}
