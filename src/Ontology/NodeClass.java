package Ontology;

import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Specialization of the Node Class to represent OWL Class 
 * 
 * @author Romulo de Carvalho Magalhaes
 *
 */
public class NodeClass extends Node{

	private IRI iri;
    private String descClass;
    private String descIRI;
    
    /**
     * Initializes Class Node from OWLObject
     * 
     * @param o
     */
	public NodeClass(String ce)
    {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		String tempC = ce;
		tempC = tempC.replaceAll("#","/");
		tempC = tempC.replaceAll("<", "");
		tempC = tempC.replaceAll(">", "");
		IRI tempI = IRI.create(tempC);
		OWLObject o = factory.getOWLClass(tempI);
		expressions = new ArrayList<String>();
		expressions.add(o.toString());
		this.descIRI = getIRI(expressions.get(0).toString());
		this.descClass = getDescriptionClass(expressions.get(0).toString());
		this.iri = IRI.create(this.descIRI);
		this.Bottom = false;
		this.Top = false;
		this.type = NodeType.Class;
		this.negation = false;
		this.level = -1;
		this.remove = false;
    }
	
	/**
     * Initializes Class Node from OWLObject and ID
	 * 
	 * @param id
	 * @param o
	 */
	public NodeClass(int id, String ce)
    {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		String tempC = ce;
		tempC = tempC.replaceAll("#","/");
		tempC = tempC.replaceAll("<", "");
		tempC = tempC.replaceAll(">", "");
		IRI tempI = IRI.create(tempC);
		OWLObject o = factory.getOWLClass(tempI);
		expressions = new ArrayList<String>();
		expressions.add(o.toString());
		this.id = id;
		this.descIRI = getIRI(expressions.get(0).toString());
		this.descClass = getDescriptionClass(expressions.get(0).toString());
		this.iri = IRI.create(this.descIRI);
		this.Bottom = false;
		this.type = NodeType.Class;
		this.negation = false;
		this.level = -1;
		this.remove = false;
    }
	
	/**
     * Initializes Class Node from Class description and IRI
     * 
	 * @param cs
	 * @param is
	 */
	public  NodeClass (String cs ,String is)
	{
		this.expressions = new ArrayList<String>();
		this.descIRI = is;
		this.descClass = cs;
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		String temp = is.concat(cs);
		temp= temp.replaceAll("#","/");
		temp = temp.replaceAll("<", "");
		temp = temp.replaceAll(">", "");
		IRI i = IRI.create(temp);
		OWLObject o = factory.getOWLClass(i);
		this.expressions.add(o.toString());
	}
	
	//basic gets and sets
	public String getDescClass() {
        return descClass;
    }
	public void setDescClass(String s) {
        this.descClass = s;
    }
    public String getDescIRI() {
        return descIRI;
    }
    public void setDescIRI(String s) {
        this.descIRI = s;
    }
    public IRI getIri() {
        return iri;
    }
    public void setIri(IRI i) {
        this.iri = i;
    }
    
    /**
     * Get Clean IRI from OWLObject expression string
     * 
	 * @param s
	 * @return
	 */
	public String getIRI(String s)
	{
		String temp = s;
		if(s.contains("rdf:")||s.contains("RDF:"))
		{
			return "";
		}
        if(s.contains("<"))
        {
        	if(s.contains("#"))
        	{
        		temp = s.substring(s.indexOf("<")+1,s.lastIndexOf("#"));
        		temp = temp + "/";
            }else 
            {
            	if(s.contains(":") && !s.contains("/"))
            	{
            		temp = s.substring(s.indexOf("<")+1,s.lastIndexOf(":"));
            		temp = temp + "/";
        		}else 
        		{
        			temp = s.substring(s.indexOf("<")+1,s.lastIndexOf("/"));
        			temp = temp + "/";
    			}
            }
    	}
        if(temp.contains(")"))
        {
        	temp = temp.substring(0,temp.indexOf(")")-1);
    	}
        return temp;
    }
	
	/**
     * Get clean Class Description from OWLObject expression string
	 * 
	 * @param s
	 * @return
	 */
	public String getDescriptionClass(String s)
	{
		if(s.contains("rdf:")||s.contains("RDF:"))
		{
			String temp = s;
			temp = temp.replaceAll("<", "");
			temp = temp.replaceAll(">", "");
			return temp;
		}
		if(s.contains("#"))
		{
           return s.substring(s.indexOf("#") + 1, s.indexOf(">"));
        }
		return s.substring(s.lastIndexOf("/") + 1, s.indexOf(">"));
	}
}
