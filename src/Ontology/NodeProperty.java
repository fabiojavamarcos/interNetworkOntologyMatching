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
 * Specialization of the Node Class to represent OWL Properties 
 * 
 * @author Romulo de Carvalho Magalhaes
 *
 */
public class NodeProperty extends Node{

	private IRI iri;
    private String descProp;
    private String descIRI;

	/**
	 * Initializes a Property Node from OWLObject, with ID
	 * 
	 * @param id
	 * @param desc
	 */
	public NodeProperty(int id, String ce)
    {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		String tempC = ce;
		tempC = tempC.replaceAll("#","/");
		tempC = tempC.replaceAll("<", "");
		tempC = tempC.replaceAll(">", "");
		IRI tempI = IRI.create(tempC);
		OWLObjectProperty o = factory.getOWLObjectProperty(tempI);
		expressions = new ArrayList<String>();
		expressions.add(o.toString());
		this.id = id;
		this.descIRI = getIRI(expressions.get(0).toString());
		this.descProp = getDescriptionProperty(expressions.get(0).toString());
		this.iri = IRI.create(this.descIRI);
		this.Bottom = false;
		this.type = NodeType.Property;
		this.negation = false;
		this.level = -1;
		this.remove = false;
    }
	
	/**
	 * Initializes Property Node from IRI and Description property, with ID
	 * 
	 * @param id
	 * @param is
	 * @param ps
	 */
	public NodeProperty(int id, String is, String ps)
    {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		PrefixManager pm = new DefaultPrefixManager(is);
		OWLObjectProperty prop = factory.getOWLObjectProperty(ps, pm);
		this.id = id;
		this.descIRI = is;
		this.descProp = ps;
		this.iri = IRI.create(this.descIRI);
		this.Bottom = false;
		this.Top = false;
		this.type = NodeType.Property;
		this.negation = false;
		this.level = -1;
		this.remove = false;
		String temp = prop.toString();
        temp = temp.replaceAll("#", "/");
        expressions = new ArrayList<String>();
        expressions.add(temp);
    }

	/**
	 * Initializes Property Node from IRI and Description property, without ID
	 * 
	 * @param is
	 * @param ps
	 */
    public NodeProperty(String is, String ps)
    {
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        PrefixManager pm = new DefaultPrefixManager(is);
        OWLObjectProperty prop = factory.getOWLObjectProperty(ps, pm);
        String temp = prop.toString();
        temp = temp.replaceAll("#", "/");
        this.descIRI = is;
        this.descProp = ps;
        this.iri = IRI.create(this.descIRI);
        this.Bottom = false;
        this.Top = false;
        this.type = NodeType.Property;
        this.negation = false;
        this.level = -1;
        this.remove = false;
        expressions = new ArrayList<String>();
        expressions.add(temp);
    }

    //Basic gets and sets
	public String getDescProp() {
        return descProp;
    }
	public void setDescProp(String s) {
        this.descProp = s;
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
            	temp = s.substring(s.indexOf("<")+1,s.indexOf("#"));
            	temp = temp + "/";
            }else
            {
            	temp = s.substring(s.indexOf("<")+1,s.lastIndexOf("/")+1);
            }
            return temp;
        }
        if(s.contains(":"))
        {
        	temp = s.substring(0,s.lastIndexOf(":"));
        	temp = temp + "/";
        }
        return temp;
    }
	
    /**
     * Get clean Property Description from OWLObject expression string
     * @param s
     * @return
     */
	public String getDescriptionProperty(String s)
	{
        String temp = s;
        if(s.contains("rdf:")||s.contains("RDF:"))
		{
			temp = temp.replaceAll("<", "");
			temp = temp.replaceAll(">", "");
			return temp;
		}
        if(s.contains("<"))
        {
            if(s.contains("#")) 
            {
            	temp = temp.replaceAll(s.substring(s.indexOf("<"),s.indexOf("#")+1),"");
            }else 
            {
            	temp = temp.replaceAll(s.substring(s.indexOf("<"),s.lastIndexOf("/")+1),"");
            }
        }else 
        {
        	if(s.contains(":"))
        	{
        		temp = temp.replaceAll(s.substring(0,s.lastIndexOf(":")+1),"");
            }
        }	
        if(s.contains(">"))
        {
        	temp = temp.replaceAll(temp.substring(temp.indexOf(">"),temp.indexOf(">")+1),"");
        }
        return temp;
    }
}
