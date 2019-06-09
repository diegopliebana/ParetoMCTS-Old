package spgame;

import dst.game.DstState;
import dst.players.TranspositionTable;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 04/03/13
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class TransParetoTreeNode extends ParetoTreeNode
{
    public boolean m_newNodeCreated;
    public boolean USE_TRANSPOSITION_TABLE = true;
    public int[] m_childCount;

    public TransParetoTreeNode(State state, Roller roller, TreePolicy treePolicy) {
        this(state, null, -1, roller, treePolicy);
    }

    public TransParetoTreeNode(State state, ParetoTreeNode parent, int childIndex, Roller roller, TreePolicy treePolicy) {
        super(state, parent, childIndex, roller, treePolicy);

        m_childCount = new int[state.nActions()];
    }

    @Override
    public ParetoTreeNode treePolicy() {

        ParetoTreeNode cur = this;
        ParetoTreeNode oldCur = this;
        while (cur.nonTerminal() && !cur.state.isTerminal())
        {
            oldCur = cur;
            if (cur.notFullyExpanded()) {
                m_newNodeCreated = true;
                ParetoTreeNode n = cur.expand();
                
                if(m_newNodeCreated)
                {
                    m_runList.add(0,n);
                    return n;
                }
                cur = n;
                
            } else {
                cur = cur.bestChild();
            }
            m_runList.add(0,cur);
        }
        return cur;
    }

    @Override
    public ParetoTreeNode expand() {
        // choose a random unused action and add a new node for that
        int bestAction = -1;
        double bestValue = -1;
        for (int i = 0; i < children.length; i++) {
            double x = r.nextDouble();
            if (x > bestValue && children[i] == null) {
                bestAction = i;
                bestValue = x;
            }
        }
        State nextState = state.copy();
        nextState.next(bestAction);

        int[] playerPos = ((DstState)(nextState)).getBoard().m_playerPosition;
        int numMoves = ((DstState)(nextState)).getNumMoves();
        TransParetoTreeNode rep = TranspositionTable.GetInstance().getRepresentative(Math.abs(playerPos[0]), playerPos[1], numMoves);

        if(!USE_TRANSPOSITION_TABLE)
            rep = null; //set this here to unable the use of transposition tables

        if(rep == null)
        {
            //This is a new position explored in the tree: create node, assign it as a child, and added to the list of nodes in this posittion (set it as representative).
            TransParetoTreeNode tn = new TransParetoTreeNode(nextState, this, bestAction, this.roller, this.treePolicy);
            children[bestAction] = tn;
            TranspositionTable.GetInstance().addNodeToList(Math.abs(playerPos[0]), playerPos[1],numMoves, tn);
        }else{
            //No need to add anything, just create the link to this node.
            children[bestAction] = rep;
            m_newNodeCreated = false;
        }

        return children[bestAction];
    }

    @Override
    public void backUp(double result[], boolean added, int cI) {
        //nVisits++;
        //added = pa.add(result);
        int comingFrom = this.childIndex;

        //for(int i = 0; i < result.length; ++i)
        //    totValue[i] += result[i];

        //for(ParetoTreeNode pn : m_runList)
        int numNodes = m_runList.size();
        for(int i = 0; i < numNodes; ++i)
        {
            ParetoTreeNode pn = m_runList.get(i);
            pn.nVisits++;

            if(added)
                added = pn.pa.add(result);

            for(int j = 0; j < result.length; ++j)
                pn.totValue[j] += result[j];

            if(i+1 < numNodes)
            {
                TransParetoTreeNode parentNode = (TransParetoTreeNode) m_runList.get(i+1);
                parentNode.m_childCount[pn.childIndex]++;
                comingFrom = pn.childIndex;
            }
            else if(i+1 == numNodes)
            {
                if(pn.parent != null)
                    throw new RuntimeException("This should be the root... and it's not.");

                if(added)
                {
                    pn.valueRoute.get(comingFrom).add(result);
                }

            }

        }

    }

    public double Q()
    {
        //return totValue[0]*totValue[1];
        return getHV(false);
    }

}
