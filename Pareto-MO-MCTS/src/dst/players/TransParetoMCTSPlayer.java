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
public class TransParetoMCTSPlayer implements Player {

    int m_nSims = 100; // number of simulations per move
    TreePolicy m_treePolicy;
    ParetoTreeNode tn;
    State curState;
    Random r;
    double target0, target1;
    int lastAction = -1;
    public ParetoArchive globalPA;

    public TransParetoMCTSPlayer(int a_nSims, TreePolicy treePolicy, Random r, double target0)
    {
        m_nSims = a_nSims;
        m_treePolicy = treePolicy;
        this.r = r;
        this.target0 = target0;
        this.target1 = 1 - target0;
        globalPA = new ParetoArchive();
    }
    
    @Override
    public int getMove(State a_gameState)
    {
        curState = (a_gameState);

        long timeNow = System.currentTimeMillis();

        TranspositionTable.GetInstance().reset();
        Roller randomRoller = new RandomRoller(RandomRoller.RANDOM_ROLLOUT, this.r);

        tn = new TransParetoTreeNode(curState,randomRoller,m_treePolicy);
        tn.mctsSearch(m_nSims);

        for(int i = 0; i < tn.pa.m_members.size(); ++i)
        {
            globalPA.add(tn.pa.m_members.get(i));
        }

        lastAction = tn.bestActionIndex(globalPA, new double[]{target0, target1});

        return (Integer) ((DstState)(a_gameState)).getBoard().getMoves().get(lastAction);
    }
    
    public double getHV(boolean a_normalized)
    {
        return tn.getHV(a_normalized);
    }

    public void reset(){}
}
