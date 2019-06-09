package contPuddleworld.players;

import contPuddleworld.game.CPW;
import contPuddleworld.game.Controller;
import contPuddleworld.game.GameCPW;
import contPuddleworld.game.MacroAction;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import spgame.Player;
import spgame.State;
import utils.ParetoArchive;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class MOEAPlayer implements Player {

    int m_nEvals = 100; // number of evaluations per move
    int m_populationSize = 50;//20;
    State curState;
    String m_algorithmName;
    double target0, target1;   //For rolling horizon, which one to take.

    //Macroactions
    private int m_currentMacroAction;                       //Current action in the macro action being executed.
    private MacroAction m_lastAction;                       //Last macro action to be executed.

    public MOEAPlayer(int a_nEvals, String a_algorithm, double target0)
    {
        m_nEvals = a_nEvals;
        CPW.m_pa = new ParetoArchive();
        m_algorithmName =  a_algorithm;
        this.target0 = target0;
        this.target1 = 1-target0;
        m_currentMacroAction = 0;
        m_lastAction = new MacroAction(false,0,Controller.MACRO_ACTION_LENGTH);
    }



    @Override
    public int getMove(State a_gameState)
    {
        if(m_currentMacroAction != 0)
        {
            m_currentMacroAction--;
            return m_lastAction.buildAction();
        }

        int numEvalsMacro = m_nEvals * Controller.MACRO_ACTION_LENGTH;

        CPW.m_pa = new ParetoArchive();
        CPW.m_currentState = (GameCPW)a_gameState;
        //System.out.println(((GameCPW)a_gameState).m_gameOver);

        NondominatedPopulation result = new Executor()
                .withProblemClass(CPW.class)
                .withAlgorithm(m_algorithmName)
                .withMaxEvaluations(numEvalsMacro)
                .withProperty("populationSize", m_populationSize)
                .run();

        //Choose one of the solutions found, to determine which move to make.
        Solution chosen = maxSolutionDist(result, a_gameState);

        int moves[] = EncodingUtils.getInt(chosen);
        int action = moves[0];

        m_currentMacroAction = Controller.MACRO_ACTION_LENGTH - 1;
        m_lastAction = new MacroAction(action,Controller.MACRO_ACTION_LENGTH);
        return m_lastAction.buildAction();
    }
    
    public double getHV(boolean a_normalized)
    {
        if(a_normalized)
            return CPW.m_pa.computeHV2(curState.getValueBounds());
        else
            return CPW.m_pa.computeHV2();

    }

    public void reset()
    {
        CPW.m_pa = new ParetoArchive();
    }

    //Selects a single solution, to make a move, according to the weights provided.
    public Solution maxSolution(NondominatedPopulation result, State a_gameState)
    {
        double[][] bounds =  a_gameState.getValueBounds();
        double bestValue = -Double.MAX_VALUE;
        Solution chosen = null;

        for (Solution solution : result) {
            double solutionValue[] = solution.getObjectives();
            double val0 = Utils.normalise(-solutionValue[0], bounds[0][0], bounds[0][1]); //REMEMBER to negate the solutions (MOEA minimizes)!
            double val1 = Utils.normalise(-solutionValue[1], bounds[1][0], bounds[1][1]);
            double val = this.target0 * val0 + this.target1 * val1;

            if(val > bestValue) {
                bestValue = val;
                chosen = solution;
            }
        }

        if(chosen == null)
            throw new RuntimeException("No solution found :(");

        return chosen;
    }

    //Selects a single solution, to make a move, according to the weights provided.
    public Solution maxSolutionDist(NondominatedPopulation result, State a_gameState)
    {
        double[][] bounds =  a_gameState.getValueBounds();
        double distance = Double.MAX_VALUE;
        Solution chosen = null;
        double[] targets = new double[]{this.target0, this.target1};
        int i = 0;

        for (Solution solution : result) {
            double solutionValue[] = solution.getObjectives();
            double val0 = Utils.normalise(-solutionValue[0], bounds[0][0], bounds[0][1]); //REMEMBER to negate the solutions (MOEA minimizes)!
            double val1 = Utils.normalise(-solutionValue[1], bounds[1][0], bounds[1][1]);
            double[] thisResNorm = new double[]{val0, val1};
            double thisDist = Utils.distanceEuq(thisResNorm, targets);
            if(thisDist < distance)
                {
                    distance = thisDist;
                    chosen = solution;
                }
        }

        if(chosen == null)
            throw new RuntimeException("No solution found :(");

        //System.out.println("SOLUTION " + (i++) + ": " + chosen.getObjective(0) + "," + chosen.getObjective(1) + " moving " + chosen.getVariable(0));
        //System.out.println("######");

        return chosen;
    }

}
