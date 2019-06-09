package contPuddleworld.players;

import contPuddleworld.game.Controller;
import contPuddleworld.game.GameCPW;
import contPuddleworld.game.MacroAction;
import dst.game.DstState;
import spgame.*;
import utils.ParetoArchive;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 12/02/13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class ParetoMCTSPlayer implements Player {

    int m_nSims = 100; // number of simulations per move
    TreePolicy m_treePolicy;
    ParetoTreeNode tn;
    State curState;
    Random r;
    double target0, target1;
    int lastAction = -1;
    public ParetoArchive globalPA;

    //Macroactions
    private int m_currentMacroAction;                       //Current action in the macro action being executed.
    private MacroAction m_lastAction;                       //Last macro action to be executed.

    public ParetoMCTSPlayer(int a_nSims, TreePolicy treePolicy, Random r, double target0)
    {
        m_nSims = a_nSims;
        m_treePolicy = treePolicy;
        this.r = r;
        this.target0 = target0;
        this.target1 = 1 - target0;
        globalPA = new ParetoArchive();
        m_currentMacroAction = 0;
        m_lastAction = new MacroAction(false,0, Controller.MACRO_ACTION_LENGTH);
    }
    
    @Override
    public int getMove(State a_gameState)
    {
        if(m_currentMacroAction != 0)
        {
            m_currentMacroAction--;
            //System.out.print(m_lastAction.buildAction());
            return m_lastAction.buildAction();
        }
        //System.out.println("-");

        int numSimsMacro = m_nSims*Controller.MACRO_ACTION_LENGTH;

        curState = a_gameState;
        Roller randomRoller = new RandomRoller(RandomRoller.RANDOM_ROLLOUT, this.r);
        tn = new ParetoTreeNode(curState.copy(),randomRoller,m_treePolicy);

        tn.mctsSearch(numSimsMacro);

        /*if(tn.state.isTerminal())
        {
            lastAction = tn.bestActionIndex();
        }else{
            lastAction = tn.bestActionIndex(target0, target1);
        }  */

        lastAction = 0;
        if(!tn.state.isTerminal())
        {
            lastAction = tn.bestActionIndex(target0, target1);
        }

        /*int mo = tn.bestActionIndex(target0, target1);
        int reg = tn.bestActionIndex();
        lastAction = mo;

        if(mo != reg)
        {
            System.out.println("Mismatch! mo: " + mo + ", reg: " + reg);
            mo = tn.bestActionIndex(target0, target1);
            reg = tn.bestActionIndex();
        }     */

        //System.out.println("## CHOSEN: " + lastAction + " ##");
        //mo = tn.bestActionIndex(target0, target1);
        //reg = tn.bestActionIndex();

        for(int i = 0; i < tn.pa.m_members.size(); ++i)
        {
            globalPA.add(tn.pa.m_members.get(i));
        }


        m_currentMacroAction = Controller.MACRO_ACTION_LENGTH - 1;
        m_lastAction = new MacroAction(lastAction,Controller.MACRO_ACTION_LENGTH);
        return m_lastAction.buildAction();

        //return (Integer)((GameCPW)(curState)).m_moves.get(lastAction);
    }
    
    public double getHV(boolean a_normalized)
    {
        return tn.getHV(a_normalized);
    }

    public void reset(){}
}
