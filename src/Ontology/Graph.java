package Ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/*
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLOntologyManager;
*/
import Ontology.Node;

/**
 * Graph representation for OWL data
 * 
 * 
 *
 */
public class Graph {

	private int nVertices; 						//number of vertices
	private HashMap<Integer, Node> vertices;	//list of vertices
	private ArrayList<LinkedList<Integer>> adj; //Adjacency list
	
	/**
	 * Initializes an empty Graph
	 */
	public Graph() 
	{
		nVertices = 0;
		vertices = new HashMap<Integer, Node>();
		adj = new ArrayList<LinkedList<Integer>>();
	}
	
	/**
	 * Given an OWLObject returns its key in the graph or -1 case it isn't contained by it
	 * @param o
	 * @return int with its key
	 */
	public int getNodeKey(String comp) 
	{
		String compOriginal = comp;
		comp = comp.replaceAll("#", "/");
		comp = comp.replaceAll("<", "");
		comp = comp.replaceAll(">", "");
		
		for(Map.Entry<Integer, Node> n : this.vertices.entrySet())
		{
			String temp = n.getValue().getExpression().toString();
            NodeType t = n.getValue().getNodeType();
            temp = temp.replaceAll("#", "/");
            temp = temp.replaceAll("<", "");
            temp = temp.replaceAll(">", "");
            if(temp.equals(comp))
            {
            	if(t.equals(NodeType.RestrictionCardinality))
            	{
            		NodeRestrictionCardinality nc = (NodeRestrictionCardinality)n.getValue();
            		int card = nc.getCard();
            		String tempS = compOriginal;
            		tempS = tempS.replaceAll("#", "/");
            		int cardO = UsefulOWL.returnCard(tempS.toString());
            		if(cardO==card)
            		{
            			return n.getKey();
            		}
            	}
            	else if(t.equals(NodeType.RestrictionComplementOfRestrictionCardinality))
            	{
            		NodeRestrictionComplementOfRestrictionCardinality nrcc = (NodeRestrictionComplementOfRestrictionCardinality)n.getValue();
            		int card = nrcc.getCard();
            		String tempS = compOriginal;
            		tempS = tempS.replaceAll("#", "/");
            		int cardO = UsefulOWL.returnCard(tempS.toString());
            		if(cardO==card)
            		{
            			return n.getKey();
            		}
            	}
            	else
            	{
            		return n.getKey();
            	}
            }
		}
        return -1;
	}
	
	/**
	 * Returns the first restriction cardinality node that has an expression equals to the one 
	 * being searched ignoring the cardinality
	 * @param comp
	 * @return
	 */
	public int getNodeKeyCRIgnoringCard(String comp) 
	{
		comp = comp.replaceAll("#", "/");
		comp = comp.replaceAll("<", "");
		comp = comp.replaceAll(">", "");
		
		for(Map.Entry<Integer, Node> n : this.vertices.entrySet())
		{
			String temp = n.getValue().getExpression().toString();
            NodeType t = n.getValue().getNodeType();
            temp = temp.replaceAll("#", "/");
            temp = temp.replaceAll("<", "");
            temp = temp.replaceAll(">", "");
            if(temp.equals(comp))
            {
            	if((t.equals(NodeType.RestrictionCardinality)) || (t.equals(NodeType.RestrictionComplementOfRestrictionCardinality)))
            	{
            		return n.getKey();
            	}
            }
		}
        return -1;
	}
	
	/**
	 * Insert new OWLObject node into the graph case it doens't already exists, 
	 * returning its key
	 * 
	 * @param o
	 * @param t
	 * @return node key
	 */
	public int insertNode(String o, NodeType t) 
	{
		int nk = getNodeKey(o);
		if(nk == -1) 
		{
             int id = this.nVertices;
             if(t.equals(NodeType.Class))
             {
            	 if(o.contains("~"))
            	 {
            		 o = o.replaceAll("~", "");
            		 return insertNodeRestrictionComplementOfClass(o, NodeType.RestrictionComplementOfClass);
            	 }
            	 NodeClass n = new NodeClass(id,o);
            	 this.vertices.put(id,n);
            	 this.nVertices++;
             }else if(t.equals(NodeType.Property))
             {
            	 if(o.contains("~"))
            	 {
            		 o = o.replaceAll("~", "");
            		 String tiri = NodeRestrictionComplementOfProperty.getIRI(o);
            		 String tprop = NodeRestrictionComplementOfProperty.getDescriptionProperty(o);
            		 return insertNodeRestrictionComplementOfProperty(tprop,tiri);
            	 }
            	 NodeProperty n = new NodeProperty(id,o);
            	 this.vertices.put(id,n);
            	 this.nVertices++;
             }else if(t.equals(NodeType.RestrictionCardinality))
             {
            	 NodeRestrictionCardinality n = new NodeRestrictionCardinality(id, o);
            	 this.vertices.put(id,n);
            	 this.nVertices++;
             }
             LinkedList<Integer> list = new LinkedList<Integer>();
             list.addFirst(id);
             this.adj.add(list);
             return id;
        }
        return -1;
	}
	
	/**
	 * Tries to insert OWLObject node into the graph case it doens't already exists, 
	 * returning its key
	 * 
	 * @param o
	 * @param t
	 * @return node key
	 */
	public int insertNodeComp(String o, NodeType t) 
	{
		int nk = getNodeKey(o);
		if(nk == -1) 
		{
             int id = this.nVertices;
             if(t.equals(NodeType.Class))
             {
            	 if(o.contains("~"))
            	 {
            		 o = o.replaceAll("~", "");
            		 return insertNodeRestrictionComplementOfClass(o, NodeType.RestrictionComplementOfClass);
            	 }
            	 NodeClass n = new NodeClass(id,o);
            	 this.vertices.put(id,n);
            	 this.nVertices++;
             }else if(t.equals(NodeType.Property))
             {
            	 if(o.contains("~"))
            	 {
            		 o = o.replaceAll("~", "");
            		 String tiri = NodeRestrictionComplementOfProperty.getIRI(o);
            		 String tprop = NodeRestrictionComplementOfProperty.getDescriptionProperty(o);
            		 return insertNodeRestrictionComplementOfProperty(tprop,tiri);
            	 }
            	 NodeProperty n = new NodeProperty(id,o);
            	 this.vertices.put(id,n);
            	 this.nVertices++;
             }else if(t.equals(NodeType.RestrictionCardinality))
             {
            	 NodeRestrictionCardinality n = new NodeRestrictionCardinality(id, o);
            	 this.vertices.put(id,n);
            	 this.nVertices++;
             }
             LinkedList<Integer> list = new LinkedList<Integer>();
             list.addFirst(id);
             this.adj.add(list);
             return id;
        }
        return nk;
	}
	
	/**
	 * Given Restriction Cardinality Node variables returns its key in the graph or -1 case it isn't contained by it
	 * 
	 * @param IRIDomain
	 * @param IRIProp
	 * @param descProp
	 * @param descRangeOrDomain
	 * @param card
	 * @return
	 */
	public int getNodeKeyRestrictionCardinality(String IRIDomain, String IRIProp,String descProp, String descRangeOrDomain, int card ) 
	{
		IRIDomain = IRIDomain.replace("#", "/");
    	IRIProp = IRIProp.replace("#", "/");
    	for(Map.Entry<Integer, Node> n : this.vertices.entrySet())
    	{
    		if(n.getValue().getNodeType().equals(NodeType.RestrictionCardinality))
            {
            	NodeRestrictionCardinality cn = (NodeRestrictionCardinality) n.getValue();
            	if((cn.getCard() == card) && (cn.getDescProp().equals(descProp)) && (cn.getDescRangeOrDomain().equals(descRangeOrDomain))
        			&& (cn.getIRIProp().equals(IRIProp)) && (cn.getIRIDomain().equals(IRIDomain)))
            	{
            		return n.getKey();            		
            	}                
            }
    	}
        return -1;
	}
	
	/**
	 * Insert Restriction Cardinality Node with its variables and not with OWLObject
	 * 
	 * @param IRIDomain
	 * @param IRIProp
	 * @param descProp
	 * @param rangeOrDomain
	 * @param card
	 * @return node key
	 */
	public int insertNodeRestrictionCardinality(String IRIDomain, String IRIProp, String descProp, String descRangeOrDomain, int card )
	{
		if(descProp.equals("owl:Thing"))
        {
			IRIProp = "";
        }
        if(descRangeOrDomain.equals("owl:Thing"))
        {
        	IRIDomain = "";
        }
		int nk = getNodeKeyRestrictionCardinality(IRIDomain,IRIProp,descProp,descRangeOrDomain,card); 
		if(nk == -1) 
		{
			int id = this.nVertices;
			NodeRestrictionCardinality noProp = new NodeRestrictionCardinality(id,IRIDomain,IRIProp,descProp,descRangeOrDomain,card);
            this.vertices.put(id, noProp);
            this.nVertices++;
            LinkedList<Integer> list = new LinkedList<Integer>();
            list.addFirst(id);
            this.adj.add(list);
            return id;
		}
		return -1;
	}
	
	/**
	 * Insert Restriction Cardinality Node with its variables and not with OWLObject
	 * 
	 * @param IRIDomain
	 * @param IRIProp
	 * @param descProp
	 * @param rangeOrDomain
	 * @param card
	 * @return node key
	 */
	public int insertNodeRestrictionCardinalityComp(String IRIDomain, String IRIProp, String descProp, String descRangeOrDomain, int card )
	{
		if(descProp.equals("owl:Thing"))
        {
			IRIProp = "";
        }
        if(descRangeOrDomain.equals("owl:Thing"))
        {
        	IRIDomain = "";
        }
		int nk = getNodeKeyRestrictionCardinality(IRIDomain,IRIProp,descProp,descRangeOrDomain,card); 
		if(nk == -1) 
		{
			int id = this.nVertices;
			NodeRestrictionCardinality noProp = new NodeRestrictionCardinality(id,IRIDomain,IRIProp,descProp,descRangeOrDomain,card);
            this.vertices.put(id, noProp);
            this.nVertices++;
            LinkedList<Integer> list = new LinkedList<Integer>();
            list.addFirst(id);
            this.adj.add(list);
            return id;
		}
		return nk;
	}
	
	/**
	 * Given Property Node variables returns its key in the graph or -1 case it isn't contained by it
	 * @param descIRI
	 * @param descProp
	 * @return
	 */
	public int getNodeKeyProperty(String descIRI, String descProp) 
	{
		for (Map.Entry<Integer, Node> n : this.vertices.entrySet())
		{
			if(n.getValue().getNodeType().equals(NodeType.Property))
            {
            	NodeProperty pn = (NodeProperty) n.getValue();
            	if((pn.getDescProp().equals(descProp)) && (pn.getDescIRI().equals(descIRI)))
            	{
            		return n.getKey();            		
            	}                
            }
		}
        return -1;
	}
	
	/**
	 * Insert Property Node with its variables and not with OWLObject
	 * 
	 * @param IRIDomain
	 * @param IRIProp
	 * @param descProp
	 * @param rangeOrDomain
	 * @param card
	 * @return node key
	 */
	public int insertNodeProperty(String descIRI, String descProp)
	{
		int nk = getNodeKeyProperty(descIRI,descProp);
		if(nk == -1) 
		{
			int id = this.nVertices;
			NodeProperty noProp = new NodeProperty(id, descIRI, descProp);
            this.vertices.put(id, noProp);
            this.nVertices++;
            LinkedList<Integer> list = new LinkedList<Integer>();
            list.addFirst(id);
            this.adj.add(list);
            return id;
		}
		return -1;
	}
	
	/**
	 * Insert Property Node with its variables and not with OWLObject
	 * 
	 * @param IRIDomain
	 * @param IRIProp
	 * @param descProp
	 * @param rangeOrDomain
	 * @param card
	 * @return node key
	 */
	public int insertNodePropertyComp(String descIRI, String descProp)
	{
		int nk = getNodeKeyProperty(descIRI,descProp);
		if(nk == -1) 
		{
			int id = this.nVertices;
			NodeProperty noProp = new NodeProperty(id, descIRI, descProp);
            this.vertices.put(id, noProp);
            this.nVertices++;
            LinkedList<Integer> list = new LinkedList<Integer>();
            list.addFirst(id);
            this.adj.add(list);
            return id;
		}
		return nk;
	}
	
	/**
	 * Given Complement Of Class Node variable returns its key in the graph or -1 case it isn't contained by it
	 * 
	 * @param exp
	 * @return
	 */
	public int getNodeKeyComplementOfClass(String exp) 
	{
		exp = exp.replaceAll("#", "/");
		exp = exp.replaceAll("<", "");
		exp = exp.replaceAll(">", "");
		for (Map.Entry<Integer, Node> n : this.vertices.entrySet())
		{
			if(n.getValue().getNodeType().equals(NodeType.RestrictionComplementOfClass))
            {
            	NodeRestrictionComplementOfClass ncc = (NodeRestrictionComplementOfClass) n.getValue();
            	String temp = ncc.getExpClass().toString();
            	temp = temp.replaceAll("#", "/");
            	temp = temp.replaceAll("<", "");
            	temp = temp.replaceAll(">", "");
            	if(temp.equals(exp))
            	{
            		return n.getKey();            		
            	}                
            }
		}
        return -1;
	}
	
	/**
	 * Insert Complement Of Class Node with its OWLClassExpression and not with OWLObject
	 * 
	 * @param exp
	 * @return node key
	 */
	public int insertNodeRestrictionComplementOfClass(String exp)
	{
		int nk = getNodeKeyComplementOfClass(exp); //compares with class expression, not with expression
		if(nk == -1) 
		{
			int id = this.nVertices;
			NodeRestrictionComplementOfClass ncc = new NodeRestrictionComplementOfClass(id,exp);
            this.vertices.put(id, ncc);
            this.nVertices++;
            LinkedList<Integer> list = new LinkedList<Integer>();
            list.addFirst(id);
            this.adj.add(list);
            return id;
		}
		return -1;
	}
	
	/**
	 * Insert Complement Of Class Node with its OWLClassExpression and not with OWLObject
	 * 
	 * @param exp
	 * @return node key
	 */
	public int insertNodeRestrictionComplementOfClassComp(String exp)
	{
		int nk = getNodeKeyComplementOfClass(exp); //compares with class expression, not with expression
		if(nk == -1) 
		{
			int id = this.nVertices;
			NodeRestrictionComplementOfClass ncc = new NodeRestrictionComplementOfClass(id,exp);
            this.vertices.put(id, ncc);
            this.nVertices++;
            LinkedList<Integer> list = new LinkedList<Integer>();
            list.addFirst(id);
            this.adj.add(list);
            return id;
		}
		return nk;
	}
	
	/**
	 * Insert Complement Of Class Node with its OWLClassExpression(already negated)
	 * 
	 * @param exp
	 * @param t
	 * @return node key
	 */
	public int insertNodeRestrictionComplementOfClass(String exp, NodeType t)
	{
		String tempClassExpression = exp;
		tempClassExpression = tempClassExpression.substring(tempClassExpression.indexOf("<"), tempClassExpression.indexOf(">")+1);
		tempClassExpression = tempClassExpression.replaceAll("#", "/");
		int nk = getNodeKeyComplementOfClass(tempClassExpression);
		if(nk == -1) 
		{
			int id = this.nVertices;
			NodeRestrictionComplementOfClass ncc = new NodeRestrictionComplementOfClass(id,exp,t);
            this.vertices.put(id, ncc);
            this.nVertices++;
            LinkedList<Integer> list = new LinkedList<Integer>();
            list.addFirst(id);
            this.adj.add(list);
            return id;
		}
		return -1;
	}
	
	/**
	 * Given Complement Of Property Node variable returns its key in the graph or -1 case it isn't contained by it
	 * 
	 * @param prop
	 * @return
	 */
	public int getNodeKeyComplementOfProperty(String iri, String prop) 
	{
		for (Map.Entry<Integer, Node> n : this.vertices.entrySet())
		{
			if(n.getValue().getNodeType().equals(NodeType.RestrictionComplementOfProperty))
            {
            	NodeRestrictionComplementOfProperty ncp = (NodeRestrictionComplementOfProperty) n.getValue();
            	if(ncp.getDescProp().equals(prop) && ncp.getDescIRI().equals(iri))
            	{
            		return n.getKey();          		
            	}                
            }
		}
        return -1;
	}
	
	/**
	 * Insert Complement Of Property Node with its variables and not with OWLObject
	 * 
	 * @param prop
	 * @param iri
	 * @return node key
	 */
	public int insertNodeRestrictionComplementOfProperty(String prop, String iri)
	{
		int nk = getNodeKeyComplementOfProperty(iri,prop);
		if(nk == -1) 
		{
			int id = this.nVertices;
			NodeRestrictionComplementOfProperty ncp = new NodeRestrictionComplementOfProperty(id,prop,iri);
            this.vertices.put(id, ncp);
            this.nVertices++;
            LinkedList<Integer> list = new LinkedList<Integer>();
            list.addFirst(id);
            this.adj.add(list);
            return id;
		}
		return -1;
	}
	
	/**
	 * Insert Complement Of Property Node with its variables and not with OWLObject
	 * 
	 * @param prop
	 * @param iri
	 * @return node key
	 */
	public int insertNodeRestrictionComplementOfPropertyComp(String prop, String iri)
	{
		int nk = getNodeKeyComplementOfProperty(iri,prop);
		if(nk == -1) 
		{
			int id = this.nVertices;
			NodeRestrictionComplementOfProperty ncp = new NodeRestrictionComplementOfProperty(id,prop,iri);
            this.vertices.put(id, ncp);
            this.nVertices++;
            LinkedList<Integer> list = new LinkedList<Integer>();
            list.addFirst(id);
            this.adj.add(list);
            return id;
		}
		return nk;
	}

	/**
	 * Given Complement Of Restriction Cardinality Node variables returns its key in the graph or -1 case it isn't contained by it
	 * 
	 * @param prop
	 * @return
	 */
	public int getNodeKeyComplementOfRestrictionCardinality(String IRIDomain,String IRIProp,String descProp,String descRangeOrDomain,int card) 
	{
		IRIDomain = IRIDomain.replaceAll("#", "/");
		IRIProp = IRIProp.replaceAll("#", "/");
		
		for(Map.Entry<Integer, Node> n : this.vertices.entrySet())
		{
			if(n.getValue().getNodeType().equals(NodeType.RestrictionComplementOfRestrictionCardinality))
            {
            	NodeRestrictionComplementOfRestrictionCardinality ncrc = (NodeRestrictionComplementOfRestrictionCardinality) n.getValue();
            	if(ncrc.getDescProp().equals(descProp) && ncrc.getIRIDomain().equals(IRIDomain) && 
    			   ncrc.getiRIProp().equals(IRIProp) && ncrc.getDescRangeOrDomain().equals(descRangeOrDomain) &&
    			   ncrc.getCard() == card)
            	{	
            		return n.getKey();          		
            	}                
            }
		}
		
        return -1;
	}
	
	/**
	 * Insert Complement Of Restriction Cardinality Node extracting its variables from the OWLObject
	 * 
	 * @param prop
	 * @param iri
	 * @return node key
	 */
	public int insertNodeRestrictionComplementOfRestrictionCardinality(String o)
	{
		String desc = o;
		desc = desc.replaceAll("#", "/");
        String iriDomain = UsefulOWL.returnIRIDomain(desc);
        String iriProp = UsefulOWL.returnIRIProp(desc);
        String descProp = UsefulOWL.returnProp(desc);
        String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
        int card = UsefulOWL.returnCard(desc);
        if(descProp.equals("owl:Thing"))
        {
        	iriProp = "";
        }
        if(descRangeOrDomain.equals("owl:Thing"))
        {
        	iriDomain = "";
        }
        int nk = getNodeKeyComplementOfRestrictionCardinality(iriDomain,iriProp,descProp,descRangeOrDomain,card);//getNodeKey(o);
        if(nk == -1) 
        {
           int id = this.nVertices;
           NodeRestrictionComplementOfRestrictionCardinality noProp = new NodeRestrictionComplementOfRestrictionCardinality(id,iriDomain,iriProp,descProp,descRangeOrDomain,card);
           vertices.put(id, noProp);
           this.nVertices++;
           LinkedList<Integer> list = new LinkedList<Integer>();
           list.addFirst(id);
           this.adj.add(list);
           return id;
        }
        return -1;
	}
	
	/**
	 * Insert Complement Of Restriction Cardinality Node extracting its variables from the OWLObject
	 * 
	 * @param prop
	 * @param iri
	 * @return node key
	 */
	public int insertNodeRestrictionComplementOfRestrictionCardinalityComp(String o)
	{
		String desc = o;
		desc = desc.replaceAll("#", "/");
        String iriDomain = UsefulOWL.returnIRIDomain(desc);
        String iriProp = UsefulOWL.returnIRIProp(desc);
        String descProp = UsefulOWL.returnProp(desc);
        String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
        int card = UsefulOWL.returnCard(desc);
        if(descProp.equals("owl:Thing"))
        {
        	iriProp = "";
        }
        if(descRangeOrDomain.equals("owl:Thing"))
        {
        	iriDomain = "";
        }
        int nk = getNodeKeyComplementOfRestrictionCardinality(iriDomain,iriProp,descProp,descRangeOrDomain,card);
        if(nk == -1) 
        {
           int id = this.nVertices;
           NodeRestrictionComplementOfRestrictionCardinality noProp = new NodeRestrictionComplementOfRestrictionCardinality(id,iriDomain,iriProp,descProp,descRangeOrDomain,card);
           vertices.put(id, noProp);
           this.nVertices++;
           LinkedList<Integer> list = new LinkedList<Integer>();
           list.addFirst(id);
           this.adj.add(list);
           return id;
        }
        return nk;
	}
	/**
	 * Insert Complement Of Restriction Cardinality Node extracting its variables from the OWLObject using 
	 * an Max Cardinality Object
	 * 
	 * @param prop
	 * @param iri
	 * @return node key
	 */
	public int insertNodeRestrictionComplementOfRestrictionCardinalityFromMax(String o)
	{
		String desc = o;
		desc = desc.replaceAll("#", "/");
        String iriDomain = UsefulOWL.returnIRIDomain(desc);
        String iriProp = UsefulOWL.returnIRIProp(desc);
        String descProp = UsefulOWL.returnProp(desc);
        String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
        int card = UsefulOWL.returnCard(desc);
        if(descProp.equals("owl:Thing"))
        {
        	iriProp = "";
        }
        if(descRangeOrDomain.equals("owl:Thing"))
        {
        	iriDomain = "";
        }
        card++;
        int nk = getNodeKeyComplementOfRestrictionCardinality(iriDomain,iriProp,descProp,descRangeOrDomain,card);
        if(nk == -1) 
        {
           int id = this.nVertices;
           NodeRestrictionComplementOfRestrictionCardinality noProp = new NodeRestrictionComplementOfRestrictionCardinality(id,iriDomain,iriProp,descProp,descRangeOrDomain,card);
           vertices.put(id, noProp);
           this.nVertices++;
           LinkedList<Integer> list = new LinkedList<Integer>();
           list.addFirst(id);
           this.adj.add(list);
           return id;
        }
        return -1;
	}
	
	
	/**
	 * Returns a list of all nodes from a given graph that are not Complements(negated)
	 * 
	 * @param g
	 * @return list of Nodes that are not Complements
	 * 
	 */
	@SuppressWarnings({"incomplete-switch", "rawtypes" })
	public ArrayList<List> getNodesWithoutNot(Graph g)
    {
        ArrayList<List> NodesWithoutNot = new ArrayList<List>();
        ArrayList<NodeClass> nodesClassWithoutNot = new ArrayList<NodeClass>();
        ArrayList<NodeProperty> nodesPropertyWithoutNot = new ArrayList<NodeProperty>();
        ArrayList<NodeRestrictionCardinality> nodesRCWithoutNot = new ArrayList<NodeRestrictionCardinality>();
        
        for (Map.Entry<Integer, Node> n : g.getVertices().entrySet())
        {
        	NodeType nt = n.getValue().getNodeType();
        	switch(nt)
        	{
        		case Class: 
        			nodesClassWithoutNot.add((NodeClass) n.getValue()); 
        			break;
                case Property: 
                	nodesPropertyWithoutNot.add((NodeProperty) n.getValue()); 
                	break;
                case RestrictionCardinality:
                	nodesRCWithoutNot.add((NodeRestrictionCardinality) n.getValue()); 
                	break;
            }
        }
        NodesWithoutNot.add(nodesClassWithoutNot);
        NodesWithoutNot.add(nodesPropertyWithoutNot);
        NodesWithoutNot.add(nodesRCWithoutNot);
        return NodesWithoutNot;
    }
	
	/**
	 * Returns a list of all nodes with tag nodeBottom true
	 * 
	 * @param g
	 * @return
	 */
	public static ArrayList<Node> getNodesBottom(Graph g)
    {
		ArrayList<Node> resp = new ArrayList<Node>();
        for (Map.Entry<Integer, Node> n : g.getVertices().entrySet())
        {
        	Node nv = n.getValue();
        	if(nv.isNodeBottom())
        	{
        		resp.add(nv.getNode());
        	}
        }
		return resp;
    }
	
	/**
	 * Returns a list of all nodes from a given graph that are Complements and have the tag Bottom true
	 * 
	 * @param g
	 * @return list of Nodes that are Complements
	 * @throws Exception
	 */	
	@SuppressWarnings({"rawtypes", "incomplete-switch" })
	public ArrayList<List> getNegatedBottomNodes(Graph g)
	{
		ArrayList<List> NodesWithNot = new ArrayList<List>();
        ArrayList<NodeRestrictionComplementOfClass> nodesClassWithNot = new ArrayList<NodeRestrictionComplementOfClass>();
        ArrayList<NodeRestrictionComplementOfProperty> nodesPropertyWithNot = new ArrayList<NodeRestrictionComplementOfProperty>();
        ArrayList<NodeRestrictionComplementOfRestrictionCardinality> nodesRCWithNot = new ArrayList<NodeRestrictionComplementOfRestrictionCardinality>();
        for (Map.Entry<Integer, Node> n : g.getVertices().entrySet())
        {
            if(n.getValue().isNodeBottom())
            {
            	NodeType t = n.getValue().getNodeType();
                switch(t)
                {
                    case RestrictionComplementOfClass:
                    	nodesClassWithNot.add((NodeRestrictionComplementOfClass) n.getValue());
                        break;
                    case RestrictionComplementOfProperty :
                    	nodesPropertyWithNot.add((NodeRestrictionComplementOfProperty) n.getValue());
                        break;
                    case RestrictionComplementOfRestrictionCardinality :
                    	nodesRCWithNot.add((NodeRestrictionComplementOfRestrictionCardinality) n.getValue());
                        break;
                }
            }
        }
        NodesWithNot.add(nodesClassWithNot);
        NodesWithNot.add(nodesPropertyWithNot);
        NodesWithNot.add(nodesRCWithNot);
        return NodesWithNot;
	}
	
	
	/**
	 * Returns a list of all Nodes Property that have the tag Bottom true
	 * @param g
	 * @return
	 */
	public ArrayList<NodeProperty> getNodeBottomPropertyGraph() 
	{
		ArrayList<NodeProperty> l = new ArrayList<NodeProperty>();
        for (Map.Entry<Integer, Node> n : this.vertices.entrySet())
        {
           if((n.getValue().getNodeType().equals(NodeType.Property)) && (n.getValue().isNodeBottom()))
           {
        	   l.add((NodeProperty)n.getValue());
    	   }
        }            
    	return l;
     }

	/**
	 * Search the graph for a Node Property that has the tag Bottom true, returns the node case it is found or null otherwise
	 * 
	 * @param descProp
	 * @param descIRI
	 * @return
	 */
    public NodeProperty isNodeBottomPropertyInGraph(String descProp, String descIRI) 
    {
        for (Map.Entry<Integer, Node> n : this.vertices.entrySet())
        {
        	if (n.getValue().getNodeType().equals(NodeType.Property) && n.getValue().isNodeBottom())
        	{
        		NodeProperty np = (NodeProperty) n.getValue();
        		if(np.getDescIRI().equals(descIRI) && np.getDescProp().equals(descProp))
        		{
        			return np;
                }
            }
        }
    	return null;
	}

    /**
	 * Search a Node Restriction Cardinality that has the tag Bottom true, returns the node case it is found or null otherwise
     * 
     * @param descProp
     * @param IRIDomain
     * @return
     */
    public NodeRestrictionCardinality isNodeBottomRestrictionCardinalityInGraph(String descProp, String IRIProp) 
    {
		for (Map.Entry<Integer, Node> n : this.vertices.entrySet())
        {
        	if(n.getValue().getNodeType().equals(NodeType.RestrictionCardinality) && n.getValue().isNodeBottom())
        	{
        		NodeRestrictionCardinality nrc = (NodeRestrictionCardinality) n.getValue();
        		if((nrc.getIRIProp().equals(IRIProp)) && (nrc.getDescProp().equals(descProp)))
        		{
                      return nrc;
                }
            }
        }
    	return null;
     }


    /**
	 * Returns a list of all Nodes Restriction Cardinality that have the tag Bottom true
	 * 	 
	 * @return 
	 */
     public ArrayList<NodeRestrictionCardinality> getNodeBottomRestrictionCardGrafo()
     {
    	 ArrayList<NodeRestrictionCardinality> l = new ArrayList<NodeRestrictionCardinality>();
    	 for (Map.Entry<Integer, Node> n : this.vertices.entrySet())
         {
             if(n.getValue().getNodeType().equals(NodeType.RestrictionCardinality))	
             {
            	 if(n.getValue().isNodeBottom())
            	 {
            		 l.add((NodeRestrictionCardinality)n.getValue());
        		 }
        	 }
         }
         return l;
     }
     
     /**
      * Checks if Adj list is empty for the node with key v
      * 
      * @param v
      * @return
      */
     public boolean isAdjEmpty(int v) 
     {
    	 if(this.adj.get(v) != null)
    	 {
    		 LinkedList<Integer> e = this.adj.get(v);
    		 if (e.size() == 1)
    		 {
    			 return true;
			 }
		 }
    	 return false;
	 }
     
     /**
      * Search the graph for an edge between two OWLObjects, returning true case it does and false case it doesn't
      * 
      * @param o origin
      * @param d destiny
      * @return 
      */
     public boolean findEdge(String o, String d)
     {
    	 int idO = getNodeKey(o);
    	 int idD = getNodeKey(d);
    	 if((idO >= 0) && (idD >= 0) && (idO < this.adj.size())) 
    	 {
    		 LinkedList<Integer> e = this.adj.get(idO);
    		 if((e != null) && (e.getFirst() == idO))
			 {
				 for(int i : e)
				 {
					 if(i == idD)
					 {
						 return true;
					 }
				 }
			 }    		 
    	 }
    	 return false;
	 }
     
     /**
      * Creates an edge between two OWLObjects, case it doesn't already exists 
      * and the OWLObjects are valid nodes in the graph 
      * 
      * @param o origin
      * @param d destiny
      */
     public void insertEdge(String o, String d) 
     {
    	 int idO = getNodeKey(o);
    	 int idD = getNodeKey(d);
    	 //check if edge exists
    	 if(!findEdge(o, d)) 
    	 {
    		 //check if nodes exist
        	 if((idO >= 0) && (idD >= 0) && (idO < this.adj.size()) && (idD < this.adj.size())) 
    		 {
    			 LinkedList<Integer> e = this.adj.get(idO);
        		 if((e != null) && (e.getFirst() == idO))
    			 {
        			 e.addLast(idD);
					 return;
				 }
			 }
		 }
	 }
     
     /**
      * Returns the Adjacent list in this graph for the node with id == nId
      * 
      * @param nId
      * @return
      */
     public LinkedList<Integer> getList(int nId)
 	 {
    	 LinkedList<Integer> e = this.adj.get(nId);
    	 if((e != null) && (!e.isEmpty())) 
    	 {
 			return e;
		 }
    	 return null;
 	 }
     
     /**
      * Returns the first edge from a node with id nId
      * 
      * @param nId
      * @return
      */
     public Edge getFirstEdge(int nId) 
     {
    	 LinkedList<Integer> e = this.adj.get(nId);
    	 if((e != null) && (!e.isEmpty())) 
    	 {
 			int e1 = e.get(1);
 			return new Edge(nId,e1);
		 }
    	 return null;
 	 }
     
 	/**
 	 * Gets Edge form iterator, will be used to get edges from adj list
 	 * 
 	 * @param v
 	 * @param it
 	 * @return
 	 */
     public Edge proxAdj(int v, Iterator<Integer> it)
     {
		if (it.hasNext())
		{
			int i = it.next();
			return new Edge(v,i);
		}
		else
		{
			return null;
		}
 	}
     
     /**
 	 *  Given a list and a node returns an adjacency list from that node and that list
 	 *  getListaAdjacencia e getListaAdjacenciaProjection
 	 *  
 	 * @param list
 	 * @param v
 	 * @return
 	 */
 	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<LinkedList<Integer>> getAdjListFromList(ArrayList<LinkedList<Integer>> list, HashMap<Integer,Node> v)
 	{
 		ArrayList<LinkedList<Integer>> nl = new ArrayList<LinkedList<Integer>>();
 		ArrayList<Integer> key = new ArrayList(v.keySet());
 		Collections.sort(key);
 		for(Integer i: key)
 		{
 			LinkedList<Integer> l = new LinkedList<Integer>();
 			for(LinkedList<Integer> elem: list)
 			{
 				if(!elem.isEmpty())
 				{
 					if(elem.get(0).equals(i.intValue()))
 					{
 						l = elem;
 						break;
 					}
 				}
 			}
 			nl.add(l);
 		}
 		return nl;
 	}
     
	
	
	/**
	 * Change the level of a node in a given Graph to a given value
	 * 
	 * @param g
	 * @param n
	 * @param id
	 * @return
	 */
 	public static Graph updateNodeLevel(Graph g, int nl, int id) 
 	{
 		for (Map.Entry<Integer, Node> n : g.getVertices().entrySet())
        {
 			if(n.getKey() == id)
 			{
 				n.getValue().setLevel(nl);
 				break;
			}
 		}
 		return g;
    }
 	
 	/**
 	 * Search the Graph for the node n and returns its key if found or -1 if not
 	 * @param n
 	 * @return
 	 */
	public static int findNodeKey(String IRIn2f, Node n2f, String IRIg, Graph g)
	{
		/*
		NodeType type = n2f.getNodeType();
		int retID = g.getNodeKey(n2f.getExpression());
		if(retID > -1)
		{
			return retID;
		}
		if((type == NodeType.RestrictionCardinality)||(type == NodeType.RestrictionComplementOfRestrictionCardinality))
        {
			retID = g.getNodeKeyCRIgnoringCard(n2f.getExpression());
			return retID;
        }
		*/
		HashMap<Integer,Node> vertices = g.getVertices();
		NodeType type = n2f.getNodeType();
		for(Map.Entry<Integer, Node> temp : vertices.entrySet())
		{
            Node n = temp.getValue();
            if(n.getNodeType().toString().equals(type.toString()))
            {
            	switch(type)
            	{
				   case Class :
				       NodeClass nc2f = (NodeClass)n2f;
				       NodeClass nc = (NodeClass)n;
				       if((nc.getDescClass().equals(nc2f.getDescClass())))
				       {
				    	   if((nc.getDescIRI().equals(nc2f.getDescIRI())))
				    	   {
				    		   return nc.getId();
				    	   }
				    	   else if((nc.getDescIRI().equals(IRIn2f)) || 
			    			  (nc2f.getDescIRI().equals(IRIg)))
				    	   {
				    		   return nc.getId();
				    	   }
				       }
				       break;
				   case Property :
				       NodeProperty np2f = (NodeProperty)n2f;
				       NodeProperty np = (NodeProperty)n;
				       if(np.getDescProp().equals(np2f.getDescProp()))
				       {
				    	   
				    	   if((np.getDescIRI().equals(np2f.getDescIRI())))
				    	   {
				    		   return np.getId();
				    	   }
				    	   else if((np.getDescIRI().equals(IRIn2f)) || 
			    			  (np2f.getDescIRI().equals(IRIg)))
				    	   {
				    		   return np.getId();
				    	   }
				       }
				       break;
				   case RestrictionCardinality :
					   NodeRestrictionCardinality nrc2f = (NodeRestrictionCardinality)n2f;
					   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n;
					   if((nrc.getIRIProp().equals(nrc2f.getIRIProp()))&&(nrc.getDescProp().equals(nrc2f.getDescProp()))&&
					   (nrc.getIRIDomain().equals(nrc2f.getIRIDomain()))&&(nrc.getDescRangeOrDomain().equals(nrc2f.getDescRangeOrDomain()))
					   &&(nrc.getCard() == nrc2f.getCard()))
					   {
						   return nrc.getId();
					   }
				       break;
				   case RestrictionComplementOfClass :
					   NodeRestrictionComplementOfClass notClass2f = (NodeRestrictionComplementOfClass)n2f;
					   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n;
				       if(notClass.getDescClass().equals(notClass2f.getDescClass()))
				       {
				    	   
				    	   if((notClass.getDescIRI().equals(notClass2f.getDescIRI())))
			    		   {
				    		   return notClass.getId();
			    		   }
				    	   else if((notClass.getDescIRI().equals(IRIn2f)) || 
			    				   (notClass2f.getDescIRI().equals(IRIg)))
				    	   {
				    		   return notClass.getId();
				    	   }
				       }
				       break;
				   case RestrictionComplementOfProperty :
					   NodeRestrictionComplementOfProperty notProp2f = (NodeRestrictionComplementOfProperty)n2f;
					   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n;
					   if(notProp.getDescProp().equals(notProp2f.getDescProp()))
				       {
						   if((notProp.getDescIRI().equals(notProp2f.getDescIRI())))
						   {
							   return notProp.getId();
						   }
						   else if ((notProp.getDescIRI().equals(IRIn2f)) || 
			    			  (notProp2f.getDescIRI().equals(IRIg)))
				    	   {
							   return notProp.getId();
				    	   }
				       }
				       break;
				   case RestrictionComplementOfRestrictionCardinality :
					   NodeRestrictionComplementOfRestrictionCardinality notRestCard2f = (NodeRestrictionComplementOfRestrictionCardinality)n2f;
					   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n;
					   if((notRestCard.getiRIProp().equals(notRestCard2f.getiRIProp()))&&(notRestCard.getDescProp().equals(notRestCard2f.getDescProp()))&&
					   (notRestCard.getIRIDomain().equals(notRestCard2f.getIRIDomain()))&&(notRestCard.getDescRangeOrDomain().equals(notRestCard2f.getDescRangeOrDomain()))
					   &&(notRestCard.getCard() == notRestCard2f.getCard()))
					   {
						   return notRestCard.getId();
					   }
				       break;
				   default: 
					   break;
			    }
            }
        }
        if((type == NodeType.RestrictionCardinality)||(type == NodeType.RestrictionComplementOfRestrictionCardinality))
        {
        	for(Map.Entry<Integer, Node> temp : vertices.entrySet())
    		{
                Node n = temp.getValue();
                if(n.getNodeType().toString().equals(type.toString()))
                {
                	switch(type)
                	{
	                	case RestrictionCardinality :
	 					   NodeRestrictionCardinality nrc2f = (NodeRestrictionCardinality)n2f;
	 					   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n;
	 					   if((nrc.getIRIProp().equals(nrc2f.getIRIProp()))&&(nrc.getDescProp().equals(nrc2f.getDescProp()))&&
						   (nrc.getIRIDomain().equals(nrc2f.getIRIDomain()))&&(nrc.getDescRangeOrDomain().equals(nrc2f.getDescRangeOrDomain())))
	 					   {
	 						   return nrc.getId();
	 					   }
	 				       break;
	                	case RestrictionComplementOfRestrictionCardinality :
	 					   NodeRestrictionComplementOfRestrictionCardinality notRestCard2f = (NodeRestrictionComplementOfRestrictionCardinality)n2f;
	 					   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n;
	 					   if((notRestCard.getiRIProp().equals(notRestCard2f.getiRIProp()))&&(notRestCard.getDescProp().equals(notRestCard2f.getDescProp()))&&
						   (notRestCard.getIRIDomain().equals(notRestCard2f.getIRIDomain()))&&(notRestCard.getDescRangeOrDomain().equals(notRestCard2f.getDescRangeOrDomain())))
	 					   {
	 						   return notRestCard.getId();
	 					   }
	 				       break;
	 				   default: 
	 					   break;
                	}
                }
            }
        }
        return -1;
	}
	
	
	/**
	 * Modified version for difference procedure
	 * 
	 * @param IRIn2f
	 * @param n2f
	 * @param IRIg
	 * @param g
	 * @return
	 */
	public static int findNodeKeyDifference(String IRIn2f, Node n2f, String IRIg, Graph g)
	{
		/*
		int retID = g.getNodeKey(n2f.getExpression());
		return retID;
		*/
 		NodeType type = n2f.getNodeType();
		HashMap<Integer,Node> vertices = g.getVertices();
		for(Map.Entry<Integer, Node> temp : vertices.entrySet())
		{
            Node n = temp.getValue();
            if(n.getNodeType().toString().equals(type.toString()))
            {
            	switch(type)
            	{
				   case Class :
				       NodeClass nc2f = (NodeClass)n2f;
				       NodeClass nc = (NodeClass)n;
				       if((nc.getDescClass().equals(nc2f.getDescClass())))
				       {
				    	   if((nc.getDescIRI().equals(nc2f.getDescIRI())))
				    	   {
				    		   return nc.getId();
				    	   }
				    	   else if((nc.getDescIRI().equals(IRIn2f)) || 
			    			  (nc2f.getDescIRI().equals(IRIg)))
				    	   {
				    		   return nc.getId();
				    	   }
				       }
				       break;
				   case Property :
				       NodeProperty np2f = (NodeProperty)n2f;
				       NodeProperty np = (NodeProperty)n;
				       if(np.getDescProp().equals(np2f.getDescProp()))
				       {
				    	   
				    	   if((np.getDescIRI().equals(np2f.getDescIRI())))
				    	   {
				    		   return np.getId();
				    	   }
				    	   else if((np.getDescIRI().equals(IRIn2f)) || 
			    			  (np2f.getDescIRI().equals(IRIg)))
				    	   {
				    		   return np.getId();
				    	   }
				       }
				       break;
				   case RestrictionCardinality :
					   NodeRestrictionCardinality nrc2f = (NodeRestrictionCardinality)n2f;
					   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n;
					   if((nrc.getIRIProp().equals(nrc2f.getIRIProp()))&&(nrc.getDescProp().equals(nrc2f.getDescProp()))&&
					   (nrc.getIRIDomain().equals(nrc2f.getIRIDomain()))&&(nrc.getDescRangeOrDomain().equals(nrc2f.getDescRangeOrDomain()))
					   &&(nrc.getCard() == nrc2f.getCard()))
					   {
						   return nrc.getId();
					   }
				       break;
				   case RestrictionComplementOfClass :
					   NodeRestrictionComplementOfClass notClass2f = (NodeRestrictionComplementOfClass)n2f;
					   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n;
				       if(notClass.getDescClass().equals(notClass2f.getDescClass()))
				       {
				    	   
				    	   if((notClass.getDescIRI().equals(notClass2f.getDescIRI())))
			    		   {
				    		   return notClass.getId();
			    		   }
				    	   else if((notClass.getDescIRI().equals(IRIn2f)) || 
			    				   (notClass2f.getDescIRI().equals(IRIg)))
				    	   {
				    		   return notClass.getId();
				    	   }
				       }
				       break;
				   case RestrictionComplementOfProperty :
					   NodeRestrictionComplementOfProperty notProp2f = (NodeRestrictionComplementOfProperty)n2f;
					   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n;
					   if(notProp.getDescProp().equals(notProp2f.getDescProp()))
				       {
						   if((notProp.getDescIRI().equals(notProp2f.getDescIRI())))
						   {
							   return notProp.getId();
						   }
						   else if ((notProp.getDescIRI().equals(IRIn2f)) || 
			    			  (notProp2f.getDescIRI().equals(IRIg)))
				    	   {
							   return notProp.getId();
				    	   }
				       }
				       break;
				   case RestrictionComplementOfRestrictionCardinality :
					   NodeRestrictionComplementOfRestrictionCardinality notRestCard2f = (NodeRestrictionComplementOfRestrictionCardinality)n2f;
					   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n;
					   if((notRestCard.getiRIProp().equals(notRestCard2f.getiRIProp()))&&(notRestCard.getDescProp().equals(notRestCard2f.getDescProp()))&&
					   (notRestCard.getIRIDomain().equals(notRestCard2f.getIRIDomain()))&&(notRestCard.getDescRangeOrDomain().equals(notRestCard2f.getDescRangeOrDomain()))
					   &&(notRestCard.getCard() == notRestCard2f.getCard()))
					   {
						   return notRestCard.getId();
					   }
				       break;
				   default: 
					   break;
			    }
            }
        }
        return -1;
	}
	
	
	/**
	 * Searches Graph g for a Restriction Cardinality node with the same property and domain but
	 * with different cardinality than the one given
	 * 
	 * @param IRIn2f
	 * @param n2f
	 * @param IRIg
	 * @param g
	 * @return
	 */
	public static int findDifferentNodeRCKey(Node n2f, Graph g)
	{
		NodeType type = n2f.getNodeType();
		if((type !=  NodeType.RestrictionCardinality)&&(type !=  NodeType.RestrictionComplementOfRestrictionCardinality))
		{
			return -1;
		}		
		HashMap<Integer,Node> vertices = g.getVertices();
		for(Map.Entry<Integer, Node> temp : vertices.entrySet())
		{
            Node n = temp.getValue();
            if(n.getNodeType().toString().equals(type.toString()))
            {
            	switch(type)
            	{
				   case RestrictionCardinality :
					   NodeRestrictionCardinality nrc2f = (NodeRestrictionCardinality)n2f;
					   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n;
					   if((nrc.getIRIProp().equals(nrc2f.getIRIProp()))&&(nrc.getDescProp().equals(nrc2f.getDescProp()))&&
					   (nrc.getIRIDomain().equals(nrc2f.getIRIDomain()))&&(nrc.getDescRangeOrDomain().equals(nrc2f.getDescRangeOrDomain()))&&(nrc.getCard() != nrc2f.getCard()))
					   {
						   return nrc.getId();
					   }
				       break;
				   case RestrictionComplementOfRestrictionCardinality :
					   NodeRestrictionComplementOfRestrictionCardinality notRestCard2f = (NodeRestrictionComplementOfRestrictionCardinality)n2f;
					   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n;
					   if((notRestCard.getiRIProp().equals(notRestCard2f.getiRIProp()))&&(notRestCard.getDescProp().equals(notRestCard2f.getDescProp()))&&
					   (notRestCard.getIRIDomain().equals(notRestCard2f.getIRIDomain()))&&(notRestCard.getDescRangeOrDomain().equals(notRestCard2f.getDescRangeOrDomain()))&&(notRestCard.getCard() != notRestCard2f.getCard()))
					   {
						   return notRestCard.getId();
					   }
				       break;
				   default: 
					   break;
			    }
            }
        }
        return -1;
	}
	
	//basic gets and sets
	public int getNumVertices(){
		return this.nVertices;
	}
	public void setNumVertices(int n){
		this.nVertices = n;
	}	
	public HashMap<Integer, Node> getVertices(){
		return this.vertices;
	}
	public void setVertices(HashMap<Integer, Node> v){
		this.vertices = v;
	}	
	public ArrayList<LinkedList<Integer>> getAdj(){
		return adj;
	}
	public void setAdj(ArrayList<LinkedList<Integer>> a){
		this.adj = a;
	}	
	
	/**
	 * Implements Edges between nodes of a Graph
	 *
	 * 
	 *
	 */
	public static class Edge {
		private int idNode1, idNode2;

		public Edge(int id1, int id2) {
			this.idNode1 = id1;
			this.idNode2 = id2;
		}

		public int idNode1() {
			return this.idNode1;
		}

		public int idNode2() {
			return this.idNode2;
		}

	}
	
	/**
	 * Simple comparator for nodes
	 *
	 * 
	 *
	 */
	public class VerticeComparator implements Comparator<Node> 
	{
		public int compare(Node no1, Node no2) 
		{
			if(no1.getId() < no2.getId()) 
			{
				return -1;
            }
            if(no1.getId() > no2.getId())
            {
            	return 1;
            } 
            return 0;
		}
	}
}
