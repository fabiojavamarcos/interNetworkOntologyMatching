package Ontology;

import javax.swing.JTextArea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

//import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
//import org.semanticweb.owlapi.model.OWLOntologyCreationException;
//import org.semanticweb.owlapi.model.OWLOntologyManager;
//import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;





import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;






//import org.semanticweb.owlapi.model.PrefixManager;
import java.io.*;

import Ontology.Node;
import Ontology.Graph;
import Ontology.BreadthFirstSearch;


/**
 * Implements the creation of the Restriction Graph, 
 * transforming the OWLOntology in a structured Graph that will be used for the logical operations over Ontologies 
 *
 * @author Romulo de Carvalho Magalhaes - adapted by Fabio Marcos de Abreu Santos
 *
 */
public class ConstraintGraph {

	/**
	 * Creates a Graph from an OWLOntology
	 * 
	 * @param NormOnt
	 * @param Log - error log
	 * @return
	 * @throws Exception 
	 */
	public Graph createGraph(Object[] NormOnt, String Log, String debug) throws Exception
	{
		Graph g = new Graph();
        OWLOntology ontology = (OWLOntology) NormOnt[0];
		OWLDataFactory factory = (OWLDataFactory) NormOnt[1];
		//OWLOntologyManager manager = (OWLOntologyManager)NormOnt[2];
		String path = System.getProperty("user.home") + "/LastLoadedGraphTime.txt";
    	File logFile = new File(path);
    	BufferedWriter writer = null;
    	java.util.Date date;
		
		try{
			//Time stamping the load
			date = new java.util.Date();
			String stamp;
			stamp = "Loading Ontology: \n";
			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
			System.out.println(stamp);
			writer = new BufferedWriter(new FileWriter(logFile));
			
			//Stage 1 Start
			//Classes insertion
			for(OWLClass cls : ontology.getClassesInSignature()) 
			{
	            if(!cls.isOWLThing())
	            {
	            	g.insertNode(cls.toString(), NodeType.Class);
	            }
			}
			//Properties insertion
			for(OWLObjectProperty pro : ontology.getObjectPropertiesInSignature()) 
			{
	            g.insertNode(pro.toString(), NodeType.Property);
			}
			
			//SubClasses insertion
			for(OWLSubClassOfAxiom subClass : ontology.getAxioms(AxiomType.SUBCLASS_OF)) 
			{
				if((!subClass.getSubClass().isOWLThing())&&(!subClass.getSuperClass().isOWLThing()))
				{
					ClassExpressionType subClassType = subClass.getSubClass().getClassExpressionType();
					ClassExpressionType superClassType = subClass.getSuperClass().getClassExpressionType();
					OWLObject subClassObj = subClass.getSubClass();
					OWLObject superClassObj = subClass.getSuperClass();
					String subClassStr = subClassObj.toString();
					String superClassStr = superClassObj.toString();
					if(UsefulOWL.isObjectObjectMaxCardinality(superClassType))
					{
						String desc = superClassStr;
						desc = desc.replaceAll("#", "/");
				        String IRIDomain = UsefulOWL.returnIRIDomain(desc);
				        String IRIProp = UsefulOWL.returnIRIProp(desc);
				        String descProp = UsefulOWL.returnProp(desc);
				        String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
				        int card = UsefulOWL.returnCard(desc);
				        card++;
				        PrefixManager pmiRIDomain = new DefaultPrefixManager(IRIDomain);
				    	PrefixManager pmiRIProp = new DefaultPrefixManager(IRIProp);
				        OWLClassExpression domainOrRangeObjProp = factory.getOWLClass(descRangeOrDomain, pmiRIDomain);
				       	OWLObjectProperty propRestriction = factory.getOWLObjectProperty(descProp, pmiRIProp);
				       	OWLObjectMinCardinality restrictionMinCard = factory.getOWLObjectMinCardinality(card, propRestriction, domainOrRangeObjProp);
				       	OWLObjectComplementOf notRestrictionMinCard = factory.getOWLObjectComplementOf((OWLClassExpression)restrictionMinCard);
				       	superClassObj = notRestrictionMinCard;
				       	superClassStr = notRestrictionMinCard.toString();
					}
					if(UsefulOWL.isObjectObjectMaxCardinality(subClassType))
					{
						String desc = subClassStr;
						desc = desc.replaceAll("#", "/");
				        String IRIDomain = UsefulOWL.returnIRIDomain(desc);
				        String IRIProp = UsefulOWL.returnIRIProp(desc);
				        String descProp = UsefulOWL.returnProp(desc);
				        String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
				        int card = UsefulOWL.returnCard(desc);
				        card++;
				        PrefixManager pmiRIDomain = new DefaultPrefixManager(IRIDomain);
				    	PrefixManager pmiRIProp = new DefaultPrefixManager(IRIProp);
				        OWLClassExpression domainOrRangeObjProp = factory.getOWLClass(descRangeOrDomain, pmiRIDomain);
				       	OWLObjectProperty propRestriction = factory.getOWLObjectProperty(descProp, pmiRIProp);
				       	OWLObjectMinCardinality restrictionMinCard = factory.getOWLObjectMinCardinality(card, propRestriction, domainOrRangeObjProp);
				       	OWLObjectComplementOf notRestrictionMinCard = factory.getOWLObjectComplementOf((OWLClassExpression)restrictionMinCard);
				       	subClassObj = notRestrictionMinCard;
				       	subClassStr = notRestrictionMinCard.toString();
					}
					g = insertNodeSuperSubClassAxiom(superClassType,superClassObj,g);
					g = insertNodeSubSubClassAxiom(subClassType,subClassObj, g);
					g.insertEdge(subClassStr, superClassStr);
				}
			}
			//SubProperties insertion
			for(OWLSubObjectPropertyOfAxiom subProp : ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) 
			{
				OWLObjectPropertyExpression SubPropExp = subProp.getSubProperty();
				OWLObjectPropertyExpression SuperPropExp = subProp.getSuperProperty();
				g.insertNode(SuperPropExp.toString(), NodeType.Property);
				g.insertNode(SubPropExp.toString(), NodeType.Property);
				g.insertEdge(SubPropExp.toString(), SuperPropExp.toString());
			}
			//Stage 1 End
			if (debug.contentEquals("Y")) {
				System.out.println("Classes and Properties");
				Graph2Console(g);
			}
			//Stage 2 Start
			HashMap<Integer, Node> vertices = g.getVertices();
	        List<LinkedList<Integer>> adj = g.getAdj();
	        
			for(OWLSubObjectPropertyOfAxiom subProp : ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) 
			{
				OWLObjectPropertyExpression p = subProp.getSubProperty();
	            OWLObjectPropertyExpression q = subProp.getSuperProperty();
	            for(int j = 0; j < adj.size(); j++) 
				{
		            LinkedList<Integer> e = adj.get(j);
	            	if(e.size() > 0 &&(e !=null) )
	            	{
	            		if(vertices.get(e.get(0)).getNodeType().equals(NodeType.RestrictionCardinality)) 
	            		{
	            			NodeRestrictionCardinality nok = (NodeRestrictionCardinality) g.getVertices().get(e.get(0));
	            			if((p.getDomains(ontology) != null) &&(q.getDomains(ontology) != null) && 
            					(!p.getDomains(ontology).isEmpty() &&  ! p.getRanges(ontology).isEmpty()) && 
            					(!q.getDomains(ontology).isEmpty()  &&  ! q.getRanges(ontology).isEmpty()))
	            			{
	            				//System.out.println(" q.getRanges(ontology):" +  q.getRanges(ontology)); 
	                            //System.out.println(" q.getRanges(ontology):" +  q.getRanges(ontology)); 
	                            OWLClassExpression domainP = p.getDomains(ontology).iterator().next(); // DomainP
	                            OWLClassExpression domainQ = q.getDomains(ontology).iterator().next(); // DomainQ
	                            OWLClassExpression rangeP = p.getRanges(ontology).iterator().next(); // RangeP
	                            OWLClassExpression rangeQ = q.getRanges(ontology).iterator().next(); // RangeQ
	                            String expDomainP = UsefulOWL.returnIRI(domainP.toString());
	                            String expRangeP = UsefulOWL.returnIRI(rangeP.toString());
	                            if(nok.getDescProp().equals(UsefulOWL.returnPropObjectProperty(p)) && nok.getDescRangeOrDomain().equals(expDomainP)) 
	                            {
	                            	OWLObjectMinCardinality l = factory.getOWLObjectMinCardinality(nok.getCard(), q, domainQ);
	                                g.insertNode(l.toString(), NodeType.RestrictionCardinality);
	                                g.insertEdge(nok.getExpression(), l.toString());
	                            }
	                            if(nok.getDescProp().equals(UsefulOWL.returnPropObjectProperty(p)) && nok.getDescRangeOrDomain().equals(expRangeP)) 
	                            {
	                            	OWLObjectMinCardinality l = factory.getOWLObjectMinCardinality(nok.getCard(), q, rangeQ);
	                            	g.insertNode(l.toString(), NodeType.RestrictionCardinality);
	                            	g.insertEdge(nok.getExpression(), l.toString());
	                        	}
	                        }
	        			}
	        		}
	        	}
	        }
			//Stage 2 end
			
			//Stage 3 Start
			
			//Add the negation for each node in the adj list
			vertices = g.getVertices();
			adj = g.getAdj();
			if (debug.contentEquals("Y")) {

				Graph2Console(g);
			}
			//int tempSize = adj.size();
			for(int j = 0; j < adj.size(); j++) 
			{
	            LinkedList<Integer> e = adj.get(j);
	            if (e != null)
	            {
	            	Node n = vertices.get(e.get(0));
					NodeType type = n.getNodeType();
	            	switch(type)
	            	{
					   case Class :
					       NodeClass nc = (NodeClass)n;
					       g.insertNodeRestrictionComplementOfClass(nc.getExpression());
					       break;
					   case Property :
					       NodeProperty np = (NodeProperty)n;
					       g.insertNodeRestrictionComplementOfProperty(np.getDescProp(),np.getDescIRI());
					       break;
					   case RestrictionCardinality :
						   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n;
						   g.insertNodeRestrictionComplementOfRestrictionCardinality(nrc.getExpression());
						   break;
					   case RestrictionComplementOfClass :
					       NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n;
					       g.insertNode(notClass.getExpClass().toString(), NodeType.Class);
					       break;
					   case RestrictionComplementOfProperty :
					       NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n;
					       g.insertNodeProperty(notProp.getDescIRI(), notProp.getDescProp());
					       break;
					   case RestrictionComplementOfRestrictionCardinality :
					       NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n;
					       g.insertNodeRestrictionCardinality(notRestCard.getIRIDomain(),notRestCard.getiRIProp(),notRestCard.getDescProp(),notRestCard.getDescRangeOrDomain(),notRestCard.getCard());
					       break;
					   default: 
						   break;
				    }
            	}
            }
			if (debug.contentEquals("Y")) {

				System.out.println("Add negations");
				Graph2Console(g);
			}
			//Copying original Adjacent List
			vertices = g.getVertices();
			adj = g.getAdj();
			
	        LinkedList<Integer> tempChecked;
	        ArrayList<LinkedList<Integer>> adjChecked = new ArrayList<LinkedList<Integer>>();
	        for(LinkedList<Integer> tempC1 : adj)
	        {
	        	tempChecked = new LinkedList<Integer>();
	        	for(int i = 0; i < tempC1.size(); i++)
	        	{
	        		int Element = tempC1.get(i);
	        		tempChecked.add(Element);
	        	}
	        	
	        	adjChecked.add(tempChecked);
	        }
			
	        
	        
			//3iii - inserting correspondent edges between the negations added 
	        vertices = g.getVertices();
			adj = g.getAdj();
			String notM = ""; 
			String notN = "";
			for(int i = 0; i < adjChecked.size(); i++)
			{
	            LinkedList<Integer> e = adjChecked.get(i);
	        	if(e.size() > 0 && e.get(0)!= null)
	        	{
	        		Node nodeExpM =  vertices.get(e.get(0));
	        		notM = getNotNode(nodeExpM);
	        		int idNotM = g.getNodeKey(notM);
	        		for(int k = 1; k < e.size(); k++)
	        		{
	        			Node noExpN =  vertices.get(e.get(k));
	        			notN = getNotNode(noExpN);
	        			int idNotN = g.getNodeKey(notN);
	        			LinkedList<Integer> adjn = adj.get(idNotN);
	        			if(!adjn.contains(idNotM))
	        			{
	        				adjn.addLast(idNotM);
	        			}
	        			//g.insertEdge(notN, notM);
        			}
        		}
        	}
        	
			
			
			
	        //3ii - adding arcs between related restriction card when necessary
			
			g = addEdgesBetweenRC(g);
			if (debug.contentEquals("Y")) {

				System.out.println("Linking mincard");
				Graph2Console(g);
			}
			//Stage 3 End
	        //Stage 4 Start
	        
	        
	        vertices = g.getVertices();
			adj = g.getAdj();
			//1st I need to separate all NodeRestrictionCardinality from the NodeRestrictionComplementOfRestrictionCardinality
            ArrayList<NodeRestrictionComplementOfRestrictionCardinality> nodesRCMin1WithNOT = new ArrayList<NodeRestrictionComplementOfRestrictionCardinality>();
            ArrayList<NodeRestrictionCardinality> nodesRCMin1WithoutNOT = new ArrayList<NodeRestrictionCardinality>();
            BreadthFirstSearch bfs = new BreadthFirstSearch(g);
            
            for(Map.Entry<Integer, Node> n : vertices.entrySet())
    		{
            	if(n.getValue().getNodeType().equals(NodeType.RestrictionCardinality))
            	{
            		NodeRestrictionCardinality tempN = (NodeRestrictionCardinality) n.getValue();
            		int card = tempN.getCard();
            		if(card == 1)
            		{
            			nodesRCMin1WithoutNOT.add(tempN);
            		}
            	}
            	if(n.getValue().getNodeType().equals(NodeType.RestrictionComplementOfRestrictionCardinality))
            	{
            		NodeRestrictionComplementOfRestrictionCardinality tempN = (NodeRestrictionComplementOfRestrictionCardinality) n.getValue();
            		int card = tempN.getCard();
            		if(card == 1)
            		{
            			nodesRCMin1WithNOT.add(tempN);
            		}
            	}
            }
            //2nd insert edges
            for(NodeRestrictionCardinality nP : nodesRCMin1WithoutNOT)
            {
            	int idP = nP.getId();
            	String notDescPropP = nP.getDescProp();
            	for(NodeRestrictionComplementOfRestrictionCardinality nQ : nodesRCMin1WithNOT)
            	{
                	int idQ = nQ.getId();
                	String notDescPropQ = nQ.getDescProp();
                	if((!notDescPropP.matches(notDescPropQ))&&(bfs.existsPath(idP, idQ)))
                	{
                		notDescPropP = "~" + notDescPropP;
                		notDescPropQ = "~" + notDescPropQ;
                		String nodeK = new NodeProperty(nP.getIRIDomain(),nP.getDescProp()).getExpression();
                		String notNodeK = new NodeProperty(nP.getIRIDomain(),notDescPropP).getExpression();
                		String nodeL = new NodeProperty(nQ.getIRIDomain(),notDescPropQ).getExpression();
                		String notNodeL = new NodeProperty(nQ.getIRIDomain(),nQ.getDescProp()).getExpression();
                        g.insertEdge(nodeK, nodeL);
                        g.insertEdge(notNodeL, notNodeK);
                	}
            	}
            }
	        //Stage 4 End
			if (debug.contentEquals("Y")) {

	            System.out.println("\n Graph Constructed: \n");
				Graph2Console(g);
				System.out.println("\n Tagging Bottom and Top Nodes: \n");
	            g = searchBottomNodes(g);
				Graph2Console(g);
				Graph2File(g);
				System.out.println("\n Ontology loaded");
				//Graph2Console(g);
			}
			//Time stamping
			date= new java.util.Date();
			stamp = stamp + "Ontology loaded: \n";
			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
			System.out.println(stamp);
			writer.write(stamp);
	        writer.close();
		}
		catch(Exception e)	
		{
			String str = Log + "\nError : Could not load ontology";
  		  	Log=str;
  		  	System.out.println(str);// to show error when running in batch
  		  	throw e;
		}
		return g;
	}
	
	/**
	 * Creates a Graph from an OWLOntology
	 * 
	 * @param NormOnt
	 * @param Log - error log
	 * @return
	 * @throws Exception 
	 */
	public Graph createGraphBatch(Object[] NormOnt, String Log) throws Exception
	{
		Graph g = new Graph();
        OWLOntology ontology = (OWLOntology) NormOnt[0];
		OWLDataFactory factory = (OWLDataFactory) NormOnt[1];
		//OWLOntologyManager manager = (OWLOntologyManager)NormOnt[2];
		String path = System.getProperty("user.home") + "/LastLoadedGraphTime.txt";
    	File logFile = new File(path);
    	BufferedWriter writer = null;
    	java.util.Date date;
		
		try{
			//Time stamping the load
			date = new java.util.Date();
			String stamp;
			stamp = "Loading Ontology: \n";
			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
			System.out.println(stamp);
			writer = new BufferedWriter(new FileWriter(logFile));
			
			//Stage 1 Start
			//Classes insertion
			for(OWLClass cls : ontology.getClassesInSignature()) 
			{
	            if(!cls.isOWLThing())
	            {
	            	g.insertNode(cls.toString(), NodeType.Class);
	            }
			}
			//Properties insertion
			for(OWLObjectProperty pro : ontology.getObjectPropertiesInSignature()) 
			{
	            g.insertNode(pro.toString(), NodeType.Property);
			}
			
			//SubClasses insertion
			for(OWLSubClassOfAxiom subClass : ontology.getAxioms(AxiomType.SUBCLASS_OF)) 
			{
				if((!subClass.getSubClass().isOWLThing())&&(!subClass.getSuperClass().isOWLThing()))
				{
					ClassExpressionType subClassType = subClass.getSubClass().getClassExpressionType();
					ClassExpressionType superClassType = subClass.getSuperClass().getClassExpressionType();
					OWLObject subClassObj = subClass.getSubClass();
					OWLObject superClassObj = subClass.getSuperClass();
					String subClassStr = subClassObj.toString();
					String superClassStr = superClassObj.toString();
					if(UsefulOWL.isObjectObjectMaxCardinality(superClassType))
					{
						String desc = superClassStr;
						desc = desc.replaceAll("#", "/");
				        String IRIDomain = UsefulOWL.returnIRIDomain(desc);
				        String IRIProp = UsefulOWL.returnIRIProp(desc);
				        String descProp = UsefulOWL.returnProp(desc);
				        String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
				        int card = UsefulOWL.returnCard(desc);
				        card++;
				        PrefixManager pmiRIDomain = new DefaultPrefixManager(IRIDomain);
				    	PrefixManager pmiRIProp = new DefaultPrefixManager(IRIProp);
				        OWLClassExpression domainOrRangeObjProp = factory.getOWLClass(descRangeOrDomain, pmiRIDomain);
				       	OWLObjectProperty propRestriction = factory.getOWLObjectProperty(descProp, pmiRIProp);
				       	OWLObjectMinCardinality restrictionMinCard = factory.getOWLObjectMinCardinality(card, propRestriction, domainOrRangeObjProp);
				       	OWLObjectComplementOf notRestrictionMinCard = factory.getOWLObjectComplementOf((OWLClassExpression)restrictionMinCard);
				       	superClassObj = notRestrictionMinCard;
				       	superClassStr = notRestrictionMinCard.toString();
					}
					if(UsefulOWL.isObjectObjectMaxCardinality(subClassType))
					{
						String desc = subClassStr;
						desc = desc.replaceAll("#", "/");
				        String IRIDomain = UsefulOWL.returnIRIDomain(desc);
				        String IRIProp = UsefulOWL.returnIRIProp(desc);
				        String descProp = UsefulOWL.returnProp(desc);
				        String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
				        int card = UsefulOWL.returnCard(desc);
				        card++;
				        PrefixManager pmiRIDomain = new DefaultPrefixManager(IRIDomain);
				    	PrefixManager pmiRIProp = new DefaultPrefixManager(IRIProp);
				        OWLClassExpression domainOrRangeObjProp = factory.getOWLClass(descRangeOrDomain, pmiRIDomain);
				       	OWLObjectProperty propRestriction = factory.getOWLObjectProperty(descProp, pmiRIProp);
				       	OWLObjectMinCardinality restrictionMinCard = factory.getOWLObjectMinCardinality(card, propRestriction, domainOrRangeObjProp);
				       	OWLObjectComplementOf notRestrictionMinCard = factory.getOWLObjectComplementOf((OWLClassExpression)restrictionMinCard);
				       	subClassObj = notRestrictionMinCard;
				       	subClassStr = notRestrictionMinCard.toString();
					}
					g = insertNodeSuperSubClassAxiom(superClassType,superClassObj,g);
					g = insertNodeSubSubClassAxiom(subClassType,subClassObj, g);
					g.insertEdge(subClassStr, superClassStr);
				}
			}
			//SubProperties insertion
			for(OWLSubObjectPropertyOfAxiom subProp : ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) 
			{
				OWLObjectPropertyExpression SubPropExp = subProp.getSubProperty();
				OWLObjectPropertyExpression SuperPropExp = subProp.getSuperProperty();
				g.insertNode(SuperPropExp.toString(), NodeType.Property);
				g.insertNode(SubPropExp.toString(), NodeType.Property);
				g.insertEdge(SubPropExp.toString(), SuperPropExp.toString());
			}
			//Stage 1 End
			System.out.println("Classes and Properties");
			//Graph2Console(g);
			//Stage 2 Start
			HashMap<Integer, Node> vertices = g.getVertices();
	        List<LinkedList<Integer>> adj = g.getAdj();
	        
			for(OWLSubObjectPropertyOfAxiom subProp : ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) 
			{
				OWLObjectPropertyExpression p = subProp.getSubProperty();
	            OWLObjectPropertyExpression q = subProp.getSuperProperty();
	            for(int j = 0; j < adj.size(); j++) 
				{
		            LinkedList<Integer> e = adj.get(j);
	            	if(e.size() > 0 &&(e !=null) )
	            	{
	            		if(vertices.get(e.get(0)).getNodeType().equals(NodeType.RestrictionCardinality)) 
	            		{
	            			NodeRestrictionCardinality nok = (NodeRestrictionCardinality) g.getVertices().get(e.get(0));
	            			if((p.getDomains(ontology) != null) &&(q.getDomains(ontology) != null) && 
            					(!p.getDomains(ontology).isEmpty() &&  ! p.getRanges(ontology).isEmpty()) && 
            					(!q.getDomains(ontology).isEmpty()  &&  ! q.getRanges(ontology).isEmpty()))
	            			{
	            				//System.out.println(" q.getRanges(ontology):" +  q.getRanges(ontology)); 
	                            //System.out.println(" q.getRanges(ontology):" +  q.getRanges(ontology)); 
	                            OWLClassExpression domainP = p.getDomains(ontology).iterator().next(); // DomainP
	                            OWLClassExpression domainQ = q.getDomains(ontology).iterator().next(); // DomainQ
	                            OWLClassExpression rangeP = p.getRanges(ontology).iterator().next(); // RangeP
	                            OWLClassExpression rangeQ = q.getRanges(ontology).iterator().next(); // RangeQ
	                            String expDomainP = UsefulOWL.returnIRI(domainP.toString());
	                            String expRangeP = UsefulOWL.returnIRI(rangeP.toString());
	                            if(nok.getDescProp().equals(UsefulOWL.returnPropObjectProperty(p)) && nok.getDescRangeOrDomain().equals(expDomainP)) 
	                            {
	                            	OWLObjectMinCardinality l = factory.getOWLObjectMinCardinality(nok.getCard(), q, domainQ);
	                                g.insertNode(l.toString(), NodeType.RestrictionCardinality);
	                                g.insertEdge(nok.getExpression(), l.toString());
	                            }
	                            if(nok.getDescProp().equals(UsefulOWL.returnPropObjectProperty(p)) && nok.getDescRangeOrDomain().equals(expRangeP)) 
	                            {
	                            	OWLObjectMinCardinality l = factory.getOWLObjectMinCardinality(nok.getCard(), q, rangeQ);
	                            	g.insertNode(l.toString(), NodeType.RestrictionCardinality);
	                            	g.insertEdge(nok.getExpression(), l.toString());
	                        	}
	                        }
	        			}
	        		}
	        	}
	        }
			//Stage 2 end
			
			//Stage 3 Start
			
			//Add the negation for each node in the adj list
			vertices = g.getVertices();
			adj = g.getAdj();
			//Graph2Console(g);
			//int tempSize = adj.size();
			for(int j = 0; j < adj.size(); j++) 
			{
	            LinkedList<Integer> e = adj.get(j);
	            if (e != null)
	            {
	            	Node n = vertices.get(e.get(0));
					NodeType type = n.getNodeType();
	            	switch(type)
	            	{
					   case Class :
					       NodeClass nc = (NodeClass)n;
					       g.insertNodeRestrictionComplementOfClass(nc.getExpression());
					       break;
					   case Property :
					       NodeProperty np = (NodeProperty)n;
					       g.insertNodeRestrictionComplementOfProperty(np.getDescProp(),np.getDescIRI());
					       break;
					   case RestrictionCardinality :
						   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n;
						   g.insertNodeRestrictionComplementOfRestrictionCardinality(nrc.getExpression());
						   break;
					   case RestrictionComplementOfClass :
					       NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n;
					       g.insertNode(notClass.getExpClass().toString(), NodeType.Class);
					       break;
					   case RestrictionComplementOfProperty :
					       NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n;
					       g.insertNodeProperty(notProp.getDescIRI(), notProp.getDescProp());
					       break;
					   case RestrictionComplementOfRestrictionCardinality :
					       NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n;
					       g.insertNodeRestrictionCardinality(notRestCard.getIRIDomain(),notRestCard.getiRIProp(),notRestCard.getDescProp(),notRestCard.getDescRangeOrDomain(),notRestCard.getCard());
					       break;
					   default: 
						   break;
				    }
            	}
            }
			System.out.println("Add negations");
			//Graph2Console(g);
			//Copying original Adjacent List
			vertices = g.getVertices();
			adj = g.getAdj();
			
	        LinkedList<Integer> tempChecked;
	        ArrayList<LinkedList<Integer>> adjChecked = new ArrayList<LinkedList<Integer>>();
	        for(LinkedList<Integer> tempC1 : adj)
	        {
	        	tempChecked = new LinkedList<Integer>();
	        	for(int i = 0; i < tempC1.size(); i++)
	        	{
	        		int Element = tempC1.get(i);
	        		tempChecked.add(Element);
	        	}
	        	
	        	adjChecked.add(tempChecked);
	        }
			
	        
	        
			//3iii - inserting correspondent edges between the negations added 
	        vertices = g.getVertices();
			adj = g.getAdj();
			String notM = ""; 
			String notN = "";
			for(int i = 0; i < adjChecked.size(); i++)
			{
	            LinkedList<Integer> e = adjChecked.get(i);
	        	if(e.size() > 0 && e.get(0)!= null)
	        	{
	        		Node nodeExpM =  vertices.get(e.get(0));
	        		notM = getNotNode(nodeExpM);
	        		int idNotM = g.getNodeKey(notM);
	        		for(int k = 1; k < e.size(); k++)
	        		{
	        			Node noExpN =  vertices.get(e.get(k));
	        			notN = getNotNode(noExpN);
	        			int idNotN = g.getNodeKey(notN);
	        			LinkedList<Integer> adjn = adj.get(idNotN);
	        			if(!adjn.contains(idNotM))
	        			{
	        				adjn.addLast(idNotM);
	        			}
	        			//g.insertEdge(notN, notM);
        			}
        		}
        	}
        	
			
			
			
	        //3ii - adding arcs between related restriction card when necessary
			
			g = addEdgesBetweenRC(g);
			System.out.println("Linking mincard");
			//Graph2Console(g);
			//Stage 3 End
	        //Stage 4 Start
	        
	        
	        vertices = g.getVertices();
			adj = g.getAdj();
			//1st I need to separate all NodeRestrictionCardinality from the NodeRestrictionComplementOfRestrictionCardinality
            ArrayList<NodeRestrictionComplementOfRestrictionCardinality> nodesRCMin1WithNOT = new ArrayList<NodeRestrictionComplementOfRestrictionCardinality>();
            ArrayList<NodeRestrictionCardinality> nodesRCMin1WithoutNOT = new ArrayList<NodeRestrictionCardinality>();
            BreadthFirstSearch bfs = new BreadthFirstSearch(g);
            
            for(Map.Entry<Integer, Node> n : vertices.entrySet())
    		{
            	if(n.getValue().getNodeType().equals(NodeType.RestrictionCardinality))
            	{
            		NodeRestrictionCardinality tempN = (NodeRestrictionCardinality) n.getValue();
            		int card = tempN.getCard();
            		if(card == 1)
            		{
            			nodesRCMin1WithoutNOT.add(tempN);
            		}
            	}
            	if(n.getValue().getNodeType().equals(NodeType.RestrictionComplementOfRestrictionCardinality))
            	{
            		NodeRestrictionComplementOfRestrictionCardinality tempN = (NodeRestrictionComplementOfRestrictionCardinality) n.getValue();
            		int card = tempN.getCard();
            		if(card == 1)
            		{
            			nodesRCMin1WithNOT.add(tempN);
            		}
            	}
            }
            //2nd insert edges
            for(NodeRestrictionCardinality nP : nodesRCMin1WithoutNOT)
            {
            	int idP = nP.getId();
            	String notDescPropP = nP.getDescProp();
            	for(NodeRestrictionComplementOfRestrictionCardinality nQ : nodesRCMin1WithNOT)
            	{
                	int idQ = nQ.getId();
                	String notDescPropQ = nQ.getDescProp();
                	if((!notDescPropP.matches(notDescPropQ))&&(bfs.existsPath(idP, idQ)))
                	{
                		notDescPropP = "~" + notDescPropP;
                		notDescPropQ = "~" + notDescPropQ;
                		String nodeK = new NodeProperty(nP.getIRIDomain(),nP.getDescProp()).getExpression();
                		String notNodeK = new NodeProperty(nP.getIRIDomain(),notDescPropP).getExpression();
                		String nodeL = new NodeProperty(nQ.getIRIDomain(),notDescPropQ).getExpression();
                		String notNodeL = new NodeProperty(nQ.getIRIDomain(),nQ.getDescProp()).getExpression();
                        g.insertEdge(nodeK, nodeL);
                        g.insertEdge(notNodeL, notNodeK);
                	}
            	}
            }
	        //Stage 4 End
            System.out.println("\n Graph Constructed: \n");
			//Graph2Console(g);
			System.out.println("\n Tagging Bottom and Top Nodes: \n");
            g = searchBottomNodes(g);
			//Graph2Console(g);
			//Graph2File(g);
			System.out.println("\n Ontology loaded");
			Graph2Console(g);
			//Time stamping
			date= new java.util.Date();
			stamp = stamp + "Ontology loaded: \n";
			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
			System.out.println(stamp);
			writer.write(stamp);
	        writer.close();
		}
		catch(Exception e)	
		{
			Log+=  "\nError : Could not load ontology";
  		  	
  		  	throw e;
		}
		return g;
	}
	/**
	 * Treat the insertion into the Graph g of the Super classes 
	 * 
	 * @param classExpressionType
	 * @param obj
	 * @param g
	 * @return
	 */
	public Graph insertNodeSuperSubClassAxiom(ClassExpressionType classExpressionType,OWLObject obj,Graph g)
    {
        if(UsefulOWL.isRestrictionComplementOfClass(classExpressionType,obj)) 
        {
        	g.insertNodeRestrictionComplementOfClass(obj.toString(), NodeType.RestrictionComplementOfClass);
        }
        if(UsefulOWL.isRestrictionComplementOfRestrictionCardinality(classExpressionType, obj))
        {
        	g.insertNodeRestrictionComplementOfRestrictionCardinality(obj.toString());
        }
        if(UsefulOWL.isObjectObjectMaxCardinality(classExpressionType))
        {
        	g.insertNodeRestrictionComplementOfRestrictionCardinality(obj.toString());
        }
        if(UsefulOWL.isClass(classExpressionType)) 
        {
        	g.insertNode(obj.toString(), NodeType.Class);
        }
        if(UsefulOWL.isObjectObjectMinCardinality(classExpressionType))
        {
           g.insertNode(obj.toString(), NodeType.RestrictionCardinality);
        }
        return g;
    }

	/**
	 * Treat the insertion into the Graph g of the Sub classes
	 * 
	 * @param classExpressionType
	 * @param obj
	 * @param g
	 * @return
	 */
    public Graph insertNodeSubSubClassAxiom(ClassExpressionType classExpressionType,OWLObject obj, Graph g) 
    {
    	if(UsefulOWL.isRestrictionComplementOfClass(classExpressionType , obj)) 
        {
        	g.insertNodeRestrictionComplementOfClass(obj.toString());
        }
        if(UsefulOWL.isRestrictionComplementOfRestrictionCardinality(classExpressionType, obj))
        {
        	g.insertNodeRestrictionComplementOfRestrictionCardinality(obj.toString());
        }
        if(UsefulOWL.isObjectObjectMaxCardinality(classExpressionType))
        {
        	g.insertNodeRestrictionComplementOfRestrictionCardinality(obj.toString());
        }
        if(UsefulOWL.isClass(classExpressionType)) 
        {
        	g.insertNode(obj.toString(), NodeType.Class);
        }
        if(UsefulOWL.isObjectObjectMinCardinality(classExpressionType))
        {
           g.insertNode(obj.toString(), NodeType.RestrictionCardinality);
        }
        return g;
    }
    
    /**
     * Returns a new node (as an OWLObject) that is the negation of the node n
     * 
     * @param n
     * @return
     */
    public static String getNotNode(Node n)
    {
    	String notNode = "";
    	NodeType type = n.getNodeType();
    	NodeClass nodeClass;
    	NodeProperty nodeProp;
    	NodeRestrictionCardinality nodeRestCard;
    	NodeRestrictionComplementOfClass notClass;
    	NodeRestrictionComplementOfProperty notProp;
    	NodeRestrictionComplementOfRestrictionCardinality notRestCard;
    	switch(type)
    	{
    		case Class :
    			notClass = new NodeRestrictionComplementOfClass(n.getExpression());
    			notNode = notClass.getExpression();
    			break;
       			
   			case Property :
   				nodeProp = (NodeProperty) n;
   				notProp = new NodeRestrictionComplementOfProperty(nodeProp.getDescProp(), nodeProp.getDescIRI());
   				notNode = notProp.getExpression();
   				break;
   				
   			case RestrictionCardinality :
   				notRestCard = new NodeRestrictionComplementOfRestrictionCardinality(n.getExpression());
   				notNode = notRestCard.getExpression();
   				break;
   				
   			case RestrictionComplementOfClass :
   				notClass = (NodeRestrictionComplementOfClass) n;
   				nodeClass = new NodeClass(notClass.getDescClass(), notClass.getDescIRI());
   				notNode = nodeClass.getExpression();
   				break;
   				
   			case RestrictionComplementOfProperty :
   				notProp = (NodeRestrictionComplementOfProperty) n;
   				nodeProp = new NodeProperty(notProp.getDescIRI(), notProp.getDescProp());
   				notNode = nodeProp.getExpression();
   				break;
   				
   			case RestrictionComplementOfRestrictionCardinality :
   				notRestCard = (NodeRestrictionComplementOfRestrictionCardinality) n;
   				nodeRestCard = new NodeRestrictionCardinality(notRestCard.getIRIDomain(),notRestCard.getiRIProp(),
   						notRestCard.getDescProp(),notRestCard.getDescRangeOrDomain(),notRestCard.getCard());
   				notNode = nodeRestCard.getExpression();
   				break;
		}
    	return notNode;
    }
    
    /**
     * Searches Contradiction Paths between Class nodes from g, node n with path to node not n
     * 
     * @param g
     * @param nosClassWithoutNOT
     * @param bfs
     * @param caminhoContradicao
     * @return
     */
    public LinkedList<Graph.Edge> getContradictionsClass(Graph g, ArrayList<NodeClass> nodesClassWithoutNot, BreadthFirstSearch bfs, LinkedList<Graph.Edge> Contradictions) 
    {
        for(NodeClass n : nodesClassWithoutNot) 
        {
            int idM = g.getNodeKey(n.getExpression());
            int idN = g.getNodeKeyComplementOfClass(n.getExpression().toString());
            ArrayList<LinkedList<Integer>> TransitiveClosure = bfs.getReachable();
            
            if((idM >= 0)&&(idN >= 0))
            {
            	Graph.Edge e = new Graph.Edge(idM, idN);
                if(bfs.existsPath(idM, idN))
                {
                    g = setBottomNode(g, idM, idN);
                    Contradictions.add(e);
                }
                else if(!bfs.existsPath(idN, idM))
                {
                	for(LinkedList<Integer> tempC : TransitiveClosure)
                	{
                		if(tempC.contains(idM) && tempC.contains(idN) && (tempC.get(0) != idN) && (tempC.get(0) != idM))
                		{
                			Node nB = g.getVertices().get(tempC.get(0));
            				nB.setNodeBottom(true);
                			if(!Contradictions.contains(e))
                			{                				
	                            Contradictions.add(e);
                			}
                		}
                	}
                }
            }
        }
        return Contradictions;
    }
    
    /**
     * Searches Contradiction Paths between Property nodes from g, node n with path to node notn
     * 
     * @param g
     * @param nodesPropertyWithoutNot
     * @param bfs
     * @param Contradictions
     * @return
     */
    public LinkedList<Graph.Edge> getContradictionsProperty(Graph g, ArrayList<NodeProperty> nodesPropertyWithoutNot, BreadthFirstSearch bfs, LinkedList<Graph.Edge> Contradictions) 
    {
        for(NodeProperty n : nodesPropertyWithoutNot) 
        {
            int idM = g.getNodeKeyProperty(n.getDescIRI(),n.getDescProp());
            int idN = g.getNodeKeyComplementOfProperty(n.getDescIRI(),n.getDescProp());
            ArrayList<LinkedList<Integer>> TransitiveClosure = bfs.getReachable();
            
            if((idM >= 0)&&(idN >= 0))
            {
            	Graph.Edge e = new Graph.Edge(idM, idN);
                if(bfs.existsPath(idM, idN))
                {
                    g = setBottomNode(g, idM, idN);
                    Contradictions.add(e);
                }
                else if(!bfs.existsPath(idN, idM))
                {
                	for(LinkedList<Integer> tempC : TransitiveClosure)
                	{
                		if(tempC.contains(idM) && tempC.contains(idN) && (tempC.get(0) != idN) && (tempC.get(0) != idM))
                		{
                			Node nB = g.getVertices().get(tempC.get(0));
            				nB.setNodeBottom(true);
                			if(!Contradictions.contains(e))
                			{                				
	                            Contradictions.add(e);
                			}
                		}
                	}
                }
            }
        }
        return Contradictions;
    }
    
    /**
     * Searches Contradiction Paths between Restriction Cardinality nodes from g, node n with path to node notn
     * 
     * @param g
     * @param nodesRCWithoutNot
     * @param bfs
     * @param Contradictions
     * @return
     */
    public LinkedList<Graph.Edge> getContradictionsRestrictionCardinality(Graph g, ArrayList<NodeRestrictionCardinality> nodesRCWithoutNot, BreadthFirstSearch bfs, LinkedList<Graph.Edge> Contradictions) 
    {
        for(NodeRestrictionCardinality n : nodesRCWithoutNot) 
        {
            int idM = g.getNodeKeyRestrictionCardinality(n.getIRIDomain(), n.getIRIProp(), n.getDescProp(), n.getDescRangeOrDomain(), n.getCard());
            int idN = g.getNodeKeyComplementOfRestrictionCardinality(n.getIRIDomain(), n.getIRIProp(), n.getDescProp(), n.getDescRangeOrDomain(), n.getCard());
            ArrayList<LinkedList<Integer>> TransitiveClosure = bfs.getReachable();
            
            if((idM >= 0)&&(idN >= 0))
            {
            	Graph.Edge e = new Graph.Edge(idM, idN);
                if(bfs.existsPath(idM, idN))
                {
                    g = setBottomNode(g, idM, idN);
                    Contradictions.add(e);
                }
                else if(!bfs.existsPath(idN, idM))
                {
                	for(LinkedList<Integer> tempC : TransitiveClosure)
                	{
                		if(tempC.contains(idM) && tempC.contains(idN) && (tempC.get(0) != idN) && (tempC.get(0) != idM))
                		{
                			Node nB = g.getVertices().get(tempC.get(0));
            				nB.setNodeBottom(true);
                			if(!Contradictions.contains(e))
                			{                				
	                            Contradictions.add(e);
                			}
                		}
                	}
                }
            }
        }
        return Contradictions;
    }
    
    /**
     * Tag nodes as bottom and top nodes, and set node level to 0 for the bottom one
     * 
     * @param g
     * @param idM
     * @param idN
     * @return
     */
    public Graph setBottomNode(Graph g, int idM, int idN) 
    {
    	Node nM = g.getVertices().get(idM);
    	Node nN = g.getVertices().get(idN);
        if((nN.isNegation())&&(!(nM.isNegation()))) 
        {
        	nM.setNodeBottom(true);
        	nM.setNodeTop(false);
        	//nM.setLevel(0);
        	nN.setNodeBottom(false);
        	nN.setNodeTop(true);
            return g;
        }
        if (!(nN.isNegation())&&(nM.isNegation()))
        {
        	nN.setNodeBottom(true);
        	nN.setNodeTop(false);
        	//nN.setLevel(0);
        	nM.setNodeBottom(false);
        	nM.setNodeTop(true);
        }
        return g;
    }
    
    /**
     * Add arcs in g between related restriction cardinalities when necessary
     * 
     * @param g
     * @return
     */
    public static Graph addEdgesBetweenRC(Graph g)
    {
    	//3ii - adding arcs between related restriction card when necessary
        HashMap<Integer,Node> vertices = g.getVertices();
		ArrayList<LinkedList<Integer>>adj = g.getAdj();
		//tempSize = adj.size();
		for(int i = 0; i < adj.size(); i++)
		{
            LinkedList<Integer> e = adj.get(i);
            if(e.get(0) != null && vertices.get(e.get(0)).getNodeType().equals(NodeType.RestrictionCardinality)) 
            {
            	NodeRestrictionCardinality nodeM = (NodeRestrictionCardinality)vertices.get(e.get(0));
            	int cardM = nodeM.getCard();
            	String propM = nodeM.getDescProp();
            	String propIRIM = nodeM.getIRIProp();
            	String domainOrRangeM = nodeM.getDescRangeOrDomain();
            	String domainOrRangeIRIM = nodeM.getIRIDomain();
            	for(int j = 0; j < adj.size(); j++)
    			{
    	            LinkedList<Integer> f = adj.get(j);
            		if(f.get(0) != null && vertices.get(f.get(0)).getNodeType().equals(NodeType.RestrictionCardinality))
            		{
            			NodeRestrictionCardinality nodeN = (NodeRestrictionCardinality)vertices.get(f.get(0));
            			int cardN = nodeN.getCard();
            			String propN = nodeN.getDescProp();
            			String propIRIN = nodeN.getIRIProp();
            			String domainOrRangeN = nodeN.getDescRangeOrDomain();
            			String domainOrRangeIRIN = nodeN.getIRIDomain();
            			if((propN.equals(propM)) && (propIRIN.equals(propIRIM)) && (cardM < cardN) && 
    					(domainOrRangeIRIN.equals(domainOrRangeIRIM)) && (domainOrRangeN.equals(domainOrRangeM))) 
            			{
            				g.insertEdge(nodeN.getExpression(), nodeM.getExpression());
        				}
        			}
        		}
        	}
            else if(e.get(0) != null && vertices.get(e.get(0)).getNodeType().equals(NodeType.RestrictionComplementOfRestrictionCardinality)) 
            {
            	NodeRestrictionComplementOfRestrictionCardinality nodeM = (NodeRestrictionComplementOfRestrictionCardinality)vertices.get(e.get(0));
            	int cardM = nodeM.getCard();
            	String propM = nodeM.getDescProp();
            	String propIRIM = nodeM.getiRIProp();
            	String domainOrRangeM = nodeM.getDescRangeOrDomain();
            	String domainOrRangeIRIM = nodeM.getIRIDomain();
            	for(int j = 0; j < adj.size(); j++)
    			{
    	            LinkedList<Integer> f = adj.get(j);
            		if(f.get(0) != null && vertices.get(f.get(0)).getNodeType().equals(NodeType.RestrictionComplementOfRestrictionCardinality))
            		{
            			NodeRestrictionComplementOfRestrictionCardinality nodeN = (NodeRestrictionComplementOfRestrictionCardinality)vertices.get(f.get(0));
            			int cardN = nodeN.getCard();
            			String propN = nodeN.getDescProp();
            			String propIRIN = nodeN.getiRIProp();
            			String domainOrRangeN = nodeN.getDescRangeOrDomain();
            			String domainOrRangeIRIN = nodeN.getIRIDomain();
            			if((propN.equals(propM)) && (propIRIN.equals(propIRIM)) && (cardM < cardN) && 
    					(domainOrRangeIRIN.equals(domainOrRangeIRIM)) && (domainOrRangeN.equals(domainOrRangeM))) 
            			{
            				g.insertEdge(nodeM.getExpression(), nodeN.getExpression());
        				}
        			}
        		}
        	}
        }
    	return g;
    }
    
    /**
     * Search for Bottom and top Nodes in the Graph g tagging those and setting their level
     * 
     * @param g
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Graph searchBottomNodes(Graph g)
    {
    	//Definition 3 Start
        //Part I Start
    	ArrayList<List> nodesWithoutNot = g.getNodesWithoutNot(g);
        ArrayList<NodeClass> nodesClassWithoutNot = (ArrayList<NodeClass>) nodesWithoutNot.get(0);
        ArrayList<NodeProperty> nodesPropertyWithoutNot = (ArrayList<NodeProperty>) nodesWithoutNot.get(1);
        ArrayList<NodeRestrictionCardinality> nodesRCWithoutNot = (ArrayList<NodeRestrictionCardinality>) nodesWithoutNot.get(2) ;
        //redo the bfs all over
        BreadthFirstSearch bfs = new BreadthFirstSearch(g);
        bfs.fullBFS();
        //searching contradictions node n with edge to notn
        LinkedList<Graph.Edge> ContradictionPaths = new LinkedList<Graph.Edge>();
        
        ContradictionPaths = getContradictionsClass(g, nodesClassWithoutNot,bfs, ContradictionPaths);
        ContradictionPaths = getContradictionsProperty(g, nodesPropertyWithoutNot, bfs, ContradictionPaths);
        ContradictionPaths = getContradictionsRestrictionCardinality(g, nodesRCWithoutNot, bfs, ContradictionPaths);
        HashMap<Integer, Node> vertices = g.getVertices();
        List<LinkedList<Integer>> adj = g.getAdj();
        
        for(int j = 0; j < adj.size(); j++)
        {
        	if(adj.get(j) != null)
        	{
        		LinkedList<Integer> a = adj.get(j);
                int idK = a.get(0);
                if(idK >= 0)
                {
                	for(Graph.Edge e : ContradictionPaths )
                	{
                		if(a.contains(e.idNode2()) && a.contains(e.idNode1()))
                        {
                			Node nK = vertices.get(idK);
                			nK.setNodeBottom(true);//idk is bottom node with level 0
                			nK.setLevel(0);
                        }
            		}
            	}
            }
    	}
        //Graph2Console(g);
        //Part I End

        //Part II Start - Updating bottom nodes level
        ArrayList<Node> nodesBottomList = Graph.getNodesBottom(g);
        boolean loop = true;
        while(loop)
        {
        	loop = false;
        	for(Node n1 : nodesBottomList)
            {
            	int level = n1.getLevel();
            	if(level != 0)
            	{
    	        	int n1ID = n1.getId();
    	        	LinkedList<Integer> a = adj.get(n1ID);
    	        	for(Node n2 : nodesBottomList)
    	        	{
    	        		int n2ID = n2.getId();
    	        		if((n1ID != n2ID) && (n2.getLevel() > -1))
    	        		{
    	        			if(a.contains(n2ID))
    	        			{
    	        				if(level == -1)
    	        				{
    	        					level = n2.getLevel() + 1;
    	        					n1.setLevel(level);
    	        					loop = true;
    	        				}
    	        				else if((n2.getLevel() + 1) < level)
    	        				{
    	        					level = n2.getLevel() + 1;
    	        					n1.setLevel(level);
    	        					loop = true;
    	        				}
    	        			}
    	        		}
    	        	}
            	}
            }
        }
        
        //Part II End
        
        //Part III Start - Checking for top nodes, and marking them
        ArrayList<List> bottomNodesWithNot = g.getNegatedBottomNodes(g);
        ArrayList<NodeRestrictionComplementOfClass> bottomNodesClassWithNot = (ArrayList<NodeRestrictionComplementOfClass>)bottomNodesWithNot.get(0);
        ArrayList<NodeRestrictionComplementOfProperty> bottomNodesPropertyWithNot = (ArrayList<NodeRestrictionComplementOfProperty>)bottomNodesWithNot.get(1);
        ArrayList<NodeRestrictionComplementOfRestrictionCardinality> bottomNodesRestritionCardinalityWithNot = (ArrayList<NodeRestrictionComplementOfRestrictionCardinality>)bottomNodesWithNot.get(2) ;
        
        for(NodeRestrictionComplementOfClass nNC: bottomNodesClassWithNot)
        {
        	NodeClass nC = new NodeClass(nNC.getExpClass().toString());
        	int idNotNC = g.getNodeKey(nC.getExpression());
        	if(idNotNC >= 0)
        	{
        		g.getVertices().get(idNotNC).setNodeTop(true);
        		g.getVertices().get(idNotNC).setNodeBottom(false);
        	}
        }
        for(NodeRestrictionComplementOfProperty nNP: bottomNodesPropertyWithNot)
        {
        	NodeProperty nP = new NodeProperty(nNP.getDescIRI(), nNP.getDescProp());
        	int idNotNP = g.getNodeKey(nP.getExpression());
        	if(idNotNP >= 0)
        	{
        		g.getVertices().get(idNotNP).setNodeTop(true);
        		g.getVertices().get(idNotNP).setNodeBottom(false);
        	}
        }
        for(NodeRestrictionComplementOfRestrictionCardinality nNRC: bottomNodesRestritionCardinalityWithNot)
        {
        	NodeRestrictionCardinality nRC = new NodeRestrictionCardinality(nNRC.getIRIDomain(), nNRC.getiRIProp(),nNRC.getDescProp(), nNRC.getDescRangeOrDomain(), nNRC.getCard());
        	int idNotNK = g.getNodeKey(nRC.getExpression());
        	int idNK = g.getNodeKeyComplementOfRestrictionCardinality(nNRC.getIRIDomain(), nNRC.getiRIProp(), nNRC.getDescProp(), nNRC.getDescRangeOrDomain(), nNRC.getCard());
        	if((idNotNK >= 0) && (idNK >= 0))
        	{
        		g.getVertices().get(idNotNK).setNodeTop(true);
        		g.getVertices().get(idNotNK).setNodeBottom(false);
        		g.getVertices().get(idNK).setNodeTop(false);
        		g.getVertices().get(idNK).setNodeBottom(true);
    		}
        }
        //Part III End
        //Definition 3 End
    	return g;
    }
    
    
    /***
     * Print Graph representation on Console
     * @param g
     * @return
     */
	public static String Graph2Console(Graph g)
    {
        String s = "" ;
        s = s + "\n Vertices: \n";
        int nEdges = 0;
        for(Map.Entry<Integer, Node> n : g.getVertices().entrySet())
		{
            s += "\n Node(" + n.getKey() + ") - Bottom ("+n.getValue().isNodeBottom()
                     + ")  - Top ("+ n.getValue().isNodeTop() +") ("+ n.getValue().getId() + "):";
            for(String exps : n.getValue().getExpressions())
            {
            	s += exps;
            }
            s += "\n";
            int j = n.getValue().getId();
            s += "\n Adj [" + j + "]:" + "  ";
            LinkedList<Integer> e = g.getAdj().get(j);
            for(int k = 0; k < e.size(); k++)
            {
            	s += "(" + e.get(k) + ")";
            }
            nEdges = nEdges + e.size() - 1;
        }
        s += "\n Number of Vertices:"+ g.getVertices().size();
        s += "\n";
        s += "\n Number of Edges:"+ nEdges;
        s += "\n";
        System.out.print(s);  
        return s;
    }
    /***
     * Print the same that is printed on console but on a temporary txt file
     * 
     * @param g
     */
	public static void Graph2File(Graph g)
    {
    	try{
    		String path = System.getProperty("user.home") + "/LastLoadedGraph.txt";
	    	File logFile = new File(path);
	    	BufferedWriter writer = null;
	    	writer = new BufferedWriter(new FileWriter(logFile));
	    	String s = "" ;
	        s = s + "\n Vertices: \n";
	        int nEdges = 0;
	        for(Map.Entry<Integer, Node> n : g.getVertices().entrySet())
			{
	            s += "\n Node(" + n.getKey() + ") - Bottom ("+n.getValue().isNodeBottom()
	                     + ")  - Top ("+ n.getValue().isNodeTop() +") ("+ n.getValue().getId() + "):";
	            for(String exps : n.getValue().getExpressions())
	            {
	            	s += exps;
	            }
	            s += "\n";
	            int j = n.getValue().getId();
	            s += "\n Adj [" + j + "]:" + "  ";
	            LinkedList<Integer> e = g.getAdj().get(j);
	            for(int k = 0; k < e.size(); k++)
	            {
	            	s += "(" + e.get(k) + ")";
	            }
	            nEdges = nEdges + e.size() - 1;
	        }
	        s += "\n Number of Vertices:"+ g.getVertices().size();
	        s += "\n";
	        s += "\n Number of Edges:"+ nEdges;
	        s += "\n";
	        writer.write(s);
	        writer.close();
    	}
    	catch(Exception ex)
    	{
    		System.out.print(ex.getMessage()); 
    	}
    }
	
}
