package contPuddleworld.players;

import spgame.Player;
import spgame.State;

import java.util.Random;

/**
 * Created by Diego Perez, University of Essex.
 * Date: 31/03/13
 */
public class RandomPlayer implements Player {

    Random m_rnd;

    public RandomPlayer()
    {
        m_rnd  = new Random();
    }

    @Override
    public int getMove(State a_gameState) {
        return m_rnd.nextInt(a_gameState.nActions());
    }

    @Override
    public double getHV(boolean a_normalized) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
