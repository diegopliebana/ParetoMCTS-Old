package dst;

import dst.game.DeepSeaTreasure;
import dst.game.DstBoard;
import dst.players.TransParetoMCTSPlayer;
import dst.policies.TransParetoTreePolicy;
import spgame.Player;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class PlaySingleDST
{
    public static void main(String args[])
    {
        PlayDST.loadOptima();
        DstBoard board = new DstBoard();
        board.readBoard();

        //Player player = new MCTSPlayer(1000, new WeightedTreePolicy(0.8,0));
        //Player player = new ParetoMCTSPlayer(10000, new ParetoTreePolicy(0.8), new Random(), 0.5);
        // Player player = new MOEAPlayer(10000, "NSGAII", 0.5);   //10K: ~350ms per move
        //Player player = new MOEAPlayer(1000, "SMSEMOA", 0.01);
        //Player player = new QLPlayer(100, new Random(), 0.5);
        Player player = new TransParetoMCTSPlayer(20000, new TransParetoTreePolicy(Math.sqrt(2)), new Random(), 0.5);  //20K: ~300ms per move
        
        DeepSeaTreasure dst = new DeepSeaTreasure(player, board);
        double[] result = dst.runGame();

        System.out.println("Game result: " + result[0] + "," + result[1]);
        //if(player instanceof MCTSPlayer)
        {
            double hv = player.getHV(false);
            System.out.println("HV: " + hv);
        }
    }
    
}

