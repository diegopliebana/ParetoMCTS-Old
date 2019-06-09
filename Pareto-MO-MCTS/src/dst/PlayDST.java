package dst;

import dst.game.DST;
import dst.game.DeepSeaTreasure;
import dst.game.DstBoard;
import dst.game.DstState;
import dst.players.*;
import spgame.policies.ParetoTreePolicy;
import spgame.policies.SimpleHVTreePolicy;
import dst.policies.WeightedTreePolicy;
import spgame.Player;
import utils.ParetoArchive;
import utils.StatSummary;
import utils.Utils;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class PlayDST
{

    public static double [][] optima = new double[10][2];
    public static double [] optimaHV = new double[10];
    public static double [][] bounds = new double[2][2];
    public static int optimaCount[] = new int[10];
    public static int optimaHits[] = new int[10];
    public static int optimaDiscoveries[] = new int[10];

    public static final boolean SHOW_TRAJECTORY = false;
    public static final boolean SHOW_GAME_RES = false;
    public static double optimalHValue;

    public static void loadOptima()
    {
        optima[0][0] = 99; optima[0][1] = 1;
        optima[1][0] = 97; optima[1][1] = 2;
        optima[2][0] = 95; optima[2][1] = 3;
        optima[3][0] = 93; optima[3][1] = 5;
        optima[4][0] = 92; optima[4][1] = 8;
        optima[5][0] = 91; optima[5][1] = 16;
        optima[6][0] = 87; optima[6][1] = 24;
        optima[7][0] = 86; optima[7][1] = 50;
        optima[8][0] = 83; optima[8][1] = 74;
        optima[9][0] = 81; optima[9][1] = 124;

        ParetoArchive overallPA = new ParetoArchive();

        DstBoard board = new DstBoard();
        board.readBoard();
        bounds = (new DstState(board)).getValueBounds();
        
        for(int i = 0; i < optimaHV.length; ++i)
        {
            //optimaHV[i] = getHVValue(optima[i][0],optima[i][1]);
            optimaHV[i] = optima[i][0] *optima[i][1];
            overallPA.add(optima[i]);
        }

        optimalHValue = overallPA.computeHV2(bounds);

    }

    private static double getHVValue(double a, double b)
    {
        double val1 = Utils.normalise(a, bounds[0][0], bounds[0][1]);
        double val2 = Utils.normalise(b, bounds[1][0], bounds[1][1]);
        return val1 * val2;
    }
    
    public static int checkOptima(double[] res)
    {
        for(int i =0; i < optima.length; ++i)
        {
            if((optima[i][0] == (int)res[0]) && (optima[i][1] == (int)res[1]))
                return i;
        }
        return -1;
    }



    public static int checkOptima(double[] res, int which)
    {
        for(int i =0; i < optima.length; ++i)
        {
            if(optima[i][which] == (int)res[which])
                return i;
        }
        return -1;
    }

    public static void showOptima()
    {
        //System.out.println("Optima found:");
        if(optimaCount != null) for(int i =0; i < optima.length; ++i)
        {
            System.out.print(" " + optimaCount[i] + " ");
        }
        if(optima != null) for(int i =0; i < optima.length; ++i)
        {
            System.out.print(" " + optimaHits[i] + " ");
        }
        if(optimaDiscoveries != null) for(int i =0; i < optimaDiscoveries.length; ++i)
        {
            System.out.print(" " + optimaDiscoveries[i] + " ");
        }
        //System.out.println();
    }

    public static void testWeights(int a_kValue)
    {
        double initTargetWeight0 = 0.0;
        double targetInc = 0.01;
        double lastTarget = 1.0;

        int numGamesPerTarget = 100;

        for(double t = initTargetWeight0; t <= lastTarget; t+=targetInc)
        {
            optimaCount = new int[10];
            optimaHits = new int[10];
            StatSummary ss = new StatSummary();
            StatSummary archiveHV = new StatSummary();
            Player player = null;

            for(int i = 0; i < numGamesPerTarget; ++i)
            {
                DstBoard board = new DstBoard();
                board.readBoard();
                player = new MCTSPlayer(1000, new WeightedTreePolicy(a_kValue,t), new Random());
                DeepSeaTreasure dst = new DeepSeaTreasure(player, board);
                double[] result = dst.runGame();

                if(SHOW_GAME_RES) System.out.println(t + ", " + result[0] + ", " + result[1]);
                int which = checkOptima(result);
                if(which != -1)
                    optimaCount[which]++;

                which = checkOptima(result,1);
                if(which != -1)
                    optimaHits[which]++;

                ss.add(result[0] * result[1]);
                archiveHV.add(player.getHV(false));

                if(SHOW_TRAJECTORY){ for(int m=0; m < dst.m_gameMoves.length; ++m)
                    System.out.print(dst.m_gameMoves[m]);
                System.out.println();}
            }

            System.out.format("%.2f", t);
            showOptima();
            System.out.format(" %.2f %.2f %.2f %.2f %.2f %.2f\n", ss.mean(), ss.sd(), ss.stdErr(), archiveHV.mean(),archiveHV.sd(), archiveHV.stdErr());
        }
    }

    public static void testWeightsQL(int trainingIterations)
    {
        double initTargetWeight0 = 0.00;
        double targetInc = 0.01;
        double lastTarget = 1.0;

        int numGamesPerTarget = 100;

        for(double t = initTargetWeight0; t <= lastTarget; t+=targetInc)
        {
            optimaCount = new int[10];
            optimaHits = new int[10];
            StatSummary ss = new StatSummary();
            StatSummary archiveHV = new StatSummary();
            Player player = null;

            for(int i = 0; i < numGamesPerTarget; ++i)
            {
                //System.out.println("Game: " + i);
                DstBoard board = new DstBoard();
                board.readBoard();
                player = new QLPlayer(trainingIterations, new Random(), t);
                DeepSeaTreasure dst = new DeepSeaTreasure(player, board);
                double[] result = dst.runGame();

                if(SHOW_GAME_RES) System.out.println(t + ", " + result[0] + ", " + result[1]);
                int which = checkOptima(result);
                if(which != -1)
                    optimaCount[which]++;

                which = checkOptima(result,1);
                if(which != -1)
                    optimaHits[which]++;

                ss.add(result[0] * result[1]);
                archiveHV.add(player.getHV(false));

                if(SHOW_TRAJECTORY){ for(int m=0; m < dst.m_gameMoves.length; ++m)
                    System.out.print(dst.m_gameMoves[m]);
                    System.out.println();}
            }

            System.out.format("%.2f", t);
            showOptima();
            System.out.format(" %.2f %.2f %.2f %.2f %.2f %.2f\n", ss.mean(), ss.sd(), ss.stdErr(), archiveHV.mean(),archiveHV.sd(), archiveHV.stdErr());
        }
    }

    public static void testMOEAPlayerWeights(int maxNumEvaluations, String algorithm)
    {
        double initTargetWeight0 = 0.00;
        double targetInc = 0.01;
        double lastTarget = 1.0;

        int numGamesPerTarget = 100;

        for(double t = initTargetWeight0; t <= lastTarget; t+=targetInc)
        {
            optimaCount = new int[10];
            optimaHits = new int[10];
            optimaDiscoveries = new int[10];
            StatSummary ss = new StatSummary();
            StatSummary archiveHV = new StatSummary();
            Player player = null;

            for(int i = 0; i < numGamesPerTarget; ++i)
            {
                //System.out.print(".");
                DstBoard board = new DstBoard();
                board.readBoard();
                player = new MOEAPlayer(maxNumEvaluations,algorithm,t);
                DeepSeaTreasure dst = new DeepSeaTreasure(player, board);
                double[] result = dst.runGame();

                if(SHOW_GAME_RES) System.out.println(t + ", " + result[0] + ", " + result[1]);
                int which = checkOptima(result);
                if(which != -1)
                    optimaCount[which]++;

                which = checkOptima(result,1);
                if(which != -1)
                    optimaHits[which]++;

                ParetoArchive pa = DST.m_pa;
                for(int j = 0; j < pa.m_members.size(); ++j)
                {
                    double[] m = pa.m_members.get(j);
                    int whichDiscovered = checkOptima(m);
                    if(whichDiscovered != -1)
                        optimaDiscoveries[whichDiscovered]++;

                }

                ss.add(result[0] * result[1]);
                archiveHV.add(player.getHV(false));

                if(SHOW_TRAJECTORY){ for(int m=0; m < dst.m_gameMoves.length; ++m)
                    System.out.print(dst.m_gameMoves[m]);
                    System.out.println();}
            }

            System.out.format("%.2f", t);
            showOptima();
            System.out.format(" %.2f %.2f %.2f %.2f %.2f %.2f\n", ss.mean(), ss.sd(), ss.stdErr(), archiveHV.mean(),archiveHV.sd(), archiveHV.stdErr());
        }
    }


    public static void testParetoMCTSPlayerWeights(int maxNumEvaluations, double kValue)
    {
        double initTargetWeight0 = 0.00;
        double targetInc = 0.01;
        double lastTarget = 1.0;

        int numGamesPerTarget = 100;

        for(double t = initTargetWeight0; t <= lastTarget; t+=targetInc)
        {
            optimaCount = new int[10];
            optimaHits = new int[10];
            optimaDiscoveries = new int[10];
            StatSummary ss = new StatSummary();
            StatSummary archiveHV = new StatSummary();
            Player player = null;

            for(int i = 0; i < numGamesPerTarget; ++i)
            {
                //System.out.print(".");
                DstBoard board = new DstBoard();
                board.readBoard();
                player = new ParetoMCTSPlayer(maxNumEvaluations, new ParetoTreePolicy(kValue), new Random(), t);
                DeepSeaTreasure dst = new DeepSeaTreasure(player, board);
                double[] result = dst.runGame();

                if(SHOW_GAME_RES) System.out.println(t + ", " + result[0] + ", " + result[1]);
                int which = checkOptima(result);
                if(which != -1)
                    optimaCount[which]++;

                which = checkOptima(result,1);
                if(which != -1)
                    optimaHits[which]++;
                
                ParetoArchive pa = ((ParetoMCTSPlayer)player).globalPA;
                for(int j = 0; j < pa.m_members.size(); ++j)
                {
                    double[] m = pa.m_members.get(j);
                    int whichDiscovered = checkOptima(m);
                    if(whichDiscovered != -1)
                        optimaDiscoveries[whichDiscovered]++;

                }

                ss.add(result[0] * result[1]);
                archiveHV.add(((ParetoMCTSPlayer)player).globalPA.computeHV2());

                if(SHOW_TRAJECTORY){ for(int m=0; m < dst.m_gameMoves.length; ++m)
                    System.out.print(dst.m_gameMoves[m]);
                    System.out.println();}
            }

            System.out.format("%.2f", t);
            showOptima();
            System.out.format(" %.2f %.2f %.2f %.2f %.2f %.2f\n", ss.mean(), ss.sd(), ss.stdErr(), archiveHV.mean(),archiveHV.sd(), archiveHV.stdErr());
        }
    }

    public static void testTransParetoMCTSPlayerWeights(int maxNumEvaluations, double kValue)
    {
        double initTargetWeight0 = 0.00;
        double targetInc = 0.01;
        double lastTarget = 1.0;

        int numGamesPerTarget = 100;

        for(double t = initTargetWeight0; t <= lastTarget; t+=targetInc)
        {
            optimaCount = new int[10];
            optimaHits = new int[10];
            optimaDiscoveries = new int[10];
            StatSummary ss = new StatSummary();
            StatSummary archiveHV = new StatSummary();
            Player player = null;

            for(int i = 0; i < numGamesPerTarget; ++i)
            {
                //System.out.print(".");
                DstBoard board = new DstBoard();
                board.readBoard();
                player = new TransParetoMCTSPlayer(maxNumEvaluations, new ParetoTreePolicy(kValue), new Random(), t);
                DeepSeaTreasure dst = new DeepSeaTreasure(player, board);
                double[] result = dst.runGame();

                if(SHOW_GAME_RES) System.out.println(t + ", " + result[0] + ", " + result[1]);
                int which = checkOptima(result);
                if(which != -1)
                    optimaCount[which]++;

                which = checkOptima(result,1);
                if(which != -1)
                    optimaHits[which]++;

                ParetoArchive pa = ((TransParetoMCTSPlayer)player).globalPA;
                for(int j = 0; j < pa.m_members.size(); ++j)
                {
                    double[] m = pa.m_members.get(j);
                    int whichDiscovered = checkOptima(m);
                    if(whichDiscovered != -1)
                        optimaDiscoveries[whichDiscovered]++;

                }

                ss.add(result[0] * result[1]);
                archiveHV.add(((TransParetoMCTSPlayer)player).globalPA.computeHV2());

                if(SHOW_TRAJECTORY){ for(int m=0; m < dst.m_gameMoves.length; ++m)
                    System.out.print(dst.m_gameMoves[m]);
                    System.out.println();}
            }

            System.out.format("%.2f", t);
            showOptima();
            System.out.format(" %.2f %.2f %.2f %.2f %.2f %.2f\n", ss.mean(), ss.sd(), ss.stdErr(), archiveHV.mean(),archiveHV.sd(), archiveHV.stdErr());
        }
    }

    public static void testSimpleHV(double a_kValue)
    {
        int numGames = 100;
        StatSummary ss = new StatSummary();
        StatSummary archiveHV = new StatSummary();
        Player player = null;

        optimaCount = new int[10];
        optimaHits = new int[10];

        for(int i = 0; i < numGames; ++i)
        {
            DstBoard board = new DstBoard();
            board.readBoard();
            player = new MCTSPlayer(1000, new SimpleHVTreePolicy(a_kValue), new Random());
            DeepSeaTreasure dst = new DeepSeaTreasure(player, board);
            double[] result = dst.runGame();


            int which = checkOptima(result);
            if(which != -1)
                optimaCount[which]++;

            which = checkOptima(result,1);
            if(which != -1)
                optimaHits[which]++;

            ss.add(result[0] * result[1]);
            archiveHV.add(player.getHV(false));

            if(SHOW_GAME_RES)
            {
                System.out.print(i + ", " + result[0] + ", " + result[1] + ": ");
                System.out.format("%.3f",getHVValue(result[0], result[1]));
                if(which != -1) System.out.println("*");
                else System.out.println();
            }

            if(SHOW_TRAJECTORY){ for(int m=0; m < dst.m_gameMoves.length; ++m)
                System.out.print(dst.m_gameMoves[m]);
                System.out.println();}

        }
        showOptima();
        System.out.format(" %.2f %.2f %.2f %.2f %.2f %.2f\n", ss.mean(), ss.sd(), ss.stdErr(), archiveHV.mean(),archiveHV.sd(), archiveHV.stdErr());
    }

    public static void testSimpleHV_k()
    {
        double initK = 0.0;
        double kInc = 0.01;
        double lastK = 10.0;
        int numGames = 100;
        double hv = 0.0;
        StatSummary ss = new StatSummary();
        StatSummary archiveHV = new StatSummary();
        Player player = null;

        for(double t = initK; t <= lastK; t+=kInc)
        {
            optimaCount = new int[10];
            optimaHits = new int[10];

            for(int i = 0; i < numGames; ++i)
            {
                DstBoard board = new DstBoard();
                board.readBoard();
                player = new MCTSPlayer(1000, new SimpleHVTreePolicy(t), new Random());
                DeepSeaTreasure dst = new DeepSeaTreasure(player, board);
                double[] result = dst.runGame();

                int which = checkOptima(result);
                if(which != -1)
                    optimaCount[which]++;

                which = checkOptima(result,1);
                if(which != -1)
                    optimaHits[which]++;

                double thisHV = getHVValue(result[0], result[1]);
                ss.add(thisHV);
                archiveHV.add(player.getHV(false));

                if(SHOW_GAME_RES)
                {
                    System.out.print(i + ", " + result[0] + ", " + result[1] + ": ");
                    System.out.format("%.3f",thisHV);
                    if(which != -1) System.out.println("*");
                    else System.out.println();
                }

                if(SHOW_TRAJECTORY){ for(int m=0; m < dst.m_gameMoves.length; ++m)
                    System.out.print(dst.m_gameMoves[m]);
                    System.out.println();}

            }
            System.out.format("%.2f", t);
            showOptima();
            System.out.format(" %.2f %.2f %.2f %.2f %.2f %.2f\n", ss.mean(), ss.sd(), ss.stdErr(), archiveHV.mean(),archiveHV.sd(), archiveHV.stdErr());
        }
    }

    public static void testPlayer(Player a_player, int a_runs)
    {
        optimaCount = new int[10];
        optimaHits = new int[10];
        StatSummary ss = new StatSummary();
        StatSummary archiveHV = new StatSummary();

        for(int i = 0; i < a_runs; ++i)
        {
            a_player.reset();

            DstBoard board = new DstBoard();
            board.readBoard();
            DeepSeaTreasure dst = new DeepSeaTreasure(a_player, board);
            double[] result = dst.runGame();

            if(SHOW_GAME_RES) System.out.println(result[0] + ", " + result[1]);
            int which = checkOptima(result);
            if(which != -1)
            {
                //System.out.println("Optima! " + which);
                optimaCount[which]++;
            }

            which = checkOptima(result,1);
            if(which != -1)
                optimaHits[which]++;

            ss.add(result[0] * result[1]);
            archiveHV.add(a_player.getHV(false));

            if(SHOW_TRAJECTORY){ for(int m=0; m < dst.m_gameMoves.length; ++m)
                System.out.print(dst.m_gameMoves[m]);
                System.out.println();}

        }

        showOptima();
        System.out.format(" %.2f %.2f %.2f %.2f %.2f %.2f\n", ss.mean(), ss.sd(), ss.stdErr(), archiveHV.mean(),archiveHV.sd(), archiveHV.stdErr());
    }

    public static void main(String args[])
    {
        loadOptima();

        System.out.print("# Optima:");
        for(int i =0; i < optima.length; ++i)
        {
            System.out.print(" [" + optima[i][0] + "," + optima[i][1] + "]:");
            System.out.print(optimaHV[i] + ",");
            System.out.format("%.2f", getHVValue(optima[i][0],optima[i][1]));

        }
        System.out.println();

        System.out.println("# K O1 O2 O3 O4 O5 O6 O7 O8 O9 O10 H1 H2 H3 H4 H5 H6 H7 H8 H9 H10" +
                " HV-Mean HV-SD HV-StdErr HV-ARC-Mean HV-ARC-SD HV-ARC-StdErr");


        //testWeighted(0.8);
        //testSimpleHV(0.8);
        //testSimpleHV_k();
        //testPlayer(new MOEAPlayer(1000,"NSGAII", 0.5), 100);
        //testPlayer(new MOEAPlayer(100,"SMSEMOA"), 100);
        //testPlayer(new QLPlayer(100, new Random(), 0.999), 100);
        //testPlayer(new MCTSPlayer(500, new WeightedTreePolicy(0.8, 0.5), new Random()), 100);
        //testPlayer(new ParetoMCTSPlayer(1000, new ParetoTreePolicy(0.8), new Random(), 0.5), 100);
        //testPlayer(new TransParetoMCTSPlayer(10000, new ParetoTreePolicy(Math.sqrt(2)), new Random(), 0.5), 100);
        //testMOEAPlayerWeights(1250,"NSGAII");
        //testMOEAPlayerWeights(1000,"SMSEMOA");
        //testParetoMCTSPlayerWeights(1000, 0.35);
        testTransParetoMCTSPlayerWeights(4500, Math.sqrt(2));

        //testWeightsQL(500);

    }


}
