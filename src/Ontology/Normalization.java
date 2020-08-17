package Ontology;

import Application.OSType;




//import Main.OntologyManagerTab;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;







//OWL API
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

/**
 * Creates a new ontology, that is the normalization of the 
 * original and places in the same folder as the original file with the same name + "Normalized.owl" at the end
 * 
 * @author Romulo de Carvalho Magalhaes - adapted by Fabio Marcos de Abreu Santos
 *
 */
public class Normalization {

	/**
	 * Creates a new Ontology from a file so it can be normalized, this new file will be at the same place as the
	 * original and have "Normalized.owl" at its end
	 * 
	 * @param path - The path of the original OWL file
	 * @return returnPath - path of the new one to be normalized
	 * @throws OWLOntologyStorageException
	 */
	public String NewNormalization(String path, String name, OSType osn) throws OWLOntologyStorageException
	{
		String normalizedOntologyPath = "";
		try{			
			File startingOntology = new File(path);
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(startingOntology);
            OWLOntologyFormat format = manager.getOntologyFormat(ontology); //get the format of the original ontology to pass on to the normalized one
            IRI normalizedOntologyIRI;
            //IRI ontologyIRI = manager.getOntologyDocumentIRI(ontology);
            if(osn == OSType.Windows)
            {
            	normalizedOntologyPath = name.substring(0,name.lastIndexOf(".")) + "Normalized.owl";
            	normalizedOntologyIRI = IRI.create("http://www.semanticweb.org/ontologies/2014/5/" + normalizedOntologyPath);
            	normalizedOntologyPath = path.substring(0,path.lastIndexOf("\\")+1) + normalizedOntologyPath;
            }else
            {
            	//I can treat Linux and Mac the same way
            	normalizedOntologyPath = name.substring(0,name.lastIndexOf(".")) + "Normalized.owl";
            	normalizedOntologyIRI = IRI.create("http://www.semanticweb.org/ontologies/2014/5/" + normalizedOntologyPath);
            	normalizedOntologyPath = path.substring(0,path.lastIndexOf("/")+1) + normalizedOntologyPath;
            }
            //new file created
            File file = new File(normalizedOntologyPath); 
            //Copying the prefixes of the original
            RDFXMLOntologyFormat rdfSyntaxFormat = new RDFXMLOntologyFormat();
            if(format.isPrefixOWLOntologyFormat()) 
            {
            	rdfSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
            }
            IRI documentIRI = IRI.create("file:"+normalizedOntologyPath);
            SimpleIRIMapper mapper = new SimpleIRIMapper(normalizedOntologyIRI, documentIRI);
            manager.addIRIMapper(mapper);
            OWLOntology normalizedOntology = manager.createOntology(normalizedOntologyIRI);
            manager.saveOntology(normalizedOntology, rdfSyntaxFormat, IRI.create(file.toURI()));
            
		}catch(OWLOntologyCreationException e) 
		{
			normalizedOntologyPath = "Error loading ontology: " + e.getMessage();
			System.out.println(normalizedOntologyPath);
		}catch(OWLOntologyStorageException e) 
		{
			normalizedOntologyPath = "Error saving ontology: " + e.getMessage();
			System.out.println(normalizedOntologyPath);
	    }catch(Exception e)
	    {
	    	normalizedOntologyPath = "Error: " + e.getMessage();
			System.out.println(normalizedOntologyPath);
	    }
		return normalizedOntologyPath;
	}

	/**
	 * Creates a new normalized Ontology at the same folder as the original using the 6 rules
     * (Insert basic concepts in the new Ontology then apply the 6 normalization rules)
     * 
	 * @param pathOntology
	 * @param osn
	 * @return Object containing the path to the normalized ontology and the original ontology IRI
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 */
	public Object[] runOntologyNormalization(String pathOntology, String nameOntology, OSType osn) throws OWLOntologyCreationException, OWLOntologyStorageException 
	{
		Object[] obj = new Object[2];
		OWLOntologyManager managerOnt = OWLManager.createOWLOntologyManager();
        OWLOntologyManager managerOntNormalized = OWLManager.createOWLOntologyManager();
        File fileOntologyLocal = new File(pathOntology);
        OWLOntology ontology = managerOnt.loadOntologyFromOntologyDocument(fileOntologyLocal);
        obj[1] = getOntologyIRI(ontology);
        if(pathOntology.endsWith("Normalized.owl"))
		{
        	obj[0] = pathOntology;
    		return obj;
		}
        String nameFileOntologyNormalized = NewNormalization(pathOntology, nameOntology, osn);
        File fileOntologyNormalized = new File(nameFileOntologyNormalized);
        OWLOntology ontologyNormalized = managerOntNormalized.loadOntologyFromOntologyDocument(fileOntologyNormalized);
        managerOntNormalized = addOntologyNomalized(managerOntNormalized, ontology , ontologyNormalized);
        managerOntNormalized = normalize1(managerOntNormalized, ontology , ontologyNormalized);
        managerOntNormalized = normalize2(managerOntNormalized, ontology , ontologyNormalized);
        managerOntNormalized = normalize3and4(managerOntNormalized, ontology , ontologyNormalized);
        managerOntNormalized = normalize5(managerOntNormalized, ontology , ontologyNormalized);
        managerOntNormalized = normalize6(managerOntNormalized, ontology , ontologyNormalized);
        try{
        	//saves new normalized ontology
        	//other exceptions are treated at the restriction graph...        	
        	managerOntNormalized.saveOntology(ontologyNormalized, IRI.create(fileOntologyNormalized.toURI()));
        }
        catch(OWLOntologyStorageException e) 
        {
        	String error = "Error, could not save ontology: " + e.getMessage();
        	System.out.println(error);
        	obj[0] = error;
        	return obj;
    	}
        obj[0] = nameFileOntologyNormalized;
		return obj;
	}
	
	/**
	 * Returns a string containing the ontology IRI
	 * 
	 * @param ontology
	 * @return
	 */
    private String getOntologyIRI(OWLOntology ontology) 
    {
    	String temp = ontology.toString();
    	temp = temp.substring(temp.indexOf("<") + 1, temp.indexOf(">"));
    	temp = temp.replaceAll("#", "/");
    	char c = temp.charAt(temp.length()-1);
    	if(c != '/')
    		temp = temp + "/";
    	
		return temp;
	}

	/**
     * Insert into the normalized ontology concepts and restritions of the original ontology 
     * 
     * @param manager
     * @param ontology
     * @param ontologyNormalized
     * @return
     */
    public OWLOntologyManager addOntologyNomalized (OWLOntologyManager manager,OWLOntology ontology , OWLOntology ontologyNormalized)
    {
    	// insert the concepts of the original
    	manager = addConceptOntologyNomalized(manager,ontology,ontologyNormalized);
    	// insert the properties of the original
        manager = addPropertyOntologyNomalized(manager,ontology,ontologyNormalized);
        // insert the sub property restritions of the original        
        manager = addSubPropertyOntologyNomalized(manager,ontology,ontologyNormalized);
        // Insert the sub class restritions of the original
        manager = addSubClassOntologyNomalized(manager,ontology,ontologyNormalized);
        // Insert the minimum cardinality axioms
        manager = addObjectObjectMinCardinalityOntologyNomalized(manager,ontology,ontologyNormalized);
        return manager;
    }
    
    /**
     * Insert into the normalized ontology the concepts of the original ontology
     * 
     * @param manager
     * @param ontology
     * @param ontologyNormalized
     * @return
     */
    public OWLOntologyManager addConceptOntologyNomalized (OWLOntologyManager manager,OWLOntology ontology,OWLOntology ontologyNormalized)
    {
    	for (OWLObjectProperty pro : ontology.getObjectPropertiesInSignature()) 
    	{
    		for (OWLObjectPropertyRangeAxiom proRange : ontology.getObjectPropertyRangeAxioms(pro))
    		{
    			manager.addAxiom(ontologyNormalized, proRange);
    		}
    		for (OWLObjectPropertyDomainAxiom proDomain : ontology.getObjectPropertyDomainAxioms(pro))
    		{
                manager.addAxiom(ontologyNormalized, proDomain);
    		}   
        }
    	return manager;
	}
    
    /**
     * Insert into the normalized ontology the properties of the old ontology
     * 
     * @param OWLOntologyManager manager,
     *        OWLOntology ontology ,
     *        OWLOntology ontologyNormalized
     * @return  OWLOntologyManager
    */
    private OWLOntologyManager addPropertyOntologyNomalized(OWLOntologyManager manager,OWLOntology ontology,OWLOntology ontologyNormalized) 
    {
    	for(OWLDataProperty pro : ontology.getDataPropertiesInSignature()) 
    	{
    		for (OWLDataPropertyRangeAxiom proRange : ontology.getDataPropertyRangeAxioms(pro))
    		{
    			manager.addAxiom(ontologyNormalized,proRange);
    		}
    		for (OWLDataPropertyDomainAxiom proDomain : ontology.getDataPropertyDomainAxioms(pro))
    		{
    			manager.addAxiom(ontologyNormalized,proDomain);
    		}
        }
    	return manager;
    }
    
    /**
     * Insert into the normalized ontology the sub properties restritions of the original ontology 
     * 
     * @param manager
     * @param ontology
     * @param ontologyNormalized
     * @return
     */
    private OWLOntologyManager addSubPropertyOntologyNomalized(OWLOntologyManager manager, OWLOntology ontology, OWLOntology ontologyNormalized)
    {
    	OWLDataFactory factory = manager.getOWLDataFactory();
        for (OWLSubObjectPropertyOfAxiom subProp : ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) 
        {
        	OWLObjectPropertyExpression expEspecifica = subProp.getSubProperty();
        	OWLObjectPropertyExpression expGenerica = subProp.getSuperProperty();
        	OWLSubObjectPropertyOfAxiom axiomSubsumes = factory.getOWLSubObjectPropertyOfAxiom(expEspecifica,expGenerica);
            AddAxiom addAxiom = new AddAxiom(ontologyNormalized, axiomSubsumes);
            manager.applyChange(addAxiom);
        }
        return manager;
    }
    
    /**
     * Insert into the normalized ontology the sub classes axioms of the original ontology 
     * that are contain concepts of the Extralite family
     * 
     * @param manager
     * @param ontology
     * @param ontologyNormalized
     * @return
     */
    private OWLOntologyManager addSubClassOntologyNomalized(OWLOntologyManager manager, OWLOntology ontology,OWLOntology ontologyNormalized)
    {
    	OWLDataFactory factory = manager.getOWLDataFactory();
    	Set<OWLSubClassOfAxiom> sub = ontology.getAxioms(AxiomType.SUBCLASS_OF);
    	
    	for(OWLSubClassOfAxiom i : sub) 
    	{
    		OWLObject o = i.getSuperClass();
    		OWLObject o1 = i.getSubClass();
    		ClassExpressionType cet = i.getSuperClass().getClassExpressionType();
    		ClassExpressionType cet1 = i.getSubClass().getClassExpressionType();
    		
    		if(UsefulOWL.isClass(cet)||UsefulOWL.isRestrictionComplementOfClass(cet,o)||UsefulOWL.isRestrictionComplementOfRestrictionCardinality(cet,o)) 
    		{
    			if(UsefulOWL.isClass(cet1)||UsefulOWL.isRestrictionComplementOfClass(cet1,o1)||UsefulOWL.isRestrictionComplementOfRestrictionCardinality(cet1,o1)||UsefulOWL.isObjectObjectMaxCardinality (cet1))
    			{
    				OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom((OWLClassExpression)o1, (OWLClassExpression)o);
            		manager.addAxiom(ontologyNormalized, axiom);
    			}    			
            }
		}
        return manager;
     }
    
    /**
     * Insert into the normalized ontology the minimum cardinality axioms
     * 
     * @param manager
     * @param ontology
     * @param ontologyNormalized
     * @return
     */
    private OWLOntologyManager addObjectObjectMinCardinalityOntologyNomalized(OWLOntologyManager manager, OWLOntology ontology,OWLOntology ontologyNormalized)
    {
    	OWLDataFactory factory = manager.getOWLDataFactory();
    	Set<OWLSubClassOfAxiom> sub = ontology.getAxioms(AxiomType.SUBCLASS_OF);
    	for(OWLSubClassOfAxiom i : sub)
    	{
            if(UsefulOWL.isObjectObjectMinCardinality (i.getSuperClass().getClassExpressionType())) 
            {
            	OWLObjectMinCardinality restritionMinCard = (OWLObjectMinCardinality) i.getSuperClass();
            	OWLObject o = i.getSubClass();
        		ClassExpressionType cet = i.getSubClass().getClassExpressionType();
        		
        		if(UsefulOWL.isClass(cet)||UsefulOWL.isRestrictionComplementOfClass(cet,o)||UsefulOWL.isRestrictionComplementOfRestrictionCardinality(cet,o)||UsefulOWL.isObjectObjectMaxCardinality(cet)) 
        		{
        			OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom((OWLClassExpression)o, restritionMinCard);
            		manager.addAxiom(ontologyNormalized, axiom);
        		}
            }
            else if(UsefulOWL.isObjectObjectMinCardinality (i.getSubClass().getClassExpressionType())) 
            {
            	OWLObjectMinCardinality restritionMinCard = (OWLObjectMinCardinality) i.getSubClass();
            	OWLObject o = i.getSuperClass();
        		ClassExpressionType cet = i.getSuperClass().getClassExpressionType();
        		
        		if(UsefulOWL.isClass(cet)||UsefulOWL.isRestrictionComplementOfClass(cet,o)||UsefulOWL.isRestrictionComplementOfRestrictionCardinality(cet,o)||UsefulOWL.isObjectObjectMaxCardinality(cet)) 
        		{
        			OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom(restritionMinCard, (OWLClassExpression)o);
            		manager.addAxiom(ontologyNormalized, axiom);
        		}
            }
       }
       return manager;
    }
    
    /**
     * Apply the First Normalization Rule: ∃P [is contained in] C == (>= 1 P) [is contained in] C
     * 
     * @param manager
     * @param ontology
     * @param ontologyNormalized
     * @return
     */
    private OWLOntologyManager normalize1(OWLOntologyManager manager, OWLOntology ontology, OWLOntology ontologyNormalized) 
    {
    	// 1st we need to recover all properties from Objects that have Domain
        Set<OWLObjectPropertyDomainAxiom> propriedadeDomain = ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN);
        Set<OWLSubClassOfAxiom> sub = ontology.getAxioms(AxiomType.SUBCLASS_OF);
        OWLDataFactory factory = manager.getOWLDataFactory();
        boolean added = false;
        for(OWLObjectPropertyDomainAxiom i : propriedadeDomain) 
        {
        	if(!i.getDomain().isAnonymous()) 
        	{
        		//Then we create an Minimum Cardinality Restriction for each one
        		OWLClassExpression classe = i.getDomain();
        		OWLObjectPropertyExpression propriedade = i.getProperty();
        		added = false;
        		for(OWLSubClassOfAxiom j : sub)
            	{
        			if(UsefulOWL.isObjectObjectMinCardinality (j.getSubClass().getClassExpressionType())) 
        			{
        				OWLObjectMinCardinality restritionMinCard = (OWLObjectMinCardinality) j.getSubClass();
        				Set<OWLClass> domainsRanges = restritionMinCard.getClassesInSignature();
                        Iterator<OWLClass> k = domainsRanges.iterator();
                        OWLClass domainOrRange = null;
                        if(k.hasNext()) 
                        {
                        	domainOrRange = (OWLClass) k.next();
                    	}
        				if(restritionMinCard.getProperty().equals(propriedade) && domainOrRange.equals(classe))
        				{
        					OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom(restritionMinCard, (OWLClassExpression)classe);
                    		manager.addAxiom(ontologyNormalized, axiom);
                    		added = true;
                    		break;
        				}
        			}
            	}
        		if(!added)
        		{
        			OWLObjectMinCardinality restricaoMin = factory.getOWLObjectMinCardinality(1, propriedade, classe);
            		OWLSubClassOfAxiom axiomSub = factory.getOWLSubClassOfAxiom(restricaoMin, classe);
            		AddAxiom axiom = new AddAxiom(ontologyNormalized,axiomSub);
            		manager.applyChange(axiom);
        		}        		
    		}
    	}
        return manager;
    }

    /**
     * Apply the Second Normalization Rule: ∃P- [is contained in] C == (>= 1 P-) [is contained in] C
     * 
     * @param manager
     * @param ontology
     * @param ontologyNormalized
     * @return
     */
    private OWLOntologyManager normalize2(OWLOntologyManager manager,OWLOntology ontology,OWLOntology ontologyNormalized) 
    {
    	// 1st we need to recover all properties from Objects that have Range(properties where Range isn't Thing)
    	OWLDataFactory factory = manager.getOWLDataFactory();
    	Set<OWLObjectPropertyRangeAxiom> propriedadeRange = ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE);
    	Set<OWLSubClassOfAxiom> sub = ontology.getAxioms(AxiomType.SUBCLASS_OF);
    	boolean added = false;
    	
    	for (OWLObjectPropertyRangeAxiom i : propriedadeRange)
    	{    
    		if(!i.getRange().isAnonymous()) 
    		{
        		//Then we create an Minimum Cardinality Restriction for each one
    			OWLClassExpression classe = i.getRange();
    			OWLObjectPropertyExpression propriedade = i.getProperty();
    			added = false;
    			for(OWLSubClassOfAxiom j : sub)
            	{
        			if(UsefulOWL.isObjectObjectMinCardinality (j.getSubClass().getClassExpressionType())) 
        			{
        				OWLObjectMinCardinality restritionMinCard = (OWLObjectMinCardinality) j.getSubClass();
        				Set<OWLClass> domainsRanges = restritionMinCard.getClassesInSignature();
                        Iterator<OWLClass> k = domainsRanges.iterator();
                        OWLClass domainOrRange = null;
                        if(k.hasNext()) 
                        {
                        	domainOrRange = (OWLClass) k.next();
                    	}
        				if(restritionMinCard.getProperty().equals(propriedade) && domainOrRange.equals(classe))
        				{
        					OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom(restritionMinCard, (OWLClassExpression)classe);
                    		manager.addAxiom(ontologyNormalized, axiom);
                    		added = true;
                    		break;
        				}
        			}
            	}
        		if(!added)
        		{
        			OWLObjectMinCardinality restricaoMin = factory.getOWLObjectMinCardinality(1, propriedade, classe);
        			OWLSubClassOfAxiom axiomSub = factory.getOWLSubClassOfAxiom(restricaoMin, classe);
        			AddAxiom axiom = new AddAxiom(ontologyNormalized,axiomSub);
                    manager.applyChange(axiom);
        		}
    		}
        }
    	return manager;
	}
    
    /**
     * Apply Third and Fourth Normalization Rules:
     * [3rd] C [is contained in] (=< n P) == C [is contained in] ~(=> n+1 P)
     * [4th] C [is contained in] (=< n P-) == C [is contained in] ~(=> n+1 P-)
     * 
     * @param manager
     * @param ontology
     * @param ontologyNormalized
     * @return
     */
    private OWLOntologyManager normalize3and4(OWLOntologyManager manager, OWLOntology ontology, OWLOntology ontologyNormalized) 
    {
    	OWLDataFactory factory = manager.getOWLDataFactory();
    	Set<OWLSubClassOfAxiom> sub = ontology.getAxioms(AxiomType.SUBCLASS_OF);
    	for(OWLSubClassOfAxiom i : sub) 
    	{
    		//Search SUPERCLASS for Maximum Cardinality
    		if(UsefulOWL.isObjectObjectMaxCardinality(i.getSuperClass().getClassExpressionType()))
    		{
    			OWLObjectMaxCardinality restritionMaxCard = (OWLObjectMaxCardinality) i.getSuperClass();
    			int card = restritionMaxCard.getCardinality();
    			OWLObjectPropertyExpression prop = restritionMaxCard.getProperty();
                Set<OWLClass> domainsRanges = restritionMaxCard.getClassesInSignature();
                Iterator<OWLClass> k = domainsRanges.iterator();
                OWLClass domainOrRange = null;
                if(k.hasNext()) 
                {
                	domainOrRange = (OWLClass) k.next();
            	}
                // Creates a Minimum Cardinality with card++
                card++;
                OWLObjectMinCardinality restritionMinCard = factory.getOWLObjectMinCardinality(card, prop, domainOrRange);
                OWLObjectComplementOf notRestritionMinCard = factory.getOWLObjectComplementOf((OWLClassExpression) restritionMinCard); //gets ~MinCard
                
        		OWLObject o = i.getSubClass();
        		ClassExpressionType cet = i.getSubClass().getClassExpressionType();
        		
        		if(UsefulOWL.isClass(cet)||UsefulOWL.isRestrictionComplementOfClass(cet,o)||UsefulOWL.isRestrictionComplementOfRestrictionCardinality(cet,o)||UsefulOWL.isObjectObjectMaxCardinality(cet)) 
        		{
        			OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom((OWLClassExpression)o, (OWLClassExpression)notRestritionMinCard);
            		manager.addAxiom(ontologyNormalized, axiom);
        		}
            }
    		else if(UsefulOWL.isObjectObjectMaxCardinality(i.getSubClass().getClassExpressionType()))
    		{
    			OWLObjectMaxCardinality restritionMaxCard = (OWLObjectMaxCardinality) i.getSubClass();
    			int card = restritionMaxCard.getCardinality();
    			OWLObjectPropertyExpression prop = restritionMaxCard.getProperty();
                Set<OWLClass> domainsRanges = restritionMaxCard.getClassesInSignature();
                Iterator<OWLClass> k = domainsRanges.iterator();
                OWLClass domainOrRange = null;
                if(k.hasNext()) 
                {
                	domainOrRange = (OWLClass) k.next();
            	}
                // Creates a Minimum Cardinality with card++
                card++;
                OWLObjectMinCardinality restritionMinCard = factory.getOWLObjectMinCardinality(card, prop, domainOrRange);
                OWLObjectComplementOf notRestritionMinCard = factory.getOWLObjectComplementOf((OWLClassExpression) restritionMinCard); //gets ~MinCard
                
        		OWLObject o = i.getSuperClass();
        		ClassExpressionType cet = i.getSuperClass().getClassExpressionType();
        		
        		if(UsefulOWL.isClass(cet)||UsefulOWL.isRestrictionComplementOfClass(cet,o)||UsefulOWL.isRestrictionComplementOfRestrictionCardinality(cet,o)||UsefulOWL.isObjectObjectMaxCardinality(cet)) 
        		{
        			OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom((OWLClassExpression)notRestritionMinCard, (OWLClassExpression)o);
            		manager.addAxiom(ontologyNormalized, axiom);
        		}
            }
		}
    	return manager;
	}

    /**
     * Apply the Fifth Normalization Rule: C|D == C [is contained in] ~D
     * 
     * @param manager
     * @param ontology
     * @param ontologyNormalized
     * @return
     */
    private OWLOntologyManager normalize5(OWLOntologyManager manager, OWLOntology ontology, OWLOntology ontologyNormalized) 
    {
    	//Get Disjoint classes
    	OWLDataFactory factory = manager.getOWLDataFactory();
    	Set<OWLDisjointClassesAxiom> classes = ontology.getAxioms(AxiomType.DISJOINT_CLASSES);
    	for(OWLDisjointClassesAxiom i : classes) 
    	{
    		//gets C
    		HashSet<OWLClass> declaration = (HashSet<OWLClass>) i.getClassesInSignature();
    		Iterator<OWLClass> j = declaration.iterator();
    		OWLClass c = (OWLClass) j.next();
    		while(j.hasNext()) 
    		{
    			//gets ~D
    			OWLClass d = (OWLClass) j.next();
    			OWLObjectComplementOf notD = factory.getOWLObjectComplementOf(d);
    			OWLAxiom axiom = factory.getOWLSubClassOfAxiom(c, notD); //C [is contained in] ~D
    			AddAxiom addAxiom = new AddAxiom(ontologyNormalized,axiom);
                manager.applyChange(addAxiom);
                OWLObjectComplementOf notC = factory.getOWLObjectComplementOf(c);
                OWLAxiom axiom1 = factory.getOWLSubClassOfAxiom(d, notC); //D [is contained in] ~D
    			AddAxiom addAxiom1 = new AddAxiom(ontologyNormalized,axiom1);
                manager.applyChange(addAxiom1);
            }
		}
    	return manager;
	}

    /**
     * Apply the Fifth Normalization Rule: P|Q == P [is contained in] ~Q
     * (The 5th and 6th rules are the same with one difference the 5th is for classes and the 6th for properties)
     * 
     * @param manager
     * @param ontology
     * @param ontologyNormalized
     * @return
     */
    private OWLOntologyManager normalize6(OWLOntologyManager manager, OWLOntology ontology, OWLOntology ontologyNormalized) 
    {
    	//Gets Disjoint properties
        OWLDataFactory factory = manager.getOWLDataFactory();
        Set<OWLDisjointObjectPropertiesAxiom> proprObj = ontology.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES);
        for (OWLDisjointObjectPropertiesAxiom i : proprObj)
        {
        	//gets P
            HashSet<OWLObjectProperty> declaration = (HashSet<OWLObjectProperty>) i.getObjectPropertiesInSignature();
			Iterator<OWLObjectProperty> j = declaration.iterator();
            OWLObjectProperty p = (OWLObjectProperty) j.next();
            while(j.hasNext()) 
            {
            	//gets ~Q
            	OWLObjectProperty q = (OWLObjectProperty) j.next();
            	NodeRestrictionComplementOfProperty nodeCompOfProp = new NodeRestrictionComplementOfProperty(q.toString());
            	OWLObjectProperty notQ = nodeCompOfProp.getOWLObject();
            	OWLAxiom axiom = factory.getOWLSubObjectPropertyOfAxiom(p,notQ);//P [is contained in] ~Q
            	AddAxiom addAxiom = new AddAxiom(ontologyNormalized,axiom);
            	manager.applyChange(addAxiom);
            	NodeRestrictionComplementOfProperty nodeCompOfProp1 = new NodeRestrictionComplementOfProperty(p.toString());
            	OWLObjectProperty notP = nodeCompOfProp1.getOWLObject();
            	OWLAxiom axiom1 = factory.getOWLSubObjectPropertyOfAxiom(q,notP);//Q [is contained in] ~P
            	AddAxiom addAxiom1 = new AddAxiom(ontologyNormalized,axiom1);
            	manager.applyChange(addAxiom1);
        	}
        }
        return manager;
    }

}
