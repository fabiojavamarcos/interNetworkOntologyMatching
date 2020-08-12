package command;

import Ontology.ConstraintGraph;
import Ontology.Graph;
import Ontology.Normalization;

public class OpenBatch1 extends Command2 {

	@Override
	public void execute(String path, String file) {
		// TODO Auto-generated method stub
		String str;
		try{
			//pathOnt1 = c.getSelectedFile().toString();
			//nameOnt1 = c.getSelectedFile().getName().toString();
			pathOnt1 = path +file;
			System.out.println("path completo:" +pathOnt1);
			nameOnt1 = file;
			System.out.println("file:" +file);
			Log += "\nLoading Batch: " + path + file;
			//System.out.println(Log);
			
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
		    			
		    			CleanUp = false;
		    			//str = Log;
		    			//str = str + "\n" + "Ontology successfully loaded as Ontology 1 batch";
		    			//Log+=str;
		    			HideIRI1 = false;
		    			//loading possible Projection on jTable2
		    			/*str = (String)(jComboBoxOperation.getSelectedItem());
		    			if(str.equals("Projection"))
		    			{
		    				fillProjectionList(gOnt1, model2);	    				
		    			}
		    			*/
					}catch(Exception ex)
					{
						Log+= "\nError openBatch1: " + ex.getMessage();
		    			//System.out.println(Log);
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

}
