package Ontology;

import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLOntologyManager;
//import org.semanticweb.owlapi.model.PrefixManager;
//import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Specialization of the Node Class to represent the complement of OWL Classes
 * 
 * @author Romulo de Carvalho Magalhaes
 *
 */
public class NodeRestrictionComplementOfClass extends Node{

	public String descClass;
    public String descIRI;
    public OWLClassExpression classExpression;
    
    /**
     * Initializes Node from OWLClassExpression not negated and ID
     * 
     * @param id
     * @param ce
     */
	public NodeRestrictionComplementOfClass(int id,String ce) 
	{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();		
		this.id = id;
		String tempC = ce;
		tempC = tempC.replaceAll("#","/");
		tempC = tempC.replaceAll("<", "");
		tempC = tempC.replaceAll(">", "");
		IRI tempI = IRI.create(tempC);
		OWLObject o = factory.getOWLClass(tempI);
		this.classExpression = (OWLClassExpression) o;
		OWLObjectComplementOf complementOf = factory.getOWLObjectComplementOf(this.classExpression);
		this.descIRI = getIRI(ce.toString());
		this.descClass = getDescriptionClass(ce.toString());
		this.Bottom = false;
		this.Top = false;
		this.type = NodeType.RestrictionComplementOfClass;
		this.negation = true;
		this.level = -1;
		this.remove = false;
		expressions = new ArrayList<String>();
		expressions.add(complementOf.toString());
	}

	/**
     * Initializes Node from OWLClassExpression(already negated) and ID, using Node type
	 * 
	 * @param id
	 * @param ce
	 * @param type
	 */
	public NodeRestrictionComplementOfClass(int id,String ce, NodeType type) 
	{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		String temp = ce;
		temp = temp.replaceAll("#","/");
		this.id = id;
		this.descIRI = getIRI(temp);
		this.descClass = getDescriptionClass(temp);
		String tempC = getDescIRI().concat(getDescClass());
		IRI tempI = IRI.create(tempC);
		OWLObject o = factory.getOWLClass(tempI);
		this.classExpression = (OWLClassExpression) o;
		this.Bottom = false;
		this.Top = false;
		this.type = type;
		this.negation = true;
		this.level = -1;
		this.remove = false;
		expressions = new ArrayList<String>();
		expressions.add(temp);
	}

	/**
     * Initializes Node from OWLClassExpression not negated
     * 
	 * @param cls
	 */
	public NodeRestrictionComplementOfClass(String cls) 
	{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();		
		String tempC = cls;
		tempC = tempC.replaceAll("#","/");
		tempC = tempC.replaceAll("<", "");
		tempC = tempC.replaceAll(">", "");
		IRI tempI = IRI.create(tempC);
		OWLObject o = factory.getOWLClass(tempI);
		this.classExpression = (OWLClassExpression) o;
		OWLObjectComplementOf complementOf = factory.getOWLObjectComplementOf(this.classExpression);
		this.descIRI = getIRI(cls.toString());
		this.descClass = getDescriptionClass(cls.toString());
		this.Bottom = false;
		this.type = NodeType.RestrictionComplementOfClass;
		this.negation = true;
		this.level = -1;
		this.remove = false;
		expressions = new ArrayList<String>();
		expressions.add(complementOf.toString());
	}
	
	//basic gets and sets
    public String getDescClass(){
        return descClass;
    }
    public void setDescClass(String s){
        this.descClass = s;
    }
    public String getDescIRI(){
        return descIRI;
    }
    public void setDescIRI(String s){
        this.descIRI = s;
    }
    public OWLClassExpression getExpClass(){
        return classExpression;
    }
    public void setExpClass(OWLClassExpression ce){
        this.classExpression = ce;
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
        			temp = s.substring(s.indexOf("<")+1,s.lastIndexOf("/")+1);
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
        String temp = "";
        if(s.contains("rdf:")||s.contains("RDF:"))
		{
        	temp = s;
			temp = temp.replaceAll("<", "");
			temp = temp.replaceAll(">", "");
			return temp;
		}
        if(s.contains("#"))
        {
           temp = s.substring(s.indexOf("#")+1,s.indexOf(">"));
        }else 
        {
           temp = s.substring(s.lastIndexOf("/")+1,s.indexOf(">"));
        }
        return temp;
    }
}
