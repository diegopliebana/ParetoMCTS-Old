package contPuddleworld.game;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;
import spgame.State;
import utils.ParetoArchive;
import utils.Vector2d;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 25/02/13
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class CPW extends AbstractProblem {

    public static final int numObjectives = 2;
    public static final int numVariables = Controller.ROLLOUT_DEPTH;

    public static GameCPW m_currentState;
    public static ParetoArchive m_pa;

    public CPW()
    {
        super(numVariables,numObjectives);
    }

    @Override
    public void evaluate(Solution solution) {
        //To change body of implemented methods use File | Settings | File Templates.
        int moves[] = EncodingUtils.getInt(solution);
        Vector2d v = new Vector2d();

        GameCPW stateCopy = m_currentState.copy();
        int effectiveMoves = 0;
        while(!stateCopy.isTerminal() && effectiveMoves < moves.length)
        {
            int move = moves[effectiveMoves];
            advance(stateCopy,move);
            if(effectiveMoves == 0)
            {
                v = stateCopy.m_ship.s.copy();
            }

            effectiveMoves++;

        }

        double objectives[] = stateCopy.value();

        //MOEA MINIMIZES BY DEFAULT: We have to negate the objectives to get it right!
        double negObjectives[] = new double[]{-objectives[0], -objectives[1]};

        solution.setObjectives(negObjectives);
    }

    public void advance(State st, int action)
    {
        boolean gameOver = false;
        for(int singleAction = 0; !gameOver && singleAction < Controller.MACRO_ACTION_LENGTH; ++singleAction)
        {
            st.next(action);
            gameOver = st.isTerminal();
        }
    }

    @Override
    public Solution newSolution() {

        Solution solution = new Solution(numberOfVariables, numberOfObjectives);

        for(int i = 0; i < numberOfVariables; ++i)
        {
            solution.setVariable(i, EncodingUtils.newInt(0,5));
        }

        return solution;
    }

}
