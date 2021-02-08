package Application;

/**
 * Enum for OS type
 *
 * 
 *
 */
public enum OSType {

	Mac(0),
	Linux(1),
    Windows(2);

	public final int OSName;
	
    private OSType(int os)
    {
    	this.OSName = os;
    }
}
