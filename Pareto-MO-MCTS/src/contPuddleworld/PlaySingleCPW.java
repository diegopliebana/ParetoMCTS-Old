package contPuddleworld;

import contPuddleworld.game.Controller;
import contPuddleworld.game.GameCPW;
import contPuddleworld.players.KeyPlayer;
import contPuddleworld.players.MOEAPlayer;
import contPuddleworld.players.ParetoMCTSPlayer;
import contPuddleworld.players.RandomPlayer;
import dst.players.NullPlayer;
import dst.players.TransParetoMCTSPlayer;
import dst.policies.RandomTreePolicy;
import dst.policies.TransParetoTreePolicy;
import spgame.Player;
import spgame.policies.ParetoTreePolicy;
import spgame.policies.SimpleHVTreePolicy;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class PlaySingleCPW
{
    public static void main(String args[])
    {
        GameCPW cpw = new GameCPW("CPWmap/cpw3.map", true, new Random().nextInt());
        boolean visuals = true;
        double delay = Controller.BOT_DELAY;

        //Player player = new MCTSPlayer(1000, new WeightedTreePolicy(0.8,0));
        //Player player = new ParetoMCTSPlayer(10000, new ParetoTreePolicy(0.8), new Random(), 0.5);
        // Player player = new MOEAPlayer(10000, "NSGAII", 0.5);   //10K: ~350ms per move
        //Player player = new MOEAPlayer(1000, "SMSEMOA", 0.01);
        //Player player = new QLPlayer(100, new Random(), 0.5);
        //Player player = new TransParetoMCTSPlayer(20000, new TransParetoTreePolicy(Math.sqrt(2)), new Random(), 0.5);  //20K: ~300ms per move
        //Player player = new TransParetoMCTSPlayer(20000, new RandomTreePolicy(), new Random(), 0.5);

        //Player player = new KeyPlayer();    delay = Controller.DELAY; //Human play delay: 16
        //Player player = new RandomPlayer();
        //Player player = new ParetoMCTSPlayer(900,new ParetoTreePolicy(0.8),new Random(), 0);
        Player player = new ParetoMCTSPlayer(900,new SimpleHVTreePolicy(0.8),new Random(), 0);
        //Player player = new MOEAPlayer(700, "NSGAII", 1);   //10K: ~350ms per move

        cpw.setPlayer(player);
        double[] result = cpw.run(visuals, delay);

        System.out.print("Game result: " + result[0] + "," + result[1] + "; ");
        System.out.println("T: " + (1000-result[0]) + ", F: " + (1000-result[1]));

        //if(player instanceof MCTSPlayer)
        {
            double hv = player.getHV(false);
            System.out.println("HV: " + hv);
        }
    }
    
}

