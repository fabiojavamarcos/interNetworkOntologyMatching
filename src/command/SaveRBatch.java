package command;

import Ontology.SaveOntology;

public class SaveRBatch extends Command2 {

	@Override
	public void execute(String path, String file) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		String str = "";
    	
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
    			//Log+= "\nSaving Ontology as: " + pathOntResults + "\n";
    			//System.out.println(Log);
 
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
    			return;
    		}
 
	}

}
