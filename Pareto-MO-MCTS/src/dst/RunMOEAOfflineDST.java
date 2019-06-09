package dst;

import dst.game.DST;
import dst.game.DstBoard;
import spgame.RandomRoller;
import dst.game.DstState;
import dst.players.TranspositionTable;
import dst.policies.TransParetoTreePolicy;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import spgame.TransParetoTreeNode;
import utils.ParetoArchive;
import utils.StatSummary;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 28/02/13
 * Time: 20:37
 * To change this template use File | Settings | File Templates.
 */
public class RunMOEAOfflineDST
{
    public static void runNSGAII()
    {
        int numRuns = 40;
        TreeMap<Integer, LinkedList<Double>> progress = new TreeMap<Integer, LinkedList<Double>>();

        for(int run = 0; run < numRuns; ++run)
        {
            progress.put(run, new LinkedList<Double>());

            DST.m_pa = new ParetoArchive();
            PlayDST.loadOptima();
            DstBoard board = new DstBoard();
            board.readBoard();
            DstState curState = new DstState(board);
            DST.m_currentState = curState;
            String algorithmName = "NSGAII";
            int nEvals = 100000;

            Instrumenter instrumenter = new Instrumenter()
                    .withReferenceSet(new File("./pf/DST.dat"))
                    .withFrequency(1)
                    .attachAll();

            NondominatedPopulation result = new Executor()
                    .withProblemClass(DST.class)
                    .withAlgorithm(algorithmName)
                    .withMaxEvaluations(nEvals)
                    .withProperty("populationSize", 50)
                    .withInstrumenter(instrumenter)
                    .run();

            Accumulator accumulator = instrumenter.getLastAccumulator();


            for (int i=0; i<accumulator.size("NFE"); i++) {

                ParetoArchive pa = new ParetoArchive();

                ArrayList<Solution> solutions = (ArrayList) (accumulator.get("Approximation Set", i));
                for (Solution solution : solutions)
                {

                    double solutionValue[] = solution.getObjectives();
                    solutionValue[0] = -solutionValue[0];
                    solutionValue[1] = -solutionValue[1];
                    pa.add(solutionValue);
                }

                //System.out.println(accumulator.get("NFE", i) + "\t" + pa.computeHV2());
                progress.get(run).add(pa.computeHV2());
            }

            if(run == numRuns-1)
            {
                System.out.println("# Evals HV-Run0 HV-Run1 HV-Run2 ...");

                for (int i=0; i<accumulator.size("NFE"); i++) {
                    StatSummary ss = new StatSummary();
                    System.out.print(accumulator.get("NFE", i) + " ");

                    for(int r = 0; r < numRuns; ++r)
                    {
                        System.out.print(progress.get(r).get(i) + " ");
                        ss.add(progress.get(r).get(i));
                    }
                    System.out.format("%.3f, %.3f, %.3f\n", ss.mean(), ss.sd(), ss.stdErr());
                }
            }


        }
    }


    public static void runSMSEMOA()
    {
        int numRuns = 40;
        TreeMap<Integer, LinkedList<Double>> progress = new TreeMap<Integer, LinkedList<Double>>();
        int maxNumEvals = 100000, initNumEvals = 50, incNumEvals = 50;

        for(int run = 0; run < numRuns; ++run)
        {
            progress.put(run, new LinkedList<Double>());

            for(int nEvals = initNumEvals; nEvals <= maxNumEvals; nEvals += incNumEvals){

                ParetoArchive pa = new ParetoArchive();

                DST.m_pa = new ParetoArchive();
                PlayDST.loadOptima();
                DstBoard board = new DstBoard();
                board.readBoard();
                DstState curState = new DstState(board);
                DST.m_currentState = curState;
                String algorithmName = "SMSEMOA";

                NondominatedPopulation result = new Executor()
                        .withProblemClass(DST.class)
                        .withAlgorithm(algorithmName)
                        .withMaxEvaluations(nEvals)
                        .withProperty("populationSize", 50)
                        .run();


                for (Solution solution : result)
                {

                    double solutionValue[] = solution.getObjectives();
                    solutionValue[0] = -solutionValue[0];
                    solutionValue[1] = -solutionValue[1];
                    pa.add(solutionValue);
                }

                progress.get(run).add(pa.computeHV2());

            }

            if(run == numRuns-1)
            {
                System.out.println("# Evals HV-Run0 HV-Run1 HV-Run2 ...");


                for(int nEvals = initNumEvals, i = 0; nEvals <= maxNumEvals; nEvals += incNumEvals, ++i)
                {
                    StatSummary ss = new StatSummary();
                    System.out.print(nEvals + " ");
                    for(int r = 0; r < numRuns; ++r)
                    {
                        double hv = progress.get(r).get(i);
                        System.out.print(hv + " ");
                        ss.add(hv);
                    }
                    System.out.format(" [%.3f, %.3f, %.3f]\n", ss.mean(), ss.sd(), ss.stdErr());
                }
            }


        }
    }

    public static void runParetoMCTS()
    {
        int numRuns = 40;
        double kValue = 1; //Math.sqrt(2); //20;//Math.sqrt(2);
        TreeMap<Integer, LinkedList<Double>> progress = new TreeMap<Integer, LinkedList<Double>>();
        int maxNumEvals = 100050, initNumEvals = 1, incNumEvals = 1;

        for(int run = 0; run < numRuns; ++run)
        {
            double hv = 0.0;
            progress.put(run, new LinkedList<Double>());

            PlayDST.loadOptima();
            DstBoard board = new DstBoard();
            board.readBoard();
            DstState curState = new DstState(board);

            Random rnd = new Random();
            //ParetoTreeNode tn = new ParetoTreeNode(curState,new DstRoller(DstRoller.RANDOM_ROLLOUT, rnd), new ParetoTreePolicy(kValue));//new RandomTreePolicy()); //new ParetoTreePolicy(kValue));
            TransParetoTreeNode tn = new TransParetoTreeNode(curState,new RandomRoller(RandomRoller.RANDOM_ROLLOUT, rnd), new TransParetoTreePolicy(kValue));

            TranspositionTable.GetInstance().reset();

            int numEvals = 0;

            while(numEvals <= maxNumEvals)
            {
                if(numEvals > 50 && numEvals < 100)
                    incNumEvals = 5;
                else if(numEvals > 100 && numEvals < 10000)
                    incNumEvals = 10;
                else if(numEvals > 10000)
                    incNumEvals = 50;

                if(hv < 10455.0)
                    tn.mctsSearch(incNumEvals);

                hv = tn.getHV(false);
                progress.get(run).add(hv);
                //System.out.println(numEvals + " " + tn.getHV(false));
                numEvals += incNumEvals;
            }

            incNumEvals = 1;
            if(run == numRuns-1)
            {
                System.out.println("# Evals HV-Run0 HV-Run1 HV-Run2 ...");

                for(int nEvals = initNumEvals, i = 0; nEvals <= maxNumEvals; nEvals += incNumEvals, ++i)
                {
                    if(nEvals > 50 && nEvals < 100)
                        incNumEvals = 5;
                    else if(nEvals > 100 && nEvals < 10000)
                        incNumEvals = 10;
                    else if(nEvals > 10000)
                        incNumEvals = 50;

                    System.out.print(nEvals + " ");
                    StatSummary ss = new StatSummary();
                    for(int r = 0; r < numRuns; ++r)
                    {
                        ss.add(progress.get(r).get(i));
                        System.out.print(progress.get(r).get(i) + " ");
                    }
                    System.out.format("%.3f %.3f %.3f\n", ss.mean(), ss.sd(), ss.stdErr());

                }
            }

        }
    }

    public static void main(String args[])
    {
        runNSGAII();
        //runSMSEMOA();
        //runParetoMCTS();
    }
    
    
}
