package command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import Ontology.ConstraintGraph;
import Ontology.Graph;
import Ontology.Node;
import Ontology.NodeRestrictionCardinality;
import Ontology.NodeRestrictionComplementOfRestrictionCardinality;
import Ontology.NodeType;

public class MinimizeGraphBatch extends Command {

	@Override
	public void execute(String operation) {
		// TODO Auto-generated method stub
		if(gResults != null)
		{
			String str = Log;
			try{
    			CleanUp = true;
    			
    			//Thread worker = new Thread() {
    				//@SuppressWarnings({ "rawtypes", "unchecked" })
					//public void run() {
    					//Log += "\nMinimizing Resulting Graph";
    					try{
    						//Evaluate cardinality restrictions according to the Procedure realized
    						HashMap<Integer,Node> vertices = gResults.getVertices();
    						ArrayList<LinkedList<Integer>> adj = gResults.getAdj();
    						
    						LinkedList<Integer> Nodes = new LinkedList<Integer>();
    						LinkedList<Integer> MatchedNodes = new LinkedList<Integer>();
    						
    						Set s = vertices.entrySet();
    				        Iterator it = (Iterator) s.iterator();
    				        
    				        while(it.hasNext()) 
    				        {
    				            Map.Entry<Integer, Node> temp = (Map.Entry<Integer, Node>) it.next();
    				            Node n = temp.getValue();
    				            NodeType type = n.getNodeType();
    				            int id = n.getId();
    				    		if((type ==  NodeType.RestrictionCardinality)||(type ==  NodeType.RestrictionComplementOfRestrictionCardinality))
    				    		{
    				    			if(!Nodes.contains(id)&&!MatchedNodes.contains(id))
    				    			{
    				    				int n2fid = Graph.findDifferentNodeRCKey(n,gResults);
    				    				if(n2fid != -1)
    				    				{
    				    					Node n2 = vertices.get(n2fid);
    				    					if((n.isNodeBottom() == n2.isNodeBottom())
    				    							&&(n.isNodeTop() == n.isNodeTop()))
    				    					{
	    				    					Nodes.add(id);
	    				    					MatchedNodes.add(n2fid);
	    				    					
	    				    					if(type ==  NodeType.RestrictionCardinality)
	    				    					{
	    				    						NodeRestrictionCardinality nrc;  
    				    						    NodeRestrictionCardinality nrc2; 
    				    						   // String tempstr = (String)(jComboBoxOperation.getSelectedItem());
    				    			    			if(operation.equals("Intersection"))
    				    			    			{
    				    			    				if(((NodeRestrictionCardinality)n).getCard() > ((NodeRestrictionCardinality)n2).getCard())
	    				    						    {
	    				    						    	nrc = (NodeRestrictionCardinality)n;
	    				    						    	nrc2 = (NodeRestrictionCardinality)n2;
	    				    						    }
	    				    						    else
	    				    						    {
	    				    						    	nrc = (NodeRestrictionCardinality)n2;
	    				    						    	nrc2 = (NodeRestrictionCardinality)n;
	    				    						    }
    				    			    			}
    				    			    			else
    				    			    			{
	    				    						    if(((NodeRestrictionCardinality)n).getCard() < ((NodeRestrictionCardinality)n2).getCard())
	    				    						    {
	    				    						    	nrc = (NodeRestrictionCardinality)n;
	    				    						    	nrc2 = (NodeRestrictionCardinality)n2;
	    				    						    }
	    				    						    else
	    				    						    {
	    				    						    	nrc = (NodeRestrictionCardinality)n2;
	    				    						    	nrc2 = (NodeRestrictionCardinality)n;
	    				    						    }
    				    			    			}
    				    						    
	    				    						int idfinal = nrc.getId();
	    				    						int idreplace = nrc2.getId();
    				    						    LinkedList<Integer> tempC1 = adj.get(idfinal);
    				    						    LinkedList<Integer> tempC2 = adj.get(idreplace);
    				    						    boolean replace = true;
    				    						    
    				    						    if(tempC2.size() >= 2)
    				    				        	{
    				    				        		for(int i = 1; i < tempC2.size(); i++)
    				    				        		{
    				    				        			int element = tempC2.get(i);
    				    				        			if(!tempC1.contains(element))
    				    				        			{
    				    				        				replace = false;
    				    				        			}
    				    				        		}
    				    				        	}
    				    						    if(replace)
    				    						    {
	    				    						    for(LinkedList<Integer> tempAdj : adj)
	    					    				        {
	    				    						    	if(tempAdj.contains(idreplace))
	    				    						    	{
	    				    						    		int index = tempAdj.indexOf(idreplace);
	    				    						    		if(index > 0)
	    				    						    		{
	    				    						    			if(!tempAdj.contains(idfinal))
	    				    						    			{
	    				    						    				replace = false;
	    				    						    			}
	    				    						    		}
	    				    						    	}
	    					    				        }
    				    						    }
    				    						    if(replace)
    				    						    {
    				    						    	nrc2.setRemove(true);
    				    						    	while(tempC2.size() >= 2)
				    						    		{
				    						    			tempC2.removeLast();
				    						    		}
				    						    		for(LinkedList<Integer> tempAdj : adj)
	    					    				        {
	    				    						    	if(tempAdj.contains(idreplace))
	    				    						    	{
	    				    						    		int index = tempAdj.indexOf(idreplace);
	    				    						    		if(index > 0)
	    				    						    		{
	    				    						    			tempAdj.remove(index);
	    				    						    		}
	    				    						    	}
	    					    				        }
    				    						    }
	    				    					}
	    				    					else
	    				    					{
	    				    						NodeRestrictionComplementOfRestrictionCardinality nrc;
	    				    						NodeRestrictionComplementOfRestrictionCardinality nrc2;
	    				    						//String tempstr = (String)(jComboBoxOperation.getSelectedItem());
    				    			    			if(operation.equals("Intersection"))
    				    			    			{
    				    			    				if(((NodeRestrictionCardinality)n).getCard() < ((NodeRestrictionCardinality)n2).getCard())
	    				    						    {
	    				    						    	nrc = (NodeRestrictionComplementOfRestrictionCardinality)n;
	    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n2;
	    				    						    }
	    				    						    else
	    				    						    {
	    				    						    	nrc = (NodeRestrictionComplementOfRestrictionCardinality)n2;
	    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n;
	    				    						    }
    				    			    			}
    				    			    			else
    				    			    			{
		    				    						if(((NodeRestrictionComplementOfRestrictionCardinality)n).getCard() > ((NodeRestrictionComplementOfRestrictionCardinality)n2).getCard())
		    				    						{
		    				    							nrc = (NodeRestrictionComplementOfRestrictionCardinality)n;
	    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n2;
	    				    						    }
	    				    						    else
	    				    						    {
	    				    						    	nrc = (NodeRestrictionComplementOfRestrictionCardinality)n2;
	    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n;
		    				    						}
    				    			    			}
	    				    						
	    				    						int idfinal = nrc.getId();
	    				    						int idreplace = nrc2.getId();
    				    						    LinkedList<Integer> tempC1 = adj.get(idfinal);
    				    						    LinkedList<Integer> tempC2 = adj.get(idreplace);
    				    						    boolean replace = true;
    				    						    
    				    						    if(tempC2.size() >= 2)
    				    				        	{
    				    				        		for(int i = 1; i < tempC2.size(); i++)
    				    				        		{
    				    				        			int element = tempC2.get(i);
    				    				        			if(!tempC1.contains(element))
    				    				        			{
    				    				        				replace = false;
    				    				        			}
    				    				        		}
    				    				        	}
    				    						    
    				    						    if(replace)
    				    						    {
	    				    						    for(LinkedList<Integer> tempAdj : adj)
	    					    				        {
	    				    						    	if(tempAdj.contains(idreplace))
	    				    						    	{
	    				    						    		int index = tempAdj.indexOf(idreplace);
	    				    						    		if(index > 0)
	    				    						    		{
	    				    						    			if(!tempAdj.contains(idfinal))
	    				    						    			{
	    				    						    				replace = false;
	    				    						    			}
	    				    						    		}
	    				    						    	}
	    					    				        }
    				    						    }
    				    						    if(replace)
    				    						    {
    				    						    	nrc2.setRemove(true);
    				    						    	while(tempC2.size() >= 2)
				    						    		{
				    						    			tempC2.removeLast();
				    						    		}
	    				    						    for(LinkedList<Integer> tempAdj : adj)
	    					    				        {
	    				    						    	if(tempAdj.contains(idreplace))
	    				    						    	{
	    				    						    		int index = tempAdj.indexOf(idreplace);
	    				    						    		if(index > 0)
	    				    						    		{
	    				    						    			tempAdj.remove(index);
	    				    						    		}
	    				    						    	}
	    					    				        }
    				    						    }
	    				    					}
	    				    				}
    				    				}
    				    			}
    				    		}	    				            
    				        }
    						//Copy Adjacent List
    				        LinkedList<Integer> tempChecked;
    				        ArrayList<LinkedList<Integer>> adjChecked = new ArrayList<LinkedList<Integer>>();
    				        for(LinkedList<Integer> tempC1 : adj)
    				        {
    				        	tempChecked = new LinkedList<Integer>();
    				        	//Collections.copy(tempChecked,tempC1);
    				        	
    				        	for(int i = 0; i < tempC1.size(); i++)
    				        	{
    				        		int Element = tempC1.get(i);
    				        		tempChecked.add(Element);
    				        	}
    				        	
    				        	adjChecked.add(tempChecked);
    				        }
    				        
    				        //finds MEG deleting unnecessary edges
    				        for(LinkedList<Integer> tempC1 : adj)
    				        {
    				        	int OriginalElement = tempC1.get(0);
    				        	if(tempC1.size() >= 2)
    				        	{
    				        		for(int i = 1; i < tempC1.size(); i++)
    				        		{
    				        			int Element = tempC1.get(i);
    				        			LinkedList<Integer> tempC2 = adjChecked.get(Element);
    				        			for(int j = 1; j < tempC2.size(); j++)
	    				        		{
    				        				int TestReacheble = tempC2.get(j);
    				        				int TestIndex = tempC1.indexOf(TestReacheble);
    				        				if(TestIndex > 0)
    				        				{
    				        					tempC1.remove(TestIndex);
    				        				}
    				        				/*Cycle
    				        				else if(TestIndex == 0)
    				        				{
    				        				}
    				        				*/
	    				        		}
    				        		}
    				        	}
    				        }
    				        gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
    				        if (debug.equals("Y")) {
    				        	System.out.println("Add edges");
    				        	ConstraintGraph.Graph2Console(gResults);
    				        }
    				        
   				        
    		    			//str = str + "\nMinimization Done!";
    		    			//Log+=str;
    				        
    					}catch(Exception ex)
    					{
    						System.out.println(ex.getStackTrace());
    						//str = Log + "\n" + ex.getMessage();
    		    			//Log+=str;
    		    			return;
    					}
    				//}
    			//};
    			//worker.start();
			}
			catch(Exception ex)
			{
				System.out.println(ex.getStackTrace());
				//str = Log + "\nError : " + ex.getMessage();
    			//Log +=str;
    			return;
			}
		}
    //}
}


}
