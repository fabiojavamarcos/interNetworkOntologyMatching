package command;

import control.Control;

public abstract class Command extends Control{
	
	public abstract void execute(String operation);

}
