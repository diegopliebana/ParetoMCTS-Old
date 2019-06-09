package spgame;

/**
 * Created by Simon M. Lucas
 * sml@essex.ac.uk
 * Date: 11-Dec-2010
 * Time: 03:36:15
 */
public interface State {
    boolean isTerminal();
    double[] value();
    double[][] getValueBounds();
    int nActions();
    public void next(int a_action);
    public State copy();  
    public int getNumTargets();
}
