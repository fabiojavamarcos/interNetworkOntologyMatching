package Ontology;

/**
 * Enum to specify each Node Type in the Ontology 
 * 
 * @author Romulo de Carvalho Magalhaes - adapted by Fabio Marcos de Abreu Santos
 *
 */
public enum NodeType {

	Class(0),
    Property(1),
    RestrictionComplementOfClass(2),
    RestrictionComplementOfProperty(3),
    RestrictionCardinality(4),
    RestrictionComplementOfRestrictionCardinality(5);
	
	public final int NodeType;

    private NodeType(int type)
    {
    	this.NodeType = type;
    }
}
