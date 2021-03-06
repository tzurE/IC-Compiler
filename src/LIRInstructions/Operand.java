package LIRInstructions;

/** The super class of all operand types in a LIR program.
 */
public abstract class Operand extends LIRNode{
	/** Returns a string representation of the operand
	 * in the same way it appears in the program.
	 */
	public abstract String toString();
}