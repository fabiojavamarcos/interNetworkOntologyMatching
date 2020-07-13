package Main;

import control.Control;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//edu.stanford.smi.protege.Application.main(args);
		Control control = new Control();
		if (args == null){
			control.initialize();
		} else {
			control.batch(args);
		}

	}

}
