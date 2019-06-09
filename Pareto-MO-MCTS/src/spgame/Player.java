package spgame;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
public interface Player {
    int getMove(State a_gameState);
    double getHV(boolean a_normalized);
    void reset();
}
