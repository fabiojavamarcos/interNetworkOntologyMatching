package Ontology;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextArea;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

@SuppressWarnings("unused")
public class SaveOntology {

	public static Boolean SaveOntologyToFile(String path, String name, Graph g, JTextArea Log)
	{
		String str = "";
		try{
			File file = new File(path);
			Object[] resp = createOntology(path, name, Log, file);
			if(resp == null)
			{
				return false;
			}
			OWLOntology ontology = (OWLOntology)resp[1];
			OWLOntologyManager manager = (OWLOntologyManager)resp[2];
			IRI nameIRI =  IRI.create(file.toURI());
			manager = loadManager(ontology , manager, g, nameIRI);
			manager.saveOntology(ontology, nameIRI);
			str = Log.getText();
			str = str + "\n" + "Ontology successfully saved as: " + path;
			Log.setText(str);
		}
		catch(Exception e)
		{
			str = Log.getText();
    	    str = str + "\n" + "Could not save ontology: " + e.getMessage();
    	    Log.setText(str);
    	    return false;
		}
		return true;
	}
	
	/**
	 * Given a Graph g fills the ontology according to its Nodes and Adj List
	 * 
	 * @param ontology
	 * @param manager
	 * @param g
	 * @param nameIRI
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static OWLOntologyManager loadManager(OWLOntology ontology, OWLOntologyManager manager, Graph g, IRI nameIRI)
	{
		OWLDataFactory factory = manager.getOWLDataFactory(); 
		PrefixManager prefix;
		PrefixManager prefix1;
		String s;
		IRI iri;
		//first we add all Class, Complement of Class, Property and Complement of Property nodes
		HashMap<Integer,Node> vertices = g.getVertices();
		ArrayList<LinkedList<Integer>> adj = g.getAdj();
		LinkedList<Integer> tempAdj;
		Set cont = vertices.entrySet();
        Iterator it = (Iterator) cont.iterator();
        
        while(it.hasNext())
        {
        	Map.Entry<Integer, Node> temp = (Map.Entry<Integer, Node>)it.next();
        	Node n = temp.getValue();
        	NodeType type = n.getNodeType();
        	if(!n.isRemove())
        	{
	        	switch(type)
	        	{
				   case Class :
					   OWLClass nc = (OWLClass) getObjectFromNode(n,factory);
					   OWLDeclarationAxiom classAxiom = factory.getOWLDeclarationAxiom(nc);
		               manager.addAxiom(ontology, classAxiom);
		               tempAdj = UsefulOWL.getListwithFirstElement(adj, n.getId());
		               for(int i = 1; i < tempAdj.size(); i++)
			           {
			        		Node superNode = vertices.get(tempAdj.get(i));
			        		type = superNode.getNodeType();
			        		OWLObject superNodeObj = getObjectFromNode(superNode,factory);
			        		if((!type.equals(NodeType.Property))&&(!type.equals(NodeType.RestrictionComplementOfProperty)))
			        		{
			        			OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom((OWLClassExpression)nc, (OWLClassExpression)superNodeObj);
				        		manager.addAxiom(ontology, axiom);
			        		}
			           }
				       break;
				   case Property :
					   OWLProperty np = (OWLProperty) getObjectFromNode(n,factory);
					   OWLDeclarationAxiom propAxiom = factory.getOWLDeclarationAxiom(np);
		               manager.addAxiom(ontology, propAxiom);
		               tempAdj = UsefulOWL.getListwithFirstElement(adj, n.getId());
		               for(int i = 1; i < tempAdj.size(); i++)
			           {
			        		Node superNode = vertices.get(tempAdj.get(i));
			        		type = superNode.getNodeType();
			        		OWLObject superNodeObj = getObjectFromNode(superNode,factory);
			        		if((type.equals(NodeType.Property))||(type.equals(NodeType.RestrictionComplementOfProperty)))
			        		{
			        			OWLAxiom axiom = factory.getOWLSubObjectPropertyOfAxiom((OWLObjectProperty)np, (OWLObjectProperty)superNodeObj);
			        			manager.addAxiom(ontology, axiom);
			        		}
			           }
				       break;
				   case RestrictionCardinality :
					   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality) n;
					   s = nrc.getIRIDomain();
					   if(s.endsWith("/"))
					   {
						   s = s.substring(0, s.length()-1) + "#";
					   }
					   prefix = new DefaultPrefixManager(s);
		               OWLClass nodeRDC = factory.getOWLClass(nrc.getDescRangeOrDomain() , prefix);
		               s = nrc.getIRIProp();
					   if(s.endsWith("/"))
					   {
						   s = s.substring(0, s.length()-1) + "#";
					   }
					   prefix1 = new DefaultPrefixManager(s);
		               OWLObjectPropertyExpression nodeRDP = factory.getOWLObjectProperty(nrc.getDescProp(), prefix1);
		               OWLObjectMinCardinality restMinCard = factory.getOWLObjectMinCardinality(nrc.getCard(), nodeRDP, nodeRDC);
					   //OWLSubClassOfAxiom axiomMinCard = factory.getOWLSubClassOfAxiom(restMinCard, nodeRDC);
					   //manager.addAxiom(ontology, axiomMinCard);
		               tempAdj = UsefulOWL.getListwithFirstElement(adj, n.getId());
		               for(int i = 1; i < tempAdj.size(); i++)
			           {
			        		Node superNode = vertices.get(tempAdj.get(i));
			        		type = superNode.getNodeType();
			        		OWLObject superNodeObj = getObjectFromNode(superNode,factory);
			        		if((!type.equals(NodeType.Property))&&(!type.equals(NodeType.RestrictionComplementOfProperty)))
			        		{
			        			OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom((OWLClassExpression)restMinCard, (OWLClassExpression)superNodeObj);
				        		manager.addAxiom(ontology, axiom);
			        		}
			           }
				       break;
					   
				   case RestrictionComplementOfRestrictionCardinality :
					   NodeRestrictionComplementOfRestrictionCardinality notRC = (NodeRestrictionComplementOfRestrictionCardinality)n;
					   //prefix = new DefaultPrefixManager(notRC.getIRIDomain());
					   s = notRC.getIRIDomain();
					   if(s.endsWith("/"))
					   {
						   s = s.substring(0, s.length()-1) + "#";
					   }
					   prefix = new DefaultPrefixManager(s);
		               OWLClass nodeNRDC = factory.getOWLClass(":"+notRC.getDescRangeOrDomain() , prefix);
		               s = notRC.getiRIProp();
					   if(s.endsWith("/"))
					   {
						   s = s.substring(0, s.length()-1) + "#";
					   }
					   prefix1 = new DefaultPrefixManager(s);
		               //prefix1 = new DefaultPrefixManager(notRC.getiRIProp());
		               OWLObjectPropertyExpression nodeNRDP = factory.getOWLObjectProperty(":"+notRC.getDescProp(), prefix1);
		               OWLObjectMaxCardinality notRestritionMinCard = factory.getOWLObjectMaxCardinality(notRC.getCard()-1, nodeNRDP, nodeNRDC);
		               //OWLObjectMinCardinality restNotMinCard = factory.getOWLObjectMinCardinality(notRC.getCard(), nodeNRDP, nodeNRDC);
		               //OWLObjectComplementOf notRestritionMinCard = factory.getOWLObjectComplementOf((OWLClassExpression) restNotMinCard);
		               
					   tempAdj = UsefulOWL.getListwithFirstElement(adj, n.getId());
		               for(int i = 1; i < tempAdj.size(); i++)
			           {
			        		Node superNode = vertices.get(tempAdj.get(i));
			        		type = superNode.getNodeType();
			        		OWLObject superNodeObj = getObjectFromNode(superNode,factory);
			        		if((!type.equals(NodeType.Property))&&(!type.equals(NodeType.RestrictionComplementOfProperty)))
			        		{
			        			OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom((OWLClassExpression)notRestritionMinCard, (OWLClassExpression)superNodeObj);
				        		manager.addAxiom(ontology, axiom);
			        		}
			           }
				       break;
					   
				   default: 
					   break;
			    }
        	}
        }
		return manager;
	}
	
	
	public static OWLObject getObjectFromNode(Node n, OWLDataFactory factory)
	{
		String description = n.getExpression();
    	NodeType type = n.getNodeType();
    	IRI tempI;
    	PrefixManager prefix;
		PrefixManager prefix1;
		String temp;
    	
    	switch(type)
    	{
		   case Class :
			   NodeClass nc = (NodeClass)n;
			   temp = nc.getDescIRI();
			   if(temp.endsWith("/"))
			   {
				   temp = temp.substring(0, temp.length()-1) + "#";
			   }
			   prefix = new DefaultPrefixManager(temp);
			   OWLClass oc = factory.getOWLClass(":"+nc.getDescClass(),prefix);
			   return oc;
		   case Property :
			   NodeProperty np = (NodeProperty)n;
			   temp = np.getDescIRI();
			   if(temp.endsWith("/"))
			   {
				   temp = temp.substring(0, temp.length()-1) + "#";
			   }
			   prefix = new DefaultPrefixManager(temp);
			   OWLObjectProperty p = factory.getOWLObjectProperty(":"+np.getDescProp(),prefix);
			   return p;
		   case RestrictionComplementOfClass :
			   NodeRestrictionComplementOfClass ncc = (NodeRestrictionComplementOfClass) n;
			   String classDesc = ncc.getDescClass();
			   temp = ncc.getDescIRI();
			   if(temp.endsWith("/"))
			   {
				   temp = temp.substring(0, temp.length()-1) + "#";
			   }
			   prefix = new DefaultPrefixManager(temp);
               OWLClass nClass = factory.getOWLClass(":"+classDesc,prefix);
               OWLObjectComplementOf conClass = factory.getOWLObjectComplementOf((OWLClassExpression) nClass);
               return conClass;
		   case RestrictionComplementOfProperty :
			   NodeRestrictionComplementOfProperty ncp = (NodeRestrictionComplementOfProperty)n;
			   String descProp = "~" + ncp.getDescProp();
			   temp = ncp.getDescIRI();
			   if(temp.endsWith("/"))
			   {
				   temp = temp.substring(0, temp.length()-1) + "#";
			   }
			   prefix = new DefaultPrefixManager(temp);
			   OWLObjectProperty notProp = factory.getOWLObjectProperty(":"+descProp, prefix);
			   //OWLObjectComplementOf connotProp = factory.getOWLObjectComplementOf((OWLClassExpression) notProp);
               return notProp;
	       case RestrictionCardinality :
			   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality) n;
			   temp = nrc.getIRIDomain();
			   if(temp.endsWith("/"))
			   {
				   temp = temp.substring(0, temp.length()-1) + "#";
			   }
			   prefix = new DefaultPrefixManager(temp);
			   //prefix = new DefaultPrefixManager(nrc.getIRIDomain());
               OWLClass nodeRDC = factory.getOWLClass(":"+nrc.getDescRangeOrDomain() , prefix);
               temp = nrc.getIRIProp();
			   if(temp.endsWith("/"))
			   {
				   temp = temp.substring(0, temp.length()-1) + "#";
			   }
			   prefix1 = new DefaultPrefixManager(temp);
               //prefix1 = new DefaultPrefixManager(nrc.getIRIProp());
               OWLObjectPropertyExpression nodeRDP = factory.getOWLObjectProperty(":"+nrc.getDescProp(), prefix1);
               OWLObjectMinCardinality restMinCard = factory.getOWLObjectMinCardinality(nrc.getCard(), nodeRDP, nodeRDC);
               return restMinCard;
		   case RestrictionComplementOfRestrictionCardinality :
			   NodeRestrictionComplementOfRestrictionCardinality notRC = (NodeRestrictionComplementOfRestrictionCardinality)n;
			   //prefix = new DefaultPrefixManager(notRC.getIRIDomain());
			   temp = notRC.getIRIDomain();
			   if(temp.endsWith("/"))
			   {
				   temp = temp.substring(0, temp.length()-1) + "#";
			   }
			   prefix = new DefaultPrefixManager(temp);
               OWLClass nodeNRDC = factory.getOWLClass(":"+notRC.getDescRangeOrDomain() , prefix);
               temp = notRC.getiRIProp();
			   if(temp.endsWith("/"))
			   {
				   temp = temp.substring(0, temp.length()-1) + "#";
			   }
			   prefix1 = new DefaultPrefixManager(temp);
               //prefix1 = new DefaultPrefixManager(notRC.getiRIProp());
               OWLObjectPropertyExpression nodeNRDP = factory.getOWLObjectProperty(":"+notRC.getDescProp(), prefix1);
               OWLObjectMinCardinality restNotMinCard = factory.getOWLObjectMinCardinality(notRC.getCard(), nodeNRDP, nodeNRDC);
               OWLObjectComplementOf notRestritionMinCard = factory.getOWLObjectComplementOf((OWLClassExpression) restNotMinCard);
               return notRestritionMinCard;
		   default: 
			   break;
	    }
        
		return null;
	}
	
	
	/**
	 * Initializes an OWL Ontology with its basic attributes
	 * 
	 * @param path
	 * @param name
	 * @param Log
	 * @param file
	 * @return Object containing its path, OWLOntology and its manager
	 */
	private static Object[] createOntology(String path, String name, JTextArea Log, File file)
	{
		String str = "";
		Object[] obj = new Object[3];
        OWLOntology ontology = null;
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        //String caminhoOntologia = caminhoArquivoOntologia  + nomeOntologia + ".owl";
        IRI myIRI = IRI.create("http://www.semanticweb.org/ontologies/2014/5/" + name.substring(0,name.lastIndexOf(".")));
        
        try{
            RDFXMLOntologyFormat rdfSyntaxFormat = new RDFXMLOntologyFormat();
            IRI documentIRI = IRI.create("file:" + path);
            SimpleIRIMapper mapper = new SimpleIRIMapper(myIRI, documentIRI);
            manager.addIRIMapper(mapper);
            ontology = manager.createOntology(myIRI);
            manager.saveOntology(ontology, rdfSyntaxFormat, IRI.create(file.toURI()));
       }
       catch(OWLOntologyCreationException e)
       {
    	   str = Log.getText();
    	   str = str + "\n" + "Could not load ontology: " + e.getMessage();
    	   Log.setText(str);
    	   return null;
       }
       catch(OWLOntologyStorageException e)
       {
    	   str = Log.getText();
    	   str = str + "\n" + "Could not save ontology: " + e.getMessage();
    	   Log.setText(str);
    	   return null;
       }
       catch(Exception e)
       {
    	   str = Log.getText();
    	   str = str + "\n" + "Error: " + e.getMessage();
    	   Log.setText(str);
    	   return null;
       }
       obj[0]= path;
       obj[1]= ontology;
       obj[2]= manager;
       return obj;
	}

}
