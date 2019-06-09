package spgame;

import utils.ParetoArchive;
import utils.Utils;

public class QNode {

    // exploration term
    // 0.5 works ok for Othello
    State state;
    QNode parent;
    public Roller roller;
    public ParetoArchive pa;

    double alpha = 0.1;
    double gamma = 1.0;

    // next thing to work out: when to expand the state...

    public QNode[] children;
    public double qvalue[][];
    public int nCount;

    public QNode(State state, Roller roller) {
        this(state, null, roller);
        this.roller = roller;
        pa = new ParetoArchive();
    }

    public QNode(State state, QNode parent, Roller roller) {
        this.state = state;
        this.parent = parent;
        children = new QNode[state.nActions()];
        this.roller = roller;
        qvalue = new double[state.nActions()][2];
        
        for (int i = 0; i < qvalue.length; ++i)
        {
            qvalue[i][0] = 0;
            qvalue[i][1] = 1;
        }
        
        nCount = 0;
    }

    int depth() {
        if (parent == null) return 0;
        else return 1 + parent.depth();
    }

    public void qSearch(int its, double t0, double t1) {
        for (int i = 0; i < its; i++) {
            qPolicy(t0, t1);
        }
    }

    public void qPolicy(double t0, double t1)
    {
        QNode cur = this;
        while(!cur.state.isTerminal()) {

            int nextAction = cur.roller.roll(cur.state);
            State nextState = cur.state.copy();
            nextState.next(nextAction);
            QNode next = null;

            if(cur.children[nextAction] == null)
            {
                QNode newNode = new QNode(nextState, cur, this.roller);
                cur.children[nextAction] = newNode;
                next = newNode;
            }else{
                next = cur.children[nextAction];
            }

            //2 objectives, weighted by t0 and t1:
            double maxQ = next.maxQ(t0, t1);

            double q = cur.qvalue[nextAction][0];
            double r = next.state.value()[0];
            cur.qvalue[nextAction][0] = q + alpha * (r + gamma * maxQ - q);

            q = cur.qvalue[nextAction][1];
            r = next.state.value()[1];
            cur.qvalue[nextAction][1] = q + alpha * (r + gamma * maxQ - q);

            next.nCount++;
            cur = next;
        }
    }

    boolean nonTerminal() {
        return children != null;
    }

    public double maxQ(double t0, double t1)
    {
        double[][] bounds =  state.getValueBounds();
        double bestValue = -Double.MAX_VALUE;
        for (int i=0; i<qvalue.length; i++)
        {
            double val0 = Utils.normalise(qvalue[i][0], bounds[0][0], bounds[0][1]);
            double val1 = Utils.normalise(qvalue[i][1], bounds[1][0], bounds[1][1]);
            double val = t0 * val0 + t1 * val1;

            if(val > bestValue) {
                bestValue = val;
            }
        }
        return bestValue;
    }


    public int bestActionWeighted(double t0, double t1) {

        int selected = -1;
        double[][] bounds =  state.getValueBounds();
        double bestValue = -Double.MAX_VALUE;
        for (int i=0; i<qvalue.length; i++)
        {
            double val0 = Utils.normalise(qvalue[i][0], bounds[0][0], bounds[0][1]);
            double val1 = Utils.normalise(qvalue[i][1], bounds[1][0], bounds[1][1]);
            double val = t0 * val0 + t1 * val1;

            if(val > bestValue) {
                bestValue = val;
                selected = i;
            }
        }
        if (selected == -1)
            throw new RuntimeException("Unexpected selection!");
        return selected;
    }
}
