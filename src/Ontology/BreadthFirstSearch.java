package Ontology;

import javax.swing.JTextArea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Queue;

import Ontology.Graph;
import Ontology.Node;
import Ontology.Graph.Edge;


/**
 * This Class implements the Breadth First Search algorithm over the Graph Class to discover if there are paths between certain nodes
 *
 * @author Rômulo de Carvalho Magalhães - adapted by Fabio Marcos de Abreu Santos
 *
 */
@SuppressWarnings("unused")
public class BreadthFirstSearch {

	private Graph g;
	private int numVertices;
	private int calculated[];
	private ArrayList<LinkedList<Integer>> reachable;
	private Boolean calculatedFull;
	
	public BreadthFirstSearch() 
	{
		this.g = null;
		this.numVertices = 0;
	}
	
	/**
	 * Initializes class with a given Graph
	 * 
	 * @param original
	 */
	public BreadthFirstSearch(Graph original) 
	{
		this.calculatedFull = false;
		this.g = original;
		this.numVertices = original.getNumVertices();
		this.calculated = new int[this.numVertices];
		this.reachable = new ArrayList<LinkedList<Integer>>();
		for(int i = 0; i < this.numVertices; i++)
		{
			LinkedList<Integer> temp = new LinkedList<Integer>();
			temp.add(i);
			this.reachable.add(temp);
			this.calculated[i] = 0;
		}
	}
	
	/**
	 * Searches a path from idO to idD
	 * 
	 * @param idO
	 * @param idD
	 * @return
	 */
	public boolean existsPath(int idO, int idD)
	{
		if(idO==idD)
		{
			return true;
		}
		if(this.calculated[idO]==0)
		{//calculate bfs
			bfs(idO);
			this.calculated[idO] = 1;
		}
		//search the visited nodes for answer
		LinkedList<Integer> temp = this.reachable.get(idO);
		for(Integer i: temp)
		{
			if(i==idD)
			{
				return true;
			}
		}		
		return false;
	}
	
	/**
	 * Do the Breadth First Search for a given root node and save all the nodes reachable from that 1st one
	 * 
	 * @param root
	 */
	public void bfs(int root)
	{
		ArrayList<Integer> visited = new ArrayList<Integer>(Collections.nCopies(this.numVertices, 0));
	    Queue<Integer> queue = new LinkedList<Integer>();
	    queue.add(root);
	    visited.set(root, 1);
	    ArrayList<LinkedList<Integer>> tempAdj = this.g.getAdj();
	    while(!queue.isEmpty())
	    {
	        int node = (int)queue.remove();
	        if(!this.g.isAdjEmpty(node))
	        {
	        	LinkedList<Integer> tempChilds = tempAdj.get(node);
	        	if(tempChilds != null && tempChilds.size() > 0)
	        	{
	        		Iterator<Integer> iterator = tempChilds.listIterator(1);
	        		while(iterator.hasNext())
	        		{
	        			int child = iterator.next();
                        if (visited.get(child)==0) 
                        {
                        	visited.set(child,1);
                        	queue.add(child);
                        	LinkedList<Integer> tempR = this.reachable.get(root);
                        	tempR.add(child);
                        }
	        		}
	        	}
	        }
	    }
	}
	
	/**
	 * Calculates all the reachable nodes from all the nodes for the graph in memory
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void fullBFS()
	{
		Set cont = g.getVertices().entrySet();
        Iterator i = (Iterator) cont.iterator();
        while(i.hasNext()) 
        {
        	Map.Entry<Integer, Node> n = (Map.Entry<Integer, Node>) i.next();
        	int nID = n.getValue().getId();
        	bfs(nID);
			this.calculated[nID] = 1;
        }
        this.calculatedFull = true;
	}
	
	/**
	 * If the full BFS has been calculated returns the transitive closure of graph g,
	 * otherwise calculates the Full BFS and then returns the transitive closure
	 * @return
	 */
	public ArrayList<LinkedList<Integer>> getReachable()
	{
		if(this.calculatedFull)
		{
			return this.reachable;
		}
		fullBFS();
		return this.reachable;
	}
	
	/**
	 * May be used to force full BFS recalculation when getting the transitive closure of g
	 */
	public void resetCalculatedFull()
	{
		this.calculatedFull = false;
	}
	
	public Boolean getCalculatedFull()
	{
		return this.calculatedFull;
	}

}
