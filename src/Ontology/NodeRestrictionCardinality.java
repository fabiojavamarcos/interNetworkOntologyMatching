package Ontology;

import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Specialization of the Node Class to represent OWL Cardinality restriction 
 * 
 * @author Romulo de Carvalho Magalhaes - adapted by Fabio Marcos de Abreu Santos
 *
 */
public class NodeRestrictionCardinality extends Node {

	private String iRIProp;
    private String iRIDomain;
    private String descProp;
    private String descRangeOrDomain;
    private int card;
    
    /**
     * Initializes Restriction Cardinality Node from OWLObject and ID
     * 
     * @param id
     * @param o
     */
	public NodeRestrictionCardinality(int id, String o)
    {
		this.expressions = new ArrayList<String>();
		this.expressions.add(o.replaceAll("#", "/"));
		this.id = id;
		this.iRIDomain = getIRIDomain(expressions.get(0).toString());
		this.iRIProp = getIRIProp (expressions.get(0).toString());
		this.descProp = UsefulOWL.returnProp(expressions.get(0).toString());
		this.card = UsefulOWL.returnCard(expressions.get(0).toString());
		this.descRangeOrDomain = UsefulOWL.returnDomainOrRange(expressions.get(0).toString());
		this.Bottom = false;
		this.Top = false;
		this.type = NodeType.RestrictionCardinality;
		this.negation = false;
		this.level = -1;
		this.remove = false;		
    }
 
	/**
     * Initializes Restriction Cardinality Node from variables with ID
	 * 
	 * @param id
	 * @param IRIDomain
	 * @param IRIProp
	 * @param descProp
	 * @param descRangeOrDomain
	 * @param card
	 */
    public NodeRestrictionCardinality(int id,String IRIDomain,String IRIProp,String descProp,String descRangeOrDomain ,int card)
    {
    	IRIDomain = IRIDomain.replace("#", "/");
    	IRIProp = IRIProp.replace("#", "/");
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    	OWLDataFactory factory = manager.getOWLDataFactory();      
    	PrefixManager pmProp = new DefaultPrefixManager(IRIProp);
    	PrefixManager pmIRIDomain = new DefaultPrefixManager(IRIDomain);
    	this.id = id;
    	this.iRIDomain = IRIDomain;
    	this.iRIProp = IRIProp;
	   	this.descProp = descProp;
	   	this.card = card;
	   	this.descRangeOrDomain = descRangeOrDomain;
	   	this.Bottom = false;
	   	this.type = NodeType.RestrictionCardinality;
	   	this.negation = false;
	   	this.level = -1;
	   	this.remove = false;
	   	OWLClassExpression domainOrRangeObjProp = factory.getOWLClass(descRangeOrDomain, pmIRIDomain);
	   	OWLObjectProperty propRestriction = factory.getOWLObjectProperty(descProp, pmProp);
	   	OWLObjectMinCardinality RestrictionMinCard = factory.getOWLObjectMinCardinality(card, propRestriction,domainOrRangeObjProp);
	   	expressions = new ArrayList<String>();
	   	expressions.add(RestrictionMinCard.toString());
    }

    /**
     * Initializes Restriction Cardinality Node from variables without ID
     * 
     * @param IRIDomain
     * @param IRIProp
     * @param descProp
     * @param descRangeOrDomain
     * @param card
     */
    public NodeRestrictionCardinality(String IRIDomain,String IRIProp,String descProp,String descRangeOrDomain ,int card)
    {
    	IRIDomain = IRIDomain.replace("#", "/");
    	IRIProp = IRIProp.replace("#", "/");
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    	OWLDataFactory factory = manager.getOWLDataFactory();      
    	PrefixManager pmProp = new DefaultPrefixManager(IRIProp);
    	PrefixManager pmIRIDomain = new DefaultPrefixManager(IRIDomain);
    	this.iRIDomain = IRIDomain;
    	this.iRIProp = IRIProp;
	   	this.descProp = descProp;
	   	this.card = card;
	   	this.descRangeOrDomain = descRangeOrDomain;
	   	this.Bottom = false;
	   	this.type = NodeType.RestrictionCardinality;
	   	this.negation = false;
	   	this.level = -1;
	   	this.remove = false;
	   	OWLClassExpression domainOrRangeObjProp = factory.getOWLClass(descRangeOrDomain, pmIRIDomain);
	   	OWLObjectProperty propRestriction = factory.getOWLObjectProperty(descProp, pmProp);
	   	OWLObjectMinCardinality restrictionMinCard = factory.getOWLObjectMinCardinality(card, propRestriction,domainOrRangeObjProp);
	   	expressions = new ArrayList<String>();
	   	expressions.add(restrictionMinCard.toString());
    }

    //basic gets and sets
    public String getIRIDomain(){
        return iRIDomain;
    }
    public void setIRIDomain(String s){
        this.iRIDomain = s;
    }
    public String getIRIProp(){
        return iRIProp;
    }
    public void setIRIProp(String s){
        this.iRIProp = s;
    }
    public String getDescProp(){
        return descProp;
    }
    public void setDescProp(String s){
        this.descProp = s;
    }
    public String getDescRangeOrDomain(){
        return descRangeOrDomain;
    }
    public void setDescRangeOrDomain(String s){
        this.descRangeOrDomain = s;
    }
    public int getCard(){
        return card;
    }
    public void setCard(int c){
        this.card = c;
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
        if(s.contains("<"))
        {
        	if(s.contains("#"))
        	{
        		temp = s.substring(s.indexOf("<")+1,s.indexOf("#")+1);
            }else 
            {
            	temp= s.substring(s.indexOf("<")+1,s.lastIndexOf("/")+1);
            }
         }
        return temp;
    }
    
    /**
     * Get IRIDomain from OWLObject expression string
     * 
     * @param s
     * @return
     */
    public String getIRIDomain(String s)
    {
    	String temp = s;
    	if((s.contains("rdf:"))||(s.contains("RDF:")))
    	{
    		int i = s.lastIndexOf("<");
    		int j = s.indexOf("rdf:");
    		if(j < 0)
    		{
    			j = s.indexOf("RDF:");
    		}
    		if((s.contains("<"))&&(j<i))
            {
            	temp = s.substring(s.lastIndexOf("<")+1,s.lastIndexOf(">"));
            	if(temp.contains("#")) 
                {
                	temp = temp.substring(0,temp.indexOf("#"));
                	temp = temp + "/";
                }else 
                {
                	temp = temp.substring(0,temp.lastIndexOf("/")+1);
                }
            }
    		else
    		{
    			return "";
    		}
    	}
    	else if(s.contains("<"))
        {
        	temp = s.substring(s.lastIndexOf("<")+1,s.lastIndexOf(">"));
        	if(temp.contains("#")) 
            {
            	temp = temp.substring(0,temp.indexOf("#"));
            	temp = temp + "/";
            }else 
            {
            	temp = temp.substring(0,temp.lastIndexOf("/")+1);
            }
        }
        return temp;
    }
  
    /**
     * Get IRIProp from OWLObject expression string
     * 
     * @param s
     * @return
     */
    public String getIRIProp(String s)
    {
        String temp = s;
        if((s.contains("rdf:"))||(s.contains("RDF:")))
    	{
    		int i = s.indexOf("<");
    		int j = s.indexOf("rdf:");
    		if(j < 0)
    		{
    			j = s.indexOf("RDF:");
    		}
    		if((s.contains("<"))&&(j>i))
            {
            	temp = s.substring(s.indexOf("<")+1,s.indexOf(">"));
            	if(temp.contains("#")) 
                {
                	temp = temp.substring(0,temp.indexOf("#"));
                	temp = temp + "/";
                }else 
                {
                	temp = temp.substring(0,temp.lastIndexOf("/")+1);
                }
            }
    		else
    		{
    			return "";
    		}
    	}
    	else if(s.contains("<"))
        {
        	temp = s.substring(s.indexOf("<")+1,s.indexOf(">"));
            if(temp.contains("#")) 
            {
            	temp = temp.substring(0,temp.indexOf("#"));
            	temp = temp + "/";
            }else 
            {
            	temp = temp.substring(0,temp.lastIndexOf("/")+1);
            }
        }
        return temp;
    }
}
