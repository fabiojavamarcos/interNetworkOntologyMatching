package Main;

import control.Control;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * MVC implementation
		 * 
		 * @author Fabio Marcos de Abreu Santos
		 *
		 */
		Control control = new Control();
		if (args == null){
			control.initialize();
		} else {
			control.batch(args);
		}

	}

}
