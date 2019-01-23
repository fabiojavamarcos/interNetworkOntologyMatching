package Ontology;

import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Implements Nodes from OWL Graph
 * 
 * @author Romulo de Carvalho Magalhaes
 *
 */

public class Node {

	protected ArrayList<String> expressions ;
	protected int id;
	protected NodeType type;
	protected boolean Top;   	//is top node
	protected boolean Bottom;	//is bottom node
	protected int level;		//used for bfs later
	protected boolean negation;	//if it is a not "~"
	protected boolean remove;	//if it is to be removed
    
    /**
     * Basic constructor, just to initialize it
     */
    public Node()
    {    	
    }
	
    /**
     * Initialize generic node from ID and OWLObject
     * @param id
     * @param o
     */
	public Node(int id, OWLObject o)
	{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		String tempC = o.toString();
		tempC= tempC.replaceAll("#","/");
		IRI tempI = IRI.create(tempC);
		OWLObject o1 = factory.getOWLClass(tempI);
		expressions = new ArrayList<String>();
		expressions.add(o1.toString());
		this.id = id;
		this.Bottom = false;	   
		this.level = -1;
		this.negation = false;
		this.remove = false;		
	}
	
	/**
	 * Initializes node from ID, with expressions list and a type
	 * @param id
	 * @param l
	 * @param t
	 */
	public Node(int id, ArrayList<OWLObject> l, NodeType t)
	{
		expressions = new ArrayList<String>();
		for(OWLObject o : l)
		{
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory factory = manager.getOWLDataFactory();
			String tempC = o.toString();
			tempC= tempC.replaceAll("#","/");
			IRI tempI = IRI.create(tempC);
			OWLObject o1 = factory.getOWLClass(tempI);
			this.expressions.add(o1.toString());
		}	   
		this.id = id;
		this.type = t;
		this.Bottom = false;
		this.level = -1;
		this.negation = false;
		this.remove = false;		
	}
	
	//basic gets and sets
	public ArrayList<String> getExpressions() {
		return this.expressions;
	}	
	public void setExpressions(ArrayList<OWLObject> expressions) {
		this.expressions = new ArrayList<String>();
		for(OWLObject o : expressions)
		{
			this.expressions.add(o.toString());
		}
	}
	public String getExpression() {
		return this.expressions.get(0);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public NodeType getNodeType() {
		return type;
	}
	public void setNodeType(NodeType t) {
		this.type = t;
	}
	public boolean isNodeTop() {
		return Top;
    }
	public void setNodeTop(boolean n) {
		this.Top = n;
	}
	public boolean isNodeBottom() {
		return Bottom;
    }
	public void setNodeBottom(boolean n) {
		this.Bottom = n;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int l) {
		this.level = l;
	}
	public boolean isNegation() {
		return negation;
    }
	public void setNegation(boolean n) {
		this.negation = n;
	}
	public boolean isRemove() {
		return remove;
    }
	public void setRemove(boolean r) {
		this.remove = r;
	}
	public Node getNode() {
		return this;
	}
	public String getFullName(){
    	String resp = "";
    	resp = this.getExpression().toString();
    	resp = resp.substring(1, resp.length()-1);
    	return resp;
    }
	
	/**
	 * Search for object in the Node expressions list
	 * 
	 * @param obj
	 * @return
	 */
	public boolean equals(OWLObject obj) 
	{
	    for(String o : this.expressions)
	    {
	       if(o == obj.toString()) 
	       {
	    	   return true;
	       }	
	    }
		return false;
	}
}
