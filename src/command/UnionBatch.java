package command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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

public class UnionBatch extends Command {

	@Override
	public void execute(String operation) {
		// TODO Auto-generated method stub
		/**
		 * Batch implementation
		 * 
		 * @author Fabio Marcos de Abreu Santos
		 *
		 */
		Log+= "\nRunning Union Batch over " + pathOnt1 + " and " + pathOnt2;
		//System.out.println(Log);

		//Thread worker = new Thread() {
			//public void run() {
				String str;
				try{
					//Time Stamp
					String path = System.getProperty("user.home") + "/LastRunProcedureTime.txt";
			    	File logFile = new File(path);
			    	BufferedWriter writer = null;
			    	java.util.Date date;
			    	date = new java.util.Date();
					String stamp;
					stamp = "Running Union: \n";
					stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
					System.out.println(stamp);
					writer = new BufferedWriter(new FileWriter(logFile));
					
					str = Log;
					//1st we pick the vertices that are in both Ontologies
					HashMap<Integer,Node> vertices1 = gOnt1.getVertices();
					HashMap<Integer,Node> vertices2 = gOnt2.getVertices();
					//Clear Results Table
					//DefaultTableModel modelInc = clearResultsTable();
					gResults = new Graph();
					ArrayList<Integer> nodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt2 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt3 = new ArrayList<Integer>();
					//1st we add all the nodes from ontology 1
					for(Map.Entry<Integer, Node> temp : vertices1.entrySet())
					{
			        	Node n1 = temp.getValue();
			        	NodeType type = n1.getNodeType();
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
						       tempID = gResults.insertNodeRestrictionCardinalityComp(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
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
						       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard.getExpression());
						       break;
						   default: 
							   break;
					    }
	        			if(tempID != -1)
	        			{
	        				nodesOnt1.add(n1.getId());
	        				nodesOnt3.add(tempID);
	        			}	        			
			        }
			        if (debug.equals("Y")) {

			        	ConstraintGraph.Graph2Console(gResults);
			        }
			        //now we set the adjacency list as an equivalent to the ontology 1
			        ArrayList<LinkedList<Integer>> AdjList1 = gOnt1.getAdj();
			        ArrayList<LinkedList<Integer>> AdjList3 = new ArrayList<LinkedList<Integer>>();
					for(int i = 0; i < nodesOnt1.size(); i++)
					{
						int element1 = nodesOnt1.get(i);
						LinkedList<Integer> tempRO1 = UsefulOWL.getListwithFirstElement(AdjList1, element1);
						LinkedList<Integer> tempRO3 = new LinkedList<Integer>();
						tempRO3.add(nodesOnt3.get(i));
						if(tempRO1 != null)
						{
							for(int j = 1; j < tempRO1.size(); j++)
							{
								element1 = tempRO1.get(j);
								int index = nodesOnt1.indexOf(element1);
								if(index > -1)
								{
									int element3 = nodesOnt3.get(index);
									if(!tempRO3.contains(element3))
									{
										tempRO3.add(element3);
									}
								}
							}
						}
						AdjList3.add(tempRO3);
					}
					
					gResults.setAdj(AdjList3);
					//we now have a Graph that is a copy of Ontology 1 
					//we need to add all nodes from Ontology 2 and its Edges
			        if (debug.equals("Y")) {

				        System.out.print("\n => Original Graph: \n");
				        ConstraintGraph.Graph2Console(gOnt1);
				        System.out.print("\n => Nodes included: \n");
				        ConstraintGraph.Graph2Console(gResults);
			        }
			        //now we try to insert all nodes from Ontology 2 marking its equivalents in the lists
			        nodesOnt3 = new ArrayList<Integer>();
			        for(Map.Entry<Integer, Node> temp : vertices2.entrySet())
					{
			        	Node n2 = temp.getValue();
			        	NodeType type = n2.getNodeType();
	        			int tempID = -1;
	        			switch(type)
		            	{
						   case Class :
						       NodeClass nc = (NodeClass)n2;
						       tempID = gResults.getNodeKey(nc.getExpression());
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeComp(nc.getExpression(), NodeType.Class);
						       }
						       break;
						   case Property :
						       NodeProperty np = (NodeProperty)n2;
						       tempID = gResults.getNodeKeyProperty(np.getDescIRI(),np.getDescProp());
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodePropertyComp(np.getDescIRI(),np.getDescProp());
						       }
						       break;
						   case RestrictionCardinality : 
							   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n2;
							   tempID = gResults.getNodeKeyRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());
							   if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionCardinalityComp(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());
						       }								       
						       break;
						   case RestrictionComplementOfClass :
							   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n2;
							   tempID = gResults.getNodeKeyComplementOfClass(notClass.getExpClass().toString());
							   if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionComplementOfClassComp(notClass.getExpClass().toString());
						       }
							   break;
						   case RestrictionComplementOfProperty :
							   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n2;
						       tempID = gResults.getNodeKeyComplementOfProperty(notProp.getDescIRI(),notProp.getDescProp());
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionComplementOfPropertyComp(notProp.getDescProp(), notProp.getDescIRI());
						       }
						       break;
						   case RestrictionComplementOfRestrictionCardinality :
							   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n2;
						       String desc = notRestCard.getExpression();
						       desc = desc.replaceAll("#", "/");
						       String iriDomain = UsefulOWL.returnIRIDomain(desc);
						       String iriProp = UsefulOWL.returnIRIProp(desc);
						       String descProp = UsefulOWL.returnProp(desc);
						       String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
						       int card = UsefulOWL.returnCard(desc);
						       tempID = gResults.getNodeKeyComplementOfRestrictionCardinality(iriDomain,iriProp,descProp,descRangeOrDomain,card);
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard.getExpression());
						       }						   
						       break;
						   default: 
							   break;
					    }
	        			nodesOnt2.add(n2.getId());	
					    nodesOnt3.add(tempID);	    	    
			        }
			        //And we add to the adjacency list of the resulting Ontology all edges from Ontology 2
			        ArrayList<LinkedList<Integer>> AdjList2 = gOnt2.getAdj();
			        AdjList3 = gResults.getAdj();
					for(int i = 0; i < nodesOnt2.size(); i++)
					{
						int element2 = nodesOnt2.get(i);
						int element3 = nodesOnt3.get(i);
						LinkedList<Integer> tempRO2 = UsefulOWL.getListwithFirstElement(AdjList2, element2);
						LinkedList<Integer> tempRO3 = UsefulOWL.getListwithFirstElement(AdjList3, element3);
						if((tempRO2 != null) && (tempRO3 != null))
						{
							for(int j = 1; j < tempRO2.size(); j++)
							{
								element2 = tempRO2.get(j);
								int index = nodesOnt2.indexOf(element2);
								if(index > -1)
								{
									element3 = nodesOnt3.get(index);
									if(!tempRO3.contains(element3))
									{
										tempRO3.add(element3);
									}
								}
							}
						}
					}
			        //now we search for bottom and top nodes
					ConstraintGraph rgd = new ConstraintGraph();
					gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
			        gResults = rgd.searchBottomNodes(gResults);
			        if (debug.equals("Y")) {

				        System.out.print("\n => End of Union: \n");
				        ConstraintGraph.Graph2Console(gResults);
			        }
			        
					//fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					//str = Log + "\n" + "Union done!";
	    			//Log +=str;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			System.out.println("Minimize Graphs... after Union: "+ pathOnt1 + " and " + pathOnt2);
	    			Command com = (Command) commands.get("MinimizeGraph");
	    			
	    			com.execute("Union");
	    			//minimizeGraphBatch("Union");
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
	    			OntAUX[4] = nameOnt1+"U"+nameOnt2;
	    			OntAUX[5] = gResults;
	    			OntAUX[6] = null;
	    			 
	    			ontologiesProcessed.add(OntAUX);  
	    			//Time stamping
	    			date= new java.util.Date();
	    			stamp = stamp + "Union done: \n";
	    			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
	    			System.out.println(stamp);
	    			writer.write(stamp);
	    	        writer.close();
				}catch(Exception ex)
				{
					Log+= "\n" + ex.getMessage();
					//System.out.println(Log);
	    			gResults = null;
	    			return;
				}
			//}
		//};
		//worker.start();
		}


}


