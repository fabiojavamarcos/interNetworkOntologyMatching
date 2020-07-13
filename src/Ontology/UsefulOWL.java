package Ontology;

import java.util.ArrayList;
import java.util.LinkedList;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

/**
 * Class containing small useful functions for OWL handling,
 * like extracting terms from IRIs and checking OWLObjects type
 * 
 * @author Romulo de Carvalho Magalhaes
 *
 */
public class UsefulOWL {
	//Text Extractors	
	/**
	 * Returns the term of a given IRI
	 * 
	 * @param exp
	 * @return
	 */
	public static String returnIRI(String exp)
	{
		String concept = exp;
        if(exp.contains("<"))
        {
        	if(exp.contains("#"))
        	{
        		concept = concept.replaceAll(concept.substring(exp.indexOf("<"),exp.indexOf("#")+1),"");
    		}else 
    		{
    			if(exp.contains(":")&&(!exp.contains("/")))
    			{
    				concept = concept.replaceAll(concept.substring(exp.indexOf("<"),exp.lastIndexOf(":")+1),"");
				}else
				{
					concept = concept.replaceAll(concept.substring(exp.indexOf("<"),exp.lastIndexOf("/"+"")+1),"");
				}
			}
    	}
        if(exp.contains(">"))
        {
        	concept= concept.replaceAll(concept.substring(concept.indexOf(">"), concept.indexOf(">")+1), "");
        }
        return concept;
    }
	
	/**
	 * Returns the term of a IRIDomain 
	 * 
	 * @param exp
	 * @return
	 */
	public static String returnIRIDomain(String exp)
	{
        String iriDomain = "";
        if(exp.contains("<"))
        {
            if(exp.contains("#"))
            {
            	iriDomain = exp.substring(exp.indexOf("<")+1,exp.lastIndexOf("#")+1);
            	if(iriDomain.contains("<"))
            	{
            		iriDomain = iriDomain.substring(iriDomain.indexOf("<")+1,iriDomain.lastIndexOf("#") + 1);
            	}
            }else 
            {
            	iriDomain = exp.substring(exp.lastIndexOf("<")+ 1, exp.lastIndexOf(">")+ 1);
            	iriDomain = iriDomain.substring( 0 , iriDomain.lastIndexOf("/")+ 1 );
            }
        }
        return iriDomain;
	}
	
	/**
	 * Returns the term of an IRIProp 
	 * 
	 * @param exp
	 * @return
	 */
	public static String returnIRIProp(String exp)
	{
		String iriProp = "";
		if(exp.contains("<"))
		{
			iriProp = exp.substring(exp.indexOf("<")+1,exp.indexOf(">"));
			if(iriProp.contains("#"))
            {
				iriProp = iriProp.substring(0,iriProp.indexOf("#") + 1);
			}else
			{
				iriProp = iriProp.substring( 0 , iriProp.lastIndexOf("/") + 1 );
            }
         }
		return iriProp;
    }
	/**
	 * Extract property from a given Restriction Cardinality
	 * 
	 * @param Restriction
	 * @return
	 */
	public static String returnProp(String Restriction) 
	{
		int fimCard = Restriction.toString().indexOf(" ");
		int fimExpsObjectMinCardinality = Restriction.toString().indexOf(")");
		String expsObjectMinCardinality = Restriction.toString().substring(fimCard + 1, fimExpsObjectMinCardinality);
		int fimProp = expsObjectMinCardinality.indexOf(" ");
		String propIRI = expsObjectMinCardinality.toString().substring(0,fimProp);
		if(propIRI.contains("#"))
		{
		    String prop = expsObjectMinCardinality.substring( propIRI.indexOf("#") + 1, propIRI.indexOf(">"));
		    return prop;
		}
		String prop = expsObjectMinCardinality.substring(propIRI.lastIndexOf("/") + 1, propIRI.indexOf(">"));
		return prop;
	}
	
	/**
	 * Extract Property from OWLObjectPropertyExpression
	 * 
	 * @param objectProperty
	 * @return
	 */
	public static String returnPropObjectProperty(OWLObjectPropertyExpression objectProperty)
    {
		String resp = returnIRI(objectProperty.getSignature().toString()).replace("]", "").replace("[", ""); 
		return resp;
    }
	
	/**
	 * Returns the cardinality of a given Cardinality restriction
	 * 
	 * @param Restriction
	 * @return
	 */
	public static int returnCard(String Restriction)
	{
		int inicioCard = Restriction.toString().lastIndexOf("(");
		int fimCard = Restriction.toString().indexOf(" ");
		int card = Integer.parseInt(Restriction.toString().substring(inicioCard + 1, fimCard));
		return card;
	}
	
	/**
	 * 
	 * @param Restriction
	 * @return
	 */
	public static String returnDomainOrRange(String Restriction)
	{
		int fimCard = Restriction.toString().indexOf("<");
		int fimExpsObjectMinCardinality = Restriction.toString().indexOf(")");
		String expsObjectMinCardinality = Restriction.toString().substring(fimCard, fimExpsObjectMinCardinality + 1);
		int fimProp = expsObjectMinCardinality.indexOf(" ");
		String domainOrRangeIRI = expsObjectMinCardinality.toString().substring(fimProp+1,expsObjectMinCardinality.toString().indexOf(")"));
		String domainOrRange= "owl:Thing";
		if(!domainOrRangeIRI.contains("Thing"))
		{
			if(domainOrRangeIRI.contains("#") && domainOrRangeIRI.contains(">"))
			{
				domainOrRange = domainOrRangeIRI.substring(domainOrRangeIRI.indexOf("#")+1,domainOrRangeIRI.indexOf(">"));
				return domainOrRange;
			}else 
			{
				if(domainOrRangeIRI.lastIndexOf("/") < domainOrRangeIRI.indexOf(">"))
				{
					domainOrRange = domainOrRangeIRI.substring(domainOrRangeIRI.lastIndexOf("/")+1,domainOrRangeIRI.indexOf(">"));
				}
				else if ((!domainOrRangeIRI.contains("/"))&&((domainOrRangeIRI.contains("rdf:"))||(domainOrRangeIRI.contains("RDF:"))))
				{
					domainOrRange = domainOrRangeIRI.replaceAll("<", "");
					domainOrRange = domainOrRange.replaceAll(">", "");
				}					
				return domainOrRange;
			}
	    }
	    return domainOrRange;
	}
	
	public static LinkedList<Integer> getListwithFirstElement(ArrayList<LinkedList<Integer>> aL, int element)
	{
		for(LinkedList<Integer> tempRO : aL)
		{
			if(tempRO.get(0) == element)
			{
				return tempRO;
			}
		}
		return null;
	}
	
	//Type Checkers
	public static boolean isObjectObjectMinCardinality(ClassExpressionType classExpressionType)
    {
       if(classExpressionType.getName().equals("ObjectMinCardinality"))
           return true;
       else
           return false;
    }

    public static boolean isObjectObjectMinCardinality(String expression)
    {
       if(expression.contains("ObjectMinCardinality"))
           return true;
       else
           return false;
    }

    public static boolean isObjectComplementOf(String expression)
    {
       if(expression.contains("ObjectComplementOf"))
           return true;
       else
           return false;
    }


    public static boolean isObjectObjectMaxCardinality(ClassExpressionType classExpressionType)
    {
       if(classExpressionType.getName().equals("ObjectMaxCardinality"))
           return true;
       else
           return false;
    }

    public static boolean isClass(ClassExpressionType classExpressionType)
    {
       if(classExpressionType.getName().equals("Class"))
           return true;
       else
           return false;
    }

    public static boolean isRestrictionComplementOfClass(ClassExpressionType classExpressionType,OWLObject desc)
    { 
       if((classExpressionType.getName().equals("ObjectComplementOf"))&&(!desc.toString().contains("ObjectMinCardinality"))&&(!desc.toString().contains("ObjectMaxCardinality")))
           return true;
       else
           return false;
    }

    public static boolean isRestrictionComplementOfRestrictionCardinality(ClassExpressionType classExpressionType,OWLObject desc)
    {
       if((classExpressionType.getName().equals("ObjectComplementOf"))&&(desc.toString().contains("ObjectMinCardinality")))
           return true;
       else
           return false;
    }
}
