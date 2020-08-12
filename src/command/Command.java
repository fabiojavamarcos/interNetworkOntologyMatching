package command;

import control.Control;

public abstract class Command extends Control{
	/**
	 * Command implementation
	 * 
	 * @author Fabio Marcos de Abreu Santos
	 *
	 */
	
	public abstract void execute(String operation);

}
