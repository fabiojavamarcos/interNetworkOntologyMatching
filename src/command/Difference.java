package command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import Ontology.BreadthFirstSearch;
import Ontology.ConstraintGraph;
import Ontology.Graph;
import Ontology.Node;
import Ontology.NodeClass;
import Ontology.NodeProperty;
import Ontology.NodeRestrictionCardinality;
import Ontology.NodeRestrictionComplementOfClass;
import Ontology.NodeRestrictionComplementOfProperty;
import Ontology.NodeRestrictionComplementOfRestrictionCardinality;
import Ontology.NodeType;
import Ontology.UsefulOWL;

public class Difference extends Command {

	@Override
	public void execute(String operation) {
		
	/**
	 * Runs difference in another thread
	 * 
	 */
	
		//enableButtons(false);
		//root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		//String str = Log + "\nRunning Difference over " + pathOnt1 + " and " + pathOnt2;
		//Log+=str;
		//root.update(root.getGraphics());

		//Thread worker = new Thread() {
			//public void run() {
				//String str;
				try{
					//Time Stamp
					String path = System.getProperty("user.home") + "/LastRunProcedureTime.txt";
			    	File logFile = new File(path);
			    	BufferedWriter writer = null;
			    	java.util.Date date;
			    	date = new java.util.Date();
					String stamp;
					stamp = "Running Difference: \n";
					stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
					System.out.println(stamp);
					writer = new BufferedWriter(new FileWriter(logFile));
					
					//str = Log.getText();
					/**
					 * 1st I will get all the reachable from the 1st and the 2nd Ontologies
					 * 
					 * then I will drop from the 1st every adj that is equal in both ontologies(also all Cardinality 
					 * restriction nodes that are equal)
					 * 
					 * by the end, if a node is without adj I will drop it too
					 */
					BreadthFirstSearch bfs1 = new BreadthFirstSearch(gOnt1);
					BreadthFirstSearch bfs2 = new BreadthFirstSearch(gOnt2);
					bfs1.fullBFS();
					bfs2.fullBFS();
					ArrayList<LinkedList<Integer>> TransitiveClosure1 = bfs1.getReachable();
					ArrayList<LinkedList<Integer>> TransitiveClosure2 = bfs2.getReachable();
					HashMap<Integer,Node> vertices1 = gOnt1.getVertices();
					HashMap<Integer,Node> vertices2 = gOnt2.getVertices();
					
					//DefaultTableModel modelInc = clearResultsTable();
					gResults = new Graph();
					//Equivalent nodes from gOnt1 and gOnt2
					ArrayList<Integer> nodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt2 = new ArrayList<Integer>();
					//Equivalent nodes from gOnt1 and gOnt3
					ArrayList<Integer> finalNodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> finalNodesOnt3 = new ArrayList<Integer>();
					
					for(Map.Entry<Integer, Node> temp : vertices1.entrySet())
					{
			        	Node n1 = temp.getValue();
			        	int nodeKey1 = n1.getId();
			        	NodeType type = n1.getNodeType();
			        	int nodeKey2 = Graph.findNodeKeyDifference(IRIOnt1, n1, IRIOnt2, gOnt2);
			        	if(nodeKey2 == -1)
			        	{
			        		//System.out.print("Não encontrados: \n");
	        				//System.out.print("* "+ nodeKey1+"\n");
		        			int tempID = -1;
		        			switch(type)
			            	{
							   case Class :
							       NodeClass nc = (NodeClass)n1;
							       tempID = gResults.insertNode(nc.getExpression(), NodeType.Class);							      
							       break;
							   case Property :
							       NodeProperty np = (NodeProperty)n1;
							       tempID = gResults.insertNodeProperty(np.getDescIRI(),np.getDescProp());
							       break;
							   case RestrictionCardinality :
								   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
							       tempID = gResults.insertNodeRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
							       break;
							   case RestrictionComplementOfClass :
								   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
							       tempID = gResults.insertNodeRestrictionComplementOfClass(notClass.getExpClass().toString());
							       break;
							   case RestrictionComplementOfProperty :
								   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
							       tempID = gResults.insertNodeRestrictionComplementOfProperty(notProp.getDescProp(), notProp.getDescIRI());
							       break;
							   case RestrictionComplementOfRestrictionCardinality :
								   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
							       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinality(notRestCard.getExpression());
							       break;
							   default: 
								   break;
						    }
		        			if(tempID != -1)
		        			{
		        				finalNodesOnt1.add(nodeKey1);
		        				finalNodesOnt3.add(tempID);
		        			}
		        			else
		        			{
				        		System.out.print("Erro ao incerir: \n");
		        				System.out.print("* "+ nodeKey1+"\n");
		        			}
			        	}
			        	else
			        	{
			        		nodesOnt1.add(nodeKey1);
			        		nodesOnt2.add(nodeKey2);
			        	}
			        }
			        ArrayList<LinkedList<Integer>> CheckedTransitiveClosure = new ArrayList<LinkedList<Integer>>();
			        LinkedList<Integer> tempChecked;
			        for(LinkedList<Integer> tempC1 : TransitiveClosure1)
			        {
			        	tempChecked = new LinkedList<Integer>();
			        	int element = tempC1.get(0);
			        	if(nodesOnt1.contains(element))
			        	{
			        		tempChecked.add(element);
			        		int index = nodesOnt1.indexOf(element);
			        		int element2 = nodesOnt2.get(index);
			        		LinkedList<Integer> tempC2 = UsefulOWL.getListwithFirstElement(TransitiveClosure2, element2);
			        		for(int i : tempC1)
			        		{
			        			if(!nodesOnt1.contains(i))
			        			{
			        				tempChecked.add(i);
			        			}
			        			else
			        			{
			        				index = nodesOnt1.indexOf(i);
					        		element2 = nodesOnt2.get(index);
					        		if(!tempC2.contains(element2))
					        		{
					        			tempChecked.add(i);
					        		}
			        			}
			        		}
			        		if((tempChecked != null)&&(!tempChecked.isEmpty())&&(tempChecked.size()>1))
				        	{
				        		CheckedTransitiveClosure.add(tempChecked);
				        	}
			        	}
			        	else
			        	{
			        		for(int i : tempC1)
			        		{
			        			tempChecked.add(i);
			        		}
			        		if((tempChecked != null)&&(!tempChecked.isEmpty()))
				        	{
				        		CheckedTransitiveClosure.add(tempChecked);
				        	}
			        	}			        	
			        }
			        
			        //Now I have the closure with all the edges that must be added to the result
			        for(LinkedList<Integer> tempC1 : CheckedTransitiveClosure)
			        {
			        	int element = tempC1.get(0);
			        	if(!finalNodesOnt1.contains(element))
			        	{
			        		//1st I insert the node in gResults if it isn't already there
			        		Node n1 = vertices1.get(element);
			        		int nodeKeyR = Graph.findNodeKey(IRIOnt1, n1, "", gResults);
			        		if(nodeKeyR == -1)
				        	{
			        			NodeType type = n1.getNodeType();			        			
			        			int tempID = -1;
			        			switch(type)
				            	{
								   case Class :
								       NodeClass nc = (NodeClass)n1;
								       tempID = gResults.insertNode(nc.getExpression(), NodeType.Class);							      
								       break;
								   case Property :
								       NodeProperty np = (NodeProperty)n1;
								       tempID = gResults.insertNodeProperty(np.getDescIRI(),np.getDescProp());
								       break;
								   case RestrictionCardinality :
									   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
								       tempID = gResults.insertNodeRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
								       break;
								   case RestrictionComplementOfClass :
									   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfClass(notClass.getExpClass().toString());
								       break;
								   case RestrictionComplementOfProperty :
									   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfProperty(notProp.getDescProp(), notProp.getDescIRI());
								       break;
								   case RestrictionComplementOfRestrictionCardinality :
									   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinality(notRestCard.getExpression());
								       break;
								   default: 
									   break;
							    }
			        			if(tempID != -1)
			        			{
			        				finalNodesOnt1.add(n1.getId());
			        				finalNodesOnt3.add(tempID);
			        			}
				        	}
			        	}
			        	//If I already inserted the node in the resulting graph
			        	if(finalNodesOnt1.contains(element))
			        	{
			        		int index = finalNodesOnt1.indexOf(element);
			        		int idResult = finalNodesOnt3.get(index);
			        		ArrayList<LinkedList<Integer>> ResultAdj = gResults.getAdj();
			        		LinkedList<Integer> tempResultAdj = UsefulOWL.getListwithFirstElement(ResultAdj, idResult);
			        		//I get its adj list and add all nodes from CheckedTransitiveClosure
			        		for(int i = 1; i<tempC1.size(); i++)
			        		{
			        			element = tempC1.get(i);
			        			if(finalNodesOnt1.contains(element))
			        			{
			        				//if the node already exists in gResults I add only the edge
			        				index = finalNodesOnt1.indexOf(element);
			        				idResult = finalNodesOnt3.get(index);
			        				if(!tempResultAdj.contains(idResult))
			        				{
			        					tempResultAdj.add(idResult);
			        				}
			        			}
			        			else
			        			{
			        				//I must add the node and then add the edge
			        				Node n1 = vertices1.get(element).getNode();
			        				NodeType type = n1.getNodeType();
			        				int tempID = -1;
				        			switch(type)
					            	{
									   case Class :
									       NodeClass nc = (NodeClass)n1;
									       tempID = gResults.insertNode(nc.getExpression(), NodeType.Class);							      
									       break;
									   case Property :
									       NodeProperty np = (NodeProperty)n1;
									       tempID = gResults.insertNodeProperty(np.getDescIRI(),np.getDescProp());
									       break;
									   case RestrictionCardinality :
										   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
									       tempID = gResults.insertNodeRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
									       break;
									   case RestrictionComplementOfClass :
										   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
									       tempID = gResults.insertNodeRestrictionComplementOfClass(notClass.getExpClass().toString());
									       break;
									   case RestrictionComplementOfProperty :
										   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
									       tempID = gResults.insertNodeRestrictionComplementOfProperty(notProp.getDescProp(), notProp.getDescIRI());
									       break;
									   case RestrictionComplementOfRestrictionCardinality :
										   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
									       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinality(notRestCard.getExpression());
									       break;
									   default: 
										   break;
								    }
				        			if(tempID != -1)
				        			{
				        				finalNodesOnt1.add(n1.getId());
				        				finalNodesOnt3.add(tempID);
				        				tempResultAdj.add(tempID);
				        			}
			        			}
			        		}
			        	}
			        }
					
			        ConstraintGraph rgd = new ConstraintGraph();
					gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
			        gResults = rgd.searchBottomNodes(gResults);
			        if (debug.equals("Y")) {

				        System.out.print("\n => End of Difference: \n");
				        ConstraintGraph.Graph2Console(gResults);
			        }
			        
					//fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					//str = Log + "\n" + "Difference Done!";
	    			//Log+=str;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			System.out.println("Minimize Graphs... after Difference: "+ pathOnt1 + " and " + pathOnt2);
	    			Command com = new MinimizeGraph(); 
	    			commands.get("MinimizeGraph");
	    			com.execute("Difference");
	    			//minimizeGraphBatch("Difference");
	    			// store to use later without open files ou process normalization and graphs again
	    			Object [] OntAUX = new Object [7];
	    		    /*		obj[0] = ontology;
	    			obj[1] = factory;
	    			obj[2] = manager;
	    		    obj[3] = ontPath;
	    		    obj[4] = ontName;
	    		    obj[5] = graph;
	    		    obj[6] = IRI;
	    		*/
	    			OntAUX[0] = null;
	    			OntAUX[1] = null;
	    			OntAUX[2] = null;
	    			OntAUX[3] = null;
	    			OntAUX[4] = nameOnt1+"D"+nameOnt2;
	    			OntAUX[5] = gResults;
	    			OntAUX[6] = null;
	    			 
	    			ontologiesProcessed.add(OntAUX);  
	    			
	    			//Time stamping
	    			date= new java.util.Date();
	    			stamp = stamp + "Difference done: \n";
	    			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
	    			System.out.println(stamp);
	    			writer.write(stamp);
	    	        writer.close();
				}catch(Exception ex)
				{
					System.out.println(ex.getStackTrace());
					//str = Log + "\n" + ex.getMessage();
	    			//Log+=str;
	    			gResults = null;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			//}
		//};
		//worker.start();	
	}

}
