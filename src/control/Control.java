package control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import Application.OSType;
import Ontology.BreadthFirstSearch;
import Ontology.ConstraintGraph;
import Ontology.Graph;
import Ontology.Node;
import Ontology.NodeClass;
import Ontology.NodeProperty;
import Ontology.NodeRestrictionCardinality;
import Ontology.NodeRestrictionComplementOfClass;
import Ontology.NodeRestrictionComplementOfProperty;
import Ontology.NodeRestrictionComplementOfRestrictionCardinality;
import Ontology.NodeType;
import Ontology.Normalization;
import Ontology.SaveOntology;
import Ontology.UsefulOWL;

import java.util.Date;

public class Control {
	
    private String Log;
	boolean ProjOp = false;
	boolean HideIRI1 = false;
	boolean HideIRI2 = false;
	boolean HideIRIResults = false;
	boolean CleanUp = false;
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
	private String debug;
	private String output;
	private String messages;
	private Date date;
	private Timestamp now;
    
	/**
	 * Basic GUI Design plus initializing Class variables 
	 */
	public void initialize() 
	{
		getOSType();
        //this.setLabel("Ontology Manager Tab"); //Tab name
        
		pathOnt1 = "";		
		pathOnt2 = "";
		nameOnt1 = "";
		nameOnt2 = "";
		pathOntResults = "";
		nameOntResults = "";
		gOnt1 = null;
		gOnt2 = null;
		gResults = null;
		Log = new String();
		HideIRI1=false;
		HideIRI2=false;
		HideIRIResults = false;
		CleanUp = false;
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
	public void batch(String[] args) {
		// TODO Auto-generated method stub
		// carrega OS da máquina
		getOSType();
		
		Log = new String(); // to keep compatibility with the log 
		date = new Date(); 
		now = new Timestamp(date.getTime());
		
		this.writeTimeStamp("starting internetwork ontology matching");
		
		path = args[0];
		System.out.println("path: "+ path);
		
		n1 = Integer.parseInt(args[1]);
		n2 = Integer.parseInt(args[2]);
		n3 = Integer.parseInt(args[3]);
		System.out.println("net1 size= " + n1);
		System.out.println("net2 size= " + n2);
		System.out.println("intranetwork alignments size=" +n3 );
		Log+="net1 size= " + n1 +"\n";
		Log+="net2 size= " + n2 +"\n";
		Log+="intranetwork alignments size=" +n3+"\n";

		output = args[4+n1+n2+n3];
		debug = args[4+n1+n2+n3+1];
		messages = args[4+n1+n2+n3+2];
		System.out.println("output dir= " + output);
		System.out.println("debug= " + debug);
		System.out.println("messages= " + messages);
		Log+="output dir= " + output+"\n";
		Log+="debug= " + debug+"\n";
		Log+="messages= " + messages+"\n";


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
				Log+="net 1: "+ args[i]+"\n";
			}
			if (i >= n1+4){ // >= n: to network 2
				network2.add(args[i]);
				System.out.println("net 2: "+ args[i]);
				Log+="net 2: "+ args[i]+"\n";
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
			System.out.println("Intra alignments:"+args[i]);
			Log+="Intra alignments:"+ args[i]+"\n";
		}
		
		LogBatch += Log;
		
		writeTimeStamp("Starting 1o union ...");
		
		runNet1Union(network1);
		
		writeTimeStamp("Starting 2o union ...");
		
		runNet2Union(network2);
		
		if (n3>0) {
			runNet1IntraAlignmentsV2();
			runNet2IntraAlignmentsV2();
		}
		
		writeTimeStamp("finishing 2 unions ...");
		writeTimeStamp("starting intersections ...");
		
		runNetsIntersections(network1,network2);

		writeTimeStamp("finishing intersections ...");
		writeTimeStamp("starting 1st difference ...");
		
		runNet1Difference(network1);
		
		writeTimeStamp("starting 2nd difference ...");
		
		runNet2Difference(network2);

		writeTimeStamp("finishing 2nd difference ...");
		

		writeTimeStamp("Finishing 2 networks  ...");

		// finally call alin to match resulting results lastDifferenceN1 and lastDifferenceN2
		System.out.println(" Calling alin to match "+ lastDifferenceN1 + "x " + lastDifferenceN2);

		System.out.println("--Log  ---");
		System.out.println(Log);
		//System.out.println("--LogBatch ---");
		//System.out.println(LogBatch);
		
		try{ 
			File f = new File(output+"log"+n1+n2+n3+args[4]+args[5]+".txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		    writer.write (Log);
	
		    //Close writer
		    writer.close();
		    f = new File(output+"logTime"+n1+n2+n3+args[4]+args[5]+".txt");
			writer = new BufferedWriter(new FileWriter(f));
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
		System.out.println("Running dif with intranet alignments in batch: "+ lastUnionN1 + " - " + intranetworkAlignmentsFileNames.get(0));

		runOntBatch("Difference");
		
		System.out.println("Saving net1 after dif w/ intra alignments in batch: "+ lastUnionN1+ "D"+intranetworkAlignmentsFileNames.get(0));
		lastUnionN1 += "D"+intranetworkAlignmentsFileNames.get(0);
		
		saveRBatch(path, lastUnionN1); // save each union
		partialUnionResultsN1.put(lastUnionN1+"D"+intranetworkAlignmentsFileNames.get(0), lastUnionN1+"D"+intranetworkAlignmentsFileNames.get(0) ); // save Union after apply intra alignments!
		
	}
	
	private void runNet1IntraAlignmentsV2() { // changed the order: intraalignments - lastUnionN1
		// TODO Auto-generated method stub
		// computes differences from network1 and intra alignments
		int pos = findLoaded(lastUnionN1);
		if (pos == -1){
			System.out.println("Opening union1 in batch"+ lastUnionN1);
			
			openBatch2(path, lastUnionN1); // open 2 first!!!!
		}else {
			System.out.println("load from memory ..." + lastUnionN1 + " pos: "+ pos);
			loadFromMemory1(pos);
		}
		openBatch1(path, intranetworkAlignmentsFileNames.get(0)); // open 1 after!!!!
		System.out.println("Running dif with intranet alignments in batch: "+ lastUnionN1 + " - " + intranetworkAlignmentsFileNames.get(0));

		runOntBatch("Difference");
		
		System.out.println("Saving net1 after dif w/ intra alignments in batch: "+ lastUnionN1+ "D"+intranetworkAlignmentsFileNames.get(0));
		lastUnionN1 += "D"+intranetworkAlignmentsFileNames.get(0);
		
		saveRBatch(path, lastUnionN1); // save each union
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
		System.out.println("Running dif with intranet alignments in batch: "+ lastUnionN2 + " - "+ intranetworkAlignmentsFileNames.get(1));

		runOntBatch("Difference");
		
		System.out.println("Saving net2 after dif w/ intra alignments in batch: "+ lastUnionN2+ "D"+intranetworkAlignmentsFileNames.get(1));
		lastUnionN2 += "D"+intranetworkAlignmentsFileNames.get(1);
		
		saveRBatch(path, lastUnionN2); // save each union
		partialUnionResultsN2.put(lastUnionN2+"D"+intranetworkAlignmentsFileNames.get(1), lastUnionN2+"D"+intranetworkAlignmentsFileNames.get(1) ); // save Union after apply intra alignments!
	}
	private void runNet2IntraAlignmentsV2() { // changed the order: intraalignments - lastUnionN2

		// computes differences from network2 and intra alignments
		int pos = findLoaded(lastUnionN2);
		if (pos == -1){
			System.out.println("Opening union2 in batch"+ lastUnionN2);
			
			openBatch2(path, lastUnionN2); // open 2 frist!!!
		}else {
			System.out.println("load from memory ..." + lastUnionN2 + " pos: "+ pos);
			loadFromMemory1(pos);
		}
		openBatch1(path, intranetworkAlignmentsFileNames.get(1)); // open 1 after!!!
		System.out.println("Running dif with intranet alignments in batch: "+ lastUnionN2 + " - "+ intranetworkAlignmentsFileNames.get(1));

		runOntBatch("Difference");
		
		System.out.println("Saving net2 after dif w/ intra alignments in batch: "+ lastUnionN2+ "D"+intranetworkAlignmentsFileNames.get(1));
		lastUnionN2 += "D"+intranetworkAlignmentsFileNames.get(1);
		
		saveRBatch(path, lastUnionN2); // save each union
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
			
			lastUnionN2 += "U"+network2.get(i);
			System.out.println("Saving union2 in batch: "+ lastUnionN2);

			
			saveRBatch(path, lastUnionN2); // save each union
			partialUnionResultsN2.put(lastUnionN2, lastUnionN2); // save partial result for later!
			
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
			
			lastUnionN1 += "U"+network1.get(i);
			System.out.println("Saving union1 in batch: "+ lastUnionN1);
			
			
			saveRBatch(path, lastUnionN1); // save each union
			partialUnionResultsN1.put(lastUnionN1, lastUnionN1); // save partial result for later!
			
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
				saveRBatch(path, network1.get(i)+"I"+network2.get(j)); // save each intersection
				
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
		saveRBatch(path, "net1-"+n1+n2+n3);
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
		saveRBatch(path, "net2-"+n1+n2+n3);
					
	}
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
		Log+=("opening from memory file:" +nameOnt1+"\n");
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
		Log+=("opening from memory file:" +nameOnt2+"\n");
	}

	private void writeTimeStamp(String message){
		
    	date = new Date();
		String stamp, timeElapsed;
		Timestamp old = now;
		now = new Timestamp(date.getTime());
		long elapsedTimeMillis = now.getTime() - old.getTime();
		//long hours = (elapsedTimeMillis / (1000 * 60 * 60)) % 24;
		//long seconds = (elapsedTimeMillis / (1000));
		timeElapsed = message + " Time Elapsed : " + elapsedTimeMillis;
		stamp = message +" \n";
		stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
		System.out.println(stamp);
		Log += stamp + "\n";
		LogBatch += stamp + "\n"; //log with only essencial stuff
		System.out.println(timeElapsed);
		Log += timeElapsed + "\n";
		LogBatch += timeElapsed + "\n"; //log with only essencial stuff

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
			Log += "\nLoading Batch: " + path + file;
			//System.out.println(Log);
			//root.update(root.getGraphics());
			
	//		Thread worker = new Thread() {
		//		public void run() {
			//		String str;
					try{
						str = Log;
    					Normalization norm = new Normalization();
    					Object[] o = norm.runOntologyNormalization(pathOnt2, nameOnt2, osType);
    	    			String pathNorm = o[0].toString();
    	    			//exception inside normalization...
		    			if(pathNorm.substring(0, 5).equals("Error"))
		    			{
		    				str = str + "\n" + pathNorm;
		    				Log += str;
		    				//enableButtons(true);
    		    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    				return;
		    			}
		    			IRIOnt2 = o[1].toString();
		    			System.out.print("\n Original IRI Ontology 2: " + IRIOnt2 + "\n");
		    			Ont2 = loadOntBatch(pathNorm);
		    			ConstraintGraph r = new ConstraintGraph();
		    			gOnt2 = new Graph();
		    			gOnt2 = r.createGraph(Ont2, Log, debug);
		    			
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
		    			//str = Log;
		    			//str = str + "\n" + "Ontology successfully loaded as Ontology 2 batch";
		    			//Log += str;
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
						Log+= "\nError openBatch2 : " + ex.getMessage();
						//System.out.println(Log);
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
			Log+= "\n Error openBatch2:" + ex.getMessage();
			//System.out.println(Log);
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
			Log += "\nLoading Batch: " + path + file;
			//System.out.println(Log);
			//root.update(root.getGraphics());
			
			//Thread worker = new Thread() {
				//public void run() {
					//String str;
					try{
						str = Log;
    					Normalization norm = new Normalization();
    					Object[] o = norm.runOntologyNormalization(pathOnt1, nameOnt1, osType);
    	    			String pathNorm = o[0].toString();
    	    			//exception inside normalization...
		    			if(pathNorm.substring(0, 5).equals("Error"))
		    			{
		    				str = str + "\n" + pathNorm;
		    				Log+=str;
		    				//enableButtons(true);
    		    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    				return;
		    			}
		    			IRIOnt1 = o[1].toString();
		    			System.out.print("\n Original IRI Ontology 1: " + IRIOnt1 + "\n");
		    			Ont1 = loadOntBatch(pathNorm);
		    			ConstraintGraph r = new ConstraintGraph();
		    			gOnt1 = new Graph();
		    			gOnt1 = r.createGraph(Ont1, Log, debug);
		    			
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
		    			//str = Log;
		    			//str = str + "\n" + "Ontology successfully loaded as Ontology 1 batch";
		    			//Log+=str;
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
						Log+= "\nError openBatch1: " + ex.getMessage();
		    			//System.out.println(Log);
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
			Log+= "\n Error openBatch1" + ex.getMessage();
			//System.out.println(Log);
			//enableButtons(true);
			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
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
    					Log += "\nPlease load Second Antology";
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
    			//Log+= "\nPlease load an Antology first";
    			//System.out.println(Log);
    			System.out.println("\nPlease load an Antology first");
    			return;
    		}
			
    	}catch(Exception ex)
    	{
    		Log+= "\n Error runOntBatch" + ex.getMessage();
    		//System.out.println(Log);
			//enableButtons(true);
			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    	}

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
    			//Log+= "\nSaving Ontology as: " + pathOntResults + "\n";
    			//System.out.println(Log);
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
    						Log += "\n erro saveRbatch:" + ex.getMessage();
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
    			//Log+= "\n saveRbatch: " + ex.getMessage();
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
	/**
	 * Using the OWL API initializes the manager, ontology and datafactory from the file and returns the object containing it
	 * 
	 * @param ontPath
	 * @return Object containing manager, ontology, datafactory and original OWL file path 
	 */
	public Object[] loadOntBatch(String ontPath) 
	{
		String str = Log;
		Object[] obj = new Object[4];
		try {	                
			
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
				Log += str;
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
		//String str = Log + "\nRunning Difference over " + pathOnt1 + " and " + pathOnt2;
		//Log+=str;
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
			        if (debug.equals("Y")) {

				        System.out.print("\n => End of Difference: \n");
				        ConstraintGraph.Graph2Console(gResults);
			        }
			        
					//fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					//str = Log + "\n" + "Difference Done!";
	    			//Log+=str;
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
					//str = Log + "\n" + ex.getMessage();
	    			//Log+=str;
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
		Log += "\nRunning Intersection over " + pathOnt1 + " and " + pathOnt2;
		
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
			        if (debug.equals("Y")) {
				        System.out.print("\n => Nodes included: \n");
				        ConstraintGraph.Graph2Console(gResults);
			        }
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
			        if (debug.equals("Y")) {

			        	System.out.print("\n => End of intersection: \n");
			        	ConstraintGraph.Graph2Console(gResults);
			        }
			        
					//fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					//str = Log + "\n" + "Intersection done!";
	    			//Log+=str;
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
					//str = Log + "\n" + ex.getMessage();
	    			//Log+=str;
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
		Log+= "\nRunning Union Batch over " + pathOnt1 + " and " + pathOnt2;
		//System.out.println(Log);
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
					
					str = Log;
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
			        if (debug.equals("Y")) {

			        	ConstraintGraph.Graph2Console(gResults);
			        }
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
			        if (debug.equals("Y")) {

				        System.out.print("\n => Original Graph: \n");
				        ConstraintGraph.Graph2Console(gOnt1);
				        System.out.print("\n => Nodes included: \n");
				        ConstraintGraph.Graph2Console(gResults);
			        }
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
			        if (debug.equals("Y")) {

				        System.out.print("\n => End of Union: \n");
				        ConstraintGraph.Graph2Console(gResults);
			        }
			        
					//fillJTableWithIRI(gResults,modelInc);
					HideIRIResults = false;
					CleanUp = false;
					//str = Log + "\n" + "Union done!";
	    			//Log +=str;
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
					Log+= "\n" + ex.getMessage();
					//System.out.println(Log);
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
	 * Implements Show and Hide action for the Elements IRI in Ontology 2 
	 *
	 * 
	 *
	 */
	public void minimizeGraphBatch (String operation){
		//public void actionPerformed(ActionEvent e) 
	    //{
			if(gResults != null)
			{
				//root = SwingUtilities.getRoot((JButton) e.getSource());
				String str = Log;
				try{
					//enableButtons(false);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			CleanUp = true;
	    			
	    			//Thread worker = new Thread() {
	    				//@SuppressWarnings({ "rawtypes", "unchecked" })
						//public void run() {
	    					//Log += "\nMinimizing Resulting Graph";
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
	    				        if (debug.equals("Y")) {
	    				        	System.out.println("Add edges");
	    				        	ConstraintGraph.Graph2Console(gResults);
	    				        }
	    				        
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
	    		    			//Log+=str;
	    				        
	    					}catch(Exception ex)
	    					{
	    						System.out.println(ex.getStackTrace());
	    						//str = Log + "\n" + ex.getMessage();
	    		    			//Log+=str;
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
					//str = Log + "\nError : " + ex.getMessage();
	    			//Log +=str;
	    			//enableButtons(true);
	    			//root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    			return;
				}
			}
	    //}
	}

}
