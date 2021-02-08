package Ontology;

import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Specialization of the Node Class to represent the complement of OWL Cardinality restriction 
 * 
 * 
 *
 */
public class NodeRestrictionComplementOfRestrictionCardinality extends Node{

	private String iRIProp;
    private String iRIDomain;
    private String descProp;
    private String descRangeOrDomain;
    private int card;
    
    /**
     * Initializes Complement Restriction Cardinality Node from variables and ID
     * 
     * @param id
     * @param iRIDomain
     * @param iRIProp
     * @param descProp
     * @param descRangeOrDomain
     * @param card
     */
    public NodeRestrictionComplementOfRestrictionCardinality(int id,String IRIDomain,String IRIProp,String descProp,String descRangeOrDomain,int card) 
    {
    	IRIDomain = IRIDomain.replace("#", "/");
    	IRIProp = IRIProp.replace("#", "/");
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    	OWLDataFactory factory = manager.getOWLDataFactory();
    	PrefixManager pmiRIDomain = new DefaultPrefixManager(IRIDomain);
    	PrefixManager pmiRIProp = new DefaultPrefixManager(IRIProp);
    	
    	this.id = id;
    	this.descProp = descProp;
    	this.descRangeOrDomain = descRangeOrDomain;
    	this.iRIDomain = IRIDomain;
       	this.iRIProp = IRIProp;
       	this.card = card;
       	this.Bottom = false;
       	this.Top = false;
       	this.type = NodeType.RestrictionComplementOfRestrictionCardinality;
       	this.negation = true;
       	this.level = -1;
       	this.remove = false;

       	OWLClassExpression domainOrRangeObjProp = factory.getOWLClass(descRangeOrDomain, pmiRIDomain);
       	OWLObjectProperty propRestriction = factory.getOWLObjectProperty(descProp, pmiRIProp);
       	OWLObjectMinCardinality restrictionMinCard = factory.getOWLObjectMinCardinality(card, propRestriction, domainOrRangeObjProp);
       	OWLObjectComplementOf notProp = factory.getOWLObjectComplementOf((OWLClassExpression)restrictionMinCard);
       	expressions = new ArrayList<String>();
       	expressions.add(notProp.toString());
    }

    /**
     * Initializes Complement Restriction Cardinality Node from variables and without ID
     * 
     * @param descIRI
     * @param descProp
     * @param descRangeOrDomain
     * @param card
     */
    /*
    public NodeRestrictionComplementOfRestrictionCardinality(String descIRI,String descProp,String descRangeOrDomain,int card) 
    {
    	descIRI = descIRI.replace("#", "/");
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    	OWLDataFactory factory = manager.getOWLDataFactory();
    	PrefixManager pm = new DefaultPrefixManager(descIRI);	

    	this.descProp = descProp;
    	this.descRangeOrDomain = descRangeOrDomain;
    	this.card = card;
    	this.Bottom = false;
    	this.Top = false;
    	this.type = NodeType.RestrictionComplementOfRestrictionCardinality;
    	this.negation = true;
    	this.level = -1;
    	this.remove = false;

    	OWLClassExpression domainOrRangeObjProp = factory.getOWLClass(descRangeOrDomain, pm);
    	OWLObjectProperty propRestriction = factory.getOWLObjectProperty(descProp, pm);
    	OWLObjectMinCardinality restrictionMinCard = factory.getOWLObjectMinCardinality(1, propRestriction,domainOrRangeObjProp);
    	OWLObjectComplementOf notProp = factory.getOWLObjectComplementOf((OWLClassExpression)restrictionMinCard);
    	expressions = new ArrayList<String>();
    	expressions.add(notProp.toString());
    }
    */

    /**
     * Initializes Complement Restriction Cardinality Node from OWLClassExpression
     * 
     * @param restrictionCard
     */
	public NodeRestrictionComplementOfRestrictionCardinality(String restrictionCard) 
    {
    	String desc = restrictionCard;
		desc = desc.replaceAll("#", "/");
        String iriDomain = UsefulOWL.returnIRIDomain(desc);
        String iriProp = UsefulOWL.returnIRIProp(desc);
        String descProp = UsefulOWL.returnProp(desc);
        String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
        int card = UsefulOWL.returnCard(desc);
        this.card = card;
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    	OWLDataFactory factory = manager.getOWLDataFactory();
    	PrefixManager pmiRIDomain = new DefaultPrefixManager(iriDomain);
    	PrefixManager pmiRIProp = new DefaultPrefixManager(iriProp);
    	OWLClassExpression domainOrRangeObjProp = factory.getOWLClass(descRangeOrDomain, pmiRIDomain);
       	OWLObjectProperty propRestriction = factory.getOWLObjectProperty(descProp, pmiRIProp);
       	OWLObjectMinCardinality restrictionMinCard = factory.getOWLObjectMinCardinality(card, propRestriction, domainOrRangeObjProp);
       	OWLObjectComplementOf notProp = factory.getOWLObjectComplementOf((OWLClassExpression)restrictionMinCard);
    	this.Bottom = false;
    	this.Top = false;
    	this.type = NodeType.RestrictionComplementOfRestrictionCardinality;
    	this.negation = true;
       	this.level = -1;
       	this.remove = false;

       	expressions = new ArrayList<String>();
       	expressions.add(notProp.toString());
    }

    //basic gets and sets
    public String getIRIDomain(){
        return iRIDomain;
    }
    public void setiRIDomain(String s){
        this.iRIDomain = s;
    }
    public String getiRIProp(){
        return iRIProp;
    }
    public void setiRIProp(String s){
        this.iRIProp = s;
    }
    public int getCard(){
        return card;
    }
    public void setCard(int i){
        this.card = i;
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
    public void setDescRangeOrDomain(String s) {
        this.descRangeOrDomain = s;
    }
}
