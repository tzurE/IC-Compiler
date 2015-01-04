package LIRInstructions;

/** A string literal.
 */
public class StringLiteral extends LIRNode{
	public String var;
	public final String value;
	protected int address = -1;
	
	protected static int numberOfStringLiterals = 0;
	
	public StringLiteral(String var, String value) {
		this.var = var;
		this.value = value;
		++numberOfStringLiterals;
	}
	
	/** Returns the total number of literals created so far.
	 */
	public static int getNumberOfStringLiterals() {
		return numberOfStringLiterals;
	}
	
	public void assignAddress(int address) {
		if (this.address != -1) {
			throw new RuntimeException("Attempt to assign an address twice to " + this);
		}
		this.address = address;
	}
	
	public int getAddress() {
		return address;
	}
	
	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getValue() {
		return value;
	}
	
	public String toString() {
		return var + ": \"" + value + "\"";
	}
	
	public String print() {
		System.out.println(this.toString());
		return null;
	}
}