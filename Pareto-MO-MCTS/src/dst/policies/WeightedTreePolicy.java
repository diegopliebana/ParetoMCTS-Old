package dst.policies;

import spgame.ParetoTreeNode;
import spgame.SimpleTreeNode;
import spgame.TreePolicy;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
public class WeightedTreePolicy implements TreePolicy
{
    //For 2 objectives:
    public double target0 = 0.05; //Default values.
    public double target1 = 1-target0;
    public double K;

    public WeightedTreePolicy(){}

    public WeightedTreePolicy(double a_kValue, double target0)
    {
        this.target0 = target0;
        target1 = 1-target0;
        K = a_kValue;
    }

    @Override
    public ParetoTreeNode bestChild(ParetoTreeNode node, double[][] bounds) {
        return null;  //N/A
    }

    @Override
    public SimpleTreeNode bestChild(SimpleTreeNode n, double[][] bounds) {

        SimpleTreeNode selected = null;
        double bestValue = -Double.MAX_VALUE;
        for (SimpleTreeNode child : n.children) {

            double childValue1 = child.totValue[0]/(child.nVisits + n.epsilon);
            childValue1 = Utils.normalise(childValue1, bounds[0][0], bounds[0][1]);
            double childValue2 = child.totValue[1]/(child.nVisits + n.epsilon);
            childValue2 = Utils.normalise(childValue2, bounds[1][0], bounds[1][1]);

            double childValue = childValue1 * target0 +  childValue2 * target1;

            double uctValue = childValue +
                            K * Math.sqrt(Math.log(n.nVisits + 1) / (child.nVisits + n.epsilon)) +
                            n.r.nextDouble() * n.epsilon;
            // small random numbers: break ties in unexpanded nodes
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }
        if (selected == null)
            throw new RuntimeException("Warning! returning null: " + bestValue + " : " + n.children.length);
        return selected;
    }

}
