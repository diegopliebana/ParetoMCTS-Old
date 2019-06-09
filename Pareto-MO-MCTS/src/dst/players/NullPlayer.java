package dst.players;

import spgame.Player;
import spgame.State;

/**
 * Created by Diego Perez, University of Essex.
 * Date: 31/03/13
 */
public class NullPlayer implements Player {

    @Override
    public int getMove(State a_gameState) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
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
