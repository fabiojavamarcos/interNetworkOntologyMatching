package command;

import Ontology.ConstraintGraph;
import Ontology.Graph;
import Ontology.Normalization;

public class OpenBatch2 extends Command2 {

	@Override
	public void execute(String path, String file) {
		// TODO Auto-generated method stub
		String str;
		try{
			//enableButtons(false);
			//pathOnt1 = c.getSelectedFile().toString();
			//nameOnt1 = c.getSelectedFile().getName().toString();
			pathOnt2 = path +file;
			System.out.println("path completo:" +pathOnt2);
			nameOnt2 = file;
			System.out.println("file:" +nameOnt2);
			Log += "\nLoading Batch: " + path + file;
			//System.out.println(Log);
			
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
		    				fillProjectionList(gOnt1, model2);	    				
		    			}
		    			*/
					}catch(Exception ex)
					{
						Log+= "\nError openBatch2 : " + ex.getMessage();
						//System.out.println(Log);
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
			return;
		}

	}

}
