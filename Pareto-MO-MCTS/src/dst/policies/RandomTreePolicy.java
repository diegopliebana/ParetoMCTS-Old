package dst.policies;

import spgame.ParetoTreeNode;
import spgame.SimpleTreeNode;
import spgame.TreePolicy;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 28/02/13
 * Time: 10:27
 * To change this template use File | Settings | File Templates.
 */
public class RandomTreePolicy implements TreePolicy{

    public RandomTreePolicy()
    {
    }

    @Override
    public ParetoTreeNode bestChild(ParetoTreeNode node, double[][] bounds) {

        ParetoTreeNode selected = null;
        double bestValue = -Double.MAX_VALUE;
        
        int selection = node.r.nextInt(node.children.length);
        return node.children[selection];
    }

    @Override
    public SimpleTreeNode bestChild(SimpleTreeNode node, double[][] bounds) {
        return null;  //N/A
    }
}
