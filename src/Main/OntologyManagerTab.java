/**
 * Creates main tab and GUI inside protege to implement Logical Operations over Ontologies
 * 
 * @author Romulo de Carvalho Magalhaes
 * @author Fabio Marcos de Abreu Santos - alter to run over network of ontologies.
 *
 */

package Main;



import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import java.sql.Timestamp;
import java.util.Collections;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java.awt.Cursor;
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

//OWL API
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
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
import org.semanticweb.owlapi.model.OWLException;
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













//my imports
import Ontology.BreadthFirstSearch;
import Ontology.Graph;
import Ontology.Graph.Edge;
import Ontology.Node;
import Ontology.NodeClass;
import Ontology.NodeProperty;
import Ontology.NodeRestrictionCardinality;
import Ontology.NodeRestrictionComplementOfClass;
import Ontology.NodeRestrictionComplementOfProperty;
import Ontology.NodeRestrictionComplementOfRestrictionCardinality;
import Ontology.NodeType;
import Ontology.Normalization;
import Ontology.ConstraintGraph;
import Ontology.SaveOntology;
import Ontology.UsefulOWL;
import Application.OSType;
import Application.ProjectionItem;
import Application.ProjectionTableModel;
import edu.stanford.smi.protege.widget.AbstractTabWidget;

@SuppressWarnings({ "serial", "unused" })
public class OntologyManagerTab extends JFrame {
	private JButton btnLoadOnt1 = new JButton("Load Ontology 1");
	private JButton btnShowHideIRI1 = new JButton("Show/Hide Ontology 1 IRIs");
	private JButton btnLoadOnt2 = new JButton("Load Ontology 2");
	private JButton btnShowHideIRI2 = new JButton("Show/Hide Ontology 2 IRIs");
	private JButton btnRun = new JButton("Run");
	private JButton btnSave = new JButton("Save Results");
	private JButton btnShowHideIRIResults = new JButton("Show/Hide Results IRIs");
	private JButton btnCleanUp = new JButton("Minimize Graph");
    private JTextArea Log;
    private DefaultCaret LogCaret;
    private JScrollPane jScrollPaneLog;
    private JLabel warninglbl = new JLabel();
	private JLabel lbl1 = new JLabel();
	private JLabel lbl2 = new JLabel();
	private JComboBox jComboBoxOperation;
	boolean ProjOp = false;
	boolean HideIRI1 = false;
	boolean HideIRI2 = false;
	boolean HideIRIResults = false;
	boolean CleanUp = false;
	private JTable jTable1; 			//Ontology1 Table
    private JScrollPane jScrollPane1;
    private JTable jTable2; 			//Ontology2 Table
    private JScrollPane jScrollPane2;
    private JTable jTableInc; 			//Inclusion Constraints Table
    private JScrollPane jScrollPaneInc;
    private JScrollPane jScrollPaneMain;
    private Component root;
    private String pathOnt1; 			//Ontology1 Path
    private String nameOnt1; 			//Ontology1 Name
    private Object[] Ont1; 				//Object used to store Ontology 1
    
    /*		obj[0] = ontology;
			obj[1] = factory;
			obj[2] = manager;
            obj[3] = ontPath;
  
            */
    private Graph gOnt1; 				//Graph for Ontology 1
    private String IRIOnt1;				//IRI for the original Ontology 1
    private String pathOnt2; 			//Ontology2 Path
    private String nameOnt2; 			//Ontology2 Name
    private Object[] Ont2;				//Object used to store Ontology 2
    private Graph gOnt2; 				//Graph for Ontology 2
    private String IRIOnt2;				//IRI for the original Ontology 2
    private Graph gResults; 			//Graph for resulting Ontology
    private String pathOntResults; 		//Resulting Ontology save path
    private String nameOntResults;		//Resulting Ontology save name
    private boolean ShowBottomWarning;

    private ArrayList <String> network1 = new ArrayList(); // network 1 to match
    private ArrayList <String> network2 = new ArrayList(); // network 2 to match
    //private Object intranetworkAlignments [][]= new Object [100][100]; // RDF files form intranetwork alignments 1st dimension file name 2nd dimension number of the network they will be used 
    private ArrayList <String> intranetworkAlignmentsFileNames = new ArrayList();
    private String intranetworkFileNet1, intranetworkFileNet2;
    private String path; // path to OWL files
    private int n1; // n ontologies to compose network 1
    private int n2; // n ontologies to compose network 2
    private int n3; // n intranetwork alignments from both network 1 and 2
    private String LogBatch = " ";
    private ArrayList ontologiesProcessed = new ArrayList(); // ontologies processed including their graphs 
    
 // salva trabalhos temporários
 	private	Map <String,String> partialUnionResultsN1 = new HashMap<String,String>();
 	private	Map <String,String> partialUnionResultsN2 = new HashMap<String,String>();
 	private	Map <String,String> partialIntersectionResultsN1N2Names = new HashMap<String,String>();
 		
 	private	List <String> partialIntersectionResultsN1N2 = new ArrayList<String>();
 	private	Map <String,String> partialDifferenceResultsN1 = new HashMap<String,String>();
 	private	Map <String,String> partialDifferenceResultsN2 = new HashMap<String,String>();
 	
 	private String lastUnionN1;
    private String lastUnionN2;
    private String lastDifferenceN1;
    private String lastDifferenceN2;
    
	public OSType osType;
    
	/**
	 * Basic GUI Design plus initializing Class variables 
	 */
	public void initialize() 
	{
		getOSType();
        //this.setLabel("Ontology Manager Tab"); //Tab name
        this.setTitle("Ontology Manager Stand Alone");
		pathOnt1 = "";		
		pathOnt2 = "";
		nameOnt1 = "";
		nameOnt2 = "";
		pathOntResults = "";
		nameOntResults = "";
		gOnt1 = null;
		gOnt2 = null;
		gResults = null;
		this.setLayout(null);
		lbl2.setBounds(640, 420, 60, 20);
		lbl2.setText("Log: ");
		this.add(lbl2);
		Log = new JTextArea();
		jScrollPaneLog = new JScrollPane();
		Log.setColumns(20);
		Log.setRows(10);
		Log.setBackground(Color.WHITE);
		Log.setEditable(false);
		LogCaret = (DefaultCaret)Log.getCaret();
		LogCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		jScrollPaneLog.setViewportView(Log);
		jScrollPaneLog.setBounds(640, 440, 620, 215);
		this.add(jScrollPaneLog);
		lbl1.setBounds(10, 6, 100, 20);
		lbl1.setText("Operation: ");
		this.add(lbl1);
		warninglbl.setForeground(Color.red);
		warninglbl.setBounds(8, 375, 1500, 20);
		warninglbl.setFont(new Font("Lucida Grande", Font.BOLD, 10));
		warninglbl.setText("");
		warninglbl.setVisible(false);
		this.add(warninglbl);		
		//setting up table forOntology 1
		jTable1 = new JTable();
		jScrollPane1 = new JScrollPane();
		DefaultTableModel model1 = new DefaultTableModel(
	               new Object [][] {

	               },
	               new String [] {
	                   "Ontology 1", ""
	               });				
		jTable1.setModel(model1);     
		jScrollPane1.setBounds(6, 40, 620, 320);
        jScrollPane1.setViewportView(jTable1);
        this.add(jScrollPane1);      
        btnLoadOnt1.setBounds(6, 365, 305, 29);
		this.add(btnLoadOnt1);
		btnLoadOnt1.addActionListener(new OpenO1());
		btnShowHideIRI1.setBounds(321, 365, 305, 29);
		this.add(btnShowHideIRI1);
		btnShowHideIRI1.addActionListener(new ShowHideIRI1());
		HideIRI1=false;
		btnShowHideIRI1.setEnabled(false);
		//setting up table forOntology 2
        jTable2 = new JTable();
		jScrollPane2 = new JScrollPane();
		DefaultTableModel model2 = new DefaultTableModel(
	               new Object [][] {

	               },
	               new String [] {
	                   "Ontology 2", ""
	               });				
		jTable2.setModel(model2);
        jScrollPane2.setBounds(640, 40, 620, 320);
        jScrollPane2.setViewportView(jTable2);        
        this.add(jScrollPane2);
		btnLoadOnt2.setBounds(640, 365, 305, 29);		
		this.add(btnLoadOnt2);
		btnLoadOnt2.addActionListener(new OpenO2());
		btnShowHideIRI2.setBounds(955, 365, 305, 29);
		this.add(btnShowHideIRI2);
		btnShowHideIRI2.addActionListener(new ShowHideIRI2());
		HideIRI2=false;
		btnShowHideIRI2.setEnabled(false);
		//setting up table forResults, Inclusion Constraints
		jTableInc = new JTable();
		jScrollPaneInc = new JScrollPane();
		jTableInc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Resulting ontology", ""
            }
        ));
		jTableInc.setEnabled(false);
		jScrollPaneInc.setBounds(6, 420, 620, 200);
        jScrollPaneInc.setViewportView(jTableInc);
        this.add(jScrollPaneInc);
        btnSave.setBounds(420, 625, 202, 29);
		this.add(btnSave);
		btnSave.addActionListener(new SaveR());
		btnSave.setEnabled(false);
		btnShowHideIRIResults.setBounds(213, 625, 202, 29);
		this.add(btnShowHideIRIResults);
		btnShowHideIRIResults.addActionListener(new ShowHideIRIResults());
		btnShowHideIRIResults.setEnabled(false);
		HideIRIResults = false;
		btnCleanUp.setBounds(6, 625, 202, 29);
		this.add(btnCleanUp);
		btnCleanUp.addActionListener(new MinimizeGraph());
		btnCleanUp.setEnabled(false);
		CleanUp = false;
        //ComboBox for operation selection
        jComboBoxOperation = new JComboBox();
		jComboBoxOperation.setBounds(80, 6, 100, 20);
		jComboBoxOperation.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Union", "Intersection", "Projection", "Difference"}));
		jComboBoxOperation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	jComboBoxOperationActionPerformed(evt);
            }
        });
		jComboBoxOperation.setSelectedIndex(0);
		this.add(jComboBoxOperation);
		btnRun.setBounds(190, 6, 117, 22);
		this.add(btnRun);
		btnRun.addActionListener(new RunOnt());
		ShowBottomWarning = true;
		//Welcome Message
		Log.setText("Welcome to the Ontology Manager Tab! \nDeveloped by Romulo de Carvalho Magalhaes \n");
		this.pack();
		this.setVisible(true);
	}
	
	private void getOSType() {
		// TODO Auto-generated method stub
		//Getting OS type
		String os = System.getProperty("os.name");
		os = os.substring(0, 3);
        if(os.equals("Mac")) 
        {
        	osType = OSType.Mac;
        }
        else if(os.equals("Lin"))
        {
        	osType = OSType.Linux;
        }
        else if(os.equals("Win")) 
        {
        	osType = OSType.Windows;
        }
	}

	/**
	 * Manages GUI correctly for each operation selected
	 * 
	 * @param evt
	 */
	private void jComboBoxOperationActionPerformed(java.awt.event.ActionEvent evt)
	{
		try{
			String str = (String)(jComboBoxOperation.getSelectedItem());
			if(str.equals("Projection"))
			{
				if(!ProjOp)
				{
			        //btnLoadOnt1.setBounds(6, 365, 300, 29);
					btnLoadOnt1.setText("Load Ontology Projection");
					btnLoadOnt2.setVisible(false);
					btnShowHideIRI2.setVisible(false);
					ProjOp = true;
					Ont2 = null;
					gOnt2 = null;
					//populate projection table in Ontology 2 model
					if(gOnt1 != null)
					{
						ProjectionTableModel model2 = new ProjectionTableModel();
	    				jTable2.setModel(model2);
	    				fillProjectionList(gOnt1,model2);
						
					}else
					{
						//clear table 2
						DefaultTableModel model2 = new DefaultTableModel(
				                new Object [][] {
	
				                },
				                new String [] {
				                    "Vocabulary", "Choice"
				                }
				            );
						jTable2.setModel(model2);
					}
					
				}
			}
			else 
			{
				if(ProjOp)
				{
					//btnLoadOnt1.setBounds(6, 345, 117, 29);
					btnLoadOnt1.setText("Load Ontology 1");
					btnLoadOnt2.setVisible(true);
					btnShowHideIRI2.setVisible(true);
					btnShowHideIRI2.setEnabled(false);
					//clear table 2
					DefaultTableModel model2 = new DefaultTableModel(
				               new Object [][] {
				            		   
				               },
				               new String [] {
				                   "Ontology 2", ""
				               });				
			        jTable2.setModel(model2);
					ProjOp = false;
					Ont2 = null;
					gOnt2 = null;
				}
			}
		}catch(Exception ex)
		{
			String str = Log.getText() + "\n" + ex.getMessage();
			Log.setText(str);
			return;
		}
	}
	
	/**
	 * Given a node n returns its label according to its type with IRI
	 * 
	 * @param n
	 * @return
	 */
	public static String getNodeLabelWithIRI(Node n)
	{
        String label = "";
        NodeType nType = n.getNodeType();
        switch(nType)
        {
            case Class :
               NodeClass nC = (NodeClass)n;
               label = nC.getDescIRI() + nC.getDescClass();
               break;
            case Property :
               NodeProperty nP = (NodeProperty)n;
               label = nP.getDescIRI() + nP.getDescProp();
               break;
            case RestrictionCardinality :
                NodeRestrictionCardinality nRC = (NodeRestrictionCardinality)n;
                label = "(" + ">=" + nRC.getCard() + "  " + nRC.getIRIProp() + nRC.getDescProp() + "  " + nRC.getIRIDomain() + nRC.getDescRangeOrDomain() + ")";
                break;
            case RestrictionComplementOfClass :
                NodeRestrictionComplementOfClass nNC = (NodeRestrictionComplementOfClass)n;
                label = nNC.getDescIRI() + "~" + nNC.getDescClass();
                break;
            case RestrictionComplementOfProperty :
                NodeRestrictionComplementOfProperty nNP = (NodeRestrictionComplementOfProperty)n;
                label = nNP.getDescIRI() + "~" + nNP.getDescProp();
                break;
            case RestrictionComplementOfRestrictionCardinality :
                NodeRestrictionComplementOfRestrictionCardinality nNRC = (NodeRestrictionComplementOfRestrictionCardinality)n;
                label = "~(" + ">=" + nNRC.getCard() + "  " + nNRC.getiRIProp() + nNRC.getDescProp() + "  " + nNRC.getIRIDomain() + nNRC.getDescRangeOrDomain() + ")";
                break;
        }
        return label;
	}
	
	/**
	 * Given a node n returns its label according to its type without IRI
	 * 
	 * @param n
	 * @return
	 */
	public static String getNodeLabelWithoutIRI(Node n)
	{
        String label = "";
        NodeType nType = n.getNodeType();
        switch(nType)
        {
            case Class :
               NodeClass nC = (NodeClass)n;
               label = nC.getDescClass();
               break;
            case Property :
               NodeProperty nP = (NodeProperty)n;
               label = nP.getDescProp();
               break;
            case RestrictionCardinality :
                NodeRestrictionCardinality nRC = (NodeRestrictionCardinality)n;
                label = "(" + ">=" + nRC.getCard() + "  " + nRC.getDescProp() + "  " + nRC.getDescRangeOrDomain() + ")";
                break;
            case RestrictionComplementOfClass :
                NodeRestrictionComplementOfClass nNC = (NodeRestrictionComplementOfClass)n;
                label = "~" + nNC.getDescClass();
                break;
            case RestrictionComplementOfProperty :
                NodeRestrictionComplementOfProperty nNP = (NodeRestrictionComplementOfProperty)n;
                label = "~" + nNP.getDescProp();
                break;
            case RestrictionComplementOfRestrictionCardinality :
                NodeRestrictionComplementOfRestrictionCardinality nNRC = (NodeRestrictionComplementOfRestrictionCardinality)n;
                label = "~(" + ">=" + nNRC.getCard() + "  " + nNRC.getDescProp() + "  " + nNRC.getDescRangeOrDomain() + ")";
                break;
        }
        return label;
	}
	
	/**
	 * Fill projection list according to Ontology Graph gOnt and return it
	 * 
	 * @param gOnt
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<ProjectionItem> fillProjectionList(Graph gOnt, ProjectionTableModel model)
	{
		ArrayList<ProjectionItem> ProjectionItemList = new ArrayList<ProjectionItem>();
        Set cont = gOnt.getVertices().entrySet();
        Iterator i = (Iterator) cont.iterator();
        while(i.hasNext())
        {
        	ProjectionItem pI = new ProjectionItem();
        	Map.Entry<Integer, Node> n = (Map.Entry<Integer, Node>)i.next();
        	if((n.getValue().getNodeType().equals(NodeType.Class)) || (n.getValue().getNodeType().equals(NodeType.Property)))
        	{
        		String description;
        		if(HideIRI1)
        		{
        			description = getNodeLabelWithoutIRI(n.getValue());
        		}
        		else
        		{
        			description = getNodeLabelWithIRI(n.getValue());
        		}
        		if(!description.contains("~"))
        		{
        			pI.setName(description);
        			pI.setProjection(Boolean.FALSE);
        			pI.setNode(n.getValue());
        			ProjectionItemList.add(pI);
    			}
    		}
    	}
        Collections.sort(ProjectionItemList, new Comparator(){
        	public int compare(Object o1, Object o2) {
                return (((ProjectionItem)o1).getName()).compareTo(((ProjectionItem)o2).getName());
            }
        });
        
        i = (Iterator) ProjectionItemList.iterator();
        while(i.hasNext())
        {
        	ProjectionItem pI = (ProjectionItem)i.next();
        	model.insert(pI);
        }
        
        return ProjectionItemList;
	}
	
	public String getComparatorString(Node n)
	{
		NodeType type = n.getNodeType();
		switch(type)
    	{
		   case Class :
		       NodeClass nc = (NodeClass)n;
		       return nc.getDescClass();
		   case Property :
		       NodeProperty np = (NodeProperty)n;
		       return np.getDescProp();
		   case RestrictionCardinality :
			   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n;
		       return nrc.getExpression();
		   case RestrictionComplementOfClass :
			   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n;
		       return notClass.getDescClass();
		   case RestrictionComplementOfProperty :
			   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n;
		       return notProp.getDescProp();
		   case RestrictionComplementOfRestrictionCardinality :
			   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n;
		       return notRestCard.getExpression();
		   default: 
			   break;
	    }
		return null;
	}
	
	/**
	 * Fill DefaultTableModel model with the correspondent Ontology Graph gOnt with IRIs
	 * 
	 * @param gOnt
	 * @param model
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DefaultTableModel fillJTableWithIRI(Graph gOnt, DefaultTableModel model)
	{
		String str = Log.getText();
		Map<Integer, Node> verticesOntOrdenado = gOnt.getVertices();
		Object[] a = verticesOntOrdenado.entrySet().toArray();
		Arrays.sort(a, new Comparator(){
            public int compare(Object o1, Object o2)
            {
            	Node n1 = (Node)((Map.Entry) (o1)).getValue();
                Node n2 = (Node)((Map.Entry) (o2)).getValue();
                String s1 = getComparatorString(n1);
                String s2 = getComparatorString(n2);
                return ((Comparable) s1).compareTo(s2);
            }
		});
		
        String oExp = "";
        String dExp = "";
        Set cont = verticesOntOrdenado.entrySet();
        Iterator i = (Iterator) cont.iterator();
        int bottomCount = 0;
        while(i.hasNext()) 
        {
        	Map.Entry<Integer, Node> temp = (Map.Entry<Integer, Node>) i.next();
        	Node n = temp.getValue();
        	if(!n.isRemove())
        	{
	        	if(!CleanUp)
	        	{
		        	if(n.isNodeBottom())
		        	{
		        		oExp = getNodeLabelWithIRI(n);
		        		model.addRow(new String [] {oExp, "      _|_ - node, Level "+n.getLevel()});
		        		bottomCount++;
		        	}	
		            LinkedList<Integer> l = gOnt.getList(n.getId());
		            Node nodeOrigin;
		            HashMap<Integer,Node> vertices = gOnt.getVertices();
		            if((l!= null)&&(!l.isEmpty()))
		            {
		            	nodeOrigin = vertices.get(l.get(0));
		        	    oExp = getNodeLabelWithIRI(nodeOrigin);
		        	    if((l.size() == 1))
		        	    {
		        	    	model.addRow(new String [] {oExp, "            "});
		    		    }
		        	    if((l.size() > 0))
		        	    {
		        	    	for(int k = 1; k < l.size(); k++)
		        		    {
		        			    Node nodeDestiny =  vertices.get(l.get(k));
		                        dExp = getNodeLabelWithIRI(nodeDestiny);
		                        model.addRow(new String [] {oExp, dExp});
		                    }
		    		    }
		    	    }
	        	}
	        	else
	        	{
	        		if(n.isNodeBottom())
		        	{
		        		oExp = getNodeLabelWithIRI(n);
		        		model.addRow(new String [] {oExp, "      _|_ - node, Level "+n.getLevel()});
		        		bottomCount++;
		        	}
	        		else
	        		{
			            LinkedList<Integer> l = gOnt.getList(n.getId());
			            Node nodeOrigin;
			            HashMap<Integer,Node> vertices = gOnt.getVertices();
			            if((l!= null)&&(!l.isEmpty()))
			            {
			            	nodeOrigin = vertices.get(l.get(0));
			        	    oExp = getNodeLabelWithIRI(nodeOrigin);
			        	    if((l.size() == 1))
			        	    {
			        	    	model.addRow(new String [] {oExp, "            "});
			    		    }
			        	    if((l.size() > 0))
			        	    {
			        	    	for(int k = 1; k < l.size(); k++)
			        		    {
			        			    Node nodeDestiny =  vertices.get(l.get(k));
			                        dExp = getNodeLabelWithIRI(nodeDestiny);
			                        model.addRow(new String [] {oExp, dExp});
			                    }
			    		    }
			    	    }
	        		}
	        	}
        	}
        }
        if((bottomCount > 0)&&(ShowBottomWarning))
        {
        	str = str +"\nWarning: " + bottomCount +" bottom nodes found!";
        	Log.setText(str);
        	ShowBottomWarning = false;
        }
        return model;
	}
	
	/**
	 * Fill DefaultTableModel model with the correspondent Ontology Graph gOnt without IRIs
	 * 
	 * @param gOnt
	 * @param model
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DefaultTableModel fillJTableWithoutIRI(Graph gOnt, DefaultTableModel model)
	{
		String str = Log.getText();
		Map<Integer, Node> verticesOntOrdenado = gOnt.getVertices();
		Object[] a = verticesOntOrdenado.entrySet().toArray();
		Arrays.sort(a, new Comparator(){
            public int compare(Object o1, Object o2)
            {
            	Node n1 = (Node)((Map.Entry) (o1)).getValue();
                Node n2 = (Node)((Map.Entry) (o2)).getValue();
                String s1 = getComparatorString(n1);
                String s2 = getComparatorString(n2);
                return ((Comparable) s1).compareTo(s2);
            }
		});
		
        String oExp = "";
        String dExp = "";
        Set cont = verticesOntOrdenado.entrySet();
        Iterator i = (Iterator) cont.iterator();
        int bottomCount = 0;

        while(i.hasNext()) 
        {
        	Map.Entry<Integer, Node> temp = (Map.Entry<Integer, Node>) i.next();
        	Node n = temp.getValue();
        	if(!n.isRemove())
        	{
	        	if(!CleanUp)
	        	{
		        	if(n.isNodeBottom())
		        	{
		        		oExp = getNodeLabelWithoutIRI(n);
		        		model.addRow(new String [] {oExp, "      _|_ - node, Level "+n.getLevel()});
		        		bottomCount++;
		        	}
		            LinkedList<Integer> l = gOnt.getList(n.getId());
		            Node nodeOrigin;
		            HashMap<Integer,Node> vertices = gOnt.getVertices();
		            if((l!= null)&&(!l.isEmpty()))
		            {
		            	nodeOrigin = vertices.get(l.get(0));
		        	    oExp = getNodeLabelWithoutIRI(nodeOrigin);
		        	    if((l.size() == 1))
		        	    {
		        	    	model.addRow(new String [] {oExp, "            "});
		    		    }
		        	    if((l.size() > 0))
		        	    {
		        	    	for(int k = 1; k < l.size(); k++)
		        		    {
		        			    Node nodeDestiny =  vertices.get(l.get(k));
		                        dExp = getNodeLabelWithoutIRI(nodeDestiny);
		                        model.addRow(new String [] {oExp, dExp});
		                    }
		    		    }
		    	    }
	        	}
	        	else
	        	{
	        		if(n.isNodeBottom())
		        	{
		        		oExp = getNodeLabelWithoutIRI(n);
		        		model.addRow(new String [] {oExp, "      _|_ - node, Level "+n.getLevel()});
		        		bottomCount++;
		        	}
	        		else
	        		{
			            LinkedList<Integer> l = gOnt.getList(n.getId());
			            Node nodeOrigin;
			            HashMap<Integer,Node> vertices = gOnt.getVertices();
			            if((l!= null)&&(!l.isEmpty()))
			            {
			            	nodeOrigin = vertices.get(l.get(0));
			        	    oExp = getNodeLabelWithoutIRI(nodeOrigin);
			        	    if((l.size() == 1))
			        	    {
			        	    	model.addRow(new String [] {oExp, "            "});
			    		    }
			        	    if((l.size() > 0))
			        	    {
			        	    	for(int k = 1; k < l.size(); k++)
			        		    {
			        			    Node nodeDestiny =  vertices.get(l.get(k));
			                        dExp = getNodeLabelWithoutIRI(nodeDestiny);
			                        model.addRow(new String [] {oExp, dExp});
			                    }
			    		    }
			    	    }
	        		}
	        	}
        	}
        }
        /*
        if(bottomCount > 0)
        {
        	str = str +"\nWarning: " + bottomCount +" bottom nodes found!";
        	Log.setText(str);
        }
        */
        return model;
	}
	
	/**
	 * Blocks or enable tab buttons
	 * 
	 * @param b
	 */
	public void enableButtons(Boolean b)
	{
		jComboBoxOperation.setEnabled(b);
		btnRun.setEnabled(b);
		btnLoadOnt1.setEnabled(b);
		btnLoadOnt2.setEnabled(b);
		
		if((this.gOnt1 != null)&&(this.gOnt1.getNumVertices() > 0))
		{
			btnShowHideIRI1.setEnabled(b);
		}
		else
		{
			btnShowHideIRI1.setEnabled(false);
		}
		
		if((this.gOnt2 != null)&&(this.gOnt2.getNumVertices() > 0))
		{
			btnShowHideIRI2.setEnabled(b);
		}
		else
		{
			btnShowHideIRI2.setEnabled(false);
		}
		
		if((this.gResults != null)&&(this.gResults.getNumVertices() > 0))
		{
			btnSave.setEnabled(b);
			btnShowHideIRIResults.setEnabled(b);
			btnCleanUp.setEnabled(b);
		}
		else
		{
			btnShowHideIRIResults.setEnabled(false);
			btnCleanUp.setEnabled(false);
		}
	}
	
	/**
	 * Implements Show and Hide action for the Elements IRI in Ontology 1 
	 *
	 * @author Rômulo de Carvalho Magalhães
	 *
	 */
	class ShowHideIRI1 implements ActionListener {
		public void actionPerformed(ActionEvent e) 
	    {
			if(gOnt1 != null)
			{
				root = SwingUtilities.getRoot((JButton) e.getSource());
				String str = Log.getText();
				try{
					enableButtons(false);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			if(HideIRI1)
	    			{
	    				HideIRI1 = false;
	    				//Cleaning table
		    			DefaultTableModel model1 = new DefaultTableModel(
		    					new Object [][] {

		    					},
		    					new String [] {
		    							nameOnt1, ""
		    					});
		    			
		    			jTable1.setModel(model1);
		    			//fill table with ontology
		    			model1 = fillJTableWithIRI(gOnt1, model1);
	    			}
	    			else
	    			{
	    				HideIRI1 = true;
	    				//Cleaning table
		    			DefaultTableModel model1 = new DefaultTableModel(
		    					new Object [][] {

		    					},
		    					new String [] {
		    							nameOnt1, ""
		    					});
		    			
		    			jTable1.setModel(model1);
		    			//fill table with ontology
		    			model1 = fillJTableWithoutIRI(gOnt1, model1);
	    			}
	    			str = (String)(jComboBoxOperation.getSelectedItem());
	    			if(str.equals("Projection"))
	    			{
	    				ProjectionTableModel model2 = new ProjectionTableModel();
	    				jTable2.setModel(model2);
	    				fillProjectionList(gOnt1, model2);	    				
	    			}
	    			//str = str + "\n" + "message...";
	    			//Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				catch(Exception ex)
				{
					str = Log.getText() + "\nError : " + ex.getMessage();
	    			Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			}
	    }
	}
	
	/**
	 * Implements Show and Hide action for the Elements IRI in Ontology 2 
	 *
	 * @author Rômulo de Carvalho Magalhães
	 *
	 */
	class ShowHideIRI2 implements ActionListener {
		public void actionPerformed(ActionEvent e) 
	    {
			if(gOnt2 != null)
			{
				root = SwingUtilities.getRoot((JButton) e.getSource());
				String str = Log.getText();
				try{
					enableButtons(false);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			if(HideIRI2)
	    			{
	    				HideIRI2 = false;
	    				//Cleaning table
		    			DefaultTableModel model2 = new DefaultTableModel(
		    					new Object [][] {

		    					},
		    					new String [] {
		    							nameOnt2, ""
		    					});
		    			
		    			jTable2.setModel(model2);
		    			//fill table with ontology
		    			model2 = fillJTableWithIRI(gOnt2, model2);
	    			}
	    			else
	    			{
	    				HideIRI2 = true;
	    				//Cleaning table
		    			DefaultTableModel model2 = new DefaultTableModel(
		    					new Object [][] {

		    					},
		    					new String [] {
		    							nameOnt2, ""
		    					});
		    			
		    			jTable2.setModel(model2);
		    			//fill table with ontology
		    			model2 = fillJTableWithoutIRI(gOnt2, model2);
	    			}
	    			//str = str + "\n" + "message...";
	    			//Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				catch(Exception ex)
				{
					str = Log.getText() + "\nError : " + ex.getMessage();
	    			Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			}
	    }
	}
	
	/**
	 * Implements Show and Hide action for the Elements IRI in Ontology 2 
	 *
	 * @author Rômulo de Carvalho Magalhães
	 *
	 */
	class MinimizeGraph implements ActionListener {
		public void actionPerformed(ActionEvent e) 
	    {
			if(gResults != null)
			{
				root = SwingUtilities.getRoot((JButton) e.getSource());
				String str = Log.getText();
				try{
					enableButtons(false);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			CleanUp = true;
	    			
	    			Thread worker = new Thread() {
	    				@SuppressWarnings({ "rawtypes", "unchecked" })
						public void run() {
	    					String str = Log.getText() + "\nMinimizing Resulting Graph";
	    					try{
	    						//Evaluate cardinality restrictions according to the Procedure realized
	    						HashMap<Integer,Node> vertices = gResults.getVertices();
	    						ArrayList<LinkedList<Integer>> adj = gResults.getAdj();
	    						
	    						LinkedList<Integer> Nodes = new LinkedList<Integer>();
	    						LinkedList<Integer> MatchedNodes = new LinkedList<Integer>();
	    						
	    						Set s = vertices.entrySet();
	    				        Iterator it = (Iterator) s.iterator();
	    				        
	    				        while(it.hasNext()) 
	    				        {
	    				            Map.Entry<Integer, Node> temp = (Map.Entry<Integer, Node>) it.next();
	    				            Node n = temp.getValue();
	    				            NodeType type = n.getNodeType();
	    				            int id = n.getId();
	    				    		if((type ==  NodeType.RestrictionCardinality)||(type ==  NodeType.RestrictionComplementOfRestrictionCardinality))
	    				    		{
	    				    			if(!Nodes.contains(id)&&!MatchedNodes.contains(id))
	    				    			{
	    				    				int n2fid = Graph.findDifferentNodeRCKey(n,gResults);
	    				    				if(n2fid != -1)
	    				    				{
	    				    					Node n2 = vertices.get(n2fid);
	    				    					if((n.isNodeBottom() == n2.isNodeBottom())
	    				    							&&(n.isNodeTop() == n.isNodeTop()))
	    				    					{
		    				    					Nodes.add(id);
		    				    					MatchedNodes.add(n2fid);
		    				    					
		    				    					if(type ==  NodeType.RestrictionCardinality)
		    				    					{
		    				    						NodeRestrictionCardinality nrc;  
	    				    						    NodeRestrictionCardinality nrc2; 
	    				    						    String tempstr = (String)(jComboBoxOperation.getSelectedItem());
	    				    			    			if(str.equals("Intersection"))
	    				    			    			{
	    				    			    				if(((NodeRestrictionCardinality)n).getCard() > ((NodeRestrictionCardinality)n2).getCard())
		    				    						    {
		    				    						    	nrc = (NodeRestrictionCardinality)n;
		    				    						    	nrc2 = (NodeRestrictionCardinality)n2;
		    				    						    }
		    				    						    else
		    				    						    {
		    				    						    	nrc = (NodeRestrictionCardinality)n2;
		    				    						    	nrc2 = (NodeRestrictionCardinality)n;
		    				    						    }
	    				    			    			}
	    				    			    			else
	    				    			    			{
		    				    						    if(((NodeRestrictionCardinality)n).getCard() < ((NodeRestrictionCardinality)n2).getCard())
		    				    						    {
		    				    						    	nrc = (NodeRestrictionCardinality)n;
		    				    						    	nrc2 = (NodeRestrictionCardinality)n2;
		    				    						    }
		    				    						    else
		    				    						    {
		    				    						    	nrc = (NodeRestrictionCardinality)n2;
		    				    						    	nrc2 = (NodeRestrictionCardinality)n;
		    				    						    }
	    				    			    			}
	    				    						    
		    				    						int idfinal = nrc.getId();
		    				    						int idreplace = nrc2.getId();
	    				    						    LinkedList<Integer> tempC1 = adj.get(idfinal);
	    				    						    LinkedList<Integer> tempC2 = adj.get(idreplace);
	    				    						    boolean replace = true;
	    				    						    
	    				    						    if(tempC2.size() >= 2)
	    				    				        	{
	    				    				        		for(int i = 1; i < tempC2.size(); i++)
	    				    				        		{
	    				    				        			int element = tempC2.get(i);
	    				    				        			if(!tempC1.contains(element))
	    				    				        			{
	    				    				        				replace = false;
	    				    				        			}
	    				    				        		}
	    				    				        	}
	    				    						    if(replace)
	    				    						    {
		    				    						    for(LinkedList<Integer> tempAdj : adj)
		    					    				        {
		    				    						    	if(tempAdj.contains(idreplace))
		    				    						    	{
		    				    						    		int index = tempAdj.indexOf(idreplace);
		    				    						    		if(index > 0)
		    				    						    		{
		    				    						    			if(!tempAdj.contains(idfinal))
		    				    						    			{
		    				    						    				replace = false;
		    				    						    			}
		    				    						    		}
		    				    						    	}
		    					    				        }
	    				    						    }
	    				    						    if(replace)
	    				    						    {
	    				    						    	nrc2.setRemove(true);
	    				    						    	while(tempC2.size() >= 2)
    				    						    		{
    				    						    			tempC2.removeLast();
    				    						    		}
    				    						    		for(LinkedList<Integer> tempAdj : adj)
		    					    				        {
		    				    						    	if(tempAdj.contains(idreplace))
		    				    						    	{
		    				    						    		int index = tempAdj.indexOf(idreplace);
		    				    						    		if(index > 0)
		    				    						    		{
		    				    						    			tempAdj.remove(index);
		    				    						    		}
		    				    						    	}
		    					    				        }
	    				    						    }
		    				    					}
		    				    					else
		    				    					{
		    				    						NodeRestrictionComplementOfRestrictionCardinality nrc;
		    				    						NodeRestrictionComplementOfRestrictionCardinality nrc2;
		    				    						String tempstr = (String)(jComboBoxOperation.getSelectedItem());
	    				    			    			if(str.equals("Intersection"))
	    				    			    			{
	    				    			    				if(((NodeRestrictionCardinality)n).getCard() < ((NodeRestrictionCardinality)n2).getCard())
		    				    						    {
		    				    						    	nrc = (NodeRestrictionComplementOfRestrictionCardinality)n;
		    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n2;
		    				    						    }
		    				    						    else
		    				    						    {
		    				    						    	nrc = (NodeRestrictionComplementOfRestrictionCardinality)n2;
		    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n;
		    				    						    }
	    				    			    			}
	    				    			    			else
	    				    			    			{
			    				    						if(((NodeRestrictionComplementOfRestrictionCardinality)n).getCard() > ((NodeRestrictionComplementOfRestrictionCardinality)n2).getCard())
			    				    						{
			    				    							nrc = (NodeRestrictionComplementOfRestrictionCardinality)n;
		    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n2;
		    				    						    }
		    				    						    else
		    				    						    {
		    				    						    	nrc = (NodeRestrictionComplementOfRestrictionCardinality)n2;
		    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n;
			    				    						}
	    				    			    			}
		    				    						
		    				    						int idfinal = nrc.getId();
		    				    						int idreplace = nrc2.getId();
	    				    						    LinkedList<Integer> tempC1 = adj.get(idfinal);
	    				    						    LinkedList<Integer> tempC2 = adj.get(idreplace);
	    				    						    boolean replace = true;
	    				    						    
	    				    						    if(tempC2.size() >= 2)
	    				    				        	{
	    				    				        		for(int i = 1; i < tempC2.size(); i++)
	    				    				        		{
	    				    				        			int element = tempC2.get(i);
	    				    				        			if(!tempC1.contains(element))
	    				    				        			{
	    				    				        				replace = false;
	    				    				        			}
	    				    				        		}
	    				    				        	}
	    				    						    
	    				    						    if(replace)
	    				    						    {
		    				    						    for(LinkedList<Integer> tempAdj : adj)
		    					    				        {
		    				    						    	if(tempAdj.contains(idreplace))
		    				    						    	{
		    				    						    		int index = tempAdj.indexOf(idreplace);
		    				    						    		if(index > 0)
		    				    						    		{
		    				    						    			if(!tempAdj.contains(idfinal))
		    				    						    			{
		    				    						    				replace = false;
		    				    						    			}
		    				    						    		}
		    				    						    	}
		    					    				        }
	    				    						    }
	    				    						    if(replace)
	    				    						    {
	    				    						    	nrc2.setRemove(true);
	    				    						    	while(tempC2.size() >= 2)
    				    						    		{
    				    						    			tempC2.removeLast();
    				    						    		}
		    				    						    for(LinkedList<Integer> tempAdj : adj)
		    					    				        {
		    				    						    	if(tempAdj.contains(idreplace))
		    				    						    	{
		    				    						    		int index = tempAdj.indexOf(idreplace);
		    				    						    		if(index > 0)
		    				    						    		{
		    				    						    			tempAdj.remove(index);
		    				    						    		}
		    				    						    	}
		    					    				        }
	    				    						    }
		    				    					}
		    				    				}
	    				    				}
	    				    			}
	    				    		}	    				            
	    				        }
	    						//Copy Adjacent List
	    				        LinkedList<Integer> tempChecked;
	    				        ArrayList<LinkedList<Integer>> adjChecked = new ArrayList<LinkedList<Integer>>();
	    				        for(LinkedList<Integer> tempC1 : adj)
	    				        {
	    				        	tempChecked = new LinkedList<Integer>();
	    				        	//Collections.copy(tempChecked,tempC1);
	    				        	
	    				        	for(int i = 0; i < tempC1.size(); i++)
	    				        	{
	    				        		int Element = tempC1.get(i);
	    				        		tempChecked.add(Element);
	    				        	}
	    				        	
	    				        	adjChecked.add(tempChecked);
	    				        }
	    				        
	    				        //finds MEG deleting unnecessary edges
	    				        for(LinkedList<Integer> tempC1 : adj)
	    				        {
	    				        	int OriginalElement = tempC1.get(0);
	    				        	if(tempC1.size() >= 2)
	    				        	{
	    				        		for(int i = 1; i < tempC1.size(); i++)
	    				        		{
	    				        			int Element = tempC1.get(i);
	    				        			LinkedList<Integer> tempC2 = adjChecked.get(Element);
	    				        			for(int j = 1; j < tempC2.size(); j++)
		    				        		{
	    				        				int TestReacheble = tempC2.get(j);
	    				        				int TestIndex = tempC1.indexOf(TestReacheble);
	    				        				if(TestIndex > 0)
	    				        				{
	    				        					tempC1.remove(TestIndex);
	    				        				}
	    				        				/*Cycle
	    				        				else if(TestIndex == 0)
	    				        				{
	    				        				}
	    				        				*/
		    				        		}
	    				        		}
	    				        	}
	    				        }
	    				        gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
	    				        
	    				        ConstraintGraph.Graph2Console(gResults);
	    				        
	    						if(!HideIRIResults)
	    		    			{
	    		    				//Cleaning table
	    		    				DefaultTableModel modelInc = new DefaultTableModel(
	    		    			               new Object [][] {

	    		    			               },
	    		    			               new String [] {
	    		    			                   "Resulting Ontology", ""
	    		    			               });				
	    		    				jTableInc.setModel(modelInc);
	    			    			//fill table with ontology
	    		    				modelInc = fillJTableWithIRI(gResults, modelInc);
	    		    			}
	    		    			else
	    		    			{
	    		    				//Cleaning table
	    		    				DefaultTableModel modelInc = new DefaultTableModel(
	    		    			               new Object [][] {

	    		    			               },
	    		    			               new String [] {
	    		    			                   "Resulting Ontology", ""
	    		    			               });				
	    		    				jTableInc.setModel(modelInc);
	    			    			//fill table with ontology
	    		    				modelInc = fillJTableWithoutIRI(gResults, modelInc);
	    		    			}
	    		    			enableButtons(true);
	    		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    		    			str = str + "\nMinimization Done!";
	    		    			Log.setText(str);
	    					}catch(Exception ex)
	    					{
	    						str = Log.getText() + "\n" + ex.getMessage();
	    		    			Log.setText(str);
	    		    			enableButtons(true);
	    		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    		    			return;
	    					}
	    				}
	    			};
	    			worker.start();
				}
				catch(Exception ex)
				{
					str = Log.getText() + "\nError : " + ex.getMessage();
	    			Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			}
	    }
	}
	
	/**
	 * Implements Show and Hide action for the Elements IRI in Ontology 2 
	 *
	 * @author Rômulo de Carvalho Magalhães
	 *
	 */
	class ShowHideIRIResults implements ActionListener {
		public void actionPerformed(ActionEvent e) 
	    {
			if(gResults != null)
			{
				root = SwingUtilities.getRoot((JButton) e.getSource());
				String str = Log.getText();
				try{
					enableButtons(false);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			if(HideIRIResults)
	    			{
	    				HideIRIResults = false;
	    				//Cleaning table
	    				DefaultTableModel modelInc = new DefaultTableModel(
	    			               new Object [][] {

	    			               },
	    			               new String [] {
	    			                   "Resulting Ontology", ""
	    			               });				
	    				jTableInc.setModel(modelInc);
		    			//fill table with ontology
	    				modelInc = fillJTableWithIRI(gResults, modelInc);
	    			}
	    			else
	    			{
	    				HideIRIResults = true;
	    				//Cleaning table
	    				DefaultTableModel modelInc = new DefaultTableModel(
	    			               new Object [][] {

	    			               },
	    			               new String [] {
	    			                   "Resulting Ontology", ""
	    			               });				
	    				jTableInc.setModel(modelInc);
		    			//fill table with ontology
	    				modelInc = fillJTableWithoutIRI(gResults, modelInc);
	    			}
	    			//str = str + "\n" + "message...";
	    			//Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				catch(Exception ex)
				{
					str = Log.getText() + "\nError : " + ex.getMessage();
	    			Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			}
	    }
	}
	
	/**
	 * Implements file browser to get Ontology1 and load it in gOnt1 object
	 * 
	 * @author Romulo de Carvalho Magalhaes
	 *
	 */
	class OpenO1 implements ActionListener {
	    public void actionPerformed(ActionEvent e) 
	    {
	    	root = SwingUtilities.getRoot((JButton) e.getSource());
	    	JFileChooser c = new JFileChooser();
	    	String str;
	    	//Show "Open" dialog:
	    	int rVal = c.showOpenDialog(OntologyManagerTab.this);
	    	if(rVal == JFileChooser.APPROVE_OPTION)
	    	{
	    		try{
	    			enableButtons(false);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			pathOnt1 = c.getSelectedFile().toString();
	    			nameOnt1 = c.getSelectedFile().getName().toString();
	    			str = Log.getText() + "\nLoading: " + pathOnt1;
	    			Log.setText(str);
	    			root.update(root.getGraphics());
	    			
	    			Thread worker = new Thread() {
	    				public void run() {
	    					String str;
	    					try{
	    						str = Log.getText();
		    					Normalization norm = new Normalization();
		    					Object[] o = norm.runOntologyNormalization(pathOnt1, nameOnt1, osType);
		    	    			String pathNorm = o[0].toString();
		    	    			//exception inside normalization...
				    			if(pathNorm.substring(0, 5).equals("Error"))
				    			{
				    				str = str + "\n" + pathNorm;
				    				Log.setText(str);
				    				enableButtons(true);
		    		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				    				return;
				    			}
				    			IRIOnt1 = o[1].toString();
				    			System.out.print("\n Original IRI Ontology 1: " + IRIOnt1 + "\n");
				    			Ont1 = loadOnt(pathNorm);
				    			ConstraintGraph r = new ConstraintGraph();
				    			gOnt1 = new Graph();
				    			gOnt1 = r.createGraph(Ont1, Log);
				    			//Ontology loaded into memory, cleaning table 1
				    			DefaultTableModel model1 = new DefaultTableModel(
				    					new Object [][] {

				    					},
				    					new String [] {
				    							nameOnt1, ""
				    					});
				    			
				    			jTable1.setModel(model1);
				    			//fill table with ontology
				    			ShowBottomWarning = true;
				    			CleanUp = false;
				    			model1 = fillJTableWithIRI(gOnt1, model1);
				    			str = Log.getText();
				    			str = str + "\n" + "Ontology successfully loaded as Ontology 1";
				    			Log.setText(str);
				    			HideIRI1 = false;
				    			//loading possible Projection on jTable2
				    			str = (String)(jComboBoxOperation.getSelectedItem());
				    			if(str.equals("Projection"))
				    			{
				    				ProjectionTableModel model2 = new ProjectionTableModel();
				    				jTable2.setModel(model2);
				    				fillProjectionList(gOnt1, model2);	    				
				    			}
				    			enableButtons(true);
	    		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    					}catch(Exception ex)
	    					{
	    						str = Log.getText() + "\nError : " + ex.getMessage();
	    		    			Log.setText(str);
	    		    			enableButtons(true);
	    		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    		    			return;
	    					}
		    			}
	    			};
	    			worker.start();
	    		}
	    		catch(Exception ex)
	    		{
	    			str = Log.getText() + "\n" + ex.getMessage();
	    			Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
	    		}
    		}
	    	/*
	    	if(rVal == JFileChooser.CANCEL_OPTION) 
	    	{
	    		pathOnt1 = "";
	    		nameOnt1 = "";
	    		str = Log.getText() + "\n" + "You pressed cancel, no new Ontology was loaded";
	    		Log.setText(str);
	    	}
	    	*/
    	}
	}
	
	/**
	 * Implements file browser to get Ontology2 and load it in gOnt2 object
	 * 
	 * @author Romulo de Carvalho Magalhaes
	 *
	 */
	class OpenO2 implements ActionListener {
	    
		public void actionPerformed(ActionEvent e) 
	    {
	    	root = SwingUtilities.getRoot((JButton) e.getSource());
			JFileChooser c = new JFileChooser();
	    	String str;
	    	//Show "Open" dialog:
	    	int rVal = c.showOpenDialog(OntologyManagerTab.this);
	    	if(rVal == JFileChooser.APPROVE_OPTION)
	    	{
	    		try{
	    			enableButtons(false);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			pathOnt2 = c.getSelectedFile().toString();//c.getSelectedFile().getName();
	    			nameOnt2 = c.getSelectedFile().getName().toString();
	    			str = Log.getText() + "\nLoading: " + pathOnt2;
	    			Log.setText(str);
	    			root.update(root.getGraphics());

	    			Thread worker = new Thread() {
	    				public void run() {
	    					String str;
	    					try{
	    						str = Log.getText();
		    					Normalization norm = new Normalization();
		    					Object[]o =norm.runOntologyNormalization(pathOnt2, nameOnt2, osType);
		    	    			String pathNorm = o[0].toString();
		    	    			//exception inside normalization...
				    			if(pathNorm.substring(0, 5).equals("Error"))
				    			{
				    				str = str + "\n" + pathNorm;
				    				Log.setText(str);
				    				enableButtons(true);
		    		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				    				return;
				    			}
				    			IRIOnt2 = o[1].toString();
				    			System.out.print("\n Original IRI Ontology 2: " + IRIOnt2 + "\n");
				    			Ont2 = loadOnt(pathNorm);
				    			ConstraintGraph r = new ConstraintGraph();
				    			gOnt2 = new Graph();
				    			gOnt2 = r.createGraph(Ont2, Log);
				    			//Ontology loaded into memory, cleaning table 1
				    			DefaultTableModel model2 = new DefaultTableModel(
				    					new Object [][] {

				    					},
				    					new String [] {
				    							nameOnt2, ""
				    					});
				    			
				    			jTable2.setModel(model2);
				    			//fill table with ontology
				    			ShowBottomWarning = true;
				    			CleanUp = false;
				    			model2 = fillJTableWithIRI(gOnt2, model2);
				    			HideIRI2 = false;
				    			str = Log.getText();
				    			str = str + "\n" + "Ontology successfully loaded as Ontology 2";
				    			Log.setText(str);
				    			enableButtons(true);
	    		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    					}catch(Exception ex)
	    					{
	    						str = Log.getText() + "\n" + ex.getMessage();
	    		    			Log.setText(str);
	    		    			enableButtons(true);
	    		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    		    			return;
	    					}
		    			}
	    			};
	    			worker.start();
	    		}
	    		catch(Exception ex)
	    		{
	    			str = Log.getText() + "\n" + ex.getMessage();
	    			Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
	    		}
    		}
	    	/*
	    	if(rVal == JFileChooser.CANCEL_OPTION) 
	    	{
	    		pathOnt2 = "";
	    		nameOnt2 = "";
	    		str = Log.getText() + "\n" + "You pressed cancel, no new Ontology was loaded";
	    		Log.setText(str);
	    	}
	    	*/
	    }
	}

	/**
	 * Implements file chooser to save resulting Ontology
	 *
	 * @author Rômulo de Carvalho Magalhães
	 *
	 */
	class SaveR implements ActionListener{
	    public void actionPerformed(ActionEvent e)
	    {
	    	root = SwingUtilities.getRoot((JButton) e.getSource());
	    	JFileChooser c = new JFileChooser();
	    	String str = "";
	    	
	    	// Demonstrate "Save" dialog:
	    	int rVal = c.showSaveDialog(OntologyManagerTab.this);
	    	if(rVal == JFileChooser.APPROVE_OPTION)
	    	{	
	    		try{
	    			pathOntResults = c.getSelectedFile().getAbsolutePath().toString();
	    			nameOntResults = c.getSelectedFile().getName().toString();
	    			int num = pathOntResults.lastIndexOf(".");
	    			
	    			if(num == -1)
	    			{
	    				pathOntResults = pathOntResults + "Normalized.owl";
	    				nameOntResults = nameOntResults + "Normalized.owl";
	    			}
	    			else if (!pathOntResults.endsWith("Normalized.owl"))
	    			{
	    				String s = pathOntResults.substring(0, pathOntResults.lastIndexOf("."));
	    				pathOntResults = pathOntResults + "Normalized.owl";
	    				nameOntResults = nameOntResults + "Normalized.owl";
	    			}
	    			
	    			/*
	    			if(num == -1)
	    			{
	    				pathOntResults = pathOntResults + ".owl";
	    				nameOntResults = nameOntResults + ".owl";
	    			}
	    			*/
	    			enableButtons(false);
	    			btnSave.setEnabled(false);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			str = Log.getText() + "\nSaving Ontology as: " + pathOntResults;
	    			Log.setText(str);
	    			root.update(root.getGraphics());

	    			Thread worker = new Thread() {
	    				public void run() {
	    					String str;
	    					try{
	    						//Save Ontology
	    						SaveOntology.SaveOntologyToFile(pathOntResults, nameOntResults, gResults, Log);
				    			
				    			enableButtons(true);
	    		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    					}catch(Exception ex)
	    					{
	    						str = Log.getText() + "\n" + ex.getMessage();
	    		    			Log.setText(str);
	    		    			enableButtons(true);
	    		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    		    			return;
	    					}
		    			}
	    			};
	    			worker.start();
	    		}
	    		catch(Exception ex)
	    		{
	    			str = Log.getText() + "\n" + ex.getMessage();
	    			Log.setText(str);
	    			enableButtons(true);
	    			btnSave.setEnabled(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
	    		}
	    	}
	    	if(rVal == JFileChooser.CANCEL_OPTION)
	    	{
	    		str = Log.getText() + "\n" + "You pressed cancel, Ontology was not saved";
	    		Log.setText(str);
	    	}
	    }
	}
	
	/**
	 * Using the OWL API initializes the manager, ontology and datafactory from the file and returns the object containing it
	 * 
	 * @param ontPath
	 * @return Object containing manager, ontology, datafactory and original OWL file path 
	 */
	public Object[] loadOnt(String ontPath) 
	{
		String str = Log.getText();
		Object[] obj = new Object[4];
		try {	                
			warninglbl.setVisible(false);
			OWLOntologyManager manager = (OWLOntologyManager) OWLManager.createOWLOntologyManager();
            IRI physicalURI = IRI.create( "file:" + ontPath);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(physicalURI);	                
			OWLDataFactory factory = manager.getOWLDataFactory();
         	obj[0] = ontology;
			obj[1] = factory;
			obj[2] = manager;
            obj[3] = ontPath;
		    return obj;
		} 
		catch (OWLOntologyCreationException e) 
		{
				str = str +"\n" + "Error Loading Ontology : \n" + e.getMessage();
				Log.setText(str);
				warninglbl.setText("Error Loading : "+ ontPath);
				warninglbl.setVisible(true);
		}
	    return null;
    }

	
	/**
	 * Checks if the Ontologies are loaded correctly and call the operations runners
	 *
	 * @author Romulo de Carvalho Magalhaes
	 *
	 */
	class RunOnt implements ActionListener {
	    public void actionPerformed(ActionEvent e) 
	    {
	    	root = SwingUtilities.getRoot((JButton) e.getSource());
	    	String str;
	    	try{
	    		if(gOnt1 != null)
	    		{
	    			str = (String)(jComboBoxOperation.getSelectedItem());
	    			if(str.equals("Projection"))
	    			{
	    				runProjection();	    				
	    			}else
	    			{
	    				if(gOnt2 == null)
	    				{
	    					str = Log.getText() + "\nPlease load Second Antology";
	    	    			Log.setText(str);
	    	    			return;
	    				}
	    				if(str.equals("Union"))
	    				{
	    					runUnion();
	    				}else if(str.equals("Intersection"))
	    				{
	    					runIntersection();
	    				}else if(str.equals("Difference"))
	    				{
	    					runDifference();
	    				}
	    			}
	    			
	    		}else
	    		{
	    			str = Log.getText() + "\nPlease load an Antology first";
	    			Log.setText(str);
	    			return;
	    		}
    			
	    	}catch(Exception ex)
	    	{
	    		str = Log.getText() + "\n" + ex.getMessage();
    			Log.setText(str);
    			enableButtons(true);
    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    	}
	    }
	}

	/**
	 * Runs difference in another thread
	 * 
	 */
	public void runDifference()
	{
		enableButtons(false);
		root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String str = Log.getText() + "\nRunning Difference over " + pathOnt1 + " and " + pathOnt2;
		Log.setText(str);
		root.update(root.getGraphics());

		Thread worker = new Thread() {
			public void run() {
				String str;
				try{
					//Time Stamp
					String path = System.getProperty("user.home") + "/LastRunProcedureTime.txt";
			    	File logFile = new File(path);
			    	BufferedWriter writer = null;
			    	java.util.Date date;
			    	date = new java.util.Date();
					String stamp;
					stamp = "Running Difference: \n";
					stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
					System.out.println(stamp);
					writer = new BufferedWriter(new FileWriter(logFile));
					
					str = Log.getText();
					/**
					 * 1st I will get all the reachable from the 1st and the 2nd Ontologies
					 * 
					 * then I will drop from the 1st every adj that is equal in both ontologies(also all Cardinality 
					 * restriction nodes that are equal)
					 * 
					 * by the end, if a node is without adj I will drop it too
					 */
					BreadthFirstSearch bfs1 = new BreadthFirstSearch(gOnt1);
					BreadthFirstSearch bfs2 = new BreadthFirstSearch(gOnt2);
					bfs1.fullBFS();
					bfs2.fullBFS();
					ArrayList<LinkedList<Integer>> TransitiveClosure1 = bfs1.getReachable();
					ArrayList<LinkedList<Integer>> TransitiveClosure2 = bfs2.getReachable();
					HashMap<Integer,Node> vertices1 = gOnt1.getVertices();
					HashMap<Integer,Node> vertices2 = gOnt2.getVertices();
					
					DefaultTableModel modelInc = clearResultsTable();
					gResults = new Graph();
					//Equivalent nodes from gOnt1 and gOnt2
					ArrayList<Integer> nodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt2 = new ArrayList<Integer>();
					//Equivalent nodes from gOnt1 and gOnt3
					ArrayList<Integer> finalNodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> finalNodesOnt3 = new ArrayList<Integer>();
					
					for(Map.Entry<Integer, Node> temp : vertices1.entrySet())
					{
			        	Node n1 = temp.getValue();
			        	int nodeKey1 = n1.getId();
			        	NodeType type = n1.getNodeType();
			        	int nodeKey2 = Graph.findNodeKeyDifference(IRIOnt1, n1, IRIOnt2, gOnt2);
			        	if(nodeKey2 == -1)
			        	{
			        		System.out.print("Não encontrados: \n");
	        				System.out.print("* "+ nodeKey1+"\n");
		        			int tempID = -1;
		        			switch(type)
			            	{
							   case Class :
							       NodeClass nc = (NodeClass)n1;
							       tempID = gResults.insertNode(nc.getExpression(), NodeType.Class);							      
							       break;
							   case Property :
							       NodeProperty np = (NodeProperty)n1;
							       tempID = gResults.insertNodeProperty(np.getDescIRI(),np.getDescProp());
							       break;
							   case RestrictionCardinality :
								   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
							       tempID = gResults.insertNodeRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
							       break;
							   case RestrictionComplementOfClass :
								   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
							       tempID = gResults.insertNodeRestrictionComplementOfClass(notClass.getExpClass().toString());
							       break;
							   case RestrictionComplementOfProperty :
								   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
							       tempID = gResults.insertNodeRestrictionComplementOfProperty(notProp.getDescProp(), notProp.getDescIRI());
							       break;
							   case RestrictionComplementOfRestrictionCardinality :
								   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
							       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinality(notRestCard.getExpression());
							       break;
							   default: 
								   break;
						    }
		        			if(tempID != -1)
		        			{
		        				finalNodesOnt1.add(nodeKey1);
		        				finalNodesOnt3.add(tempID);
		        			}
		        			else
		        			{
				        		System.out.print("Erro ao incerir: \n");
		        				System.out.print("* "+ nodeKey1+"\n");
		        			}
			        	}
			        	else
			        	{
			        		nodesOnt1.add(nodeKey1);
			        		nodesOnt2.add(nodeKey2);
			        	}
			        }
			        ArrayList<LinkedList<Integer>> CheckedTransitiveClosure = new ArrayList<LinkedList<Integer>>();
			        LinkedList<Integer> tempChecked;
			        for(LinkedList<Integer> tempC1 : TransitiveClosure1)
			        {
			        	tempChecked = new LinkedList<Integer>();
			        	int element = tempC1.get(0);
			        	if(nodesOnt1.contains(element))
			        	{
			        		tempChecked.add(element);
			        		int index = nodesOnt1.indexOf(element);
			        		int element2 = nodesOnt2.get(index);
			        		LinkedList<Integer> tempC2 = UsefulOWL.getListwithFirstElement(TransitiveClosure2, element2);
			        		for(int i : tempC1)
			        		{
			        			if(!nodesOnt1.contains(i))
			        			{
			        				tempChecked.add(i);
			        			}
			        			else
			        			{
			        				index = nodesOnt1.indexOf(i);
					        		element2 = nodesOnt2.get(index);
					        		if(!tempC2.contains(element2))
					        		{
					        			tempChecked.add(i);
					        		}
			        			}
			        		}
			        		if((tempChecked != null)&&(!tempChecked.isEmpty())&&(tempChecked.size()>1))
				        	{
				        		CheckedTransitiveClosure.add(tempChecked);
				        	}
			        	}
			        	else
			        	{
			        		for(int i : tempC1)
			        		{
			        			tempChecked.add(i);
			        		}
			        		if((tempChecked != null)&&(!tempChecked.isEmpty()))
				        	{
				        		CheckedTransitiveClosure.add(tempChecked);
				        	}
			        	}			        	
			        }
			        
			        //Now I have the closure with all the edges that must be added to the result
			        for(LinkedList<Integer> tempC1 : CheckedTransitiveClosure)
			        {
			        	int element = tempC1.get(0);
			        	if(!finalNodesOnt1.contains(element))
			        	{
			        		//1st I insert the node in gResults if it isn't already there
			        		Node n1 = vertices1.get(element);
			        		int nodeKeyR = Graph.findNodeKey(IRIOnt1, n1, "", gResults);
			        		if(nodeKeyR == -1)
				        	{
			        			NodeType type = n1.getNodeType();			        			
			        			int tempID = -1;
			        			switch(type)
				            	{
								   case Class :
								       NodeClass nc = (NodeClass)n1;
								       tempID = gResults.insertNode(nc.getExpression(), NodeType.Class);							      
								       break;
								   case Property :
								       NodeProperty np = (NodeProperty)n1;
								       tempID = gResults.insertNodeProperty(np.getDescIRI(),np.getDescProp());
								       break;
								   case RestrictionCardinality :
									   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
								       tempID = gResults.insertNodeRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
								       break;
								   case RestrictionComplementOfClass :
									   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfClass(notClass.getExpClass().toString());
								       break;
								   case RestrictionComplementOfProperty :
									   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfProperty(notProp.getDescProp(), notProp.getDescIRI());
								       break;
								   case RestrictionComplementOfRestrictionCardinality :
									   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinality(notRestCard.getExpression());
								       break;
								   default: 
									   break;
							    }
			        			if(tempID != -1)
			        			{
			        				finalNodesOnt1.add(n1.getId());
			        				finalNodesOnt3.add(tempID);
			        			}
				        	}
			        	}
			        	//If I already inserted the node in the resulting graph
			        	if(finalNodesOnt1.contains(element))
			        	{
			        		int index = finalNodesOnt1.indexOf(element);
			        		int idResult = finalNodesOnt3.get(index);
			        		ArrayList<LinkedList<Integer>> ResultAdj = gResults.getAdj();
			        		LinkedList<Integer> tempResultAdj = UsefulOWL.getListwithFirstElement(ResultAdj, idResult);
			        		//I get its adj list and add all nodes from CheckedTransitiveClosure
			        		for(int i = 1; i<tempC1.size(); i++)
			        		{
			        			element = tempC1.get(i);
			        			if(finalNodesOnt1.contains(element))
			        			{
			        				//if the node already exists in gResults I add only the edge
			        				index = finalNodesOnt1.indexOf(element);
			        				idResult = finalNodesOnt3.get(index);
			        				if(!tempResultAdj.contains(idResult))
			        				{
			        					tempResultAdj.add(idResult);
			        				}
			        			}
			        			else
			        			{
			        				//I must add the node and then add the edge
			        				Node n1 = vertices1.get(element).getNode();
			        				NodeType type = n1.getNodeType();
			        				int tempID = -1;
				        			switch(type)
					            	{
									   case Class :
									       NodeClass nc = (NodeClass)n1;
									       tempID = gResults.insertNode(nc.getExpression(), NodeType.Class);							      
									       break;
									   case Property :
									       NodeProperty np = (NodeProperty)n1;
									       tempID = gResults.insertNodeProperty(np.getDescIRI(),np.getDescProp());
									       break;
									   case RestrictionCardinality :
										   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
									       tempID = gResults.insertNodeRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
									       break;
									   case RestrictionComplementOfClass :
										   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
									       tempID = gResults.insertNodeRestrictionComplementOfClass(notClass.getExpClass().toString());
									       break;
									   case RestrictionComplementOfProperty :
										   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
									       tempID = gResults.insertNodeRestrictionComplementOfProperty(notProp.getDescProp(), notProp.getDescIRI());
									       break;
									   case RestrictionComplementOfRestrictionCardinality :
										   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
									       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinality(notRestCard.getExpression());
									       break;
									   default: 
										   break;
								    }
				        			if(tempID != -1)
				        			{
				        				finalNodesOnt1.add(n1.getId());
				        				finalNodesOnt3.add(tempID);
				        				tempResultAdj.add(tempID);
				        			}
			        			}
			        		}
			        	}
			        }
					
			        ConstraintGraph rgd = new ConstraintGraph();
					gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
			        gResults = rgd.searchBottomNodes(gResults);
			        System.out.print("\n => End of Difference: \n");
			        ConstraintGraph.Graph2Console(gResults);
			        ShowBottomWarning = true;
					fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					str = Log.getText() + "\n" + "Difference Done!";
	    			Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			
	    			//Time stamping
	    			date= new java.util.Date();
	    			stamp = stamp + "Difference done: \n";
	    			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
	    			System.out.println(stamp);
	    			writer.write(stamp);
	    	        writer.close();
				}catch(Exception ex)
				{
					str = Log.getText() + "\n" + ex.getMessage();
	    			Log.setText(str);
	    			gResults = null;
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			}
		};
		worker.start();	
	}

	
	
	/**
	 * Runs Intersection in another thread
	 * 
	 */
	public void runIntersection()
	{
		enableButtons(false);
		root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String str = Log.getText() + "\nRunning Intersection over " + pathOnt1 + " and " + pathOnt2;
		Log.setText(str);
		root.update(root.getGraphics());

		Thread worker = new Thread() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void run() {
				String str;
				try{
					//Time Stamp
					String path = System.getProperty("user.home") + "/LastRunProcedureTime.txt";
			    	File logFile = new File(path);
			    	BufferedWriter writer = null;
			    	java.util.Date date;
			    	date = new java.util.Date();
					String stamp;
					stamp = "Running Intersection: \n";
					stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
					System.out.println(stamp);
					writer = new BufferedWriter(new FileWriter(logFile));
					
					str = Log.getText();
					//run intersection
					//1st we pick the vertices that are in both Ontologies
					HashMap<Integer,Node> vertices1 = gOnt1.getVertices();
					HashMap<Integer,Node> vertices2 = gOnt2.getVertices();
					//Clear Results Table
					DefaultTableModel modelInc = clearResultsTable();
					gResults = new Graph();
					ArrayList<Integer> nodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt2 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt3 = new ArrayList<Integer>();
					Set cont = vertices1.entrySet();
		            Iterator it = (Iterator) cont.iterator();
		            int tempCard1;
		            int tempCard2;
			        while(it.hasNext())
			        {
			        	Map.Entry<Integer, Node> temp = (Map.Entry<Integer, Node>)it.next();
			        	Node n1 = temp.getValue();
			        	//OWLObject o1 = n1.getExpression();
			        	int nodeKey2 = Graph.findNodeKey(IRIOnt1, n1, IRIOnt2, gOnt2); //gOnt2.getNodeKey(o1);
			        	if(nodeKey2 != -1)
			        	{
			        		Node n2 = vertices2.get(nodeKey2);
			        		String s = "\n n1 : " + n1.getExpression().toString();
			        		s= s + "  -  n2 : "+n2.getExpression().toString();
			        		//System.out.print(s);
			        		if(n2.getNodeType().toString().equals(n1.getNodeType().toString()))
			        		{
			        			nodesOnt1.add(n1.getId());
			        			nodesOnt2.add(nodeKey2);
			        			NodeType type = n2.getNodeType();
			        			int tempID = -1;
			        			switch(type)
				            	{
								   case Class :
								       NodeClass nc = (NodeClass)n1;
								       tempID = gResults.insertNodeComp(nc.getExpression(), NodeType.Class);							      
								       break;
								   case Property :
								       NodeProperty np = (NodeProperty)n1;
								       tempID = gResults.insertNodePropertyComp(np.getDescIRI(),np.getDescProp());
								       break;
								   case RestrictionCardinality :
									   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
									   NodeRestrictionCardinality nrc2 = (NodeRestrictionCardinality)n2;
									   tempCard1 = nrc.getCard();
									   tempCard2 = nrc2.getCard();
									   if(tempCard1 < tempCard2)
									   {
										   tempCard1 = tempCard2;
									   }
								       tempID = gResults.insertNodeRestrictionCardinalityComp(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),tempCard1);								       
								       break;
								   case RestrictionComplementOfClass :
									   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfClassComp(notClass.getExpClass().toString());
								       break;
								   case RestrictionComplementOfProperty :
									   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfPropertyComp(notProp.getDescProp(), notProp.getDescIRI());
								       break;
								   case RestrictionComplementOfRestrictionCardinality :
									   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
									   NodeRestrictionComplementOfRestrictionCardinality notRestCard2 = (NodeRestrictionComplementOfRestrictionCardinality)n2;
									   tempCard1 = UsefulOWL.returnCard(notRestCard.getExpression());
									   tempCard2 = UsefulOWL.returnCard(notRestCard2.getExpression());
									   if(tempCard1 > tempCard2)
									   {
										   tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard2.getExpression());
									   }
									   else
									   {
										   tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard.getExpression());
									   }
								       break;
								   default: 
									   break;
							    }
			        			nodesOnt3.add(tempID);
			        		}
			        	}
			        }
			        System.out.print("\n => Nodes included: \n");
			        ConstraintGraph.Graph2Console(gResults);
					//now we get a new Transitive Closure for the final Graph with only the nodes that will be included
			        //Graph 1
			        BreadthFirstSearch bfs1 = new BreadthFirstSearch(gOnt1);
					ArrayList<LinkedList<Integer>> originalTransitiveClosure1 = bfs1.getReachable();
					//ArrayList<LinkedList<Integer>> originalTransitiveClosure1 = gOnt1.getAdj();
					ArrayList<LinkedList<Integer>> newTransitiveClosure1 = new ArrayList<LinkedList<Integer>>();
					//Then we create a new closure without the non-checked nodes and the ones that references them
					for(LinkedList<Integer> tempRO : originalTransitiveClosure1)
					{
						if(nodesOnt1.contains(tempRO.get(0)))
						{
							LinkedList<Integer> tempR = new LinkedList<Integer>();
							for(Integer i : tempRO)
							{
								if(nodesOnt1.contains(i))
								{
									tempR.add(i);
								}
							}
							newTransitiveClosure1.add(tempR);
						}
					}
					//Graph 2
					BreadthFirstSearch bfs2 = new BreadthFirstSearch(gOnt2);
					ArrayList<LinkedList<Integer>> originalTransitiveClosure2 = bfs2.getReachable();
					//ArrayList<LinkedList<Integer>> originalTransitiveClosure2 = gOnt2.getAdj();
					ArrayList<LinkedList<Integer>> newTransitiveClosure2 = new ArrayList<LinkedList<Integer>>();
					for(LinkedList<Integer> tempRO : originalTransitiveClosure2)
					{
						if(nodesOnt2.contains(tempRO.get(0)))
						{
							LinkedList<Integer> tempR = new LinkedList<Integer>();
							for(Integer i : tempRO)
							{
								if(nodesOnt2.contains(i))
								{
									tempR.add(i);
								}
							}
							newTransitiveClosure2.add(tempR);
						}
					}
					//and create the transitive closure of the Resulting graph using it
					//Graph3
					ArrayList<LinkedList<Integer>> newTransitiveClosure3 = new ArrayList<LinkedList<Integer>>();
					for(int i = 0; i < nodesOnt1.size(); i++)
					{
						int element1 = nodesOnt1.get(i);
						LinkedList<Integer> tempRO1 = UsefulOWL.getListwithFirstElement(newTransitiveClosure1, element1);
						int element2 = nodesOnt2.get(i);
						LinkedList<Integer> tempRO2 = UsefulOWL.getListwithFirstElement(newTransitiveClosure2, element2);
						LinkedList<Integer> tempRO3 = new LinkedList<Integer>();
						tempRO3.add(nodesOnt3.get(i));
						
						if((tempRO2 != null) &&(tempRO1 != null))
						{
							for(int j = 1; j < tempRO1.size(); j++)
							{
								element1 = tempRO1.get(j);
								int index = nodesOnt1.indexOf(element1);
								if(index > -1)
								{
									element2 = nodesOnt2.get(index);
									if(tempRO2.contains(element2))
									{
										tempRO3.add(nodesOnt3.get(index));
									}
								}
							}
						}
						newTransitiveClosure3.add(tempRO3);
					}
					gResults.setAdj(newTransitiveClosure3);
					ConstraintGraph rgd = new ConstraintGraph();
					gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
			        gResults = rgd.searchBottomNodes(gResults);
					System.out.print("\n => End of intersection: \n");
			        ConstraintGraph.Graph2Console(gResults);
			        ShowBottomWarning = true;
					fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					str = Log.getText() + "\n" + "Intersection done!";
	    			Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			
	    			//Time stamping
	    			date= new java.util.Date();
	    			stamp = stamp + "Intersection done: \n";
	    			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
	    			System.out.println(stamp);
	    			writer.write(stamp);
	    	        writer.close();
				}catch(Exception ex)
				{
					str = Log.getText() + "\n" + ex.getMessage();
	    			Log.setText(str);
	    			gResults = null;
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			}
		};
		worker.start();		
	}

	/**
	 * Runs union in another thread
	 * 
	 */
	public void runUnion()
	{
		enableButtons(false);
		root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String str = Log.getText() + "\nRunning Union over " + pathOnt1 + " and " + pathOnt2;
		Log.setText(str);
		root.update(root.getGraphics());

		Thread worker = new Thread() {
			public void run() {
				String str;
				try{
					//Time Stamp
					String path = System.getProperty("user.home") + "/LastRunProcedureTime.txt";
			    	File logFile = new File(path);
			    	BufferedWriter writer = null;
			    	java.util.Date date;
			    	date = new java.util.Date();
					String stamp;
					stamp = "Running Union: \n";
					stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
					System.out.println(stamp);
					writer = new BufferedWriter(new FileWriter(logFile));
					
					str = Log.getText();
					//1st we pick the vertices that are in both Ontologies
					HashMap<Integer,Node> vertices1 = gOnt1.getVertices();
					HashMap<Integer,Node> vertices2 = gOnt2.getVertices();
					//Clear Results Table
					DefaultTableModel modelInc = clearResultsTable();
					gResults = new Graph();
					ArrayList<Integer> nodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt2 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt3 = new ArrayList<Integer>();
					//1st we add all the nodes from ontology 1
					for(Map.Entry<Integer, Node> temp : vertices1.entrySet())
					{
			        	Node n1 = temp.getValue();
			        	NodeType type = n1.getNodeType();
	        			int tempID = -1;
	        			switch(type)
		            	{
						   case Class :
						       NodeClass nc = (NodeClass)n1;
						       tempID = gResults.insertNodeComp(nc.getExpression(), NodeType.Class);							      
						       break;
						   case Property :
						       NodeProperty np = (NodeProperty)n1;
						       tempID = gResults.insertNodePropertyComp(np.getDescIRI(),np.getDescProp());
						       break;
						   case RestrictionCardinality :
							   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
						       tempID = gResults.insertNodeRestrictionCardinalityComp(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
						       break;
						   case RestrictionComplementOfClass :
							   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
						       tempID = gResults.insertNodeRestrictionComplementOfClassComp(notClass.getExpClass().toString());
						       break;
						   case RestrictionComplementOfProperty :
							   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
						       tempID = gResults.insertNodeRestrictionComplementOfPropertyComp(notProp.getDescProp(), notProp.getDescIRI());
						       break;
						   case RestrictionComplementOfRestrictionCardinality :
							   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
						       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard.getExpression());
						       break;
						   default: 
							   break;
					    }
	        			if(tempID != -1)
	        			{
	        				nodesOnt1.add(n1.getId());
	        				nodesOnt3.add(tempID);
	        			}	        			
			        }
			        ConstraintGraph.Graph2Console(gResults);
			        //now we set the adjacency list as an equivalent to the ontology 1
			        ArrayList<LinkedList<Integer>> AdjList1 = gOnt1.getAdj();
			        ArrayList<LinkedList<Integer>> AdjList3 = new ArrayList<LinkedList<Integer>>();
					for(int i = 0; i < nodesOnt1.size(); i++)
					{
						int element1 = nodesOnt1.get(i);
						LinkedList<Integer> tempRO1 = UsefulOWL.getListwithFirstElement(AdjList1, element1);
						LinkedList<Integer> tempRO3 = new LinkedList<Integer>();
						tempRO3.add(nodesOnt3.get(i));
						if(tempRO1 != null)
						{
							for(int j = 1; j < tempRO1.size(); j++)
							{
								element1 = tempRO1.get(j);
								int index = nodesOnt1.indexOf(element1);
								if(index > -1)
								{
									int element3 = nodesOnt3.get(index);
									if(!tempRO3.contains(element3))
									{
										tempRO3.add(element3);
									}
								}
							}
						}
						AdjList3.add(tempRO3);
					}
					
					gResults.setAdj(AdjList3);
					//we now have a Graph that is a copy of Ontology 1 
					//we need to add all nodes from Ontology 2 and its Edges
			        System.out.print("\n => Original Graph: \n");
			        ConstraintGraph.Graph2Console(gOnt1);
			        System.out.print("\n => Nodes included: \n");
			        ConstraintGraph.Graph2Console(gResults);
			        //now we try to insert all nodes from Ontology 2 marking its equivalents in the lists
			        nodesOnt3 = new ArrayList<Integer>();
			        for(Map.Entry<Integer, Node> temp : vertices2.entrySet())
					{
			        	Node n2 = temp.getValue();
			        	NodeType type = n2.getNodeType();
	        			int tempID = -1;
	        			switch(type)
		            	{
						   case Class :
						       NodeClass nc = (NodeClass)n2;
						       tempID = gResults.getNodeKey(nc.getExpression());
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeComp(nc.getExpression(), NodeType.Class);
						       }
						       break;
						   case Property :
						       NodeProperty np = (NodeProperty)n2;
						       tempID = gResults.getNodeKeyProperty(np.getDescIRI(),np.getDescProp());
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodePropertyComp(np.getDescIRI(),np.getDescProp());
						       }
						       break;
						   case RestrictionCardinality : 
							   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n2;
							   tempID = gResults.getNodeKeyRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());
							   if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionCardinalityComp(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());
						       }								       
						       break;
						   case RestrictionComplementOfClass :
							   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n2;
							   tempID = gResults.getNodeKeyComplementOfClass(notClass.getExpClass().toString());
							   if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionComplementOfClassComp(notClass.getExpClass().toString());
						       }
							   break;
						   case RestrictionComplementOfProperty :
							   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n2;
						       tempID = gResults.getNodeKeyComplementOfProperty(notProp.getDescIRI(),notProp.getDescProp());
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionComplementOfPropertyComp(notProp.getDescProp(), notProp.getDescIRI());
						       }
						       break;
						   case RestrictionComplementOfRestrictionCardinality :
							   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n2;
						       String desc = notRestCard.getExpression();
						       desc = desc.replaceAll("#", "/");
						       String iriDomain = UsefulOWL.returnIRIDomain(desc);
						       String iriProp = UsefulOWL.returnIRIProp(desc);
						       String descProp = UsefulOWL.returnProp(desc);
						       String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
						       int card = UsefulOWL.returnCard(desc);
						       tempID = gResults.getNodeKeyComplementOfRestrictionCardinality(iriDomain,iriProp,descProp,descRangeOrDomain,card);
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard.getExpression());
						       }						   
						       break;
						   default: 
							   break;
					    }
	        			nodesOnt2.add(n2.getId());	
					    nodesOnt3.add(tempID);	    	    
			        }
			        //And we add to the adjacency list of the resulting Ontology all edges from Ontology 2
			        ArrayList<LinkedList<Integer>> AdjList2 = gOnt2.getAdj();
			        AdjList3 = gResults.getAdj();
					for(int i = 0; i < nodesOnt2.size(); i++)
					{
						int element2 = nodesOnt2.get(i);
						int element3 = nodesOnt3.get(i);
						LinkedList<Integer> tempRO2 = UsefulOWL.getListwithFirstElement(AdjList2, element2);
						LinkedList<Integer> tempRO3 = UsefulOWL.getListwithFirstElement(AdjList3, element3);
						if((tempRO2 != null) && (tempRO3 != null))
						{
							for(int j = 1; j < tempRO2.size(); j++)
							{
								element2 = tempRO2.get(j);
								int index = nodesOnt2.indexOf(element2);
								if(index > -1)
								{
									element3 = nodesOnt3.get(index);
									if(!tempRO3.contains(element3))
									{
										tempRO3.add(element3);
									}
								}
							}
						}
					}
			        //now we search for bottom and top nodes
					ConstraintGraph rgd = new ConstraintGraph();
					gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
			        gResults = rgd.searchBottomNodes(gResults);
			        System.out.print("\n => End of Union: \n");
			        ConstraintGraph.Graph2Console(gResults);
			        ShowBottomWarning = true;
					fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					str = Log.getText() + "\n" + "Union done!";
	    			Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			
	    			//Time stamping
	    			date= new java.util.Date();
	    			stamp = stamp + "Union done: \n";
	    			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
	    			System.out.println(stamp);
	    			writer.write(stamp);
	    	        writer.close();
				}catch(Exception ex)
				{
					str = Log.getText() + "\n" + ex.getMessage();
	    			Log.setText(str);
	    			gResults = null;
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			}
		};
		worker.start();
	}
	
	/**
	 * Runs projection in another thread
	 * 
	 */
	public void runProjection()
	{
		enableButtons(false);
		root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String str = Log.getText() + "\nRunning projection over " + pathOnt1;
		Log.setText(str);
		root.update(root.getGraphics());

		Thread worker = new Thread() {
			public void run() {
				String str;
				try{
					//Time Stamp
					String path = System.getProperty("user.home") + "/LastRunProcedureTime.txt";
			    	File logFile = new File(path);
			    	BufferedWriter writer = null;
			    	java.util.Date date;
			    	date = new java.util.Date();
					String stamp;
					stamp = "Running Projection: \n";
					stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
					System.out.println(stamp);
					writer = new BufferedWriter(new FileWriter(logFile));
			    	
					str = Log.getText();
					ProjectionTableModel model2 = (ProjectionTableModel)jTable2.getModel();
					ArrayList<Integer> checked = new ArrayList<Integer>();
					ArrayList<Integer> unchecked = new ArrayList<Integer>();
					ArrayList<String> uncheckedNames = new ArrayList<String>();
					//1st I initialized the checked nodes list with the classes and properties checked
					//and their complements
					for(ProjectionItem p : model2.getProjectionItemList())
					{
						if(p.getProjection())
						{
							Node n = p.getNode();
							checked.add(n.getId());
							if(n.getNodeType() == NodeType.Class)
							{
								NodeClass nc = (NodeClass) n;
								int idNotN = gOnt1.getNodeKeyComplementOfClass(nc.getExpression().toString());
								if(idNotN >= 0)
								{
									checked.add(idNotN);
								}
							}
							else
							{
								NodeProperty np = (NodeProperty) n;
								int idNotN = gOnt1.getNodeKeyComplementOfProperty(np.getDescIRI(),np.getDescProp());
								if(idNotN >= 0)
								{
									checked.add(idNotN);
								}
							}
							
						}else
						{
							unchecked.add(p.getNode().getId());
							uncheckedNames.add(p.getFullName());
						}
					}
					//Checks if anything was selected
					if(checked.isEmpty())
					{
						str = str + "\n" + "No nodes selected for Projection...";
		    			Log.setText(str);
		    			enableButtons(true);
		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    			writer.close();
		    			return;
					}
					//Clear Results Table
					DefaultTableModel modelInc = clearResultsTable();
					//now we add the complements of class and properties whose classes and properties
					//are not checked to the unchecked list 
					HashMap<Integer,Node> vertices = gOnt1.getVertices();
					for(Map.Entry<Integer, Node> temp : vertices.entrySet())
					{
			        	Node n = temp.getValue();
			        	NodeType type = n.getNodeType();
			        	if((!checked.contains(n.getId()))&&
		        		(type.equals(NodeType.RestrictionComplementOfClass)))
		        		{
			        		unchecked.add(n.getId());
			        		uncheckedNames.add(n.getFullName());
			        	}
			        	else if((!checked.contains(n.getId()))&&
		        		(type.equals(NodeType.RestrictionComplementOfProperty)))
			        	{
			        		unchecked.add(n.getId());
			        		uncheckedNames.add(n.getFullName());
			        	}
			        }
			        //then we search the cardinality restrictions related to the checked nodes
					for(Map.Entry<Integer, Node> temp : vertices.entrySet())
					{
			        	Node n = temp.getValue();
			        	String description = n.getExpression().toString();
			        	NodeType type = n.getNodeType();
			        	if((type.equals(NodeType.RestrictionCardinality))||(type.equals(NodeType.RestrictionComplementOfRestrictionCardinality)))
			        	{
		        			if((!unchecked.contains(n.getId()))&&(!checked.contains(n.getId())))
		        			{
					        	for(String s: uncheckedNames)
					        	{
					        		if(description.contains(s))
					        		{
					        			unchecked.add(n.getId());
					        			break;
					        		}
					        	}
		        			}
			        	}
			        }
					//Run projection
					//Now we get the closure for gOnt1
					BreadthFirstSearch bfs = new BreadthFirstSearch(gOnt1);
					ArrayList<LinkedList<Integer>> originalTransitiveClosure = bfs.getReachable();
					ArrayList<LinkedList<Integer>> projectionTransitiveClosure = new ArrayList<LinkedList<Integer>>();
					//Then we create a new closure without the non-checked nodes and the ones that references them
					for(LinkedList<Integer> tempRO : originalTransitiveClosure)
					{
						if(!unchecked.contains(tempRO.get(0)))
						{
							LinkedList<Integer> tempR = new LinkedList<Integer>();
							for(Integer i : tempRO)
							{
								if(!unchecked.contains(i))
								{
									tempR.add(i);
								}
							}
							projectionTransitiveClosure.add(tempR);
						}
					}
					gResults = new Graph();
					ArrayList<LinkedList<Integer>> newAdj = new ArrayList<LinkedList<Integer>>();
					for(LinkedList<Integer> tempRP : projectionTransitiveClosure)
					{
						LinkedList<Integer> newAdjTemp = new LinkedList<Integer>();
						for(int i : tempRP)
						{
							Node n = vertices.get(i);
							NodeType type = n.getNodeType();
							int tempID = -1;
							Boolean refs = false;
							if(!checked.contains(tempRP.get(0)))
							{
								//checks if nodes not checked references checked nodes
								for(LinkedList<Integer> insAdj : projectionTransitiveClosure)
								{
									if(insAdj.get(0) == i)
									{
										for(int j : insAdj)
									    {
											if(checked.contains(j))
										    {
												refs = true;
											    break;
										    }
									    }
										break;
									}
								}								
							}
							else
							{
								refs = true;
							}
							switch(type)
			            	{
							   case Class :
							       NodeClass nc = (NodeClass)n;
							       tempID = gResults.getNodeKey(nc.getExpression());
							       if(tempID == -1)
							       {
							    	   tempID = gResults.insertNode(nc.getExpression(), NodeType.Class);
							       }
							       break;
							   case Property :
							       NodeProperty np = (NodeProperty)n;
							       tempID = gResults.getNodeKeyProperty(np.getDescIRI(),np.getDescProp());
							       if(tempID == -1)
							       {
							    	   tempID = gResults.insertNodeProperty(np.getDescIRI(),np.getDescProp());
							       }
							       break;
							   case RestrictionCardinality : 
								   if(refs)
								   {
									   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n;
									   tempID = gResults.getNodeKeyRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());
									   if(tempID == -1)
								       {
								    	   tempID = gResults.insertNodeRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());
								       }
								   }								       
							       break;
							   case RestrictionComplementOfClass :
								   if(refs)
								   {
									   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n;
									   tempID = gResults.getNodeKeyComplementOfClass(notClass.getExpClass().toString());
									   if(tempID == -1)
								       {
								    	   tempID = gResults.insertNodeRestrictionComplementOfClass(notClass.getExpClass().toString());
								       }
								   }
							       break;
							   case RestrictionComplementOfProperty :
								   if(refs)
								   {
									   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n;
								       tempID = gResults.getNodeKeyComplementOfProperty(notProp.getDescIRI(),notProp.getDescProp());
								       if(tempID == -1)
								       {
								    	   tempID = gResults.insertNodeRestrictionComplementOfProperty(notProp.getDescProp(), notProp.getDescIRI());
								       }
								   }
							       break;
							   case RestrictionComplementOfRestrictionCardinality :
								   if(refs)
								   {
									   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n;
								       String desc = notRestCard.getExpression();
								       desc = desc.replaceAll("#", "/");
								       String iriDomain = UsefulOWL.returnIRIDomain(desc);
								       String iriProp = UsefulOWL.returnIRIProp(desc);
								       String descProp = UsefulOWL.returnProp(desc);
								       String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
								       int card = UsefulOWL.returnCard(desc);
								       tempID = gResults.getNodeKeyComplementOfRestrictionCardinality(iriDomain,iriProp,descProp,descRangeOrDomain,card);
								       if(tempID == -1)
								       {
								    	   tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinality(notRestCard.getExpression());
								       }
								   }
							       break;
							   default: 
								   break;
						    }
							if(tempID != -1)
							{
								newAdjTemp.add(tempID);
							}
						}
						if(!newAdjTemp.isEmpty())
						{
							newAdj.add(newAdjTemp);
						}
					}
					//Adding edges to adj
					ArrayList<LinkedList<Integer>> adj = gResults.getAdj();
					for(LinkedList<Integer> temp : newAdj)
					{
						LinkedList<Integer> tempI = adj.get(temp.get(0));
						for(int i : temp)
						{
							if(!tempI.contains(i))
							{
								tempI.addLast(i);
							}
						}
					}
					ShowBottomWarning = true;
					fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					ConstraintGraph rgd = new ConstraintGraph();
					gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
			        gResults = rgd.searchBottomNodes(gResults);
	    			str = Log.getText() + "\n" + "Projection done!";
	    			Log.setText(str);
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			
	    			//Time stamping
	    			date= new java.util.Date();
	    			stamp = stamp + "Projection done: \n";
	    			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
	    			System.out.println(stamp);
	    			writer.write(stamp);
	    	        writer.close();
				}catch(Exception ex)
				{
					str = Log.getText() + "\n" + ex.getMessage();
	    			Log.setText(str);
	    			gResults = null;
	    			enableButtons(true);
	    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			}
		};
		worker.start();
		
	}
	
	/**
	 * Clear Results Tables
	 */
	public DefaultTableModel clearResultsTable()
	{
		DefaultTableModel modelInc = new DefaultTableModel(
	               new Object [][] {

	               },
	               new String [] {
	                   "Resulting Ontology", ""
	               });				
		jTableInc.setModel(modelInc);
		gResults = null;
		btnSave.setEnabled(false);
		return modelInc;
	}

	/**
	 * Implements Show and Hide action for the Elements IRI in Ontology 2 
	 *
	 * @author Rômulo de Carvalho Magalhães
	 *
	 */
	public void minimizeGraphBatch (String operation){
		//public void actionPerformed(ActionEvent e) 
	    //{
			if(gResults != null)
			{
				//root = SwingUtilities.getRoot((JButton) e.getSource());
				String str = LogBatch;
				try{
					//enableButtons(false);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			CleanUp = true;
	    			
	    			//Thread worker = new Thread() {
	    				//@SuppressWarnings({ "rawtypes", "unchecked" })
						//public void run() {
	    					str += LogBatch + "\nMinimizing Resulting Graph";
	    					try{
	    						//Evaluate cardinality restrictions according to the Procedure realized
	    						HashMap<Integer,Node> vertices = gResults.getVertices();
	    						ArrayList<LinkedList<Integer>> adj = gResults.getAdj();
	    						
	    						LinkedList<Integer> Nodes = new LinkedList<Integer>();
	    						LinkedList<Integer> MatchedNodes = new LinkedList<Integer>();
	    						
	    						Set s = vertices.entrySet();
	    				        Iterator it = (Iterator) s.iterator();
	    				        
	    				        while(it.hasNext()) 
	    				        {
	    				            Map.Entry<Integer, Node> temp = (Map.Entry<Integer, Node>) it.next();
	    				            Node n = temp.getValue();
	    				            NodeType type = n.getNodeType();
	    				            int id = n.getId();
	    				    		if((type ==  NodeType.RestrictionCardinality)||(type ==  NodeType.RestrictionComplementOfRestrictionCardinality))
	    				    		{
	    				    			if(!Nodes.contains(id)&&!MatchedNodes.contains(id))
	    				    			{
	    				    				int n2fid = Graph.findDifferentNodeRCKey(n,gResults);
	    				    				if(n2fid != -1)
	    				    				{
	    				    					Node n2 = vertices.get(n2fid);
	    				    					if((n.isNodeBottom() == n2.isNodeBottom())
	    				    							&&(n.isNodeTop() == n.isNodeTop()))
	    				    					{
		    				    					Nodes.add(id);
		    				    					MatchedNodes.add(n2fid);
		    				    					
		    				    					if(type ==  NodeType.RestrictionCardinality)
		    				    					{
		    				    						NodeRestrictionCardinality nrc;  
	    				    						    NodeRestrictionCardinality nrc2; 
	    				    						   // String tempstr = (String)(jComboBoxOperation.getSelectedItem());
	    				    			    			if(operation.equals("Intersection"))
	    				    			    			{
	    				    			    				if(((NodeRestrictionCardinality)n).getCard() > ((NodeRestrictionCardinality)n2).getCard())
		    				    						    {
		    				    						    	nrc = (NodeRestrictionCardinality)n;
		    				    						    	nrc2 = (NodeRestrictionCardinality)n2;
		    				    						    }
		    				    						    else
		    				    						    {
		    				    						    	nrc = (NodeRestrictionCardinality)n2;
		    				    						    	nrc2 = (NodeRestrictionCardinality)n;
		    				    						    }
	    				    			    			}
	    				    			    			else
	    				    			    			{
		    				    						    if(((NodeRestrictionCardinality)n).getCard() < ((NodeRestrictionCardinality)n2).getCard())
		    				    						    {
		    				    						    	nrc = (NodeRestrictionCardinality)n;
		    				    						    	nrc2 = (NodeRestrictionCardinality)n2;
		    				    						    }
		    				    						    else
		    				    						    {
		    				    						    	nrc = (NodeRestrictionCardinality)n2;
		    				    						    	nrc2 = (NodeRestrictionCardinality)n;
		    				    						    }
	    				    			    			}
	    				    						    
		    				    						int idfinal = nrc.getId();
		    				    						int idreplace = nrc2.getId();
	    				    						    LinkedList<Integer> tempC1 = adj.get(idfinal);
	    				    						    LinkedList<Integer> tempC2 = adj.get(idreplace);
	    				    						    boolean replace = true;
	    				    						    
	    				    						    if(tempC2.size() >= 2)
	    				    				        	{
	    				    				        		for(int i = 1; i < tempC2.size(); i++)
	    				    				        		{
	    				    				        			int element = tempC2.get(i);
	    				    				        			if(!tempC1.contains(element))
	    				    				        			{
	    				    				        				replace = false;
	    				    				        			}
	    				    				        		}
	    				    				        	}
	    				    						    if(replace)
	    				    						    {
		    				    						    for(LinkedList<Integer> tempAdj : adj)
		    					    				        {
		    				    						    	if(tempAdj.contains(idreplace))
		    				    						    	{
		    				    						    		int index = tempAdj.indexOf(idreplace);
		    				    						    		if(index > 0)
		    				    						    		{
		    				    						    			if(!tempAdj.contains(idfinal))
		    				    						    			{
		    				    						    				replace = false;
		    				    						    			}
		    				    						    		}
		    				    						    	}
		    					    				        }
	    				    						    }
	    				    						    if(replace)
	    				    						    {
	    				    						    	nrc2.setRemove(true);
	    				    						    	while(tempC2.size() >= 2)
    				    						    		{
    				    						    			tempC2.removeLast();
    				    						    		}
    				    						    		for(LinkedList<Integer> tempAdj : adj)
		    					    				        {
		    				    						    	if(tempAdj.contains(idreplace))
		    				    						    	{
		    				    						    		int index = tempAdj.indexOf(idreplace);
		    				    						    		if(index > 0)
		    				    						    		{
		    				    						    			tempAdj.remove(index);
		    				    						    		}
		    				    						    	}
		    					    				        }
	    				    						    }
		    				    					}
		    				    					else
		    				    					{
		    				    						NodeRestrictionComplementOfRestrictionCardinality nrc;
		    				    						NodeRestrictionComplementOfRestrictionCardinality nrc2;
		    				    						//String tempstr = (String)(jComboBoxOperation.getSelectedItem());
	    				    			    			if(operation.equals("Intersection"))
	    				    			    			{
	    				    			    				if(((NodeRestrictionCardinality)n).getCard() < ((NodeRestrictionCardinality)n2).getCard())
		    				    						    {
		    				    						    	nrc = (NodeRestrictionComplementOfRestrictionCardinality)n;
		    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n2;
		    				    						    }
		    				    						    else
		    				    						    {
		    				    						    	nrc = (NodeRestrictionComplementOfRestrictionCardinality)n2;
		    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n;
		    				    						    }
	    				    			    			}
	    				    			    			else
	    				    			    			{
			    				    						if(((NodeRestrictionComplementOfRestrictionCardinality)n).getCard() > ((NodeRestrictionComplementOfRestrictionCardinality)n2).getCard())
			    				    						{
			    				    							nrc = (NodeRestrictionComplementOfRestrictionCardinality)n;
		    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n2;
		    				    						    }
		    				    						    else
		    				    						    {
		    				    						    	nrc = (NodeRestrictionComplementOfRestrictionCardinality)n2;
		    				    						    	nrc2 = (NodeRestrictionComplementOfRestrictionCardinality)n;
			    				    						}
	    				    			    			}
		    				    						
		    				    						int idfinal = nrc.getId();
		    				    						int idreplace = nrc2.getId();
	    				    						    LinkedList<Integer> tempC1 = adj.get(idfinal);
	    				    						    LinkedList<Integer> tempC2 = adj.get(idreplace);
	    				    						    boolean replace = true;
	    				    						    
	    				    						    if(tempC2.size() >= 2)
	    				    				        	{
	    				    				        		for(int i = 1; i < tempC2.size(); i++)
	    				    				        		{
	    				    				        			int element = tempC2.get(i);
	    				    				        			if(!tempC1.contains(element))
	    				    				        			{
	    				    				        				replace = false;
	    				    				        			}
	    				    				        		}
	    				    				        	}
	    				    						    
	    				    						    if(replace)
	    				    						    {
		    				    						    for(LinkedList<Integer> tempAdj : adj)
		    					    				        {
		    				    						    	if(tempAdj.contains(idreplace))
		    				    						    	{
		    				    						    		int index = tempAdj.indexOf(idreplace);
		    				    						    		if(index > 0)
		    				    						    		{
		    				    						    			if(!tempAdj.contains(idfinal))
		    				    						    			{
		    				    						    				replace = false;
		    				    						    			}
		    				    						    		}
		    				    						    	}
		    					    				        }
	    				    						    }
	    				    						    if(replace)
	    				    						    {
	    				    						    	nrc2.setRemove(true);
	    				    						    	while(tempC2.size() >= 2)
    				    						    		{
    				    						    			tempC2.removeLast();
    				    						    		}
		    				    						    for(LinkedList<Integer> tempAdj : adj)
		    					    				        {
		    				    						    	if(tempAdj.contains(idreplace))
		    				    						    	{
		    				    						    		int index = tempAdj.indexOf(idreplace);
		    				    						    		if(index > 0)
		    				    						    		{
		    				    						    			tempAdj.remove(index);
		    				    						    		}
		    				    						    	}
		    					    				        }
	    				    						    }
		    				    					}
		    				    				}
	    				    				}
	    				    			}
	    				    		}	    				            
	    				        }
	    						//Copy Adjacent List
	    				        LinkedList<Integer> tempChecked;
	    				        ArrayList<LinkedList<Integer>> adjChecked = new ArrayList<LinkedList<Integer>>();
	    				        for(LinkedList<Integer> tempC1 : adj)
	    				        {
	    				        	tempChecked = new LinkedList<Integer>();
	    				        	//Collections.copy(tempChecked,tempC1);
	    				        	
	    				        	for(int i = 0; i < tempC1.size(); i++)
	    				        	{
	    				        		int Element = tempC1.get(i);
	    				        		tempChecked.add(Element);
	    				        	}
	    				        	
	    				        	adjChecked.add(tempChecked);
	    				        }
	    				        
	    				        //finds MEG deleting unnecessary edges
	    				        for(LinkedList<Integer> tempC1 : adj)
	    				        {
	    				        	int OriginalElement = tempC1.get(0);
	    				        	if(tempC1.size() >= 2)
	    				        	{
	    				        		for(int i = 1; i < tempC1.size(); i++)
	    				        		{
	    				        			int Element = tempC1.get(i);
	    				        			LinkedList<Integer> tempC2 = adjChecked.get(Element);
	    				        			for(int j = 1; j < tempC2.size(); j++)
		    				        		{
	    				        				int TestReacheble = tempC2.get(j);
	    				        				int TestIndex = tempC1.indexOf(TestReacheble);
	    				        				if(TestIndex > 0)
	    				        				{
	    				        					tempC1.remove(TestIndex);
	    				        				}
	    				        				/*Cycle
	    				        				else if(TestIndex == 0)
	    				        				{
	    				        				}
	    				        				*/
		    				        		}
	    				        		}
	    				        	}
	    				        }
	    				        gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
	    				        
	    				        //ConstraintGraph.Graph2Console(gResults);
	    				        
	    						/*if(!HideIRIResults)
	    		    			{
	    		    				//Cleaning table
	    		    				DefaultTableModel modelInc = new DefaultTableModel(
	    		    			               new Object [][] {

	    		    			               },
	    		    			               new String [] {
	    		    			                   "Resulting Ontology", ""
	    		    			               });				
	    		    				//jTableInc.setModel(modelInc);
	    			    			//fill table with ontology
	    		    				//modelInc = fillJTableWithIRI(gResults, modelInc);
	    		    			}
	    		    			else
	    		    			{
	    		    				//Cleaning table
	    		    				DefaultTableModel modelInc = new DefaultTableModel(
	    		    			               new Object [][] {

	    		    			               },
	    		    			               new String [] {
	    		    			                   "Resulting Ontology", ""
	    		    			               });				
	    		    				//jTableInc.setModel(modelInc);
	    			    			//fill table with ontology
	    		    				//modelInc = fillJTableWithoutIRI(gResults, modelInc);
	    		    			}*/
	    				        
	    		    			//enableButtons(true);
	    		    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    		    			//str = str + "\nMinimization Done!";
	    		    			//LogBatch+=str;
	    				        
	    					}catch(Exception ex)
	    					{
	    						System.out.println(ex.getStackTrace());
	    						//str = LogBatch + "\n" + ex.getMessage();
	    		    			//LogBatch+=str;
	    		    			//enableButtons(true);
	    		    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    		    			return;
	    					}
	    				//}
	    			//};
	    			//worker.start();
				}
				catch(Exception ex)
				{
					System.out.println(ex.getStackTrace());
					//str = LogBatch + "\nError : " + ex.getMessage();
	    			//LogBatch +=str;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			}
	    //}
	}


	private void saveRBatch(String path, String file) {
		// TODO Auto-generated method stub
		//root = SwingUtilities.getRoot((JButton) e.getSource());
    	//JFileChooser c = new JFileChooser();
    	String str = "";
    	
    	// Demonstrate "Save" dialog:
    	//int rVal = c.showSaveDialog(OntologyManagerTab.this);
    	//if(rVal == JFileChooser.APPROVE_OPTION)
    	//{	
    		try{
    			//pathOntResults = c.getSelectedFile().getAbsolutePath().toString();
    			pathOntResults = path+file;
    			//nameOntResults = c.getSelectedFile().getName().toString();
    			nameOntResults = file;
    			int num = pathOntResults.lastIndexOf(".");
    			
    			if(num == -1)
    			{
    				pathOntResults = pathOntResults + "Normalized.owl";
    				nameOntResults = nameOntResults + "Normalized.owl";
    			}
    			else if (!pathOntResults.endsWith("Normalized.owl"))
    			{
    				String s = pathOntResults.substring(0, pathOntResults.lastIndexOf("."));
    				pathOntResults = pathOntResults + "Normalized.owl";
    				nameOntResults = nameOntResults + "Normalized.owl";
    			}
    			
    			/*
    			if(num == -1)
    			{
    				pathOntResults = pathOntResults + ".owl";
    				nameOntResults = nameOntResults + ".owl";
    			}
    			*/
    			//enableButtons(false);
    			//btnSave.setEnabled(false);
    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    			//LogBatch+= "\nSaving Ontology as: " + pathOntResults + "\n";
    			//System.out.println(LogBatch);
    			//root.update(root.getGraphics());

    	//		Thread worker = new Thread() {
    		//		public void run() {
    			//		String str;
    					try{
    						//Save Ontology
    						//SaveOntology.SaveOntologyToFile(pathOntResults, nameOntResults, gResults, Log);
    						// save without the normalized for batch process
    						SaveOntology.SaveOntologyToFile(path+file, nameOntResults, gResults, Log);
			    			
			    			//enableButtons(true);
    		    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    					}catch(Exception ex)
    					{
    						LogBatch += "\n erro saveRbatch:" + ex.getMessage();
    		    			//Log.setText(str);
    		    			//enableButtons(true);
    		    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		    			return;
    					}
	    //			}
    		//	};
    			//worker.start();
    		}
    		catch(Exception ex)
    		{
    			//LogBatch+= "\n saveRbatch: " + ex.getMessage();
    			ex.printStackTrace();
    			//enableButtons(true);
    			//btnSave.setEnabled(true);
    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    			return;
    		}
    	//}
    	/*if(rVal == JFileChooser.CANCEL_OPTION)
    	{
    		str = Log.getText() + "\n" + "You pressed cancel, Ontology was not saved";
    		Log.setText(str);
    	}*/
   // }

	}

	private void runOntBatch(String operation) {
		// TODO Auto-generated method stub
		//root = SwingUtilities.getRoot((JButton) e.getSource());
    	String str;
    	try{
    		if(gOnt1 != null)
    		{
    			//str = (String)(jComboBoxOperation.getSelectedItem());
    			str = operation;
    			
    			if(str.equals("Projection"))
    			{
    				//runProjectionBatch();	    				
    			}else
    			{
    				if(gOnt2 == null)
    				{
    					//str = Log.getText() + "\nPlease load Second Antology";
    	    			//Log.setText(str);
    					LogBatch += "\nPlease load Second Antology";
    	    			return;
    				}
    				if(str.equals("Union"))
    				{
    					runUnionBatch();
    				}else if(str.equals("Intersection"))
    				{
    					runIntersectionBatch();
    				}else if(str.equals("Difference"))
    				{
    					runDifferenceBatch();
    				}
    			}
    			
    		}else
    		{
    			//LogBatch+= "\nPlease load an Antology first";
    			//System.out.println(LogBatch);
    			System.out.println("\nPlease load an Antology first");
    			return;
    		}
			
    	}catch(Exception ex)
    	{
    		LogBatch+= "\n Error runOntBatch" + ex.getMessage();
    		//System.out.println(LogBatch);
			//enableButtons(true);
			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    	}

	}

	private void openBatch2(String path, String file) {
		// TODO Auto-generated method stub
		String str;
		try{
			//enableButtons(false);
			//root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			//pathOnt1 = c.getSelectedFile().toString();
			//nameOnt1 = c.getSelectedFile().getName().toString();
			pathOnt2 = path +file;
			System.out.println("path completo:" +pathOnt2);
			nameOnt2 = file;
			System.out.println("file:" +nameOnt2);
			LogBatch += "\nLoading Batch: " + path + file;
			//System.out.println(LogBatch);
			//root.update(root.getGraphics());
			
	//		Thread worker = new Thread() {
		//		public void run() {
			//		String str;
					try{
						str = LogBatch;
    					Normalization norm = new Normalization();
    					Object[] o = norm.runOntologyNormalization(pathOnt2, nameOnt2, osType);
    	    			String pathNorm = o[0].toString();
    	    			//exception inside normalization...
		    			if(pathNorm.substring(0, 5).equals("Error"))
		    			{
		    				str = str + "\n" + pathNorm;
		    				LogBatch += str;
		    				//enableButtons(true);
    		    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    				return;
		    			}
		    			IRIOnt2 = o[1].toString();
		    			System.out.print("\n Original IRI Ontology 2: " + IRIOnt2 + "\n");
		    			Ont2 = loadOntBatch(pathNorm);
		    			ConstraintGraph r = new ConstraintGraph();
		    			gOnt2 = new Graph();
		    			gOnt2 = r.createGraph(Ont2, Log);
		    			
		    			// store to use later without open files ou process normalization and graphs again
		    			Object [] OntAUX = new Object [7];
		    		    /*		obj[0] = ontology;
		    			obj[1] = factory;
		    			obj[2] = manager;
		    		    obj[3] = ontPath;
		    		    obj[4] = ontName;
		    		    obj[5] = graph;
		    		    obj[6] = IRI;
		    		*/
		    			OntAUX[0] = Ont2[0];
		    			OntAUX[1] = Ont2[1];
		    			OntAUX[2] = Ont2[2];
		    			OntAUX[3] = Ont2[3];
		    			OntAUX[4] = file;
		    			OntAUX[5] = gOnt2;
		    			OntAUX[6] = IRIOnt2;
		    			 
		    			ontologiesProcessed.add(OntAUX); 
		    			//Ontology loaded into memory, cleaning table 1
		    			/*DefaultTableModel model1 = new DefaultTableModel(
		    					new Object [][] {

		    					},
		    					new String [] {
		    							nameOnt1, ""
		    					});
		    			*/
		    			//jTable1.setModel(model1);
		    			//fill table with ontology
		    			//ShowBottomWarning = true;
		    			CleanUp = false;
		    			//model1 = fillJTableWithIRI(gOnt1, model1);
		    			//str = LogBatch;
		    			//str = str + "\n" + "Ontology successfully loaded as Ontology 2 batch";
		    			//LogBatch += str;
		    			HideIRI1 = false;
		    			//loading possible Projection on jTable2
		    			/*str = (String)(jComboBoxOperation.getSelectedItem());
		    			if(str.equals("Projection"))
		    			{
		    				ProjectionTableModel model2 = new ProjectionTableModel();
		    				jTable2.setModel(model2);
		    				fillProjectionList(gOnt1, model2);	    				
		    			}
		    			enableButtons(true);
		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    			*/
					}catch(Exception ex)
					{
						LogBatch+= "\nError openBatch2 : " + ex.getMessage();
						//System.out.println(LogBatch);
		    			//enableButtons(true);
		    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    			return;
					}
  //  			}
//			};
//			worker.start();
		}
		catch(Exception ex)
		{
			LogBatch+= "\n Error openBatch2:" + ex.getMessage();
			//System.out.println(LogBatch);
			//enableButtons(true);
			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}

	}

	private void openBatch1(String path, String file) {
		// TODO Auto-generated method stub
		String str;
		try{
			//enableButtons(false);
			//root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			//pathOnt1 = c.getSelectedFile().toString();
			//nameOnt1 = c.getSelectedFile().getName().toString();
			pathOnt1 = path +file;
			System.out.println("path completo:" +pathOnt1);
			nameOnt1 = file;
			System.out.println("file:" +file);
			LogBatch += "\nLoading Batch: " + path + file;
			//System.out.println(LogBatch);
			//root.update(root.getGraphics());
			
			//Thread worker = new Thread() {
				//public void run() {
					//String str;
					try{
						str = LogBatch;
    					Normalization norm = new Normalization();
    					Object[] o = norm.runOntologyNormalization(pathOnt1, nameOnt1, osType);
    	    			String pathNorm = o[0].toString();
    	    			//exception inside normalization...
		    			if(pathNorm.substring(0, 5).equals("Error"))
		    			{
		    				str = str + "\n" + pathNorm;
		    				LogBatch+=str;
		    				//enableButtons(true);
    		    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    				return;
		    			}
		    			IRIOnt1 = o[1].toString();
		    			System.out.print("\n Original IRI Ontology 1: " + IRIOnt1 + "\n");
		    			Ont1 = loadOntBatch(pathNorm);
		    			ConstraintGraph r = new ConstraintGraph();
		    			gOnt1 = new Graph();
		    			gOnt1 = r.createGraph(Ont1, Log);
		    			
		    			// store to use later without open files ou process normalization and graphs again
		    			Object [] OntAUX = new Object [7];
		    		    /*		obj[0] = ontology;
		    			obj[1] = factory;
		    			obj[2] = manager;
		    		    obj[3] = ontPath;
		    		    obj[4] = ontName;
		    		    obj[5] = graph;
		    		    obj[6] = IRI;
		    		*/
		    			OntAUX[0] = Ont1[0];
		    			OntAUX[1] = Ont1[1];
		    			OntAUX[2] = Ont1[2];
		    			OntAUX[3] = Ont1[3];
		    			OntAUX[4] = file;
		    			OntAUX[5] = gOnt1;
		    			OntAUX[6] = IRIOnt1;
		    			 
		    			ontologiesProcessed.add(OntAUX); 
		    			
		    			//Ontology loaded into memory, cleaning table 1
		    			/*DefaultTableModel model1 = new DefaultTableModel(
		    					new Object [][] {

		    					},
		    					new String [] {
		    							nameOnt1, ""
		    					});
		    			*/
		    			//jTable1.setModel(model1);
		    			//fill table with ontology
		    			//ShowBottomWarning = true;
		    			CleanUp = false;
		    			//model1 = fillJTableWithIRI(gOnt1, model1);
		    			//str = LogBatch;
		    			//str = str + "\n" + "Ontology successfully loaded as Ontology 1 batch";
		    			//LogBatch+=str;
		    			HideIRI1 = false;
		    			//loading possible Projection on jTable2
		    			/*str = (String)(jComboBoxOperation.getSelectedItem());
		    			if(str.equals("Projection"))
		    			{
		    				ProjectionTableModel model2 = new ProjectionTableModel();
		    				jTable2.setModel(model2);
		    				fillProjectionList(gOnt1, model2);	    				
		    			}
		    			enableButtons(true);
		    			root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    			*/
					}catch(Exception ex)
					{
						LogBatch+= "\nError openBatch1: " + ex.getMessage();
		    			//System.out.println(LogBatch);
		    			//enableButtons(true);
		    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    			return;
					}
    			//}
			//};
			//worker.start();
		}
		catch(Exception ex)
		{
			LogBatch+= "\n Error openBatch1" + ex.getMessage();
			//System.out.println(LogBatch);
			//enableButtons(true);
			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
	}
	
	
	/**
	 * Using the OWL API initializes the manager, ontology and datafactory from the file and returns the object containing it
	 * 
	 * @param ontPath
	 * @return Object containing manager, ontology, datafactory and original OWL file path 
	 */
	public Object[] loadOntBatch(String ontPath) 
	{
		String str = LogBatch;
		Object[] obj = new Object[4];
		try {	                
			warninglbl.setVisible(false);
			OWLOntologyManager manager = (OWLOntologyManager) OWLManager.createOWLOntologyManager();
            IRI physicalURI = IRI.create( "file:" + ontPath);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(physicalURI);	                
			OWLDataFactory factory = manager.getOWLDataFactory();
         	obj[0] = ontology;
			obj[1] = factory;
			obj[2] = manager;
            obj[3] = ontPath;
		    return obj;
		} 
		catch (OWLOntologyCreationException e) 
		{
				str = str +"\n" + "Error Loading Ontology : \n" + e.getMessage();
				LogBatch += str;
				//warninglbl.setText("Error Loading : "+ ontPath);
				//warninglbl.setVisible(true);
		}
	    return null;
    }

	/**
	 * Runs difference in another thread
	 * 
	 */
	public void runDifferenceBatch()
	{
		//enableButtons(false);
		//root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		//String str = LogBatch + "\nRunning Difference over " + pathOnt1 + " and " + pathOnt2;
		//LogBatch+=str;
		//root.update(root.getGraphics());

		//Thread worker = new Thread() {
			//public void run() {
				//String str;
				try{
					//Time Stamp
					String path = System.getProperty("user.home") + "/LastRunProcedureTime.txt";
			    	File logFile = new File(path);
			    	BufferedWriter writer = null;
			    	java.util.Date date;
			    	date = new java.util.Date();
					String stamp;
					stamp = "Running Difference: \n";
					stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
					System.out.println(stamp);
					writer = new BufferedWriter(new FileWriter(logFile));
					
					//str = Log.getText();
					/**
					 * 1st I will get all the reachable from the 1st and the 2nd Ontologies
					 * 
					 * then I will drop from the 1st every adj that is equal in both ontologies(also all Cardinality 
					 * restriction nodes that are equal)
					 * 
					 * by the end, if a node is without adj I will drop it too
					 */
					BreadthFirstSearch bfs1 = new BreadthFirstSearch(gOnt1);
					BreadthFirstSearch bfs2 = new BreadthFirstSearch(gOnt2);
					bfs1.fullBFS();
					bfs2.fullBFS();
					ArrayList<LinkedList<Integer>> TransitiveClosure1 = bfs1.getReachable();
					ArrayList<LinkedList<Integer>> TransitiveClosure2 = bfs2.getReachable();
					HashMap<Integer,Node> vertices1 = gOnt1.getVertices();
					HashMap<Integer,Node> vertices2 = gOnt2.getVertices();
					
					//DefaultTableModel modelInc = clearResultsTable();
					gResults = new Graph();
					//Equivalent nodes from gOnt1 and gOnt2
					ArrayList<Integer> nodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt2 = new ArrayList<Integer>();
					//Equivalent nodes from gOnt1 and gOnt3
					ArrayList<Integer> finalNodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> finalNodesOnt3 = new ArrayList<Integer>();
					
					for(Map.Entry<Integer, Node> temp : vertices1.entrySet())
					{
			        	Node n1 = temp.getValue();
			        	int nodeKey1 = n1.getId();
			        	NodeType type = n1.getNodeType();
			        	int nodeKey2 = Graph.findNodeKeyDifference(IRIOnt1, n1, IRIOnt2, gOnt2);
			        	if(nodeKey2 == -1)
			        	{
			        		//System.out.print("Não encontrados: \n");
	        				//System.out.print("* "+ nodeKey1+"\n");
		        			int tempID = -1;
		        			switch(type)
			            	{
							   case Class :
							       NodeClass nc = (NodeClass)n1;
							       tempID = gResults.insertNode(nc.getExpression(), NodeType.Class);							      
							       break;
							   case Property :
							       NodeProperty np = (NodeProperty)n1;
							       tempID = gResults.insertNodeProperty(np.getDescIRI(),np.getDescProp());
							       break;
							   case RestrictionCardinality :
								   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
							       tempID = gResults.insertNodeRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
							       break;
							   case RestrictionComplementOfClass :
								   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
							       tempID = gResults.insertNodeRestrictionComplementOfClass(notClass.getExpClass().toString());
							       break;
							   case RestrictionComplementOfProperty :
								   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
							       tempID = gResults.insertNodeRestrictionComplementOfProperty(notProp.getDescProp(), notProp.getDescIRI());
							       break;
							   case RestrictionComplementOfRestrictionCardinality :
								   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
							       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinality(notRestCard.getExpression());
							       break;
							   default: 
								   break;
						    }
		        			if(tempID != -1)
		        			{
		        				finalNodesOnt1.add(nodeKey1);
		        				finalNodesOnt3.add(tempID);
		        			}
		        			else
		        			{
				        		System.out.print("Erro ao incerir: \n");
		        				System.out.print("* "+ nodeKey1+"\n");
		        			}
			        	}
			        	else
			        	{
			        		nodesOnt1.add(nodeKey1);
			        		nodesOnt2.add(nodeKey2);
			        	}
			        }
			        ArrayList<LinkedList<Integer>> CheckedTransitiveClosure = new ArrayList<LinkedList<Integer>>();
			        LinkedList<Integer> tempChecked;
			        for(LinkedList<Integer> tempC1 : TransitiveClosure1)
			        {
			        	tempChecked = new LinkedList<Integer>();
			        	int element = tempC1.get(0);
			        	if(nodesOnt1.contains(element))
			        	{
			        		tempChecked.add(element);
			        		int index = nodesOnt1.indexOf(element);
			        		int element2 = nodesOnt2.get(index);
			        		LinkedList<Integer> tempC2 = UsefulOWL.getListwithFirstElement(TransitiveClosure2, element2);
			        		for(int i : tempC1)
			        		{
			        			if(!nodesOnt1.contains(i))
			        			{
			        				tempChecked.add(i);
			        			}
			        			else
			        			{
			        				index = nodesOnt1.indexOf(i);
					        		element2 = nodesOnt2.get(index);
					        		if(!tempC2.contains(element2))
					        		{
					        			tempChecked.add(i);
					        		}
			        			}
			        		}
			        		if((tempChecked != null)&&(!tempChecked.isEmpty())&&(tempChecked.size()>1))
				        	{
				        		CheckedTransitiveClosure.add(tempChecked);
				        	}
			        	}
			        	else
			        	{
			        		for(int i : tempC1)
			        		{
			        			tempChecked.add(i);
			        		}
			        		if((tempChecked != null)&&(!tempChecked.isEmpty()))
				        	{
				        		CheckedTransitiveClosure.add(tempChecked);
				        	}
			        	}			        	
			        }
			        
			        //Now I have the closure with all the edges that must be added to the result
			        for(LinkedList<Integer> tempC1 : CheckedTransitiveClosure)
			        {
			        	int element = tempC1.get(0);
			        	if(!finalNodesOnt1.contains(element))
			        	{
			        		//1st I insert the node in gResults if it isn't already there
			        		Node n1 = vertices1.get(element);
			        		int nodeKeyR = Graph.findNodeKey(IRIOnt1, n1, "", gResults);
			        		if(nodeKeyR == -1)
				        	{
			        			NodeType type = n1.getNodeType();			        			
			        			int tempID = -1;
			        			switch(type)
				            	{
								   case Class :
								       NodeClass nc = (NodeClass)n1;
								       tempID = gResults.insertNode(nc.getExpression(), NodeType.Class);							      
								       break;
								   case Property :
								       NodeProperty np = (NodeProperty)n1;
								       tempID = gResults.insertNodeProperty(np.getDescIRI(),np.getDescProp());
								       break;
								   case RestrictionCardinality :
									   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
								       tempID = gResults.insertNodeRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
								       break;
								   case RestrictionComplementOfClass :
									   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfClass(notClass.getExpClass().toString());
								       break;
								   case RestrictionComplementOfProperty :
									   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfProperty(notProp.getDescProp(), notProp.getDescIRI());
								       break;
								   case RestrictionComplementOfRestrictionCardinality :
									   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinality(notRestCard.getExpression());
								       break;
								   default: 
									   break;
							    }
			        			if(tempID != -1)
			        			{
			        				finalNodesOnt1.add(n1.getId());
			        				finalNodesOnt3.add(tempID);
			        			}
				        	}
			        	}
			        	//If I already inserted the node in the resulting graph
			        	if(finalNodesOnt1.contains(element))
			        	{
			        		int index = finalNodesOnt1.indexOf(element);
			        		int idResult = finalNodesOnt3.get(index);
			        		ArrayList<LinkedList<Integer>> ResultAdj = gResults.getAdj();
			        		LinkedList<Integer> tempResultAdj = UsefulOWL.getListwithFirstElement(ResultAdj, idResult);
			        		//I get its adj list and add all nodes from CheckedTransitiveClosure
			        		for(int i = 1; i<tempC1.size(); i++)
			        		{
			        			element = tempC1.get(i);
			        			if(finalNodesOnt1.contains(element))
			        			{
			        				//if the node already exists in gResults I add only the edge
			        				index = finalNodesOnt1.indexOf(element);
			        				idResult = finalNodesOnt3.get(index);
			        				if(!tempResultAdj.contains(idResult))
			        				{
			        					tempResultAdj.add(idResult);
			        				}
			        			}
			        			else
			        			{
			        				//I must add the node and then add the edge
			        				Node n1 = vertices1.get(element).getNode();
			        				NodeType type = n1.getNodeType();
			        				int tempID = -1;
				        			switch(type)
					            	{
									   case Class :
									       NodeClass nc = (NodeClass)n1;
									       tempID = gResults.insertNode(nc.getExpression(), NodeType.Class);							      
									       break;
									   case Property :
									       NodeProperty np = (NodeProperty)n1;
									       tempID = gResults.insertNodeProperty(np.getDescIRI(),np.getDescProp());
									       break;
									   case RestrictionCardinality :
										   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
									       tempID = gResults.insertNodeRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
									       break;
									   case RestrictionComplementOfClass :
										   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
									       tempID = gResults.insertNodeRestrictionComplementOfClass(notClass.getExpClass().toString());
									       break;
									   case RestrictionComplementOfProperty :
										   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
									       tempID = gResults.insertNodeRestrictionComplementOfProperty(notProp.getDescProp(), notProp.getDescIRI());
									       break;
									   case RestrictionComplementOfRestrictionCardinality :
										   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
									       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinality(notRestCard.getExpression());
									       break;
									   default: 
										   break;
								    }
				        			if(tempID != -1)
				        			{
				        				finalNodesOnt1.add(n1.getId());
				        				finalNodesOnt3.add(tempID);
				        				tempResultAdj.add(tempID);
				        			}
			        			}
			        		}
			        	}
			        }
					
			        ConstraintGraph rgd = new ConstraintGraph();
					gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
			        gResults = rgd.searchBottomNodes(gResults);
			        //System.out.print("\n => End of Difference: \n");
			        //ConstraintGraph.Graph2Console(gResults);
			        ShowBottomWarning = true;
					//fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					//str = LogBatch + "\n" + "Difference Done!";
	    			//LogBatch+=str;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			System.out.println("Minimize Graphs... after Difference: "+ pathOnt1 + " and " + pathOnt2);
	    			minimizeGraphBatch("Difference");
	    			// store to use later without open files ou process normalization and graphs again
	    			Object [] OntAUX = new Object [7];
	    		    /*		obj[0] = ontology;
	    			obj[1] = factory;
	    			obj[2] = manager;
	    		    obj[3] = ontPath;
	    		    obj[4] = ontName;
	    		    obj[5] = graph;
	    		    obj[6] = IRI;
	    		*/
	    			OntAUX[0] = null;
	    			OntAUX[1] = null;
	    			OntAUX[2] = null;
	    			OntAUX[3] = null;
	    			OntAUX[4] = nameOnt1+"D"+nameOnt2;
	    			OntAUX[5] = gResults;
	    			OntAUX[6] = null;
	    			 
	    			ontologiesProcessed.add(OntAUX);  
	    			
	    			//Time stamping
	    			date= new java.util.Date();
	    			stamp = stamp + "Difference done: \n";
	    			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
	    			System.out.println(stamp);
	    			writer.write(stamp);
	    	        writer.close();
				}catch(Exception ex)
				{
					System.out.println(ex.getStackTrace());
					//str = LogBatch + "\n" + ex.getMessage();
	    			//LogBatch+=str;
	    			gResults = null;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			//}
		//};
		//worker.start();	
	}

	/**
	 * Runs Intersection in another thread
	 * 
	 */
	public void runIntersectionBatch()
	{
		//enableButtons(false);
		//root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String str = LogBatch + "\nRunning Intersection over " + pathOnt1 + " and " + pathOnt2;
		LogBatch += str;
		//root.update(root.getGraphics());

		//Thread worker = new Thread() {
			//@SuppressWarnings({ "rawtypes", "unchecked" })
			//public void run() {
				//String str;
				try{
					//Time Stamp
					String path = System.getProperty("user.home") + "/LastRunProcedureTime.txt";
			    	File logFile = new File(path);
			    	BufferedWriter writer = null;
			    	java.util.Date date;
			    	date = new java.util.Date();
					String stamp;
					stamp = "Running Intersection: \n";
					stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
					System.out.println(stamp);
					writer = new BufferedWriter(new FileWriter(logFile));
					
					str = Log.getText();
					//run intersection
					//1st we pick the vertices that are in both Ontologies
					HashMap<Integer,Node> vertices1 = gOnt1.getVertices();
					HashMap<Integer,Node> vertices2 = gOnt2.getVertices();
					//Clear Results Table
					//DefaultTableModel modelInc = clearResultsTable();
					gResults = new Graph();
					ArrayList<Integer> nodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt2 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt3 = new ArrayList<Integer>();
					Set cont = vertices1.entrySet();
		            Iterator it = (Iterator) cont.iterator();
		            int tempCard1;
		            int tempCard2;
			        while(it.hasNext())
			        {
			        	Map.Entry<Integer, Node> temp = (Map.Entry<Integer, Node>)it.next();
			        	Node n1 = temp.getValue();
			        	//OWLObject o1 = n1.getExpression();
			        	int nodeKey2 = Graph.findNodeKey(IRIOnt1, n1, IRIOnt2, gOnt2); //gOnt2.getNodeKey(o1);
			        	if(nodeKey2 != -1)
			        	{
			        		Node n2 = vertices2.get(nodeKey2);
			        		String s = "\n n1 : " + n1.getExpression().toString();
			        		s= s + "  -  n2 : "+n2.getExpression().toString();
			        		//System.out.print(s);
			        		if(n2.getNodeType().toString().equals(n1.getNodeType().toString()))
			        		{
			        			nodesOnt1.add(n1.getId());
			        			nodesOnt2.add(nodeKey2);
			        			NodeType type = n2.getNodeType();
			        			int tempID = -1;
			        			switch(type)
				            	{
								   case Class :
								       NodeClass nc = (NodeClass)n1;
								       tempID = gResults.insertNodeComp(nc.getExpression(), NodeType.Class);							      
								       break;
								   case Property :
								       NodeProperty np = (NodeProperty)n1;
								       tempID = gResults.insertNodePropertyComp(np.getDescIRI(),np.getDescProp());
								       break;
								   case RestrictionCardinality :
									   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
									   NodeRestrictionCardinality nrc2 = (NodeRestrictionCardinality)n2;
									   tempCard1 = nrc.getCard();
									   tempCard2 = nrc2.getCard();
									   if(tempCard1 < tempCard2)
									   {
										   tempCard1 = tempCard2;
									   }
								       tempID = gResults.insertNodeRestrictionCardinalityComp(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),tempCard1);								       
								       break;
								   case RestrictionComplementOfClass :
									   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfClassComp(notClass.getExpClass().toString());
								       break;
								   case RestrictionComplementOfProperty :
									   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
								       tempID = gResults.insertNodeRestrictionComplementOfPropertyComp(notProp.getDescProp(), notProp.getDescIRI());
								       break;
								   case RestrictionComplementOfRestrictionCardinality :
									   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
									   NodeRestrictionComplementOfRestrictionCardinality notRestCard2 = (NodeRestrictionComplementOfRestrictionCardinality)n2;
									   tempCard1 = UsefulOWL.returnCard(notRestCard.getExpression());
									   tempCard2 = UsefulOWL.returnCard(notRestCard2.getExpression());
									   if(tempCard1 > tempCard2)
									   {
										   tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard2.getExpression());
									   }
									   else
									   {
										   tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard.getExpression());
									   }
								       break;
								   default: 
									   break;
							    }
			        			nodesOnt3.add(tempID);
			        		}
			        	}
			        }
			        //System.out.print("\n => Nodes included: \n");
			        //ConstraintGraph.Graph2Console(gResults);
					//now we get a new Transitive Closure for the final Graph with only the nodes that will be included
			        //Graph 1
			        BreadthFirstSearch bfs1 = new BreadthFirstSearch(gOnt1);
					ArrayList<LinkedList<Integer>> originalTransitiveClosure1 = bfs1.getReachable();
					//ArrayList<LinkedList<Integer>> originalTransitiveClosure1 = gOnt1.getAdj();
					ArrayList<LinkedList<Integer>> newTransitiveClosure1 = new ArrayList<LinkedList<Integer>>();
					//Then we create a new closure without the non-checked nodes and the ones that references them
					for(LinkedList<Integer> tempRO : originalTransitiveClosure1)
					{
						if(nodesOnt1.contains(tempRO.get(0)))
						{
							LinkedList<Integer> tempR = new LinkedList<Integer>();
							for(Integer i : tempRO)
							{
								if(nodesOnt1.contains(i))
								{
									tempR.add(i);
								}
							}
							newTransitiveClosure1.add(tempR);
						}
					}
					//Graph 2
					BreadthFirstSearch bfs2 = new BreadthFirstSearch(gOnt2);
					ArrayList<LinkedList<Integer>> originalTransitiveClosure2 = bfs2.getReachable();
					//ArrayList<LinkedList<Integer>> originalTransitiveClosure2 = gOnt2.getAdj();
					ArrayList<LinkedList<Integer>> newTransitiveClosure2 = new ArrayList<LinkedList<Integer>>();
					for(LinkedList<Integer> tempRO : originalTransitiveClosure2)
					{
						if(nodesOnt2.contains(tempRO.get(0)))
						{
							LinkedList<Integer> tempR = new LinkedList<Integer>();
							for(Integer i : tempRO)
							{
								if(nodesOnt2.contains(i))
								{
									tempR.add(i);
								}
							}
							newTransitiveClosure2.add(tempR);
						}
					}
					//and create the transitive closure of the Resulting graph using it
					//Graph3
					ArrayList<LinkedList<Integer>> newTransitiveClosure3 = new ArrayList<LinkedList<Integer>>();
					for(int i = 0; i < nodesOnt1.size(); i++)
					{
						int element1 = nodesOnt1.get(i);
						LinkedList<Integer> tempRO1 = UsefulOWL.getListwithFirstElement(newTransitiveClosure1, element1);
						int element2 = nodesOnt2.get(i);
						LinkedList<Integer> tempRO2 = UsefulOWL.getListwithFirstElement(newTransitiveClosure2, element2);
						LinkedList<Integer> tempRO3 = new LinkedList<Integer>();
						tempRO3.add(nodesOnt3.get(i));
						
						if((tempRO2 != null) &&(tempRO1 != null))
						{
							for(int j = 1; j < tempRO1.size(); j++)
							{
								element1 = tempRO1.get(j);
								int index = nodesOnt1.indexOf(element1);
								if(index > -1)
								{
									element2 = nodesOnt2.get(index);
									if(tempRO2.contains(element2))
									{
										tempRO3.add(nodesOnt3.get(index));
									}
								}
							}
						}
						newTransitiveClosure3.add(tempRO3);
					}
					gResults.setAdj(newTransitiveClosure3);
					ConstraintGraph rgd = new ConstraintGraph();
					gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
			        gResults = rgd.searchBottomNodes(gResults);
					//System.out.print("\n => End of intersection: \n");
			        //ConstraintGraph.Graph2Console(gResults);
			        ShowBottomWarning = true;
					//fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					//str = LogBatch + "\n" + "Intersection done!";
	    			//LogBatch+=str;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			System.out.println("Minimize Graphs... after Intersection: "+ pathOnt1 + " and " + pathOnt2);
	    			minimizeGraphBatch("Intersection");
	    			// store to use later without open files ou process normalization and graphs again
	    			Object [] OntAUX = new Object [7];
	    		    /*		obj[0] = ontology;
	    			obj[1] = factory;
	    			obj[2] = manager;
	    		    obj[3] = ontPath;
	    		    obj[4] = ontName;
	    		    obj[5] = graph;
	    		    obj[6] = IRI;
	    		*/
	    			OntAUX[0] = null;
	    			OntAUX[1] = null;
	    			OntAUX[2] = null;
	    			OntAUX[3] = null;
	    			OntAUX[4] = nameOnt1+"I"+nameOnt2;
	    			OntAUX[5] = gResults;
	    			OntAUX[6] = null;
	    			 
	    			ontologiesProcessed.add(OntAUX);  
	    			
	    			//Time stamping
	    			date= new java.util.Date();
	    			stamp = stamp + "Intersection done: \n";
	    			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
	    			System.out.println(stamp);
	    			writer.write(stamp);
	    	        writer.close();
				}catch(Exception ex)
				{
					ex.printStackTrace();
					//str = LogBatch + "\n" + ex.getMessage();
	    			//LogBatch+=str;
	    			gResults = null;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			//}
		//};
		//worker.start();		
	}


	public void runUnionBatch()
	{
		//enableButtons(false);
		//root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		LogBatch+= "\nRunning Union Batch over " + pathOnt1 + " and " + pathOnt2;
		//System.out.println(LogBatch);
		//root.update(root.getGraphics());

		//Thread worker = new Thread() {
			//public void run() {
				String str;
				try{
					//Time Stamp
					String path = System.getProperty("user.home") + "/LastRunProcedureTime.txt";
			    	File logFile = new File(path);
			    	BufferedWriter writer = null;
			    	java.util.Date date;
			    	date = new java.util.Date();
					String stamp;
					stamp = "Running Union: \n";
					stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
					System.out.println(stamp);
					writer = new BufferedWriter(new FileWriter(logFile));
					
					str = Log.getText();
					//1st we pick the vertices that are in both Ontologies
					HashMap<Integer,Node> vertices1 = gOnt1.getVertices();
					HashMap<Integer,Node> vertices2 = gOnt2.getVertices();
					//Clear Results Table
					//DefaultTableModel modelInc = clearResultsTable();
					gResults = new Graph();
					ArrayList<Integer> nodesOnt1 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt2 = new ArrayList<Integer>();
					ArrayList<Integer> nodesOnt3 = new ArrayList<Integer>();
					//1st we add all the nodes from ontology 1
					for(Map.Entry<Integer, Node> temp : vertices1.entrySet())
					{
			        	Node n1 = temp.getValue();
			        	NodeType type = n1.getNodeType();
	        			int tempID = -1;
	        			switch(type)
		            	{
						   case Class :
						       NodeClass nc = (NodeClass)n1;
						       tempID = gResults.insertNodeComp(nc.getExpression(), NodeType.Class);							      
						       break;
						   case Property :
						       NodeProperty np = (NodeProperty)n1;
						       tempID = gResults.insertNodePropertyComp(np.getDescIRI(),np.getDescProp());
						       break;
						   case RestrictionCardinality :
							   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n1;
						       tempID = gResults.insertNodeRestrictionCardinalityComp(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());								       
						       break;
						   case RestrictionComplementOfClass :
							   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n1;
						       tempID = gResults.insertNodeRestrictionComplementOfClassComp(notClass.getExpClass().toString());
						       break;
						   case RestrictionComplementOfProperty :
							   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n1;
						       tempID = gResults.insertNodeRestrictionComplementOfPropertyComp(notProp.getDescProp(), notProp.getDescIRI());
						       break;
						   case RestrictionComplementOfRestrictionCardinality :
							   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n1;
						       tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard.getExpression());
						       break;
						   default: 
							   break;
					    }
	        			if(tempID != -1)
	        			{
	        				nodesOnt1.add(n1.getId());
	        				nodesOnt3.add(tempID);
	        			}	        			
			        }
			        //ConstraintGraph.Graph2Console(gResults);
			        //now we set the adjacency list as an equivalent to the ontology 1
			        ArrayList<LinkedList<Integer>> AdjList1 = gOnt1.getAdj();
			        ArrayList<LinkedList<Integer>> AdjList3 = new ArrayList<LinkedList<Integer>>();
					for(int i = 0; i < nodesOnt1.size(); i++)
					{
						int element1 = nodesOnt1.get(i);
						LinkedList<Integer> tempRO1 = UsefulOWL.getListwithFirstElement(AdjList1, element1);
						LinkedList<Integer> tempRO3 = new LinkedList<Integer>();
						tempRO3.add(nodesOnt3.get(i));
						if(tempRO1 != null)
						{
							for(int j = 1; j < tempRO1.size(); j++)
							{
								element1 = tempRO1.get(j);
								int index = nodesOnt1.indexOf(element1);
								if(index > -1)
								{
									int element3 = nodesOnt3.get(index);
									if(!tempRO3.contains(element3))
									{
										tempRO3.add(element3);
									}
								}
							}
						}
						AdjList3.add(tempRO3);
					}
					
					gResults.setAdj(AdjList3);
					//we now have a Graph that is a copy of Ontology 1 
					//we need to add all nodes from Ontology 2 and its Edges
			        //System.out.print("\n => Original Graph: \n");
			        //ConstraintGraph.Graph2Console(gOnt1);
			        //System.out.print("\n => Nodes included: \n");
			        //ConstraintGraph.Graph2Console(gResults);
			        //now we try to insert all nodes from Ontology 2 marking its equivalents in the lists
			        nodesOnt3 = new ArrayList<Integer>();
			        for(Map.Entry<Integer, Node> temp : vertices2.entrySet())
					{
			        	Node n2 = temp.getValue();
			        	NodeType type = n2.getNodeType();
	        			int tempID = -1;
	        			switch(type)
		            	{
						   case Class :
						       NodeClass nc = (NodeClass)n2;
						       tempID = gResults.getNodeKey(nc.getExpression());
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeComp(nc.getExpression(), NodeType.Class);
						       }
						       break;
						   case Property :
						       NodeProperty np = (NodeProperty)n2;
						       tempID = gResults.getNodeKeyProperty(np.getDescIRI(),np.getDescProp());
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodePropertyComp(np.getDescIRI(),np.getDescProp());
						       }
						       break;
						   case RestrictionCardinality : 
							   NodeRestrictionCardinality nrc = (NodeRestrictionCardinality)n2;
							   tempID = gResults.getNodeKeyRestrictionCardinality(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());
							   if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionCardinalityComp(nrc.getIRIDomain(),nrc.getIRIProp(),nrc.getDescProp(),nrc.getDescRangeOrDomain(),nrc.getCard());
						       }								       
						       break;
						   case RestrictionComplementOfClass :
							   NodeRestrictionComplementOfClass notClass = (NodeRestrictionComplementOfClass)n2;
							   tempID = gResults.getNodeKeyComplementOfClass(notClass.getExpClass().toString());
							   if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionComplementOfClassComp(notClass.getExpClass().toString());
						       }
							   break;
						   case RestrictionComplementOfProperty :
							   NodeRestrictionComplementOfProperty notProp = (NodeRestrictionComplementOfProperty)n2;
						       tempID = gResults.getNodeKeyComplementOfProperty(notProp.getDescIRI(),notProp.getDescProp());
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionComplementOfPropertyComp(notProp.getDescProp(), notProp.getDescIRI());
						       }
						       break;
						   case RestrictionComplementOfRestrictionCardinality :
							   NodeRestrictionComplementOfRestrictionCardinality notRestCard = (NodeRestrictionComplementOfRestrictionCardinality)n2;
						       String desc = notRestCard.getExpression();
						       desc = desc.replaceAll("#", "/");
						       String iriDomain = UsefulOWL.returnIRIDomain(desc);
						       String iriProp = UsefulOWL.returnIRIProp(desc);
						       String descProp = UsefulOWL.returnProp(desc);
						       String descRangeOrDomain = UsefulOWL.returnDomainOrRange(desc);
						       int card = UsefulOWL.returnCard(desc);
						       tempID = gResults.getNodeKeyComplementOfRestrictionCardinality(iriDomain,iriProp,descProp,descRangeOrDomain,card);
						       if(tempID == -1)
						       {
						    	   tempID = gResults.insertNodeRestrictionComplementOfRestrictionCardinalityComp(notRestCard.getExpression());
						       }						   
						       break;
						   default: 
							   break;
					    }
	        			nodesOnt2.add(n2.getId());	
					    nodesOnt3.add(tempID);	    	    
			        }
			        //And we add to the adjacency list of the resulting Ontology all edges from Ontology 2
			        ArrayList<LinkedList<Integer>> AdjList2 = gOnt2.getAdj();
			        AdjList3 = gResults.getAdj();
					for(int i = 0; i < nodesOnt2.size(); i++)
					{
						int element2 = nodesOnt2.get(i);
						int element3 = nodesOnt3.get(i);
						LinkedList<Integer> tempRO2 = UsefulOWL.getListwithFirstElement(AdjList2, element2);
						LinkedList<Integer> tempRO3 = UsefulOWL.getListwithFirstElement(AdjList3, element3);
						if((tempRO2 != null) && (tempRO3 != null))
						{
							for(int j = 1; j < tempRO2.size(); j++)
							{
								element2 = tempRO2.get(j);
								int index = nodesOnt2.indexOf(element2);
								if(index > -1)
								{
									element3 = nodesOnt3.get(index);
									if(!tempRO3.contains(element3))
									{
										tempRO3.add(element3);
									}
								}
							}
						}
					}
			        //now we search for bottom and top nodes
					ConstraintGraph rgd = new ConstraintGraph();
					gResults = ConstraintGraph.addEdgesBetweenRC(gResults);
			        gResults = rgd.searchBottomNodes(gResults);
			        //System.out.print("\n => End of Union: \n");
			        //ConstraintGraph.Graph2Console(gResults);
			        ShowBottomWarning = true;
					//fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					//str = LogBatch + "\n" + "Union done!";
	    			//LogBatch +=str;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			System.out.println("Minimize Graphs... after Union: "+ pathOnt1 + " and " + pathOnt2);
	    			minimizeGraphBatch("Union");
	    			// store to use later without open files ou process normalization and graphs again
	    			Object [] OntAUX = new Object [7];
	    		    /*		obj[0] = ontology;
	    			obj[1] = factory;
	    			obj[2] = manager;
	    		    obj[3] = ontPath;
	    		    obj[4] = ontName;
	    		    obj[5] = graph;
	    		    obj[6] = IRI;
	    		*/
	    			OntAUX[0] = null;
	    			OntAUX[1] = null;
	    			OntAUX[2] = null;
	    			OntAUX[3] = null;
	    			OntAUX[4] = nameOnt1+"U"+nameOnt2;
	    			OntAUX[5] = gResults;
	    			OntAUX[6] = null;
	    			 
	    			ontologiesProcessed.add(OntAUX);  
	    			//Time stamping
	    			date= new java.util.Date();
	    			stamp = stamp + "Union done: \n";
	    			stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
	    			System.out.println(stamp);
	    			writer.write(stamp);
	    	        writer.close();
				}catch(Exception ex)
				{
					LogBatch+= "\n" + ex.getMessage();
					//System.out.println(LogBatch);
	    			gResults = null;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			//}
		//};
		//worker.start();
	}
	
	public int findLoaded (String file){
		for (int i = 0; i<ontologiesProcessed.size(); i++){
			Object[] oAUX = (Object[]) ontologiesProcessed.get(i);
			if (oAUX[4].equals(file))
				return i; 
		}
		return -1;	
	}
	public void runNet2Union(ArrayList<String>network2){
		lastUnionN2 = network2.get(0);
		// process operations
		// network2 creation with Union
		System.out.println("opening..." + path + network2.get(0));
		openBatch1(path, network2.get(0));
		for (int i=1; i<network2.size(); i++){
			int pos = findLoaded(network2.get(i));
			if (pos == -1){
				System.out.println("batch opening..." + path + network2.get(i));
			
				openBatch2(path, network2.get(i)); // second element from network1
			}
			else{
				
				System.out.println("load from memory ..." + path + network2.get(i) + " pos: "+ pos);
				loadFromMemory2(pos);
				
			}
			System.out.println("calling union2 in batch");
			runOntBatch("Union");
			
			System.out.println("Saving union2 in batch: "+ lastUnionN2+ "U"+network2.get(i));
			lastUnionN2 += "U"+network2.get(i);
			
			//saveRBatch(path, lastUnionN2+"U"+network2.get(i)); // save each union
			partialUnionResultsN2.put(lastUnionN2+"U"+network2.get(i), lastUnionN2+"U"+network2.get(i)); // save partial result for later!
			
			pos = findLoaded(lastUnionN2);
			if (pos == -1){
				System.out.println("batch opening..." + path + lastUnionN2);
			
				openBatch1(path, lastUnionN2); // open the result of o1 U o2 as o1 again
			} else {
				System.out.println("load from memory ..." + path + lastUnionN2 + " pos: "+ pos);
				loadFromMemory1(pos);

			}
			
			System.out.println("last Union n2: "+lastUnionN2);
		}

	}
	public void runNet1Union(ArrayList<String>network1){
		lastUnionN1 = network1.get(0);
		// process operations
		// network1 creation with Union
		System.out.println("opening..." + path + network1.get(0));
		openBatch1(path, network1.get(0));
		for (int i=1; i<network1.size(); i++){
			int pos = findLoaded(network1.get(i));
			if (pos == -1){
				System.out.println("batch opening..." + path + network1.get(i));
			
				openBatch2(path, network1.get(i)); // second element from network1
			}
			else{
				
				System.out.println("load from memory ..." + path + network1.get(i) + " pos: "+ pos);
				loadFromMemory2(pos);
				
			}
			System.out.println("calling union1 in batch");
			runOntBatch("Union");
			
			System.out.println("Saving union1 in batch: "+ lastUnionN1+ "U"+network1.get(i));
			lastUnionN1 += "U"+network1.get(i);
			
			//saveRBatch(path, lastUnionN1+"U"+network1.get(i)); // save each union
			partialUnionResultsN1.put(lastUnionN1+"U"+network1.get(i), lastUnionN1+"U"+network1.get(i)); // save partial result for later!
			
			pos = findLoaded(lastUnionN1);
			if (pos == -1){
				System.out.println("batch opening..." + path + lastUnionN1);
			
				openBatch1(path, lastUnionN1); // open the result of o1 U o2 as o1 again
			} else {
				System.out.println("load from memory ..." + path + lastUnionN1 + " pos: "+ pos);
				loadFromMemory1(pos);

			}
			
			System.out.println("last Union n1: "+lastUnionN1);
		}

	}
	
	public void runNetsIntersections(ArrayList<String> network1,ArrayList<String> network2){
		// intersection 1x2
		int totalIntersections = network1.size()*network2.size();
		System.out.println("starting batch intersection ="+totalIntersections);
		String lastIntersection = "";
		// computes intersection over N1 and N2
		// load both i and j ontologies from net1 and net2 

		for (int i=0; i<network1.size(); i++){
			
			int pos = findLoaded(network1.get(i));
			if (pos == -1){
				System.out.println("batch opening..." + path + network1.get(i));
				
				openBatch1(path, network1.get(i));
			}
			else {
				System.out.println("load from memory ..." + path + network1.get(i) + " pos: "+ pos);
				loadFromMemory1(pos);

			}
			for (int j=0; j<network2.size(); j++){
				pos = findLoaded(network2.get(j));
				if (pos == -1){
					System.out.println("batch opening..." + path + network2.get(j));
					
					openBatch2(path, network2.get(j));
				} else {
					System.out.println("load from memory ..." + path + network2.get(j) + " pos: "+ pos);
					loadFromMemory2(pos);

				}
				
				System.out.println("calling intersection in batch..." );
				runOntBatch("Intersection");
				
				System.out.println("Saving intersection in batch"+ network1.get(i)+"I"+network2.get(j));
				//saveRBatch(path, network1.get(i)+"I"+network2.get(j)); // save each intersection
				
				partialIntersectionResultsN1N2Names.put(network1.get(i)+"I"+network2.get(j), network1.get(i)+"I"+network2.get(j)); // save partial result for later!
				partialIntersectionResultsN1N2.add(network1.get(i)+"I"+network2.get(j));
				lastIntersection = network1.get(i)+"I"+network2.get(j);
				System.out.println("last Intersection batch: "+network1.get(i)+"I"+network2.get(j));

			}
		}

	}
	
	public void runNet1Difference(ArrayList <String> network1){
		// difference N1
		lastDifferenceN1 = "";
		// computes differences from network1 and intersections
		int pos = findLoaded(lastUnionN1);
		if (pos == -1){
			System.out.println("Opening union1 in batch"+ lastUnionN1);
			
			openBatch1(path, lastUnionN1);
		}else {
			System.out.println("load from memory ..." + lastUnionN1 + " pos: "+ pos);
			loadFromMemory1(pos);

		}
		for (int i=0; i<partialIntersectionResultsN1N2.size(); i++){
			pos = findLoaded(partialIntersectionResultsN1N2.get(i));

			if (pos == -1){
				System.out.println("Opening intersection in batch: "+ i + partialIntersectionResultsN1N2.get(i));

				openBatch2(path, partialIntersectionResultsN1N2.get(i));
			} else {
				System.out.println("load from memory ..." + partialIntersectionResultsN1N2.get(i) + " pos: "+ pos);
				loadFromMemory2(pos);
			}
			System.out.println("Running dif in batch: ");

			runOntBatch("Difference");
			if (i==0){
				//System.out.println("saving dif in batch: "+lastUnionN1+"D"+partialIntersectionResultsN1N2.get(i));
				//saveRBatch(path, lastUnionN1+"D"+partialIntersectionResultsN1N2.get(i));
				partialDifferenceResultsN1.put(lastUnionN1+"D"+partialIntersectionResultsN1N2.get(i), lastUnionN1+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
				lastDifferenceN1 = lastUnionN1+"D"+partialIntersectionResultsN1N2.get(i);

			} else {
				//System.out.println("saving dif in batch: "+lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i));
				//saveRBatch(path, lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i));
				partialDifferenceResultsN1.put(lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i), partialIntersectionResultsN1N2.get(i-1)+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
				lastDifferenceN1 = lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i);

			}
			
			pos = findLoaded(lastDifferenceN1);
			if (pos == -1){
				System.out.println("Opening intersection in batch: "+ i + lastDifferenceN1);

				openBatch1(path, lastDifferenceN1); // open the result of o1 D o2 as o1 again
			}
			else {
				System.out.println("load from memory ..." + lastDifferenceN1 + " pos: "+ pos);
				loadFromMemory1(pos);

			}
		}
		System.out.println("last Difference n1: "+lastDifferenceN1);
		System.out.println("saving dif in batch: "+lastDifferenceN1+ " as net1");
		saveRBatch(path, "net1");
	}
	
	public void runNet2Difference(ArrayList<String> network2){
		// difference N2
		
		// computes differences from network2 and intersections
		int pos = findLoaded(lastUnionN2);
		if (pos == -1){
			System.out.println("Opening union2 in batch"+ lastUnionN2);
			
			openBatch1(path, lastUnionN2);
		} else {
			System.out.println("load from memory ..." + lastUnionN2 + " pos: "+ pos);
			loadFromMemory1(pos);
		}
		for (int i=0; i<partialIntersectionResultsN1N2.size(); i++){
			pos = findLoaded(partialIntersectionResultsN1N2.get(i));

			if (pos == -1){
				System.out.println("Opening intersection in batch: "+ i + partialIntersectionResultsN1N2.get(i));

				openBatch2(path, partialIntersectionResultsN1N2.get(i));
			} else {
				System.out.println("load from memory ..." + partialIntersectionResultsN1N2.get(i) + " pos: "+ pos);
				loadFromMemory2(pos);

			}
			System.out.println("Running dif in batch: ");

			runOntBatch("Difference");
			if (i==0){
				System.out.println("saving dif in batch: "+lastUnionN2+"D"+partialIntersectionResultsN1N2.get(i));
				//saveRBatch(path, lastUnionN2+"D"+partialIntersectionResultsN1N2.get(i));
				partialDifferenceResultsN2.put(lastUnionN2+"D"+partialIntersectionResultsN1N2.get(i), lastUnionN2+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
				lastDifferenceN2 = lastUnionN2+"D"+partialIntersectionResultsN1N2.get(i);

			} else {
				System.out.println("saving dif in batch: "+lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i));
				//saveRBatch(path, lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i));
				partialDifferenceResultsN2.put(lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i), partialIntersectionResultsN1N2.get(i-1)+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
				lastDifferenceN2 = lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i);

			}
			pos = findLoaded(lastDifferenceN2);
			if (pos == -1){
				System.out.println("Opening intersection in batch: "+ i + lastDifferenceN2);

				openBatch1(path, lastDifferenceN2); // open the result of o1 D o2 as o1 again
			} else {
				System.out.println("load from memory ..." + lastDifferenceN2 + " pos: "+ pos);
				loadFromMemory1(pos);

			}
		}
		System.out.println("last Difference n2: "+lastDifferenceN2);
		System.out.println("saving dif in batch: "+lastDifferenceN2+ " as net2");
		saveRBatch(path, "net2");
					
	}
	/**
	 * Main function that sets the project for Protege
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//edu.stanford.smi.protege.Application.main(args);
		OntologyManagerTab omt = new OntologyManagerTab();
		if (args == null){
			omt.initialize();
		} else {
			omt.batch(args);
		}

	}

	/**
	 *  function that run in batch loading ontologies from args
	 * 
	 * @param args
	 * args[0] = path
	 * args[1] = number of ontologies in net1
	 * args[2] = number of ontologies in net2
	 * args[3..n] = name of files containing ontologies from net 1 and 2
	 * 
	 * for instance: /Users/fabiomarcosdeabreusantos/Documents/Ontologias/ 2 cmt.owl dblp.owl cmt.owl foaf.owl
	 */
	private void batch(String[] args) {
		// TODO Auto-generated method stub
		// carrega OS da máquina
		getOSType();
		
		Log = new JTextArea(); // to keep compatibility with the log 
		
		this.writeTimeStamp("starting internetwork ontology matching");
		
		path = args[0];
		System.out.println("path: "+ path);
		
		n1 = Integer.parseInt(args[1]);
		n2 = Integer.parseInt(args[2]);
		n3 = Integer.parseInt(args[3]);
		System.out.println("net1 size= " + n1);
		System.out.println("net2 size= " + n2);
		System.out.println("intranetwork alignments size=" +n3 );

		/*if (n3%2!=0) {
			System.out.println(" N3 must not be and odd number. Intranetwork alignments must be informed in pairs. Format <network number, file name>");
			System.exit(0);
		}
		if (n3>200) {
			System.out.println(" N3 must not be greater than 200. Intranetwork alignments must be informed in pairs. Format <network number, file name>");
			System.exit(0);
		}*/
		
		// reading both networks
		for (int i=4; i < 4+n1+n2; i++){
			
			if (i < n1+4){ // < n: to network 1
				network1.add(args[i]);
				System.out.println("net 1: "+ args[i]);
			}
			if (i >= n1+4){ // >= n: to network 2
				network2.add(args[i]);
				System.out.println("net 2: "+ args[i]);
			}
		}
		// reading intranetwork alignments
		/*int count = 0;
		int countIntraN1 = 0;
		int countIntraN2 = 0;
		int j=4+n1+n2;
		while  (j < args.length) {
			intranetworkAlignments[count][0] = args[j];
			j++;
			if (args[j].equals("1")) 
				countIntraN1 ++;
			else 
				if (args[j].equals("2")) 
					countIntraN2 ++;
				else
					System.out.println(" only implemented to 2 networks");
					System.exit(0);

			intranetworkAlignments[count][1] = args[j];
			System.out.println("- Network: " +args[j-1] + " - Intranetwork Alignment file: " + args[j] );
			j++;
			count++;
		}
		*/
		
		// reading intranetwork alignments
		for (int i = 4+n1+n2; i < 4+n1+n2+n3; i++) {
			intranetworkAlignmentsFileNames.add(args[i]);
		}
	
		runNet1Union(network1);
		runNet2Union(network2);
		
		runNet1IntraAlignments();
		runNet2IntraAlignments();
		
		writeTimeStamp("finishing 2 unions ...");
		writeTimeStamp("starting intersections ...");
		
		runNetsIntersections(network1,network2);

		writeTimeStamp("finishing intersections ...");
		writeTimeStamp("starting 1st difference ...");
		
		runNet1Difference(network1);
		runNet2Difference(network2);
		
		writeTimeStamp("finishing 1st difference ...");
		writeTimeStamp("starting 2nd difference ...");

		writeTimeStamp("finishing 2nd difference ...");
		

		writeTimeStamp("Finishing 2 networks  ...");

		// finally call alin to match resulting results lastDifferenceN1 and lastDifferenceN2
		System.out.println(" Calling alin to match "+ lastDifferenceN1 + "x " + lastDifferenceN2);

		System.out.println("--Log JTextArea ---");
		System.out.println(Log.getText());
		System.out.println("--LogBatch ---");
		//System.out.println(LogBatch);
		
		try{ 
			File f = new File(path+"log.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		    writer.write (LogBatch);
	
		    //Close writer
		    writer.close();
		} catch(Exception e) {
		    e.printStackTrace();
		}
	}


	private void runNet1IntraAlignments() {
		// TODO Auto-generated method stub
		// computes differences from network1 and intra alignments
		int pos = findLoaded(lastUnionN1);
		if (pos == -1){
			System.out.println("Opening union1 in batch"+ lastUnionN1);
			
			openBatch1(path, lastUnionN1);
		}else {
			System.out.println("load from memory ..." + lastUnionN1 + " pos: "+ pos);
			loadFromMemory1(pos);
		}
		openBatch2(path, intranetworkAlignmentsFileNames.get(0));
		System.out.println("Running dif with intranet alignments in batch: ");

		runOntBatch("Difference");
		
		System.out.println("Saving union1 in batch: "+ lastUnionN1+ "U"+intranetworkAlignmentsFileNames.get(0));
		lastUnionN1 += "D"+intranetworkAlignmentsFileNames.get(0);
		
		//saveRBatch(path, lastUnionN1+"U"intranetworkAlignmentsFileNames.get(0)); // save each union
		partialUnionResultsN1.put(lastUnionN1+"D"+intranetworkAlignmentsFileNames.get(0), lastUnionN1+"D"+intranetworkAlignmentsFileNames.get(0) ); // save Union after apply intra alignments!
		
	}
	private void runNet2IntraAlignments() {

			// computes differences from network2 and intra alignments
			int pos = findLoaded(lastUnionN2);
			if (pos == -1){
				System.out.println("Opening union2 in batch"+ lastUnionN2);
				
				openBatch1(path, lastUnionN2);
			}else {
				System.out.println("load from memory ..." + lastUnionN2 + " pos: "+ pos);
				loadFromMemory1(pos);
			}
			openBatch2(path, intranetworkAlignmentsFileNames.get(1));
			System.out.println("Running dif with intranet alignments in batch: ");

			runOntBatch("Difference");
			
			System.out.println("Saving union2 in batch: "+ lastUnionN1+ "U"+intranetworkAlignmentsFileNames.get(1));
			lastUnionN2 += "D"+intranetworkAlignmentsFileNames.get(1);
			
			//saveRBatch(path, lastUnionN1+"U"intranetworkAlignmentsFileNames.get(0)); // save each union
			partialUnionResultsN2.put(lastUnionN2+"D"+intranetworkAlignmentsFileNames.get(1), lastUnionN2+"D"+intranetworkAlignmentsFileNames.get(1) ); // save Union after apply intra alignments!
	}
	/*private void runIntraAlignmentsUnion(int intraN1, int intraN2) {
		// TODO Auto-generated method stub
		/*System.out.println("opening..." + path + "ekaw-sigkdd-w-uri.owl");
		openBatch1(path, "cmt.owl");
		System.out.println("opening..." + path + "sigkdd.owl");
		openBatch2(path, "sigkdd.owl");
		System.out.println("calling intersection in batch..." );
		runOntBatch("Intersection");
		System.out.println("finishing intersection in batch..." );
		saveRBatch(path,"ekaw-sigkdd.owlIsigkdd.owl");
		System.exit(0);*/
	/*
		if (intraN1 > 1) {
			for (int i =0; i<intranetworkAlignments.length ;i++) {
				
				
			}
		}
		if (intraN2 > 1) {
			
		}
	}
	*/
	
	private void loadFromMemory1(int pos) {
		// TODO Auto-generated method stub
		/*		obj[0] = ontology;
		obj[1] = factory;
		obj[2] = manager;
	    obj[3] = ontPath;
	    obj[4] = ontName;
	    obj[5] = graph;
	    obj[6] = IRI;
*/
		Object oAUX[]  = (Object []) ontologiesProcessed.get(pos);
		Ont1[0] = oAUX[0];
		Ont1[1] = oAUX[1];
		Ont1[2] = oAUX[2];
		Ont1[3] = oAUX[3];
		String fileAUX = (String) oAUX[4];
		gOnt1 = (Graph) oAUX[5];
		//pathOnt1 = (String) oAUX[3];
		String pathNorm = (String) oAUX[3];
		System.out.println("path normalizado:" +pathNorm);
		pathOnt1 = path+fileAUX;
		System.out.println("path completo:" +pathOnt1);
		nameOnt1 = fileAUX;
		System.out.println("file:" +nameOnt1);
	}

	private void loadFromMemory2(int pos) {
		// TODO Auto-generated method stub
		/*		obj[0] = ontology;
		obj[1] = factory;
		obj[2] = manager;
	    obj[3] = ontPath;
	    obj[4] = ontName;
	    obj[5] = graph;
	    obj[6] = IRI;
*/
		Object oAUX[]  = (Object []) ontologiesProcessed.get(pos);
		Ont2[0] = oAUX[0];
		Ont2[1] = oAUX[1];
		Ont2[2] = oAUX[2];
		Ont2[3] = oAUX[3];
		String fileAUX = (String) oAUX[4];
		gOnt2 = (Graph) oAUX[5];
		//pathOnt2 = (String) oAUX[3];
		String pathNorm = (String) oAUX[3];
		System.out.println("path normalizado:" +pathNorm);
		pathOnt2 = path+fileAUX;
		System.out.println("path completo:" +pathOnt2);
		nameOnt2 = fileAUX;
		System.out.println("file:" +nameOnt2);
	}

	private void writeTimeStamp(String message){
		java.util.Date date;
    	date = new java.util.Date();
		String stamp;
		stamp = message +" \n";
		stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
		System.out.println(stamp);
	}
	
}