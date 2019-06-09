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
public class QLPlayer implements Player {
    int m_nSims = 100; // number of simulations per move    
    ParetoArchive pa;
    DstState curState;
    Random r;
    QNode qn;
    double target0, target1;

    public QLPlayer(int a_nSims, Random r, double target0)
    {
        m_nSims = a_nSims;
        pa = new ParetoArchive();
        this.r = r;
        this.target0 = target0;
        this.target1 = 1 - target0;
    }
    
    @Override
    public int getMove(State a_gameState)
    {
        curState = (DstState)(a_gameState);
        Roller dstRoller = new RandomRoller(RandomRoller.RANDOM_ROLLOUT, this.r);
        qn = new QNode(curState,null,dstRoller);

        qn.pa = pa;
        qn.qSearch(m_nSims,target0,target1);    //Q-learning training
        pa = qn.pa;

        int action = qn.bestActionWeighted(target0,target1);                 //Q-learning move
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
