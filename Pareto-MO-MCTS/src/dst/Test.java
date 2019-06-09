package dst;

import dst.game.DeepSeaTreasure;
import dst.players.TransParetoMCTSPlayer;
import dst.policies.TransParetoTreePolicy;
import spgame.Player;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 13/02/13
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class Test 
{
    public static void main(String args[])
    {
        int N = 100;
        int[] iterations = new int[]{100,8500,9000};

        for (int iteration : iterations)
        {
            System.out.print(iteration + " ");
            for(int i = 0; i < N; ++i)
            {
                PlayDST.loadOptima();
                //CpwBoard board = new CpwBoard();
                //board.readBoard();

                //Player player = new MCTSPlayer(1000, new WeightedTreePolicy(0.8,0));
                //Player player = new ParetoMCTSPlayer(10000, new ParetoTreePolicy(0.8), new Random(), 0.5);
                //Player player = new MOEAPlayer(iteration, "NSGAII", 0.5);   //10K: ~350ms per move
                //Player player = new MOEAPlayer(1000, "SMSEMOA", 0.01);
                //Player player = new QLPlayer(100, new Random(), 0.5);
                Player player = new TransParetoMCTSPlayer(iteration, new TransParetoTreePolicy(Math.sqrt(2)), new Random(), 0.5);  //20K: ~300ms per move

                //DeepSeaTreasure dst = new DeepSeaTreasure(player, board);
                //dst.runGame();
            }
            System.out.println();
        }






    }
}
