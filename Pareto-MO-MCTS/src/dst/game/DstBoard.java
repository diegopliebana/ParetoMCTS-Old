package dst.game;

import utils.File2String;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 10:24
 * To change this template use File | Settings | File Templates.
 */
public class DstBoard
{
    /**
     * Board for the DST.
     */
    public int[][] m_board;

    /**
     * Position of the hunter
     */
    public int[] m_playerPosition;

    /**
     * Valid moves:
     */
    public ArrayList m_moves;

    /**
     * Filename with the board.
     */
    public String m_filename = "./dstBoard.txt";
    

    //min an max values for the reward.
    public double m_minValue = 0; //In DST, 0 is the minimum reward for treasure (not 1!!)
    public double m_maxValue = -1;


    public DstBoard()
    {
        m_playerPosition = new int[]{0,0};

        m_moves = new ArrayList();
        m_moves.add(DstConstants.MOVE_UP);
        m_moves.add(DstConstants.MOVE_RIGHT);
        m_moves.add(DstConstants.MOVE_DOWN);
        m_moves.add(DstConstants.MOVE_LEFT);
    }
    
    public void readBoard()
    {
        String[][] fileData = File2String.getArray(m_filename);
        m_board = new int[fileData.length][fileData[0].length];
        
        for(int i = 0; i < fileData.length; ++i)
        {
            for(int j = 0; j < fileData[i].length; ++j)
            {
                m_board[i][j] = Integer.parseInt(fileData[i][j]);
                if(m_board[i][j] > m_maxValue)
                    m_maxValue = m_board[i][j];
                if(m_board[i][j] < m_minValue && m_board[i][j] > 0)
                    m_minValue = m_board[i][j];
            }
        }
    }

    public ArrayList getMoves()
    {
        return m_moves;
    }


    public void playMove(int a_action)
    {
        int p0 = m_playerPosition[0], p1 = m_playerPosition[1];
        switch(a_action)
        {
            case DstConstants.MOVE_RIGHT:
                p1 = Math.min(m_board[0].length-1,m_playerPosition[1]+1);
                break;
            case DstConstants.MOVE_LEFT:
                p1 = Math.max(0,m_playerPosition[1]-1);
                break;
            case DstConstants.MOVE_UP:
                p0 = Math.min(0,m_playerPosition[0]+1);
                break;
            case DstConstants.MOVE_DOWN:
                p0 = Math.max(-m_board.length+1,m_playerPosition[0]-1);
                break;
            default:
                throw new RuntimeException("Unknown move: " + a_action);
        }

        //Make the move if it is not the floor
        if(valueInBoard(p0,p1) != DstConstants.POS_GROUND)
        {
            m_playerPosition[0] = p0;
            m_playerPosition[1] = p1;
        }

    }

    public boolean isTerminal()
    {
        int valueInBoard = valueInBoard();
        if(valueInBoard != DstConstants.POS_NIL && valueInBoard != DstConstants.POS_GROUND)
        {
            return true;
        }
        else return false;
    }

    public double value()
    {
        int valueInBoard = valueInBoard();
        if(valueInBoard == DstConstants.POS_NIL || valueInBoard == DstConstants.POS_GROUND)
        {
            return 0;
        }
        else return valueInBoard;
    }

    public int valueInBoard(int p0, int p1)
    {
        //System.out.println("POS: " + m_playerPosition[0] + "," + m_playerPosition[1]);
        return m_board[Math.abs(p0)][p1];
    }

    public int valueInBoard()
    {
        //System.out.println("POS: " + m_playerPosition[0] + "," + m_playerPosition[1]);
        return m_board[Math.abs(m_playerPosition[0])][m_playerPosition[1]];
    }

    public double getMinValue() {return m_minValue;}
    public double getMaxValue() {return m_maxValue;}

    public DstBoard copy()
    {
        DstBoard b = new DstBoard();
        b.m_board = new int[m_board.length][m_board[0].length];
        for(int i = 0; i < m_board.length; ++i)
        {
            for(int j = 0; j < m_board[i].length; ++j)
            {
                b.m_board[i][j] = m_board[i][j];
            }
        }
        b.m_playerPosition[0] = m_playerPosition[0];
        b.m_playerPosition[1] = m_playerPosition[1];
        b.m_minValue = m_minValue;
        b.m_maxValue = m_maxValue;
        return b;
    }



}
