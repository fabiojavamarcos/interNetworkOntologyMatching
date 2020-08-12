package command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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

public class Intersection extends Command {

	@Override
	public void execute(String operation) {
		
	/**
	 * Runs Intersection in another thread
	 * 
	 */
	
		//enableButtons(false);
		//root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Log += "\nRunning Intersection over " + pathOnt1 + " and " + pathOnt2;
		
		//root.update(root.getGraphics());

		//Thread worker = new Thread() {
			//@SuppressWarnings({ "rawtypes", "unchecked" })
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
					stamp = "Running Intersection: \n";
					stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
					System.out.println(stamp);
					writer = new BufferedWriter(new FileWriter(logFile));
					
					
					//run intersection
					//1st we pick the vertices that are in both Ontologies
					HashMap<Integer,Node> vertices1 = gOnt1.getVertices();
					HashMap<Integer,Node> vertices2 = gOnt2.getVertices();
					//Clear Results Table
					//DefaultTableModel modelInc = clearResultsTable();
					gResults = new Graph();
					ArrayList<Integer> nodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt2 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt3 = new ArrayList<Integer>();
					Set cont = vertices1.entrySet();
		            Iterator it = (Iterator) cont.iterator();
		            int tempCard1;
		            int tempCard2;
			        while(it.hasNext())
			        {
			        	Map.Entry<Integer, Node> temp = (Map.Entry<Integer, Node>)it.next();
			        	Node n1 = temp.getValue();
			        	//OWLObject o1 = n1.getExpression();
			        	int nodeKey2 = Graph.findNodeKey(IRIOnt1, n1, IRIOnt2, gOnt2); //gOnt2.getNodeKey(o1);
			        	if(nodeKey2 != -1)
			        	{
			        		Node n2 = vertices2.get(nodeKey2);
			        		String s = "\n n1 : " + n1.getExpression().toString();
			        		s= s + "  -  n2 : "+n2.getExpression().toString();
			        		//System.out.print(s);
			        		if(n2.getNodeType().toString().equals(n1.getNodeType().toString()))
			        		{
			        			nodesOnt1.add(n1.getId());
			        			nodesOnt2.add(nodeKey2);
			        			NodeType type = n2.getNodeType();
			        			int tempID = -1;
			        			switch(type)
				            	{
								   case Class :
								       NodeClass nc = (NodeClass)n1;
								       tempID = gResults.insertNodeComp(nc.getExpression(), NodeType.Class);							      
								       break;
								   case Property :
								       NodeProperty np = (NodeProperty)n1;
								       tempID = gResults.insertNodePropertyComp(np.getDescIRI(),np.getDescProp());
								       break;
								   case RestrictionCardinality :
									   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
									   NodeRestrictionCardinality nrc2 = (NodeRestrictionCardinality)n2;
									   tempCard1 = nrc.getCard();
									   tempCard2 = nrc2.getCard();
									   if(tempCard1 < tempCard2)
									   {
										   tempCard1 = tempCard2;
									   }
								       tempID = gResults.insertNodeRestrictionCardinalityComp(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),tempCard1);								       
								       break;
								   case RestrictionComplementOfClass :
									   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfClassComp(notClass.getExpClass().toString());
								       break;
								   case RestrictionComplementOfProperty :
									   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfPropertyComp(notProp.getDescProp(), notProp.getDescIRI());
								       break;
								   case RestrictionComplementOfRestrictionCardinality :
									   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
									   NodeRestrictionComplementOfRestrictionCardinality notRestCard2 = (NodeRestrictionComplementOfRestrictionCardinality)n2;
									   tempCard1 = UsefulOWL.returnCard(notRestCard.getExpression());
									   tempCard2 = UsefulOWL.returnCard(notRestCard2.getExpression());
									   if(tempCard1 > tempCard2)
									   {
										   tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard2.getExpression());
									   }
									   else
									   {
										   tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard.getExpression());
									   }
								       break;
								   default: 
									   break;
							    }
			        			nodesOnt3.add(tempID);
			        		}
			        	}
			        }
			        if (debug.equals("Y")) {
				        System.out.print("\n => Nodes included: \n");
				        ConstraintGraph.Graph2Console(gResults);
			        }
					//now we get a new Transitive Closure for the final Graph with only the nodes that will be included
			        //Graph 1
			        BreadthFirstSearch bfs1 = new BreadthFirstSearch(gOnt1);
					ArrayList<LinkedList<Integer>> originalTransitiveClosure1 = bfs1.getReachable();
					//ArrayList<LinkedList<Integer>> originalTransitiveClosure1 = gOnt1.getAdj();
					ArrayList<LinkedList<Integer>> newTransitiveClosure1 = new ArrayList<LinkedList<Integer>>();
					//Then we create a new closure without the non-checked nodes and the ones that references them
					for(LinkedList<Integer> tempRO : originalTransitiveClosure1)
					{
						if(nodesOnt1.contains(tempRO.get(0)))
						{
							LinkedList<Integer> tempR = new LinkedList<Integer>();
							for(Integer i : tempRO)
							{
								if(nodesOnt1.contains(i))
								{
									tempR.add(i);
								}
							}
							newTransitiveClosure1.add(tempR);
						}
					}
					//Graph 2
					BreadthFirstSearch bfs2 = new BreadthFirstSearch(gOnt2);
					ArrayList<LinkedList<Integer>> originalTransitiveClosure2 = bfs2.getReachable();
					//ArrayList<LinkedList<Integer>> originalTransitiveClosure2 = gOnt2.getAdj();
					ArrayList<LinkedList<Integer>> newTransitiveClosure2 = new ArrayList<LinkedList<Integer>>();
					for(LinkedList<Integer> tempRO : originalTransitiveClosure2)
					{
						if(nodesOnt2.contains(tempRO.get(0)))
						{
							LinkedList<Integer> tempR = new LinkedList<Integer>();
							for(Integer i : tempRO)
							{
								if(nodesOnt2.contains(i))
								{
									tempR.add(i);
								}
							}
							newTransitiveClosure2.add(tempR);
						}
					}
					//and create the transitive closure of the Resulting graph using it
					//Graph3
					ArrayList<LinkedList<Integer>> newTransitiveClosure3 = new ArrayList<LinkedList<Integer>>();
					for(int i = 0; i < nodesOnt1.size(); i++)
					{
						int element1 = nodesOnt1.get(i);
						LinkedList<Integer> tempRO1 = UsefulOWL.getListwithFirstElement(newTransitiveClosure1, element1);
						int element2 = nodesOnt2.get(i);
						LinkedList<Integer> tempRO2 = UsefulOWL.getListwithFirstElement(newTransitiveClosure2, element2);
						LinkedList<Integer> tempRO3 = new LinkedList<Integer>();
						tempRO3.add(nodesOnt3.get(i));
						
						if((tempRO2 != null) &&(tempRO1 != null))
						{
							for(int j = 1; j < tempRO1.size(); j++)
							{
								element1 = tempRO1.get(j);
								int index = nodesOnt1.indexOf(element1);
								if(index > -1)
								{
									element2 = nodesOnt2.get(index);
									if(tempRO2.contains(element2))
									{
										tempRO3.add(nodesOnt3.get(index));
									}
								}
							}
						}
						newTransitiveClosure3.add(tempRO3);
					}
					gResults.setAdj(newTransitiveClosure3);
					ConstraintGraph rgd = new ConstraintGraph();
					gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
			        gResults = rgd.searchBottomNodes(gResults);
			        if (debug.equals("Y")) {

			        	System.out.print("\n => End of intersection: \n");
			        	ConstraintGraph.Graph2Console(gResults);
			        }
			        
					//fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					//str = Log + "\n" + "Intersection done!";
	    			//Log+=str;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			System.out.println("Minimize Graphs... after Intersection: "+ pathOnt1 + " and " + pathOnt2);
	    			Command com = new MinimizeGraph(); 
	    			commands.get("MinimizeGraph");
	    			com.execute("Intersection");
	    			//minimizeGraphBatch("Intersection");
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
	    			OntAUX[4] = nameOnt1+"I"+nameOnt2;
	    			OntAUX[5] = gResults;
	    			OntAUX[6] = null;
	    			 
	    			ontologiesProcessed.add(OntAUX);  
	    			
	    			//Time stamping
	    			date= new java.util.Date();
	    			stamp = stamp + "Intersection done: \n";
	    			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
	    			System.out.println(stamp);
	    			writer.write(stamp);
	    	        writer.close();
				}catch(Exception ex)
				{
					ex.printStackTrace();
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
