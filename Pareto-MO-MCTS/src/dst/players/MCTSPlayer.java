package dst.players;

import spgame.RandomRoller;
import dst.game.DstState;
import spgame.*;
import utils.ParetoArchive;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class MCTSPlayer implements Player {
    int m_nSims = 100; // number of simulations per move
    TreePolicy m_treePolicy;
    SimpleTreeNode tn;
    ParetoArchive pa;
    DstState curState;
    Random r;

    public MCTSPlayer (int a_nSims, TreePolicy treePolicy, Random r)
    {
        m_nSims = a_nSims;
        m_treePolicy = treePolicy;
        pa = new ParetoArchive();
        this.r = r;
    }
    
    @Override
    public int getMove(State a_gameState)
    {
        curState = (DstState)(a_gameState);
        Roller dstRoller = new RandomRoller(RandomRoller.RANDOM_ROLLOUT, this.r);
        tn = new SimpleTreeNode(curState,dstRoller,m_treePolicy);
        tn.pa = pa;
        tn.mctsSearch(m_nSims);
        pa = tn.pa;
        int action = tn.bestActionIndex();
        return (Integer) ((DstState)(a_gameState)).getBoard().getMoves().get(action);
    }
    
    public double getHV(boolean a_normalized)
    {
        if(a_normalized)
            return pa.computeHV2(curState.getValueBounds());
        else return
                pa.computeHV2();

    }

    public void reset(){}

}
