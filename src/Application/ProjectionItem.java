package Application;
import Ontology.Node;

/**
 * This class is a representation of the nodes from a given ontology that will be given as a choice for projection
 *
 * 
 *
 */
public class ProjectionItem {

	private String name;
    private Boolean project;
    private Node node;
    
	public ProjectionItem() {
		// TODO Auto-generated constructor stub
	}
	
	public ProjectionItem(String s, Boolean b, Node n)
	{
		this.name = s;
		this.project = b;
		this.node = n;
	}
	
	public String getName() {
        return this.name;
    }

    public void setName(String s) {
        this.name = s;
    }
    
    public Boolean getProjection() {
        return this.project;
    }

    public void setProjection(Boolean b) {
        this.project = b;
    }
    
    public Node getNode() {
        return this.node;
    }

    public void setNode(Node n) {
        this.node = n;
    }
    
    public String getFullName(){
    	String resp = "";
    	resp = this.node.getExpression().toString();
    	resp = resp.substring(1, resp.length()-1);
    	return resp;
    }
    

}
