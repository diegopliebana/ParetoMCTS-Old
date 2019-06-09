package dst.game;

import spgame.Player;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 12:52
 * To change this template use File | Settings | File Templates.
 */
public class DeepSeaTreasure
{
    Player m_player;
    public int[] m_gameMoves;
    DstState m_curState;
    
    
    public DeepSeaTreasure(Player p, DstBoard dst)
    {
        m_player = p;
        m_curState = new DstState(dst);
        m_gameMoves = new int[DstConstants.MAX_MOVES];
    }

    public double[] runGame()
    {
        int moves = 0;
        while(!m_curState.isTerminal())
        {
            int move = m_player.getMove(m_curState);
            m_curState.next(move);
            //System.out.println("POS " + m_curState.getBoard().m_playerPosition[0] + ", "+ m_curState.getBoard().m_playerPosition[1]
            //        + ": " +  m_curState.value()[0] + ", " + m_curState.value()[1]);
            m_gameMoves[moves++] = move;
        }
        return m_curState.value();
    }



}
