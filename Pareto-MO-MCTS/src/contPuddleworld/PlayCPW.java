package contPuddleworld;

import contPuddleworld.game.Controller;
import contPuddleworld.game.GameCPW;
import contPuddleworld.players.*;
import contPuddleworld.players.MOEAPlayer;
import dst.players.*;
import dst.policies.TransParetoTreePolicy;
import spgame.Player;
import spgame.TreePolicy;
import spgame.policies.ParetoTreePolicy;
import spgame.policies.SimpleHVTreePolicy;
import utils.StatSummary;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class PlayCPW
{
    public static boolean SHOW_GAME_RES = true;

    public static void testParetoMCTSPlayerWeights(TreePolicy tp, int maxNumEvaluations, double kValue, int numMaps, int numRunsPerMap, int[] seeds)
    {
        double initTargetWeight0 = 0.00;
        double targetInc = 0.1;
        double lastTarget = 1.0;

        int numGamesPerTarget = 100;

        for(double t = initTargetWeight0; t <= lastTarget; t+=targetInc)
        {
            StatSummary ss = new StatSummary();
            StatSummary ssTime = new StatSummary();
            StatSummary ssFuel = new StatSummary();
            StatSummary archiveHV = new StatSummary();
            Player player = null;

            for(int i = 0; i < numMaps; ++i)
            {
                //System.out.print(".");
                int seed = seeds[i];

                for(int g = 0; g < numRunsPerMap; ++g)
                {
                    GameCPW cpw = new GameCPW("CPWmap/cpw.map", true, seed);
                    player = new contPuddleworld.players.ParetoMCTSPlayer(maxNumEvaluations,tp,new Random(), t);

                    cpw.setPlayer(player);
                    double[] result = cpw.run(false, 0);
                    //double[] result = cpw.run(true, Controller.BOT_DELAY);

                    if(SHOW_GAME_RES) System.out.println(t + ", " + result[0] + ", " + result[1]);

                    ssTime.add(result[0]);
                    ssFuel.add(result[1]);
                    ss.add(result[0] * result[1]);
                    archiveHV.add(player.getHV(false));
                }
            }

            System.out.format("%.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f \n", t,
                    ssTime.mean(), ssTime.sd(), ssTime.stdErr(), ssFuel.mean(), ssFuel.sd(), ssFuel.stdErr(),
                    ss.mean(), ss.sd(), ss.stdErr(), archiveHV.mean(),archiveHV.sd(), archiveHV.stdErr());
        }
    }


    public static void testParetoNSGAPlayerWeights(int maxNumEvaluations, double kValue, int numMaps, int numRunsPerMap, int[] seeds)
    {
        double initTargetWeight0 = 0.00;
        double targetInc = 0.1;
        double lastTarget = 1.0;

        int numGamesPerTarget = 100;

        for(double t = initTargetWeight0; t <= lastTarget; t+=targetInc)
        {
            StatSummary ss = new StatSummary();
            StatSummary ssTime = new StatSummary();
            StatSummary ssFuel = new StatSummary();
            StatSummary archiveHV = new StatSummary();
            Player player = null;

            for(int i = 0; i < numMaps; ++i)
            {
                //System.out.print(".");
                int seed = seeds[i];

                for(int g = 0; g < numRunsPerMap; ++g)
                {
                    GameCPW cpw = new GameCPW("CPWmap/cpw.map", true, seed);
                    player = new MOEAPlayer(maxNumEvaluations, "NSGAII", t);

                    cpw.setPlayer(player);
                    double[] result = cpw.run(false, 0);
                    //double[] result = cpw.run(true, Controller.BOT_DELAY);

                    if(SHOW_GAME_RES) System.out.println(t + ", " + result[0] + ", " + result[1]);

                    ssTime.add(result[0]);
                    ssFuel.add(result[1]);
                    ss.add(result[0] * result[1]);
                    archiveHV.add(player.getHV(false));
                }
            }

            System.out.format("%.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f \n", t,
                    ssTime.mean(), ssTime.sd(), ssTime.stdErr(), ssFuel.mean(), ssFuel.sd(), ssFuel.stdErr(),
                    ss.mean(), ss.sd(), ss.stdErr(), archiveHV.mean(),archiveHV.sd(), archiveHV.stdErr());
        }
    }

    public static void main(String args[])
    {
        SHOW_GAME_RES = false;
        int NUM_MAPS = 10;
        int NUM_RUNS_PER_MAP = 10;
                                                                                                                                               /*
        Random seedGen = new Random();
        int[] seeds = new int[NUM_MAPS];
        System.out.print("Seeds: ");
        for(int i = 0; i < NUM_MAPS; ++i)
        {
            seeds[i] = seedGen.nextInt();
            System.out.print(seeds[i] + ",");
        }
        System.out.println();
                                                                                                                                                 */
        int seeds[] = new int[]{545218856,130642247,1534309653,-299823858,-1192050070,1566290211,-1022625627,-240363214,-1331862616,305571408};

        System.out.println("# K HV-Mean HV-SD HV-StdErr HV-ARC-Mean HV-ARC-SD HV-ARC-StdErr");

        //testParetoMCTSPlayerWeights(new ParetoTreePolicy(Math.sqrt(2)), 900, Math.sqrt(2), NUM_MAPS, NUM_RUNS_PER_MAP,seeds);
        testParetoMCTSPlayerWeights(new SimpleHVTreePolicy(Math.sqrt(2)), 900, Math.sqrt(2), NUM_MAPS, NUM_RUNS_PER_MAP,seeds);
        //testParetoNSGAPlayerWeights(700, -1, NUM_MAPS, NUM_RUNS_PER_MAP,seeds);
    }


}
