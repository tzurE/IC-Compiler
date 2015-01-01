package LIRInstructions;

/** The super class of all LIR instructions.
 */
public abstract class Instruction extends LIRNode{
	public abstract void accept(Visitor c);
}