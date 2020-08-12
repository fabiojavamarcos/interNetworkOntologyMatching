package control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import Application.OSType;
import Ontology.ConstraintGraph;
import Ontology.Graph;
import Ontology.Normalization;
import Ontology.SaveOntology;
import command.Command;
import command.Command2;
import command.Difference;
import command.DifferenceBatch;
import command.Intersection;
import command.IntersectionBatch;
import command.MinimizeGraph;
import command.MinimizeGraphBatch;
import command.OpenBatch1;
import command.OpenBatch2;
import command.SaveRBatch;
import command.Union;
import command.UnionBatch;

public class Control {
	
    protected String Log;
	protected boolean ProjOp = false;
	protected boolean HideIRI1 = false;
	protected boolean HideIRI2 = false;
	protected boolean HideIRIResults = false;
	protected boolean CleanUp = false;
    protected String pathOnt1; 			//Ontology1 Path
    protected String nameOnt1; 			//Ontology1 Name
    protected Object[] Ont1; 				//Object used to store Ontology 1
    
    /*		obj[0] = ontology;
			obj[1] = factory;
			obj[2] = manager;
            obj[3] = ontPath;
  
            */
    protected Graph gOnt1; 				//Graph for Ontology 1
    protected String IRIOnt1;				//IRI for the original Ontology 1
    protected String pathOnt2; 			//Ontology2 Path
    protected String nameOnt2; 			//Ontology2 Name
    protected Object[] Ont2;				//Object used to store Ontology 2
    protected Graph gOnt2; 				//Graph for Ontology 2
    protected String IRIOnt2;				//IRI for the original Ontology 2
    protected Graph gResults; 			//Graph for resulting Ontology
    protected String pathOntResults; 		//Resulting Ontology save path
    protected String nameOntResults;		//Resulting Ontology save name
    protected boolean ShowBottomWarning;

    protected ArrayList <String> network1 = new ArrayList(); // network 1 to match
    protected ArrayList <String> network2 = new ArrayList(); // network 2 to match
    //protected Object intranetworkAlignments [][]= new Object [100][100]; // RDF files form intranetwork alignments 1st dimension file name 2nd dimension number of the network they will be used 
    protected ArrayList <String> intranetworkAlignmentsFileNames = new ArrayList();
    protected String intranetworkFileNet1, intranetworkFileNet2;
    protected String path; // path to OWL files
    protected int n1; // n ontologies to compose network 1
    protected int n2; // n ontologies to compose network 2
    protected int n3; // n intranetwork alignments from both network 1 and 2
    protected String LogBatch = " ";
    protected ArrayList ontologiesProcessed = new ArrayList(); // ontologies processed including their graphs 
    
 // salva trabalhos temporários
 	protected	Map <String,String> partialUnionResultsN1 = new HashMap<String,String>();
 	protected	Map <String,String> partialUnionResultsN2 = new HashMap<String,String>();
 	protected	Map <String,String> partialIntersectionResultsN1N2Names = new HashMap<String,String>();
 		
 	protected	List <String> partialIntersectionResultsN1N2 = new ArrayList<String>();
 	protected	Map <String,String> partialDifferenceResultsN1 = new HashMap<String,String>();
 	protected	Map <String,String> partialDifferenceResultsN2 = new HashMap<String,String>();
 	
 	protected String lastUnionN1;
    protected String lastUnionN2;
    protected String lastDifferenceN1;
    protected String lastDifferenceN2;
    
	public OSType osType;
	protected String debug;
	protected String output;
	protected String messages;
	protected Date date;
	protected Timestamp now;
	protected Hashtable commands = new Hashtable();
	
	protected Command com;
	protected Command2 com2;

    
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
		
		Difference d = new Difference();
		Intersection i = new Intersection();
		MinimizeGraph mg = new MinimizeGraph();
		Union u = new Union();
		DifferenceBatch db = new DifferenceBatch();
		IntersectionBatch ib = new IntersectionBatch();
		MinimizeGraphBatch mgb = new MinimizeGraphBatch();
		UnionBatch ub = new UnionBatch();
		OpenBatch1 ob1 = new OpenBatch1();
		OpenBatch2 ob2 = new OpenBatch2();
		SaveRBatch srb = new SaveRBatch();
		
		commands.put("Difference", d);
		commands.put("Intersection",i);
		commands.put("Union",u);
		commands.put("MinimizeGraph",mg);
		commands.put("DifferenceBatch",db);
		commands.put("IntersectionBatch",ib);
		commands.put("UnionBatch",ub);
		commands.put("MinimizeGraphBatch",mgb);
		commands.put("OpenBatch1",ob1);
		commands.put("OpenBatch2",ob2);
		commands.put("SaveRBatch",ob2);
		
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
			com2 = (Command2) commands.get("openBatch1");
			
			com2.execute(path, lastUnionN1);

			//openBatch1(path, lastUnionN1);
		}else {
			System.out.println("load from memory ..." + lastUnionN1 + " pos: "+ pos);
			loadFromMemory1(pos);
		}
		com2 = (Command2) commands.get("openBatch2");
		
		com2.execute(path, intranetworkAlignmentsFileNames.get(0));

		//(path, intranetworkAlignmentsFileNames.get(0));
		System.out.println("Running dif with intranet alignments in batch: "+ lastUnionN1 + " - " + intranetworkAlignmentsFileNames.get(0));

		runOntBatch("Difference");
		
		System.out.println("Saving net1 after dif w/ intra alignments in batch: "+ lastUnionN1+ "D"+intranetworkAlignmentsFileNames.get(0));
		lastUnionN1 += "D"+intranetworkAlignmentsFileNames.get(0);

		com2 = (Command2) commands.get("SaveRBatch");
		
		com2.execute(path, lastUnionN1);

		//saveRBatch(path, lastUnionN1); // save each union
		partialUnionResultsN1.put(lastUnionN1+"D"+intranetworkAlignmentsFileNames.get(0), lastUnionN1+"D"+intranetworkAlignmentsFileNames.get(0) ); // save Union after apply intra alignments!
		
	}
	
	private void runNet1IntraAlignmentsV2() { // changed the order: intraalignments - lastUnionN1
		// TODO Auto-generated method stub
		// computes differences from network1 and intra alignments
		int pos = findLoaded(lastUnionN1);
		if (pos == -1){
			System.out.println("Opening union1 in batch"+ lastUnionN1);
			
			com2 = (Command2) commands.get("openBatch2");
			
			com2.execute(path, lastUnionN1);

			//openBatch2(path, lastUnionN1); // open 2 first!!!!
		}else {
			System.out.println("load from memory ..." + lastUnionN1 + " pos: "+ pos);
			loadFromMemory1(pos);
		}
		com2 = (Command2) commands.get("openBatch1");
		
		com2.execute(path, intranetworkAlignmentsFileNames.get(0));
		//openBatch1(path, intranetworkAlignmentsFileNames.get(0)); // open 1 after!!!!
		System.out.println("Running dif with intranet alignments in batch: "+ lastUnionN1 + " - " + intranetworkAlignmentsFileNames.get(0));

		runOntBatch("Difference");
		
		System.out.println("Saving net1 after dif w/ intra alignments in batch: "+ lastUnionN1+ "D"+intranetworkAlignmentsFileNames.get(0));
		lastUnionN1 += "D"+intranetworkAlignmentsFileNames.get(0);
		
		com2 = (Command2) commands.get("SaveRBatch");
		
		com2.execute(path, lastUnionN1);
		//saveRBatch(path, lastUnionN1); // save each union
		partialUnionResultsN1.put(lastUnionN1+"D"+intranetworkAlignmentsFileNames.get(0), lastUnionN1+"D"+intranetworkAlignmentsFileNames.get(0) ); // save Union after apply intra alignments!
		
	}
	private void runNet2IntraAlignments() {

		// computes differences from network2 and intra alignments
		int pos = findLoaded(lastUnionN2);
		if (pos == -1){
			System.out.println("Opening union2 in batch"+ lastUnionN2);
			
			com2 = (Command2) commands.get("openBatch1");
			
			com2.execute(path, lastUnionN2);

			//openBatch1(path, lastUnionN2);
		}else {
			System.out.println("load from memory ..." + lastUnionN2 + " pos: "+ pos);
			loadFromMemory1(pos);
		}
		com2 = (Command2) commands.get("openBatch2");
		
		com2.execute(path, intranetworkAlignmentsFileNames.get(1));
		//openBatch2(path, intranetworkAlignmentsFileNames.get(1));
		System.out.println("Running dif with intranet alignments in batch: "+ lastUnionN2 + " - "+ intranetworkAlignmentsFileNames.get(1));

		runOntBatch("Difference");
		
		System.out.println("Saving net2 after dif w/ intra alignments in batch: "+ lastUnionN2+ "D"+intranetworkAlignmentsFileNames.get(1));
		lastUnionN2 += "D"+intranetworkAlignmentsFileNames.get(1);
		
		com2 = (Command2) commands.get("SaveRBatch");
		
		com2.execute(path, lastUnionN2);
		//saveRBatch(path, lastUnionN2); // save each union
		partialUnionResultsN2.put(lastUnionN2+"D"+intranetworkAlignmentsFileNames.get(1), lastUnionN2+"D"+intranetworkAlignmentsFileNames.get(1) ); // save Union after apply intra alignments!
	}
	private void runNet2IntraAlignmentsV2() { // changed the order: intraalignments - lastUnionN2

		// computes differences from network2 and intra alignments
		int pos = findLoaded(lastUnionN2);
		if (pos == -1){
			System.out.println("Opening union2 in batch"+ lastUnionN2);
			
			com2 = (Command2) commands.get("openBatch2");
			
			com2.execute(path, lastUnionN2);
			//openBatch2(path, lastUnionN2); // open 2 frist!!!
		}else {
			System.out.println("load from memory ..." + lastUnionN2 + " pos: "+ pos);
			loadFromMemory1(pos);
		}
		com2 = (Command2) commands.get("openBatch1");
		
		com2.execute(path, intranetworkAlignmentsFileNames.get(1));
		//openBatch1(path, intranetworkAlignmentsFileNames.get(1)); // open 1 after!!!
		System.out.println("Running dif with intranet alignments in batch: "+ lastUnionN2 + " - "+ intranetworkAlignmentsFileNames.get(1));

		runOntBatch("Difference");
		
		System.out.println("Saving net2 after dif w/ intra alignments in batch: "+ lastUnionN2+ "D"+intranetworkAlignmentsFileNames.get(1));
		lastUnionN2 += "D"+intranetworkAlignmentsFileNames.get(1);
		
		com2 = (Command2) commands.get("SaveRBatch");
		
		com2.execute(path, lastUnionN2);
		//saveRBatch(path, lastUnionN2); // save each union
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
		com2 = (Command2) commands.get("openBatch1");
		
		com2.execute(path, network2.get(0));
		//openBatch1(path, network2.get(0));
		for (int i=1; i<network2.size(); i++){
			int pos = findLoaded(network2.get(i));
			if (pos == -1){
				System.out.println("batch opening..." + path + network2.get(i));
			
				com2 = (Command2) commands.get("openBatch2");
				
				com2.execute(path, network2.get(i));
				//openBatch2(path, network2.get(i)); // second element from network1
			}
			else{
				
				System.out.println("load from memory ..." + path + network2.get(i) + " pos: "+ pos);
				loadFromMemory2(pos);
				
			}
			System.out.println("calling union2 in batch");
			runOntBatch("Union");
			
			lastUnionN2 += "U"+network2.get(i);
			System.out.println("Saving union2 in batch: "+ lastUnionN2);

			com2 = (Command2) commands.get("SaveRBatch");
			
			com2.execute(path, lastUnionN2);
			
			//saveRBatch(path, lastUnionN2); // save each union
			partialUnionResultsN2.put(lastUnionN2, lastUnionN2); // save partial result for later!
			
			pos = findLoaded(lastUnionN2);
			if (pos == -1){
				System.out.println("batch opening..." + path + lastUnionN2);
			
				com2 = (Command2) commands.get("openBatch1");
				
				com2.execute(path, lastUnionN2);
				//openBatch1(path, lastUnionN2); // open the result of o1 U o2 as o1 again
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
		com2 = (Command2) commands.get("openBatch1");
		
		com2.execute(path, network1.get(0));
		//openBatch1(path, network1.get(0));
		for (int i=1; i<network1.size(); i++){
			int pos = findLoaded(network1.get(i));
			if (pos == -1){
				System.out.println("batch opening..." + path + network1.get(i));
			
				com2 = (Command2) commands.get("openBatch2");
				
				com2.execute(path, network1.get(i));
				//openBatch2(path, network1.get(i)); // second element from network1
			}
			else{
				
				System.out.println("load from memory ..." + path + network1.get(i) + " pos: "+ pos);
				loadFromMemory2(pos);
				
			}
			System.out.println("calling union1 in batch");
			runOntBatch("Union");
			
			lastUnionN1 += "U"+network1.get(i);
			System.out.println("Saving union1 in batch: "+ lastUnionN1);
			
			com2 = (Command2) commands.get("SaveRBatch");
			
			com2.execute(path, lastUnionN1);
			
			//saveRBatch(path, lastUnionN1); // save each union
			partialUnionResultsN1.put(lastUnionN1, lastUnionN1); // save partial result for later!
			
			pos = findLoaded(lastUnionN1);
			if (pos == -1){
				System.out.println("batch opening..." + path + lastUnionN1);
			
				com2 = (Command2) commands.get("openBatch1");
				
				com2.execute(path, lastUnionN1);
				//openBatch1(path, lastUnionN1); // open the result of o1 U o2 as o1 again
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
				
				com2 = (Command2) commands.get("openBatch1");
				
				com2.execute(path, network1.get(i));
				//openBatch1(path, network1.get(i));
			}
			else {
				System.out.println("load from memory ..." + path + network1.get(i) + " pos: "+ pos);
				loadFromMemory1(pos);

			}
			for (int j=0; j<network2.size(); j++){
				pos = findLoaded(network2.get(j));
				if (pos == -1){
					System.out.println("batch opening..." + path + network2.get(j));
					
					com2 = (Command2) commands.get("openBatch2");
					
					com2.execute(path, network2.get(j));
					//openBatch2(path, network2.get(j));
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
			
			com2 = (Command2) commands.get("openBatch1");
			
			com2.execute(path, lastUnionN1);
			//openBatch1(path, lastUnionN1);
		}else {
			System.out.println("load from memory ..." + lastUnionN1 + " pos: "+ pos);
			loadFromMemory1(pos);

		}
		for (int i=0; i<partialIntersectionResultsN1N2.size(); i++){
			pos = findLoaded(partialIntersectionResultsN1N2.get(i));

			if (pos == -1){
				System.out.println("Opening intersection in batch: "+ i + partialIntersectionResultsN1N2.get(i));

				com2 = (Command2) commands.get("openBatch2");
				
				com2.execute(path, partialIntersectionResultsN1N2.get(i));
				//openBatch2(path, partialIntersectionResultsN1N2.get(i));
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

				com2 = (Command2) commands.get("openBatch1");
				
				com2.execute(path, lastDifferenceN1);
				//openBatch1(path, lastDifferenceN1); // open the result of o1 D o2 as o1 again
			}
			else {
				System.out.println("load from memory ..." + lastDifferenceN1 + " pos: "+ pos);
				loadFromMemory1(pos);

			}
		}
		System.out.println("last Difference n1: "+lastDifferenceN1);
		System.out.println("saving dif in batch: "+lastDifferenceN1+ " as net1");
		com2 = (Command2) commands.get("SaveRBatch");
		
		com2.execute(path, "net1-"+n1+n2+n3);
		//saveRBatch(path, "net1-"+n1+n2+n3);
	}
	
	public void runNet2Difference(ArrayList<String> network2){
		// difference N2
		
		// computes differences from network2 and intersections
		int pos = findLoaded(lastUnionN2);
		if (pos == -1){
			System.out.println("Opening union2 in batch"+ lastUnionN2);
			com2 = (Command2) commands.get("openBatch1");
			
			com2.execute(path, lastUnionN2);

			//openBatch1(path, lastUnionN2);
		} else {
			System.out.println("load from memory ..." + lastUnionN2 + " pos: "+ pos);
			loadFromMemory1(pos);
		}
		for (int i=0; i<partialIntersectionResultsN1N2.size(); i++){
			pos = findLoaded(partialIntersectionResultsN1N2.get(i));

			if (pos == -1){
				System.out.println("Opening intersection in batch: "+ i + partialIntersectionResultsN1N2.get(i));

				com2 = (Command2) commands.get("openBatch2");
				
				com2.execute(path, partialIntersectionResultsN1N2.get(i));
				//openBatch2(path, partialIntersectionResultsN1N2.get(i));
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

				com2 = (Command2) commands.get("openBatch1");
				
				com2.execute(path, lastDifferenceN2);
				//openBatch1(path, lastDifferenceN2); // open the result of o1 D o2 as o1 again
			} else {
				System.out.println("load from memory ..." + lastDifferenceN2 + " pos: "+ pos);
				loadFromMemory1(pos);

			}
		}
		System.out.println("last Difference n2: "+lastDifferenceN2);
		System.out.println("saving dif in batch: "+lastDifferenceN2+ " as net2");
		com2 = (Command2) commands.get("SaveRBatch");
		
		com2.execute(path, "net1-"+n1+n2+n3);
		//saveRBatch(path, "net2-"+n1+n2+n3);
					
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

	private void runOntBatch(String operation) {
		// TODO Auto-generated method stub
		//root = SwingUtilities.getRoot((JButton) e.getSource());
    	String str;
		//minimizeGraphBatch("Intersection");

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
    					Command com = (Command) commands.get("UnionBatch");
    					
    					com.execute("Union");

    					//runUnionBatch();
    				}else if(str.equals("Intersection"))
    				{
    					Command com = (Command) commands.get("IntersectionBatch");
    					
    					com.execute("Intersection");

    					//runIntersectionBatch();
    				}else if(str.equals("Difference"))
    				{
    					Command com = (Command) commands.get("DifferenceBatch"); 
    					
    					com.execute("Difference");
    					//runDifferenceBatch();
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
	

	

	


}
