package dst.players;

import spgame.TransParetoTreeNode;

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 04/03/13
 * Time: 12:41
 * To change this template use File | Settings | File Templates.
 */
public class TranspositionTable         //Singleton
{
    private static TranspositionTable m_instance;

    public TreeMap<Integer, TransParetoTreeNodeList> m_table;

    private TranspositionTable()
    {
        m_table = new TreeMap<Integer, TransParetoTreeNodeList>();
    }

    public static TranspositionTable GetInstance()
    {
        if(m_instance == null)
        {
            m_instance = new TranspositionTable();
        }
        return m_instance;
    }

    public void reset()
    {
        m_table = new TreeMap<Integer, TransParetoTreeNodeList>();
    }

    public LinkedList<TransParetoTreeNode> getNodesList(int a_row, int a_column, int a_numMoves)
    {
        int hashKey = getHashKey(a_row, a_column, a_numMoves);
        return  m_table.get(hashKey);
    }

    public void addNodeToList(int a_row, int a_column, int a_numMoves, TransParetoTreeNode a_node)
    {
        int hashKey = getHashKey(a_row, a_column, a_numMoves);
        if(m_table.get(hashKey) == null)
            m_table.put(hashKey, new TransParetoTreeNodeList());

        m_table.get(hashKey).add(a_node);
    }

    public TransParetoTreeNode getRepresentative(int a_row, int a_column, int a_numMoves)
    {
        int hashKey = getHashKey(a_row, a_column, a_numMoves);
        TransParetoTreeNodeList list = m_table.get(hashKey);
        if(list == null || list.size() == 0)
            return null;
        else
            return list.get(0);
    }

    
    public double[] getDataInPosition (int a_row, int a_column, int a_numMoves)
    {
        int hashKey = getHashKey(a_row, a_column, a_numMoves);
        TransParetoTreeNodeList list = m_table.get(hashKey);

        if(list == null || list.size() == 0)
            return new double[]{0.0, 0.0};
        else
            return new double[]{list.get(0).Q(),list.get(0).nVisits};

    }
    
    private int getHashKey(int a_row, int a_column, int a_numMoves)
    {
        //return 100*a_row + a_column;
        return 10000*a_numMoves + 100*a_row + a_column;
    }

}

class TransParetoTreeNodeList extends LinkedList<TransParetoTreeNode> {}