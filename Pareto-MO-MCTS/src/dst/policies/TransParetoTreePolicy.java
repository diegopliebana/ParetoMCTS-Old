package dst.policies;

import dst.game.DstState;
import dst.players.TranspositionTable;
import spgame.ParetoTreeNode;
import spgame.SimpleTreeNode;
import spgame.TransParetoTreeNode;
import spgame.TreePolicy;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 28/02/13
 * Time: 10:27
 * To change this template use File | Settings | File Templates.
 */
public class TransParetoTreePolicy implements TreePolicy{

    public double K;

    public TransParetoTreePolicy(double a_kValue)
    {
        K = a_kValue;
    }

    @Override
    public ParetoTreeNode bestChild(ParetoTreeNode node, double[][] bounds) {

        ParetoTreeNode selected = null;
        double bestValue = -Double.MAX_VALUE;
        for (ParetoTreeNode child : node.children) {

            int[] playerPos = ((DstState)(child.state)).getBoard().m_playerPosition;
            int numMoves = ((DstState)(child.state)).getNumMoves();
            double[] accumStats = TranspositionTable.GetInstance().getDataInPosition(Math.abs(playerPos[0]), playerPos[1], numMoves);
            int Nsa = ((TransParetoTreeNode)node).m_childCount[child.childIndex];
            int Ns = (int) accumStats[1];

            double childValue = accumStats[0]; //According to Childs et al. UCT2
            //childValue *= (double)Nsa/Ns; //According to Childs et al. UCT3
            childValue /= (double)Ns; //Normal UCT.
            childValue = Utils.normalise(childValue, 0.0, bounds[0][1] * bounds[1][1]);

            //double childValue = child.totValue[0] * 0.5 +  child.totValue[1] * 0.5;
            //childValue /= (child.nVisits + node.epsilon);

            double uctValue = childValue +
                    K * Math.sqrt(Math.log(node.nVisits + 1) / (child.nVisits + node.epsilon)) +
                    node.r.nextDouble() * node.epsilon;
            // small random numbers: break ties in unexpanded nodes
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }
        if (selected == null)
            throw new RuntimeException("Warning! returning null: " + bestValue + " : " + node.children.length);
        return selected;
    }

    @Override
    public SimpleTreeNode bestChild(SimpleTreeNode node, double[][] bounds) {
        return null;  //N/A
    }
}
