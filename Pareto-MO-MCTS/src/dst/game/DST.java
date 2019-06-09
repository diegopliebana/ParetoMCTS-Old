package dst.game;

import utils.ParetoArchive;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 25/02/13
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class DST extends AbstractProblem {

    public static final int numObjectives = 2;
    public static final int numVariables = 100;

    public static DstState m_currentState;
    public static ParetoArchive m_pa;
    
    public DST()
    {
        super(numVariables,numObjectives);
    }

    @Override
    public void evaluate(Solution solution) {
        //To change body of implemented methods use File | Settings | File Templates.
        int moves[] = EncodingUtils.getInt(solution);

        DstState stateCopy = m_currentState.copy();
        int effectiveMoves = 0;
        while(!stateCopy.isTerminal() && effectiveMoves < moves.length && stateCopy.getNumMoves() < DstConstants.MAX_MOVES)
        {
            int move = moves[effectiveMoves];
            stateCopy.next(move);
            effectiveMoves++;
        }

        double objectives[] = stateCopy.value();
        m_pa.add(objectives);
        
        //MOEA MINIMIZES BY DEFAULT: We have to negate the objectives to get it right!
        double negObjectives[] = new double[]{-objectives[0], -objectives[1]};

        solution.setObjectives(negObjectives);
    }

    @Override
    public Solution newSolution() {

        Solution solution = new Solution(numberOfVariables, numberOfObjectives);

        for(int i = 0; i < numberOfVariables; ++i)
        {
            solution.setVariable(i, EncodingUtils.newInt(0,3));
        }

        return solution;
    }

}
