package Ontology;

import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Specialization of the Node Class to represent the complement of OWL properties 
 * 
 * @author Romulo de Carvalho Magalhaes
 *
 */
public class NodeRestrictionComplementOfProperty extends Node{

	public IRI iri;
    public String descProp;
    public String descIRI;

	/**
	 * Initializes Complement of Property Node from IRI and Description property, with ID
	 * 
	 * @param id
	 * @param prop
	 * @param iri
	 */
	public NodeRestrictionComplementOfProperty(int id,String prop,String iri)
	{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		PrefixManager pm = new DefaultPrefixManager(iri);
		this.id = id;
		this.descIRI = iri;
		this.descProp = prop;
		this.iri = IRI.create(iri);
		this.Bottom = false;
		this.Top = false;
		this.type = NodeType.RestrictionComplementOfProperty;
		this.negation = true;
		this.level = -1;
		this.remove = false;
		String temp = "~" + prop;
		OWLObjectProperty notProp = factory.getOWLObjectProperty(temp, pm);
		temp = notProp.toString().replaceAll("#", "/");
		expressions = new ArrayList<String>();
		expressions.add(temp);
	}
	
	/**
	 * Initializes Complement of Property Node from IRI and Description property, without ID
	 * 
	 * @param prop
	 * @param iri
	 */
	public NodeRestrictionComplementOfProperty(String prop,String iri)
	{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		PrefixManager pm = new DefaultPrefixManager(iri);
		this.descIRI = iri;
		this.descProp = prop;
		this.iri = IRI.create(iri);
		this.Bottom = false;
		this.Top = false;
		this.type = NodeType.RestrictionComplementOfProperty;
		this.negation = true;
		this.level = -1;
		this.remove = false;
		String temp = "~" + prop;
		OWLObjectProperty notProp = factory.getOWLObjectProperty(temp, pm);
		temp = notProp.toString().replaceAll("#", "/");
		expressions = new ArrayList<String>();
		expressions.add(temp);
	}

	/**
	 * Initializes Complement of Property Node from IRI of Property Node
	 * 
	 * @param iri
	 */
	public NodeRestrictionComplementOfProperty(String iri) 
	{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		PrefixManager pm = new DefaultPrefixManager(iri);
		String temp = getDescriptionProperty(iri);
		this.descIRI = getIRI(iri);
		this.descProp = temp;
		this.iri = IRI.create(iri);
		this.Bottom = false;
		this.Top = false;
		this.type = NodeType.RestrictionComplementOfProperty;
		this.negation = true;
		this.level = -1;
		this.remove = false;
		temp = "~" + temp;
		OWLObjectProperty notProp = factory.getOWLObjectProperty(temp, pm);
		temp = notProp.toString().replaceAll("#", "/");
		expressions = new ArrayList<String>();
		expressions.add(temp);
	}
	
	/**
	 * Initializes Complement of Property Node from IRI of Property Node
	 * 
	 * @param id
	 * @param iri
	 */
	public NodeRestrictionComplementOfProperty(int id, String iri) 
	{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		PrefixManager pm = new DefaultPrefixManager(iri);
		String temp = getDescriptionProperty(iri);
		this.id = id;
		this.descIRI = getIRI(iri);
		this.descProp = temp;
		this.iri = IRI.create(iri);
		this.Bottom = false;
		this.Top = false;
		this.type = NodeType.RestrictionComplementOfProperty;
		this.negation = true;
		this.level = -1;
		this.remove = false;
		temp = "~" + temp;
		OWLObjectProperty notProp = factory.getOWLObjectProperty(temp, pm);
		temp = notProp.toString().replaceAll("#", "/");
		expressions = new ArrayList<String>();
		expressions.add(temp);
	}
	
	public OWLObjectProperty getOWLObject()
	{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		PrefixManager pm = new DefaultPrefixManager(this.descIRI);
		String temp = "~" + this.descProp;
		OWLObjectProperty notProp = factory.getOWLObjectProperty(temp, pm);
		return notProp;
	}

	//basic gets and sets
    public String getDescIRI(){
        return descIRI;
    }
    public void setDescIRI(String s){
        this.descIRI = s;
    }
    public String getDescProp(){
        return descProp;
    }
    public void setDescProp(String s){
        this.descProp = s;
    }
    public IRI getIri(){
        return iri;
    }
    public void setIri(IRI i){
        this.iri = i;
    }
    
    /**
     * Get Clean IRI from OWLObject expression string
     * 
     * @param s
     * @return
     */
    public static String getIRI(String s)
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
        		temp = temp.replaceAll(s.substring(s.indexOf("<"),s.indexOf("#")+1),"");
        		temp = temp.replaceAll("#", "/");
            }else 
            {
            	if(s.contains(":") && !s.contains("/"))
            	{
            		temp = temp.replaceAll(s.substring(s.indexOf("<"),s.lastIndexOf(":")+1),"");
            		temp = temp.replaceAll(":", "/");
        		}else 
        		{
        			temp = temp.replaceAll(s.substring(s.indexOf("<"),s.lastIndexOf("/")+1),"");
    			}
            }
        }
        if(s.contains(">"))
        {
        	temp = temp.replaceAll(temp.substring(temp.indexOf(">"),temp.indexOf(">")+1),"");
        }
        return temp;
    }

    /**
     * Get clean Property Description from OWLObject expression string
     * @param s
     * @return
     */
    public static String getDescriptionProperty(String s)
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
