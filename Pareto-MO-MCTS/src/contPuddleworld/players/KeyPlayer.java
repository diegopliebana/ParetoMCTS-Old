package contPuddleworld.players;

import contPuddleworld.game.GameCPW;
import spgame.Player;
import spgame.State;

/**
 * This class is used for the KeyController (human playing).
 * PTSP-Competition
 * Created by Diego Perez, University of Essex.
 * Date: 20/12/11
 */
public class KeyPlayer implements Player {
    /**
     * To manage the keyboard input.
     */
    private KeyInput m_input;

    /**
     * Constructor of the KeyController.
     */
    public KeyPlayer()
    {
        m_input = new KeyInput();
    }

    /**
     * This function is called every execution step to get the action to execute.
     * @param a_GameCPWCopy Copy of the current GameCPW state.
     * @param a_timeDue The time the next move is due
     * @return the integer identifier of the action to execute (see interface framework.core.Controller for definitions)
     */
    public int getAction(GameCPW a_GameCPWCopy, long a_timeDue)
    {
        return m_input.getAction();
    }

    /**
     * Return the input manager
     * @return the input manager
     */
    public KeyInput getInput() {return m_input;}

    @Override
    public int getMove(State a_GameCPWState) {
        return this.getAction(null,0);  //To change body of implemented methods use File | Settings | File Templates.
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
